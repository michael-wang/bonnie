package com.studioirregular.bonniesbrunch.entity;

import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.FoodSystem.Food;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.InputSystem.TouchEvent;
import com.studioirregular.bonniesbrunch.SoundSystem;
import com.studioirregular.bonniesbrunch.SoundSystem.Sound;
import com.studioirregular.bonniesbrunch.component.ButtonComponent;
import com.studioirregular.bonniesbrunch.component.FoodComponent;
import com.studioirregular.bonniesbrunch.component.FrameAnimationComponent;
import com.studioirregular.bonniesbrunch.component.RenderComponent;
import com.studioirregular.bonniesbrunch.entity.BrunchHolder.Size;
import com.studioirregular.bonniesbrunch.entity.FoodMachine.CookSlot.State;

/*
 * There are two types of food machines:
 * 1. medium sized: can hold up to two food button and three cook slots.
 * 2. large sized: hold up to three food button and five cook slots.
 * 
 * TODO: add sanity check, look out for orphan animation/food component. 
 */
public class FoodMachine extends GameEntity {

	private static final String TAG = "food-machine";
	
	public enum Type {
		MEDIUM_MACHINE,
		LARGE_MACHINE
	};
	
	public enum ItemType {
		FoodButton,
		FoodSlot
	}
	
	public FoodMachine(int zOrder) {
		super(zOrder);
	}
	
	public void setup(Type type, int[] foods, FoodMachineIcon myIcon, int cookDuration) {
		this.type = type;
		this.foods = foods;
		this.myIcon = myIcon;
		this.cooking = false;
		
		slotCount = 0;
		if (foods.length == 1) {
			slotCount = 2;
		} else if (foods.length == 2) {
			slotCount = 3;
		} else if (foods.length == 3) {
			slotCount = 5;
		}
		assert slotCount > 0;
		
		internalSetup(cookDuration);
	}
	
	public void enableItem(ItemType type, int index, boolean enable) {
		if (ItemType.FoodButton == type) {
			if (0 <= index && index < buttons.length) {
				buttons[index].setDisable(!enable);
			} else {
				Log.e(TAG, "enableItem index out of range:" + index);
			}
		} else if (ItemType.FoodSlot == type) {
			if (0 <= index && index < slots.length) {
				slots[index].setDisable(!enable);
			} else {
				Log.e(TAG, "enableItem index out of range:" + index);
			}
		}
	}
	
	private void internalSetup(int cookDuration) {
		if (type == Type.MEDIUM_MACHINE) {
			this.setup(244, 181, MEDIUM_MACHINE_WIDTH, MEDIUM_MACHINE_HEIGHT);
		} else if (type == Type.LARGE_MACHINE) {
			this.setup(180, 181, LARGE_MACHINE_WIDTH, LARGE_MACHINE_HEIGHT);
		}
		
		RenderComponent bg = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		if (type == Type.MEDIUM_MACHINE) {
			bg.setup(MEDIUM_MACHINE_WIDTH, MEDIUM_MACHINE_HEIGHT);
			bg.setup("popup_three_bg");
		} else if (type == Type.LARGE_MACHINE) {
			bg.setup(LARGE_MACHINE_WIDTH, LARGE_MACHINE_HEIGHT);
			bg.setup("popup_five_bg");
		}
		add(bg);
		
		// food buttons, assume box already set.
		buttons = new FoodButton[foods.length];
		for (int i = 0; i < foods.length; i++) {
			FoodButton foodButton = new FoodButton(MIN_GAME_COMPONENT_Z_ORDER + 1 + i, this);
			final float width = type == Type.MEDIUM_MACHINE ? MEDIUM_MACHINE_FOOD_BUTTON_WIDTH
					: LARGE_MACHINE_FOOD_BUTTON_WIDTH;
			foodButton.setup(foods[i], box.left + width * i, 
					box.top + PADDING_TOP, width, FoodButton.HEIGHT);
			add(foodButton);
			
			buttons[i] = foodButton;
		}
		
		// cook slots
		slots = new CookSlot[slotCount];
		for (int i = 0; i < slotCount; i++) {
			CookSlot slot = new CookSlot(MIN_GAME_COMPONENT_Z_ORDER + 10 + i, this);
			final float x = box.left
					+ (type == Type.MEDIUM_MACHINE ? MEDIUM_MACHINE_PADDING_LEFT
							: LARGE_MACHINE_PADDING_LEFT) + CookSlot.WIDTH * i;
			final float y = box.bottom - PADDING_BOTTOM - CookSlot.HEIGHT;
			slot.setup(x, y, CookSlot.WIDTH, CookSlot.HEIGHT, cookDuration);
			slot.setTouchArea(i == 0 ? -12 : 0, 
					-12, 
					i == (slotCount-1) ? 12 : 0, 
					12);
			
			slots[i] = slot;
			add(slot);
		}
		
		// close button
		ButtonEntity.Builder builder = new ButtonEntity.Builder(
				Type.MEDIUM_MACHINE == type ? 448 : 512, 150, 
						60, 60, "popup_close_bg", "popup_close_bg");
		builder.emitEventWhenPressed(GameEvent.BUTTON_UP);
		builder.offsetTouchArea(0, -16, 16, 0);
		add(new ButtonEntity(MIN_GAME_COMPONENT_Z_ORDER + 10, builder));
	}
	
