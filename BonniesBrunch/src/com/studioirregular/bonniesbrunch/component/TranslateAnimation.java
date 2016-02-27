package com.studioirregular.bonniesbrunch.component;

import com.studioirregular.bonniesbrunch.entity.GameEntity;

public class TranslateAnimation extends Animation {

//	private static final String TAG = "translate-animation";
	
	public TranslateAnimation(int zOrder, float fromX, float fromY, float toX, float toY, int duration) {
		this(zOrder, fromX, fromY, toX, toY, duration, false);
	}
	
	public TranslateAnimation(int zOrder, float dx, float dy, int duration) {
		this(zOrder, 0, 0, dx, dy, duration, true);
	}
	
	private TranslateAnimation(int zOrder, float fromX, float fromY, float toX, float toY, int duration, boolean relatively) {
		super(zOrder);
		
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = toX;
		this.toY = toY;
		this.duration = duration;
		this.relatively = relatively;
	}
	
	@Override
	public void update(long timeDelta, GameEntity parent) {
		if (onUpdate(timeDelta, parent) == false) {
			return;
		}
		
		if (relatively) {
			final float dx = (toX - fromX) * timeDelta / duration;
			final float dy = (toY - fromY) * timeDelta / duration;
			parent.relativeMove(dx, dy);
		} else {
			final float x = fromX + (toX - fromX) * elapsedTime / duration;
			final float y = fromY + (toY - fromY) * elapsedTime / duration;
			parent.move(x, y);
		}
	}
	
	@Override
	public void reset() {
		super.reset();
	}
	
	@Override
	protected void onFillBefore(GameEntity parent) {
		parent.move(fromX, fromY);
	}
	
	@Override
	protected void onFillAfter(GameEntity parent) {
		parent.move(toX, toY);
	}
	
	@Override
	protected void onFinished(GameEntity parent) {
		super.onFinished(parent);
		
		parent.move(toX, toY);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " fromX:" + fromX + ",fromY:" + fromY + ",toX:" + toX + ",toY:" + toY + ",relatively:" + relatively;
	}
	
	private float fromX, fromY, toX, toY;
	private boolean relatively;
}
