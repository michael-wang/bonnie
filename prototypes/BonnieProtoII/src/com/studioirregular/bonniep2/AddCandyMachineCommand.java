package com.studioirregular.bonniep2;

public class AddCandyMachineCommand extends AddEntityCommand {

	public AddCandyMachineCommand(SceneBase scene, String id, int level) {
		super(scene, id);
		this.level = level;
	}

	@Override
	protected Entity createEntity() {
		return new CandyMachineEntity(scene, entityId, level);
	}
	
	private int level;

}
