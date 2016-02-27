package com.studioirregular.bonniesbrunch.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.test.InstrumentationTestCase;

import com.studioirregular.bonniesbrunch.TextureSystem;
import com.studioirregular.bonniesbrunch.TextureSystem.Texture;
import com.studioirregular.bonniesbrunch.TextureSystem.TexturePartition;

public class TextureSystemTest extends InstrumentationTestCase {

	public void testBuildTexture_Success() {
		final String TEXTURE_ID = "test";
		Texture texture = new Texture(TEXTURE_ID, 1024, 1024);
		
		texture.add(new TexturePartition("game_bg_01", 0, 0));
		
		Context context = getInstrumentation().getContext();
		Bitmap bitmap = TextureSystem.getInstance().loadTextureBitmap(context, texture);
		
		assertEquals(TEXTURE_ID, texture.id);
		assertEquals(1024f, texture.width);
		assertEquals(1024f, texture.height);
		assertEquals(0, texture.glName);
		assertNotNull(bitmap);
		
		TexturePartition part = texture.partitions.get(0);
		assertEquals(720.0f, part.box.width());
		assertEquals(260.0f, part.box.height());
		assertEquals(TEXTURE_ID, part.textureId);
		assertEquals(0, part.glName);
		assertEquals(false, part.load);
	}
	
	public void testBuildTexture_Failed() {
		Bitmap bitmap = TextureSystem.getInstance().loadTextureBitmap(null, new Texture("test", 1024, 1024));
		assertNull(bitmap);
		
		Context context = getInstrumentation().getContext();
		bitmap = TextureSystem.getInstance().loadTextureBitmap(context, new Texture("", 1024, 1024));
		assertNull(bitmap);
		
		bitmap = TextureSystem.getInstance().loadTextureBitmap(context, new Texture("test", 127, 1024));
		assertNull(bitmap);
		
		bitmap = TextureSystem.getInstance().loadTextureBitmap(context, new Texture("test", 1024, 127));
		assertNull(bitmap);
		
		bitmap = TextureSystem.getInstance().loadTextureBitmap(context, null);
		assertNull(bitmap);
	}
}
