package com.studioirregular.pattern.command;

import java.util.HashMap;
import java.util.Map;

import android.graphics.RectF;
import android.util.Log;

public class GameEntity {

	private static final String TAG = "game-entity";
	
	private String name;
	private RectF box = new RectF();
	private Map<String, GameComponent> componentMap = new HashMap<String, GameComponent>();
	
	public GameEntity(String name, float left, float top, float right, float bottom) {
		this.name = name;
		box.set(left, top, right, bottom);
	}
	
	public void addComponent(GameComponent component) {
		Log.d(TAG, name + " addComponent component:" + component);
		componentMap.put(component.getName(), component);
	}
	
	public GameComponent removeComponent(String componentName) {
		Log.d(TAG, name + " removeComponent component:" + componentName);
		return componentMap.remove(componentName);
	}
	
	@Override
	public String toString() {
		return "GameEntity name:" + name + ",box:" + box + ",#components:" + componentMap.size();
	}
	
	public String getName() {
		return name;
	}
	
}
