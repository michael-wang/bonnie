package com.studioirregular.bonniep2;

import android.util.Log;

public class CustomerTakeSeatCommand implements Command {

	private static final String TAG = "customer-take-seat-command";
	
	private SceneBase scene;
	private String entityId;
	private int seat;
	
	public CustomerTakeSeatCommand(SceneBase scene, String entityId, int seat) {
		this.scene = scene;
		this.entityId = entityId;
		this.seat = seat;
	}
	
	@Override
	public void execute() {
		Entity entity = scene.getEntity(entityId);
		if (entity == null || !(entity instanceof CustomerEntity)) {
			Log.e(TAG, "execute entity not found or not customer entity:" + entity);
			return;
		}
		
		CustomerEntity customer = (CustomerEntity)entity;
		customer.takeSeat(seat);
	}

}
