package com.studioirregular.bonniep2;

import android.content.Context;

import com.studioirregular.bonnie.foodsystem.FoodSystem.Food;
import com.studioirregular.bonnie.foodsystem.FoodSystem.FoodCombination;
import com.studioirregular.bonnie.levelsystem.Level;


public class SceneParser1_1 implements SceneParser {

//	private static final String TAG = "scene-parser";
	
	private SceneBase scene;
	
	public SceneParser1_1(int levelConfigXml)  {
		
	}
	
	@Override
	public boolean parse(Context context, SceneBase scene) {
		this.scene = scene;
		genNode1();
		genNode2();
		genOpeningEP1();
		SceneNode node = genStage();
		genTutorialText(node);
		genBonnieSpeak_1_1_01();
		genBonnieSpeak_1_1_02();
		genCustomerJogginGirl();
		genHint_tapOnBegal();
		genBonnieSpeak_1_1_03();
		genDragToCustomer();
		genBonnie_wellDone();
		
		return true;
	}
	
	private void genNode1() {
		final String ENTITY_ID = "story_p1";
		
		AddEntityCommand addEntity = new AddEntityCommand(scene, ENTITY_ID);
		// setup event to command mapping
		Event buttonUp = new ComponentEvent(ComponentEvent.BUTTON_UP, "button");
		Event entityClicked = new EntityEvent(EntityEvent.USER_CLICK, ENTITY_ID);
		Command sendClicked = new SendEventCommand(entityClicked, scene);
		addEntity.addCommandTrigger(buttonUp, sendClicked);
		
		AddRenderComponentCommand addRender = new AddRenderComponentCommand(scene, ENTITY_ID, "render", 0, 0, 720, 480, "story_start_p1");
		AddButtonComponentCommand addButton = new AddButtonComponentCommand(scene, ENTITY_ID, "button", 0, 0, 720, 480);
		
		SceneNode node = new SceneNode("node_1");
		node.addCommand(addEntity);
		node.addCommand(addRender);
		node.addCommand(addButton);
		
		NextNodeCommand nextNode = new NextNodeCommand(scene);
		node.addEventCommandPair(new EntityEvent(EntityEvent.USER_CLICK, ENTITY_ID), nextNode);
		
		scene.addSceneNode(node);
	}
	
	private void genNode2() {
		final String ENTITY_ID = "story_p2";
		
		RemoveEntityCommand removeEntity = new RemoveEntityCommand(scene, "story_p1");
		
		AddEntityCommand addEntity = new AddEntityCommand(scene, ENTITY_ID);
		// setup event to command mapping
		Event buttonUp = new ComponentEvent(ComponentEvent.BUTTON_UP, "button");
		Event entityClicked = new EntityEvent(EntityEvent.USER_CLICK, ENTITY_ID);
		Command sendClicked = new SendEventCommand(entityClicked, scene);
		addEntity.addCommandTrigger(buttonUp, sendClicked);
		
		AddRenderComponentCommand addRender = new AddRenderComponentCommand(scene, ENTITY_ID, "render", 0, 0, 720, 480, "story_start_p2");
		AddButtonComponentCommand addButton = new AddButtonComponentCommand(scene, ENTITY_ID, "button", 0, 0, 720, 480);
		
		SceneNode node = new SceneNode("node_2");
		node.addCommand(removeEntity);
		node.addCommand(addEntity);
		node.addCommand(addRender);
		node.addCommand(addButton);
		
		NextNodeCommand nextNode = new NextNodeCommand(scene);
		node.addEventCommandPair(new EntityEvent(EntityEvent.USER_CLICK, ENTITY_ID), nextNode);
		
		scene.addSceneNode(node);
	}
	
	private void genOpeningEP1() {
		SceneNode node = new SceneNode("opening_ep1");
		
		// remove previous image
		node.addCommand(new RemoveEntityCommand(scene, "story_p2"));
		
		AddEntityCommand addEntity = new AddEntityCommand(scene, "opening_ep1");
		
		Event buttonUp = new ComponentEvent(ComponentEvent.BUTTON_UP, "button");
		Event entityClicked = new EntityEvent(EntityEvent.USER_CLICK, "opening_ep1");
		Command sendClicked = new SendEventCommand(entityClicked, scene);
		addEntity.addCommandTrigger(buttonUp, sendClicked);
		node.addCommand(addEntity);
		
		node.addCommand(new AddRenderComponentCommand(scene, "opening_ep1", "render", 0, 0, 720, 480, "opening_bg_ep1"));
		node.addCommand(new AddButtonComponentCommand(scene, "opening_ep1", "button", 0, 0, 720, 480));
		
		NextNodeCommand nextNode = new NextNodeCommand(scene);
		node.addEventCommandPair(new EntityEvent(EntityEvent.USER_CLICK, "opening_ep1"), nextNode);
		
		scene.addSceneNode(node);
	}
	
