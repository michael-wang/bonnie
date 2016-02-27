package com.studioirregular.bonniesbrunch.component;

import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.RenderSystem;
import com.studioirregular.bonniesbrunch.RenderSystem.RenderObject;
import com.studioirregular.bonniesbrunch.TextureSystem;
import com.studioirregular.bonniesbrunch.TextureSystem.Texture;
import com.studioirregular.bonniesbrunch.TextureSystem.TexturePartition;
import com.studioirregular.bonniesbrunch.entity.GameEntity;

public class DecimalDigit extends GameComponent {

	private static final String TAG = "decimal-digit";
	
	public static class TextureConfig {
		public Texture texture;
		public TexturePartition[] partitions = new TexturePartition[10];
		public TextureConfig(String textureName) {
			texture = TextureSystem.getInstance().getTexture(textureName);
		}
		// digit: 0, 1, ..., 9
		public void addPartition(int digit, String partitionName) {
			if (Config.DEBUG_LOG) Log.d(TAG, "TextureConfig::addPartition digit:" + digit + ", partition:" + partitionName);
			partitions[digit] = TextureSystem.getInstance().getPartition(partitionName);
		}
	}
	
	public DecimalDigit(int zOrder) {
		super(zOrder);
	}
	
	public void setup(float width, float height, float dx, float dy, TextureConfig textureConfig) {
		if (Config.DEBUG_LOG) Log.d(TAG, "setup w:" + width + ",h:" + height + ",dx:" + dx + ",dy:" + dy);
		
		this.dx = dx;
		this.dy = dy;
		
		renders = new RenderObject[10];
		for (int i = 0; i < 10; i++) {
			RenderObject render = new RenderObject(width, height);
			assert textureConfig.partitions[i] != null;
			
			render.setTexture(textureConfig.texture, textureConfig.partitions[i]);
			renders[i] = render;
		}
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public void setValue(int value) {
		if (Config.DEBUG_LOG) Log.d(TAG, "setValue value:" + value);
		
		assert 0 <= value && value <= 9;
		this.value = value;
	}
	
	@Override
	public void update(long timeDelta, GameEntity parent) {
		if (parent.isVisible() && visible) {
			if (0 <= value && value < 10) {
				if (renders[value] != null) {
					RenderObject render = renders[value];
					
					render.setPosition(parent.getX() + dx, parent.getY() + dy);
					render.changeScale(parent.scale);
					RenderSystem.getInstance().scheduleRenderObject(render);
				}
			} else {
				Log.e(TAG, "value out of range (0 - 9):" + value);
			}
		}
	}
	
	@Override
	public void reset() {
	}
	
	private boolean visible = true;
	private int value;
	private float dx, dy;
	private RenderObject[] renders;

}
