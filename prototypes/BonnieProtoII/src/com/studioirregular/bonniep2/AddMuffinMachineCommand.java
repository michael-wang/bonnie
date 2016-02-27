package com.studioirregular.bonniep2;


public class AddMuffinMachineCommand extends AddEntityCommand {

	public AddMuffinMachineCommand(SceneBase scene, String id, int level) {
		super(scene, id);
		this.level = level;
	}
	
	@Override
	protected Entity createEntity() {
		return new FoodMachineMuffinEntity(scene, entityId, level);
	}
	
	private int level;

}
