package com.studioirregular.bonniep2;

import com.studioirregular.bonnie.foodsystem.FoodSystem.FoodCombination;

public class AddCustomerEntityCommand extends AddEntityCommand {

	private int customerType;
	private FoodCombination preferredFood;
	
	public AddCustomerEntityCommand(SceneBase scene, String id, int customerType, FoodCombination preferredFood) {
		this(scene, id, null, customerType, preferredFood);
	}
	
	public AddCustomerEntityCommand(SceneBase scene, String id, String afterEntityId, int customerType, FoodCombination preferredFood) {
		super(scene, id, afterEntityId);
		this.customerType = customerType;
		this.preferredFood = preferredFood;
	}
	
	@Override
	protected BasicEntity createEntity() {
		return new CustomerEntity(scene, entityId, customerType, preferredFood);
	}
	
}
