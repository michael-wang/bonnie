package com.studioirregular.bonniep2;

import com.studioirregular.bonniep2.FoodProductHolder.Size;

public class FoodBagelComponent extends FoodComponent {

	public FoodBagelComponent(String id) {
		super(id);
	}
	
	@Override
	public String getTextureId(Size type) {
		if (type == Size.Normal) {
			return "food_3_bagel_b";
		} else if (type == Size.Medium) {
			return "food_3_bagel_m";
		}
		return "";
	}

	@Override
	public int getPrice() {
		return 200;
	}
	
	@Override
	public boolean isCombinable(FoodComponent food) {
		if (Category.Sauce == food.getCategory()) {
			return true;
		} else if (Category.Beverage == food.getCategory()) {
			return true;
		}
		return false;
	}
	
	@Override
	protected Category getCategory() {
		return Category.Bread;
	}

	@Override
	protected Type getType() {
		return Type.Bagel;
	}
	
}
