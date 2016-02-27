package com.studioirregular.bonniep2;

public class CoordinateConverter {

	public static CoordinateConverter getInstance() {
		if (sInstance == null) {
			sInstance = new CoordinateConverter();
		}
		return sInstance;
	}
	
	public void setOffset(float xOffset, float yOffset) {
		this.offsetX = xOffset;
		this.offsetY = yOffset;
	}
	
	public float convertX(float x) {
		return x - offsetX;
	}
	
	public float convertY(float y) {
		return y - offsetY;
	}
	
	private static CoordinateConverter sInstance = null;
	
	private float offsetX, offsetY;
	private CoordinateConverter() {
		offsetX = offsetY = 0.0f;
	}
}