	@Override
	public void reset() {
		if (cooking) {
			cooking = false;
		}
		super.reset();
	}
	
	@Override
	public boolean dispatch(TouchEvent event, GameEntity parent) {
		if (!visible) {
			return false;
		}
		
		if (super.dispatch(event, parent) == false) {
			if (!disable) {
				// if touch out side of me, get hide.
				if (box.contains(event.x, event.y) == false) {
					if (event.type == TouchEvent.DOWN) {
						hide();
					}
				}
			}
		}
		
		return true;	// block touch events
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		return false;
	}

	@Override
	protected void handleGameEvent(GameEvent event) {
		if (Config.DEBUG_LOG) Log.d(TAG, "handleGameEvent event:" + event);
		if (event.what == GameEvent.FOOD_BUTTON_REQUEST_ADD) {
			handleRequestAdd(event.arg1);
		} else if (event.what == GameEvent.BUTTON_UP) {
			hide();
		}
	}
	
	private void handleRequestAdd(int foodType) {
		CookSlot slot = findEmptySlot();
		if (slot != null) {
			GameEvent addFood = GameEventSystem.getInstance().obtain(GameEvent.FOOD_MACHINE_ADD_FOOD, foodType);
			slot.send(addFood);	// direct message: for handleRequestAdd is invoked when dispatch touch event.
		}
	}
	
	// back door for cook slot to notify me its state changed.
	// NOTICE: recursive call NOT allowed, in order to maintain correct order to notify myIcon about slot state change.
	public void onSlotStateChanged(CookSlot slot) {
		if (Config.DEBUG_LOG) Log.d(TAG, "onSlotStateChanged slot.state:" + slot.getState() + ",cooking:" + cooking);
		
		final int slotIndex = findSlotIndex(slot);
		
		// update my state
		State newState = slot.getState();
		if (newState == State.READY) {
			if (!cooking) {
				cooking = true;
				GameEventSystem.scheduleEvent(GameEvent.FOOD_MACHINE_START_COOKING, slotIndex, slot);
			}
		} else if (newState == State.COOKING) {
			assert cooking;
		} else if (newState == State.DONE) {
			assert cooking;
			
			GameEventSystem.scheduleEvent(GameEvent.FOOD_MACHINE_FINISH_COOKING, slotIndex, slot);
			
			CookSlot nextSlot = findNextReadySlot(slotIndex);
			if (nextSlot == null) {
				cooking = false;
			} else {
				final int nextSlotIndex = findSlotIndex(nextSlot);
				GameEventSystem.scheduleEvent(GameEvent.FOOD_MACHINE_START_COOKING, nextSlotIndex, nextSlot);
			}
		} else if (newState == State.EMPTY) {
			hide();
		}
		
		// then notify myIcon
		myIcon.notifyCookSlotStateChanged(this, slot);
	}
	
	// exclusive for FoodMachineIcon
	public CookSlot findNextDoneCookingSlot() {
		for (int i = slots.length - 1; i >= 0; i--) {
			if (slots[i].getState() == State.DONE) {
				return slots[i];
			}
		}
		return null;
	}
	
	// for test
	public CookSlot getSlot(int index) {
		assert 0 <= index && index < slots.length;
		return slots[index];
	}
	
	private CookSlot findEmptySlot() {
		for (int i = 0; i < slots.length; i++) {
			if (slots[i].isAvailableToAddAnotherFood()) {
				return slots[i];
			}
		}
		return null;
	}
	
	private int findSlotIndex(CookSlot slot) {
		for (int i = 0; i < slots.length; i++) {
			if (slots[i] == slot) {
				return i;
			}
		}
		return -1;
	}
	
