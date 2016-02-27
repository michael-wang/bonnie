package com.studioirregular.bonniep2;

import com.studioirregular.bonniep2.FoodComponent.Type;

public class AddFoodProducerCommand extends AddEntityCommand {

	protected Type foodType;
	
	public AddFoodProducerCommand(SceneBase scene, String id, Type foodType) {
		super(scene, id);
		this.foodType = foodType;
	}
	
	@Override
	protected Entity createEntity() {
		return new FoodProductProducerEntity(scene, entityId, foodType);
	}
	
}
