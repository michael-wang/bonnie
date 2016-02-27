package com.studioirregular.bonniep2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.os.Environment;
import android.util.Log;

public class TextureSystem {

	private static final boolean SAVE_TEXTURE_TO_FILE = false;
	
	// singleton
	public static TextureSystem getInstance() {
		if (sInstance == null) {
			sInstance = new TextureSystem();
		}
		return sInstance;
	}
	private static TextureSystem sInstance = null;
	private TextureSystem() {}
	
	public class XmlFormatException extends Exception {
		/**
		 * ?
		 */
		private static final long serialVersionUID = 1L;

		public XmlFormatException(String s) {
			super(s);
		}
	}
	
	static final boolean DO_LOG = false;
	private static final String TAG = "texture-system";
	
	private Map<Integer, Texture> textures = new HashMap<Integer, Texture>();
	
	public void load(Context context, GL10 gl, int xmlResId)
			throws XmlFormatException, XmlPullParserException, IOException {
		if (DO_LOG) Log.d(TAG, "load xmlResId:" + xmlResId);
		
		long startTime = System.currentTimeMillis();
		
		XmlResourceParser resParser = context.getResources().getXml(xmlResId);
	    TextureParser parser = new TextureParser(context, gl, resParser);
	    parser.parse();
	    
	    long elapsedTime = System.currentTimeMillis() - startTime;
	    if (DO_LOG) Log.w(TAG, "elapsedTime for laod texture:" + elapsedTime);
	}
	
	public void release(GL10 gl) {
		if (DO_LOG) Log.d(TAG, "release");
		
		final int textureCount = textures.size();
		int[] textureNames = new int[textureCount];
		
		Collection<Texture> textureCollection = textures.values();
		Iterator<Texture> itrTexture = textureCollection.iterator();
		int i = 0;
		while (itrTexture.hasNext()) {
			Texture texture = itrTexture.next();
			textureNames[i] = texture.getId();
			i++;
		}
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(4 * textureCount);
		vbb.order(ByteOrder.nativeOrder());
		IntBuffer texNameBuffer = vbb.asIntBuffer();
		texNameBuffer.put(textureNames);
		texNameBuffer.position(0);
		gl.glDeleteTextures(textureCount, texNameBuffer);
		
		int error = gl.glGetError();
		if (error != GL10.GL_NO_ERROR) {
			Log.e(TAG, "release error after glDeleteTextures:" + error);
		}
		
		textures.clear();
	}
	
	public GLTexture getPart(String name) {
		Texture texture = null;
		TexturePart part = null;
		for (Texture t : textures.values()) {
			part = t.getPart(name);
			if (part != null) {
				texture = t;
				break;
			}
		}
		
		if (texture == null || part == null) {
			Log.e(TAG, "getPart cannot find texture for:" + name);
			return null;
		}
		
		GLTexture glTex = new GLTexture(part.getName(), texture.getId(), texture.getWidth(), texture.getHeight(), 
				part.getLeft(), part.getTop(), part.getRight(), part.getBottom());
		
		return glTex;
	}
	
	public TexturePart getTextureInfo(String name) {
		TexturePart part = null;
		for (Texture t : textures.values()) {
			part = t.getPart(name);
			if (part != null) {
				return part;
			}
		}
		return null;
	}
	
	private class TextureParser {
		private Context context;
		private GL10 gl;
		private XmlPullParser parser;
		private Resources externalResources;
		
		public TextureParser(Context context, GL10 gl, XmlPullParser parser) {
			this.context = context;
			this.gl = gl;
			this.parser = parser;
			try {
				externalResources = context.getPackageManager().getResourcesForApplication(TextureResource.EXTERNAL_RESOURCES_PACKAGE);
			} catch (NameNotFoundException e) {
				Log.e(TAG, "TextureParser cannot find external resource package:" + TextureResource.EXTERNAL_RESOURCES_PACKAGE);
				Log.e(TAG, "exception:" + e);
			}
		}
		
		public void parse() throws XmlPullParserException, XmlFormatException, IOException {
			Texture texture = null;
			int event = parser.getEventType();
		    while(event != XmlPullParser.END_DOCUMENT) {
		    	switch(event) {
		    	case XmlPullParser.START_TAG:
		    	{
		    		final String tag = parser.getName();
		    		if (tag.equals(Texture.XML_TAG)) {
		    			texture = prepareTexture();
		    		} else if (tag.equals(TexturePart.XML_TAG)) {
		    			if (texture == null) {
		    				throw new XmlFormatException("TAG: part should be contained in TAG:" + Texture.XML_TAG);
		    			}
		    			TexturePart part = parseTexturePart();
		    			Bitmap partBmp = loadTexturePartBitmap(part);
		    			
		    			if (partBmp != null) {
		    				part.setWidth(partBmp.getWidth());
		    				part.setHeight(partBmp.getHeight());
		    				texture.loadPart(part, partBmp);
		    				partBmp.recycle();
		    			} else {
		    				Log.e(TAG, "cannot decode bitmap:" + part.getName());
		    			}
		    			
		    		}
		    		break;
		    	}
		    	case XmlPullParser.END_TAG:
		    	{
		    		final String tag = parser.getName();
		    		if (tag.equals(Texture.XML_TAG)) {
		    			createTexture(texture);
		    			textures.put(texture.getId(), texture);
		    			
		    		    if (SAVE_TEXTURE_TO_FILE) {
		    		    	if (saveToFile(texture) == false) {
		    		    		Log.w(TAG, "failed to save texture to file: " + texture.getName());
		    		    	}
		    		    }
		    			texture = null;
		    		}
		    		break;
		    	}
		    	}	// end of parser event
		    	event = parser.next();
		    }
		}
		
