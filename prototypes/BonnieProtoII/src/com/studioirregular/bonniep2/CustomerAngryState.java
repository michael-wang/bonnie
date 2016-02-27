package com.studioirregular.bonniep2;



public class CustomerAngryState extends CustomerMoodState {

//	private static final String TAG = "customer-mood-state-angry";
	
	public CustomerAngryState(CustomerEntity entity, FrameAnimationComponent animation) {
		super(entity, animation);
	}
	
	@Override
	public void update(long timeDiff) {
		if (entity.getCustomer().isWaiting()) {
			int patience = entity.getCustomer().getCurrentPatience();
			if (patience > 1) {
				entity.setMoodState(entity.obtainMoodState(CustomerUnhappyState.class));
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
