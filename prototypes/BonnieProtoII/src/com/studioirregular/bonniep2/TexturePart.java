package com.studioirregular.bonniep2;

public class TexturePart {

	public static final String XML_TAG = "part";
	public static final String XML_TAG_NAME = "name";
	public static final String XML_TAG_X = "x";
	public static final String XML_TAG_Y = "y";
	
	private String name;
	private float left;
	private float top;
	private float right;
	private float bottom;
	private int refCount = 0;
	
	public TexturePart(String name, float left, float top) {
		this.name = name;
		this.left = left;
		this.top = top;
	}
	
	@Override
	public String toString() {
		return "TexturePart name:" + name + ",left:" + left + ",top:" + top + ",right:" + right + ",bottom:" + bottom;
	}
	
	public String getName() {
		return name;
	}
	
	public float getLeft() {
		return left;
	}
	
	public float getTop() {
		return top;
	}
	
	public void setWidth(float width) {
		this.right = left + width;
	}
	
	public void setHeight(float height) {
		this.bottom = top + height;
	}
	
	public float getWidth() {
		return right - left;
	}
	
	public float getHeight() {
		return bottom - top;
	}
	
	public float getRight() {
		return right;
	}
	
	public float getBottom() {
		return bottom;
	}
	
	public void addCount() {
		refCount++;
	}
	
	public void decCount() {
		refCount--;
	}
	
	public int getRefCount() {
		return refCount;
	}
}
