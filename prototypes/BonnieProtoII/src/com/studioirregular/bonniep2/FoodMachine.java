package com.studioirregular.bonniep2;

public interface FoodMachine {
	public void requestCook(FoodButtonEntity button);
	public void requestHideMachine(FoodMachineSlotEntity slot);
	public void onFoodReady(FoodMachineSlotEntity slot);
	public void onSlotEmpty(FoodMachineSlotEntity slot);
}
