package com.studioirregular.bonniep2;

import com.studioirregular.bonniep2.FoodProductHolder.Size;

public class FoodEggComponent extends CookableFoodComponent {

	public FoodEggComponent(String id) {
		super(id);
	}

	@Override
	public String getTextureId(Size type) {
		if (type == Size.Normal) {
			return "food_4_egg_b";
		} else if (type == Size.Medium) {
			return "food_4_egg_m";
		}
		return "";
	}

	@Override
	public int getPrice() {
		return 100;
	}

	@Override
	public boolean isCombinable(FoodComponent food) {
		if (food.getCategory() == this.getCategory()) {
			return false;
		} else if (Category.Bread == food.getCategory()) {
			return false;
		} else if (Category.Muffin == food.getCategory()) {
			return false;
		}
		return true;
	}

	@Override
	protected Category getCategory() {
		return Category.AddOnB;
	}

	@Override
	protected Type getType() {
		return Type.Egg;
	}

}