	private CookSlot findNextReadySlot(int startIndex) {
		for (int i = startIndex; i < slots.length; i++) {
			if (slots[i].getState() == State.READY) {
				return slots[i];
			}
		}
		for (int i = 0; i < startIndex; i++) {
			if (slots[i].getState() == State.READY) {
				return slots[i];
			}
		}
		return null;
	}
	
	private Type type;
	private int foods[];
	public boolean cooking = false;
	private FoodMachineIcon myIcon;
	
	private FoodButton[] buttons;
	private int slotCount;
	private CookSlot[] slots;
	
	private static final int MEDIUM_MACHINE_WIDTH = 233;
	private static final int MEDIUM_MACHINE_HEIGHT = 222;
	private static final int LARGE_MACHINE_WIDTH = 362;
	private static final int LARGE_MACHINE_HEIGHT = 222;
	
	private static final int MEDIUM_MACHINE_FOOD_BUTTON_WIDTH = 116;
	private static final int LARGE_MACHINE_FOOD_BUTTON_WIDTH = 120;
	
	private static final int MEDIUM_MACHINE_PADDING_LEFT = 16;
	private static final int LARGE_MACHINE_PADDING_LEFT = 14;
	private static final int PADDING_TOP = 2;
	private static final int PADDING_BOTTOM = 18;
	
	private static class FoodButton extends GameEntity {

		public FoodButton(int zOrder, FoodMachine host) {
			super(zOrder);
			this.host = host;
		}
		
		public void setup(int foodType, float x, float y, float width, float height) {
			this.setup(x, y, width, height);
			
			this.foodType = foodType;
			
			FoodComponent bg = new FoodComponent(MIN_GAME_COMPONENT_Z_ORDER);
			bg.setup(foodType, BrunchHolder.Size.Normal);
			bg.setupOffset((width - WIDTH) / 2, 0);
			add(bg);
			
			ButtonComponent button = new ButtonComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
			button.setup(x, y, width, height);
			add(button);
			
			clickSound = SoundSystem.getInstance().load("game_table_pickup_s1");
		}
		
		@Override
		protected boolean wantThisEvent(GameEvent event) {
			return false;
		}

		@Override
		protected void handleGameEvent(GameEvent event) {
			if (event.what == GameEvent.BUTTON_UP) {
				SoundSystem.getInstance().playSound(clickSound, false);
				
				GameEvent requestAdd = GameEventSystem.getInstance().obtain(GameEvent.FOOD_BUTTON_REQUEST_ADD, foodType);
				host.send(requestAdd);
			}
		}
		
		private static final float WIDTH = 90;
		private static final float HEIGHT = 90;
		private FoodMachine host;
		private int foodType;
		private Sound clickSound;
	}
	
	public static  class CookSlot extends GameEntity {

		public enum State {
			EMPTY,
			READY,
			COOKING,
			DONE,
		}
		
		private static final int WIDTH = 67;
		private static final int HEIGHT = 67;
		
		public CookSlot(int zOrder, FoodMachine host) {
			super(zOrder);
			this.host = host;
		}
		
		public void setup(float x, float y, float width, float height, int cookDuration) {
			super.setup(x, y, width, height);
			
			emptyBG = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
			emptyBG.setup(width, height);
			emptyBG.setup("popup_empty_bg");
			
			occupiedBG = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
			occupiedBG.setup(width, height);
			occupiedBG.setup("popup_full_bg");
			updateBackground();
			
			cookAnimation = new FrameAnimationComponent(MIN_GAME_COMPONENT_Z_ORDER + 3);
			cookAnimation.setFillBefore(true);
			cookAnimation.setFillAfter(true);
			
			long duration = cookDuration / 12;
			cookAnimation.addFrame("store_mask_00", width, height, duration);
			cookAnimation.addFrame("store_mask_01", width, height, duration);
			cookAnimation.addFrame("store_mask_02", width, height, duration);
			cookAnimation.addFrame("store_mask_03", width, height, duration);
			cookAnimation.addFrame("store_mask_04", width, height, duration);
			cookAnimation.addFrame("store_mask_05", width, height, duration);
			cookAnimation.addFrame("store_mask_06", width, height, duration);
			cookAnimation.addFrame("store_mask_07", width, height, duration);
			cookAnimation.addFrame("store_mask_08", width, height, duration);
			cookAnimation.addFrame("store_mask_09", width, height, duration);
			cookAnimation.addFrame("store_mask_10", width, height, duration);
			cookAnimation.addFrame("store_mask_11", width, height, duration);
			
			this.cookDuration = cookDuration;
		}
		
