package com.studioirregular.bonniep2;


public class AddToastMachineCommand extends AddEntityCommand{

	public AddToastMachineCommand(SceneBase scene, String id, int level) {
		super(scene, id);
		this.level = level;
	}
	
	@Override
	protected Entity createEntity() {
		return new FoodMachineToastEntity(scene, entityId, level);
	}
	
	private int level;

}
