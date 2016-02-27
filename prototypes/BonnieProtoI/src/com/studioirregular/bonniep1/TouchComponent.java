package com.studioirregular.bonniep1;

import android.graphics.RectF;
import android.util.Log;

public class TouchComponent implements UserInput {

	private static final String TAG = "component-touch";
	private static final boolean DO_LOG = false;
	
	private GameEntity entity;
	private RectF box;
	private Integer state = GameEntity.STATE_TOUCH_UP;
	
	public TouchComponent(GameEntity entity, float left, float top, float right, float bottom) {
		this.entity = entity;
		box = new RectF(left, top, right, bottom);
	}
	
	public void offset(float dx, float dy) {
		box.offset(dx, dy);
	}
	
	@Override
	public boolean onDown(float x, float y) {
		if (box.contains(x, y)) {
			state = GameEntity.STATE_TOUCH_DOWN;
			entity.sendEvent(GameEntity.EVENT_STATE_CHANGE, GameEntity.STATE_TOUCH_DOWN);
			return true;
		}
		return false;
	}

	@Override
	public boolean onScroll(float x, float y, float dx, float dy) {
		if (box.contains(x, y) && state == GameEntity.STATE_TOUCH_DOWN) {
			state = GameEntity.STATE_TOUCH_MOVE;
			entity.sendEvent(GameEntity.EVENT_STATE_CHANGE, GameEntity.STATE_TOUCH_MOVE);
			return true;
		} else {
			if (state != GameEntity.STATE_TOUCH_UP) {
				if (DO_LOG) Log.e(TAG, "onScroll out of BOX! state:" + state + ",box:" + box);
				state = GameEntity.STATE_TOUCH_UP;
				entity.sendEvent(GameEntity.EVENT_STATE_CHANGE, GameEntity.STATE_TOUCH_CANCEL);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onTap(float x, float y) {
		return box.contains(x, y);
	}
	
	@Override
	public boolean onUp(float x, float y) {
		if (box.contains(x, y)) {
			state = GameEntity.STATE_TOUCH_UP;
			entity.sendEvent(GameEntity.EVENT_STATE_CHANGE, GameEntity.STATE_TOUCH_UP);
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "TouchComponent box:" + box;
	}
	
}
