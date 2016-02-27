package com.studioirregular.bonniesbrunch.component;

import java.util.ArrayList;
import java.util.List;

import android.graphics.PointF;
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

public class Draggable extends GameComponent implements Touchable {

	private static final String TAG = "draggable";
	
	public Draggable(int zOrder) {
		super(zOrder);
	}
	
	public void setTouchArea(RectF parentBox, float offsetLeft, float offsetTop, float offsetRight, float offsetBottom) {
		if (Config.DEBUG_LOG)
			Log.d(TAG, "setTouchArea parent box:" + parentBox + ",offsetLeft:"
					+ offsetLeft + ",offsetTop:" + offsetTop + ",offsetRight:"
					+ offsetRight + ",offsetBottom:" + offsetBottom);
		
		dxParent = offsetLeft;
		dyParent = offsetTop;
		
		box.set(parentBox.left + offsetLeft, parentBox.top + offsetTop,
				parentBox.right + offsetRight,
				parentBox.bottom + offsetBottom);
	}
	
	@Override
	public boolean dispatch(TouchEvent event, GameEntity parent) {
		if (parent.isDisabled()) {
			return false;
		}
		
		boolean wantThisEvent = false;
		if (box.contains(event.x, event.y)) {
			wantThisEvent = true;
		} else if (afterTouchDown) {
			wantThisEvent = true;
		}
		
		if (wantThisEvent) {
			if (event.type == TouchEvent.DOWN) {
				afterTouchDown = true;
			}
			touchEvents.add(event);
		}
		
		//if (Config.DEBUG_LOG) Log.d(TAG, "dispatch box:" + box + ", event:" + event + " returns:" + wantThisEvent);
		
		return wantThisEvent;
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
			if (Config.DEBUG_LOG) Log.d(TAG, "parent moved! dx:" + dx + ",dy:" + dy);
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
	
	// TODO: start dragging after moving beyond threshold distance.
	protected void handleTouchEvent(TouchEvent event, GameEntity parent) {
		if (Config.DEBUG_LOG) Log.d(TAG, "handleTouchEvent event:" + event);
		
		if (event.type == TouchEvent.MOVE) {
			if (startDragging) {
				parent.move(parentOriginalPosition.x + (event.x - downPoint.x), parentOriginalPosition.y +  (event.y - downPoint.y));
			}
		} else if (event.type == TouchEvent.DOWN) {
			startDragging = true;
			downPoint.set(event.x, event.y);
			parentOriginalPosition.set(parent.getX(), parent.getY());
			
			parent.send(GameEventSystem.getInstance().obtain(GameEvent.DRAG_BEGIN));
		} else if (event.type == TouchEvent.UP) {
			if (afterTouchDown) {
				afterTouchDown = false;
			}
			if (startDragging) {
				startDragging = false;
				
				parent.send(GameEventSystem.getInstance().obtain(GameEvent.DRAG_END));
			}
		}
	}
	
	@Override
	public void reset() {
		afterTouchDown = false;
		downPoint.set(0, 0);
		parentOriginalPosition.set(0, 0);
		touchEvents.clear();
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
	private PointF downPoint = new PointF();
	private PointF parentOriginalPosition = new PointF();
	private boolean startDragging = false;
	private List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
	private RenderObject renderTouchRegion;

}