	private SceneNode genStage() {
		SceneNode node = new SceneNode("stage");
		
		// remove previous image
		node.addCommand(new RemoveEntityCommand(scene, "opening_ep1"));
		
		// background
		node.addCommand(new AddEntityCommand(scene, "game_bg_01"));
		node.addCommand(new AddRenderComponentCommand(scene, "game_bg_01", "render", 0, 0, 720, 260, "game_bg_01"));
		
		// table
		node.addCommand(new AddEntityCommand(scene, "game_table_bg"));
		node.addCommand(new AddRenderComponentCommand(scene, "game_table_bg", "render", 0, 250, 720, 230, "game_table_bg"));
		
		// trashcan
		node.addCommand(new AddTrashcanEntityCommand(scene, "game_table_trashcan"));
		node.addCommand(new SetEntityEnableCommand(scene, "CustomerComponent", false));
		
		// bagel
		node.addCommand(new AddFoodProducerCommand(scene, "game_table_bagel", FoodComponent.Type.Bagel));
		node.addCommand(new AddRenderComponentCommand(scene, "game_table_bagel", "render", 95, 243, 117, 158, "game_table_bagel"));
		node.addCommand(new AddButtonComponentCommand(scene, "game_table_bagel", "button", 95, 243, 117, 158));
		node.addCommand(new SetEntityEnableCommand(scene, "game_table_bagel", false));
		
		// plate
		node.addCommand(new AddFoodPlateCommand(scene, "game_table_plate_b"));
		
		return node;
	}
	
	private void genTutorialText(SceneNode node) {
		// text: tutorial
		AddEntityCommand addEntity = new AddEntityCommand(scene, "game_text_4_tutorial");
		
		Command addAnim = new AddTranslateAnimationCommand(scene, "game_text_4_tutorial", "translate", -387, 720, 185, 185, 1000L, false, true);
		addEntity.addCommandTrigger(new ComponentEvent(ComponentEvent.ENTITY_ALIVE, "game_text_4_tutorial"), addAnim);
		addEntity.addCommandTrigger(new ComponentEvent(ComponentEvent.ANIMATION_ENDED, "translate"), new NextNodeCommand(scene));
		node.addCommand(addEntity);
		
		node.addCommand(new AddRenderComponentCommand(scene, "game_text_4_tutorial", "render", -387, 185, 387, 110, "game_text_4_tutorial"));
		
		scene.addSceneNode(node);
	}
	
