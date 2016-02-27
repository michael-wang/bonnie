package com.studioirregular.bonniep1;

import javax.microedition.khronos.opengles.GL10;

public interface Scene {

	public static final float WIDTH = 720.0f;
	public static final float HEIGHT = 480.0f;
	
	public boolean isLoaded();
	public boolean load(GL10 gl);
	public void update();
	public void draw(GL10 gl);
	public void setSurfaceDimension(float width, float height);
}
