package com.studioirregular.bonniep2;

import android.util.Log;

public class SetTutorialModeCommand implements Command {

	private static final String TAG = "set_tutorial_mode-command";
	
	private Scene scene;
	private String entityId;
	private boolean tutorialMode;
	
	public SetTutorialModeCommand(Scene scene, String entityId, boolean tutorialMode) {
		this.scene = scene;
		this.entityId = entityId;
		this.tutorialMode = tutorialMode;
	}
	
	@Override
	public void execute() {
		Entity entity = scene.getEntity(entityId);
		if (entity == null) {
			Log.w(TAG, "execute cannot find entity:" + entityId);
			return;
		} else if (!(entity instanceof CustomerEntity)) {
			Log.w(TAG, "execute entity (id:" + entityId + ") not instanceof CustomerEntity:" + entity);
			return;
		}
		
		CustomerComponent customer = ((CustomerEntity)entity).getCustomer();
		if (customer == null) {
			Log.w(TAG, "execute no customer component in entity:" + entity);
			return;
		}
		
		customer.setTutorialMode(tutorialMode);
	}

}