	private void genBonnieSpeak_1_1_01() {
		SceneNode node = new SceneNode("bonnieSpeak_1-1_01");
		
		// remove tutorial text
		node.addCommand(new RemoveEntityCommand(scene, "game_text_4_tutorial"));
		
		// dialog frame
		node.addCommand(new AddEntityCommand(scene, "tutorial_frame_big_001"));
		node.addCommand(new AddRenderComponentCommand(scene, "tutorial_frame_big_001", "render", 108, 220, 550, 196, "tutorial_frame_big_001"));
		
		// dialog text
		node.addCommand(new AddEntityCommand(scene, "tutorial_1_1_text"));
		node.addCommand(new AddRenderComponentCommand(scene, "tutorial_1_1_text", "render", 170, 240, 402, 124, "tutorial_1_1_text_001"));
		
		// ok button
		AddButtonEntityCommand addButton = new AddButtonEntityCommand(scene, "tutorial_frame_ok");
		addButton.addCommandTrigger(new ComponentEvent(ComponentEvent.BUTTON_UP, "button"), new NextNodeCommand(scene));
		node.addCommand(addButton);
		
		AddStatfulRenderComponentCommand addRender = new AddStatfulRenderComponentCommand(
				scene, "tutorial_frame_ok", "render", 583, 338, 74, 74,
				ComponentEvent.BUTTON_UP, "tutorial_frame_ok");
		addRender.addStateTexturePair(ComponentEvent.BUTTON_DOWN, "tutorial_frame_ok_pressed");
		node.addCommand(addRender);
		node.addCommand(new AddButtonComponentCommand(scene, "tutorial_frame_ok", "button", 583, 338, 74, 74));
		
		// bonnie speaks
		node.addCommand(new AddEntityCommand(scene, "tutorial_bonnie_normal"));
		AddFrameAnimationComponentCommand addAnimation = new AddFrameAnimationComponentCommand(
				scene, "tutorial_bonnie_normal", "render", 0, 282, 182, 214, true, true);
		addAnimation.addFrame("tutorial_bonnie_normal_1", 100);
		addAnimation.addFrame("tutorial_bonnie_normal_2", 100);
		addAnimation.addFrame("tutorial_bonnie_normal_1", 100);
		addAnimation.addFrame("tutorial_bonnie_normal_3", 100);
		addAnimation.addFrame("tutorial_bonnie_normal_1", 100);
		addAnimation.addFrame("tutorial_bonnie_normal_3", 100);
		addAnimation.addFrame("tutorial_bonnie_normal_1", 100);
		addAnimation.addFrame("tutorial_bonnie_normal_2", 100);
		addAnimation.addFrame("tutorial_bonnie_normal_1", 100);
		
		addAnimation.addFrame("tutorial_bonnie_normal_4", 400);
		addAnimation.addFrame("tutorial_bonnie_normal_5", 400);
		addAnimation.addFrame("tutorial_bonnie_normal_4", 400);
		addAnimation.addFrame("tutorial_bonnie_normal_5", 400);
		addAnimation.addFrame("tutorial_bonnie_normal_4", 400);
		addAnimation.addFrame("tutorial_bonnie_normal_5", 400);
		addAnimation.addFrame("tutorial_bonnie_normal_4", 400);
		addAnimation.addFrame("tutorial_bonnie_normal_5", 1400);
		
		node.addCommand(addAnimation);
		
		scene.addSceneNode(node);
	}
	
	private void genBonnieSpeak_1_1_02() {
		SceneNode node = new SceneNode("bonnieSpeak_1-1_02");
		
		// dialog text
		node.addCommand(new RemoveComponentCommand(scene, "tutorial_1_1_text", "render"));
		node.addCommand(new AddRenderComponentCommand(scene, "tutorial_1_1_text", "render", 170, 240, 402, 124, "tutorial_1_1_text_002"));
		
		// remove bonnie speak
		node.addCommand(new RemoveEntityCommand(scene, "tutorial_bonnie_normal"));
		
		// bonnie great!
		node.addCommand(new AddEntityCommand(scene, "tutorial_bonnie_great"));
		AddFrameAnimationComponentCommand addAnimation = new AddFrameAnimationComponentCommand(
				scene, "tutorial_bonnie_great", "render", 0, 282, 182, 214, true, true);
		addAnimation.addFrame("tutorial_bonnie_great_1", 200);
		addAnimation.addFrame("tutorial_bonnie_great_2", 100);
		addAnimation.addFrame("tutorial_bonnie_great_1", 100);
		addAnimation.addFrame("tutorial_bonnie_great_2", 1100);
		node.addCommand(addAnimation);
		
		scene.addSceneNode(node);
	}
	
	private void genCustomerJogginGirl() {
		SceneNode node = new SceneNode("customer_jogging_girl");
		
		// remove previous entitys
		node.addCommand(new RemoveEntityCommand(scene, "tutorial_frame_ok"));
		node.addCommand(new RemoveEntityCommand(scene, "tutorial_bonnie_great"));
		node.addCommand(new RemoveEntityCommand(scene, "tutorial_1_1_text"));
		node.addCommand(new RemoveEntityCommand(scene, "tutorial_frame_big_001"));
		
		// add jogging girl
		FoodCombination foodCombination = new FoodCombination();
		foodCombination.addFood(Food.getBagel());
		AddCustomerEntityCommand addJoggingGirl = new AddCustomerEntityCommand(
				scene, "jogging_girl", "game_bg_01",
				Level.CUSTOMER_JOGGING_GIRL, foodCombination);
		addJoggingGirl.addCommandTrigger(new EntityEvent(EntityEvent.CUSTOMER_MAKE_ORDER, "jogging_girl"), new NextNodeCommand(scene));
		node.addCommand(addJoggingGirl);
		node.addCommand(new SetTutorialModeCommand(scene, "jogging_girl", true));
		
		node.addCommand(new CustomerTakeSeatCommand(scene, "jogging_girl", 1));
		
		scene.addSceneNode(node);
	}
	