		private Texture prepareTexture() throws XmlPullParserException, XmlFormatException {
			String name = "";
			float width = 0.0f;
			float height = 0.0f;
			
			final int attrCount = parser.getAttributeCount();
			int attrIndex = 0;
			while (attrIndex < attrCount) {
				if (parser.getAttributeName(attrIndex).equals(Texture.XML_ATTR_NAME)) {
					name = parser.getAttributeValue(attrIndex);
				} else if (parser.getAttributeName(attrIndex).equals(Texture.XML_ATTR_WIDTH)) {
					width = Float.parseFloat(parser.getAttributeValue(attrIndex));
				} else if (parser.getAttributeName(attrIndex).equals(Texture.XML_ATTR_HEIGHT)) {
					height = Float.parseFloat(parser.getAttributeValue(attrIndex));
				}
				attrIndex++;
			}
			
			if (width == 0.0f || height == 0.0f) {
				throw new XmlFormatException("TAG:" + parser.getName() + " missing need both attributes: width and height.");
			}
			
			return new Texture(width, height, name);
		}
		
		private TexturePart parseTexturePart() throws XmlPullParserException, XmlFormatException {
			String name = null;
			float x = -1;
			float y = -1;
			
			final int attrCount = parser.getAttributeCount();
			int attrIndex = 0;
			while (attrIndex < attrCount) {
				if (parser.getAttributeName(attrIndex).equals(TexturePart.XML_TAG_NAME)) {
					name = parser.getAttributeValue(attrIndex);
				} else if (parser.getAttributeName(attrIndex).equals(TexturePart.XML_TAG_X)) {
					x = Float.parseFloat(parser.getAttributeValue(attrIndex));
				} else if (parser.getAttributeName(attrIndex).equals(TexturePart.XML_TAG_Y)) {
					y = Float.parseFloat(parser.getAttributeValue(attrIndex));
				}
				attrIndex++;
			}
			
			if (name == null || x < 0.0f || y < 0.0f) {
				throw new XmlFormatException("TAG:" + TexturePart.XML_TAG + " missing attributes " + TexturePart.XML_TAG_NAME + " or " + TexturePart.XML_TAG_X + " or " + TexturePart.XML_TAG_Y);
			}
			
			return new TexturePart(name, x, y);
		}
		
		private Bitmap loadTexturePartBitmap(TexturePart part) throws XmlFormatException {
			boolean externalResource = false;
			int resId = 0;
			if (TextureResource.MAPPING.containsKey(part.getName())) {
				resId = TextureResource.MAPPING.get(part.getName());
			} else {
				if (externalResources != null) {
					resId = externalResources.getIdentifier(TextureResource.RES_PREFIX + part.getName(), null, null);
				}
				if (resId == 0) {
					throw new XmlFormatException("cannot find bitmap:" + part.getName());
				}
				externalResource = true;
			}
			
			Bitmap partBmp = null;
			if (externalResource) {
				InputStream is = externalResources.openRawResource(resId);
				if (is != null) {
					partBmp = BitmapFactory.decodeStream(is);
				}
			} else {
				partBmp = BitmapFactory.decodeResource(context.getResources(), resId);
			}
			return partBmp;
		}
		
		private boolean createTexture(Texture texture) {
			int[] texId = new int[1];
			
			gl.glGenTextures(1, texId, 0);
			texture.setId(texId[0]);
			
			gl.glBindTexture(GL10.GL_TEXTURE_2D, texId[0]);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, texture.getBitmap(), 0);
		    
			int error = gl.glGetError();
			if (error != GL10.GL_NO_ERROR) {
				Log.e(TAG, "create texture error:" + error);
				return false;
			}
			return true;
		}
		
		private boolean saveToFile(Texture texture) {
			Log.d(TAG, "saveToFile texture:" + texture.getName());
			
			String externalStorageState = Environment.getExternalStorageState();
			if (externalStorageState.equals(Environment.MEDIA_MOUNTED) == false) {
				Log.w(TAG, "saveToFile external storage not mounted:" + externalStorageState);
				return false;
			}
			
			File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			File file = new File(path, "bonnie-texture-" + texture.getName() + ".png");
			
			try {
				OutputStream os = new FileOutputStream(file);
				
				if (Texture.bitmap.compress(Bitmap.CompressFormat.PNG, 100, os) == false) {
					Log.w(TAG, "bitmap compress failed");
					return false;
				}
				
				os.close();
			} catch (IOException e) {
				Log.w(TAG, "saveToFile exception:" + e);
				return false;
			}
			
			return true;
		}
		
	}
}
