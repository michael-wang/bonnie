package com.studioirregular.bonniep2;

import java.util.List;

import android.util.Log;
import android.util.Pair;

import com.studioirregular.bonnie.foodsystem.FoodSystem.FoodCombination;


public class CustomerEntity extends BasicEntity implements FoodProductConsumer {

	static final boolean DO_LOG = false;
	private static final String TAG = "customer-entity";
	
	static final String CUSTOMER_COMPONENT_ID = "customer";
	
	private CustomerComponent customer;
	FoodCombination preferredFood;
	private int seat = CustomerSeat.INVALID_SEAT;
	private CustomerMoodState moodState;
	private CustomerActionState actionState;
	private FoodProductEntity expectedFood;
	private OrderBubbleEntity orderBubble;
	
	public CustomerEntity(SceneBase scene, String id, int customerType, FoodCombination preferredFood) {
		super(scene, id);
		
		addCustomerComponent(customerType);
		this.preferredFood = preferredFood;
		
		setMoodState(obtainMoodState(CustomerNormalState.class));
		actionState = obtainActionState(CustomerWalkState.class);
		
		excitedAnimation = new FrameAnimationComponent("excited-animation", 0, 0, 204, 210, false, this);
		excitedAnimation.addFrame(TextureSystem.getInstance().getPart("game_event_1_add_001"), 80);
		excitedAnimation.addFrame(TextureSystem.getInstance().getPart("game_event_1_add_002"), 80);
		excitedAnimation.addFrame(TextureSystem.getInstance().getPart("game_event_1_add_001"), 80);
		excitedAnimation.addFrame(TextureSystem.getInstance().getPart("game_event_1_add_002"), 80);
		excitedAnimation.addFrame(TextureSystem.getInstance().getPart("game_event_1_add_003"), 40);
		excitedAnimation.addFrame(TextureSystem.getInstance().getPart("game_event_1_add_004"), 40);
		excitedAnimation.addFrame(TextureSystem.getInstance().getPart("game_event_1_add_005"), 40);
		excitedAnimation.addFrame(TextureSystem.getInstance().getPart("game_event_1_add_006"), 40);
		excitedAnimation.addFrame(TextureSystem.getInstance().getPart("game_event_1_add_007"), 40);
		excitedAnimation.addFrame(TextureSystem.getInstance().getPart("game_event_1_add_008"), 40);
		
		astonishedAnimation = new FrameAnimationComponent("astonished-animation", 0, 0, 204, 210, false, this);
		astonishedAnimation.addFrame(TextureSystem.getInstance().getPart("game_event_3_wake_001"), 150);
		astonishedAnimation.addFrame(TextureSystem.getInstance().getPart("game_event_3_wake_002"), 150);
		
		if (customerType != CustomerComponent.TRAMP) {
			badSmellAnimation = new FrameAnimationComponent("sick-animation", 0, 0, 204, 210, false, this);
			badSmellAnimation.addFrame(TextureSystem.getInstance().getPart("game_event_2_smoke_001"), 150);
			badSmellAnimation.addFrame(TextureSystem.getInstance().getPart("game_event_2_smoke_002"), 150);
		}
	}
	
	public void takeSeat(int seat) {
		this.seat = seat;
		startMovingToSeat();
	}
	
	@Override
	void addComponentInternal(Component component) {
		super.addComponentInternal(component);
		
		if (component instanceof CustomerComponent) {
			customer = (CustomerComponent)component;
			if (DO_LOG) Log.d(TAG, "add customer:" + customer);
		}
	}
	
	@Override
	Component removeComponentInternal(String componentId) {
		Component component = super.removeComponentInternal(componentId);
		
		if (component instanceof CustomerComponent) {
			customer = null;
			if (DO_LOG) Log.w(TAG, "remove customer:" + customer);
		}
		
		return component;
	}
	
	public CustomerComponent getCustomer() {
		return customer;
	}
	
	@Override
	protected boolean isReadyToStart() {
		return customer != null;
	}
	
