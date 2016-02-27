package com.studioirregular.bonniep2;

import java.nio.FloatBuffer;

/* definition: got vertex of 4 points for quad:
 * (0, height, 0) (width, height, 0)
 * (0, 0, 0)      (width, 0, 0)
 * 
 * and a GLTexture
 */
public interface GLRenderable {
	public boolean getVisible();
	public void setVisible(boolean visible);
	
	public FloatBuffer getVertex();
	public GLTexture getGLTexture();
	public void updateTexture(GLTexture texture);
	
	public float getTranslateX();
	public float getTranslateY();
	
	public float getWidth();
	public float getHeight();
	
	public float getX();
	public void setX(float left);
	public float getY();
	public void setY(float top);
	
	public float getScaleX();
	public float getScaleY();
	public void setScale(float sx, float sy);
}
