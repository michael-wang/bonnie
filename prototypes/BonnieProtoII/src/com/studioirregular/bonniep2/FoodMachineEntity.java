package com.studioirregular.bonniep2;

import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import com.studioirregular.bonnie.foodsystem.FoodSystem.Food;

public abstract class FoodMachineEntity extends BasicEntity implements FoodMachine {

	private static final boolean DO_LOG = false;
	private static final String TAG = "food-machine-entity";
	
	public FoodMachineEntity(SceneBase scene, String id, int level) {
		super(scene, id);
		
		this.id = id;
		this.level = level;
		
		Food[] foods = getCookableFoods();
		
		if (DO_LOG) Log.d(TAG, "FoodMachineEntity #foods:" + foods.length);
		this.foods = new CookableFoodComponent[foods.length];
		for (int i = 0; i < foods.length; i++) {
			this.foods[i] = (CookableFoodComponent)FoodComponent.newInstance(foods[i]);
		}
	}
	
	protected abstract Food[] getCookableFoods();
	protected abstract void getTableItemBox(RectF box);
	protected abstract String getTableItemStopTextureId();
	protected abstract String[] getTableItemWorkingTextureIds();
	
	protected void setup() {
		cooking = false;
		machineVisible = false;
		
		RectF box = new RectF();
		
		// table item (stop)
		GLTexture texture = TextureSystem.getInstance().getPart(getTableItemStopTextureId());
		getTableItemBox(box);
		tableItemOff = new RenderComponent(TABLE_ITEM_RENDER_STATIC_ID, box.left, box.top, box.width(), box.height(), texture);
		add(tableItemOff);
		
		// table item (cooking)
		tableItemCooking = new FrameAnimationComponent(TABLE_ITEM_RENDER_COOKING_ID, box.left, box.top, box.width(), box.height(), true, this);
		String[] textureIds = getTableItemWorkingTextureIds();
		for (String texId : textureIds) {
			tableItemCooking.addFrame(TextureSystem.getInstance().getPart(texId), COOK_ANIMATION_FRAME_DURATION);
		}
		tableItemCooking.setVisible(false);
		add(tableItemCooking);
		
		tableItemButton = new ButtonComponent(TABLE_ITEM_BUTTON_ID, this, box.left, box.top, box.width(), box.height());
		add(tableItemButton);
		
		// machine background
		getMachineBackground(box);
		machineBox.set(box);
		
		texture = TextureSystem.getInstance().getPart(getMachineBackgroundTextureId());
		machineBg = new RenderComponent(MACHINE_BACKGROUND_ID, box.left, box.top, box.width(), box.height(), texture);
		
		// food buttons
		foodButtons = new FoodButtonEntity[foods.length];
		for (int i = 0; i < foods.length; i++) {
			box = getFoodButtonBox(i, box);
			if (DO_LOG) Log.d(TAG, "foods[" + i + "]:" + foods[i] + ", box:" + box);
			foodButtons[i] = new FoodButtonEntity(scene, id + "-food-button", foods[i].getType(), this, box);
		}
		
		// cook slots
		slots = new FoodMachineSlotEntity[getSlotCount()];
		synchronized (slots) {
			for (int i = 0; i < getSlotCount(); i++) {
				getSlotBox(i, box);
				slots[i] = new FoodMachineSlotEntity(scene, id + "-food-slot-" + i, this, box, i);
			}
		}
	}
	
	@Override
	public synchronized void requestCook(FoodButtonEntity button) {
		if (DO_LOG) Log.d(TAG, "requestCook foodType:" + button.getFoodType());
		
		for (int i = 0; i < slots.length; i++) {
			if (slots[i].isAvailable()) {
				FoodComponent food = FoodComponent.newInstance(button.getFoodType());
				if (food instanceof CookableFoodComponent) {
					slots[i].requestAdd((CookableFoodComponent)food);
					break;
				}
			}
		}
		
		if (!cooking) {
			// find first available slot to cook
			for (int i = 0; i < slots.length; i++) {
				if (slots[i].isAvailable() == false && slots[i].getFood().doneCooking() == false) {
					slots[i].requestCook();
				}
			}
			
			turnOn();
		}
	}
	
	@Override
	public void requestHideMachine(FoodMachineSlotEntity slot) {
		if (DO_LOG) Log.d(TAG, "requestHideMachine machineVisible:" + machineVisible);
		
		if (machineVisible) {
			hideMachine();
		}
	}
	
	@Override
	public synchronized void onFoodReady(FoodMachineSlotEntity slot) {
		if (DO_LOG) Log.d(TAG, "onFoodReady slot:" + slot.slotIndex);
		
		boolean allSlotsDone = true;
		for (int i = 0; i < slots.length; i++) {
			if (slots[i].isAvailable() == false && slots[i].getFood().doneCooking() == false) {
				slots[i].requestCook();
				allSlotsDone = false;
				break;
			}
		}
		
		if (allSlotsDone) {
			cooking = false;
			turnOff();
		}
	}
	
