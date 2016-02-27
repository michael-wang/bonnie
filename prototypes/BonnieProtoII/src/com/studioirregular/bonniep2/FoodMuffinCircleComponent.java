package com.studioirregular.bonniep2;

import com.studioirregular.bonniep2.FoodProductHolder.Size;

public class FoodMuffinCircleComponent extends CookableFoodComponent {

	public FoodMuffinCircleComponent(String id) {
		super(id);
	}

	@Override
	public String getTextureId(Size type) {
		if (type == Size.Normal) {
			return "food_2_waffle_circular_b";
		} else if (type == Size.Medium) {
			return "food_2_waffle_circular_m";
		}
		return "";
	}

	@Override
	public int getPrice() {
		return 300;
	}

	@Override
	public boolean isCombinable(FoodComponent food) {
		if (food.getCategory() == Category.Sauce ||
				food.getCategory() == Category.Beverage) {
			return true;
		}
		return false;
	}

	@Override
	protected Category getCategory() {
		return Category.Muffin;
	}

	@Override
	protected Type getType() {
		return Type.MuffinCircle;
	}

}
