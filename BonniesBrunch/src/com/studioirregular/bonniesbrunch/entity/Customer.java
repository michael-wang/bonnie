package com.studioirregular.bonniesbrunch.entity;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.FoodSystem.Brunch;
import com.studioirregular.bonniesbrunch.FoodSystem.Food;
import com.studioirregular.bonniesbrunch.FoodSystem.FoodSet;
import com.studioirregular.bonniesbrunch.FoodSystem.FoodType;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.SoundSystem;
import com.studioirregular.bonniesbrunch.SoundSystem.Sound;
import com.studioirregular.bonniesbrunch.base.ObjectBase;
import com.studioirregular.bonniesbrunch.component.FrameAnimationComponent;
import com.studioirregular.bonniesbrunch.component.HoldAnimation;

public class Customer extends GameEntity {

	private static final String TAG = "customer";
	
	public enum Type {
		JOGGING_GIRL,
		WORKING_MAN,
		BALLOON_BOY,
		GIRL_WITH_DOG,
		FOOD_CRITIC,
		SUPERSTAR_LADY,
		PHYSICIST,
		TRAMP,
		GRANNY,
		SUPERSTAR_MAN,
		INVALID_TYPE
	}
	
	public static Map<Type, String> CUSTOMER_TYPE_NAME;
	static {
		CUSTOMER_TYPE_NAME = new HashMap<Type, String>();
		CUSTOMER_TYPE_NAME.put(Type.JOGGING_GIRL,	"Jogging Girl");
		CUSTOMER_TYPE_NAME.put(Type.WORKING_MAN,	"Working Man");
		CUSTOMER_TYPE_NAME.put(Type.BALLOON_BOY,	"Balloon Boy");
		CUSTOMER_TYPE_NAME.put(Type.GIRL_WITH_DOG,	"Girl with Dog");
		CUSTOMER_TYPE_NAME.put(Type.FOOD_CRITIC,	"Food Critic");
		CUSTOMER_TYPE_NAME.put(Type.SUPERSTAR_LADY,	"Super Star Lady");
		CUSTOMER_TYPE_NAME.put(Type.PHYSICIST,		"Physicist");
		CUSTOMER_TYPE_NAME.put(Type.TRAMP,			"Tramp");
		CUSTOMER_TYPE_NAME.put(Type.GRANNY,			"Granny");
		CUSTOMER_TYPE_NAME.put(Type.SUPERSTAR_MAN,	"Super Star Man");
		CUSTOMER_TYPE_NAME.put(Type.INVALID_TYPE,	"Invalid Customer Type!");
	}
	
	// Parameters depending on type.
	public static class CustomerParameters {
		public int walkSpeed;			// in WALK_SPEED_PER_SECOND
		public int orderDelay;			// in millisecond
		public int tip;					// 0 - 5
		public int delayBeforeLeave;	// in millisecond
		
		public static final int PATIENCE_TIME_UNIT = 10000;	// in millisecond
		public int unhappyPatience;
		public int angryPatience;
		public int totalPatience;
		public boolean dropPatient;
		public boolean doEffect;	// say hello, physicist distraction...
		
		public CustomerParameters() {
			walkSpeed = 0 * WALK_SPEED_PER_SECOND;
			orderDelay = 1000;
			tip = 0;
			delayBeforeLeave = 1000;
			angryPatience = 1;
			unhappyPatience = 3;
			totalPatience = 5;
			dropPatient = true;
			doEffect = true;
		}
		
		public void set(CustomerParameters other) {
			walkSpeed = other.walkSpeed;
			orderDelay = other.orderDelay;
			tip = other.tip;
			delayBeforeLeave = other.delayBeforeLeave;
			
			angryPatience = other.angryPatience;
			unhappyPatience = other.unhappyPatience;
			totalPatience = other.totalPatience;
			dropPatient = other.dropPatient;
			doEffect = other.doEffect;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}
			
			if (!(o instanceof CustomerParameters)) {
				return false;
			}
			
			CustomerParameters lhs = (CustomerParameters)o;
			return walkSpeed == lhs.walkSpeed &&
					orderDelay == lhs.orderDelay &&
					tip == lhs.tip &&
					delayBeforeLeave == lhs.delayBeforeLeave &&
					unhappyPatience == lhs.unhappyPatience &&
					angryPatience == lhs.angryPatience &&
					totalPatience == lhs.totalPatience &&
					dropPatient == lhs.dropPatient &&
					doEffect == lhs.doEffect;
		}
		
