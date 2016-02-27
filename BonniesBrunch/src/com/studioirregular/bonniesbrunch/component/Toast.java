package com.studioirregular.bonniesbrunch.component;

import android.util.Log;

import com.studioirregular.bonniesbrunch.FoodSystem.Food;
import com.studioirregular.bonniesbrunch.entity.BrunchHolder.Size;

public class Toast extends FoodComponent {

	private static final String TAG = "toast-bottom";
	
	public enum Type {
		Bottom,
		Top
	};
	
	public Toast(int zOrder) {
		super(zOrder);
	}
	
	public void setup(int food, Size size, Type type) {
		this.type = type;
		super.setup(food, size);
	}
	
	@Override
	protected String getTextureId(Size size) {
		if (type == Type.Bottom) {
			if (food == Food.TOAST_WHITE) {
				return (size == Size.Normal) ? "food_1_toast_white_b" : "food_1_toast_white_m";
			} else if (food == Food.TOAST_BLACK) {
				return (size == Size.Normal) ? "food_1_toast_black_b" : "food_1_toast_black_m";
			} else if (food == Food.TOAST_YELLOW) {
				return (size == Size.Normal) ? "food_1_toast_yellow_b" : "food_1_toast_yellow_m";
			}
		} else {	// Top
			if (food == Food.TOAST_WHITE) {
				return (size == Size.Normal) ? "food_1_toast_white_up_b" : "food_1_toast_white_up_m";
			} else if (food == Food.TOAST_BLACK) {
				return (size == Size.Normal) ? "food_1_toast_black_up_b" : "food_1_toast_black_up_m";
			} else if (food == Food.TOAST_YELLOW) {
				return (size == Size.Normal) ? "food_1_toast_yellow_up_b" : "food_1_toast_yellow_up_m";
			}
		}
		Log.w(TAG,"getTextureId unsupported food type:" + food);
		return "";
	}
	
	private Type type = Type.Bottom;

}