	@Override
	public void update(long timeDiff) {
		super.update(timeDiff);
		
		if (customer != null) {
			customer.update(timeDiff);
		}
		
		if (moodState != null) {
			moodState.update(timeDiff);
		}
		
		if (actionState != null) {
			actionState.update(timeDiff);
		}
	}
	
	@Override
	public void onEvent(Event event) {
		if (DO_LOG) Log.d(TAG, "onEvent event:" + event + ",actionState:" + actionState + ", moodState:" + moodState);
		super.onEvent(event);
		
		if (moodState != null) {
			moodState.onEvent(event);
		}
		
		if (actionState != null) {
			actionState.onEvent(event);
		}
		
		switch (event.what) {
		case ComponentEvent.ANIMATION_ENDED:
		{
			ComponentEvent e = (ComponentEvent)event;
			if (onAstonished && e.getComponentId().equals(astonishedAnimation.getId())) {
				remove(astonishedAnimation.getId());
				onAstonished = false;
			} else if (e.getComponentId().equals(excitedAnimation.getId())) {
				removeComponentInternal(excitedAnimation);
				onGettingExcited = false;
			} else if (badSmellAnimation != null && e.getComponentId().equals(badSmellAnimation.getId())) {
				removeComponentInternal(badSmellAnimation);
				onGettingSicked = false;
			}
			break;
		}
		case EntityEvent.SUPER_STAR_LADY_APPEARS:
			if (customer.customerType != CustomerComponent.SUPERSTAR_LADY
					&& customer.isWaiting()) {
				getExcited();
				customer.increasePatience(1);
			}
			break;
		case EntityEvent.FOOD_CRITIC_EXCITED:
			Log.w(TAG, "onEvent EntityEvent.FOOD_CRITIC_EXCITED!");
			if (customer.customerType != CustomerComponent.FOOD_CRITIC && customer.isWaiting()) {
				customer.increasePatience(1);
				getExcited();
			}
			break;
		case EntityEvent.TRAMP_APPEARS:
			if (customer.customerType != CustomerComponent.TRAMP) {
				if (customer.isWaiting()) {
					getSick();
					customer.increasePatience(-1);
				}
			}
			break;
		}
	}
	
	int getSeat() {
		return seat;
	}
	
	FoodProductEntity getExpectedFood() {
		return expectedFood;
	}
	
	void setOrderBubble(OrderBubbleEntity bubble) {
		this.orderBubble = bubble;
	}
	
	OrderBubbleEntity getOrderBubble() {
		return this.orderBubble;
	}
	
	// customer actions
	void startMovingToSeat() {
		if (DO_LOG) Log.d(TAG, "startMovingToSeat");
		if (seat == CustomerSeat.INVALID_SEAT) {
			Log.e(TAG, "startMovingToSeat invalid seat:" + seat);
			return;
		}
		actionState.startMovingToSeat();
	}
	
	void startOrderingFood() {
		if (DO_LOG) Log.d(TAG, "startOrderingFood");
		
		actionState.startOrderingFood();
	}
	
	void makeOrder() {
		if (DO_LOG) Log.d(TAG, "makeOrder");
		
		expectedFood = actionState.makeOrder();
		scene.addEntity(expectedFood);
		
		send(this, new EntityEvent(EntityEvent.CUSTOMER_MAKE_ORDER, id));
	}
	
	void changeMind() {
		if (DO_LOG) Log.w(TAG, ">>changeMind expectedFood:" + expectedFood);
		scene.removeEntity(expectedFood);
		expectedFood = null;
		
		expectedFood = actionState.changeMind();
		scene.addEntity(expectedFood);
		if (DO_LOG) Log.w(TAG, "<<changeMind expectedFood:" + expectedFood);
	}
	
	void receivingFood(FoodProductEntity food) {
		if (DO_LOG) Log.d(TAG, "receivingFood food:" + food);
		
		actionState.receivingFood(food);
		moodState.receivingFood(food);
		
		scene.removeEntity(food);
	}
	
