package com.studioirregular.bonniep2;

import com.studioirregular.bonniep2.FoodProductHolder.Size;

public class FoodCandyComponent extends FoodComponent {

	public FoodCandyComponent(String id, Type type) {
		super(id);
		this.type = type;
	}
	
	@Override
	public String getTextureId(Size size) {
		if (type == Type.Candy1) {
			return "game_table_candy_candy_1";
		} else if (type == Type.Candy2) {
			return "game_table_candy_candy_2";
		} else if (type == Type.Candy3) {
			return "game_table_candy_candy_3";
		}
		return "";
	}

	@Override
	public int getPrice() {
		return 0;
	}
	
	@Override
	public boolean isCombinable(FoodComponent food) {
		return false;
	}
	
	@Override
	protected Category getCategory() {
		return Category.Candy;
	}

	@Override
	protected Type getType() {
		return type;
	}
	
	private Type type;

}
