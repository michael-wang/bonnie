package com.studioirregular.bonniesbrunch.entity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.RectF;
import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.FoodSystem.Brunch;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.GameScoreSystem;
import com.studioirregular.bonniesbrunch.InputSystem.TouchEvent;
import com.studioirregular.bonniesbrunch.LevelSystem.GameLevel;
import com.studioirregular.bonniesbrunch.SoundSystem;
import com.studioirregular.bonniesbrunch.SoundSystem.Sound;
import com.studioirregular.bonniesbrunch.base.ObjectArray;
import com.studioirregular.bonniesbrunch.base.ObjectArray.OutOfCapacityException;
import com.studioirregular.bonniesbrunch.base.ObjectBase;
import com.studioirregular.bonniesbrunch.component.FixedCustomerGenerator;
import com.studioirregular.bonniesbrunch.component.RandomCustomerGenerator;
import com.studioirregular.bonniesbrunch.entity.Customer.CustomerAnimations;
import com.studioirregular.bonniesbrunch.entity.Customer.CustomerHost;
import com.studioirregular.bonniesbrunch.entity.Customer.CustomerParameters;


public class CustomerManager extends GameEntity implements CustomerHost {

	private static final String TAG = "customer-manager";
	
	public static class SeatConfig {
		public SeatConfig(float x, float y, float w, float h) {
			this(x, y, w, h, false);
		}
		
		public SeatConfig(float x, float y, float w, float h, boolean allowLineUp) {
			this.x = x;
			this.y = y;
			this.width = w;
			this.height = h;
			this.allowLineUp = allowLineUp;
		}
		
		public float x;
		public float y;
		public float width;
		public float height;
		public boolean allowLineUp;
	}
	
	public static class CustomerSpec {
		public static enum Mode {
			Normal,
			SpecialLevel,
			Tutorial
		}
		
		public Customer.Type type;
		public Customer.OrderingSpec orderSpec;
		public Mode mode = Mode.Normal;
		public int assignSeat = -1;			// for tutorial mode.
		public int reduceInitialPatient;	// for tutorial mode.
		
		public CustomerSpec(Customer.Type type, Customer.OrderingSpec orderSpec) {
			this(type, orderSpec, Mode.Normal, -1);
		}
		
		public CustomerSpec(Customer.Type type, Customer.OrderingSpec orderSpec, Mode mode, int seatIndex) {
			this(type, orderSpec, mode, seatIndex, 0);
		}
		
		public CustomerSpec(Customer.Type type, Customer.OrderingSpec orderSpec, Mode mode, int seatIndex, int reduceInitialPatient) {
			this.type = type;
			this.orderSpec = orderSpec;
			this.mode = mode;
			this.assignSeat = seatIndex;
			this.reduceInitialPatient = reduceInitialPatient;
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + " type:" + Customer.CUSTOMER_TYPE_NAME.get(type) + ", orderSpec:" + orderSpec + ",mode:" + mode + ",assignSeat:" + assignSeat;
		}
		
	}
	
	public static class ServiceResult {
		public static ServiceResult served(int earnedMoney, int tipMoney, Customer customer) {
			ServiceResult result = new ServiceResult();
			result.customer = customer;
			result.served = true;
			result.earnedMoney = earnedMoney;
			result.tipMoney = tipMoney;
			return result;
		}
		
		public static ServiceResult serveFailed(Customer customer) {
			ServiceResult result = new ServiceResult();
			result.customer = customer;
			result.served = false;
			result.earnedMoney = 0;
			result.tipMoney = 0;
			return result;
		}
		
		public Customer customer;
		public boolean served;
		public int earnedMoney;
		public int tipMoney;
	}
	
	public CustomerManager(int zOrder) {
		super(zOrder);
	}
	
	public void debugSetup(CustomerSpec[] customerTriggers) {
		if (customerTriggers != null && customerTriggers.length > 0) {
			customerButtons = new CustomerButton[customerTriggers.length];
			
			float x = TRIGGER_BUTTON_INITIAL_X;
			float y = TRIGGER_BUTTON_INITIAL_Y;
			for (int i = 0; i < customerTriggers.length; i++) {
				CustomerSpec trigger = customerTriggers[i];
				CustomerButton button = new CustomerButton(TRIGGER_BUTTON_Z_ORDER);
				button.setup(x, y, TRIGGER_BUTTON_WIDTH, TRIGGER_BUTTON_HEIGHT);
				button.setup(this, trigger);
				
				x -= (TRIGGER_BUTTON_WIDTH + TRIGGER_BUTTON_GAP);
				
				customerButtons[i] = button;
			}
			customerButtonsAdded = false;
		}
	}
	
