package com.studioirregular.bonniesbrunch.main;

import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.command.AddAnimationSet;
import com.studioirregular.bonniesbrunch.command.AddSimpleEntity;
import com.studioirregular.bonniesbrunch.command.Command;
import com.studioirregular.bonniesbrunch.command.DisableAllTableItems;
import com.studioirregular.bonniesbrunch.command.PlaySound;
import com.studioirregular.bonniesbrunch.command.RemoveEntity;
import com.studioirregular.bonniesbrunch.entity.GameEntity;

public class SceneLoader_LevelRunning extends SceneLoader {

	public SceneLoader_LevelRunning(SceneManager manager, GameEntityRoot root) {
		super(manager, root);
	}
	
	@Override
	public void load() {
		assert root instanceof RootMainGame;
		RootMainGame mainGame = (RootMainGame)root;
		
		zOrderBase = GameEntity.MIN_GAME_ENTITY_Z_ORDER + 4;
		
		manager.add(loadStartText(mainGame));
		manager.add(loadGo(mainGame));
	}
	
	// text: start fly by from left to right
	private SceneNode loadStartText(RootMainGame mainGame) {
		SceneNode node = new SceneNode();
		
		Command command = new DisableAllTableItems(mainGame.table(), true);
		node.add(command);
		
		AddSimpleEntity addText = new AddSimpleEntity(manager, "text",
				zOrderBase, -387, 185, 387, 110, "game_text_1_start");
		addText.installEventMap(
				GameEventSystem.getInstance().obtain(GameEvent.ANIMATION_END, 1),
				GameEventSystem.getInstance().obtain(
						GameEvent.SCENE_MANAGER_NEXT_NODE));
		node.add(addText);
		
		AddAnimationSet addAnimation = new AddAnimationSet(manager, "text", GameEntity.MIN_GAME_COMPONENT_Z_ORDER, false);
		addAnimation.addTranslateAnimation(-387, 185, 166, 185, 300);
		addAnimation.addHoldAnimation(1000);
		addAnimation.addTranslateAnimation(166, 185, 720, 185, 300, true, 1);
		node.add(addAnimation);
		
		command = new PlaySound("game_text_1_start_s1", false);
		node.add(command);
		
		return node;
	}
	
	private SceneNode loadGo(RootMainGame mainGame) {
		SceneNode node = new SceneNode();
		
		Command command = new DisableAllTableItems(mainGame.table(), false);
		node.add(command);
		
		command = new RemoveEntity(manager, "text");
		node.add(command);
		
		// tricky: add hold animation so we can send next node event when its ended.
		AddSimpleEntity addHolder = new AddSimpleEntity(manager, "holder", zOrderBase, 0, 0, 0, 0, "");
		
		final GameEvent holderEnd = GameEventSystem.getInstance().obtain(GameEvent.ANIMATION_END, 0);
		final GameEvent nextNode = GameEventSystem.getInstance().obtain(GameEvent.SCENE_MANAGER_NEXT_NODE);
		addHolder.installEventMap(holderEnd, nextNode);
		node.add(addHolder);
		
		AddAnimationSet addAnimation = new AddAnimationSet(manager, "holder", zOrderBase, false);
		addAnimation.addHoldAnimation(100, true, 0);
		node.add(addAnimation);
		
		return node;
	}
	
	private int zOrderBase;
}
