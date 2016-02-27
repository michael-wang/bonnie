package com.studioirregular.bonniesbrunch.main;

import com.studioirregular.bonniesbrunch.FoodSystem.Food;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.LevelSystem.TableItem;
import com.studioirregular.bonniesbrunch.command.AddAnimationSet;
import com.studioirregular.bonniesbrunch.command.AddButtonEntity;
import com.studioirregular.bonniesbrunch.command.AddFrameAnimation;
import com.studioirregular.bonniesbrunch.command.AddSimpleEntity;
import com.studioirregular.bonniesbrunch.command.Command;
import com.studioirregular.bonniesbrunch.command.DisableAllTableItems;
import com.studioirregular.bonniesbrunch.command.EnableTableItem;
import com.studioirregular.bonniesbrunch.command.RemoveEntity;
import com.studioirregular.bonniesbrunch.command.SendGameEvent;
import com.studioirregular.bonniesbrunch.entity.Customer;
import com.studioirregular.bonniesbrunch.entity.CustomerManager.CustomerSpec;
import com.studioirregular.bonniesbrunch.entity.GameEntity;


public class SceneLoaderTutorial1_1 extends SceneLoader {

	public SceneLoaderTutorial1_1(SceneManager manager, GameEntityRoot root) {
		super(manager, root);
	}
	
