package com.studioirregular.bonniesbrunch.entity;

import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.base.ObjectBase;
import com.studioirregular.bonniesbrunch.component.RenderComponent;

public class SimpleEntity extends GameEntity {

//	private static final String TAG = "simple-entity";
	
	public SimpleEntity(int zOrder) {
		super(zOrder);
	}
	
	public void setup(float x, float y, float width, float height, String textPartition) {
		super.setup(x, y, width, height);
		
		if (textPartition != null && textPartition.length() > 0) {
			RenderComponent bg = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
			bg.setup(width, height);
			bg.setup(textPartition);
			add(bg);
		}
	}
	
	public void removeSelfOnEvent(int eventWhat) {
		removingEventWhat = eventWhat;
	}
	
	@Override
	public void update(long timeDelta, ObjectBase parent) {
		super.update(timeDelta, parent);
		
		if (scheduleRemove) {
			scheduleRemove = false;
			if (parent instanceof GameEntity) {
				((GameEntity)parent).remove(this);
			}
		}
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		if (eventMap != null && eventMap.key.equals(event)) {
			return true;
		}
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		if (removingEventWhat == event.what) {
			scheduleRemove = true;
		}
		super.handleEventMap(event);
	}
	
	private int removingEventWhat = 0;
	private boolean scheduleRemove = false;

}
