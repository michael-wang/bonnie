package com.studioirregular.bonniesbrunch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.opengl.GLUtils;
import android.os.Environment;
import android.util.Log;

import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;

// TODO: reduce memory usage.
public class TextureSystem {
	
	private static final String TAG = "texture-system";
	
	private static final int DEFAULT_BITMAP_DENSITY = 160;
	
	// singleton
	public static TextureSystem getInstance() {
		if (sInstance == null) {
			sInstance = new TextureSystem();
		}
		return sInstance;
	}
	
	public static void releaseInstance() {
		if (sInstance != null) {
			sInstance = null;
		}
	}
	
	public static final class Texture {
		public final String id;
		public final float width;
		public final float height;
		public boolean loaded = false;
		public Texture(String id, float width, float height) {
			//if (Config.DEBUG_LOG) Log.d(TAG, "Texture allocated id:" + id);
			this.id = id;
			this.width = width;
			this.height = height;
		}
		
		public int glName = 0;
		public List<TexturePartition> partitions = new ArrayList<TexturePartition>();
		
		public void add(TexturePartition part) {
			if (Config.DEBUG_LOG) Log.d(TAG, "texture:" + id + " add part:" + part.id);
			part.textureId = id;
			partitions.add(part);
		}
		
		public void setGLTextureName(int glName) {
			this.glName = glName;
			for (TexturePartition part : partitions) {
				part.glName = glName;
			}
		}
		
		public void setLoad(boolean load) {
			this.loaded = load;
			for (TexturePartition p : partitions) {
				p.load = load;
			}
		}
		
		@Override
		public String toString() {
			return "Texture id:" + id + ",w:" + width + ",h:" + height + ",glName:" + glName + ",#partitions:" + partitions.size();
		}
	}
	
	public static final class TexturePartition {
		public String id;	// id is also resource name
		public RectF box;
		public TexturePartition(String id, float x, float y) {
			//if (Config.DEBUG_LOG) Log.d(TAG, "TexturePartition allocated id:" + id);
			
			this.id = id;
			this.box = new RectF(x, y, x, y);
		}
		
		public RectF getBox() {
			return box;
		}
		
		public void setBox(float left, float top, float right, float bottom) {
			box.set(left, top, right, bottom);
		}
		
		public boolean load = false;
		public String textureId;
		public int glName = 0;
		
		@Override
		public String toString() {
			return "TexturePartition id:" + id + ",textureId:" + textureId + ",glName:" + glName;
		}
	}
	
	public synchronized Texture getTexture(String id) {
		for (Texture texture : textures) {
			if (texture.id.equals(id)) {
				return texture;
			}
		}
		return null;
	}
	
	public synchronized TexturePartition getPartition(String partitionId) {
		for (Texture texture : textures) {
			for (TexturePartition partition : texture.partitions) {
				if (partition.id.equals(partitionId)) {
					return partition;
				}
			}
		}
		Log.e(TAG, "texture partition not found:" + partitionId);
		return null;
	}
	
	public void onSurfaceCreate(GL10 gl, Context context) {
		if (Config.DEBUG_LOG) Log.d(TAG, "onSurfaceCreate");
		if (textures.isEmpty()) {
			return;
		}
		
		invalidateAllTextures();
		loadAllTextures(gl, context);
		
		GameEventSystem.scheduleEvent(GameEvent.TEXTURE_RELOAD_BEGIN, pendingLoadTextures.size());
	}
	
