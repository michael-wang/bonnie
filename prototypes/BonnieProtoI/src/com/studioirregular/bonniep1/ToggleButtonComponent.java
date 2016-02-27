package com.studioirregular.bonniep1;


public class ToggleButtonComponent implements GameEntity.StateChangedListener {
	
	private GameEntity entity;
	private Integer state = GameEntity.STATE_BUTTON_UP;
	
	public ToggleButtonComponent(GameEntity entity, Integer defaultState) {
		this.entity = entity;
		this.state = defaultState;
	}
	
	@Override
	public void onStateChanged(Integer newState) {
		boolean stateChanged = false;
		
		switch (newState) {
		case GameEntity.STATE_TOUCH_DOWN:
			if (state == GameEntity.STATE_BUTTON_DOWN) {
				state = GameEntity.STATE_BUTTON_UP;
			} else {
				state = GameEntity.STATE_BUTTON_DOWN;
			}
			stateChanged = true;
			break;
		default:
			return;
		}
		
		if (stateChanged) {
			entity.sendEvent(GameEntity.EVENT_STATE_CHANGE, state);
		}
	}
}
