package com.studioirregular.bonniesbrunch.entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.graphics.RectF;
import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.EventMap;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.InputSystem.TouchEvent;
import com.studioirregular.bonniesbrunch.InputSystem.Touchable;
import com.studioirregular.bonniesbrunch.base.ObjectBase;
import com.studioirregular.bonniesbrunch.base.ObjectManager;
import com.studioirregular.bonniesbrunch.component.GameComponent;
import com.studioirregular.bonniesbrunch.component.RenderComponent;

/*
 * Communication between GameEntity should use game event.
 * (unless absolutely sure no side effect induced)
 */
public abstract class GameEntity extends ObjectManager implements Touchable {

	private static final String TAG = "game-entity";
	
	public static final int MIN_GAME_COMPONENT_Z_ORDER = 0x10;
	public static final int MIN_GAME_ENTITY_Z_ORDER = 0x80;
	public RectF box = new RectF();
	public float scale = 1.0f;
	public boolean visible = true;
	public boolean visibleStateChanged = false;
	protected boolean disable = false;
	
	// larger value mean closer to user
	public int zOrder;
	
	public GameEntity(int zOrder) {
		super();
		this.zOrder = zOrder;
		
		if (Config.DEBUG_DRAW_ENTITY_REGION) {
			regionColor = new float[4];
		}
	}
	
	private static GameObjectComparator sComparator = null;
	static {
		sComparator = new GameObjectComparator();
	}
	
	@Override
	protected Comparator<ObjectBase> getComparator() {
		return sComparator;
	}
	
	protected static class GameObjectComparator implements Comparator<ObjectBase> {

		@Override
		public int compare(ObjectBase lhs, ObjectBase rhs) {
			if (lhs instanceof GameComponent && rhs instanceof GameComponent) {
				GameComponent lComp = (GameComponent)lhs;
				GameComponent rComp = (GameComponent)rhs;
				return lComp.zOrder - rComp.zOrder;
			} else if (lhs instanceof GameComponent && rhs instanceof GameEntity) {
				return -1;
			} else if (lhs instanceof GameEntity && rhs instanceof GameComponent) {
				return 1;
			} else if (lhs instanceof GameEntity && rhs instanceof GameEntity){
				GameEntity lEntity = (GameEntity)lhs;
				GameEntity rEntity = (GameEntity)rhs;
				return lEntity.zOrder - rEntity.zOrder;
			} else {
				Log.w(TAG, "GameObjectComparator only GameEntity and GameComponent is comparable! lhs:" + lhs + ",rhs:" + rhs);
				return 0;
			}
		}
		
	}
	
	@Override
	public void reset() {
		super.reset();
		
		visible = true;
		visibleStateChanged = false;
		disable = false;
		gameEvents.clear();
		eventMap = null;
	}
	
