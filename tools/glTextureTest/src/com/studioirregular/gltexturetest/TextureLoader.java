package com.studioirregular.gltexturetest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class TextureLoader {

	private static final String TAG = "texture-laoder";
	
	public static class Texture {
		public Bitmap bitmap;
		public int left;
		public int top;
		public int right;
		public int bottom;
		@Override
		public String toString() {
			return Texture.class.getName() + " left:" + left + ",top:" + top + ",right:" + right + ",bottom:" + bottom;
		}
		
	}
	
	// Method 1: one drawable resource in one bitmap
	public static Texture load(Context context, int resId) {
		Texture result = new Texture();
		
		// get bitmap size
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(context.getResources(), resId, opts);
		
		result.left = 0;
		result.top = 0;
		result.right = opts.outWidth;
		result.bottom = opts.outHeight;
		
		int width = fitPowerOfTwo(opts.outWidth);
		int height = fitPowerOfTwo(opts.outHeight);
		Log.d(TAG, "2^n width:" + width + ",height:" + height);
		
		// load bitmap
		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), resId);
		
		// get pixels
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, bmp.getWidth(), bmp.getHeight());
		bmp.recycle();
		
		result.bitmap = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
		Log.d(TAG, "bitmap w:" + result.bitmap.getWidth() + ",h:" + result.bitmap.getHeight() + ",config:" + result.bitmap.getConfig());
		return result;
	}
	
	// find 2^n >= value
	private static int fitPowerOfTwo(int value) {
		return (int)Math.pow(2.0f, Math.ceil(Math.log(value) / Math.log(2.0f)));
	}
	
	// Method 2: multiple resources in one bitmap
	// support horizontally loading sequence for now: T1 T2 T3...
	private Bitmap textureBitmap;
	private int lastRight;
	public void newBitmap(int width, int height) {
		Log.d(TAG, "newBitmap w:" + width + ",h:" + height);
		width = fitPowerOfTwo(width);
		height = fitPowerOfTwo(height);
		textureBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		lastRight = 0;
		Log.d(TAG, "newBitmap adjusted w:" + width + ",h:" + height);
	}
	
	// prevRight: previous texture's right coordinate
	public Texture loadIntoBitmap(Context context, int resId) {
		Texture result = new Texture();
		result.bitmap = textureBitmap;
		
		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), resId);
		int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
		bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
		
		textureBitmap.setPixels(pixels, 0, bmp.getWidth(), lastRight, 0, bmp.getWidth(), bmp.getHeight());
		
		result.left = lastRight;
		result.top = 0;
		result.right = lastRight + bmp.getWidth();
		result.bottom = bmp.getHeight();
		Log.d(TAG, "loadIntoBitmap result:" + result);
		
		lastRight = result.right;
		return result;
	}
	
	public Bitmap getBitmap() {
		return textureBitmap;
	}
	
	public void clear() {
		textureBitmap = null;
	}
	
}
