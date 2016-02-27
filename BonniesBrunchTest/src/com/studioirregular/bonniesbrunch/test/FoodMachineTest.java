package com.studioirregular.bonniesbrunch.test;

import junit.framework.TestCase;

import com.studioirregular.bonniesbrunch.FoodSystem.Food;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.component.FoodComponent;
import com.studioirregular.bonniesbrunch.component.GameComponent;
import com.studioirregular.bonniesbrunch.entity.FoodMachine;
import com.studioirregular.bonniesbrunch.entity.FoodMachine.CookSlot;
import com.studioirregular.bonniesbrunch.entity.FoodMachineIcon;
import com.studioirregular.bonniesbrunch.entity.FoodMachineIcon.Type;
import com.studioirregular.bonniesbrunch.entity.GameEntity;

public class FoodMachineTest extends TestCase {

	public void testMachineIconSetup() {
		FoodMachineIcon target = new FoodMachineIcon(0);
		implementTest(target, Type.Muffin, 1);
		
		target = new FoodMachineIcon(0);
		implementTest(target, Type.Muffin, 2);
		
		target = new FoodMachineIcon(0);
		implementTest(target, Type.Toast, 1);
		
		target = new FoodMachineIcon(0);
		implementTest(target, Type.Toast, 2);
		
		target = new FoodMachineIcon(0);
		implementTest(target, Type.Toast, 3);
		
		target = new FoodMachineIcon(0);
		implementTest(target, Type.FryingPan, 1);
		
		target = new FoodMachineIcon(0);
		implementTest(target, Type.FryingPan, 2);
		
		target = new FoodMachineIcon(0);
		implementTest(target, Type.FryingPan, 3);
	}
	
	private void implementTest(FoodMachineIcon target, Type type, int level) {
		target.setup(type, level);
		target.update(0, null);
		
		// 1 button, 1 render, 1 click animation
		assertEquals("food machine:" + type + " has wrong #components.", 3, target.getCount());
		assertFalse(target.cooking);
		assertTrue(containsComponent(target, target.renderNormal));
		assertTrue(target.cookingAnimation.isLoop());
		
		target.turnOn();
		target.update(0, null);
		if (type == Type.FryingPan) {
			assertEquals(4, target.getCount());	// 1 button, 3 animation
		} else {
			assertEquals(3, target.getCount());	// 1 button, 2 animation
		}
		assertTrue(target.cooking);
		assertTrue(containsComponent(target, target.cookingAnimation));
		assertTrue(target.cookingAnimation.isStarted());
	}
	
	private boolean containsComponent(GameEntity entity, GameComponent component) {
		final int count = entity.getCount();
		for (int i = 0; i < count; i++) {
			Object obj = entity.getObject(i);
			if (obj == component) {
				return true;
			}
		}
		return false;
	}
	
	public void testMachineCook() {
		implementMachineCook(FoodMachineIcon.Type.Muffin, FoodMachine.Type.MEDIUM_MACHINE, 1, new int[] {Food.MUFFIN_CIRCLE});
		implementMachineCook(FoodMachineIcon.Type.Muffin, FoodMachine.Type.MEDIUM_MACHINE, 2, new int[] {Food.MUFFIN_CIRCLE, Food.MUFFIN_SQUARE});
		
		implementMachineCook(FoodMachineIcon.Type.Toast, FoodMachine.Type.MEDIUM_MACHINE, 1, new int[] {Food.TOAST_WHITE});
		implementMachineCook(FoodMachineIcon.Type.Toast, FoodMachine.Type.MEDIUM_MACHINE, 2, new int[] {Food.TOAST_WHITE, Food.TOAST_BLACK});
		implementMachineCook(FoodMachineIcon.Type.Toast, FoodMachine.Type.LARGE_MACHINE, 3, new int[] {Food.TOAST_WHITE, Food.TOAST_BLACK, Food.TOAST_YELLOW});
		
		implementMachineCook(FoodMachineIcon.Type.FryingPan, FoodMachine.Type.MEDIUM_MACHINE, 1, new int[] {Food.EGG});
		implementMachineCook(FoodMachineIcon.Type.FryingPan, FoodMachine.Type.MEDIUM_MACHINE, 2, new int[] {Food.EGG, Food.HAM});
		implementMachineCook(FoodMachineIcon.Type.FryingPan, FoodMachine.Type.LARGE_MACHINE, 3, new int[] {Food.EGG, Food.HAM, Food.HOTDOG});
	}
	
	private void implementMachineCook(FoodMachineIcon.Type type, FoodMachine.Type size, int level, int[] foodTypes) {
		FoodMachine machine = new FoodMachine(0);
		
		FoodMachineIcon icon = new FoodMachineIcon(0);
		icon.setup(type, level);
		assertEquals(level, foodTypes.length);
		machine.setup(size, foodTypes, icon, 5000);
		machine.update(0, null);
		
		CookSlot slot = machine.getSlot(0);
		assertEquals(1, slot.getCount());
		assertEquals(slot.emptyBG, slot.getObject(0));
		assertEquals(CookSlot.State.EMPTY, slot.state);
		
		machine.send(GameEventSystem.getInstance().obtain(GameEvent.FOOD_BUTTON_REQUEST_ADD, Food.MUFFIN_CIRCLE));
		machine.update(0, null);
		machine.update(0, null);
		
		FoodComponent food = slot.food;
		assertNotNull(food);
		assertFalse(food.isCooking());
		assertEquals(3, slot.getCount());
		assertEquals(slot.occupiedBG, slot.getObject(0));
		assertEquals(food, slot.getObject(1));
		assertEquals(slot.cookAnimation, slot.getObject(2));
		assertFalse(slot.cookAnimation.isStarted());
		assertEquals(CookSlot.State.READY, slot.state);
		
		slot.send(GameEventSystem.getInstance().obtain(GameEvent.FOOD_MACHINE_START_COOKING, 0, slot));
		machine.update(0, null);
		
		assertTrue(food.isCooking());
		assertEquals(CookSlot.State.COOKING, slot.state);
		assertTrue(slot.cookAnimation.isStarted());
	}
	
