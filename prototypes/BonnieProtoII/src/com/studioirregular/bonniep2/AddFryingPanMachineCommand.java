package com.studioirregular.bonniep2;


public class AddFryingPanMachineCommand extends AddEntityCommand {
	
	public AddFryingPanMachineCommand(SceneBase scene, String id, int level) {
		super(scene, id);
		this.level = level;
	}
	
	@Override
	protected Entity createEntity() {
		return new FoodMachineFryingPanEntity(scene, entityId, level);
	}
	
	private int level;

}