	public void setup(GameLevel gameLevel, SeatConfig[] seatConfigs, boolean isSpecialLevel) {
		final int SEAT_COUNT = seatConfigs.length;
		seats = new CustomerSeat[SEAT_COUNT];
		scoreEffects = new MoneyEffect[SEAT_COUNT];
		tipEffects = new MoneyEffect[SEAT_COUNT];
		
		for (int i = 0; i < seatConfigs.length; i++) {
			SeatConfig config = seatConfigs[i];
			CustomerSeat seat = new CustomerSeat(CUSTOMER_SEAT_Z_ORDER);
			seat.setup(config.x, config.y, config.width, config.height, config.allowLineUp, gameLevel.specialLevel);
			add(seat);
			seats[i] = seat;
			
			MoneyEffect scoreEffect = setupMoneyEffect();
			add(scoreEffect);
			scoreEffect.hide();
			scoreEffects[i] = scoreEffect;
			
			MoneyEffect tipEffect = setupTipEffect();
			add(tipEffect);
			tipEffect.hide();
			tipEffects[i] = tipEffect;
		}
		
		seatWorkspace = new ObjectArray<Integer>(seats.length);
		
		randomCustomerGenerator.setup(gameLevel.time, gameLevel.specialLevel, gameLevel.randomAppearingCustomers);
		add(randomCustomerGenerator);
		
		fixedCustomerGenerator.setup(gameLevel.time, gameLevel.specialLevel, gameLevel.fixedTimeAppearingCustomers);
		add(fixedCustomerGenerator);
		
		soundBrunchRejected = SoundSystem.getInstance().load("game_table_customer_s2");
		soundSuperLadyArrived = SoundSystem.getInstance().load("mission_guest_07_s1");
		soundTrampArrived = SoundSystem.getInstance().load("mission_guest_09_s1");
		soundFoodCriticWaitOut = SoundSystem.getInstance().load("mission_guest_06_s1");
		
		this.isSpecialLevel = isSpecialLevel;
	}
	
	public void onLevelStart() {
		randomCustomerGenerator.levelStart();
		fixedCustomerGenerator.levelStart();
	}
	
	public void onLevelStop() {
		randomCustomerGenerator.levelStop();
		fixedCustomerGenerator.levelStop();
	}
	
	public void resetLevelState() {
		for (Customer customer : customers) {
			remove(customer);
		}
		customers.clear();
	}
	
	public void setTutorialMode(boolean on) {
		tutorialMode = on;
	}
	
	public void removeAllCustomers() {
		for (Customer customer : customers) {
			remove(customer);
		}
		
		for (int i = 0; i < seats.length; i++) {
			seats[i].forceCustomerLeave();
		}
	}
	
	// notice super lady may start ordering again when she's waiting.
	@Override
	public void startOrdering(Customer customer) {
		// change customer z-order
		if (CUSTOMER_Z_ORDER_ON_SEAT != customer.zOrder) {
			customer.zOrder = CUSTOMER_Z_ORDER_ON_SEAT;
			remove(customer);
			add(customer);
		}
	}
	
	@Override
	public void madeDecision(Customer customer) {
		if (tutorialMode) {
			GameEventSystem.scheduleEvent(GameEvent.TUTORIAL_CUSTOMER_MADE_DECISION, customer.type.ordinal());
		}
	}
	
	@Override
	public void onReceivingBrunch(Customer customer) {
		if (tutorialMode) {
			GameEventSystem.scheduleEvent(GameEvent.TUTORIAL_CUSTOMER_EVENT);
		}
	}
	
	@Override
	public void startLeaving(Customer customer) {
		// change customer z-order
		customer.zOrder = CUSTOMER_Z_ORDER_LEAVING;
		remove(customer);
		add(customer);
	}
	
	@Override
	public void customerLeaved(Customer customer) {
		customers.remove(customer);
		remove(customer);
		
		if (tutorialMode) {
			GameEventSystem.scheduleEvent(GameEvent.TUTORIAL_CUSTOMER_EVENT);
		}
	}
	
	// construct/obtain customer and make it running.
	public void requestAddCustomer(CustomerSpec spec) {
		if (Config.DEBUG_LOG) Log.d(TAG, "requestAddCustomer spec:" + spec);
		
		int seatIndex = -1;
		if (CustomerSpec.Mode.Tutorial == spec.mode) {
			seatIndex = spec.assignSeat;
		} else {
			seatIndex = randomlyPickEmptySeat();
		}
		
		if (seatIndex < 0 || seats.length <= seatIndex) {
			if (Config.DEBUG_LOG) Log.w(TAG, "requestAddCustomer no empty seat for customer:" + Customer.CUSTOMER_TYPE_NAME.get(spec.type) + " on game elapsed time:" + gameElapsedTime);
			return;
		}
		
		// TODO: move customer creation to factory.
		Customer customer = setupCustomer(spec.type, CUSTOMER_Z_ORDER_ENTERING);
		if (CustomerSpec.Mode.SpecialLevel == spec.mode) {
			adjustCustomerParametersForSpecialLevel(customer);
		} else if (CustomerSpec.Mode.Tutorial == spec.mode) {
			adjustCustomerParametersForTutorial(customer);
		}
		customer.setup(spec.orderSpec, seats[seatIndex], this);
		customer.start();
		if (spec.reduceInitialPatient != 0) {
			customer.reducePatient(spec.reduceInitialPatient);
			customer.updateMoodState();
		}
		//customer.drawEntityRegion(0.8f, 0.8f, 1.0f, 0.5f);
		add(customer);
		customers.add(customer);
		
		if (seats[seatIndex].serve(customer) == false) {
			Log.e(TAG, "requestAddCustomer failed to reserve seat:" + seats[seatIndex]);
		}
	}
	
