package com.studioirregular.bonniep2;

import android.util.Log;

public class StartAnimationCommand implements Command {

	private static final String TAG = "start-animatin-command";
	
	private Scene scene;
	private String entityId;
	private String componentId;
	
	public StartAnimationCommand(Scene scene, String entityId, String componentId) {
		this.scene = scene;
		this.entityId = entityId;
		this.componentId = componentId;
	}
	
	@Override
	public void execute() {
		Entity entity = scene.getEntity(entityId);
		if (entity == null) {
			Log.e(TAG, "cannot find entity:" + entityId);
			return;
		}
		
		Component component = entity.getComponent(componentId);
		if (component == null) {
			Log.e(TAG, "cannot find component:" + componentId);
			return;
		}
		
		if (component instanceof Animation == false) {
			Log.e(TAG, "component is not Animation:" + component);
			return;
		}
		
		Animation animation = (Animation)component;
		animation.start();
	}

}
