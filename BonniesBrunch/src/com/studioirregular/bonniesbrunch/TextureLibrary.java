package com.studioirregular.bonniesbrunch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.studioirregular.bonniesbrunch.TextureSystem.Texture;
import com.studioirregular.bonniesbrunch.TextureSystem.TextureParser;

public class TextureLibrary {
	
	private static final String TAG = "texture-library";

	public static TextureLibrary build(Context context, String xmlRes) {
		int textureXmlRes = context.getResources().getIdentifier(context.getPackageName() + ":xml/" + xmlRes, null, null);
		if (textureXmlRes == 0) {
			Log.e(TAG, "cannot find xmlRes:" + xmlRes);
			return null;
		}
		
		return build(context, textureXmlRes);
	}
	
	public static TextureLibrary build(Context context, int textureXmlRes) {
		XmlResourceParser xml = context.getResources().getXml(textureXmlRes);
		if (xml == null) {
			Log.e(TAG, "loadAll cannot find XML resource: " + textureXmlRes);
			return null;
		}
		
		TextureParser parser = new TextureParser(xml);
		List<Texture> textureList = null;
		try {
			textureList = parser.parse();
		} catch (XmlPullParserException e) {
			Log.e(TAG, "loadAll exception:" + e);
			return null;
		} catch (com.studioirregular.bonniesbrunch.TextureSystem.TextureParser.XmlFormatException e) {
			Log.e(TAG, "loadAll exception:" + e);
			return null;
		} catch (IOException e) {
			Log.e(TAG, "loadAll exception:" + e);
			return null;
		} finally {
			xml.close();
		}
		
		TextureLibrary library = new TextureLibrary();
		library.resourceId = textureXmlRes;
		for (Texture texture : textureList) {
			library.add(texture);
		}
		return library;
	}
	
	public void add(Texture texture) {
		textures.add(texture);
	}
	
	public int getCount() {
		return textures.size();
	}
	
	public Texture get(int index) {
		return textures.get(index);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " resourceId:" + resourceId + ",#textures:" + textures.size();
	}
	
	private int resourceId = 0;
	private List<Texture> textures = new ArrayList<Texture>();
}
