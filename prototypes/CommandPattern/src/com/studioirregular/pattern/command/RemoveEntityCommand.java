package com.studioirregular.pattern.command;

import android.util.Log;

public class RemoveEntityCommand implements Command {

	private static final String TAG = "remove-entity-command";
	
	private Scene scene;
	private String name;
	
	public RemoveEntityCommand(Scene scene, String entityName) {
		this.scene = scene;
		this.name = entityName;
	}
	
	@Override
	public void execute() {
		Log.d(TAG, "execute entityName:" + name);
		GameEntity entity = scene.removeEntity(name);
	}
	
	@Override
	public String toString() {
		return "RemoveComponentCommand name:" + name;
	}
}
