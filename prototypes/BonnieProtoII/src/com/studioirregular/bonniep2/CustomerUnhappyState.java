package com.studioirregular.bonniep2;



public class CustomerUnhappyState extends CustomerMoodState {

//	private static final String TAG = "customer-mood-state-unhappy";
	
	public CustomerUnhappyState(CustomerEntity entity, FrameAnimationComponent animation) {
		super(entity, animation);
	}
	
	@Override
	public void update(long timeDiff) {
		if (entity.getCustomer().isWaiting()) {
			final int patience = entity.getCustomer().getCurrentPatience();
			if (patience <= PATIENCE_THRESHOLD_ANGRY) {
				entity.setMoodState(entity.obtainMoodState(CustomerAngryState.class));
			} else if (patience > PATIENCE_THRESHOLD_UNHAPPY) {
				entity.setMoodState(entity.obtainMoodState(CustomerNormalState.class));
			}
		}
	}
	
	@Override
	public void receivingFood(FoodProductEntity food) {
		super.receivingFood(food);
		
		if (!(food instanceof FoodCandyEntity)) {
			entity.setMoodState(entity.obtainMoodState(CustomerNormalState.class));
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
