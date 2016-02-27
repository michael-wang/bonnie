package com.studioirregular.bonniep2;


public abstract class CustomerMoodState {

//	private static final String TAG = "customer-mood-state";
	
	protected static final String FRAME_ANIMATION_NAME = "mood-animation";
	protected static final float ANIMATION_INITIAL_X = 720;
	protected static final float ANIMATION_INITIAL_Y = 62;
	
	protected static final int PATIENCE_THRESHOLD_UNHAPPY	= 2;
	protected static final int PATIENCE_THRESHOLD_ANGRY		= 1;
	
	public CustomerMoodState(CustomerEntity entity, FrameAnimationComponent animation) {
		this.entity = entity;
		this.animation = animation;
	}
	
	public void onStateActivated(boolean temporarily) {
		temporarilyState = temporarily;
		
		Component currentAnimation = entity.getComponent(FRAME_ANIMATION_NAME);
		if (currentAnimation != null && currentAnimation instanceof RenderComponent) {
			animation.setX(((RenderComponent)currentAnimation).getX());
			animation.setY(((RenderComponent)currentAnimation).getY());
		} else {
			animation.setX(ANIMATION_INITIAL_X);
			animation.setY(ANIMATION_INITIAL_Y);
		}
		
		entity.remove(FRAME_ANIMATION_NAME);	// remove animation here for createAnimation need it.
		if (animation != null) {
			animation.setLoop(!temporarilyState);
			animation.start();
			entity.add(animation);
		}
	}
	
	public void update(long timeDiff) {
		
	}
	
	public void onEvent(Event event) {
		if (event.what == ComponentEvent.ANIMATION_ENDED) {
			if (temporarilyState) {
				ComponentEvent e = (ComponentEvent)event;
				if (e.getComponentId().equals(animation.getId())) {
					entity.setMoodState(findNewState());
				}
			}
		}
	}
	
	public void receivingFood(FoodProductEntity food) {
		if (food instanceof FoodCandyEntity) {
			if (entity.getCustomer().isWaiting()) {
				entity.getExcited();
				entity.getCustomer().increasePatience(1);
			}
		}
	}
	
	protected CustomerMoodState findNewState() {
		int patience = entity.getCustomer().getCurrentPatience();
		if (PATIENCE_THRESHOLD_UNHAPPY < patience) {
			return entity.obtainMoodState(CustomerNormalState.class);
		} else if (PATIENCE_THRESHOLD_ANGRY < patience) {
			return entity.obtainMoodState(CustomerUnhappyState.class);
		} else {
			return entity.obtainMoodState(CustomerAngryState.class);
		}
	}
	
	protected CustomerEntity entity;
	protected FrameAnimationComponent animation;
	protected boolean temporarilyState;

}