	private void genHint_tapOnBegal() {
		SceneNode node = new SceneNode("hint_tapOnBegal");
		
		// add hint text
		node.addCommand(new AddEntityCommand(scene, "tutorial_sign_tap_001"));
		node.addCommand(new AddRenderComponentCommand(scene, "tutorial_sign_tap_001", "render", 69, 90, 157, 59, "tutorial_sign_tap_001"));
		
		// add arrow
		node.addCommand(new AddEntityCommand(scene, "tutorial_sign_arrow_001"));
		
		node.addCommand(new AddRenderComponentCommand(scene, "tutorial_sign_arrow_001", "render", 120, 150, 57, 83, "tutorial_sign_arrow_001"));
		
		AddTranslateAnimationCommand addTranslation = new AddTranslateAnimationCommand(
				scene, "tutorial_sign_arrow_001", "moving_down", 120, 120, 150,
				180, 800, true, true);
		node.addCommand(addTranslation);
		
		node.addEventCommandPair(new EntityEvent(
				EntityEvent.TUTORIAL_FOOD_ADDED, "game_table_plate_b",
				FoodComponent.Type.Bagel), new NextNodeCommand(scene));
		
		node.addCommand(new SetEntityEnableCommand(scene, "game_table_bagel", true));
		
		scene.addSceneNode(node);
	}
	
	private void genBonnieSpeak_1_1_03() {
		SceneNode node = new SceneNode("tutorial_1_1_text_003");
		
		node.addCommand(new RemoveEntityCommand(scene, "tutorial_sign_tap_001"));
		node.addCommand(new RemoveEntityCommand(scene, "tutorial_sign_arrow_001"));
		
		node.addCommand(new SetEntityEnableCommand(scene, "game_table_bagel", false));
		
		// dialog frame
		node.addCommand(new AddEntityCommand(scene, "tutorial_frame_big_001"));
		node.addCommand(new AddRenderComponentCommand(scene, "tutorial_frame_big_001", "render", 108, 220, 550, 196, "tutorial_frame_big_001"));
		
		// dialog text
		node.addCommand(new AddEntityCommand(scene, "tutorial_1_1_text"));
		node.addCommand(new AddRenderComponentCommand(scene, "tutorial_1_1_text", "render", 170, 240, 402, 124, "tutorial_1_1_text_003"));
		
		// ok button
		AddButtonEntityCommand addButton = new AddButtonEntityCommand(scene, "tutorial_frame_ok");
		addButton.addCommandTrigger(new ComponentEvent(ComponentEvent.BUTTON_UP, "button"), new NextNodeCommand(scene));
		node.addCommand(addButton);
		
		AddStatfulRenderComponentCommand addRender = new AddStatfulRenderComponentCommand(
				scene, "tutorial_frame_ok", "render", 583, 338, 74, 74,
				ComponentEvent.BUTTON_UP, "tutorial_frame_ok");
		addRender.addStateTexturePair(ComponentEvent.BUTTON_DOWN, "tutorial_frame_ok_pressed");
		node.addCommand(addRender);
		node.addCommand(new AddButtonComponentCommand(scene, "tutorial_frame_ok", "button", 583, 338, 74, 74));
		
		// bonnie speaks
		node.addCommand(new AddEntityCommand(scene, "tutorial_bonnie_normal"));
		AddFrameAnimationComponentCommand addAnimation = new AddFrameAnimationComponentCommand(
				scene, "tutorial_bonnie_normal", "render", 0, 282, 182, 214, true, true);
		addAnimation.addFrame("tutorial_bonnie_normal_1", 100);
		addAnimation.addFrame("tutorial_bonnie_normal_2", 100);
		addAnimation.addFrame("tutorial_bonnie_normal_1", 100);
		addAnimation.addFrame("tutorial_bonnie_normal_3", 100);
		addAnimation.addFrame("tutorial_bonnie_normal_1", 100);
		addAnimation.addFrame("tutorial_bonnie_normal_3", 100);
		addAnimation.addFrame("tutorial_bonnie_normal_1", 100);
		addAnimation.addFrame("tutorial_bonnie_normal_2", 100);
		addAnimation.addFrame("tutorial_bonnie_normal_1", 100);
		
		addAnimation.addFrame("tutorial_bonnie_normal_4", 400);
		addAnimation.addFrame("tutorial_bonnie_normal_5", 400);
		addAnimation.addFrame("tutorial_bonnie_normal_4", 400);
		addAnimation.addFrame("tutorial_bonnie_normal_5", 400);
		addAnimation.addFrame("tutorial_bonnie_normal_4", 400);
		addAnimation.addFrame("tutorial_bonnie_normal_5", 400);
		addAnimation.addFrame("tutorial_bonnie_normal_4", 400);
		addAnimation.addFrame("tutorial_bonnie_normal_5", 1400);
		
		node.addCommand(addAnimation);
		
		scene.addSceneNode(node);
	}
	
