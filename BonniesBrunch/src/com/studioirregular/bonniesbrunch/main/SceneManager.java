package com.studioirregular.bonniesbrunch.main;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelNumber;
import com.studioirregular.bonniesbrunch.component.GameComponent;
import com.studioirregular.bonniesbrunch.entity.GameEntity;

/*
 * Command executor.
 */
public class SceneManager {

	private static final String TAG = "scene-manager";
	
	public SceneManager(GameEntityRoot root) {
		this.root = root;
	}
	
	public void loadLevelStart() {
		SceneLoader loader = new SceneLoader_LevelStart(this, root);
		loader.load();
	}
	
	public void loadLevelRunning() {
		SceneLoader loader = new SceneLoader_LevelRunning(this, root);
		loader.load();
	}
	
	public void loadTutorial(LevelNumber levelNumber) {
		SceneLoader loader = null;
		
		if (1 == levelNumber.major) {
			switch (levelNumber.minor) {
			case 1:
				loader = new SceneLoaderTutorial1_1(this, root);
				break;
			case 2:
				loader = new SceneLoaderTutorial1_2(this, root);
				break;
			case 5:
				loader = new SceneLoaderTutorial1_5(this, root);
				break;
			case 8:
				loader = new SceneLoaderTutorial1_8(this, root);
				break;
			}
		} else if (2 == levelNumber.major) {
			if (1 == levelNumber.minor) {
				loader = new SceneLoaderTutorial_2_1(this, root);
			} else if (4 == levelNumber.minor) {
				loader = new SceneLoaderTutorial_2_4(this, root);
			}
		} else if (3 == levelNumber.major && 1 == levelNumber.minor) {
			loader = new SceneLoaderTutorial_3_1(this, root);
		}
		
		if (loader != null) {
			loader.load();
		} else {
			Log.e(TAG, "no toturial for level:" + levelNumber);
		}
	}
	
	public void peekGameEvent(GameEvent event) {
		if (node != null) {
			node.handleEventMap(event);
		}
	}
	
	public void clear() {
		nodes.clear();
	}
	
	public void add(SceneNode node) {
		nodes.add(node);
	}
	
	public void next() {
		if (Config.DEBUG_LOG) Log.d(TAG, "next");
		
		if (nodes.isEmpty()) {
			node = null;
			onSceneEnd();
			return;
		}
		
		node = nodes.removeFirst();
		if (Config.DEBUG_LOG) Log.d(TAG, "node:" + node);
		node.run();
	}
	
	public void add(String entityName, GameEntity entity) {
		if (Config.DEBUG_LOG) Log.d(TAG, "add entityName:" + entityName + ",entity:" + entity);
		
		if (entityMap.containsKey(entityName)) {
			if (Config.DEBUG_LOG) Log.w(TAG, "add entityName existed:" + entityName);
		}
		entityMap.put(entityName, entity);
		root.add(entity);
	}
	
	public void remove(String entityName) {
		if (entityMap.containsKey(entityName)) {
			root.remove(entityMap.get(entityName));
			entityMap.remove(entityName);
		} else {
			if (Config.DEBUG_LOG) {
				Log.w(TAG, "remove cannot find entityName:" + entityName);
				Set<String> keySet = entityMap.keySet();
				for (String key : keySet) {
					Log.w(TAG, key);
				}
			}
		}
	}
	
	public boolean addComponent(String entityName, GameComponent component) {
		if (Config.DEBUG_LOG) Log.d(TAG, "addComponent entityName:" + entityName + ",component:" + component);
		
		if (entityMap.containsKey(entityName) == false) {
			Log.w(TAG, "addComponent cannot find entity by name:" + entityName);
			return false;
		}
		
		entityMap.get(entityName).add(component);
		return true;
	}
	
	public void jumpToEnd() {
		if (Config.DEBUG_LOG) Log.d(TAG, "jumpToEnd");
		
		clear();
		onSceneEnd();
	}
	
	private void onSceneEnd() {
		if (Config.DEBUG_LOG) Log.d(TAG, "onSceneEnd");
		
		Collection<GameEntity> entities = entityMap.values();
		for (GameEntity entity : entities) {
			root.remove(entity);
		}
		entityMap.clear();
		
		GameEventSystem.scheduleEvent(GameEvent.SCENE_MANAGER_SCENE_END);
	}
	
	private GameEntityRoot root;
	private LinkedList<SceneNode> nodes = new LinkedList<SceneNode>();
	
	private SceneNode node;
	private Map<String, GameEntity> entityMap = new HashMap<String, GameEntity>();
}
