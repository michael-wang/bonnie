package com.studioirregular.pattern.command;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

import android.util.Log;

public class Scene {

	private static final String TAG = "scene";
	
	private Map<String, GameEntity> entityMap = new HashMap<String, GameEntity>();
	private LinkedList<SceneNode> nodeList = new LinkedList<SceneNode>();
	private ListIterator<SceneNode> nodeIterator;
	private SceneNode node = null;
	
	public SceneNode getCurrentNode() {
		return node;
	}
	
	// Invoker: setCommand
	public void addSceneNode(SceneNode node) {
		Log.d(TAG, "addSceneNode node");
		nodeList.addLast(node);
	}
	
	// Scene interface
	public void start() {
		Log.d(TAG, "start");
		nodeIterator = nodeList.listIterator();
		node = nodeIterator.next();
		node.execute();
	}
	
	public void nextNode() {
		Log.d(TAG, "nextNode");
		if (nodeIterator.hasNext()) {
			node = nodeIterator.next();
			node.execute();
		}
	}
	
	// Receiver
	public void addEntity(GameEntity entity) {
		Log.d(TAG, "addEntity entity:" + entity);
		entityMap.put(entity.getName(), entity);
	}
	
	public GameEntity getEntity(String name) {
		return entityMap.get(name);
	}
	
	public GameEntity removeEntity(String name) {
		Log.d(TAG, "removeEntity name:" + name);
		return entityMap.remove(name);
	}
	
	// Event
	public void onEvent(GameEvent event) {
		Log.d(TAG, "onEvent event:" + event);
		Command command = node.getMappedCommand(event);
		if (command != null) {
			command.execute();
		}
	}
	
}
