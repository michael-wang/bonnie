package com.studioirregular.bonniep2;

import android.util.Log;

public class PauseButtonEntity extends BasicEntity {

	private static final boolean DO_LOG = false;
	private static final String TAG = "pause-button-entity";
	
	public PauseButtonEntity(SceneBase scene, String id) {
		super(scene, id);
		
		add(new ButtonComponent("button", this, 645, 0, 66, 48));
		
		GLTexture texture = TextureSystem.getInstance().getPart("game_pause_01_locked");
		render = new StatfulRenderComponent("render", 645, 0, 66, 48, STATE_DISABLED, texture);
		
		texture = TextureSystem.getInstance().getPart("game_pause_01");
		render.addState(STATE_NORMAL, texture);
		
		texture = TextureSystem.getInstance().getPart("game_pause_01_pressed");
		render.addState(STATE_PRESSED, texture);
		
		add(render);
	}
	
	@Override
	public void setEnable(boolean enabled) {
		super.setEnable(enabled);
		if (DO_LOG) Log.d(TAG, "setEnabled " + enabled);
		
		if (!enabled) {
			render.setState(STATE_DISABLED);
		} else {
			render.setState(STATE_NORMAL);
		}
	}
	
	@Override
	public void onEvent(Event event) {
		super.onEvent(event);
		if (DO_LOG) Log.d(TAG, "onEvent event:" + event + ",enabled:" + enabled + ",paused:" + paused);
		if (event.what == ComponentEvent.BUTTON_DOWN) {
			if (enabled) {
				render.setState(STATE_PRESSED);
			}
		} else if (event.what == ComponentEvent.BUTTON_UP) {
			if (enabled) {
				render.setState(STATE_NORMAL);
				if (!paused) {
					pause();
				} else {
					resume();
				}
			}
		}
	}
	
	private void pause() {
		paused = true;
		scene.send(this, new SceneEvent(SceneEvent.SCENE_PAUSE));
	}
	
	private void resume() {
		paused = false;
		scene.send(this, new SceneEvent(SceneEvent.SCENE_RESUME));
	}
	
	private static final int STATE_DISABLED	= 1;
	private static final int STATE_NORMAL	= 2;
	private static final int STATE_PRESSED	= 3;
	
	private boolean paused = false;
	private StatfulRenderComponent render;

}
