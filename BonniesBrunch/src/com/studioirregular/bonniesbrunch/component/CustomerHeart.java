package com.studioirregular.bonniesbrunch.component;

import android.util.Log;

import com.studioirregular.bonniesbrunch.RenderSystem;
import com.studioirregular.bonniesbrunch.RenderSystem.RenderObject;
import com.studioirregular.bonniesbrunch.TextureSystem;
import com.studioirregular.bonniesbrunch.TextureSystem.Texture;
import com.studioirregular.bonniesbrunch.TextureSystem.TexturePartition;
import com.studioirregular.bonniesbrunch.entity.GameEntity;

public class CustomerHeart extends GameComponent {

	private static final String TAG = "customer-heart";
	
	public static enum State {
		Full,
		Half,
		Hollow
	}
	
	public CustomerHeart(int zOrder) {
		super(zOrder);
		
		full = new RenderObject(WIDTH, HEIGHT);
		TexturePartition part = TextureSystem.getInstance().getPartition("game_heart_01");
		if (part == null) {
			Log.e(TAG, "can't find texture partition: game_heart_01");
			return;
		}
		Texture texture = TextureSystem.getInstance().getTexture(part.textureId);
		full.setTexture(texture, part);
		
		half = new RenderObject(WIDTH, HEIGHT);
		part = TextureSystem.getInstance().getPartition("game_heart_02");
		if (part == null) {
			Log.e(TAG, "can't find texture partition: game_heart_02");
			return;
		}
		
		// assume all hearts on same texture
		half.setTexture(texture, part);
		
		hollow = new RenderObject(WIDTH, HEIGHT);
		part = TextureSystem.getInstance().getPartition("game_heart_03_empty");
		if (part == null) {
			Log.e(TAG, "can't find texture partition: game_heart_03");
			return;
		}
		hollow.setTexture(texture, part);
	}
	
	public void setup(float dx, float dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	public void set(State newState) {
		state = newState;
	}
	
	@Override
	public void update(long timeDelta, GameEntity parent) {
		RenderObject render = null;
		
		if (parent.isVisible() == false) {
			return;
		}
		
		if (State.Full == state) {
			render = full;
		} else if (State.Half == state) {
			render = half;
		} else {
			render = hollow;
		}
		
		if (render != null) {
			render.setPosition(parent.getX() + dx, parent.getY() + dy);
			RenderSystem.getInstance().scheduleRenderObject(render);
		}
	}
	
	@Override
	public void reset() {
	}
	
	public static final float WIDTH = 21;
	public static final float HEIGHT = 18; 
	
	private State state = State.Full;
	private float dx, dy;
	
	private RenderObject full;
	private RenderObject half;
	private RenderObject hollow;

}
