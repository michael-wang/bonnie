package com.studioirregular.bonniep2;

import com.studioirregular.bonniep2.FoodProductHolder.Size;

public class FoodCoffeeComponent extends FoodComponent {

	public FoodCoffeeComponent(String id) {
		super(id);
	}
	
	@Override
	public String getTextureId(Size type) {
		if (type == Size.Normal) {
			return "food_7_coffee_b";
		} else if (type == Size.Medium) {
			return "food_7_coffee_m";
		}
		return "";
	}
	
	@Override
	public int getPrice() {
		return 50;
	}
	
	@Override
	public boolean isCombinable(FoodComponent food) {
		if (food.getCategory() == Category.Beverage) {
			return false;
		}
		return true;
	}
	
	@Override
	protected Category getCategory() {
		return Category.Beverage;
	}
	
	@Override
	protected Type getType() {
		return Type.Coffee;
	}

}
