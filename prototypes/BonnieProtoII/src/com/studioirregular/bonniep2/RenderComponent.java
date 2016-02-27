package com.studioirregular.bonniep2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class RenderComponent implements Component, GLRenderable {

	protected String id;
	protected FloatBuffer vertex;
	protected GLTexture tex;
	
	protected float x, y;
	protected float width, height;
	protected float scaleX = 1.0f, scaleY = 1.0f;
	
	protected boolean visible = true;
	
	/*
	 * width: object width in world coordinate
	 * height: object height in world coordinate
	 * texWidth: width of openGL texture
	 * texHeight: height of openGL texture
	 * texLeft/texTop/texRight/texBottom: texture location in above openGL texture.
	*/ 
	public RenderComponent(String id, float left, float top, float width, float height, GLTexture tex) {
		this.id = id;
		this.x = left + width/2;
		this.y = top + height/2;
		this.width = width;
		this.height = height;
		this.vertex = buildVertex(width, height);
		this.tex = tex;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public boolean getVisible() {
		return visible;
	}
	
	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	@Override
	public float getTranslateX() {
		return x;
	}
	
	@Override
	public void setX(float left) {
		this.x = left + width/2;
	}
	
	@Override
	public float getTranslateY() {
		return y;
	}
	
	@Override
	public void setY(float top) {
		this.y = top + height/2;
	}
	
	@Override
	public float getWidth() {
		return width;
	}
	
	@Override
	public float getHeight() {
		return height;
	}
	
	@Override
	public float getX() {
		return x - width/2;
	}
	
	@Override
	public float getY() {
		return y - height/2;
	}
	
	@Override
	public FloatBuffer getVertex() {
		return vertex;
	}
	
	@Override
	public GLTexture getGLTexture() {
		return tex;
	}
	
	@Override
	public void updateTexture(GLTexture texture) {
		tex = texture;
	}
	
	// vertex for rectangle, centered to origin so we can do scaling/rotation easily:
	// (-w/2, -h/2, 0)	(w/2, -h/2, 0)
	// (-w/2,  h/2, 0)	(w/2,  h/2, 0)
	private FloatBuffer buildVertex(float width, float height) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 3 * 4);	// 4 (x,y,z) points, 3 components, each in float (4 bytes)
		vbb.order(ByteOrder.nativeOrder());
		FloatBuffer result = vbb.asFloatBuffer();
		
		float[] coord = {
			-width/2,	height/2, 0.0f,
			width/2,	height/2, 0.0f,
			-width/2,	-height/2, 0.0f,
			width/2,	-height/2, 0.0f
		};
		
		result.put(coord);
		result.position(0);
		return result;
	}

	@Override
	public float getScaleX() {
		return scaleX;
	}

	@Override
	public float getScaleY() {
		return scaleY;
	}
	
	@Override
	public void setScale(float sx, float sy) {
		scaleX = sx;
		scaleY = sy;
	}
	
}
