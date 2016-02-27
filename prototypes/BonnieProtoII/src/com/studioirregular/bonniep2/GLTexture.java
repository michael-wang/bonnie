package com.studioirregular.bonniep2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.util.Log;

public class GLTexture {

	private static final String TAG = "gltexture";
	private static final boolean DO_LOG = false;
	
	private String name;	// resource name, as identifier
	private int glTextureId;
	private FloatBuffer coordinates;
	
	public GLTexture(String name, int textureId, float width, float height, 
			float left, float top, float right, float bottom) {
		if (DO_LOG) {
			Log.d(TAG, "GLTexture name:" + name + ",tex_id:" + textureId
					+ ",width:" + width + ",height:" + height + "left:" + left
					+ ",top:" + top + ",right:" + right + "bottom:" + bottom);
		}
		this.name = name;
		this.glTextureId = textureId;
		coordinates = buildCoordinate(left / width, top / height, right / width, bottom / height);
	}
	
	public String getName() {
		return name;
	}
	
	public int getGLTextureId() {
		return glTextureId;
	}
	
	public FloatBuffer getCoordinates() {
		return coordinates;
	}
	
	// left/right/right/bottom: coordinates within big texture which has coordinates:
	// (0, 0)	(1, 0)
	// (1, 0)	(1, 1)
	private FloatBuffer buildCoordinate(float left, float top, float right, float bottom) {
		if (DO_LOG) {
			Log.d(TAG, "buildCoordinate left:" + left + ",top:" + top
					+ ",right:" + right + ",bottom:" + bottom);
		}
		float[] texCoord = {
			left, bottom,
			right, bottom,
			left, top,
			right, top
		};
		ByteBuffer vbb = ByteBuffer.allocateDirect(texCoord.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		
		FloatBuffer result = vbb.asFloatBuffer();
		result.put(texCoord);
		result.position(0);
		return result;
	}

}
