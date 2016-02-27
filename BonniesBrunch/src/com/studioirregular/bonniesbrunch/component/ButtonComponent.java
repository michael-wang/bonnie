package com.studioirregular.bonniesbrunch.component;

import java.util.ArrayList;
import java.util.List;

import android.graphics.RectF;
import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.ContextParameters;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.InputSystem.TouchEvent;
import com.studioirregular.bonniesbrunch.InputSystem.Touchable;
import com.studioirregular.bonniesbrunch.RenderSystem;
import com.studioirregular.bonniesbrunch.RenderSystem.RenderObject;
import com.studioirregular.bonniesbrunch.entity.GameEntity;

public class ButtonComponent extends GameComponent implements Touchable {

	private static final String TAG = "button-component";
	
	public ButtonComponent(int zOrder) {
		super(zOrder);
	}
	
	public void setup(float x, float y, float width, float height) {
		this.setup(x, y, width, height, x, y);
	}
	
	// TODO: refactor parentX, parentY
	public void setup(float x, float y, float width, float height, float parentX, float parentY) {
		if (Config.DEBUG_LOG) Log.d(TAG, "setup x:" + x + ",y:" + y + ",w:" + width + ",h:" + height + ",parentX:" + parentX + ",parentY:" + parentY);
		
		dxParent = x - parentX;
		dyParent = y - parentY;
		
		box.set(x, y, x + width, y + height);
	}
	
	@Override
	public boolean dispatch(TouchEvent event, GameEntity parent) {
		if (Config.DEBUG_LOG) Log.d(TAG, "dispatch:" + event);
		
		if (parent.isDisabled()) {
			return false;
		}
		
		boolean wantThisEvent = false;
		if (box.contains(event.x, event.y)) {
			if (TouchEvent.DOWN == event.type || afterTouchDown) {
				wantThisEvent = true;
			}
		} else if (afterTouchDown) {
			wantThisEvent = true;
		}
		
		if (wantThisEvent) {
			if (event.type == TouchEvent.DOWN) {
				afterTouchDown = true;
			}
			touchEvents.add(event);
			return true;
		}
		return false;
	}
	
	@Override
	public void update(long timeDelta, GameEntity parent) {
		if (touchEvents.isEmpty() == false) {
			for (TouchEvent event : touchEvents) {
				handleTouchEvent(event, parent);
			}
			touchEvents.clear();
		}
		
		// update box
		final float dx = parent.getX() + dxParent - box.left;
		final float dy = parent.getY() + dyParent - box.top;
		if (dx != 0 || dy != 0) {
			if (Config.DEBUG_LOG) Log.w(TAG, "parent moved! dx:" + dx + ",dy:" + dy);
			box.offset(dx, dy);
		}
		
		if (ContextParameters.getInstance().debugDrawTouchArea && parent.isVisible()) {
			if (renderTouchRegion == null) {
				debugSetupDrawTouchArea();
			}
			renderTouchRegion.setPosition(box.left, box.top);
			RenderSystem.getInstance().scheduleRenderObject(renderTouchRegion);
		}
	}
	
	@Override
	public void reset() {
		afterTouchDown = false;
		touchEvents.clear();
	}
	
	protected void handleTouchEvent(TouchEvent event, GameEntity parent) {
		if (Config.DEBUG_LOG) Log.d(TAG, "handleTouchEvent:" + event);
		
		if (event.type == TouchEvent.DOWN) {
			if (box.contains(event.x, event.y)) {
				afterTouchDown = true;
				onButtonDown(parent);
			}
		} else if (afterTouchDown && event.type == TouchEvent.MOVE) {
			if (box.contains(event.x, event.y) == false) {
				afterTouchDown = false;
				onButtonCancel(parent);
			}
		} else if (afterTouchDown && event.type == TouchEvent.UP) {
			afterTouchDown = false;
			onButtonUp(parent);
		}
	}
	
	private void onButtonDown(GameEntity parent) {
		if (Config.DEBUG_LOG) Log.d(TAG, "onButtonDown");
		
		parent.send(GameEventSystem.getInstance().obtain(GameEvent.BUTTON_DOWN));
		if (ContextParameters.getInstance().debugDrawTouchArea) {
			renderTouchRegion.setColor(Config.TOUCH_REGION_COLOR_DOWN_R,
					Config.TOUCH_REGION_COLOR_DOWN_G, Config.TOUCH_REGION_COLOR_DOWN_B,
					Config.TOUCH_REGION_COLOR_DOWN_A);
		}
	}
	
	private void onButtonUp(GameEntity parent) {
		if (Config.DEBUG_LOG) Log.d(TAG, "onButtonUp");
		
		parent.send(GameEventSystem.getInstance().obtain(GameEvent.BUTTON_UP));
		if (ContextParameters.getInstance().debugDrawTouchArea) {
			renderTouchRegion.setColor(Config.TOUCH_REGION_COLOR_R,
					Config.TOUCH_REGION_COLOR_G, Config.TOUCH_REGION_COLOR_B,
					Config.TOUCH_REGION_COLOR_A);
		}
	}
	
	private void onButtonCancel(GameEntity parent) {
		if (Config.DEBUG_LOG) Log.d(TAG, "onButtonCancel");
		
		parent.send(GameEventSystem.getInstance().obtain(GameEvent.BUTTON_CANCEL));
		if (ContextParameters.getInstance().debugDrawTouchArea) {
			renderTouchRegion.setColor(Config.TOUCH_REGION_COLOR_R,
					Config.TOUCH_REGION_COLOR_G, Config.TOUCH_REGION_COLOR_B,
					Config.TOUCH_REGION_COLOR_A);
		}
	}
	
	private void debugSetupDrawTouchArea() {
		renderTouchRegion = new RenderObject(box.width(), box.height());
		renderTouchRegion.setColor(Config.TOUCH_REGION_COLOR_R,
				Config.TOUCH_REGION_COLOR_G, Config.TOUCH_REGION_COLOR_B,
				Config.TOUCH_REGION_COLOR_A);
	}
	
	private float dxParent, dyParent;
	private RectF box = new RectF();
	private boolean afterTouchDown = false;
	private List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
	private RenderObject renderTouchRegion;

}
