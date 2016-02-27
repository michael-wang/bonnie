package com.studioirregular.bonniesbrunch.main;

import java.util.List;

import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.command.AddAnimationSet;
import com.studioirregular.bonniesbrunch.command.AddButtonEntity;
import com.studioirregular.bonniesbrunch.command.AddSimpleEntity;
import com.studioirregular.bonniesbrunch.command.Command;
import com.studioirregular.bonniesbrunch.command.DisableAllTableItems;
import com.studioirregular.bonniesbrunch.command.RemoveEntity;
import com.studioirregular.bonniesbrunch.entity.GameEntity;

public class SceneLoader_LevelStart extends SceneLoader {

	public SceneLoader_LevelStart(SceneManager manager, GameEntityRoot root) {
		super(manager, root);
	}
	
	@Override
	public void load() {
		assert root instanceof RootMainGame;
		RootMainGame mainGame = (RootMainGame)root;
		
		zOrderBase = GameEntity.MIN_GAME_ENTITY_Z_ORDER + 4;
		
		if (mainGame.gameLevel().whatsNew != null) {
			List<String> texturePartitions = mainGame.gameLevel().whatsNew.partitionList;
			for (int i = 0; i < texturePartitions.size(); i++) {
				String partition = texturePartitions.get(i);
				manager.add(loadWhatsNew(mainGame, partition, i == 0));
			}
			manager.add(loadCloseDialog(mainGame));
			manager.add(loadCleanUp(mainGame));
		}
	}
	
	private SceneNode loadWhatsNew(RootMainGame mainGame, String texturePartition, boolean first) {
		SceneNode node = new SceneNode();
		
		if (first) {
			Command command = new DisableAllTableItems(mainGame.table(), true);
			node.add(command);
			
			command = new AddSimpleEntity(manager, "dialog", zOrderBase,
					0, 0, 720, 480, "card_bg");
			// for closing dialog: see loadCloseDialog()
			final GameEvent animEnd = GameEventSystem.getInstance().obtain(GameEvent.ANIMATION_END, 1);
			final GameEvent next = GameEventSystem.getInstance().obtain(GameEvent.SCENE_MANAGER_NEXT_NODE);
			((AddSimpleEntity)command).installEventMap(animEnd, next);
			node.add(command);
			
			command = new AddButtonEntity(manager, "ok_button",
					zOrderBase + 1, 616, 383, 74, 74,
					"tutorial_frame_ok", "tutorial_frame_ok_pressed",
					GameEvent.SCENE_MANAGER_NEXT_NODE);
			((AddButtonEntity)command).offsetTouchArea(-16, -16, 24, 20);
			node.add(command);
			
			command = new AddSimpleEntity(manager, "new_badge",
					zOrderBase + 2, 70, 12, 135, 135, "card_bg_new");
			node.add(command);
			
			AddAnimationSet addAnim = new AddAnimationSet(manager, "dialog", GameEntity.MIN_GAME_COMPONENT_Z_ORDER, false);
			addAnim.addScaleAnimation(0.5f, 1.0f, SCALE_ANIMATION_DURATION, false, 0, true, false);
			node.add(addAnim);
			
			addAnim = new AddAnimationSet(manager, "new_badge", GameEntity.MIN_GAME_COMPONENT_Z_ORDER, false);
			addAnim.addScaleAnimation(0.5f, 1.0f, SCALE_ANIMATION_DURATION, false, 0, true, false);
			node.add(addAnim);
			
			addAnim = new AddAnimationSet(manager, "ok_button", GameEntity.MIN_GAME_COMPONENT_Z_ORDER, false);
			addAnim.addScaleAnimation(0.5f, 1.0f, SCALE_ANIMATION_DURATION, false, 0, true, false);
			node.add(addAnim);
		} else {
			Command remove = new RemoveEntity(manager, "picture");
			node.add(remove);
		}
		
		Command command = new AddSimpleEntity(manager, "picture", zOrderBase + 1,
				112, 64, 495, 351, texturePartition);
		node.add(command);
		
		if (first) {
			AddAnimationSet addAnim = new AddAnimationSet(manager, "picture", GameEntity.MIN_GAME_COMPONENT_Z_ORDER, false);
			addAnim.addScaleAnimation(0.5f, 1.0f, SCALE_ANIMATION_DURATION, false, 0);
			node.add(addAnim);
		}
		
		return node;
	}
	
	private SceneNode loadCloseDialog(RootMainGame mainGame) {
		SceneNode node = new SceneNode();
		
		// add shrink animations
		AddAnimationSet addAnim = new AddAnimationSet(manager, "dialog", GameEntity.MIN_GAME_COMPONENT_Z_ORDER, false);
		addAnim.addScaleAnimation(1.0f, 0.5f, SCALE_ANIMATION_DURATION, true, 1);
		node.add(addAnim);
		
		addAnim = new AddAnimationSet(manager, "picture", GameEntity.MIN_GAME_COMPONENT_Z_ORDER, false);
		addAnim.addScaleAnimation(1.0f, 0.5f, SCALE_ANIMATION_DURATION, false, 0);
		node.add(addAnim);
		
		addAnim = new AddAnimationSet(manager, "new_badge", GameEntity.MIN_GAME_COMPONENT_Z_ORDER, false);
		addAnim.addScaleAnimation(1.0f, 0.5f, SCALE_ANIMATION_DURATION, false, 0);
		node.add(addAnim);
		
		addAnim = new AddAnimationSet(manager, "ok_button", GameEntity.MIN_GAME_COMPONENT_Z_ORDER, false);
		addAnim.addScaleAnimation(1.0f, 0.5f, SCALE_ANIMATION_DURATION, false, 0);
		node.add(addAnim);
		
		return node;
	}
	
	private SceneNode loadCleanUp(RootMainGame mainGame) {
		SceneNode node = new SceneNode();
		
		Command remove = new RemoveEntity(manager, "new_badge");
		node.add(remove);
		
		remove = new RemoveEntity(manager, "ok_button");
		node.add(remove);
		
		remove = new RemoveEntity(manager, "picture");
		node.add(remove);
		
		remove = new RemoveEntity(manager, "dialog");
		node.add(remove);
		
		Command command = new DisableAllTableItems(mainGame.table(), false);
		node.add(command);
		
		// tricky: add hold animation so we can send next node event when its ended.
		AddSimpleEntity addHolder = new AddSimpleEntity(manager, "holder", zOrderBase, 0, 0, 0, 0, "");
		
		final GameEvent animEnd = GameEventSystem.getInstance().obtain(GameEvent.ANIMATION_END, 1);
		final GameEvent nextNode = GameEventSystem.getInstance().obtain(GameEvent.SCENE_MANAGER_NEXT_NODE);
		addHolder.installEventMap(animEnd, nextNode);
		node.add(addHolder);
		
		AddAnimationSet addAnimation = new AddAnimationSet(manager, "holder", zOrderBase, false);
		addAnimation.addHoldAnimation(100, true, 1);
		node.add(addAnimation);
		
		return node;
	}
	
	private static final int SCALE_ANIMATION_DURATION = 300;
	private int zOrderBase;
}
