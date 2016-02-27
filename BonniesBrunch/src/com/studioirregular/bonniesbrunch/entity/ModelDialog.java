package com.studioirregular.bonniesbrunch.entity;

import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.InputSystem.TouchEvent;
import com.studioirregular.bonniesbrunch.component.RenderComponent;

public class ModelDialog extends GameEntity {

	public ModelDialog(int zOrder) {
		super(zOrder);
	}
	
	public void setBackground(String texture, int width, int height) {
		RenderComponent bg = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		bg.setup(width, height);
		bg.setup(texture);
		add(bg);
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
	}

}
