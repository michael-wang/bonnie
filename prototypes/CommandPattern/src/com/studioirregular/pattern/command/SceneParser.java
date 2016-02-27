package com.studioirregular.pattern.command;

import android.util.Log;

// Client: generate commands, add to Scene.
public class SceneParser {

	private static final String TAG = "scene-parser";
	
	private Scene scene;
	
	public void parse(Scene scene) {
		Log.d(TAG, "parse");
		
		this.scene = scene;
		
		genNode1();
		genNode2();
	}
	
	private void genNode1() {
		Log.d(TAG, "genNode1");
		
		final String ENTITY_NAME = "story_p1";
		AddEntityCommand addEntity = new AddEntityCommand(scene, ENTITY_NAME, 0, 0, 720, 480);
		AddComponentCommand addRender = new AddComponentCommand(scene, "render", ENTITY_NAME);
		AddComponentCommand addButton = new AddComponentCommand(scene, "button", ENTITY_NAME);
		
		SceneNode node = new SceneNode("node_1");
		node.addCommand(addEntity);
		node.addCommand(addRender);
		node.addCommand(addButton);
		
		NextNodeCommand nextNode = new NextNodeCommand(scene);
		node.addEventCommandPair(new ButtonEvent(ButtonEvent.BUTTON_CLICKED, ENTITY_NAME), nextNode);
		
		scene.addSceneNode(node);
	}
	
	private void genNode2() {
		Log.d(TAG, "genNode2");
		
		final String ENTITY_NAME = "story_p2";
		
		RemoveEntityCommand removeEntity = new RemoveEntityCommand(scene, "story_p2");
		
		AddEntityCommand addEntity = new AddEntityCommand(scene, ENTITY_NAME, 0, 0, 720, 480);
		AddComponentCommand addRender = new AddComponentCommand(scene, "render", ENTITY_NAME);
		AddComponentCommand addAnimation = new AddComponentCommand(scene, "animation", ENTITY_NAME);
		
		SceneNode node = new SceneNode("node_2");
		node.addCommand(removeEntity);
		node.addCommand(addEntity);
		node.addCommand(addRender);
		node.addCommand(addAnimation);
		
		RemoveComponentCommand removeAnimation = new RemoveComponentCommand(scene, ENTITY_NAME, "animation");
		node.addEventCommandPair(new ButtonEvent(ButtonEvent.BUTTON_CLICKED, ENTITY_NAME), removeAnimation);
		
		scene.addSceneNode(node);
	}
}
