package com.studioirregular.bonniep2;

public class AddFoodPlateCommand extends AddEntityCommand {

	public AddFoodPlateCommand(SceneBase scene, String id) {
		super(scene, id);
	}
	
	@Override
	protected Entity createEntity() {
		return new FoodPlateEntity(scene, entityId);
	}
	
}
