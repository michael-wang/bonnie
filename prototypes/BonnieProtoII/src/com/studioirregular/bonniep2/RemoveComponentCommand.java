package com.studioirregular.bonniep2;

import android.util.Log;

public class RemoveComponentCommand implements Command {

	private static final String TAG = "remove-component-command";
	
	private Scene scene;
	private String entityId;
	
	private Entity entity;
	private String componentId;
	
	public RemoveComponentCommand(Scene scene, String entityId, String componentId) {
		this.scene = scene;
		this.entityId = entityId;
		this.componentId = componentId;
	}
	
	public RemoveComponentCommand(Entity entity, String componentId) {
		this.entity = entity;
		this.componentId = componentId;
	}
	
	@Override
	public void execute() {
//		Log.d(TAG, "execute entityId:" + entityId + ", componentId:" + componentId);
		
		if (entity == null) {
			entity = scene.getEntity(entityId);
			if (entity == null) {
				Log.e(TAG, "execute: cannot find entity:" + entityId);
				return;
			}
		}
		
		((BasicEntity)entity).removeComponentInternal(componentId);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "entity:" + entity + " entityId:" + entityId + ", componentId:" + componentId;
	}
	
}
