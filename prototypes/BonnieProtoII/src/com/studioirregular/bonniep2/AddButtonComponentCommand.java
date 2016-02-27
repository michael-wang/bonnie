package com.studioirregular.bonniep2;

import android.util.Log;

public class AddButtonComponentCommand implements Command {

	private static final String TAG = "add-component-command";
	
	private Scene scene;
	private String entityId;
	private String componentId;
	private float left, top, width, height;
	
	public AddButtonComponentCommand(Scene scene, String entityName,
			String componentName, float left, float top, float width,
			float height) {
		this.scene = scene;
		this.componentId = componentName;
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		this.entityId = entityName;
	}
	
	@Override
	public void execute() {
//		Log.d(TAG, "execute componentId:" + componentId + ",entityId:" + entityId);
		
		Entity entity = scene.getEntity(entityId);
		if (entity == null) {
			Log.e(TAG, "execute cannot find entity:" + entityId);
			return;
		}
		
		EventHost host = null;
		if (entity instanceof EventHost) {
			host = ((EventHost) entity);
		} else {
			Log.w(TAG, "execute: button component need EventHost");
		}
		
		ButtonComponent button = new ButtonComponent(componentId, host, left, top, width, height);
		entity.add(button);
	}
	
	@Override
	public String toString() {
		return "AddButtonComponentCommand componentId:" + componentId + ", entityId:"
				+ entityId;
	}
	
}
