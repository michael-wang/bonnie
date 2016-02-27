package com.studioirregular.bonniep2;

import com.studioirregular.bonniep2.FoodProductHolder.Size;

public class FoodToastBlackComponent extends CookableFoodComponent implements FoodToast {

	public FoodToastBlackComponent(String id) {
		super(id);
	}
	
	@Override
	public String getTextureId(Size type) {
		if (type == Size.Normal) {
			return "food_1_toast_black_double_b";
		} else if (type == Size.Medium) {
			return "food_1_toast_black_double_m";
		}
		return "";
	}
	
	@Override
	public int getPrice() {
		return 300;
	}
	
	@Override
	public boolean isCombinable(FoodComponent food) {
		if (food.isMainIngredient()) {
			return false;
		}
		return true;

	}
	
	@Override
	protected Category getCategory() {
		return Category.Toast;
	}
	
	@Override
	protected Type getType() {
		return Type.ToastBlack;
	}

	@Override
	public boolean isClosed() {
		return closed;
	}
	
	@Override
	public void open() {
		closed = false;
	}
	
	@Override
	public void close() {
		closed = true;
	}

	@Override
	public String getBottomTextureId(Size size) {
		if (size == Size.Normal) {
			return "food_1_toast_black_b";
		} else if (size == Size.Medium) {
			return "food_1_toast_black_m";
		}
		return "";
	}
	
	@Override
	public String getTopTextureId(Size size) {
		if (size == Size.Normal) {
			return "food_1_toast_black_up_b";
		} else if (size == Size.Medium) {
			return "food_1_toast_black_up_m";
		}
		return "";
	}
	
	private boolean closed = true;

}
