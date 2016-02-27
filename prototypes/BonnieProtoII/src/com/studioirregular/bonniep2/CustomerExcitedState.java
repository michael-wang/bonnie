package com.studioirregular.bonniep2;

import android.util.Log;


public class CustomerExcitedState extends CustomerMoodState {

	private static final String TAG = "customer-mood-state-excited";
	
	public CustomerExcitedState(CustomerEntity entity, FrameAnimationComponent animation) {
		super(entity, animation);
	}
	
	@Override
	public void onStateActivated(boolean temporarily) {
		super.onStateActivated(temporarily);
		if (CustomerEntity.DO_LOG) Log.w(TAG, "onStateActivated");
		
		if (!temporarily) {
			if (entity.getCustomer().customerType == CustomerComponent.FOOD_CRITIC) {
				((SceneBase)entity.getScene()).send(entity, new EntityEvent(EntityEvent.FOOD_CRITIC_EXCITED, entity.getId()));
			}
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
