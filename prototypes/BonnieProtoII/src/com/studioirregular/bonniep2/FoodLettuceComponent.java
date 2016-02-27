package com.studioirregular.bonniep2;

import com.studioirregular.bonniep2.FoodProductHolder.Size;

public class FoodLettuceComponent extends FoodComponent {

	public FoodLettuceComponent(String id) {
		super(id);
	}
	
	@Override
	public String getTextureId(Size type) {
		if (type == Size.Normal) {
			return "food_5_lettuce_b";
		} else if (type == Size.Medium) {
			return "food_5_lettuce_m";
		}
		return "";
	}

	@Override
	public int getPrice() {
		return 100;
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
		return Type.Lettuce;
	}

}
