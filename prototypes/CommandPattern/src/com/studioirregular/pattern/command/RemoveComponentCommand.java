package com.studioirregular.pattern.command;

import android.util.Log;

public class RemoveComponentCommand implements Command {

	private static final String TAG = "remove-component-command";
	
	private Scene scene;
	private String entityName;
	private String componentName;
	
	public RemoveComponentCommand(Scene scene, String entityName, String componentName) {
		this.scene = scene;
		this.entityName = entityName;
		this.componentName = componentName;
	}
	
	@Override
	public void execute() {
		Log.d(TAG, "execute entityName:" + entityName + ", componentName:" + componentName);
		GameEntity entity = scene.getEntity(entityName);
		GameComponent component = entity.removeComponent(componentName);
	}
	
	@Override
	public String toString() {
		return "RemoveComponentCommand entityName:" + entityName + ", componentName:" + componentName;
	}
	
}
