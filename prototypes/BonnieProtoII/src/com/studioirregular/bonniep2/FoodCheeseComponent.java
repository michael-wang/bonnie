package com.studioirregular.bonniep2;

import com.studioirregular.bonniep2.FoodProductHolder.Size;

public class FoodCheeseComponent extends FoodComponent {

	public FoodCheeseComponent(String id) {
		super(id);
	}
	
	@Override
	public String getTextureId(Size type) {
		if (type == Size.Normal) {
			return "food_5_cheese_b";
		} else if (type == Size.Medium) {
			return "food_5_cheese_m";
		}
		return "";
	}

	@Override
	public int getPrice() {
		return 50;
	}
	
	@Override
	public boolean isCombinable(FoodComponent food) {
		if (food.getCategory() == getCategory()) {
			return false;
		}
		return true;
	}
	
	@Override
	protected Category getCategory() {
		return Category.AddOnA;
	}
	
	@Override
	protected Type getType() {
		return Type.Cheese;
	}

}
