package com.studioirregular.bonniep2;

public class AddTrashcanEntityCommand extends AddEntityCommand {

	public AddTrashcanEntityCommand(SceneBase scene, String id) {
		super(scene, id);
	}
	
	@Override
	protected Entity createEntity() {
		return new TrashcanEntity(scene, entityId);
	}
	
}
