package com.studioirregular.bonniep2;

import android.graphics.RectF;
import android.util.Log;

import com.studioirregular.bonnie.foodsystem.FoodSystem.Food;
import com.studioirregular.bonniep2.FoodProductHolder.Size;

public class FoodMachineMuffinEntity extends FoodMachineEntity {

	private static final boolean DO_LOG = false;
	private static final String TAG = "food-machine-muffin-entity";
	
	public FoodMachineMuffinEntity(SceneBase scene, String id, int level) {
		super(scene, id, level);
		
		if (DO_LOG) Log.d(TAG, "FoodMachineMuffinEntity id:" + id + ",level:" + level);
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
	}
	
	@Override
	protected Food[] getCookableFoods() {
		if (level == 1) {
			return new Food[] { Food.getMuffinCircle() };
		} else if (level == 2) {
			return new Food[] { Food.getMuffinCircle(), Food.getMuffinSquare() };
		}
		return new Food[0];
	}
	
	@Override
	protected void getTableItemBox(RectF box) {
		box.set(201, 228, 201 + 92, 228 + 135);
	}
	
	@Override
	protected String getTableItemStopTextureId() {
		if (level == 1) {
			return "game_table_waffle_lv1_normal";
		} else if (level == 2) {
			return "game_table_waffle_lv2_normal";
		}
		return "";
	}

	@Override
	protected String[] getTableItemWorkingTextureIds() {
		if (level == 1) {
			return new String[] { "game_table_waffle_lv1_work_1", "game_table_waffle_lv1_work_2" };
		} else if (level == 2) {
			return new String[] { "game_table_waffle_lv2_work_1", "game_table_waffle_lv2_work_2" };
		}
		return new String[0];
	}
	
	@Override
	protected void turnOn() {
		super.turnOn();
		foodRender.setVisible(false);
	}
	
	@Override
	protected void turnOff() {
		super.turnOff();
		updateFoodRender();
	}
	
	@Override
	public synchronized void onSlotEmpty(FoodMachineSlotEntity slot) {
		super.onSlotEmpty(slot);
		if (!cooking) {
			updateFoodRender();
		}
	}
	
	private void updateFoodRender() {
		CookableFoodComponent food = getLastAvailableFood();
		if (DO_LOG) Log.d(TAG, "updateFoodRender food:" + food + ", foodRender:" + foodRender);
		
		if (food == null) {
			foodRender.setVisible(false);
		} else {
			GLTexture texture = TextureSystem.getInstance().getPart(food.getTextureId(Size.Medium));
			foodRender.updateTexture(texture);
			foodRender.setVisible(true);
		}
	}
	
	private static final float FOOD_WIDTH = 69;
	private static final float FOOD_HEIGHT = 69;
	
	private static final int FOOD_TO_TABLE_ITEM_OFFSET_Y = 18;
	private RenderComponent foodRender;
	
}
