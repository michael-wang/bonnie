package com.studioirregular.bonniesbrunch.entity;

import android.graphics.PointF;

import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.component.ButtonComponent;
import com.studioirregular.bonniesbrunch.component.Toast;
import com.studioirregular.bonniesbrunch.component.Toast.Type;

public class ToastTop extends GameEntity {

	public ToastTop(int zOrder) {
		super(zOrder);
	}
	
	public void setup(int toast, BrunchHolder holder) {
		PointF loc = new PointF();
		holder.getOpenToastLocation(loc);
		this.setup(loc.x, loc.y, 90, 90);
		
		Toast component = new Toast(MIN_GAME_COMPONENT_Z_ORDER);
		component.setup(toast, holder.getHolderSizeType(), Type.Top);
		add(component);
		
		ButtonComponent button = new ButtonComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
		button.setup(loc.x - 4, loc.y - 8, 90 + 4, 90, loc.x, loc.y);
		add(button);
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		return false;
	}

	@Override
	protected void handleGameEvent(GameEvent event) {
		if (event.what == GameEvent.BUTTON_UP) {
			GameEventSystem.scheduleEvent(GameEvent.BRUNCH_TOAST_CLOSED, 0, this);
		}
	}

}