	@Override
	public void load() {
		assert root instanceof RootMainGame;
		RootMainGame mainGame = (RootMainGame)root;
		
		zOrderBase = GameEntity.MIN_GAME_ENTITY_Z_ORDER + 4;
		
		manager.add(loadTutorialText(mainGame));
		manager.add(loadBonnieTalks(mainGame));
		manager.add(loadBonnieExcited(mainGame));
		
		manager.add(loadCustomerAppears(mainGame, Customer.Type.JOGGING_GIRL, 1));
		manager.add(loadHintTapBagel(mainGame, false));
		manager.add(loadHoldAMoment(mainGame, 750));
		manager.add(loadBonnieTalks2(mainGame));
		manager.add(loadHintDragBrunchToCustomer(mainGame, true, false, 1));
		manager.add(loadWaitCustomerLeave(mainGame));
		manager.add(loadBonnieTalks3(mainGame));
		
		manager.add(loadCustomerAppears(mainGame, Customer.Type.BALLOON_BOY, 2));
		manager.add(loadHintTapBagel(mainGame, false));
		manager.add(loadHoldAMoment(mainGame, 200));
		manager.add(loadHintDragBrunchToCustomer(mainGame, false, true, 2));
		manager.add(loadWaitCustomerLeave(mainGame));
		manager.add(loadBonnieTalks3(mainGame));
		
		manager.add(loadBonnieTalksAboutMistake(mainGame));
		manager.add(loadHintTapBagel(mainGame, true));
		manager.add(loadHoldAMoment(mainGame, 200));
		manager.add(loadHintDragBrunchToTrashcan(mainGame));
		manager.add(loadHoldAMoment(mainGame, 750));
		manager.add(loadBonnieTalksOver(mainGame));
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
	
	private SceneNode loadBonnieTalks(RootMainGame mainGame) {
		SceneNode node = new SceneNode();
		
		Command command = new AddButtonEntity(manager, "skip_button",
				zOrderBase + 1, 536, 3, 179, 50,
				"tutorial_frame_skip", "tutorial_frame_skip_pressed",
				GameEvent.SCENE_MANAGER_REQUEST_SKIP);
		((AddButtonEntity)command).offsetTouchArea(-16, -8, 8, 16);
		node.add(command);
		
		buildBonnieTalks(node, "tutorial_1_1_text_001", false);
		
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
	
	private void removeBonnieTalks(SceneNode node) {
		RemoveEntity remove = new RemoveEntity(manager, "text");
		remove.and("ok_button");
		remove.and("bonnie");
		remove.and("text_base");
		node.add(remove);
	}
	
	private SceneNode loadBonnieExcited(RootMainGame mainGame) {
		SceneNode node = new SceneNode();
		
		RemoveEntity remove = new RemoveEntity(manager, "text");
		remove.and("bonnie");
		node.add(remove);
		
		Command command = new AddSimpleEntity(manager, "text", zOrderBase + 1,
				168, 241, 402, 124, "tutorial_1_1_text_002");
		node.add(command);
		
		command = new AddSimpleEntity(manager, "bonnie",
				zOrderBase + 1, 0, 282, 182, 214, "");
		node.add(command);
		AddFrameAnimation commandAnimation = new AddFrameAnimation(manager, "bonnie",
				GameEntity.MIN_GAME_COMPONENT_Z_ORDER, 0, 0, 182, 214, true,
				true, true);
		commandAnimation.addFrame("tutorial_bonnie_great_1", 200);
		commandAnimation.addFrame("tutorial_bonnie_great_2", 100);
		commandAnimation.addFrame("tutorial_bonnie_great_1", 100);
		commandAnimation.addFrame("tutorial_bonnie_great_2", 2000);
		node.add(commandAnimation);
		
		return node;
	}
	
	private SceneNode loadCustomerAppears(RootMainGame mainGame, Customer.Type type, int seatIndex) {
		SceneNode node = new SceneNode();
		
		removeBonnieTalks(node);
		
		Customer.OrderingSpec orderSpec = new Customer.OrderingSpec();
		orderSpec.addMustHave(Food.BAGEL);
		CustomerSpec customerSpec = new CustomerSpec(type, orderSpec, CustomerSpec.Mode.Tutorial, seatIndex);
		Command command = new SendGameEvent(GameEvent.CUSTOMER_MANAGER_ADD_CUSTOMER, 0, customerSpec);
		node.add(command);
		
		final GameEvent eventMadeOrder = GameEventSystem.getInstance().obtain(GameEvent.TUTORIAL_CUSTOMER_MADE_DECISION, type.ordinal());
		final GameEvent nextNode = GameEventSystem.getInstance().obtain(GameEvent.SCENE_MANAGER_NEXT_NODE);
		node.addEventMap(eventMadeOrder, nextNode);
		
		return node;
	}
	
	private SceneNode loadHintTapBagel(RootMainGame mainGame, boolean removeBonnieTalks) {
		SceneNode node = new SceneNode();
		
		if (removeBonnieTalks) {
			removeBonnieTalks(node);
		}
		
		Command command = new EnableTableItem(mainGame.table(), TableItem.BAGEL, true);
		node.add(command);
		
		command = new AddSimpleEntity(manager, "arrow", zOrderBase, 120, 150, 57, 83, "tutorial_sign_arrow_001");
		node.add(command);
		
		AddAnimationSet addAnim = new AddAnimationSet(manager, "arrow", GameEntity.MIN_GAME_COMPONENT_Z_ORDER, true);
		addAnim.addTranslateAnimation(120, 130, 120, 150, 300);
		addAnim.addTranslateAnimation(120, 150, 120, 130, 300);
		node.add(addAnim);
		
		command = new AddSimpleEntity(manager, "hint_text", zOrderBase, 69, 90, 157, 59, "tutorial_sign_tap_001");
		node.add(command);
		
		final GameEvent trigger = GameEventSystem.getInstance().obtain(GameEvent.FOOD_GENERATED, Food.BAGEL);
		final GameEvent nextNode = GameEventSystem.getInstance().obtain(GameEvent.SCENE_MANAGER_NEXT_NODE);
		node.addEventMap(trigger, nextNode);
		
		return node;
	}
	
	private SceneNode loadHoldAMoment(RootMainGame mainGame, int duration) {
		SceneNode node = new SceneNode();
		
		RemoveEntity remove = new RemoveEntity(manager, "arrow");
		remove.and("hint_text");
		node.add(remove);
		
		Command command = new DisableAllTableItems(mainGame.table(), true);
		node.add(command);
		
		AddSimpleEntity addHolder = new AddSimpleEntity(manager, "hold_a_moment", zOrderBase, 0, 0, 0, 0, "");
		addHolder.installEventMap(
				GameEventSystem.getInstance().obtain(GameEvent.ANIMATION_END, 0), 
				GameEventSystem.getInstance().obtain(GameEvent.SCENE_MANAGER_NEXT_NODE));
		node.add(addHolder);
		
		AddAnimationSet addAnim = new AddAnimationSet(manager, "hold_a_moment", GameEntity.MIN_GAME_COMPONENT_Z_ORDER, false);
		addAnim.addHoldAnimation(duration, true, 0);
		node.add(addAnim);
		
		return node;
	}
	
	private SceneNode loadBonnieTalks2(RootMainGame mainGame) {
		SceneNode node = new SceneNode();
		
		RemoveEntity remove = new RemoveEntity(manager, "hold_a_moment");
		node.add(remove);
		
		buildBonnieTalks(node, "tutorial_1_1_text_003", true);
		
		return node;
	}
	
	private SceneNode loadHintDragBrunchToCustomer(RootMainGame mainGame, boolean removeBonnieTalks, boolean removeHoldMoment, int seat) {
		SceneNode node = new SceneNode();
		
		if (removeBonnieTalks) {
			removeBonnieTalks(node);
		}
		
		if (removeHoldMoment) {
			Command remove = new RemoveEntity(manager, "hold_a_moment");
			node.add(remove);
		}
		
		Command enableBrunch = new EnableTableItem(mainGame.table(), TableItem.BRUNCH, true);
		node.add(enableBrunch);
		
		Command command = new AddSimpleEntity(manager, "arrow", zOrderBase, 375, 296, 57, 83, "tutorial_sign_arrow_001");
		node.add(command);
		
		AddAnimationSet addAnim = new AddAnimationSet(manager, "arrow", GameEntity.MIN_GAME_COMPONENT_Z_ORDER, true);
		final float x = 90 + seat * 180;
		addAnim.addTranslateAnimation(375, 296, 375, 276, 300);
		addAnim.addTranslateAnimation(375, 276, 375, 296, 300);
		addAnim.addTranslateAnimation(375, 296, 375, 276, 300);
		addAnim.addTranslateAnimation(375, 276, 375, 296, 300);
		addAnim.addTranslateAnimation(375, 296, x, 60, 1000);
		addAnim.addTranslateAnimation(x, 60, x, 80, 300);
		addAnim.addTranslateAnimation(x, 80, x, 60, 300);
		addAnim.addTranslateAnimation(x, 60, x, 80, 300);
		addAnim.addTranslateAnimation(x, 80, x, 60, 300);
		node.add(addAnim);
		
		command = new AddSimpleEntity(manager, "hint_text", zOrderBase, 218, 225, 392, 59, "tutorial_sign_tap_003");
		node.add(command);
		
		final GameEvent trigger = GameEventSystem.getInstance().obtain(GameEvent.TUTORIAL_CUSTOMER_EVENT);
		final GameEvent nextNode = GameEventSystem.getInstance().obtain(GameEvent.SCENE_MANAGER_NEXT_NODE);
		node.addEventMap(trigger, nextNode);
		
		return node;
	}
	
	private SceneNode loadWaitCustomerLeave(RootMainGame mainGame) {
		SceneNode node = new SceneNode();
		
		RemoveEntity remove = new RemoveEntity(manager, "arrow");
		remove.and("hint_text");
		node.add(remove);
		
		final GameEvent trigger = GameEventSystem.getInstance().obtain(GameEvent.TUTORIAL_CUSTOMER_EVENT);
		final GameEvent nextNode = GameEventSystem.getInstance().obtain(GameEvent.SCENE_MANAGER_NEXT_NODE);
		node.addEventMap(trigger, nextNode);
		
		return node;
	}
	
	private SceneNode loadBonnieTalks3(RootMainGame mainGame) {
		SceneNode node = new SceneNode();
		
		buildBonnieTalks(node, "tutorial_1_1_text_004", true);
		
		return node;
	}
	
	private SceneNode loadBonnieTalksAboutMistake(RootMainGame mainGame) {
		SceneNode node = new SceneNode();
		
		removeBonnieTalks(node);
		buildBonnieTalks(node, "tutorial_1_1_text_006", false);
		
		return node;
	}
	
	private SceneNode loadHintDragBrunchToTrashcan(RootMainGame mainGame) {
		SceneNode node = new SceneNode();
		
		RemoveEntity remove = new RemoveEntity(manager, "hold_a_moment");
		node.add(remove);
		
		Command command = new EnableTableItem(mainGame.table(), TableItem.TRASHCAN, true);
		node.add(command);
		command = new EnableTableItem(mainGame.table(), TableItem.BRUNCH, true);
		node.add(command);
		
		command = new AddSimpleEntity(manager, "arrow", zOrderBase, 375, 296, 57, 83, "tutorial_sign_arrow_001");
		node.add(command);
		
		AddAnimationSet addAnim = new AddAnimationSet(manager, "arrow", GameEntity.MIN_GAME_COMPONENT_Z_ORDER, true);
		addAnim.addTranslateAnimation(375, 296, 375, 276, 300);
		addAnim.addTranslateAnimation(375, 276, 375, 296, 300);
		addAnim.addTranslateAnimation(375, 296, 375, 276, 300);
		addAnim.addTranslateAnimation(375, 276, 375, 296, 300);
		addAnim.addTranslateAnimation(375, 296, 15, 326, 1000);
		addAnim.addTranslateAnimation(15, 326, 15, 306, 300);
		addAnim.addTranslateAnimation(15, 306, 15, 326, 300);
		addAnim.addTranslateAnimation(15, 326, 15, 306, 300);
		addAnim.addTranslateAnimation(15, 306, 15, 326, 300);
		node.add(addAnim);
		
		command = new AddSimpleEntity(manager, "hint_text", zOrderBase, 218, 225, 392, 59, "tutorial_sign_tap_005");
		node.add(command);
		
		final GameEvent trigger = GameEventSystem.getInstance().obtain(GameEvent.DROP_ACCEPTED);
		final GameEvent nextNode = GameEventSystem.getInstance().obtain(GameEvent.SCENE_MANAGER_NEXT_NODE);
		node.addEventMap(trigger, nextNode);
		
		return node;
	}
	
	private SceneNode loadBonnieTalksOver(RootMainGame mainGame) {
		SceneNode node = new SceneNode();
		
		RemoveEntity remove = new RemoveEntity(manager, "hold_a_moment");
		node.add(remove);
		
		buildBonnieTalks(node, "tutorial_1_1_text_005", true);
		
		return node;
	}
	
	private int zOrderBase;

}
