package com.studioirregular.pattern.command;

import android.util.Log;

public class AddComponentCommand implements Command {

	private static final String TAG = "add-component-command";
	
	private Scene scene;
	private String name;
	private String entityName;
	
	public AddComponentCommand(Scene scene, String componentName, String entityName) {
		this.scene = scene;
		this.name = componentName;
		this.entityName = entityName;
	}
	
	@Override
	public void execute() {
		Log.d(TAG, "execute name:" + name + ",entityName:" + entityName);
		GameEntity entity = scene.getEntity(entityName);
		GameComponent component = new GameComponent(entity, name);
		entity.addComponent(component);
	}
	
	@Override
	public String toString() {
		return "AddComponentCommand name:" + name + ", entityName:" + entityName;
	}
	
}
