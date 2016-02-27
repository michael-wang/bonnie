package com.studioirregular.bonniep1;

public class ButtonComponent implements GameEntity.StateChangedListener {

	private GameEntity entity;
	private Integer state = GameEntity.STATE_BUTTON_UP;
	
	public ButtonComponent(GameEntity entity) {
		this.entity = entity;
	}
	
	@Override
	public void onStateChanged(Integer newState) {
		boolean stateChanged = false;
		
		switch (newState) {
		case GameEntity.STATE_TOUCH_DOWN:
			state = GameEntity.STATE_BUTTON_DOWN;
			stateChanged = true;
			break;
		case GameEntity.STATE_TOUCH_UP:
			if (state == GameEntity.STATE_BUTTON_DOWN) {
				state = GameEntity.STATE_BUTTON_UP;
				stateChanged = true;
			}
			break;
		case GameEntity.STATE_TOUCH_CANCEL:
			if (state == GameEntity.STATE_BUTTON_DOWN) {
				state = GameEntity.STATE_BUTTON_DOWN_CANCEL;
				stateChanged = true;
			}
			break;
		}
		
		if (stateChanged) {
			entity.sendEvent(GameEntity.EVENT_STATE_CHANGE, state);
		}
	}

}
