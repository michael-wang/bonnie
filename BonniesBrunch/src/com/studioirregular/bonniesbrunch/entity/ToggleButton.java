package com.studioirregular.bonniesbrunch.entity;

import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.component.ButtonComponent;
import com.studioirregular.bonniesbrunch.component.RenderComponent;

public class ToggleButton extends GameEntity {

	public ToggleButton(int zOrder, boolean initStateDown) {
		super(zOrder);
		stateDown = initStateDown;
	}
	
	public void setTouchArea(float offsetLeft, float offsetTop, float offsetRight, float offsetBottom) {
		ButtonComponent button = new ButtonComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
		final float touchWidth = box.width() - offsetLeft + offsetRight;
		final float touchHeight = box.height() - offsetTop + offsetBottom;
		button.setup(box.left + offsetLeft, box.top + offsetTop, touchWidth, touchHeight, box.left, box.top);
		add(button);
	}
	
	public void setupTextures(String textureUp, String textureDown, float width, float height) {
		renderUp = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		renderUp.setup(width, height);
		renderUp.setup(textureUp);
		if (!stateDown) {
			add(renderUp);
		}
		
		renderDown = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		renderDown.setup(width, height);
		renderDown.setup(textureDown);
		if (stateDown) {
			add(renderDown);
		}
	}
	
	public void setupEvents(int whatEventWhenDown, int arg1WhenDown, int whatEventWhenUp, int arg1WhenUp) {
		downEvent = GameEventSystem.getInstance().obtain(whatEventWhenDown, arg1WhenDown);
		upEvent = GameEventSystem.getInstance().obtain(whatEventWhenUp, arg1WhenUp);
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		switch (event.what) {
		case GameEvent.BUTTON_UP:
			if (!stateDown) {
				changeState(true);
			} else {
				changeState(false);
				buttonUp();
			}
			break;
		case GameEvent.BUTTON_CANCEL:
			if (!stateDown) {
				buttonUp();
			}
			break;
		case GameEvent.BUTTON_DOWN:
			if (!stateDown) {
				buttonDown();
			}
			break;
		}
	}
	
	private void buttonDown() {
		remove(renderUp);
		add(renderDown);
	}
	
	private void buttonUp() {
		remove(renderDown);
		add(renderUp);
	}
	
	private void changeState(boolean newState) {
		if (stateDown != newState) {
			stateDown = newState;
			
			if (stateDown) {
				GameEventSystem.scheduleEvent(downEvent.what, downEvent.arg1);
			} else {
				GameEventSystem.scheduleEvent(upEvent.what, upEvent.arg1);
			}
		}
	}
	
	protected boolean stateDown;
	protected RenderComponent renderUp, renderDown;
	protected GameEvent downEvent, upEvent;
}