	public void update(GL10 gl, Context context) {
		if (suspendLoading) {
			return;
		}
		
		if (pendingReleaseTextures.isEmpty() == false) {
			releaseTextures(gl, pendingReleaseTextures);
			pendingReleaseTextures.clear();
			return;
		}
		
		// got pending texture to load, either from load library or reload all textures.
		if (!pendingLoadTextures.isEmpty()) {
			Texture texture = pendingLoadTextures.removeFirst();
			loadSingleTexture(gl, context, texture);
			
			// report progress.
			GameEventSystem.scheduleEvent(GameEvent.TEXTURE_LOADING, pendingLoadTextures.size());
			
			// notify done texture loading.
			if (pendingLoadTextures.isEmpty()) {
				if (pendingLoadLibrary != null) {
					// texture loading start by load library
					GameEventSystem.scheduleEvent(GameEvent.TEXTURE_LIBRARY_LOAD_END, 0, pendingLoadLibrary);
					pendingLoadLibrary = null;
				} else {
					// texture loading start by reload all textures.
					GameEventSystem.scheduleEvent(GameEvent.TEXTURE_RELOAD_END);
				}
				
				// so system can GC them.
				buildInBitmap = null;
				buildInCanvas = null;
			}
		}
	}
	
	// support loading one library at a time.
	public boolean load(TextureLibrary library) {
		if (Config.DEBUG_LOG) Log.w(TAG, "load library:" + library);
		
		if (pendingLoadLibrary != null) {
			if (Config.DEBUG_LOG) Log.w(TAG, "load library:" + library + " failed, already loading one:" + pendingLoadLibrary);
			return false;
		}
		
		final int count = library.getCount();
		for (int i = 0; i < count; i++) {
			final Texture texture = library.get(i);
			Texture existed = getTexture(texture.id);
			if (existed == null) {
				textures.add(texture);
				pendingLoadTextures.add(texture);
				if (Config.DEBUG_LOG) Log.d(TAG, "add texture:" + texture);
			} else {
				if (Config.DEBUG_LOG) Log.w(TAG, "load texture existed:" + texture.id);
				continue;
			}
		}
		
		GameEventSystem.scheduleEvent(GameEvent.TEXTURE_LIBRARY_LOAD_BEGIN, pendingLoadTextures.size(), library);
		
		pendingLoadLibrary = library;
		if (Config.DEBUG_LOG) showCurrentTextures();
		return true;
	}
	
	private void showCurrentTextures() {
		for (Texture texture : textures) {
			Log.d(TAG, "texture:" + texture.id + ",loaded:" + texture.loaded);
		}
	}
	
	public void release(TextureLibrary library) {
		if (Config.DEBUG_LOG) Log.w(TAG, "release library:" + library);
		
		final int count = library.getCount();
		for (int i = 0; i < count; i++) {
			Texture texture = library.get(i);
			if (textures.remove(texture)) {
				if (Config.DEBUG_LOG) Log.d(TAG, "texture removed:" + texture);
				pendingReleaseTextures.add(texture);
			} else {
				if (Config.DEBUG_LOG) Log.w(TAG, "release: texture not found:" + texture);
			}
		}
		
		if (Config.DEBUG_LOG) showCurrentTextures();
	}
	
	public void suspendLoading(boolean suspend) {
		suspendLoading = suspend;
	}
	
	private void invalidateAllTextures() {
		final int count = textures.size();
		for (int i = 0; i < count; i++) {
			textures.get(i).setLoad(false);
		}
	}
	
	private void loadAllTextures(GL10 gl, Context context) {
		final int count = textures.size();
		for (int i = 0; i < count; i++) {
			final Texture texture = textures.get(i);
			if (!texture.loaded && !pendingLoadTextures.contains(texture)) {
				pendingLoadTextures.add(texture);
			}
		}
	}
	
	private boolean loadSingleTexture(GL10 gl, Context context, Texture texture) {
		if (Config.DEBUG_LOG) Log.w(TAG, "loadSingleTexture: " + texture);
		Bitmap bitmap = loadTextureBitmap(context, texture);
		if (bitmap == null) {
			return false;
		}
		
		if (generateGLTexture(gl, texture, bitmap) == false) {
			return false;
		}
		
		if (Config.TEXTURE_SYSTEM_SAVE_TEXTURE_TO_FILE) {
			saveToFile(texture, bitmap);
		}
		
		return true;
	}
	