	@Override
	public synchronized void onSlotEmpty(FoodMachineSlotEntity slot) {
	}
	
	@Override
	public void onEvent(Event event) {
		super.onEvent(event);
		if (DO_LOG) Log.d(TAG, "onEvent event:" + event);
		
		if (event.what == ComponentEvent.ENTITY_ALIVE) {
			setup();
		} else if (event.what == ComponentEvent.BUTTON_UP) {
			if (!machineVisible) {
				showMachine();
			}
		}
	}
	
	// TODO: encapsulate this with component
	@Override
	public boolean onTouch(MotionEvent event) {
		if (!machineVisible) {
			return tableItemButton.onTouch(event);
		}
		
		super.onTouch(event);
		
		for (FoodButtonEntity button : foodButtons) {
			if (button.onTouch(event)) {
				return true;
			}
		}
		for (FoodMachineSlotEntity slot : slots) {
			if (slot.onTouch(event)) {
				return true;
			}
		}
		
		final int action = event.getAction();
		final float x = event.getX();
		final float y = event.getY();
		
		if (action == MotionEvent.ACTION_UP) {
			if (!machineBox.contains(x, y)) {
				if (DO_LOG) Log.d(TAG, "tableItemBox:" + machineBox + ", x:" + x + ",y:" + y);
				hideMachine();
			}
		}
		return true;	// eat up all touch events
	}
	
	@Override
	public void update(long timeDiff) {
		super.update(timeDiff);
		
		if (slots == null) {
			return;
		}
		
		if (!machineVisible) {
			synchronized (slots) {
				for (int i = 0; i < slots.length; i++) {
					slots[i].update(timeDiff);
				}
			}
		}
		
	}
	
	protected void showMachine() {
		if (DO_LOG) Log.d(TAG, "showMachine");
		assert (machineVisible == false);
		
		add(machineBg);
		for (FoodButtonEntity button : foodButtons) {
			scene.addEntity(button);
		}
		for (FoodMachineSlotEntity slot : slots) {
			scene.addEntity(slot);
		}
		machineVisible = true;
	}
	
	protected void hideMachine() {
		if (DO_LOG) Log.d(TAG, "hideMachine");
		assert (machineVisible == true);
		
		machineVisible = false;
		
		for (FoodMachineSlotEntity slot : slots) {
			scene.removeEntity(slot);
		}
		for (FoodButtonEntity button : foodButtons) {
			scene.removeEntity(button);
		}
		this.remove(machineBg.id);
	}
	
	protected void turnOn() {
		if (DO_LOG) Log.d(TAG, "turnOn");
		assert (cooking == false);
		
		tableItemCooking.start();
		tableItemCooking.setVisible(true);
		
		tableItemOff.setVisible(false);
		
		cooking = true;
	}
	
	protected void turnOff() {
		if (DO_LOG) Log.d(TAG, "turnOff");
		assert (cooking == true);
		
		tableItemOff.setVisible(true);
		
		tableItemCooking.stop();
		tableItemCooking.setVisible(false);
		
		cooking = false;
	}
	
	protected synchronized CookableFoodComponent getLastAvailableFood() {
		CookableFoodComponent result = null;
		
		final int count = slots.length;
		for (int i = count-1; i >= 0; i--) {
			CookableFoodComponent food = slots[i].getFood();
			if (food != null && food.doneCooking()) {
				result = food;
				break;
			}
		}
		
		return result;
	}
	
	protected synchronized CookableFoodComponent getCookingFood() {
		CookableFoodComponent result = null;
		
		final int count = slots.length;
		for (int i = 0; i < count; i++) {
			CookableFoodComponent food = slots[i].getFood();
			if (food != null && food.isCooking()) {
				result = food;
				break;
			}
		}
		
		return result;
	}
	
	private String getMachineBackgroundTextureId() {
		if (level <= 2) {
			return "popup_three_bg";
		} else if (level == 3) {
			return "popup_five_bg";
		}
		return "";
	}
	
	private static final int MEDIUM_MACHINE_LEFT_POSITION = 244;
	private static final int MEDIUM_MACHINE_TOP_POSITION = 181;
	private static final int MEDIUM_MACHINE_WIDTH = 233;
	private static final int MEDIUM_MACHINE_HEIGHT = 222;
	private static final int MEDIUM_MACHINE_FOOD_BUTTON_STEP_X = 100;
	
	private static final int LARGE_MACHINE_LEFT_POSITION = 180;
	private static final int LARGE_MACHINE_TOP_POSITION = 181;
	private static final int LARGE_MACHINE_WIDTH = 362;
	private static final int LARGE_MACHINE_HEIGHT = 222;
	private static final int LARGE_MACHINE_FOOD_BUTTON_STEP_X = 110;
	
