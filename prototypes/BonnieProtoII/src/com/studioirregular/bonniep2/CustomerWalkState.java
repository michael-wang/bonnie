package com.studioirregular.bonniep2;

import android.util.Log;

public class CustomerWalkState extends CustomerActionState {

	public static final String ANIMATION_WALK_IN_NAME	= "translate-walk-in";
	public static final String ANIMATION_WALK_OUT_NAME	= "translate-walk-out";
	
	private static final String TAG = "customer-action-state-walk";
	
	public CustomerWalkState(CustomerEntity entity) {
		super(entity);
	}
	
	@Override
	public void onEvent(Event event) {
		if (CustomerEntity.DO_LOG) Log.d(TAG, "onEvent event:" + event);
		
		switch (event.what) {
		case ComponentEvent.ANIMATION_ENDED:
			if (((ComponentEvent)event).getComponentId().equals(ANIMATION_WALK_IN_NAME)) {
				entity.setActionState(entity.obtainActionState(CustomerOrderState.class));
				entity.startOrderingFood();
			} else if (((ComponentEvent)event).getComponentId().equals(ANIMATION_WALK_OUT_NAME)) {
				entity.onCustomerLeave();
			}
			break;
		}
	}
	
	@Override
	public void startMovingToSeat() {
		if (CustomerEntity.DO_LOG) Log.d(TAG, "startMovingToSeat");
		int seat = entity.getSeat();
		CustomerComponent customer = entity.getCustomer();
		
		float distance = 720 - CustomerSeat.getCustomerX(seat);
		long duration = (long) (distance * 1000 / customer.movingSpeed);
		if (CustomerEntity.DO_LOG) Log.d(TAG, "startMovingToSeat: distance:" + distance + ", duration:" + duration);
		
		TranslateAnimationComponent translation = new TranslateAnimationComponent(
				entity, entity, ANIMATION_WALK_IN_NAME, 720,
				CustomerSeat.getCustomerX(seat), CustomerSeat.getCustomerY(seat),
				CustomerSeat.getCustomerY(seat), duration, false);
		// remember to kick start animation
		translation.start();
		
		entity.add(translation);
	}

	@Override
	public void walkOut() {
		GLRenderable render = entity.getRenderable(0);
		float translateToX = 0 - (render == null ? 0 : render.getWidth());
		
		int seat = entity.getSeat();
		float distance = CustomerSeat.getCustomerX(entity.getSeat()) + Math.abs(translateToX);
		
		CustomerComponent customer = entity.getCustomer();
		long duration = (long) (distance * 1000 / customer.movingSpeed);
		TranslateAnimationComponent translation = new TranslateAnimationComponent(
				entity, entity, ANIMATION_WALK_OUT_NAME,
				CustomerSeat.getCustomerX(seat), translateToX,
				CustomerSeat.getCustomerY(seat),
				CustomerSeat.getCustomerY(seat), duration, false);
		translation.start();
		
		entity.add(translation);
		
		CustomerSeat.leaveSeat(entity.getSeat());
	}
	
	@Override
	public FoodProductEntity makeOrder() {
		Log.e(TAG, "makeOrder");
		return super.makeOrder();
	}
	
}
