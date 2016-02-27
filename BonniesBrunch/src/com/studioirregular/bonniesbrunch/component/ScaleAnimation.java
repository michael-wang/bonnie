package com.studioirregular.bonniesbrunch.component;

import com.studioirregular.bonniesbrunch.entity.GameEntity;

public class ScaleAnimation extends Animation {

	public ScaleAnimation(int zOrder, float fromScale, float toScale, int duration) {
		super(zOrder);
		
		this.fromScale = fromScale;
		this.toScale = toScale;
		setDuration(duration);
	}
	
	@Override
	protected void onFillBefore(GameEntity parent) {
		parent.scale(fromScale);
	}
	
	@Override
	protected void onFillAfter(GameEntity parent) {
		parent.scale(toScale);
	}
	
	@Override
	public void update(long timeDelta, GameEntity parent) {
		if (onUpdate(timeDelta, parent) == false) {
			return;
		}
		
		float scale = fromScale + (toScale - fromScale) * elapsedTime / duration;
		parent.scale(scale);
	}
	
	@Override
	protected void onFinished(GameEntity parent) {
		super.onFinished(parent);
		
		parent.scale(toScale);
	}
	
	private float fromScale, toScale;
}
