package com.studioirregular.bonniep2;

import android.graphics.RectF;
import android.util.Log;

import com.studioirregular.bonnie.foodsystem.FoodSystem.Food;
import com.studioirregular.bonniep2.FoodProductHolder.Size;

public class FoodMachineFryingPanEntity extends FoodMachineEntity {

	private static final boolean DO_LOG = false;
	private static final String TAG = "food-machine-frying-pan-entity";
	
	public FoodMachineFryingPanEntity(SceneBase scene, String id, int level) {
		super(scene, id, level);
		if (DO_LOG) Log.d(TAG, "FoodMachineFryingPanEntity id:" + id + ",level:" + level);
	}
	
	@Override
	protected void setup() {
		super.setup();
		
		getTableItemBox(box);
		foodRender = new RenderComponent("render-cooking-food", 
				box.left + (box.width() - FOOD_WIDTH) / 2, 
				box.bottom - FOOD_HEIGHT - FOOD_TO_TABLE_ITEM_OFFSET_Y, 
				FOOD_WIDTH, FOOD_HEIGHT, null);
		add(foodRender);
		
		cookAnimation = new FrameAnimationComponent("animation-cook", 213, 326, 98, 101, true, this);
		cookAnimation.addFrame(TextureSystem.getInstance().getPart("game_table_pan_smoke_work_1"), COOK_ANIMATION_FRAME_DURATION);
		cookAnimation.addFrame(TextureSystem.getInstance().getPart("game_table_pan_smoke_work_2"), COOK_ANIMATION_FRAME_DURATION);
		cookAnimation.setVisible(false);
		add(cookAnimation);
	}
	
	@Override
	protected Food[] getCookableFoods() {
		if (level == 1) {
			return new Food[] { Food.getEgg() };
		} else if (level == 2) {
			return new Food[] { Food.getEgg(), Food.getHam() };
		} else if (level == 3) {
			return new Food[] { Food.getEgg(), Food.getHam(), Food.getSausage() };
		}
		return new Food[0];
	}
	
	@Override
	protected void getTableItemBox(RectF box) {
		box.set(195, 353, 195 + 131, 353 + 128);
	}

	@Override
	protected String getTableItemStopTextureId() {
		switch (level) {
		case 1:
			return "game_table_pan_lv1_normal";
		case 2:
			return "game_table_pan_lv2_normal";
		case 3:
			return "game_table_pan_lv3_normal";
		}
		return "";
	}

	@Override
	protected String[] getTableItemWorkingTextureIds() {
		if (level == 1) {
			return new String[] { "game_table_pan_lv1_work_1", "game_table_pan_lv1_work_2"};
		} else if (level == 2) {
			return new String[] { "game_table_pan_lv2_work_1", "game_table_pan_lv2_work_2"};
		} else if (level == 3) {
			return new String[] { "game_table_pan_lv3_work_1", "game_table_pan_lv3_work_2"};
		}
		return new String[0];
	}
	
	@Override
	protected void turnOn() {
		super.turnOn();
		updateFoodRender();
		
		cookAnimation.start();
		cookAnimation.setVisible(true);
	}
	
	@Override
	protected void turnOff() {
		super.turnOff();
		updateFoodRender();
		
		cookAnimation.setVisible(false);
		cookAnimation.stop();
	}
	
	@Override
	public synchronized void onSlotEmpty(FoodMachineSlotEntity slot) {
		super.onSlotEmpty(slot);
		updateFoodRender();
	}
	
	@Override
	public synchronized void onFoodReady(FoodMachineSlotEntity slot) {
		super.onFoodReady(slot);
		updateFoodRender();
	}
	
	private void updateFoodRender() {
		CookableFoodComponent food = getCookingFood();
		if (food == null) {
			food = getLastAvailableFood();
		}
		if (DO_LOG) Log.d(TAG, "updateFoodRender food:" + food + ", foodRender:" + foodRender);
		
		if (food == null) {
			foodRender.setVisible(false);
		} else {
			GLTexture texture = TextureSystem.getInstance().getPart(food.getTextureId(Size.Normal));
			foodRender.updateTexture(texture);
			foodRender.setVisible(true);
		}
	}
	
	private static final float FOOD_WIDTH = 90;
	private static final float FOOD_HEIGHT = 90;
	private static final int FOOD_TO_TABLE_ITEM_OFFSET_Y = 36;
	private RenderComponent foodRender;
	private FrameAnimationComponent cookAnimation;

}