	public void setup(float x, float y, float width, float height) {
		if (Config.DEBUG_LOG) Log.d(TAG, "setup x:" + x + ",y:" + y + ",width:" + width + ",height:" + height);
		
		box.set(x, y, x + width, y + height);
		
		if (Config.DEBUG_DRAW_ENTITY_REGION) {
			renderRegion = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER - 1);
			
			getRegionColor(regionColor);
			renderRegion.setup(width, height);
			renderRegion.setup(regionColor);
			
			add(renderRegion);
		}
	}
	
	public void drawEntityRegion(float r, float g, float b, float alpha) {
		renderRegion = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER + 100);
		renderRegion.setup(box.width(), box.height());
		float[] color = new float[] {r, g, b, alpha};
		renderRegion.setup(color);
		add(renderRegion);
	}
	
	public float getX() {
		return box.left;
	}
	
	public float getY() {
		return box.top;
	}
	
	public void show() {
		if (Config.DEBUG_LOG) Log.d(TAG, getClass().getSimpleName() + ":show");
		visible = true;
		visibleStateChanged = true;
	}
	
	public void hide() {
		if (Config.DEBUG_LOG) Log.d(TAG, getClass().getSimpleName() + ":hide");
		visible = false;
		visibleStateChanged = true;
	}
	
	public void setDisable(boolean disable) {
		if (Config.DEBUG_LOG) Log.d(TAG, getClass().getSimpleName() + ":setDisable disable:" + disable);
		this.disable = disable;
		for (int i = 0; i < objects.size(); i++) {
			ObjectBase obj = objects.get(i);
			if (obj instanceof GameEntity) {
				((GameEntity)obj).setDisable(disable);
			}
		}
	}
	
	private void propagateVisibleState() {
		// ropagate visible state
		final int count = objects.size();
		for (int i = 0; i < count; i++) {
			ObjectBase obj = objects.get(i);
			if (obj instanceof GameEntity) {
				if (visible) {
					((GameEntity)obj).show();
				} else {
					((GameEntity)obj).hide();
				}
			}
		}
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public boolean isDisabled() {
		return disable;
	}
	
	public boolean hitDetection(float x, float y) {
		return box.contains(x, y);
	}
	
	public void move(float x, float y) {
		final float dx = x - box.left;
		final float dy = y - box.top;
		box.offset(dx, dy);
		
		moveChildren(dx, dy);
	}
	
	public void relativeMove(float dx, float dy) {
		box.offset(dx, dy);
		moveChildren(dx, dy);
	}
	
	protected void moveChildren(float dx, float dy) {
		for (int i = 0; i < objects.size(); i++) {
			ObjectBase obj = objects.get(i);
			if (obj instanceof GameEntity) {
				((GameEntity)obj).relativeMove(dx, dy);
			}
		}
	}
	
	public void scale(float scale) {
		this.scale = scale;
	}
	
	// NOTICE: for child component only!
	// Communication between entity should schedule event to event system,
	// to prevent change gameEvents while process them. 
	public void send(GameEvent event) {
		gameEvents.add(event);
	}
	
	@Override
	protected void commitUpdate() {
		super.commitUpdate();
		
		if (visibleStateChanged) {
			propagateVisibleState();
			visibleStateChanged = false;
		}
	}
	
	@Override
	public void update(long timeDelta, ObjectBase parent) {
		if (gameEvents.isEmpty() == false) {
			for (GameEvent event : gameEvents) {
				handleGameEvent(event);
			}
			gameEvents.clear();
		}
		
		super.update(timeDelta, parent);
	}
	
	@Override
	public boolean dispatch(TouchEvent event, GameEntity parent) {
		if (Config.DEBUG_LOG) Log.w(TAG, getClass().getSimpleName() + " dispatch touch event:" + event);
		if (!visible) {
			return false;
		}
		
		final int count = objects.size();
		for (int i = count - 1; i >= 0; i--) {
			ObjectBase obj = objects.get(i);
			if (obj instanceof Touchable) {
				if (((Touchable)obj).dispatch(event, this)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean dispatch(GameEvent event, GameEntity parent) {
		if (Config.DEBUG_LOG) Log.w(TAG, getClass().getSimpleName() + " dispatch game event:" + event);
		
		if (wantThisEvent(event)) {
			gameEvents.add(event);
			return true;
		}
		
		final int count = objects.size();
		for (int i = count - 1; i >= 0; i--) {
			ObjectBase obj = objects.get(i);
			if (obj instanceof GameEntity) {
				if (((GameEntity)obj).dispatch(event, this)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void propagate(GameEvent event, GameEntity parent) {
		if (Config.DEBUG_LOG) Log.w(TAG, getClass().getSimpleName() + " propagate game event:" + event);
		
		if (wantThisEvent(event)) {
			send(event);
		}
		
		final int count = objects.size();
		for (int i = count - 1; i >= 0; i--) {
			ObjectBase obj = objects.get(i);
			if (obj instanceof GameEntity) {
				((GameEntity)obj).propagate(event, this);
			}
		}
	}
	
	protected abstract boolean wantThisEvent(GameEvent event);
	protected abstract void handleGameEvent(GameEvent event);
	
	protected void getRegionColor(float[] color) {
		if (color != null && color.length == 4) {
			color[0] = Config.ENTITY_REGION_COLOR_R;
			color[1] = Config.ENTITY_REGION_COLOR_G;
			color[2] = Config.ENTITY_REGION_COLOR_B;
			color[3] = Config.ENTITY_REGION_COLOR_A;
		} else {
			Log.w(TAG, "getRegionColor invalid color buffer:" + color);
		}
	}
	protected RenderComponent renderRegion;
	protected float[] regionColor;
	
	protected List<GameEvent> gameEvents = new ArrayList<GameEvent>();
	protected EventMap eventMap;
	
	public void installEventMap(GameEvent receive, GameEvent emit) {
		if (Config.DEBUG_LOG) Log.d(TAG, "installEventMap receive:" + receive + ",emit:" + emit);
		
		if (eventMap == null) {
			eventMap = new EventMap(receive, emit);
		} else {
			eventMap.key = receive;
			eventMap.value = emit;
		}
	}
	
	// derived class can decide to handle event map or not.
	// if want to support, should call handleEventMap on handle game event.
	protected void handleEventMap(GameEvent receive) {
		if (Config.DEBUG_LOG) Log.d(TAG, "handleEventMap receive:" + receive + ", map:" + eventMap);
		
		if (eventMap != null && eventMap.key.equals(receive)) {
			final GameEvent emit = eventMap.value;
			GameEventSystem.scheduleEvent(emit.what, emit.arg1, emit.obj);
		}
	}
}
