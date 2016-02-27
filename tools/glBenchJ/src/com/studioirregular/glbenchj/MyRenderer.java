package com.studioirregular.glbenchj;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;

public class MyRenderer implements Renderer {

	private static final boolean DO_LOG = true;
	private static final String TAG = "renderer";
	
	private static final boolean DRAW_TEXTURE = true;	// set false to draw with color
	private static final int WORLD_WIDTH = 720;
	private static final int WORLD_HEIGHT = 480;
	private static final int SPRITE_WIDTH = 228;//128;	// element width
	private static final int SPRITE_HEIGHT = 181;//102;	// element height
	
	private static final int BACKGROUND_COUNT = 1;
	
	private static final int SPRITE_VELOCITY = 4;	// in model coord
	
	private GlBenchJActivity activity;
	private Random rand;
	
	private int count;	// rect count, including bg
	private Rect[] rects;
	private float[] translates;
	private float[] colors;	// rgba
	private FrameRateCalculator fpsCalculator;
	private float fps;
	
	// count: number of rectangles
	public MyRenderer(GlBenchJActivity activity, int count) {
		this.activity = activity;
		rand = new Random(System.currentTimeMillis());
		
		count += BACKGROUND_COUNT;	// add 1 for bg
		this.count = count;
		
		rects = new Rect[count];
		translates = new float[count * 2];
		
		if (DRAW_TEXTURE) {
			;
		} else {
			colors = new float[count * 4];
		}
		fpsCalculator = new FrameRateCalculator();
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glShadeModel(GL10.GL_FLAT);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		
		gl.glDisable(GL10.GL_DITHER);
		gl.glDisable(GL10.GL_LIGHTING);
		
        String extensions = gl.glGetString(GL10.GL_EXTENSIONS); 
        String version = gl.glGetString(GL10.GL_VERSION);
        String renderer = gl.glGetString(GL10.GL_RENDERER);
        
        Log.d(TAG, "version:" + version);
        Log.d(TAG, "renderer:" + renderer);
        Log.d(TAG, "extensions:" + extensions);
		
		if (DRAW_TEXTURE) {
			gl.glEnable(GL10.GL_TEXTURE_2D);
			
			initTexture(gl);
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		}
		
		// build background
		for (int i = 0; i < BACKGROUND_COUNT; i++) {
			rects[i] = genRect(i, WORLD_WIDTH, WORLD_HEIGHT, 0, 0);
		}
		
		for (int i = BACKGROUND_COUNT; i < count; i++) {
			rects[i] = genRect(i, SPRITE_WIDTH, SPRITE_HEIGHT, (WORLD_WIDTH - SPRITE_WIDTH)/2, (WORLD_HEIGHT - SPRITE_HEIGHT)/2);
		}
		
		fpsCalculator.start();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if (DO_LOG) Log.d(TAG, "onSurfaceChanged width:" + width + ",height:" + height);
		
		setupViewport(gl, width, height);
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, 0, WORLD_WIDTH, WORLD_HEIGHT, 0);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		updateState();
		
