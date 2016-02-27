package com.studioirregular.test.texturelost;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.util.Log;

public class TestTextureLostActivity extends Activity {
    
	private static final String TAG = "test-texture-lost";
	
	private FloatBuffer vertex;
	private int textureName;
	private FloatBuffer coord;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        GLSurfaceView view = (GLSurfaceView)findViewById(R.id.surfaceView);
        view.setRenderer(new MyRenderer());
        
        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 3 * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertex = vbb.asFloatBuffer();
        float[] vertices = {
        	0, 480, 0,
        	720, 480, 0,
        	0, 0, 0,
        	720, 0, 0
        };
        vertex.put(vertices);
        vertex.position(0);
    }
    
    class MyRenderer implements Renderer {

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			Log.w(TAG, "onSurfaceCreated config:" + config);
			
			gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			
			loadTexture(gl);
			gl.glEnable(GL10.GL_TEXTURE_2D);
			
			if (coord == null) {
		        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 3 * 4);
		        vbb.order(ByteOrder.nativeOrder());
		        vertex = vbb.asFloatBuffer();
		        float[] vertices = {
		        	0, 480, 0,
		        	720, 480, 0,
		        	0, 0, 0,
		        	720, 0, 0
		        };
		        vertex.put(vertices);
		        vertex.position(0);
			}
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			Log.w(TAG, "onSurfaceChanged width:" + width + ",height:" + height);
			
			gl.glViewport(0, 0, width, height);
			
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			GLU.gluOrtho2D(gl, 0, 720, 480, 0);
			
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
			
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertex);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureName);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, coord);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
			
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}
    	
    }
    
    private void loadTexture(GL10 gl) {
    	Log.w(TAG, "loadTexture");
    	
    	Bitmap bitmap = Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_8888);
    	Canvas canvas = new Canvas(bitmap);
    	
    	Bitmap texture = BitmapFactory.decodeResource(getResources(), R.drawable.level_menu_bg);
    	canvas.drawBitmap(texture, 0, 0, null);
    	
    	int[] textureIDs = new int[1];
    	gl.glGenTextures(1, textureIDs, 0);
    	textureName = textureIDs[0];
    	
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[0]);
    	
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
    	
    	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
    	
    	if (coord == null) {
    		final float bmpWidth = bitmap.getWidth();
    		final float bmpHeight = bitmap.getHeight();
    		final float texWidth = texture.getWidth();
    		final float texHeight = texture.getHeight();
    		coord = buildTextureCoordinate(0, 0, texWidth / bmpWidth, texHeight / bmpHeight);
    	}
    	
    	texture.recycle();
    	bitmap.recycle();
    }
    
    private FloatBuffer buildTextureCoordinate(float left, float top, float right, float bottom) {
    	Log.d(TAG, "buildTextureCoordinate left:" + left + ",top:" + top + ",right:" + right + ",bottom:" + bottom);
		float[] texCoord = { left, bottom, right, bottom, left, top, right, top };
		ByteBuffer vbb = ByteBuffer.allocateDirect(texCoord.length * 4);
		vbb.order(ByteOrder.nativeOrder());

		FloatBuffer result = vbb.asFloatBuffer();
		result.put(texCoord);
		result.position(0);
		return result;
    }
}