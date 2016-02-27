package com.studioirregular.bonniep2;


public class AddButtonEntityCommand extends AddEntityCommand {

	public AddButtonEntityCommand(SceneBase scene, String id) {
		this(scene, id, null);
	}
	
	public AddButtonEntityCommand(SceneBase scene, String id, String afterEntityId) {
		super(scene, id, afterEntityId);
	}
	
	@Override
	protected Entity createEntity() {
		return new ButtonEntity(scene, entityId);
	}
	
}
