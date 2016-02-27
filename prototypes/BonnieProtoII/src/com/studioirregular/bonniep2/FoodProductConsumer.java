package com.studioirregular.bonniep2;

public interface FoodProductConsumer {

	public boolean doYouWantToConsumeIt(FoodProductEntity food);
	public void consumeIt(FoodProductEntity food);
}