	public boolean isAnyPendingCustomer() {
		for (int i = 0; i < objects.size(); i++) {
			ObjectBase obj = objects.get(i);
			if (obj instanceof Customer) {
				return true;
			}
		}
		return false;
	}
	
	private int randomlyPickEmptySeat() {
		seatWorkspace.clear();
		try {
			for (int i = 0; i < seats.length; i++) {
				if (seats[i].available()) {
					seatWorkspace.add(i);
				}
			}
		} catch (OutOfCapacityException e) {
			return -1;
		}
		
		// no empty seat...
		if (seatWorkspace.size() == 0) {
			return -1;
		}
		
		int pickIndex = (int)(Math.random() * seatWorkspace.size());
		return seatWorkspace.get(pickIndex);
	}
	
	@Override
	public boolean dispatch(TouchEvent event, GameEntity parent) {
		if (customerButtonsAdded) {
			return super.dispatch(event, parent);
		}
		// customers won't handle any touch event.
		return false;
	}
	
	@Override
	protected void getRegionColor(float[] color) {
		color[0] = 0.5f;
		color[1] = 1.0f;
		color[2] = 0.5f;
		color[3] = 0.5f;
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		if (Customer.isCustomerEvent(event)) {
			return true;
		} else if (GameEvent.DROP_GAME_ENTITY == event.what) {
			GameEntity entity = (GameEntity)event.obj;
			if (RectF.intersects(box, entity.box)) {
				return true;
			}
			if (Config.DEBUG_LOG) Log.w(TAG, "food drop event passed, food.box:" + entity.box);
		} else {
			switch (event.what) {
			case GameEvent.CUSTOMER_MANAGER_ADD_CUSTOMER:
			case GameEvent.CUSTOMER_MANAGER_SERVICE_RESULT:
			case GameEvent.ENTITY_DONE:
			case GameEvent.GAME_CLOCK_START:
			case GameEvent.DEBUG_TOGGLE_CUSTOMER_BUTTONS:
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		if (Config.DEBUG_LOG) Log.d(TAG, "handleGameEvent event:" + event);
		if (Customer.isCustomerEvent(event)) {
			handleCustomerEvent(event);
		} else if (GameEvent.CUSTOMER_MANAGER_ADD_CUSTOMER == event.what) {
			CustomerSpec spec = (CustomerSpec)event.obj;
			requestAddCustomer(spec);
		} else if (GameEvent.CUSTOMER_MANAGER_SERVICE_RESULT == event.what) {
			ServiceResult result = (ServiceResult)event.obj;
			GameScoreSystem.getInstance().accumulateCustomer(result.served);
			
			if (result.served) {
				int earnedMoney = result.earnedMoney;
				int tipMoney = result.tipMoney;
				GameScoreSystem.getInstance().accumulate(earnedMoney, tipMoney);
				GameEventSystem.scheduleEvent(GameEvent.GAME_SCORE_ADD, earnedMoney + tipMoney);
				
				doServiceResultEffects(result.customer, earnedMoney, tipMoney);
			}
		} else if (GameEvent.DROP_GAME_ENTITY == event.what) {
			handleDropFood((GameEntity)event.obj);
		} else if (GameEvent.ENTITY_DONE == event.what) {
			if (event.obj != null && event.obj instanceof GameEntity) {
				((GameEntity)event.obj).hide();
			}
		} else if (GameEvent.DEBUG_TOGGLE_CUSTOMER_BUTTONS == event.what) {
			if (customerButtons != null && customerButtons.length > 0) {
				if (customerButtonsAdded) {
					removeCustomerButtons();
				} else {
					addCustomerButtons();
				}
			}
		} else if (GameEvent.GAME_CLOCK_START == event.what) {
			clockStarted = true;
		}
	}
	
	@Override
	public void update(long timeDelta, ObjectBase parent) {
		super.update(timeDelta, parent);
		
		if (clockStarted) {
			gameElapsedTime += timeDelta;
		}
	}
	
	private void removeCustomerButtons() {
		for (int i = 0; i < customerButtons.length; i++) {
			remove(customerButtons[i]);
		}
		customerButtonsAdded = false;
	}
	
	private void addCustomerButtons() {
		for (int i = 0; i < customerButtons.length; i++) {
			add(customerButtons[i]);
		}
		customerButtonsAdded = true;
	}
	
	private void handleCustomerEvent(GameEvent event) {
		if (Config.DEBUG_LOG) Log.d(TAG, "handleCustomerEvent event:" + event);
		
		switch (event.what) {
		case GameEvent.CUSTOMER_SUPER_LADY_ARRIVED:
			SoundSystem.getInstance().playSound(soundSuperLadyArrived, false);
			break;
		case GameEvent.CUSTOMER_TRAMP_ARRIVED:
			SoundSystem.getInstance().playSound(soundTrampArrived, false);
			break;
		case GameEvent.CUSTOMER_FOOD_CRITIC_WAIT_OUT:
			SoundSystem.getInstance().playSound(soundFoodCriticWaitOut, false);
			break;
		}
		
		for (int i = 0; i < seats.length; i++) {
			seats[i].sendEventToFirstCustomer(event);
		}
	}
	
	// food could be brunch or candy.
	private void handleDropFood(GameEntity food) {
		if (Config.DEBUG_LOG) Log.d(TAG, "handleDropFood food:" + food);
		
		boolean accepted = false;
		boolean rejectedByCustomer = false;
		for (Customer customer : customers) {
			// skip customers not waiting for brunch.
			if (Customer.ActionState.WAIT != customer.action) {
				if (isExpectedFood(food, customer)) {
					if (Config.DEBUG_LOG) Log.w(TAG, "food rejected for customer is not waiting, but is:" + customer.action);
				}
				continue;
			}
			
			// skip if customer and food's bounding box not intersect.
			if (RectF.intersects(food.box, customer.box) == false) {
				continue;
			}
			
			if (food instanceof Candy) {
				if (customer.eatCandy((Candy)food)) {
					accepted = true;
					break;
				} else {
					rejectedByCustomer = true;
				}
			} else if (food instanceof BrunchEntity) {
				Brunch brunch = ((BrunchEntity)food).getBrunch();
				if (customer.receiveBrunch(brunch)) {
					accepted = true;
					break;
				} else {
					rejectedByCustomer = true;
				}
			}
			
			if (Config.DEBUG_LOG) Log.w(TAG, "food drop failed... from food:" + food + " to customer:" + customer);
		}
		
		if (accepted) {
			GameEventSystem.scheduleEvent(GameEvent.DROP_ACCEPTED, 0, food);
		} else {
			if (rejectedByCustomer) {
				SoundSystem.getInstance().playSound(soundBrunchRejected, false);
			}
			GameEventSystem.scheduleEvent(GameEvent.DROP_REJECTED, 0, food);
		}
	}
	
	private boolean isExpectedFood(GameEntity food, Customer customer) {
		if (food instanceof Candy) {
			return false;
		}
		Brunch brunch = ((BrunchEntity)food).getBrunch();
		return customer.expectedBrunch.equals(brunch);
	}
	
	private void doServiceResultEffects(Customer customer, int moneyValue, int tipValue) {
		int effectIndex = findSeatIndex(customer.seat);
		if (effectIndex < 0) {
			Log.e(TAG, "doServiceResultEffects cannot find index for seat:" + customer.seat);
			return;
		}
		
		final int animationDuration = isSpecialLevel ? 500 : 1000;
		final float x = customer.getX();
		final float y = customer.getY();
		scoreEffects[effectIndex].doEffect(moneyValue, x, y, animationDuration, true);
		
		if (tipValue > 0) {
			final int offsetY = isSpecialLevel ? 92 : 38;
			tipEffects[effectIndex].doEffect(tipValue, x, y + offsetY, animationDuration, !isSpecialLevel);
		}
	}
	
	private final int MONEY_WIDTH = 33;
	private final int MONEY_HEIGHT = 38;
	private final int MONEY_DIGITS = 4;
	private MoneyEffect setupMoneyEffect() {
		MoneyEffect effect = new MoneyEffect(MONEY_EFFECT_Z_ORDER, MONEY_WIDTH, MONEY_HEIGHT, MONEY_DIGITS);
		effect.setup(0, 0, MONEY_WIDTH * (MONEY_DIGITS + 1), MONEY_HEIGHT);
		effect.addNumber("number_m_plus", "customer-common", "number_m_");
		return effect;
	}
	
	private final int TIP_WIDTH = 24;
	private final int TIP_HEIGHT = 29;
	private final int TIP_DIGITS = 3;
	
	private final int BONUS_TEXT_WIDTH = 69;
	private final int BONUS_TEXT_HEIGHT = 21;
	// tip effect looks like:
	// bonus
	// +tip
	private MoneyEffect setupTipEffect() {
		MoneyEffect effect = new MoneyEffect(MONEY_EFFECT_Z_ORDER, TIP_WIDTH, TIP_HEIGHT, TIP_DIGITS);
		effect.setup(0, 0, TIP_WIDTH * (TIP_DIGITS + 1), BONUS_TEXT_HEIGHT + TIP_HEIGHT);
		effect.addRenderComponent(BONUS_TEXT_WIDTH, BONUS_TEXT_HEIGHT, "number_bonus_word");
		effect.addNumber("number_bonus_plus", "customer-common", "number_bonus_");
		return effect;
	}
	
	private int findSeatIndex(CustomerSeat seat) {
		for (int i = 0; i < seats.length; i++) {
			if (seats[i] == seat) {
				return i;
			}
		}
		return -1;
	}
	
	private static final int CUSTOMER_Z_ORDER_LEAVING = MIN_GAME_ENTITY_Z_ORDER;
	private static final int CUSTOMER_Z_ORDER_ENTERING = MIN_GAME_ENTITY_Z_ORDER + 1;
	private static final int CUSTOMER_Z_ORDER_ON_SEAT = MIN_GAME_ENTITY_Z_ORDER + 2;
	private static final int CUSTOMER_SEAT_Z_ORDER = MIN_GAME_ENTITY_Z_ORDER + 3;
	private static final int MONEY_EFFECT_Z_ORDER = MIN_GAME_ENTITY_Z_ORDER + 4;
	private static final int TRIGGER_BUTTON_Z_ORDER = MIN_GAME_ENTITY_Z_ORDER + 5;
	
	private static final float TRIGGER_BUTTON_GAP = 6;
	private static final float TRIGGER_BUTTON_WIDTH = 57;
	private static final float TRIGGER_BUTTON_HEIGHT = 57;
	private static final float TRIGGER_BUTTON_INITIAL_Y = TRIGGER_BUTTON_GAP;
	private static final float TRIGGER_BUTTON_INITIAL_X = 645 - TRIGGER_BUTTON_WIDTH - TRIGGER_BUTTON_GAP;
	
	// TODO: remove gameElapsedTime for release version.
	private boolean clockStarted = false;
	private int gameElapsedTime;
	private boolean tutorialMode = false;
	private boolean isSpecialLevel = false;
	
	private CustomerSeat[] seats;// = new CustomerSeat[SEATS_COUNT];
	private ObjectArray<Integer> seatWorkspace;// = new ObjectArray<Integer>(SEATS_COUNT);
	
	private MoneyEffect[] scoreEffects;
	private MoneyEffect[] tipEffects;
	
	// to send customer events.
	private List<Customer> customers = new ArrayList<Customer>();
	
	private CustomerButton[] customerButtons;
	private boolean customerButtonsAdded = false;
	
	private RandomCustomerGenerator randomCustomerGenerator = new RandomCustomerGenerator(MIN_GAME_COMPONENT_Z_ORDER);
	private FixedCustomerGenerator fixedCustomerGenerator = new FixedCustomerGenerator(MIN_GAME_COMPONENT_Z_ORDER);
	
	private Sound soundBrunchRejected;
	private Sound soundSuperLadyArrived;
	private Sound soundTrampArrived;
	private Sound soundFoodCriticWaitOut;
	
	// populate customer context: config & animations.
	// looks like factory class' job.
	// TODO: consider move this method to factory class.
	public static Customer setupCustomer(Customer.Type type, int zOrder) {
		Customer customer = new Customer(zOrder);
		
		customer.type = type;
		CustomerParameters config = customer.config;
		CustomerAnimations animations = customer.animations;
		
		if (Customer.Type.JOGGING_GIRL == type) {
			config.walkSpeed = 4 * CustomerParameters.WALK_SPEED_PER_SECOND;
			config.orderDelay = 1000;
			config.angryPatience = 1;
			config.unhappyPatience = 3;
			config.totalPatience = 4;
			config.tip = 3;
			
			animations.normal.addFrame("game_guest_01_w1_normal_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 300);
			animations.normal.addFrame("game_guest_01_w1_normal_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 300);
			animations.normal.addFrame("game_guest_01_w1_normal_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 300);
			animations.normal.addFrame("game_guest_01_w1_normal_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 300);
			
			animations.excited.addFrame("game_guest_01_r1_excited_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.excited.addFrame("game_guest_01_r1_excited_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.excited.addFrame("game_guest_01_r1_excited_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.excited.addFrame("game_guest_01_r1_excited_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			
			animations.unhappy.addFrame("game_guest_01_w2_unhappy_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 500);
			animations.unhappy.addFrame("game_guest_01_w2_unhappy_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 500);
			animations.unhappy.addFrame("game_guest_01_w2_unhappy_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 500);
			animations.unhappy.addFrame("game_guest_01_w2_unhappy_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 500);
			animations.unhappy.addFrame("game_guest_01_w2_unhappy_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 100);
			animations.unhappy.addFrame("game_guest_01_w2_unhappy_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 50);
			animations.unhappy.addFrame("game_guest_01_w2_unhappy_5", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 300);
			animations.unhappy.addFrame("game_guest_01_w2_unhappy_4", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 100);
			animations.unhappy.addFrame("game_guest_01_w2_unhappy_5", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 100);
			animations.unhappy.addFrame("game_guest_01_w2_unhappy_4", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 100);
			
			animations.angry.addFrame("game_guest_01_w3_angry_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.angry.addFrame("game_guest_01_w3_angry_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			
			animations.sick.addFrame("game_guest_01_s1_sick_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.sick.addFrame("game_guest_01_s1_sick_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.sick.addFrame("game_guest_01_s1_sick_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
		} else if (Customer.Type.WORKING_MAN == type) {
			config.walkSpeed = 5 * CustomerParameters.WALK_SPEED_PER_SECOND;
			config.orderDelay = 1000;
			config.angryPatience = 1;
			config.unhappyPatience = 3;
			config.totalPatience = 4;
			config.tip = 4;
			
			animations.normal.addFrame("game_guest_02_w1_normal_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 1000);
			animations.normal.addFrame("game_guest_02_w1_normal_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.normal.addFrame("game_guest_02_w1_normal_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.normal.addFrame("game_guest_02_w1_normal_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.normal.addFrame("game_guest_02_w1_normal_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.normal.addFrame("game_guest_02_w1_normal_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			
			animations.excited.addFrame("game_guest_02_r1_excited_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.excited.addFrame("game_guest_02_r1_excited_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 600);
			
			animations.unhappy.addFrame("game_guest_02_w2_unhappy_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.unhappy.addFrame("game_guest_02_w2_unhappy_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.unhappy.addFrame("game_guest_02_w2_unhappy_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.unhappy.addFrame("game_guest_02_w2_unhappy_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 1000);
			
			animations.angry.addFrame("game_guest_02_w3_angry_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.angry.addFrame("game_guest_02_w3_angry_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			
			animations.sick.addFrame("game_guest_02_s1_sick_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 500);
			animations.sick.addFrame("game_guest_02_s1_sick_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
		} else if (Customer.Type.BALLOON_BOY == type) {
			config.walkSpeed = 3 * CustomerParameters.WALK_SPEED_PER_SECOND;
			config.orderDelay = 2000;
			config.angryPatience = 1;
			config.unhappyPatience = 3;
			config.totalPatience = 5;
			config.tip = 1;
			
			animations.normal.addFrame("game_guest_04_w1_normal_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.normal.addFrame("game_guest_04_w1_normal_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.normal.addFrame("game_guest_04_w1_normal_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.normal.addFrame("game_guest_04_w1_normal_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			
			animations.excited.addFrame("game_guest_04_r1_excited_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.excited.addFrame("game_guest_04_r1_excited_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.excited.addFrame("game_guest_04_r1_excited_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			
			animations.unhappy.addFrame("game_guest_04_w2_unhappy_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.unhappy.addFrame("game_guest_04_w2_unhappy_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.unhappy.addFrame("game_guest_04_w2_unhappy_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.unhappy.addFrame("game_guest_04_w2_unhappy_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.unhappy.addFrame("game_guest_04_w2_unhappy_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			
			animations.angry.addFrame("game_guest_04_w3_angry_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.angry.addFrame("game_guest_04_w3_angry_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			
			animations.sick.addFrame("game_guest_04_s1_sick_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.sick.addFrame("game_guest_04_s1_sick_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.sick.addFrame("game_guest_04_s1_sick_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.sick.addFrame("game_guest_04_s1_sick_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
		} else if (Customer.Type.GIRL_WITH_DOG == type) {
			config.walkSpeed = 2 * CustomerParameters.WALK_SPEED_PER_SECOND;
			config.orderDelay = 2000;
			config.angryPatience = 1;
			config.unhappyPatience = 3;
			config.totalPatience = 5;
			config.tip = 2;
			
			animations.normal.addFrame("game_guest_05_w1_normal_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.normal.addFrame("game_guest_05_w1_normal_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.normal.addFrame("game_guest_05_w1_normal_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.normal.addFrame("game_guest_05_w1_normal_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			
			animations.excited.addFrame("game_guest_05_r1_excited_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 270);
			animations.excited.addFrame("game_guest_05_r1_excited_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 270);
			animations.excited.addFrame("game_guest_05_r1_excited_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 270);
			
			animations.unhappy.addFrame("game_guest_05_w2_unhappy_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.unhappy.addFrame("game_guest_05_w2_unhappy_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.unhappy.addFrame("game_guest_05_w2_unhappy_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.unhappy.addFrame("game_guest_05_w2_unhappy_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.unhappy.addFrame("game_guest_05_w2_unhappy_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			
			animations.angry.addFrame("game_guest_05_w3_angry_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.angry.addFrame("game_guest_05_w3_angry_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			
			animations.sick.addFrame("game_guest_05_s1_sick_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.sick.addFrame("game_guest_05_s1_sick_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.sick.addFrame("game_guest_05_s1_sick_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.sick.addFrame("game_guest_05_s1_sick_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
		} else if (Customer.Type.GRANNY == type) {
			config.walkSpeed = 1 * CustomerParameters.WALK_SPEED_PER_SECOND;
			config.orderDelay = 4000;
			config.angryPatience = 1;
			config.unhappyPatience = 3;
			config.totalPatience = 5;
			config.tip = 3;
			
			animations.normal.addFrame("game_guest_11_w1_normal_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 400);
			animations.normal.addFrame("game_guest_11_w1_normal_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 100);
			animations.normal.addFrame("game_guest_11_w1_normal_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 100);
			animations.normal.addFrame("game_guest_11_w1_normal_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 100);
			animations.normal.addFrame("game_guest_11_w1_normal_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 100);
			animations.normal.addFrame("game_guest_11_w1_normal_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 100);
			
			animations.excited.addFrame("game_guest_11_r1_excited_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.excited.addFrame("game_guest_11_r1_excited_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 400);
			
			animations.unhappy.addFrame("game_guest_11_w2_unhappy_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 500);
			animations.unhappy.addFrame("game_guest_11_w2_unhappy_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 500);
			
			animations.angry.addFrame("game_guest_11_w3_angry_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.angry.addFrame("game_guest_11_w3_angry_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			
			animations.sick.addFrame("game_guest_11_s1_sick_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.sick.addFrame("game_guest_11_s1_sick_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
		} else if (Customer.Type.SUPERSTAR_MAN == type) {
			config.walkSpeed = 5 * CustomerParameters.WALK_SPEED_PER_SECOND;
			config.orderDelay = 0;
			config.angryPatience = 1;
			config.unhappyPatience = 2;
			config.totalPatience = 3;
			config.tip = 3;
			
			animations.normal.addFrame("game_guest_12_w1_normal_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.normal.addFrame("game_guest_12_w1_normal_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 100);
			animations.normal.addFrame("game_guest_12_w1_normal_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.normal.addFrame("game_guest_12_w1_normal_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.normal.addFrame("game_guest_12_w1_normal_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.normal.addFrame("game_guest_12_w1_normal_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 100);
			animations.normal.addFrame("game_guest_12_w1_normal_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 100);
			animations.normal.addFrame("game_guest_12_w1_normal_4", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			
			animations.excited.addFrame("game_guest_12_r1_excited_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.excited.addFrame("game_guest_12_r1_excited_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			
			animations.unhappy.addFrame("game_guest_12_w2_unhappy_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.unhappy.addFrame("game_guest_12_w2_unhappy_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.unhappy.addFrame("game_guest_12_w2_unhappy_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.unhappy.addFrame("game_guest_12_w2_unhappy_4", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			
			animations.angry.addFrame("game_guest_12_w3_angry_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.angry.addFrame("game_guest_12_w3_angry_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			
			animations.sick.addFrame("game_guest_12_s1_sick_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.sick.addFrame("game_guest_12_s1_sick_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.sick.addFrame("game_guest_12_s1_sick_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.sick.addFrame("game_guest_12_s1_sick_4", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
		} else if (Customer.Type.PHYSICIST == type) {
			config.walkSpeed = 1 * CustomerParameters.WALK_SPEED_PER_SECOND;
			config.orderDelay = 3000;
			config.angryPatience = 1;
			config.unhappyPatience = 3;
			config.totalPatience = 4;
			config.tip = 2;
			
			animations.normal.addFrame("game_guest_08_w1_normal_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.normal.addFrame("game_guest_08_w1_normal_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 100);
			animations.normal.addFrame("game_guest_08_w1_normal_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.normal.addFrame("game_guest_08_w1_normal_4", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 100);
			animations.normal.addFrame("game_guest_08_w1_normal_5", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			
			animations.excited.addFrame("game_guest_08_r1_excited_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.excited.addFrame("game_guest_08_r1_excited_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.excited.addFrame("game_guest_08_r1_excited_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.excited.addFrame("game_guest_08_r1_excited_4", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			
			animations.unhappy.addFrame("game_guest_08_w2_unhappy_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 150);
			animations.unhappy.addFrame("game_guest_08_w2_unhappy_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 150);
			
			animations.angry.addFrame("game_guest_08_w3_angry_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 100);
			animations.angry.addFrame("game_guest_08_w3_angry_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 100);
			
			animations.sick.addFrame("game_guest_08_s1_sick_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 150);
			animations.sick.addFrame("game_guest_08_s1_sick_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 100);
			animations.sick.addFrame("game_guest_08_s1_sick_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 150);
			animations.sick.addFrame("game_guest_08_s1_sick_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 100);
		} else if (Customer.Type.SUPERSTAR_LADY == type) {
			config.walkSpeed = 2 * CustomerParameters.WALK_SPEED_PER_SECOND;
			config.orderDelay = 2000;
			config.angryPatience = 1;
			config.unhappyPatience = 3;
			config.totalPatience = 5;
			config.tip = 5;
			
			animations.normal.addFrame("game_guest_07_w1_normal_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 500);
			animations.normal.addFrame("game_guest_07_w1_normal_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 500);
			animations.normal.addFrame("game_guest_07_w1_normal_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 500);
			animations.normal.addFrame("game_guest_07_w1_normal_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 500);
			animations.normal.addFrame("game_guest_07_w1_normal_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 500);
			animations.normal.addFrame("game_guest_07_w1_normal_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.normal.addFrame("game_guest_07_w1_normal_4", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.normal.addFrame("game_guest_07_w1_normal_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.normal.addFrame("game_guest_07_w1_normal_4", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 500);
			
			animations.excited.addFrame("game_guest_07_r1_excited_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.excited.addFrame("game_guest_07_r1_excited_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 500);
			
			animations.unhappy.addFrame("game_guest_07_w2_unhappy_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 500);
			animations.unhappy.addFrame("game_guest_07_w2_unhappy_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 500);
			
			animations.angry.addFrame("game_guest_07_w3_angry_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.angry.addFrame("game_guest_07_w3_angry_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			
			animations.sick.addFrame("game_guest_07_s1_sick_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 500);
			animations.sick.addFrame("game_guest_07_s1_sick_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 500);
		} else if (Customer.Type.FOOD_CRITIC == type) {
			config.walkSpeed = 3 * CustomerParameters.WALK_SPEED_PER_SECOND;
			config.orderDelay = 0;
			config.angryPatience = 1;
			config.unhappyPatience = 2;
			config.totalPatience = 3;
			config.tip = 3;
			
			animations.normal.addFrame("game_guest_06_w1_normal_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 500);
			animations.normal.addFrame("game_guest_06_w1_normal_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 500);
			animations.normal.addFrame("game_guest_06_w1_normal_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 500);
			animations.normal.addFrame("game_guest_06_w1_normal_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 500);
			animations.normal.addFrame("game_guest_06_w1_normal_4", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.normal.addFrame("game_guest_06_w1_normal_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 100);
			animations.normal.addFrame("game_guest_06_w1_normal_4", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 100);
			animations.normal.addFrame("game_guest_06_w1_normal_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 100);
			animations.normal.addFrame("game_guest_06_w1_normal_4", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 100);
			animations.normal.addFrame("game_guest_06_w1_normal_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			
			animations.excited.addFrame("game_guest_06_r1_excited_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.excited.addFrame("game_guest_06_r1_excited_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 500);
			
			animations.unhappy.addFrame("game_guest_06_w2_unhappy_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.unhappy.addFrame("game_guest_06_w2_unhappy_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 100);
			animations.unhappy.addFrame("game_guest_06_w2_unhappy_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.unhappy.addFrame("game_guest_06_w2_unhappy_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 100);
			
			animations.angry.addFrame("game_guest_06_w3_angry_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.angry.addFrame("game_guest_06_w3_angry_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			
			animations.sick.addFrame("game_guest_06_s1_sick_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.sick.addFrame("game_guest_06_s1_sick_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.sick.addFrame("game_guest_06_s1_sick_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.sick.addFrame("game_guest_06_s1_sick_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
		} else if (Customer.Type.TRAMP == type) {
			config.walkSpeed = 3 * CustomerParameters.WALK_SPEED_PER_SECOND;
			config.orderDelay = 3000;
			config.angryPatience = 1;
			config.unhappyPatience = 3;
			config.totalPatience = 5;
			config.tip = 0;
			
			animations.normal.addFrame("game_guest_09_w1_normal_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.normal.addFrame("game_guest_09_w1_normal_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.normal.addFrame("game_guest_09_w1_normal_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.normal.addFrame("game_guest_09_w1_normal_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.normal.addFrame("game_guest_09_w1_normal_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.normal.addFrame("game_guest_09_w1_normal_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.normal.addFrame("game_guest_09_w1_normal_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.normal.addFrame("game_guest_09_w1_normal_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.normal.addFrame("game_guest_09_w1_normal_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.normal.addFrame("game_guest_09_w1_normal_4", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.normal.addFrame("game_guest_09_w1_normal_5", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.normal.addFrame("game_guest_09_w1_normal_6", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.normal.addFrame("game_guest_09_w1_normal_4", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.normal.addFrame("game_guest_09_w1_normal_5", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.normal.addFrame("game_guest_09_w1_normal_6", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			
			animations.excited.addFrame("game_guest_09_r1_excited_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.excited.addFrame("game_guest_09_r1_excited_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.excited.addFrame("game_guest_09_r1_excited_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.excited.addFrame("game_guest_09_r1_excited_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			
			animations.unhappy.addFrame("game_guest_09_w2_unhappy_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.unhappy.addFrame("game_guest_09_w2_unhappy_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.unhappy.addFrame("game_guest_09_w2_unhappy_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.unhappy.addFrame("game_guest_09_w2_unhappy_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.unhappy.addFrame("game_guest_09_w2_unhappy_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.unhappy.addFrame("game_guest_09_w2_unhappy_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.unhappy.addFrame("game_guest_09_w2_unhappy_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.unhappy.addFrame("game_guest_09_w2_unhappy_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			animations.unhappy.addFrame("game_guest_09_w2_unhappy_3", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 200);
			
			animations.angry.addFrame("game_guest_09_w3_angry_1", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
			animations.angry.addFrame("game_guest_09_w3_angry_2", Customer.CUSTOMER_WIDTH, Customer.CUSTOMER_HEIGHT, 250);
		}
		
		return customer;
	}
	
	private static void adjustCustomerParametersForSpecialLevel(Customer customer) {
		Customer.CustomerParameters params = customer.config;
		
		params.walkSpeed = 5 * CustomerParameters.WALK_SPEED_PER_SECOND;
		params.orderDelay = 0;
		params.dropPatient = false;
		params.delayBeforeLeave = 0;
		params.doEffect = false;
	}
	
	private static void adjustCustomerParametersForTutorial(Customer customer) {
		customer.config.dropPatient = false;
	}

}
