package com.studioirregular.gltexturetest;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;

public class MyRenderer implements Renderer {

	private static final String TAG = "renderer";
	
	private Context context;
	private int textureSize;
	private FloatBuffer vertex;
	private Bitmap bitmap;
	
	private class GLTexture {
		public int id;
		public FloatBuffer coord;
	}
	private List<GLTexture> textures = new ArrayList<GLTexture>();
	private int currTex = 0;
	
	public MyRenderer(Context context) {
		this.context = context;
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		// get some values
		IntBuffer buf = IntBuffer.allocate(1);
		gl.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, buf);
		
		textureSize = buf.get(0);
		Log.w(TAG, "GL_MAX_TEXTURE_SIZE:" + textureSize);
		
		gl.glGetIntegerv(GL10.GL_MAX_TEXTURE_STACK_DEPTH, buf);
		Log.w(TAG, "GL_MAX_TEXTURE_STACK_DEPTH:" + buf.get(0));
		
		gl.glGetIntegerv(GL10.GL_MAX_TEXTURE_UNITS, buf);
		Log.w(TAG, "GL_MAX_TEXTURE_UNITS:" + buf.get(0));
		
		// setup vertex
		ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 3 * 4);	// 4 (x,y,z) points, 3 components, each in float (4 bytes)
		vbb.order(ByteOrder.nativeOrder());
		vertex = vbb.asFloatBuffer();
		
		float[] coord = {
			0.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			1.0f, 1.0f, 0.0f
		};
		
		vertex.put(coord);
		vertex.position(0);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, 0.0f, 1.0f, 0.0f, 1.0f);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		while (loadOneTexture(gl)) {
			Log.d(TAG, "texture count:" + textures.size());
		}
		Log.d(TAG, "total texture count:" + textures.size());
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertex);
		
		GLTexture tex = textures.get(currTex);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, tex.id);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, tex.coord);
		
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		
		currTex = (currTex + 1) % textures.size();
	}
	
	private boolean loadOneTexture(GL10 gl) {
		Bitmap src;
		try {
			if (bitmap == null) {
				bitmap = Bitmap.createBitmap(textureSize, textureSize, Bitmap.Config.ARGB_8888);
			}
			src = BitmapFactory.decodeResource(context.getResources(), R.drawable.big_tex);
		} catch (OutOfMemoryError e) {
			Log.w(TAG, "loadOneTexture:" + e);
			return false;
		}
		
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.BLACK);
		canvas.drawBitmap(src, null, new Rect(0, 0, textureSize, textureSize), null);
		src.recycle();
		
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setTextSize(textureSize/10);
		paint.setStrokeWidth(6.0f);
		canvas.drawText("TEXTURE-" + (textures.size() + 1), textureSize/4, textureSize/2, paint);
		
		int[] texId = new int[1];
		gl.glGenTextures(1, texId, 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texId[0]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
//	    Log.d(TAG, "after glTexParameterf...glGetError:" + gl.glGetError());
	    try {
	    	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, bitmap, 0);
//	    	IntBuffer pixels = IntBuffer.allocate(textureSize * textureSize);
//	    	bmp.copyPixelsToBuffer(pixels);
//	    	gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, textureSize, textureSize, 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, pixels);
	    } catch (Exception e) {
	    	Log.w(TAG, "loadOneTexture texImage2D:" + e);
	    	return false;
	    }
		Log.d(TAG, "after glTexImage2D, glGetError:" + gl.glGetError());
		
		GLTexture tex = new GLTexture();
		tex.id = texId[0];
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(2 * 4 * 4);
		vbb.order(ByteOrder.nativeOrder());
		tex.coord = vbb.asFloatBuffer();
		
		float[] coord = {
			0.0f, 1.0f, 
			1.0f, 1.0f, 
			0.0f, 0.0f, 
			1.0f, 0.0f
		};
		
		tex.coord.put(coord);
		tex.coord.position(0);
		
		textures.add(tex);
		
		return true;
	}

}