		public void setTouchArea(float offsetLeft, float offsetTop, float offsetRight, float offsetBottom) {
			button = new ButtonComponent(MIN_GAME_COMPONENT_Z_ORDER + 4);
			final float touchWidth = box.width() - offsetLeft + offsetRight;
			final float touchHeight = box.height() - offsetTop + offsetBottom;
			button.setup(box.left + offsetLeft, box.top + offsetTop, touchWidth, touchHeight, box.left, box.top);
		}
		
		public State getState() {
			return state;
		}
		
		public int getFoodType() {
			if (food == null) {
				return Food.INVALID_TYPE;
			}
			return food.getFoodType();
		}
		
		@Override
		public void reset() {
			state = State.EMPTY;
			if (food != null) {
				remove(food);
				updateBackground();
			}
			super.reset();
		}
		
		public boolean isAvailableToAddAnotherFood() {
			if (state != State.EMPTY) {
				return false;
			}
			
			for (GameEvent event : gameEvents) {
				if (GameEvent.FOOD_MACHINE_ADD_FOOD == event.what) {
					return false;
				}
			}
			
			return true;
		}
		
		@Override
		protected boolean wantThisEvent(GameEvent event) {
			if (event.what == GameEvent.FOOD_CONSUMED && event.obj == this) {
				return true;
			} else if (event.what == GameEvent.FOOD_MACHINE_START_COOKING && event.obj == this) {
				return true;
			}
			return false;
		}
		
		@Override
		protected void handleGameEvent(GameEvent event) {
			switch (event.what) {
			case GameEvent.FOOD_MACHINE_ADD_FOOD:
				handleAddFood(event.arg1);
				break;
			case GameEvent.FOOD_MACHINE_START_COOKING:
				handleStartCooking();
				break;
			case GameEvent.BUTTON_UP:
				if (food != null) {
					GameEventSystem.scheduleEvent(GameEvent.FOOD_GENERATED, food.getFoodType(), this);
				}
				break;
			case GameEvent.FOOD_CONSUMED:
				handleFoodConsumed();
				break;
			case GameEvent.FOOD_DONE_COOKING:
				handleFoodDoneCooking();
				break;
			}
		}
		
		private void handleAddFood(int foodType) {
			//Log.d(TAG, "CookSlot::handleAddFood foodType:" + foodType);
			if (State.EMPTY != state || food != null) {
				if (Config.DEBUG_LOG) Log.e(TAG, "cannot handle add food, state:" + state + ", food:" + food);
				return;
			}
			
			FoodComponent component = new FoodComponent(MIN_GAME_COMPONENT_Z_ORDER + 2);
			component.setup(foodType, Size.Medium, cookDuration);
			add(component);
			
			this.food = component;
			updateBackground();
			
			cookAnimation.rewind();
			add(cookAnimation);
			remove(button);
			
			GameEventSystem.scheduleEvent(GameEvent.FOOD_MACHINE_SLOT_FOOD_ADDED, foodType, this);
			changeState(State.READY);
		}
		
		private void handleStartCooking() {
			//Log.d(TAG, "CookSlot::handleStartCooking");
			if (food == null) {
				Log.e(TAG, "cannot start cooking for I have no food:" + food);
				return;
			}
			
			if (food.cook()) {
				cookAnimation.start();
				changeState(State.COOKING);
			}
		}
		
		private void handleFoodConsumed() {
			if (food == null) {
				Log.e(TAG, "cannot handle food consumed for I have no food:" + food);
				return;
			}
			
			remove(food);
			food = null;
			remove(button);
			updateBackground();
			
			changeState(State.EMPTY);
		}
		
		private void handleFoodDoneCooking() {
			cookAnimation.stop();
			remove(cookAnimation);
			add(button);
			
			changeState(State.DONE);
		}
		
		private void updateBackground() {
			if (food == null) {
				add(emptyBG);
				remove(occupiedBG);
			} else {
				add(occupiedBG);
				remove(emptyBG);
			}
		}
		
		private void changeState(State newState) {
			if (state != newState) {
				state = newState;
				host.onSlotStateChanged(this);
			}
		}
		
		public State state = State.EMPTY;
		private FoodMachine host;
		
		public RenderComponent emptyBG, occupiedBG;
		public FrameAnimationComponent cookAnimation;
		public ButtonComponent button;
		public FoodComponent food;
		
		private int cookDuration;
	}

}
