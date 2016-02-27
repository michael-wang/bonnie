package com.studioirregular.bonniep2;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class Texture {

	public static final String XML_TAG = "texture";
	public static final String XML_ATTR_NAME = "name";
	public static final String XML_ATTR_WIDTH = "width";
	public static final String XML_ATTR_HEIGHT = "height";
	
	private static final String TAG = "texture";
	
	private int id;
	private String name;
	private float width;
	private float height;
	private List<TexturePart> parts = new ArrayList<TexturePart>();
	static Bitmap bitmap;
	static Canvas canvas;
	
	public Texture(float width, float height, String name) {
		this.name = name;
		this.width= width;
		this.height = height;
		if (bitmap == null) {
			bitmap = Bitmap.createBitmap((int)width, (int)height, Bitmap.Config.ARGB_8888);
			if (TextureSystem.DO_LOG) Log.d(TAG, "bitmap:" + bitmap + ",w:" + bitmap.getWidth() + ",h:" + bitmap.getHeight() + ",config:" + bitmap.getConfig());
			if (bitmap == null) {
				Log.e(TAG, "failed to create bitmap of size:" + width + " x " + height + " in format:" + Bitmap.Config.ARGB_8888);
			}
			canvas = new Canvas(bitmap);
		} else {
			canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC);
		}
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	public boolean loadPart(TexturePart part, Bitmap partBmp) {
		canvas.drawBitmap(partBmp, part.getLeft(), part.getTop(), null);
		parts.add(part);
		if (TextureSystem.DO_LOG) Log.d(TAG, "loadPart part added:" + part);
		
		return true;
	}
	
	public boolean loadPart(TexturePart part, Drawable drawable) {
		part.setWidth(drawable.getIntrinsicWidth());
		part.setHeight(drawable.getIntrinsicHeight());
		if (TextureSystem.DO_LOG) Log.d(TAG, "loadPart part:" + part + ", drawable:" + drawable);
		
		drawable.setBounds((int)part.getLeft(), (int)part.getTop(), (int)part.getRight(), (int)part.getBottom());
		drawable.draw(canvas);
		
		parts.add(part);
		if (TextureSystem.DO_LOG) Log.d(TAG, "loadPart part added:" + part);
		
		return true;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public TexturePart getPart(String name) {
		for (TexturePart p : parts) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "Texture id:" + id + ",width:" + width + ",height:" + height + ",#parts:" + parts.size();
	}
	
}
