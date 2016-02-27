package com.studioirregular.bonniesbrunch.component;

import com.studioirregular.bonniesbrunch.entity.GameEntity;

// Do nothing. Used with AnimationSet to hold entity for a given duration.
public class HoldAnimation extends Animation {

	public HoldAnimation(int zOrder) {
		super(zOrder);
	}
	
	@Override
	protected void onFillBefore(GameEntity parent) {
	}
	
	@Override
	protected void onFillAfter(GameEntity parent) {
	}
	
	@Override
	public void update(long timeDelta, GameEntity parent) {
		onUpdate(timeDelta, parent);
	}

}
