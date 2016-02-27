package com.studioirregular.bonniesbrunch.component;

import java.util.ArrayList;
import java.util.List;

import com.studioirregular.bonniesbrunch.entity.GameEntity;

public class AnimationSet extends Animation {

	public AnimationSet(int zOrder) {
		super(zOrder);
	}
	
	public void addAnimation(Animation animation) {
		animations.add(animation);
		duration += animation.getDuration();
	}
	
	@Override
	public void update(long timeDelta, GameEntity parent) {
		if (onUpdate(timeDelta, parent) == false) {
			return;
		}
		
		int i = 0;
		Animation animation = animations.get(i);
		int durationBase = 0;
		
		while (animation != null && durationBase + animation.getDuration() < elapsedTime) {
			durationBase += animation.getDuration();
			i++;
			animation = animations.get(i);
		}
		
		if (animation != currentAnimation) {
			if (animation != null) {
				animation.start();
			}
			if (currentAnimation != null) {
				currentAnimation.onFinished(parent);
			}
			currentAnimation = animation;
		}
		
		if (currentAnimation != null) {
			currentAnimation.update(timeDelta, parent);
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		for (Animation animation : animations) {
			animation.reset();
		}
	}
	
	@Override
	protected void onFillBefore(GameEntity parent) {
		if (animations.isEmpty() == false) {
			animations.get(0).onFillBefore(parent);
		}
	}
	
	@Override
	protected void onFillAfter(GameEntity parent) {
		if (animations.isEmpty() == false) {
			animations.get(animations.size() - 1).onFillAfter(parent);
		}
	}
	
	@Override
	protected void onFinished(GameEntity parent) {
		super.onFinished(parent);
		
		if (animations.isEmpty() == false) {
			animations.get(animations.size() - 1).onFinished(parent);
		}
	}
	
	private List<Animation> animations = new ArrayList<Animation>();
	private Animation currentAnimation = null;

}
