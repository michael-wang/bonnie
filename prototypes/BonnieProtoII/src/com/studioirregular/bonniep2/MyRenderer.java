package com.studioirregular.bonniep2;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;

public class MyRenderer implements Renderer {

	private static final String TAG = "bonnie-proto-II-renderer";
	
	private Context context;
	private Scene scene;
	private GameHandler gameHandler;
	private FrameRateCalculator fpsCalculator;
	private float fps = 0;
	private boolean isStop = false;
	
	public MyRenderer(Context context, GameHandler gameHandler) {
		this.context = context;
		this.gameHandler = gameHandler;
		fpsCalculator = new FrameRateCalculator();
	}
	
	public void setScene(Scene scene) {
		this.scene = scene;
	}
	
	public void stop() {
		isStop = true;
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.w(TAG, "onSurfaceCreated");
		gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		fpsCalculator.start();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.w(TAG, "onSurfaceChanged w:" + width + ", h:" + height);
		gl.glViewport(40, 0, 720, 480);
		
		CoordinateConverter.getInstance().setOffset(40, 0);
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, 0, 720, 480, 0);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		if (isStop) {
			if (scene.isStop() == false) {
				scene.stop(gl);
			} else {
				return;
			}
		}
		
		if (scene != null) {
			if (scene.isLoaded() == false) {
				scene.load(context, gl);
			}
			scene.update();
			scene.draw(gl);
		}
		
		fpsCalculator.addFrame();
		if (fps != fpsCalculator.getFPS()) {
			fps = fpsCalculator.getFPS();
			gameHandler.sendMessage(gameHandler.obtainMessage(GameHandler.MSG_UPDATE_FPS, (int)fps, 0));
		}
	}

}