	private static final int FOOD_BUTTON_WIDTH = 90;
	private static final int FOOD_BUTTON_HEIGHT = 90;
	
	private void getMachineBackground(RectF box) {
		if (level <= 2) {
			box.set(MEDIUM_MACHINE_LEFT_POSITION, MEDIUM_MACHINE_TOP_POSITION,
					MEDIUM_MACHINE_LEFT_POSITION + MEDIUM_MACHINE_WIDTH,
					MEDIUM_MACHINE_TOP_POSITION + MEDIUM_MACHINE_HEIGHT);
		} else if (level == 3) {
			box.set(LARGE_MACHINE_LEFT_POSITION, LARGE_MACHINE_TOP_POSITION,
					LARGE_MACHINE_LEFT_POSITION + LARGE_MACHINE_WIDTH,
					LARGE_MACHINE_TOP_POSITION + LARGE_MACHINE_HEIGHT);
		}
	}
	
	private RectF getFoodButtonBox(int index, RectF box) {
		if (level <= 2) {
			box.set(MEDIUM_MACHINE_LEFT_POSITION + 16 + MEDIUM_MACHINE_FOOD_BUTTON_STEP_X * index,
					MEDIUM_MACHINE_TOP_POSITION,
					MEDIUM_MACHINE_LEFT_POSITION + 16 + MEDIUM_MACHINE_FOOD_BUTTON_STEP_X * index + FOOD_BUTTON_WIDTH,
					MEDIUM_MACHINE_TOP_POSITION + FOOD_BUTTON_HEIGHT);
			return box;
		} else if (level == 3) {
			box.set(LARGE_MACHINE_LEFT_POSITION + 16 + LARGE_MACHINE_FOOD_BUTTON_STEP_X * index,
					LARGE_MACHINE_TOP_POSITION, 
					LARGE_MACHINE_LEFT_POSITION + 16 + LARGE_MACHINE_FOOD_BUTTON_STEP_X * index + FOOD_BUTTON_WIDTH, 
					LARGE_MACHINE_TOP_POSITION + FOOD_BUTTON_HEIGHT);
			return box;
		}
		return box;
	}
	
	private static final int THREE_SLOTS_X_START = 260;
	private static final int THREE_SLOTS_Y_START = 321;
	private static final int THREE_SLOTS_X_STEP = 67;
	
	private static final int FIVE_SLOTS_X_START = 193;
	private static final int FIVE_SLOTS_Y_START = 321;
	private static final int FIVE_SLOTS_X_STEP = 67;
	
	private static final int SLOT_WIDTH = 67;
	private static final int SLOT_HEIGHT = 67;
	
	private int getSlotCount() {
		if (level == 1) {
			return 2;
		} else if (level == 2) {
			return 3;
		} else if (level == 3) {
			return 5;
		}
		return 0;
	}
	
	private RectF getSlotBox(int index, RectF box) {
		if (level <= 2) {
			box.set(THREE_SLOTS_X_START + THREE_SLOTS_X_STEP * index,
					THREE_SLOTS_Y_START, 
					THREE_SLOTS_X_START + THREE_SLOTS_X_STEP * index + SLOT_WIDTH,
					THREE_SLOTS_Y_START + SLOT_HEIGHT);
			return box;
		} if (level == 3) {
			box.set(FIVE_SLOTS_X_START + FIVE_SLOTS_X_STEP * index,
					FIVE_SLOTS_Y_START, 
					FIVE_SLOTS_X_START + FIVE_SLOTS_X_STEP * index + SLOT_WIDTH, 
					FIVE_SLOTS_Y_START + SLOT_HEIGHT);
			return box;
		}
		return null;
	}
	
	protected static final String TABLE_ITEM_RENDER_STATIC_ID = "render-static";
	protected static final String TABLE_ITEM_RENDER_COOKING_ID = "render-working";
	protected static final String TABLE_ITEM_BUTTON_ID = "button";
	protected static final String MACHINE_BACKGROUND_ID = "render-machine";
	
	protected static final long COOK_ANIMATION_FRAME_DURATION = 500;
	
	protected String id;
	protected int level;
	protected CookableFoodComponent[] foods;
	
	protected boolean cooking = false;
	protected RenderComponent tableItemOff;
	protected FrameAnimationComponent tableItemCooking;
	private ButtonComponent tableItemButton;
	private ScalingAnimationComponent clickResponse;
	
	private boolean machineVisible = false;
	private RenderComponent machineBg;
	private FoodButtonEntity[] foodButtons;
	private FoodMachineSlotEntity[] slots;
	
	protected RectF machineBox = new RectF();	// dirty guy

}
