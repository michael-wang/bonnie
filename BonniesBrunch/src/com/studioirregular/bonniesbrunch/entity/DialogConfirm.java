package com.studioirregular.bonniesbrunch.entity;

import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.InputSystem.TouchEvent;
import com.studioirregular.bonniesbrunch.component.RenderComponent;

public class DialogConfirm extends GameEntity {

	public DialogConfirm(int zOrder) {
		super(zOrder);
	}
	
	@Override
	public void setup(float x, float y, float width, float height) {
		super.setup(x, y, width, height);
		
		setupConfirmButtons("popup_menu_bt2_yes_disable", "popup_menu_bt2_yes_enable", 
				"popup_menu_bt1_no_disable", "popup_menu_bt1_no_enable");
	}
	
	public void setup(float x, float y, float width, float height, 
			String positiveButtonTexture, String positiveButtonPressedTexture,
			String negativeButtonTexture, String negativeButtonPressedTexture) {
		super.setup(x, y, width, height);
		
		setupConfirmButtons(positiveButtonTexture, positiveButtonPressedTexture,
				negativeButtonTexture, negativeButtonPressedTexture);
	}
	
	public void setContent(String texture, int width, int height) {
		RenderComponent content = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		content.setup(width, height);
		content.setup(texture);
		add(content);
	}
	
	@Override
	public boolean dispatch(TouchEvent event, GameEntity parent) {
		super.dispatch(event, parent);
		return true;	// eat up all touch events.
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		switch (event.what) {
		case GameEvent.GAME_CONFIRM_NO:
		case GameEvent.GAME_CONFIRM_YES:
			GameEventSystem.scheduleEvent(event.what);
			break;
		}
	}
	
	private void setupConfirmButtons(String positiveButtonTexture, String positiveButtonPressedTexture,
			String negativeButtonTexture, String negativeButtonPressedTexture) {
		// no
		ButtonEntity.Builder builder = new ButtonEntity.Builder(170, 273, 150, 66, negativeButtonTexture, negativeButtonPressedTexture);
		builder.playSoundWhenClicked("mainmenu_bt_s1");
		builder.emitEventWhenPressed(GameEvent.GAME_CONFIRM_NO);
		builder.offsetTouchArea(-16, -16, 16, 16);
		ButtonEntity button = new ButtonEntity(MIN_GAME_ENTITY_Z_ORDER, builder);
		add(button);
		
		// yes
		builder = new ButtonEntity.Builder(400, 273, 150, 66, positiveButtonTexture, positiveButtonPressedTexture);
		builder.playSoundWhenClicked("mainmenu_bt_s1");
		builder.emitEventWhenPressed(GameEvent.GAME_CONFIRM_YES);
		builder.offsetTouchArea(-16, -16, 16, 16);
		button = new ButtonEntity(MIN_GAME_ENTITY_Z_ORDER, builder);
		add(button);
	}

}
