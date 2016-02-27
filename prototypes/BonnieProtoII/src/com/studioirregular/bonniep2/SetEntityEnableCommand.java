package com.studioirregular.bonniep2;

public class SetEntityEnableCommand implements Command {

	private Scene scene;
	private String entityId;
	private boolean enable;
	
	public SetEntityEnableCommand(Scene scene, String entityId, boolean enable) {
		this.scene = scene;
		this.entityId = entityId;
		this.enable = enable;
	}
	
	@Override
	public void execute() {
		Entity entity = scene.getEntity(entityId);
		if (entity != null) {
			entity.setEnable(enable);
		}
	}

}
