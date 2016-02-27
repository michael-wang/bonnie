package com.studioirregular.bonniesbrunch.component;

import android.util.Log;

import com.studioirregular.bonniesbrunch.RenderSystem;
import com.studioirregular.bonniesbrunch.RenderSystem.RenderObject;
import com.studioirregular.bonniesbrunch.TextureSystem;
import com.studioirregular.bonniesbrunch.TextureSystem.Texture;
import com.studioirregular.bonniesbrunch.TextureSystem.TexturePartition;
import com.studioirregular.bonniesbrunch.entity.GameEntity;

public class RenderComponent extends GameComponent {

	private static final String TAG = "render-component";
	
	protected RenderObject render;
	public float width, height;
	protected float dx, dy;
	protected boolean drawColor = false;
	protected boolean drawTexture = false;
	protected boolean visible = true;
	
	public RenderComponent(int zOrder) {
		super(zOrder);
	}
	
	public void setup(float width, float height) {
		this.width = width;
		this.height = height;
		dx = dy = 0;
	}
	
	public void setupOffset(float dx, float dy) {
		//Log.w(TAG, "setupOffset dx:" + dx + ",dy:" + dy);
		this.dx = dx;
		this.dy = dy;
	}
	
	public void setup(String partitionId) {
		//Log.w(TAG, "setup partitionId:" + partitionId);
		
		TexturePartition texturePart = TextureSystem.getInstance().getPartition(partitionId);
		if (texturePart == null) {
			Log.e(TAG, "cannot find texture partition:" + partitionId);
			return;
		}
		
		Texture texture = TextureSystem.getInstance().getTexture(texturePart.textureId);
		
		if (texture == null || texturePart == null) {
			Log.w(TAG, "cannot find texture: " + texturePart.textureId + ",partition:" + partitionId);
			Log.w(TAG, "texture:" + texture + ",partition:" + texturePart);
			return;
		}
		
		if (render == null) {
			render = new RenderObject(width, height);
		}
		render.setTexture(texture, texturePart); 
		
		drawTexture = true;
	}
	
	// color: R, G, B, Alpha
	public void setup(float[] color) {
		//Log.w(TAG, "setup color:" + color[0] + "," + color[1] + "," + color[2] + "," + color[3]);
		assert (render != null);
		
		if (render == null) {
			render = new RenderObject(width, height);
		}
		render.setColor(color[0], color[1], color[2], color[3]);
		drawColor = true;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public void updateColor(float[] color) {
		render.setColor(color[0], color[1], color[2], color[3]);
	}
	
	public void changeWidth(float newWidth) {
		if (render != null) {
			render.changeWidth(newWidth);
			width = newWidth;
		}
	}
	
	public void offsetTextureX(boolean fromStart, float offset) {
		if (render != null) {
			render.offsetTextureX(fromStart, offset);
		}
	}
	
	public void changeHeight(float newHeight) {
		if (render != null) {
			render.changeHeight(newHeight);
			height = newHeight;
		}
	}
	
	public void offsetTextureY(boolean fromStart, float offset) {
		if (render != null) {
			render.offsetTextureY(fromStart, offset);
		}
	}
	
	@Override
	public void update(long timeDelta, GameEntity parent) {
		if (render != null && visible && parent.isVisible()) {
			render.setPosition(parent.getX() + dx, parent.getY() + dy);
			render.changeScale(parent.scale);
			RenderSystem.getInstance().scheduleRenderObject(render);
		}
	}
	
	@Override
	public void reset() {
		visible = true;
	}

}
