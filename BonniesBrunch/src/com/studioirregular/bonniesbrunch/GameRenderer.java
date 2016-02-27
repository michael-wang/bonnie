package com.studioirregular.bonniesbrunch;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;

import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.RenderSystem.RenderObject;
import com.studioirregular.bonniesbrunch.base.ObjectArray;

public class GameRenderer implements Renderer {
	
	private static final String TAG = "game-renderer";
	
	public GameRenderer(Context context, Game game) {
		if (Config.DEBUG_LOG) Log.d(TAG, "GameRenderer allocated!");
		
		this.context = context;
		this.game = game;
		
		if (Config.DEBUG_SHOW_FPS) { 
			fpsCalculator = new FrameRateCalculator();
		}
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		if (Config.DEBUG_LOG) Log.w(TAG, "onSurfaceCreated");
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
//		if (TextureSystem.getInstance().isTextureInitialized()) {
//			TextureSystem.getInstance().loadAllTextures(gl, context);
//		} else {
//			TextureSystem.getInstance().initializeTextures(context, R.xml.textures);
//		}
		TextureSystem.getInstance().onSurfaceCreate(gl, context);
		
		if (Config.DEBUG_SHOW_FPS) {
			fpsCalculator.start();
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if (Config.DEBUG_LOG) Log.w(TAG, "onSurfaceChanged width:" + width + ",height:" + height);
		
		setupViewport(gl, width, height);
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, 0, ContextParameters.getInstance().gameWidth, ContextParameters.getInstance().gameHeight, 0);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		if (game != null) {
			game.onSurfaceReady();
		}
	}
	
	public void doEffectFadeOut() {
		doEffectFadeOut = true;
		restoreFadeOut = false;
		effectStartTime = System.currentTimeMillis();
	}
	
	public void restoreEffectFadeOut() {
		requestRestoreFadeOut = true;
	}
	
	private void onFadeOutDone() {
		GameEventSystem.scheduleEvent(GameEvent.RENDERER_FADE_OUT_DONE);
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		
		synchronized(drawLock) {
			if (!drawQueueUpdated) {
				while (!drawQueueUpdated) {
					try {
						drawLock.wait();
					} catch (InterruptedException e) {
						
					}
				}
			}
			drawQueueUpdated = false;
		}
		
		synchronized(this) {
//			if (Config.DEBUG_LOG) Log.d(TAG, "draw frame");
			long start = System.currentTimeMillis();
			
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
			
			TextureSystem.getInstance().update(gl, context);
			
			gl.glEnable(GL10.GL_BLEND);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			
			if (doEffectFadeOut) {
				long effectElapsedTime = System.currentTimeMillis() - effectStartTime;
				if (FADE_OUT_DURATION < effectElapsedTime) {
					gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
					doEffectFadeOut = false;
					onFadeOutDone();
				} else {
					float color = 1.0f - (1.0f * effectElapsedTime / FADE_OUT_DURATION);
					gl.glColor4f(color, color, color, 1.0f);
				}
			} else if (restoreFadeOut) {
				restoreFadeOut = false;
				gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			}
			
			for (int i = 0; i < drawQueue.size(); i++) {
				RenderObject object = (RenderObject)drawQueue.get(i);
				object.draw(gl);
			}
			
			gl.glDisable(GL10.GL_BLEND);
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			
			if (requestRestoreFadeOut) {
				requestRestoreFadeOut = false;
				restoreFadeOut = true;
			}
			
			if (Config.DEBUG_LOG_FRAME_TIME) Log.w(TAG, "onDrawFrame frame time:" + (System.currentTimeMillis() - start));
		}
		
		if (Config.DEBUG_SHOW_FPS) {
			fpsCalculator.addFrame();
			if (fps != fpsCalculator.getFPS()) {
				fps = fpsCalculator.getFPS();
				game.upadteFPS(fps);
			}
		}
	}
	
	public synchronized void waitDrawFrame() {
	}
	
	public synchronized void setDrawQueue(ObjectArray<RenderObject> queue) {
//		if (Config.DEBUG_LOG) Log.d(TAG, "setDrawQueue #queue:" + queue.getCount());
		drawQueue = queue;
		synchronized(drawLock) {
			drawQueueUpdated = true;
			drawLock.notify();
		}
	}
	
	public void setupViewport(GL10 gl, int viewWidth, int viewHeight) {
		ContextParameters params = ContextParameters.getInstance();
		
		// game to view scale
		float scaleX = (float)viewWidth / params.gameWidth;
		float scaleY = (float)viewHeight / params.gameHeight;
		float scale = scaleX > scaleY ? scaleY : scaleX;
		
		float viewportWidth = params.gameWidth * scale;
		float viewportHeight = params.gameHeight * scale;
		
		float viewportOffsetY = (viewHeight - viewportHeight) / 2;
		float viewportOffsetX = (viewWidth - viewportWidth) / 2;
		
		if (gl != null) {
			gl.glViewport((int)viewportOffsetX, (int)viewportOffsetY, (int)viewportWidth, (int)viewportHeight);
		}
		
		params.viewportWidth = viewportWidth;
		params.viewportHeight = viewportHeight;
		params.viewportOffsetX = viewportOffsetX;
		params.viewportOffsetY = viewportOffsetY;
		params.scaleViewToGameX = 1/scale;
		params.scaleViewToGameY = 1/scale;
	}
	
	private Context context;
	private Game game;
	
	private ObjectArray<RenderObject> drawQueue = null;
	private boolean drawQueueUpdated = false;
	private Object drawLock = new Object();
	
	private static final long FADE_OUT_DURATION = 500;	// ms
	private boolean doEffectFadeOut;
	private long effectStartTime;
	private boolean requestRestoreFadeOut;
	private boolean restoreFadeOut;
	
	private FrameRateCalculator fpsCalculator;
	private float fps = 0;
}
