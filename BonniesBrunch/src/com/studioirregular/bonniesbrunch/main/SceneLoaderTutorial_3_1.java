package com.studioirregular.bonniesbrunch.main;

import com.studioirregular.bonniesbrunch.FoodSystem.Food;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.LevelSystem.TableItem;
import com.studioirregular.bonniesbrunch.command.AddAnimationSet;
import com.studioirregular.bonniesbrunch.command.AddButtonEntity;
import com.studioirregular.bonniesbrunch.command.AddFrameAnimation;
import com.studioirregular.bonniesbrunch.command.AddSimpleEntity;
import com.studioirregular.bonniesbrunch.command.EnableTableItem;
import com.studioirregular.bonniesbrunch.command.Command;
import com.studioirregular.bonniesbrunch.command.DisableAllTableItems;
import com.studioirregular.bonniesbrunch.command.RemoveEntity;
import com.studioirregular.bonniesbrunch.command.SendGameEvent;
import com.studioirregular.bonniesbrunch.entity.Customer;
import com.studioirregular.bonniesbrunch.entity.FoodMachine;
import com.studioirregular.bonniesbrunch.entity.FoodMachineIcon;
import com.studioirregular.bonniesbrunch.entity.CustomerManager.CustomerSpec;
import com.studioirregular.bonniesbrunch.entity.GameEntity;

public class SceneLoaderTutorial_3_1 extends SceneLoader {

	public SceneLoaderTutorial_3_1(SceneManager manager, GameEntityRoot root) {
		super(manager, root);
	}
	
