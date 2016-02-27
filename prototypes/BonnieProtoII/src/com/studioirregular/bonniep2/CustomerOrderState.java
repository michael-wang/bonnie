package com.studioirregular.bonniep2;

import android.util.Log;


public class CustomerOrderState extends CustomerActionState {

	private static final String TAG = "customer-action-state-order";
	
	private long elapsedTime;
	
	public CustomerOrderState(CustomerEntity entity) {
		super(entity);
	}
	
	@Override
	public void update(long timeDiff) {
		CustomerComponent customer = entity.getCustomer();
		elapsedTime += timeDiff;
		if (elapsedTime >= customer.orderingPeriod) {
			entity.makeOrder();
		}
	}

	@Override
	public void startOrderingFood() {
		if (CustomerEntity.DO_LOG) Log.d(TAG, "startOrderingFood");
		
		if (entity.getCustomer().customerType == CustomerComponent.SUPERSTAR_LADY) {
			((SceneBase)entity.getScene()).send(entity, new EntityEvent(EntityEvent.SUPER_STAR_LADY_APPEARS, entity.getId()));
		} else if (entity.getCustomer().customerType == CustomerComponent.TRAMP) {
			((SceneBase)entity.getScene()).send(entity, new EntityEvent(EntityEvent.TRAMP_APPEARS, entity.getId()));
		}
		
		OrderBubbleEntity bubble = new OrderBubbleEntity(
				(SceneBase) entity.getScene(),
				getOrderBubbleId(), entity.getSeat());
		entity.setOrderBubble(bubble);
		entity.getScene().addEntity(bubble);
		
		bubble.startThinking();
		elapsedTime = 0;
	}
	
	@Override
	public FoodProductEntity makeOrder() {
		FoodProductEntity order = makeDecision();
		
		entity.setActionState(entity.obtainActionState(CustomerWaitState.class));
		
		entity.getCustomer().startWaiting();
		OrderBubbleEntity bubble = entity.getOrderBubble();
		bubble.stopThinking();
		
		return order;
	}
	
}
