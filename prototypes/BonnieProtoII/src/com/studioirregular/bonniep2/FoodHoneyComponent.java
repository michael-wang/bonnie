package com.studioirregular.bonniep2;

import com.studioirregular.bonniep2.FoodProductHolder.Size;

public class FoodHoneyComponent extends FoodComponent {

	public FoodHoneyComponent(String id) {
		super(id);
	}
	
	@Override
	public String getTextureId(Size type) {
		if (type == Size.Normal) {
			return "food_6_honey_b";
		} else if (type == Size.Medium) {
			return "food_6_honey_m";
		}
		return "";
	}
	
	@Override
	public int getPrice() {
		return 50;
	}
	
	@Override
	public boolean isCombinable(FoodComponent food) {
		if (food.getCategory() == Category.Sauce) {
			return false;
		}
		return true;
	}
	
	@Override
	protected Category getCategory() {
		return Category.Sauce;
	}
	
	@Override
	protected Type getType() {
		return Type.Honey;
	}

}