	void stopWaiting() {
		scene.removeEntity(expectedFood);
		expectedFood = null;
		actionState.stopWaiting();
	}
	
	void walkOut() {
		actionState.walkOut();
	}
	
	void setActionState(CustomerActionState newState) {
		actionState = newState;
	}
	
	void setMoodState(CustomerMoodState newState) {
		this.setMoodState(newState, false);
	}
	
	void setMoodState(CustomerMoodState newState, boolean temporarily) {
		if (DO_LOG) Log.d(TAG, "setMoodState newState:" + newState.getClass().getSimpleName());
		moodState = newState;
		moodState.onStateActivated(temporarily);
	}
	
	// action state creation
	CustomerActionState obtainActionState(Class<? extends CustomerActionState> clazz) {
		if (clazz.equals(CustomerWalkState.class)) {
			if (walkActionState == null) {
				walkActionState = new CustomerWalkState(this);
			}
			return walkActionState;
		} else if (clazz.equals(CustomerOrderState.class)) {
			if (orderActionState == null) {
				orderActionState = new CustomerOrderState(this);
			}
			return orderActionState;
		} else if (clazz.equals(CustomerWaitState.class)) {
			if (waitActionState == null) {
				if (customer.customerType == CustomerComponent.FOOD_CRITIC) {
					waitActionState = new CustomerFoodCriticWaitState(this);
				} else {
					waitActionState = new CustomerWaitState(this);
				}
			}
			return waitActionState;
		}
		return null;
	}
	
	// mood state creation
	CustomerMoodState obtainMoodState(Class<? extends CustomerMoodState> clazz) {
		if (clazz.equals(CustomerNormalState.class)) {
			if (normalMoodState == null) {
				normalMoodState = createNormalMoodState();
			}
			return normalMoodState;
		} else if (clazz.equals(CustomerExcitedState.class)) {
			if (excitedMoodState == null) {
				excitedMoodState = createExcitedMoodState();
			}
			return excitedMoodState;
		} else if (clazz.equals(CustomerUnhappyState.class)) {
			if (unhappyMoodState == null) {
				unhappyMoodState = createUnhappyMoodState();
			}
			return unhappyMoodState;
		} else if (clazz.equals(CustomerAngryState.class)) {
			if (angryMoodState == null) {
				angryMoodState = createAngryMoodState();
			}
			return angryMoodState;
		} else if (clazz.equals(CustomerSickState.class)) {
			if (sickMoodState == null) {
				sickMoodState = createSickMoodState();
			}
			return sickMoodState;
		}
		return null;
	}
	
	@Override
	public boolean doYouWantToConsumeIt(FoodProductEntity food) {
		return actionState.doYouWantToConsumeIt(food);
	}

	@Override
	public void consumeIt(FoodProductEntity food) {
		receivingFood(food);
	}
	
	public void onCustomerLeave() {
		scene.removeEntity(id);
		
		((EventHost)scene).send(this, new EntityEvent(EntityEvent.TUTORIAL_CUSTOMER_LEAVED, id));
	}
	
	private void addCustomerComponent(int customerType) {
		addComponentInternal(CustomerComponent.newInstance(CUSTOMER_COMPONENT_ID, customerType, this));
	}
	
	private CustomerMoodState createNormalMoodState() {
		FrameAnimationComponent animation = new FrameAnimationComponent(CustomerMoodState.FRAME_ANIMATION_NAME, 0, 0, 204, 210, true, this);
		List< Pair<String, Long> > frames = getCustomer().frameListNormal;
		for (Pair<String, Long> frame : frames) {
			GLTexture texture = TextureSystem.getInstance().getPart(frame.first);
			animation.addFrame(texture, frame.second);
		}
		return new CustomerNormalState(this, animation);
	}
	
	private CustomerMoodState createExcitedMoodState() {
		FrameAnimationComponent animation = new FrameAnimationComponent(CustomerMoodState.FRAME_ANIMATION_NAME, 0, 0, 204, 210, true, this);
		List< Pair<String, Long> > frames = getCustomer().frameListExcited;
		for (Pair<String, Long> frame : frames) {
			GLTexture texture = TextureSystem.getInstance().getPart(frame.first);
			animation.addFrame(texture, frame.second);
		}
		return new CustomerExcitedState(this, animation);
	}
	
