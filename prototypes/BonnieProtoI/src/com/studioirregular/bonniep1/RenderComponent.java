package com.studioirregular.bonniep1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class RenderComponent {

	static class Texture {
		public int id;
		public FloatBuffer coordinates;
		public Texture(int id, float glTextureWidth, float glTextureHeight, float left, float top, float right, float bottom) {
			this.id = id;
			float[] texCoord = {
				left / glTextureWidth, bottom / glTextureHeight,
				right / glTextureWidth, bottom / glTextureHeight,
				left / glTextureWidth, top / glTextureHeight,
				right / glTextureWidth, top / glTextureHeight
			};
			ByteBuffer vbb = ByteBuffer.allocateDirect(texCoord.length * 4);
			vbb.order(ByteOrder.nativeOrder());
			coordinates = vbb.asFloatBuffer();
			coordinates.put(texCoord);
			coordinates.position(0);
		}
	}
	
	protected Texture baseTexture;
	
	public RenderComponent (Texture texture) {
		this.baseTexture = texture;
	}
	
	public int getTextureId() {
		return baseTexture.id;
	}
	
	public FloatBuffer getTextureCoordinates() {
		return baseTexture.coordinates;
	}
}
