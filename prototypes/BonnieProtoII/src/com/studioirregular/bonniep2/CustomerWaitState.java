package com.studioirregular.bonniep2;

import android.util.Log;

public class CustomerWaitState extends CustomerActionState {

	private static final String TAG = "customer-action-state-wait";
	
	public CustomerWaitState(CustomerEntity entity) {
		super(entity);
	}
	
	@Override
	public void update(long timeDiff) {
		super.update(timeDiff);
		
		CustomerComponent customer = entity.getCustomer();
		if (customer.isWaiting()) {
			final int patience = entity.getCustomer().getCurrentPatience();
			if (patience <= 0) {
				stopWaitingAndWalkOut();
			}
		}
	}
	
	@Override
	public void onEvent(Event event) {
		super.onEvent(event);
		
		if (event.what == ComponentEvent.CUSTOMER_PHYSICIST_CHANGE_THINKING) {
			CustomerComponent customer = entity.getCustomer();
			if (customer instanceof CustomerPhysicistComponent) {
				CustomerPhysicistComponent physicist = (CustomerPhysicistComponent)customer;
				if (physicist.isThinkingPhysics()) {
					entity.getExpectedFood().setVisible(false);
					entity.getOrderBubble().startThinkingPhysics();
				} else {
					entity.getOrderBubble().stopThinkingPhysics();
					entity.getExpectedFood().setVisible(true);
				}
			}
		} else if (event.what == EntityEvent.FOOD_CRITIC_WALKOUT_ANGRILY) {
			entity.astonished();
			stopWaitingAndWalkOut();
		}
	}
	
	@Override
	public void receivingFood(FoodProductEntity food) {
		if (CustomerEntity.DO_LOG) Log.d(TAG, "receivingFood food:" + food + ", while exepected:" + entity.getExpectedFood());
		
		if (!(food instanceof FoodCandyEntity)) {
			entity.stopWaiting();
			
			entity.setActionState(entity.obtainActionState(CustomerWalkState.class));
			entity.walkOut();
		}
	}
	
	@Override
	public boolean doYouWantToConsumeIt(FoodProductEntity food) {
		if (food instanceof FoodCandyEntity) {
			return true;
		}
		
		FoodProductEntity expected = entity.getExpectedFood();
		if (expected != null && expected.isSameFood(food)) {
			return true;
		}
		
		if (CustomerEntity.DO_LOG) Log.w(TAG, "doYouWantToConsumeIt rejected food:" + food + ", expected:" + expected);
		return false;
	}
	
	@Override
	public FoodProductEntity changeMind() {
		return makeDecision();
	}
	
	@Override
	public void stopWaiting() {
		entity.getCustomer().stopWaiting();
		
		entity.getScene().removeEntity(getOrderBubbleId());
		entity.setOrderBubble(null);
	}
	
	protected void stopWaitingAndWalkOut() {
		entity.stopWaiting();
		
		entity.setActionState(entity.obtainActionState(CustomerWalkState.class));
		entity.walkOut();
	}
}