		for (int i = 0; i < count; i++) {
			final Rect rect = rects[i];
			
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, rect.vertex);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, rect.texture.coord);
			
			// preserve model matrix
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glPushMatrix();
			gl.glLoadIdentity();
			
			gl.glTranslatef(translates[i*2], translates[i*2 + 1], 0);
			
			gl.glBindTexture(GL10.GL_TEXTURE_2D, rect.texture.name);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
			
			// restore model matrix
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glPopMatrix();
			
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		}
		
		fpsCalculator.addFrame();
		if (fps != fpsCalculator.getFPS()) {
			fps = fpsCalculator.getFPS();
			activity.updateFPS(fps);
		}
	}
	
	private void setupViewport(GL10 gl, int viewWidth, int viewHeight) {
		// game to view scale
		float scaleX = (float)viewWidth / (float)WORLD_WIDTH;
		float scaleY = (float)viewHeight / (float)WORLD_HEIGHT;
		float scale = scaleX > scaleY ? scaleY : scaleX;
		
		float viewportWidth = WORLD_WIDTH * scale;
		float viewportHeight = WORLD_HEIGHT * scale;
		
		float viewportOffsetY = (viewHeight - viewportHeight) / 2;
		float viewportOffsetX = (viewWidth - viewportWidth) / 2;
		
		Log.d(TAG, "setupViewport viewWidth:" + viewWidth + ", viewHeight:" + viewHeight);
		Log.d(TAG, "viewport x:" + viewportOffsetX + ",y:" + viewportOffsetY + ",w:" + viewportWidth + ",h:" + viewportHeight);
		
		if (gl != null) {
			gl.glViewport((int)viewportOffsetX, (int)viewportOffsetY, (int)viewportWidth, (int)viewportHeight);
		}
		checkGLError(gl);
	}
	
	class Rect {
		float width;
		float height;
		float x, y;
		public Rect(float width, float height, float x, float y) {
			this.width = width;
			this.height = height;
			this.x = x;
			this.y = y;
			
			float left = 0;//-width/2;
			float top = 0;//-height/2;
			float right = width;//width/2;
			float bottom = height;//height/2;
			float[] coord = {
				left, bottom, 0,
				right, bottom, 0,
				left, top, 0,
				right, top, 0
			};
			ByteBuffer vbb = ByteBuffer.allocateDirect(3 * 4 * 4);
			vbb.order(ByteOrder.nativeOrder());
			vertex = vbb.asFloatBuffer();
			vertex.put(coord);
			vertex.position(0);
		}
		FloatBuffer vertex;
		GLTexture texture;
		float dx, dy;
	}
	
	private Rect genRect(int index, int w, int h, int x, int y) {
		if (DO_LOG) Log.d(TAG, "genRect index:" + index + ",w:" + w + ",h:" + h + ",x:" + x + ",y:" + y);
		
		Rect rect = new Rect(w, h, x, y);
		
		// texture or color
		if (DRAW_TEXTURE) {
			if (index < BACKGROUND_COUNT) {	// for bg
				rect.texture = textureBG[index];
			} else {
				rect.texture = textureCars[rand.nextInt(CAR_TEXTURE_COUNT)];
			}
		} else {
			colors[index * 4 + 0] = rand.nextFloat();
			colors[index * 4 + 1] = rand.nextFloat();
			colors[index * 4 + 2] = rand.nextFloat();
			colors[index * 4 + 3] = 1.0f;//0.5f + rand.nextFloat()*0.5f;
//			if (DO_LOG) Log.d(TAG, "color:" + colors[index*4] + "," + colors[index*4+1] + "," + colors[index*4+2] + "," + colors[index*4+3]);
		}
		
		// dir
		float dx = 0;
		float dy = 0;
		if (index != 0) {
			dx = rand.nextFloat();
			dy = 1.0f - dx;
			
			dx = dx * SPRITE_VELOCITY;
			dx = dy * SPRITE_VELOCITY;
			
			dx = rand.nextBoolean() ? dx : -dx;
			dy = rand.nextBoolean() ? dy : -dy;
		}
		rect.dx = dx;
		rect.dy = dy;
//		if (DO_LOG) Log.d(TAG, "dir x:" + velocity[index].x + ",y:" + velocity[index].y);
		return rect;
	}
	
	class GLTexture {
		public int name;
		public FloatBuffer coord;
	}
	
	GLTexture[] textureBG;
	GLTexture[] textureCars;
	static final int CAR_TEXTURE_COUNT = 4;
	
	private void initTexture(GL10 gl) {
		textureBG = new GLTexture[BACKGROUND_COUNT];
		textureCars = new GLTexture[CAR_TEXTURE_COUNT];
		
		Bitmap bitmap = Bitmap.createBitmap(1024, 512, Bitmap.Config.ARGB_8888);
		textureBG[0] = buildBitmap(activity, bitmap, R.drawable.bg, 0, 0);
		int textureName = buildTexture(gl, bitmap);
		textureBG[0].name = textureName;
		
//		bitmap = Bitmap.createBitmap(1024, 512, Bitmap.Config.ARGB_8888);
//		textureBG[1] = buildBitmap(activity, bitmap, R.drawable.bg2, 0, 0);
//		textureName = buildTexture(gl, bitmap);
//		textureBG[1].name = textureName;
		
		bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
		textureCars[0] = buildBitmap(activity, bitmap, R.drawable.car, 0, 0);
		textureName = buildTexture(gl, bitmap);
		textureCars[0].name = textureName;
		
		bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
		textureCars[1] = buildBitmap(activity, bitmap, R.drawable.car2, 0, 0);
		textureName = buildTexture(gl, bitmap);
		textureCars[1].name = textureName;
		
		bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
		textureCars[2] = buildBitmap(activity, bitmap, R.drawable.car3, 0, 0);
		textureName = buildTexture(gl, bitmap);
		textureCars[2].name = textureName;
		
		bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
		textureCars[3] = buildBitmap(activity, bitmap, R.drawable.car4, 0, 0);
		textureName = buildTexture(gl, bitmap);
		textureCars[3].name = textureName;
		
//		bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
//		textureCars[4] = buildBitmap(activity, bitmap, R.drawable.car5, 0, 0);
//		textureName = buildTexture(gl, bitmap);
//		textureCars[4].name = textureName;
	}
	
	private GLTexture buildBitmap(Context context, Bitmap bitmap, int resId, float x, float y) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inScaled = false;
		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), resId, opts);
		
		Canvas canvas = new Canvas(bitmap);
		canvas.drawBitmap(bmp, x, y, null);
		
		GLTexture texture = new GLTexture();
		
		final float texWidth = bitmap.getWidth();
		final float texHeight = bitmap.getHeight();
		final float u0 = x / texWidth;
		final float u1 = (x + (float)bmp.getWidth()) / texWidth;
		final float v0 = y / texHeight;
		final float v1 = (y + (float)bmp.getHeight()) / texHeight;
		float[] texCoord = {
			u0, v1,
			u1, v1,
			u0, v0,
			u1, v0
		};
		ByteBuffer tbb = ByteBuffer.allocateDirect(2 * 4 * 4);
		tbb.order(ByteOrder.nativeOrder());
		texture.coord = tbb.asFloatBuffer();
		texture.coord.put(texCoord);
		texture.coord.position(0);
		
		bmp.recycle();
		return texture;
	}
	
	private int buildTexture(GL10 gl, Bitmap bitmap) {
		int[] textureName = new int[1];
		gl.glGenTextures(1, textureName, 0);
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureName[0]);
		
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
		
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		checkGLError(gl);
		
		bitmap.recycle();
		return textureName[0];
	}
	
	// find 2^n >= size
	private int findPowerOfTwoSize(int size) {
		int n = 6;	// texture size must >= 64
		while (Math.pow(2, n) < size) {
			n++;
		}
		return (int)Math.pow(2, n);
	}
	
	private void checkGLError(GL10 gl) {
		int err = gl.glGetError();
		if (err != GL10.GL_NO_ERROR) {
			Log.e(TAG, "glGetError:" + err);
		}
	}
	
	private void updateState() {
		for (int i = BACKGROUND_COUNT; i < count; i++) {
			float x = rects[i].x + rects[i].dx;
			float y = rects[i].y + rects[i].dy;
			
			// update dir if hit bounds
			if ((x <= 0) || (WORLD_WIDTH <= (x + SPRITE_WIDTH))) {
				rects[i].dx *= -1.0f;
			} else if ((y < 0) || (WORLD_HEIGHT < (y + SPRITE_HEIGHT))) {
				rects[i].dy *= -1.0f;
			}
			
			rects[i].x = x;
			rects[i].y = y;
			translates[i * 2] = x;
			translates[i * 2 + 1] = y;
		}
	}

}
