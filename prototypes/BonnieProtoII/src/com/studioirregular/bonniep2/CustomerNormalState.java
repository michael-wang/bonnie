package com.studioirregular.bonniep2;

import android.util.Log;

public class CustomerNormalState extends CustomerMoodState {

	private static final String TAG = "customer-mood-state-normal";
	
	public CustomerNormalState(CustomerEntity entity, FrameAnimationComponent animation) {
		super(entity, animation);
	}
	
	@Override
	public void update(long timeDiff) {
		if (entity.getCustomer().isWaiting()) {
			final int patience = entity.getCustomer().getCurrentPatience();
			if (patience <= PATIENCE_THRESHOLD_UNHAPPY) {
				if (CustomerEntity.DO_LOG) Log.d(TAG, "update: patience:" + patience);
				beforeSwitchToUnhappy();
				entity.setMoodState(entity.obtainMoodState(CustomerUnhappyState.class));
			}
		}
	}
	
	@Override
	public void receivingFood(FoodProductEntity food) {
		super.receivingFood(food);
		
		if (!(food instanceof FoodCandyEntity)) {
			CustomerMoodState newState = entity.obtainMoodState(CustomerExcitedState.class);
			entity.setMoodState(newState);
		}
	}
	
	protected void beforeSwitchToUnhappy() {
		if (entity.getCustomer().customerType == CustomerComponent.SUPERSTAR_LADY) {
			if (entity.getCustomer().isWaiting()) {
				entity.changeMind();	// super star lady changes order when she's unhappy!
			}
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
