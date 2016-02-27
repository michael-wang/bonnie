package com.studioirregular.bonniesbrunch.main;

import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.command.AddAnimationSet;
import com.studioirregular.bonniesbrunch.command.AddButtonEntity;
import com.studioirregular.bonniesbrunch.command.AddFrameAnimation;
import com.studioirregular.bonniesbrunch.command.AddSimpleEntity;
import com.studioirregular.bonniesbrunch.command.Command;
import com.studioirregular.bonniesbrunch.command.DisableAllTableItems;
import com.studioirregular.bonniesbrunch.command.RemoveEntity;
import com.studioirregular.bonniesbrunch.entity.GameEntity;

public class SceneLoaderTutorial1_5 extends SceneLoader {

	public SceneLoaderTutorial1_5(SceneManager manager, GameEntityRoot root) {
		super(manager, root);
	}
	
	@Override
	public void load() {
		assert root instanceof RootMainGame;
		RootMainGame mainGame = (RootMainGame)root;
		
		zOrderBase = GameEntity.MIN_GAME_ENTITY_Z_ORDER + 4;
		
		manager.add(loadTutorialText(mainGame));
		manager.add(loadBonnieTalks(mainGame, "tutorial_1_5_text_001", false));
		manager.add(loadBonnieContinues(mainGame, "tutorial_1_5_text_002", false));
		manager.add(loadBonnieContinues(mainGame, "tutorial_1_5_text_003", true));
	}
	
	// text: tutorial fly by from left to right
	private SceneNode loadTutorialText(RootMainGame mainGame) {
		SceneNode node = new SceneNode();
		
		Command command = new DisableAllTableItems(mainGame.table(), true);
		node.add(command);
		
		final String textEntityName = "tutorial_text";
		final int textAnimationId = textEntityName.hashCode();
		AddSimpleEntity addText = new AddSimpleEntity(manager, textEntityName,
				zOrderBase, -387, 185, 387, 110, "game_text_4_tutorial");
		addText.installEventMap(
				GameEventSystem.getInstance().obtain(GameEvent.ANIMATION_END, textAnimationId),
				GameEventSystem.getInstance().obtain(
						GameEvent.SCENE_MANAGER_NEXT_NODE));
		node.add(addText);
		
		AddAnimationSet addAnimation = new AddAnimationSet(manager, "tutorial_text", GameEntity.MIN_GAME_COMPONENT_Z_ORDER, false);
		addAnimation.addTranslateAnimation(-387, 185, 166, 185, 300);
		addAnimation.addHoldAnimation(1000);
		addAnimation.addTranslateAnimation(166, 185, 720, 185, 300, true, textAnimationId);
		node.add(addAnimation);
		
		return node;
	}
	
	private SceneNode loadBonnieTalks(RootMainGame mainGame, String talkTexture, boolean bonnieExcited) {
		SceneNode node = new SceneNode();
		
		AddButtonEntity command = new AddButtonEntity(manager, "skip_button",
				zOrderBase + 1, 536, 3, 179, 50,
				"tutorial_frame_skip", "tutorial_frame_skip_pressed",
				GameEvent.SCENE_MANAGER_REQUEST_SKIP);
		command.offsetTouchArea(-16, -8, 8, 16);
		node.add(command);
		
		buildBonnieTalks(node, talkTexture, bonnieExcited);
		
		return node;
	}
	
	private SceneNode loadBonnieContinues(RootMainGame mainGame, String talkTexture, boolean bonnieExcited) {
		SceneNode node = new SceneNode();
		
		RemoveEntity remove = new RemoveEntity(manager, "text");
		remove.and("bonnie");
		node.add(remove);
		
		Command command = new AddSimpleEntity(manager, "text", zOrderBase + 1,
				168, 241, 402, 124, talkTexture);
		node.add(command);
		
		command = new AddSimpleEntity(manager, "bonnie",
				zOrderBase + 2, 0, 282, 182, 214, "");
		node.add(command);
		AddFrameAnimation commandAnimation = new AddFrameAnimation(manager, "bonnie",
				GameEntity.MIN_GAME_COMPONENT_Z_ORDER, 0, 0, 182, 214, true,
				true, true);
		if (bonnieExcited) {
			commandAnimation.addFrame("tutorial_bonnie_great_1", 200);
			commandAnimation.addFrame("tutorial_bonnie_great_2", 100);
			commandAnimation.addFrame("tutorial_bonnie_great_1", 100);
			commandAnimation.addFrame("tutorial_bonnie_great_2", 2000);
		} else {
			commandAnimation.addFrame("tutorial_bonnie_normal_1", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_2", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_1", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_3", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_1", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_3", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_1", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_2", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_1", 400);
			commandAnimation.addFrame("tutorial_bonnie_normal_4", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_5", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_4", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_5", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_4", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_5", 100);
		}
		node.add(commandAnimation);
		
		return node;
	} 
	
	private void buildBonnieTalks(SceneNode node, String textTexture, boolean bonnieExcited) {
		Command command = new AddSimpleEntity(manager, "text_base", zOrderBase,
				108, 220, 550, 196, "tutorial_frame_big_001");
		node.add(command);
		
		command = new AddSimpleEntity(manager, "text", zOrderBase + 1,
				168, 241, 402, 124, textTexture);
		node.add(command);
		
		command = new AddButtonEntity(manager, "ok_button",
				zOrderBase + 2, 583, 338, 74, 74,
				"tutorial_frame_ok", "tutorial_frame_ok_pressed",
				GameEvent.SCENE_MANAGER_NEXT_NODE);
		((AddButtonEntity)command).offsetTouchArea(-16, -16, 24, 20);
		node.add(command);
		
		command = new AddSimpleEntity(manager, "bonnie",
				zOrderBase + 2, 0, 282, 182, 214, "");
		node.add(command);
		AddFrameAnimation commandAnimation = new AddFrameAnimation(manager, "bonnie",
				GameEntity.MIN_GAME_COMPONENT_Z_ORDER, 0, 0, 182, 214, true,
				true, true);
		if (bonnieExcited) {
			commandAnimation.addFrame("tutorial_bonnie_great_1", 200);
			commandAnimation.addFrame("tutorial_bonnie_great_2", 100);
			commandAnimation.addFrame("tutorial_bonnie_great_1", 100);
			commandAnimation.addFrame("tutorial_bonnie_great_2", 2000);
		} else {
			commandAnimation.addFrame("tutorial_bonnie_normal_1", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_2", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_1", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_3", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_1", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_3", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_1", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_2", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_1", 400);
			commandAnimation.addFrame("tutorial_bonnie_normal_4", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_5", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_4", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_5", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_4", 100);
			commandAnimation.addFrame("tutorial_bonnie_normal_5", 100);
		}
		node.add(commandAnimation);
	}
	
	private int zOrderBase;

}
