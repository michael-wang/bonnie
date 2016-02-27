package com.studioirregular.test.textureloading;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.util.Log;

public class GlTextureLoadingActivity extends Activity {
    
	private static final String TAG = "test-texture-loading";
	
	private static final float WORLD_WIDTH = 720;
	private static final float WORLD_HEIGHT = 480;
	
	private GLSurfaceView view;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        view = new GLSurfaceView(this);
        view.setRenderer(new MyRenderer(this));
        
        setContentView(view);
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		view.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		view.onPause();
	}

	private class MyRenderer implements Renderer {

		Context context;
		FloatBuffer vertex;
		FloatBuffer texCoord;
		
		public MyRenderer(Context context) {
			this.context = context;
			
			ByteBuffer vbb = ByteBuffer.allocateDirect(3 * 4 * 4);
			vbb.order(ByteOrder.nativeOrder());
			vertex = vbb.asFloatBuffer();
			
			float[] bg = {
				0,			WORLD_HEIGHT, 0,
				WORLD_WIDTH,WORLD_HEIGHT, 0,
				0,			0			, 0,
				WORLD_WIDTH,0			, 0
			};
			vertex.put(bg);
			vertex.position(0);
		}
		
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
			
			gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			gl.glShadeModel(GL10.GL_FLAT);
			gl.glDisable(GL10.GL_DEPTH_TEST);
			gl.glEnable(GL10.GL_TEXTURE_2D);
			
			gl.glDisable(GL10.GL_DITHER);
			gl.glDisable(GL10.GL_LIGHTING);
			
	        String extensions = gl.glGetString(GL10.GL_EXTENSIONS); 
	        String version = gl.glGetString(GL10.GL_VERSION);
	        String renderer = gl.glGetString(GL10.GL_RENDERER);
	        
	        Log.d(TAG, "version:" + version);
	        Log.d(TAG, "renderer:" + renderer);
	        Log.d(TAG, "extensions:" + extensions);
	        
	        loadTexture(gl, context);
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
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
			
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertex);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoord);
			
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
			
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		}
		
		private void setupViewport(GL10 gl, int viewWidth, int viewHeight) {
			// game to view scale
			float scaleX = (float)viewWidth / WORLD_WIDTH;
			float scaleY = (float)viewHeight / WORLD_HEIGHT;
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
		}
		
		private void loadTexture(GL10 gl, Context context) {
			int[] textureName = new int[1];
			gl.glGenTextures(1, textureName, 0);
			
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureName[0]);
			
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
			
			Bitmap bitmap = buildTexture(context, R.drawable.level_menu_bg);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
			checkGLError(gl);
			
			bitmap.recycle();
		}
		
		private Bitmap buildTexture(Context context, int resId) {
			Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), resId);
			
			int texWidth = findPowerOfTwoSize(bmp.getWidth());
			int texHeight = findPowerOfTwoSize(bmp.getHeight());
			Log.d(TAG, "buildTexture bmp w:" + bmp.getWidth() + ",h:" + bmp.getHeight() + ",config:" + bmp.getConfig() + ", texture w:" + texWidth + ",h:" + texHeight);
			
			Bitmap texture = Bitmap.createBitmap(texWidth, texHeight, bmp.getConfig());
			Canvas canvas = new Canvas(texture);
			
			canvas.drawBitmap(bmp, 0, 0, null);
			
			float u = (float)bmp.getWidth() / (float)texWidth;
			float v = (float)bmp.getHeight() / (float)texHeight;
			float[] coord = {
				0, v,
				u, v,
				0, 0,
				u, 0
			};
			ByteBuffer tbb = ByteBuffer.allocateDirect(2 * 4 * 4);
			tbb.order(ByteOrder.nativeOrder());
			texCoord = tbb.asFloatBuffer();
			texCoord.put(coord);
			texCoord.position(0);
			
			bmp.recycle();
			return texture;
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
    	
    }
}