		public static final int WALK_SPEED_PER_SECOND = 80;	// game world distance/second
	}
	
	public static class CustomerAnimations {
		public FrameAnimationComponent normal = new FrameAnimationComponent(GameEntity.MIN_GAME_COMPONENT_Z_ORDER);
		public FrameAnimationComponent unhappy = new FrameAnimationComponent(GameEntity.MIN_GAME_COMPONENT_Z_ORDER);
		public FrameAnimationComponent angry = new FrameAnimationComponent(GameEntity.MIN_GAME_COMPONENT_Z_ORDER);
		public FrameAnimationComponent excited = new FrameAnimationComponent(GameEntity.MIN_GAME_COMPONENT_Z_ORDER);
		public FrameAnimationComponent sick = new FrameAnimationComponent(GameEntity.MIN_GAME_COMPONENT_Z_ORDER);
		
		public CustomerAnimations() {
			normal.setLoop(true);
			normal.setFillBefore(true);
			normal.setFillAfter(true);
			
			unhappy.setLoop(true);
			unhappy.setFillBefore(true);
			unhappy.setFillAfter(true);
			
			angry.setLoop(true);
			angry.setFillBefore(true);
			angry.setFillAfter(true);
			
			excited.setLoop(true);
			excited.setFillBefore(true);
			excited.setFillAfter(true);
			
			sick.setLoop(true);
			sick.setFillBefore(true);
			sick.setFillAfter(true);
		}
		
		public FrameAnimationComponent getAnimation(MoodState mood) {
			if (MoodState.NORMAL == mood) {
				return normal;
			} else if (MoodState.EXCITED == mood) {
				return excited;
			} else if (MoodState.UNHAPPY == mood) {
				return unhappy;
			} else if (MoodState.ANGRY == mood) {
				return angry;
			} else if (MoodState.SICK == mood) {
				return sick;
			}
			return null;
		}
	}
	
	public static class OrderingSpec {
		public boolean addOptional(int food) {
			return optionalFood.addFood(food);
		}
		
		public boolean addOptional(FoodSet set) {
			return optionalFood.add(set);
		}
		
		public boolean addMustHave(int food) {
			return mustHave.addFood(food);
		}
		
		public boolean addMustHave(Brunch brunch) {
			return mustHave.add(brunch);
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + " mustHave:" + mustHave + ",optionalFood:" + optionalFood;
		}
		
		private Brunch mustHave = new Brunch();
		private FoodSet optionalFood = new FoodSet();
	}
	
	public enum ActionState {
		NONE,
		WALK,
		WAIT_IN_LINE,
		ORDER,
		WAIT	// wait for ordered brunch
	}
	
	public enum MoodState {
		NONE,
		NORMAL,
		UNHAPPY,
		ANGRY,
		EXCITED,
		SICK
	}
	
	public static interface CustomerHost {
		public void startOrdering(Customer customer);
		public void madeDecision(Customer customer);
		public void onReceivingBrunch(Customer customer);
		public void startLeaving(Customer customer);
		public void customerLeaved(Customer customer);
	}
	
	public static boolean isCustomerEvent(GameEvent event) {
		if (GameEvent.CUSTOMER_SUPER_LADY_ARRIVED <= event.what && event.what <= GameEvent.CUSTOMER_FOOD_CRITIC_WAIT_OUT) {
			return true;
		}
		return false;
	}
	
	// type independent values.
	public FrameAnimationComponent sickEffect = new FrameAnimationComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
	public FrameAnimationComponent suprisedEffect = new FrameAnimationComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
	public FrameAnimationComponent excitedEffect = new FrameAnimationComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
	
	// type dependent values, should be same on object life cycle.
	public Type type;
	public CustomerParameters config = new CustomerParameters();
	public CustomerAnimations animations = new CustomerAnimations();
	public OrderingSpec orderSpec;
	
	// values which may change on each object presentation.
	public CustomerSeat seat;
	public CustomerHost host;
	
	// values change during object presentation.
	public ActionState action = ActionState.NONE;
	public MoodState mood = MoodState.NONE;
	public Brunch expectedBrunch = new Brunch();
	public float walkDestinationX = 0;
	public int elapsedTime = 0;
	// separate this from elapsed time for customer may re-order (clearn elapsedTime) while keep waitElapsedTime.
	public int waitElapsedTime = 0;
	
	public boolean inTempMood = false;
	public long tempMoodElapsedTime = 0;
	public static long TEMP_MOOD_DURATION = 1000;
	
	private boolean ladyChangedMind = false;
	// special mode for physicist.
	private boolean thinkingPhysics = false;
	private long physicistChangeFocusDuration = 0;
	private long physicistFocusElapsedTime = 0;
	
	private static final float KEEP_DISTANCE_TO_OTHERS = 82;
	private Customer followingTarget;
	
	// sounds
	private Sound soundHello;
	private Sound soundGetBrunchNormal;
	private Sound soundGetBrunchExcited;
	private Sound soundDogBark;	// fot girl with dog
	private static final int DOG_BARK_DELAY_ANIMATION_ID = 0x10;
	
	public Customer(int zOrder) {
		super(zOrder);
		
		sickEffect.removeSelfWhenFinished(true);
		sickEffect.addFrame("game_event_2_smoke_001", CUSTOMER_WIDTH, CUSTOMER_HEIGHT, 150);
		sickEffect.addFrame("game_event_2_smoke_002", CUSTOMER_WIDTH, CUSTOMER_HEIGHT, 150);
		
		suprisedEffect.removeSelfWhenFinished(true);
		suprisedEffect.addFrame("game_event_3_wake_001", CUSTOMER_WIDTH, CUSTOMER_HEIGHT, 150);
		suprisedEffect.addFrame("game_event_3_wake_002", CUSTOMER_WIDTH, CUSTOMER_HEIGHT, 150);
		
		excitedEffect.removeSelfWhenFinished(true);
		excitedEffect.addFrame("game_event_1_add_001", CUSTOMER_WIDTH, CUSTOMER_HEIGHT, 80);
		excitedEffect.addFrame("game_event_1_add_002", CUSTOMER_WIDTH, CUSTOMER_HEIGHT, 80);
		excitedEffect.addFrame("game_event_1_add_001", CUSTOMER_WIDTH, CUSTOMER_HEIGHT, 80);
		excitedEffect.addFrame("game_event_1_add_002", CUSTOMER_WIDTH, CUSTOMER_HEIGHT, 80);
		excitedEffect.addFrame("game_event_1_add_003", CUSTOMER_WIDTH, CUSTOMER_HEIGHT, 40);
		excitedEffect.addFrame("game_event_1_add_004", CUSTOMER_WIDTH, CUSTOMER_HEIGHT, 40);
		excitedEffect.addFrame("game_event_1_add_005", CUSTOMER_WIDTH, CUSTOMER_HEIGHT, 40);
		excitedEffect.addFrame("game_event_1_add_006", CUSTOMER_WIDTH, CUSTOMER_HEIGHT, 40);
		excitedEffect.addFrame("game_event_1_add_007", CUSTOMER_WIDTH, CUSTOMER_HEIGHT, 40);
		excitedEffect.addFrame("game_event_1_add_008", CUSTOMER_WIDTH, CUSTOMER_HEIGHT, 40);
	}
	
	public void setup(OrderingSpec orderSpec, CustomerSeat seat) {
		setup(orderSpec, seat, null);
	}
	
	public void setup(OrderingSpec orderSpec, CustomerSeat seat, CustomerHost host) {
		super.setup(INITIAL_POSITION_X, INITIAL_POSITION_Y, 204, 210);
		
		this.seat = seat;
		this.orderSpec = orderSpec;
		this.host = host;
		
		loadSounds();
	}
	
	// setup internal state for this present session.
	public void start() {
		assert animations.normal.frames.size() == 4;
		setMood(MoodState.NORMAL);
		expectedBrunch.reset();
		elapsedTime = 0;
		waitElapsedTime = 0;
		
		ladyChangedMind = false;
		thinkingPhysics = false;
		physicistChangeFocusDuration = 0;
		physicistFocusElapsedTime = 0;
	}
	
	// can only walk in x direction
	public void walk(float x) {
		action = ActionState.WALK;
		walkDestinationX = x;
		elapsedTime = 0;
	}
	
	public void follow(Customer other) {
		followingTarget = other;
		
		if (other == null) {
			return;
		}
		
		if ((other.box.left + KEEP_DISTANCE_TO_OTHERS) < box.left) {
			walk(other.box.left + KEEP_DISTANCE_TO_OTHERS);
		}
	}
	
	// CustomerManager should send GameEvent.DROP_ACCEPTED for candy if customer take it.
	public boolean eatCandy(Candy candy) {
		if (candy == null || ActionState.WAIT != action) {
			return false;
		}
		
		reduceWaitElapsedTime(1);
		
		if (MoodState.EXCITED != mood) {
			setMood(MoodState.EXCITED, true);
			add(excitedEffect);
		} else {
			inTempMood = true;
			tempMoodElapsedTime = 0;
			if (excitedEffect.isFinished()) {
				add(excitedEffect);
			}
		}
		if (Config.DEBUG_LOG) Log.d(TAG, "excitedEffect started:" + excitedEffect.isStarted() + ",finished:" + excitedEffect.isFinished());
		excitedEffect.start();
		
		SoundSystem.getInstance().playSound(soundGetBrunchNormal, false);
		
		return true;
	}
	
	public boolean receiveBrunch(Brunch brunch) {
		if (expectedBrunch.equals(brunch) == false) {
			return false;
		}
		
		MoodState lastState = calculateMoodState(getRemainingPatience());
		if (MoodState.NORMAL == lastState) {
			SoundSystem.getInstance().playSound(soundGetBrunchExcited, false);
			
			excitedEffect.start();
			add(excitedEffect);
			setMood(MoodState.EXCITED, false, true);
			
			if (Type.FOOD_CRITIC == type) {
				GameEventSystem.scheduleEvent(GameEvent.CUSTOMER_FOOD_CRITIC_SATISFIED, 0, this);
			}
		} else {
			SoundSystem.getInstance().playSound(soundGetBrunchNormal, false);
			
			setMood(MoodState.NORMAL, false, true);
		}
		
		if (host != null) {
			host.onReceivingBrunch(this);
		}
		
		expectedBrunch.reset();
		payMoney(brunch);
		onLeave();
		
		return true;
	}
	
	public void reducePatient(int point) {
		waitElapsedTime += (point * CustomerParameters.PATIENCE_TIME_UNIT);
	}
	
	// NOTICE: customer event will dispatched by parent, no need to ask for them here.
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		if (isCustomerEvent(event)) {
			onCustomerEvent(event);
		} else if (GameEvent.ANIMATION_END == event.what && DOG_BARK_DELAY_ANIMATION_ID == event.arg1) {
			SoundSystem.getInstance().playSound(soundDogBark, false);
		}
	}
	
	public boolean onCustomerEvent(GameEvent event) {
		if (ActionState.WAIT != action) {
			return false;
		}
		
		if (event.what == GameEvent.CUSTOMER_SUPER_LADY_ARRIVED) {
			reduceWaitElapsedTime(1);
			if (MoodState.EXCITED != mood && this != event.obj) {
				excitedEffect.start();
				add(excitedEffect);
				
				setMood(MoodState.EXCITED, true);
				return true;
			}
		} else if (event.what == GameEvent.CUSTOMER_TRAMP_ARRIVED) {
			if (Type.TRAMP != type) {	// tramp won't affected by other tramp.
				increaseWaitElapsedTime(1);
				if (MoodState.SICK != mood) {
					setMood(MoodState.SICK, true);
					
					sickEffect.start();
					add(sickEffect);
					return true;
				}
			}
		} else if (event.what == GameEvent.CUSTOMER_FOOD_CRITIC_SATISFIED) {
			reduceWaitElapsedTime(1);
			if (MoodState.EXCITED != mood) {
				excitedEffect.start();
				add(excitedEffect);
				
				setMood(MoodState.EXCITED, true);
				return true;
			}
		} else if (GameEvent.CUSTOMER_FOOD_CRITIC_WAIT_OUT == event.what) {
			if (this != event.obj) {
				setMood(MoodState.ANGRY, false, true);
				suprisedEffect.start();
				add(suprisedEffect);
				doneWaiting();
			}
		}
		
		return false;
	}
	
	@Override
	public void update(long timeDelta, ObjectBase parent) {
		super.update(timeDelta, parent);
		
		if (action == ActionState.WALK) {
			if (walkDestinationX < 0) {	// leaving
				// wait as config says
				if (elapsedTime < config.delayBeforeLeave) {
					elapsedTime += timeDelta;
					return;
				}
			}
			
			float dx = -(float)config.walkSpeed * timeDelta / 1000f;
			
			if ((box.left + dx) <= walkDestinationX) {
				move(walkDestinationX, box.top);
				
				if (box.right <= 0) {
					host.customerLeaved(this);
				} else if (box.left <= seat.getSeatPositionX()) {
					// arrived seat
					onTakeSeat();
					startOrdering();
				} else {
					action = ActionState.WAIT_IN_LINE;
				}
			} else {
				relativeMove(dx, 0);
			}
		} else if (action == ActionState.WAIT_IN_LINE) {
			if (followingTarget != null) {
				if ((followingTarget.box.left + KEEP_DISTANCE_TO_OTHERS) < box.left) {
					walk(followingTarget.box.left + KEEP_DISTANCE_TO_OTHERS);
				}
			}
		} else if (action == ActionState.ORDER) {
			elapsedTime += timeDelta;
			if (elapsedTime >= config.orderDelay) {
				makeDecision();
				afterMakeDecision();
			}
		} else if (action == ActionState.WAIT) {
			if (config.dropPatient == true) {
				waitElapsedTime += timeDelta;
			}
			
			if (inTempMood) {
				tempMoodElapsedTime += timeDelta;
				if (TEMP_MOOD_DURATION <= tempMoodElapsedTime) {
					inTempMood = false;
					updateMoodState();	// go back to mood according to wait elapsed time
				}
			} else {
				updateMoodState();
			}
			
			if (config.doEffect && Type.PHYSICIST == type) {
				physicistFocusElapsedTime += timeDelta;
				if (physicistChangeFocusDuration <= physicistFocusElapsedTime) {
					physicistSwitchFocus();
				}
			}
		}
	}
	
	protected void setMood(MoodState newState) {
		this.setMood(newState, false, false);
	}
	
	protected void setMood(MoodState newState, boolean temporarily) {
		this.setMood(newState, temporarily, false);
	}
	
	protected void setMood(MoodState newState, boolean temporarily, boolean forced) {
		if (Config.DEBUG_LOG) Log.d(TAG, "setMood newMood:" + newState + ",temp:" + temporarily + ", forced:" + forced + ". mood:" + mood + ", inTempMood:" + inTempMood);
		
		if (temporarily) {
			inTempMood = true;
			tempMoodElapsedTime = 0;	// ok if already in same temp mood, which results in longer temp mood duration.
		} else if (inTempMood) {
			if (!forced) {
				// don't change temp mood state, unless it's a forced mood change (when receiving brunch).
				return;
			} else {
				inTempMood = false;	// force cancel temp mood.
			}
		}
		
		if (newState == mood) {
			return;
		}
		
		if (Config.DEBUG_LOG) Log.w(TAG, "setMood newState:" + newState + ",temp:" + temporarily + ", mood:" + mood);
		
		if (MoodState.NONE != mood) {
			FrameAnimationComponent animation = animations.getAnimation(mood);
			if (animation != null) {
				remove(animation);
				animation.stop();
			}
		}
		
		mood = newState;
		
		if (MoodState.NONE != mood) {
			FrameAnimationComponent animation = animations.getAnimation(mood);
			if (animation != null) {
				animation.setLoop(inTempMood ? false : true);
				animation.start();
				add(animation);
			}
		}
	}
	
	private void onTakeSeat() {
		if (config.doEffect) {
			if (Type.SUPERSTAR_LADY == type) {
				GameEventSystem.scheduleEvent(GameEvent.CUSTOMER_SUPER_LADY_ARRIVED, 0, this);
			} else if (Type.TRAMP == type) {
				GameEventSystem.scheduleEvent(GameEvent.CUSTOMER_TRAMP_ARRIVED, 0, this);
			}
		
			if (soundHello != null) {
				SoundSystem.getInstance().playSound(soundHello, false);
			}
			
			if (Type.GIRL_WITH_DOG == type && soundDogBark != null) {
				HoldAnimation barkDelay = new HoldAnimation(MIN_GAME_COMPONENT_Z_ORDER);
				barkDelay.setDuration(1000);
				barkDelay.notifyWhenFinished(true, DOG_BARK_DELAY_ANIMATION_ID);
				barkDelay.removeSelfWhenFinished(true);
				add(barkDelay);
				barkDelay.start();
			}
		}
	}
	
	private void startOrdering() {
		if (Config.DEBUG_LOG) Log.d(TAG, "startOrdering");
		action = ActionState.ORDER;
		elapsedTime = 0;
		
		if (host != null) {
			host.startOrdering(this);
		}
		seat.onCustomerStartOrdering();
	}
	
	// setup expectedBrunch according to orderSpec.
	private void makeDecision() {
		if (Config.DEBUG_LOG) Log.d(TAG, "makeDecision");
		try {
			decideBrunch(orderSpec, expectedBrunch);
		} catch (DecideBrunchException e) {
			Log.e(TAG, "makeDecision exception:" + e);
		}
	}
	
	@SuppressWarnings("serial")
	public static class DecideBrunchException extends Exception {
		public DecideBrunchException(String msg) {
			super(msg);
		}
	}
	
	// By first add must order foods.
	// Then randomly pick each food type from preferred food list.
	public void decideBrunch(OrderingSpec spec, Brunch result) throws DecideBrunchException {
		result.reset();
		if (spec.mustHave.size() > 0) {
			if (result.add(spec.mustHave) == false) {
				throw new DecideBrunchException("failed to add set:" + spec.mustHave);
			}
		}
		
		FoodSet workspace = new FoodSet();
		FoodSet set = spec.optionalFood;
		
		set.getByType(FoodType.MAIN_INGREDIENT, workspace);
		chooseSingleFoodType(workspace, result, false);
		if (result.contains(FoodType.TOAST)) {
			result.closeToast();
		}
		
		// only toast allows to add-on.
		if (result.contains(FoodType.TOAST)) {
			set.getByType(FoodType.ADD_ON_A, workspace);
			chooseSingleFoodType(workspace, result, true);
			
			set.getByType(FoodType.ADD_ON_B, workspace);
			chooseSingleFoodType(workspace, result, true);
		}
		
		set.getByType(FoodType.SAUCE, workspace);
		chooseSingleFoodType(workspace, result, true);
		
		set.getByType(FoodType.BEVERAGE, workspace);
		chooseSingleFoodType(workspace, result, true);
		
		// remember to close toast.
		if (result.contains(FoodType.TOAST)) {
			result.closeToast();
		}
	}
	
	private void chooseSingleFoodType(FoodSet source, Brunch result, boolean allowNotChoose) throws DecideBrunchException {
		int size = source.size();
		if (size > 0) {
			int index = (int) (Math.random() * (size + (allowNotChoose ? 1 : 0)));
			if (index < size) {
				int food = source.get(index);
				if (result.addFood(food) == false) {
					throw new DecideBrunchException("failed to pick food:" + Food.getFoodName(food) + " from:" + source + " and add to:" + result);
				}
			}
		}
	}
	
	private void afterMakeDecision() {
		action = ActionState.WAIT;
		
		seat.onCustomerMakeOrder(this, expectedBrunch);
		
		if (host != null) {
			host.madeDecision(this);
		}
		
		if (Type.PHYSICIST == type) {
			thinkingPhysics = false;
			physicistChangeFocusDuration = 2000 + (long)(Math.random()*2000);
			physicistFocusElapsedTime = 0;
		}
	}
	
	private void reduceWaitElapsedTime(int reducePoint) {
		waitElapsedTime -= (reducePoint * CustomerParameters.PATIENCE_TIME_UNIT);
		if (waitElapsedTime < 0) {
			waitElapsedTime = 0;
		}
	}
	
	private void increaseWaitElapsedTime(int increasePoint) {
		waitElapsedTime += (increasePoint * CustomerParameters.PATIENCE_TIME_UNIT);
	}
	
	private MoodState calculateMoodState(long remainPatience) {
		if (remainPatience <= config.angryPatience * CustomerParameters.PATIENCE_TIME_UNIT) {
			return MoodState.ANGRY;
		} else if (remainPatience <= config.unhappyPatience * CustomerParameters.PATIENCE_TIME_UNIT) {
			return MoodState.UNHAPPY;
		} else {
			return MoodState.NORMAL;
		}
	}
	
	public long getRemainingPatience() {
		return config.totalPatience * CustomerParameters.PATIENCE_TIME_UNIT - waitElapsedTime;
	}
	
	public void updateMoodState() {
		long remainPatience = config.totalPatience * CustomerParameters.PATIENCE_TIME_UNIT - waitElapsedTime;
		if (remainPatience <= 0) {
			doneWaiting();
		} else if ((Type.SUPERSTAR_LADY == type) && (!ladyChangedMind) && (remainPatience <= 2 * CustomerParameters.PATIENCE_TIME_UNIT)) {
			ladyChangedMind = true;
			startOrdering();	// super lady changes mind when patient down to 2 points.
		}
		
		MoodState newState = calculateMoodState(remainPatience);
		setMood(newState);
	}
	
	private void payMoney(Brunch receivedBrunch) {
		int brunchCost = receivedBrunch.getCost();
		
		int tip = 0;
		if (MoodState.EXCITED == mood) {
			tip = brunchCost * config.tip / 15;
		}
		
		CustomerManager.ServiceResult result = CustomerManager.ServiceResult.served(brunchCost, tip, this);
		GameEventSystem.scheduleEvent(GameEvent.CUSTOMER_MANAGER_SERVICE_RESULT, 0, result);
	}
	
	private void physicistSwitchFocus() {
		thinkingPhysics = !thinkingPhysics;
		physicistChangeFocusDuration = 2000 + (long)(Math.random()*2000);
		physicistFocusElapsedTime = 0;
		
		seat.onCustomerChangeMindFocus(thinkingPhysics);
	}
	
	static final float CUSTOMER_WIDTH = 204;
	static final float CUSTOMER_HEIGHT = 210;
	public static float INITIAL_POSITION_X = 720;
	public static float INITIAL_POSITION_Y = 67;
	public static final float WALK_OUT_X = -205;
	
	public void doneWaiting() {
		expectedBrunch.reset();
		setMood(MoodState.ANGRY);
		
		onLeave();
		
		if (Customer.Type.FOOD_CRITIC == type) {
			GameEventSystem.scheduleEvent(GameEvent.CUSTOMER_FOOD_CRITIC_WAIT_OUT, 0, this);
		}
		
		// notify service result
		CustomerManager.ServiceResult result = CustomerManager.ServiceResult.serveFailed(this);
		GameEventSystem.scheduleEvent(GameEvent.CUSTOMER_MANAGER_SERVICE_RESULT, 0, result);
	}
	
	public void onLeave() {
		if (host != null) {
			host.startLeaving(this);
		}
		
		seat.onCustomerStartLeaving(this);
		
		walk(WALK_OUT_X);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " type:" + type + ", box:" + box + ", expectedBrunch:" + expectedBrunch;
	}
	
	private void loadSounds() {
		if (Type.JOGGING_GIRL == type) {
			soundHello = SoundSystem.getInstance().load("guest_01_sound_s1");
		} else if (Type.WORKING_MAN == type) {
			soundHello = SoundSystem.getInstance().load("guest_02_sound_s1");
		} else if (Type.BALLOON_BOY == type) {
			soundHello = SoundSystem.getInstance().load("guest_04_sound_s1");
		} else if (Type.GIRL_WITH_DOG == type) {
			soundHello = SoundSystem.getInstance().load("guest_05_sound_s1");
		} else if (Type.FOOD_CRITIC == type) {
			soundHello = SoundSystem.getInstance().load("guest_06_sound_s1");
		} else if (Type.SUPERSTAR_LADY == type) {
			soundHello = SoundSystem.getInstance().load("guest_07_sound_s1");
		} else if (Type.PHYSICIST == type) {
			soundHello = SoundSystem.getInstance().load("guest_08_sound_s1");
		} else if (Type.TRAMP == type) {
			soundHello = SoundSystem.getInstance().load("guest_09_sound_s1");
		} else if (Type.SUPERSTAR_MAN == type) {
			soundHello = SoundSystem.getInstance().load("guest_12_sound_s1");
		}
		
		soundGetBrunchNormal = SoundSystem.getInstance().load("game_table_customer_s1");
		soundGetBrunchExcited = SoundSystem.getInstance().load("game_table_customer_s3");
		if (Type.GIRL_WITH_DOG == type) {
			soundDogBark = SoundSystem.getInstance().load("guest_05_puppy_s1");
		}
	}
}