	@Override
	public void load() {
		assert root instanceof RootMainGame;
		RootMainGame mainGame = (RootMainGame)root;
		
		zOrderBase = GameEntity.MIN_GAME_ENTITY_Z_ORDER + 4;
		
		manager.add(loadTutorialText(mainGame));
		manager.add(initBonnieTalks(mainGame, "tutorial_3_1_text_001", true));
		
		manager.add(loadCustomerAppears(mainGame, Customer.Type.SUPERSTAR_MAN, 2, true));
		manager.add(loadHoldAMoment(mainGame, 300, false));
		
		// make egg
		manager.add(loadHintTapFryingPan(mainGame));
		manager.add(loadHoldAMoment(mainGame, 300, true));
		
		manager.add(loadHintCookEgg(mainGame));
		manager.add(loadHoldAMoment(mainGame, 300, true));
		manager.add(loadWaitForCooking(mainGame));
		manager.add(loadHoldAMoment(mainGame, 300, false));
		
		manager.add(loadHintTapFinishedEgg(mainGame));
		manager.add(loadHoldAMoment(mainGame, 300, true));
		
		// make toast
		manager.add(loadHintTapToastMachine(mainGame));
		manager.add(loadHoldAMoment(mainGame, 300, true));
		manager.add(loadHintCookBlackToast(mainGame));
		manager.add(loadHoldAMoment(mainGame, 300, true));
		manager.add(loadWaitForCooking(mainGame));
		
		manager.add(loadHintTapBlackToast(mainGame));
		manager.add(loadHoldAMoment(mainGame, 300, true));
		manager.add(loadHintTapToastTop(mainGame));
		manager.add(loadHoldAMoment(mainGame, 300, true));
		
		manager.add(loadHintDragBrunchToCustomer(mainGame, true, 2));
		manager.add(loadWaitCustomerLeave(mainGame));
		
		manager.add(loadBonnieTalks(mainGame, "tutorial_1_1_text_005", true));
		manager.add(loadHoldAMoment(mainGame, 500, false));
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
	
	private SceneNode initBonnieTalks(RootMainGame mainGame, String talkTexture, boolean bonnieExcited) {
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
	
	private SceneNode loadBonnieTalks(RootMainGame mainGame, String talkTexture, boolean bonnieExcited) {
		return loadBonnieTalks(mainGame, talkTexture, bonnieExcited, false);
	}
	
	private SceneNode loadBonnieTalks(RootMainGame mainGame, String talkTexture, boolean bonnieExcited, boolean removeHoldMoment) {
		SceneNode node = new SceneNode();
		
		if (removeHoldMoment) {
			Command remove = new RemoveEntity(manager, "hold_a_moment");
			node.add(remove);
		}
		
		buildBonnieTalks(node, talkTexture, bonnieExcited);
		
		return node;
	}
	
	private SceneNode loadCustomerAppears(RootMainGame mainGame, Customer.Type type, int seatIndex, boolean removeBonnieTalks) {
		SceneNode node = new SceneNode();
		
		if (removeBonnieTalks) {
			removeBonnieTalks(node);
		}
		
		Customer.OrderingSpec orderSpec = new Customer.OrderingSpec();
		orderSpec.addMustHave(Food.TOAST_BLACK);
		orderSpec.addMustHave(Food.EGG);
		CustomerSpec customerSpec = new CustomerSpec(type, orderSpec, CustomerSpec.Mode.Tutorial, seatIndex);
		Command command = new SendGameEvent(GameEvent.CUSTOMER_MANAGER_ADD_CUSTOMER, 0, customerSpec);
		node.add(command);
		
		final GameEvent eventMadeOrder = GameEventSystem.getInstance().obtain(GameEvent.TUTORIAL_CUSTOMER_MADE_DECISION, type.ordinal());
		final GameEvent nextNode = GameEventSystem.getInstance().obtain(GameEvent.SCENE_MANAGER_NEXT_NODE);
		node.addEventMap(eventMadeOrder, nextNode);
		
		return node;
	}
	
	private SceneNode loadHoldAMoment(RootMainGame mainGame, int duration, boolean removeHintTap) {
		return this.loadHoldAMoment(mainGame, duration, removeHintTap, false);
	}
	
	private SceneNode loadHoldAMoment(RootMainGame mainGame, int duration, boolean removeHintTap, boolean removeBonnieTalks) {
		SceneNode node = new SceneNode();
		
		if (removeHintTap) {
			RemoveEntity remove = new RemoveEntity(manager, "arrow");
			remove.and("hint_text");
			node.add(remove);
		}
		
		if (removeBonnieTalks) {
			removeBonnieTalks(node);
		}
		
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
	
	private SceneNode loadHintTapFryingPan(RootMainGame mainGame) {
		SceneNode node = new SceneNode();
		
		Command remove = new RemoveEntity(manager, "hold_a_moment");
		node.add(remove);
		
		Command command = new EnableTableItem(mainGame.table(), TableItem.FRYING_PAN, true);
		node.add(command);
		
		hintTap(node, false, 234, 273, 183, 200);
		
		final GameEvent trigger = GameEventSystem.getInstance().obtain(GameEvent.FOOD_MACHINE_SHOW, FoodMachineIcon.Type.FryingPan.ordinal());
		final GameEvent nextNode = GameEventSystem.getInstance().obtain(GameEvent.SCENE_MANAGER_NEXT_NODE);
		node.addEventMap(trigger, nextNode);
		
		return node;
	}
	
	private SceneNode loadHintCookEgg(RootMainGame mainGame) {
		SceneNode node = new SceneNode();
		
		Command remove = new RemoveEntity(manager, "hold_a_moment");
		node.add(remove);
		
		Command command = new EnableTableItem(mainGame.table(), TableItem.FRYING_PAN, FoodMachine.ItemType.FoodButton, 0, true);
		node.add(command);
		
		hintTap(node, false, 278, 93, 227, 36);
		
		final GameEvent trigger = GameEventSystem.getInstance().obtain(GameEvent.FOOD_MACHINE_SLOT_FOOD_ADDED, Food.EGG);
		final GameEvent nextNode = GameEventSystem.getInstance().obtain(GameEvent.SCENE_MANAGER_NEXT_NODE);
		node.addEventMap(trigger, nextNode);
		
		return node;
	}
	
	private SceneNode loadWaitForCooking(RootMainGame mainGame) {
		SceneNode node = new SceneNode();
		
		final GameEvent trigger = GameEventSystem.getInstance().obtain(GameEvent.FOOD_MACHINE_FINISH_COOKING, 0);
		final GameEvent nextNode = GameEventSystem.getInstance().obtain(GameEvent.SCENE_MANAGER_NEXT_NODE);
		node.addEventMap(trigger, nextNode);
		
		return node;
	}
	
	private SceneNode loadHintTapFinishedEgg(RootMainGame mainGame) {
		SceneNode node = new SceneNode();
		
		Command remove = new RemoveEntity(manager, "hold_a_moment");
		node.add(remove);
		
		Command command = new EnableTableItem(mainGame.table(), TableItem.FRYING_PAN, FoodMachine.ItemType.FoodSlot, 0, true);
		node.add(command);
		
		hintTap(node, false, 269, 225, 218, 173);
		
		final GameEvent trigger = GameEventSystem.getInstance().obtain(GameEvent.FOOD_GENERATED, Food.EGG);
		final GameEvent nextNode = GameEventSystem.getInstance().obtain(GameEvent.SCENE_MANAGER_NEXT_NODE);
		node.addEventMap(trigger, nextNode);
		
		return node;
	}
	
	private SceneNode loadHintTapToastMachine(RootMainGame mainGame) {
		SceneNode node = new SceneNode();
		
		Command remove = new RemoveEntity(manager, "hold_a_moment");
		node.add(remove);
		
		Command command = new EnableTableItem(mainGame.table(), TableItem.TOAST_MACHINE, true);
		node.add(command);
		
		hintTap(node, false, 315, 158, 266, 96);
		
		final GameEvent trigger = GameEventSystem.getInstance().obtain(GameEvent.FOOD_MACHINE_SHOW, FoodMachineIcon.Type.Toast.ordinal());
		final GameEvent nextNode = GameEventSystem.getInstance().obtain(GameEvent.SCENE_MANAGER_NEXT_NODE);
		node.addEventMap(trigger, nextNode);
		
		return node;
	}
	
	private SceneNode loadHintCookBlackToast(RootMainGame mainGame) {
		SceneNode node = new SceneNode();
		
		Command remove = new RemoveEntity(manager, "hold_a_moment");
		node.add(remove);
		
		Command command = new EnableTableItem(mainGame.table(), TableItem.TOAST_MACHINE, FoodMachine.ItemType.FoodButton, 1, true);
		node.add(command);
		
		hintTap(node, false, 393, 93, 342, 36);
		
		final GameEvent trigger = GameEventSystem.getInstance().obtain(GameEvent.FOOD_MACHINE_START_COOKING, 0);
		final GameEvent nextNode = GameEventSystem.getInstance().obtain(GameEvent.SCENE_MANAGER_NEXT_NODE);
		node.addEventMap(trigger, nextNode);
		
		return node;
	}
	
	private SceneNode loadHintTapBlackToast(RootMainGame mainGame) {
		SceneNode node = new SceneNode();
		
		Command command = new EnableTableItem(mainGame.table(), TableItem.TOAST_MACHINE, FoodMachine.ItemType.FoodSlot, 0, true);
		node.add(command);
		
		hintTap(node, false, 269, 225, 218, 173);
		
		final GameEvent trigger = GameEventSystem.getInstance().obtain(GameEvent.FOOD_GENERATED, Food.TOAST_BLACK);
		final GameEvent nextNode = GameEventSystem.getInstance().obtain(GameEvent.SCENE_MANAGER_NEXT_NODE);
		node.addEventMap(trigger, nextNode);
		
		return node;
	}
	
	private SceneNode loadHintTapToastTop(RootMainGame mainGame) {
		SceneNode node = new SceneNode();
		
		Command remove = new RemoveEntity(manager, "hold_a_moment");
		node.add(remove);
		
		Command command = new EnableTableItem(mainGame.table(), TableItem.TOAST_TOP, true);
		node.add(command);
		
		hintTap(node, false, 488, 305, 437, 243);
		
		final GameEvent trigger = GameEventSystem.getInstance().obtain(GameEvent.BRUNCH_TOAST_CLOSED);
		final GameEvent nextNode = GameEventSystem.getInstance().obtain(GameEvent.SCENE_MANAGER_NEXT_NODE);
		node.addEventMap(trigger, nextNode);
		
		return node;
	}
	
	// seat: 0 - 3
	private SceneNode loadHintDragBrunchToCustomer(RootMainGame mainGame, boolean removeHoldMoment, int seat) {
		SceneNode node = new SceneNode();
		
		if (removeHoldMoment) {
			Command remove = new RemoveEntity(manager, "hold_a_moment");
			node.add(remove);
		} else {
			RemoveEntity remove = new RemoveEntity(manager, "arrow");
			remove.and("hint_text");
			node.add(remove);
		}
		
		Command command = new EnableTableItem(mainGame.table(), TableItem.BRUNCH, true);
		node.add(command);
		
		command = new AddSimpleEntity(manager, "arrow", zOrderBase, 375, 296, 57, 83, "tutorial_sign_arrow_001");
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
	
	private void hintTap(SceneNode node, boolean removePrevHint, float arrowX, float arrowY, float tapX, float tapY) {
		if (removePrevHint) {
			RemoveEntity remove = new RemoveEntity(manager, "arrow");
			remove.and("hint_text");
			node.add(remove);
		}
		
		Command command = new AddSimpleEntity(manager, "arrow", zOrderBase, 120, 150, 57, 83, "tutorial_sign_arrow_001");
		node.add(command);
		
		AddAnimationSet addAnim = new AddAnimationSet(manager, "arrow", GameEntity.MIN_GAME_COMPONENT_Z_ORDER, true);
		addAnim.addTranslateAnimation(arrowX, arrowY - 20, arrowX, arrowY, 300);
		addAnim.addTranslateAnimation(arrowX, arrowY, arrowX, arrowY - 20, 300);
		node.add(addAnim);
		
		command = new AddSimpleEntity(manager, "hint_text", zOrderBase, tapX, tapY, 157, 59, "tutorial_sign_tap_001");
		node.add(command);
	}
	
	private int zOrderBase;

}