	private void genDragToCustomer() {
		SceneNode node = new SceneNode("drag_bagel_to_customer");
		
		// remove previous entitys
		node.addCommand(new RemoveEntityCommand(scene, "tutorial_frame_ok"));
		node.addCommand(new RemoveEntityCommand(scene, "tutorial_bonnie_normal"));
		node.addCommand(new RemoveEntityCommand(scene, "tutorial_1_1_text"));
		node.addCommand(new RemoveEntityCommand(scene, "tutorial_frame_big_001"));
		
		// add hint text
		node.addCommand(new AddEntityCommand(scene, "tutorial_sign_tap_003"));
		node.addCommand(new AddRenderComponentCommand(scene, "tutorial_sign_tap_003", "render", 218, 225, 392, 59, "tutorial_sign_tap_003"));
		
		// add arrow
		node.addCommand(new AddEntityCommand(scene, "tutorial_sign_arrow_001"));
		
		node.addCommand(new AddRenderComponentCommand(scene, "tutorial_sign_arrow_001", "render", 375, 296, 57, 83, "tutorial_sign_arrow_001"));
		
		AddTranslateAnimationCommand addTranslation = new AddTranslateAnimationCommand(
				scene, "tutorial_sign_arrow_001", "moving_down", 375, 375, 296,
				326, 800, true, true);
		node.addCommand(addTranslation);
		
		RemoveEntityCommand removeHints = new RemoveEntityCommand(scene, new String[] {"tutorial_sign_tap_003", "tutorial_sign_arrow_001"});
		node.addEventCommandPair(new EntityEvent(EntityEvent.FOOD_PRODUCT_CONSUMED, "jogging_girl"), removeHints);
		
		node.addEventCommandPair(new EntityEvent(EntityEvent.TUTORIAL_CUSTOMER_LEAVED, "jogging_girl"), new NextNodeCommand(scene));
		
		scene.addSceneNode(node);
	}
	
	private void genBonnie_wellDone() {
		SceneNode node = new SceneNode("drag_bagel_to_customer");
		
		// dialog frame
		node.addCommand(new AddEntityCommand(scene, "tutorial_frame_big_001"));
		node.addCommand(new AddRenderComponentCommand(scene, "tutorial_frame_big_001", "render", 108, 220, 550, 196, "tutorial_frame_big_001"));
		
		// dialog text
		node.addCommand(new AddEntityCommand(scene, "tutorial_1_1_text"));
		node.addCommand(new AddRenderComponentCommand(scene, "tutorial_1_1_text", "render", 170, 240, 402, 124, "tutorial_1_1_text_004"));
		
		// ok button
		AddButtonEntityCommand addButton = new AddButtonEntityCommand(scene, "tutorial_frame_ok");
		addButton.addCommandTrigger(new ComponentEvent(ComponentEvent.BUTTON_UP, "button"), new NextNodeCommand(scene));	// <-- next node trigger
		node.addCommand(addButton);
		
		AddStatfulRenderComponentCommand addRender = new AddStatfulRenderComponentCommand(
				scene, "tutorial_frame_ok", "render", 583, 338, 74, 74,
				ComponentEvent.BUTTON_UP, "tutorial_frame_ok");
		addRender.addStateTexturePair(ComponentEvent.BUTTON_DOWN, "tutorial_frame_ok_pressed");
		node.addCommand(addRender);
		node.addCommand(new AddButtonComponentCommand(scene, "tutorial_frame_ok", "button", 583, 338, 74, 74));
		
		// bonnie great!
		node.addCommand(new AddEntityCommand(scene, "tutorial_bonnie_great"));
		AddFrameAnimationComponentCommand addAnimation = new AddFrameAnimationComponentCommand(
				scene, "tutorial_bonnie_great", "render", 0, 282, 182, 214, true, true);
		addAnimation.addFrame("tutorial_bonnie_great_1", 200);
		addAnimation.addFrame("tutorial_bonnie_great_2", 100);
		addAnimation.addFrame("tutorial_bonnie_great_1", 100);
		addAnimation.addFrame("tutorial_bonnie_great_2", 1100);
		node.addCommand(addAnimation);
		
		scene.addSceneNode(node);
	}
}