	public void testMachineDoubleCook() {
		implementMachineDoubleCook(FoodMachineIcon.Type.Muffin, FoodMachine.Type.MEDIUM_MACHINE, 1, new int[] {Food.MUFFIN_CIRCLE});
		implementMachineDoubleCook(FoodMachineIcon.Type.Muffin, FoodMachine.Type.MEDIUM_MACHINE, 2, new int[] {Food.MUFFIN_CIRCLE, Food.MUFFIN_SQUARE});
		
		implementMachineDoubleCook(FoodMachineIcon.Type.Toast, FoodMachine.Type.MEDIUM_MACHINE, 1, new int[] {Food.TOAST_WHITE});
		implementMachineDoubleCook(FoodMachineIcon.Type.Toast, FoodMachine.Type.MEDIUM_MACHINE, 2, new int[] {Food.TOAST_WHITE, Food.TOAST_BLACK});
		implementMachineDoubleCook(FoodMachineIcon.Type.Toast, FoodMachine.Type.LARGE_MACHINE, 3, new int[] {Food.TOAST_WHITE, Food.TOAST_BLACK, Food.TOAST_YELLOW});
		
		implementMachineDoubleCook(FoodMachineIcon.Type.FryingPan, FoodMachine.Type.MEDIUM_MACHINE, 1, new int[] {Food.EGG});
		implementMachineDoubleCook(FoodMachineIcon.Type.FryingPan, FoodMachine.Type.MEDIUM_MACHINE, 2, new int[] {Food.EGG, Food.HAM});
		implementMachineDoubleCook(FoodMachineIcon.Type.FryingPan, FoodMachine.Type.LARGE_MACHINE, 3, new int[] {Food.EGG, Food.HAM, Food.HOTDOG});
	}
	
	private void implementMachineDoubleCook(FoodMachineIcon.Type type, FoodMachine.Type size, int level, int[] foodTypes) {
		FoodMachine machine = new FoodMachine(0);
		
		FoodMachineIcon icon = new FoodMachineIcon(0);
		icon.setup(type, level);
		machine.setup(size, foodTypes, icon, 5000);
		machine.update(0, null);
		
		CookSlot slotFirst = machine.getSlot(0);
		assertEquals(1, slotFirst.getCount());
		assertEquals(slotFirst.emptyBG, slotFirst.getObject(0));
		assertEquals(CookSlot.State.EMPTY, slotFirst.state);
		
		CookSlot slotSecond = machine.getSlot(1);
		assertEquals(1, slotSecond.getCount());
		assertEquals(slotSecond.emptyBG, slotSecond.getObject(0));
		assertEquals(CookSlot.State.EMPTY, slotSecond.state);
		
		machine.send(GameEventSystem.getInstance().obtain(GameEvent.FOOD_BUTTON_REQUEST_ADD, Food.MUFFIN_CIRCLE));
		machine.send(GameEventSystem.getInstance().obtain(GameEvent.FOOD_BUTTON_REQUEST_ADD, Food.MUFFIN_CIRCLE));
		machine.update(0, null);
		
		FoodComponent food = slotFirst.food;
		assertNotNull(food);
		assertFalse(food.isCooking());
		assertEquals(3, slotFirst.getCount());
		assertEquals(slotFirst.occupiedBG, slotFirst.getObject(0));
		assertEquals(food, slotFirst.getObject(1));
		assertEquals(slotFirst.cookAnimation, slotFirst.getObject(2));
		assertFalse(slotFirst.cookAnimation.isStarted());
		assertEquals(CookSlot.State.READY, slotFirst.state);
		
		food = slotSecond.food;
		assertNotNull(food);
		assertFalse(food.isCooking());
		assertEquals(3, slotSecond.getCount());
		assertEquals(slotSecond.occupiedBG, slotSecond.getObject(0));
		assertEquals(food, slotSecond.getObject(1));
		assertEquals(slotSecond.cookAnimation, slotSecond.getObject(2));
		assertFalse(slotSecond.cookAnimation.isStarted());
		assertEquals(CookSlot.State.READY, slotSecond.state);
		
		slotFirst.send(GameEventSystem.getInstance().obtain(GameEvent.FOOD_MACHINE_START_COOKING, 0, slotSecond));
		machine.update(0, null);
		
		food = slotFirst.food;
		assertTrue(food.isCooking());
		assertEquals(CookSlot.State.COOKING, slotFirst.state);
		assertTrue(slotFirst.cookAnimation.isStarted());
		
		food = slotSecond.food;
		assertFalse(food.isCooking());
		assertEquals(CookSlot.State.READY, slotSecond.state);
		assertFalse(slotSecond.cookAnimation.isStarted());
	}
}