	public void releaseTextures(GL10 gl, List<Texture> victoms) {
		final int count = victoms.size();
		if (Config.DEBUG_LOG) Log.w(TAG, "releaseTextureLibrary count:" + count);
		
		int[] names = new int[count];
		for (int i = 0; i < count; i++) {
			final Texture texture = victoms.get(i);
			names[i] = texture.glName;
			if (Config.DEBUG_LOG) Log.w(TAG, "release texture:" + texture);
		}
		
		ByteBuffer bf = ByteBuffer.allocateDirect(4*count);
		bf.order(ByteOrder.nativeOrder());
		IntBuffer nameBuf = bf.asIntBuffer();
		nameBuf.put(names);
		nameBuf.position(0);
		gl.glDeleteTextures(count, nameBuf);
	}
	
	public Bitmap loadTextureBitmap(Context context, Texture texture) {
		//if (Config.DEBUG_LOG) Log.d(TAG, "loadTextureBitmap: " + texture);
		if (context == null || texture == null) {
			Log.e(TAG, "build: context:" + context + ",texture:" + texture);
			return null;
		}
		
		if (texture.id == null || texture.id.length() == 0) {
			Log.e(TAG, "build: texture.id:" + texture.id);
			return null;
		}
		
		// check width/height
		if (texture.height <= 0 || texture.width <= 0) {
			Log.e(TAG, "build: bad texture width or height, w:" + texture.width  + ",h:" + texture.height);
			return null;
		}
		
		int width = (int)texture.width;
		int height = (int)texture.height;
		if (((width & (width - 1)) != 0) || ((height & (height - 1)) != 0)) {
			Log.e(TAG, "width or height not power of two, w:" + width + ",h:" + height);
			return null;
		}
		
		if (Config.TEXTURE_DEFAULT_WIDTH != width || Config.TEXTURE_DEFAULT_WIDTH != height) {
			if (Config.DEBUG_LOG) Log.e(TAG, "not support texture dimension, width:" + width + ",height:" + height);
			return null;
		}
		
		if (buildInBitmap == null) {
			buildInBitmap = Bitmap.createBitmap(Config.TEXTURE_DEFAULT_WIDTH, Config.TEXTURE_DEFAULT_HEIGHT, Bitmap.Config.ARGB_8888);
			buildInBitmap.setDensity(DEFAULT_BITMAP_DENSITY);
			buildInCanvas = new Canvas(buildInBitmap);
		}
		
		Bitmap bitmap = buildInBitmap;
		Canvas canvas = buildInCanvas;
		// clear canvas
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC);
		
		Resources resources = null;
		if (Config.DEBUG_LOAD_EXTERNAL_RESOURCE_FOR_TEXTURE) {
			try {
				resources = context.getPackageManager().getResourcesForApplication(Config.EXTERNAL_RESOURCES_PACKAGE);
			} catch (NameNotFoundException e) {
				Log.e(TAG, "cannot find external resource package:" + Config.EXTERNAL_RESOURCES_PACKAGE);
				return null;
			}
		} else {
			resources = context.getResources();
		}
		
		final int count = texture.partitions.size();
		for (int i = 0; i < count; i++) {
			TexturePartition part = texture.partitions.get(i);
			int resId = 0;
			if (Config.DEBUG_LOAD_EXTERNAL_RESOURCE_FOR_TEXTURE) {
				resId = resources.getIdentifier(Config.EXTERNAL_RES_PREFIX + part.id, null, null);
			} else {
				resId = resources.getIdentifier(context.getPackageName() + ":drawable/" + part.id, null, null);
			}
			
			if (resId == 0) {
				if (Config.DEBUG_LOG) Log.w(TAG, "cannot find texture resource:" + part.id);
				continue;
			}
			
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inScaled = false;
			Bitmap partitionBitmap = BitmapFactory.decodeResource(resources, resId, opts);
			if (partitionBitmap == null) {
				if (Config.DEBUG_LOG) Log.w(TAG, "cannot decode bitmap:" + part.id);
				continue;
			}
			
			final float left = part.box.left;
			final float top = part.box.top;
			part.box.set(left, top, left + partitionBitmap.getWidth(), top + partitionBitmap.getHeight());
			if (Config.TEXTURE_SYSTEM_DETECT_PARTITION_COLLISION) {
				texturePartitionCollisionCheck(texture, part);
			}
			
			canvas.drawBitmap(partitionBitmap, left, top, null);
			if (Config.DEBUG_DRAW_TEXTURE_BOUND) {
				canvas.drawRect(left, top, left + partitionBitmap.getWidth(), top + partitionBitmap.getHeight(), paintTextureBound);
			}
			
			partitionBitmap.recycle();
		}
		