	private CustomerMoodState createUnhappyMoodState() {
		FrameAnimationComponent animation = new FrameAnimationComponent(CustomerMoodState.FRAME_ANIMATION_NAME, 0, 0, 204, 210, true, this);
		List< Pair<String, Long> > frames = getCustomer().frameListUnhappy;
		for (Pair<String, Long> frame : frames) {
			GLTexture texture = TextureSystem.getInstance().getPart(frame.first);
			animation.addFrame(texture, frame.second);
		}
		return new CustomerUnhappyState(this, animation);
	}
	
	private CustomerMoodState createAngryMoodState() {
		FrameAnimationComponent animation = new FrameAnimationComponent(CustomerMoodState.FRAME_ANIMATION_NAME, 0, 0, 204, 210, true, this);
		List< Pair<String, Long> > frames = getCustomer().frameListAngry;
		for (Pair<String, Long> frame : frames) {
			GLTexture texture = TextureSystem.getInstance().getPart(frame.first);
			animation.addFrame(texture, frame.second);
		}
		return new CustomerAngryState(this, animation);
	}
	
	private CustomerMoodState createSickMoodState() {
		FrameAnimationComponent animation = new FrameAnimationComponent(CustomerMoodState.FRAME_ANIMATION_NAME, 0, 0, 204, 210, true, this);
		List< Pair<String, Long> > frames = getCustomer().frameListSick;
		for (Pair<String, Long> frame : frames) {
			GLTexture texture = TextureSystem.getInstance().getPart(frame.first);
			animation.addFrame(texture, frame.second);
		}
		return new CustomerSickState(this, animation);
	}
	
	void getExcited() {
		if (DO_LOG) Log.w(TAG, id + " getExcited() moodState:" + moodState);
		if (onGettingExcited) {
			return;
		}
		
		excitedAnimation.setX(moodState.animation.getX());
		excitedAnimation.setY(moodState.animation.getY());
		
		setMoodState(obtainMoodState(CustomerExcitedState.class), true);
		
		excitedAnimation.start();
		add(excitedAnimation);
		
		onGettingExcited = true;
	}
	
	void astonished() {
		if (onAstonished) {
			return;
		}
		
		astonishedAnimation.setX(moodState.animation.getX());
		astonishedAnimation.setY(moodState.animation.getY());
		
		if (angryMoodState == null) {
			angryMoodState = obtainMoodState(CustomerAngryState.class);
		}
		if (moodState != angryMoodState) {
			setMoodState(angryMoodState);
		}
		
		astonishedAnimation.start();
		add(astonishedAnimation);
		
		onAstonished = true;
	}
	
	void getSick() {
		if (onGettingSicked) {
			return;
		}
		
		badSmellAnimation.setX(moodState.animation.getX());
		badSmellAnimation.setY(moodState.animation.getY());
		
		if (sickMoodState == null) {
			sickMoodState = obtainMoodState(CustomerSickState.class);
		}
		if (moodState != sickMoodState) {
			setMoodState(sickMoodState, true);
		}
		
		badSmellAnimation.start();
		add(badSmellAnimation);
		
		onGettingSicked = true;
	}
	
	protected CustomerActionState walkActionState;
	protected CustomerActionState orderActionState;
	protected CustomerActionState waitActionState;
	
	protected CustomerMoodState normalMoodState;
	protected CustomerMoodState excitedMoodState;
	protected CustomerMoodState unhappyMoodState;
	protected CustomerMoodState angryMoodState;
	protected CustomerMoodState sickMoodState;
	
	private boolean onGettingExcited = false;
	private FrameAnimationComponent excitedAnimation;
	private boolean onAstonished = false;
	private FrameAnimationComponent astonishedAnimation;
	private boolean onGettingSicked = false;
	private FrameAnimationComponent badSmellAnimation;
	
}
