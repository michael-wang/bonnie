package com.studioirregular.bonniep2;

public class CustomerFoodCriticWaitState extends CustomerWaitState {

	public CustomerFoodCriticWaitState(CustomerEntity entity) {
		super(entity);
	}
	
	@Override
	public void onEvent(Event event) {
		if (event.what == EntityEvent.FOOD_CRITIC_WALKOUT_ANGRILY) {
			EntityEvent e = (EntityEvent)event;
			if (e.entityId.equals(entity.getId())) {
				return; //ignore self event 
			} else {
				entity.astonished();
				super.stopWaitingAndWalkOut();
				return;
			}
		}
		super.onEvent(event);
	}
	
	@Override
	protected void stopWaitingAndWalkOut() {
		super.stopWaitingAndWalkOut();
		((SceneBase)entity.getScene()).send(entity, new EntityEvent(EntityEvent.FOOD_CRITIC_WALKOUT_ANGRILY, entity.getId()));
	}

}
