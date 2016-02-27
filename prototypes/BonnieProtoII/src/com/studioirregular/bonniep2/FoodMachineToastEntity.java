package com.studioirregular.bonniep2;

import android.graphics.RectF;

import com.studioirregular.bonnie.foodsystem.FoodSystem.Food;

public class FoodMachineToastEntity extends FoodMachineEntity {

//	private static final String TAG = "food-machine-toast-entity";
	
	public FoodMachineToastEntity(SceneBase scene, String id, int level) {
		super(scene, id, level);
	}
	
	@Override
	protected Food[] getCookableFoods() {
		if (level == 1) {
			return new Food[] { Food.getToastWhite() };
		} else if (level == 2) {
			return new Food[] { Food.getToastWhite(), Food.getToastBlack() };
		} else if (level == 3) {
			return new Food[] { Food.getToastWhite(), Food.getToastBlack(), Food.getToastYellow() };
		}
		return new Food[0];
	}
	
	@Override
	protected void getTableItemBox(RectF box) {
		box.set(293, 233, 293 + 116, 233 + 123);
	}
	
	@Override
	protected String getTableItemStopTextureId() {
		if (level == 1) {
			return "game_table_toast_lv1_normal";
		} else if (level == 2) {
			return "game_table_toast_lv2_normal";
		} else if (level == 3) {
			return "game_table_toast_lv3_normal";
		}
		return "";
	}

	@Override
	protected String[] getTableItemWorkingTextureIds() {
		if (level == 1) {
			return new String[] { "game_table_toast_lv1_work_1", "game_table_toast_lv1_work_2" };
		} else if (level == 2) {
			return new String[] { "game_table_toast_lv2_work_1", "game_table_toast_lv2_work_2" };
		} else if (level == 3) {
			return new String[] { "game_table_toast_lv3_work_1", "game_table_toast_lv3_work_2" };
		}
		return new String[0];
	}
	
}