		return bitmap;
	}
	
	private boolean texturePartitionCollisionCheck(Texture texture, TexturePartition part) {
		final int count = texture.partitions.size();
		for (int i = 0; i < count; i++) {
			TexturePartition exist = texture.partitions.get(i);
			if (exist.load == false || exist == part) {
				continue;
			}
			if (exist.box.contains(part.box)) {
				Log.w(TAG, "texture partition collosion, part1:" + part.id + " box:" + part.box + ",part2:" + exist.id + " box:" + exist.box);
				return false;
			}
		}
		return true;
	}
	
	public boolean generateGLTexture(GL10 gl, Texture texture, Bitmap bitmap) {
		//if (Config.DEBUG_LOG) Log.d(TAG, "generateGLTexture texture:" + texture + ",bitmap:" + bitmap);
		
		if (texture == null || bitmap == null) {
			return false;
		}
		
		gl.glGenTextures(1, textureName, 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureName[0]);
		
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
		
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		
		texture.setGLTextureName(textureName[0]);
		texture.setLoad(true);
		
		return true;
	}
	
	static class TextureParser {
		private XmlPullParser parser;
		private List<Texture> result = new ArrayList<Texture>();
		
		public TextureParser(XmlPullParser parser) {
			this.parser = parser;
		}
		
		public class XmlFormatException extends Exception {
			/**
			 * throw when parser detects any uncomfortable format.
			 */
			private static final long serialVersionUID = 1L;

			public XmlFormatException(String s) {
				super(s);
			}
		}
		
		private static final String XML_TAG_TEXTURE = "texture";
		private static final String XML_ATTR_TEXTURE_NAME = "name";
		private static final String XML_ATTR_TEXTURE_WIDTH = "width";
		private static final String XML_ATTR_TEXTURE_HEIGHT = "height";
		private static final String XML_TAG_PARTITION = "part";
		private static final String XML_ATTR_PARTITION_NAME = "name";
		private static final String XML_ATTR_PARTITION_X = "x";
		private static final String XML_ATTR_PARTITION_Y = "y";
		
		public List<Texture> parse() throws XmlPullParserException, XmlFormatException, IOException {
			Texture texture = null;
			int event = parser.getEventType();
		    while(event != XmlPullParser.END_DOCUMENT) {
		    	switch(event) {
		    	case XmlPullParser.START_TAG:
		    	{
		    		final String tag = parser.getName();
		    		if (tag.equals(XML_TAG_TEXTURE)) {
		    			texture = prepareTexture();
		    		} else if (tag.equals(XML_TAG_PARTITION)) {
		    			if (texture == null) {
		    				throw new XmlFormatException("TAG: part should be contained in TAG:" + XML_TAG_TEXTURE);
		    			}
		    			TexturePartition part = parseTexturePart(texture);
		    			texture.add(part);
		    		}
		    		break;
		    	}
		    	case XmlPullParser.END_TAG:
		    	{
		    		final String tag = parser.getName();
		    		if (tag.equals(XML_TAG_TEXTURE)) {
		    			result.add(texture);
		    			texture = null;
		    		}
		    		break;
		    	}
		    	}	// end of parser event
		    	event = parser.next();
		    }
		    return result;
		}
		
		private Texture prepareTexture() throws XmlPullParserException, XmlFormatException {
			String name = "";
			float width = 0.0f;
			float height = 0.0f;
			
			final int attrCount = parser.getAttributeCount();
			int attrIndex = 0;
			while (attrIndex < attrCount) {
				if (parser.getAttributeName(attrIndex).equals(XML_ATTR_TEXTURE_NAME)) {
					name = parser.getAttributeValue(attrIndex);
				} else if (parser.getAttributeName(attrIndex).equals(XML_ATTR_TEXTURE_WIDTH)) {
					width = Float.parseFloat(parser.getAttributeValue(attrIndex));
				} else if (parser.getAttributeName(attrIndex).equals(XML_ATTR_TEXTURE_HEIGHT)) {
					height = Float.parseFloat(parser.getAttributeValue(attrIndex));
				}
				attrIndex++;
			}
			
			if (width == 0.0f || height == 0.0f) {
				throw new XmlFormatException("TAG:" + parser.getName() + " missing need both attributes: width and height.");
			}
			
			return new Texture(name, width, height);
		}
		
		private TexturePartition parseTexturePart(Texture texture) throws XmlPullParserException, XmlFormatException {
			String name = null;
			float x = -1;
			float y = -1;
			
			final int attrCount = parser.getAttributeCount();
			int attrIndex = 0;
			while (attrIndex < attrCount) {
				if (parser.getAttributeName(attrIndex).equals(XML_ATTR_PARTITION_NAME)) {
					name = parser.getAttributeValue(attrIndex);
				} else if (parser.getAttributeName(attrIndex).equals(XML_ATTR_PARTITION_X)) {
					x = Float.parseFloat(parser.getAttributeValue(attrIndex));
				} else if (parser.getAttributeName(attrIndex).equals(XML_ATTR_PARTITION_Y)) {
					y = Float.parseFloat(parser.getAttributeValue(attrIndex));
				}
				attrIndex++;
			}
			
			if (name == null || x < 0.0f || y < 0.0f) {
				throw new XmlFormatException("texture:" + texture.id + " TAG:" + XML_TAG_PARTITION
						+ " missing attributes: name:" + name + ",x:" + x + ",y:" + y);
			}
			
			return new TexturePartition(name, x, y);
		}
		
	}
	
	private boolean saveToFile(Texture texture, Bitmap bitmap) {
		Log.w(TAG, "saveToFile texture:" + texture.id);
		
		String externalStorageState = Environment.getExternalStorageState();
		if (externalStorageState.equals(Environment.MEDIA_MOUNTED) == false) {
			Log.w(TAG, "saveToFile external storage not mounted:" + externalStorageState);
			return false;
		}
		
		File path = Environment.getExternalStorageDirectory();
		File file = new File(path, "bonnie-texture-" + texture.id + ".png");
		
		try {
			OutputStream os = new FileOutputStream(file);
			
			if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, os) == false) {
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
	
	private static TextureSystem sInstance = null;
	
	public List<Texture> textures = new ArrayList<Texture>();
	private TextureLibrary pendingLoadLibrary = null;
	// load one texture at a frame so we won't blocking GL thread too long.
	private LinkedList<Texture> pendingLoadTextures = new LinkedList<Texture>();
	private List<Texture> pendingReleaseTextures = new ArrayList<Texture>();
	private boolean suspendLoading;
	
	private Bitmap buildInBitmap;
	private Canvas buildInCanvas;
	private int[] textureName = new int[1];
	
	private Paint paintTextureBound;
	
	private TextureSystem() {
		if (Config.DEBUG_LOG) Log.d(TAG, "TextureSystem allocated");
		paintTextureBound = new Paint();
		if (Config.DEBUG_DRAW_TEXTURE_BOUND) {
			paintTextureBound.setColor(Color.RED);
			paintTextureBound.setStrokeWidth(1);
			paintTextureBound.setStyle(Paint.Style.STROKE);
		}
	}

}
