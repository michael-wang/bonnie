package com.studioirregular.bonniesbrunch.test;

import junit.framework.TestCase;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.studioirregular.bonniesbrunch.FoodSystem.Brunch;
import com.studioirregular.bonniesbrunch.FoodSystem.Food;
import com.studioirregular.bonniesbrunch.FoodSystem.FoodSet;
import com.studioirregular.bonniesbrunch.FoodSystem.FoodType;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.entity.Candy;
import com.studioirregular.bonniesbrunch.entity.CandyHolder;
import com.studioirregular.bonniesbrunch.entity.Customer;
import com.studioirregular.bonniesbrunch.entity.Customer.ActionState;
import com.studioirregular.bonniesbrunch.entity.Customer.CustomerParameters;
import com.studioirregular.bonniesbrunch.entity.Customer.DecideBrunchException;
import com.studioirregular.bonniesbrunch.entity.Customer.MoodState;
import com.studioirregular.bonniesbrunch.entity.Customer.Type;
import com.studioirregular.bonniesbrunch.entity.CustomerManager;
import com.studioirregular.bonniesbrunch.entity.CustomerSeat;
import com.studioirregular.bonniesbrunch.entity.GameEntity;

public class CustomerTest extends TestCase {

	private static final String TAG = "customer-test";
	
	public void testBasicStates() {
		Customer customer = CustomerManager.setupCustomer(Type.JOGGING_GIRL, GameEntity.MIN_GAME_ENTITY_Z_ORDER);
		assertNotNull(customer.sickEffect);
		assertNotNull(customer.suprisedEffect);
		assertNotNull(customer.excitedEffect);
		assertEquals(Type.JOGGING_GIRL, customer.type);
		assertNotNull(customer.config);
		assertNotNull(customer.animations);
		assertNotNull(customer.animations.normal);
		assertNotNull(customer.animations.unhappy);
		assertNotNull(customer.animations.angry);
		assertNotNull(customer.animations.excited);
		assertNotNull(customer.animations.sick);
		assertNull(customer.orderSpec);
		assertEquals(ActionState.NONE, customer.action);
		assertEquals(MoodState.NONE, customer.mood);
		assertNotNull(customer.expectedBrunch);
		
		Customer.OrderingSpec orderingSpec = new Customer.OrderingSpec();
		orderingSpec.addOptional(Food.BAGEL);
		orderingSpec.addOptional(Food.CROISSANT);
		orderingSpec.addOptional(Food.MILK);
		orderingSpec.addOptional(Food.COFFEE);
		CustomerSeat seat = new CustomerSeat(0);
		seat.setup(192, 67, 204, 210);
		customer.setup(orderingSpec, seat);
		
		assertEquals(ActionState.NONE, customer.action);
		assertEquals(MoodState.NONE, customer.mood);
		assertEquals(Customer.INITIAL_POSITION_X, customer.box.left);
		assertEquals(Customer.INITIAL_POSITION_Y, customer.box.top);
		assertEquals(0, customer.waitElapsedTime);
		assertEquals(new Brunch(), customer.expectedBrunch);
		
		customer.start();
		customer.walk(192);
		assertEquals(ActionState.WALK, customer.action);
		assertEquals(MoodState.NORMAL, customer.mood);
		assertEquals(seat.getSeatPositionX(), customer.walkDestinationX);
		assertEquals(0, customer.elapsedTime);
		customer.update(0, null);
		assertEquals(1, customer.getCount());	// 1 for normal mood animation
		assertEquals(customer.animations.normal, customer.getObject(0));
		
		long requiredTime = (720 - (int)seat.getSeatPositionX()) * 1000 / (customer.config.walkSpeed);
		Log.w(TAG, "testBasicStates requiredTime:" + requiredTime);
		customer.update(requiredTime, null);
		assertEquals(seat.getSeatPositionX(), customer.box.left);
		assertEquals(ActionState.ORDER, customer.action);
		assertEquals(MoodState.NORMAL, customer.mood);
		assertEquals(0, customer.waitElapsedTime);
		
		requiredTime = customer.config.orderDelay;
		customer.update(requiredTime, null);
		assertEquals(ActionState.WAIT, customer.action);
		assertEquals(0, customer.waitElapsedTime);
		assertEquals(MoodState.NORMAL, customer.mood);
		Brunch brunch = customer.expectedBrunch;
		assertTrue("expected brunch has BAGEL or CROISSANT but got:" + brunch, brunch.contains(Food.BAGEL) || brunch.contains(Food.CROISSANT));
		// ordered brunch may or may not contains beverage.
		//assertTrue("expected brunch has MILK or COFFEE but got:" + brunch, brunch.contains(Food.MILK) || brunch.contains(Food.COFFEE));
		
		requiredTime = (customer.config.totalPatience - customer.config.unhappyPatience) * CustomerParameters.PATIENCE_TIME_UNIT;
		customer.update(requiredTime, null);
		assertEquals(MoodState.UNHAPPY, customer.mood);
		customer.update(0, null);
		assertEquals(1, customer.getCount());
		assertEquals(customer.animations.unhappy, customer.getObject(0));
		
		requiredTime = (customer.config.unhappyPatience - customer.config.angryPatience) * CustomerParameters.PATIENCE_TIME_UNIT;
		customer.update(requiredTime, null);
		assertEquals(MoodState.ANGRY, customer.mood);
		customer.update(0, null);
		assertEquals(1, customer.getCount());
		assertEquals(customer.animations.angry, customer.getObject(0));
		
		requiredTime = customer.config.angryPatience * CustomerParameters.PATIENCE_TIME_UNIT;
		customer.update(requiredTime, null);
		assertEquals(ActionState.WALK, customer.action);
		assertEquals(Customer.WALK_OUT_X, customer.walkDestinationX);
		assertEquals(MoodState.ANGRY, customer.mood);
		assertEquals(1, customer.getCount());
		assertEquals(customer.animations.angry, customer.getObject(0));
	}
	
	public void testEatCandy() {
		Customer customer = createCustomer(CUSTOMER_ORDERING);
		
		Candy candy = new Candy(0, new FakeCandyHolder(), Candy.TYPE_I);
		
		// give candy when customer ordering.
		boolean result = customer.eatCandy(candy);
		assertFalse(result);
		assertEquals(0, customer.elapsedTime);
		
		// eat candy when waiting
		customer = createCustomer(CUSTOMER_WAITING);
		result = customer.eatCandy(candy);
		assertTrue(result);
		assertTrue(customer.inTempMood);
		assertEquals(MoodState.EXCITED, customer.mood);
		customer.update(0, null);
		assertEquals(2, customer.getCount());
		assertEquals(customer.animations.excited, customer.getObject(0));
		assertEquals(customer.excitedEffect, customer.getObject(1));
		
		// wait temp excited mood over, excited effect should be removed
		long requiredTime = Customer.TEMP_MOOD_DURATION;
		customer.update(requiredTime, null);
		assertFalse(customer.inTempMood);
		MoodState correctMood = getMood(customer.config.totalPatience, customer.waitElapsedTime);
		assertEquals(correctMood, customer.mood);
		assertFalse(customer.excitedEffect.isStarted());
		customer.update(0, null);
		customer.update(0, null);
		assertEquals(1, customer.getCount());
		
		// make customer unhappy
		if (MoodState.UNHAPPY != customer.mood) {
			requiredTime = (customer.config.totalPatience - customer.config.unhappyPatience) * CustomerParameters.PATIENCE_TIME_UNIT;
			customer.update(requiredTime, null);
			correctMood = getMood(customer.config.totalPatience, customer.waitElapsedTime);
			assertEquals(correctMood, customer.mood);
		}
		
		result = customer.eatCandy(candy);
		assertTrue(result);
		customer.update(0, null);
		assertEquals(MoodState.EXCITED, customer.mood);
		assertTrue(customer.inTempMood);
		assertEquals(0, customer.tempMoodElapsedTime);
		assertTrue(customer.excitedEffect.isStarted());
		
		// wait temp mood over
		customer.update(Customer.TEMP_MOOD_DURATION, null);
		assertFalse(customer.inTempMood);
		correctMood = getMood(customer.config.totalPatience, customer.waitElapsedTime);
		assertEquals(correctMood, customer.mood);
		
		customer.update(0, null);
		customer.update(0, null);
		assertEquals(1, customer.getCount());
		assertEquals(customer.animations.getAnimation(correctMood), customer.getObject(0));
		
		// make customer angry
		requiredTime = (customer.config.totalPatience - customer.config.angryPatience) * CustomerParameters.PATIENCE_TIME_UNIT - customer.waitElapsedTime;
		customer.update(requiredTime, null);
		assertEquals(MoodState.ANGRY, customer.mood);
		customer.update(0, null);
		assertEquals(customer.animations.getAnimation(MoodState.ANGRY), customer.getObject(0));
		
		result = customer.eatCandy(candy);
		assertTrue(result);
		assertTrue(customer.inTempMood);
		customer.update(0, null);
		assertEquals(MoodState.EXCITED, customer.mood);
		
		// wait temp mood over
		customer.update(Customer.TEMP_MOOD_DURATION, null);	// wait == 3000
		assertFalse(customer.inTempMood);
		correctMood = getMood(customer.config.totalPatience, customer.waitElapsedTime);
		assertEquals(correctMood, customer.mood);
		customer.update(0, null);
		customer.update(0, null);
		assertEquals(customer.animations.getAnimation(correctMood), customer.getObject(0));
	}
	
	private class FakeCandyHolder implements CandyHolder {

		@Override
		public void getCandyLocation(Candy candy, PointF loc) {
			loc.set(0, 0);
		}

		@Override
		public RectF getHolderBox() {
			return new RectF(0, 0, 23, 23);
		}
		
	}
	
	private MoodState getMood(int customerTotalPatience, long waitElapsedTime) {
		int unhappyPatience = (customerTotalPatience > 3) ? 3 : 2;
		
		long angryDuration = (customerTotalPatience - 1) * CustomerParameters.PATIENCE_TIME_UNIT;
		long unhappyDuration = (customerTotalPatience - unhappyPatience) * CustomerParameters.PATIENCE_TIME_UNIT;
		if (angryDuration <= waitElapsedTime) {
			return MoodState.ANGRY;
		} else if (unhappyDuration <= waitElapsedTime) {
			return MoodState.UNHAPPY;
		} else {
			return MoodState.NORMAL;
		}
	}
	
	public void testReceiveBrunch() {
		Customer customer = createCustomer(CUSTOMER_WAITING);
		assertEquals(MoodState.NORMAL, customer.mood);
		
		Brunch brunch = new Brunch();
		brunch.set(customer.expectedBrunch);
		
		// test receive brunch when mood normal
		boolean result = customer.receiveBrunch(brunch);
		assertTrue(result);
		assertEquals(MoodState.EXCITED, customer.mood);
		assertEquals(ActionState.WALK, customer.action);
		
		// test reveive brunch when mood unhappy
		customer = createCustomer(CUSTOMER_WAITING);
		
		long unhappyRequiredDuration = customer.config.totalPatience > 3 ? 2 * CustomerParameters.PATIENCE_TIME_UNIT : 1 * CustomerParameters.PATIENCE_TIME_UNIT;
		customer.update(unhappyRequiredDuration, null);
		assertEquals(MoodState.UNHAPPY, customer.mood);
		
		brunch.set(customer.expectedBrunch);
		result = customer.receiveBrunch(brunch);
		assertTrue(result);
		
		assertEquals(MoodState.NORMAL, customer.mood);
		assertEquals(ActionState.WALK, customer.action);
		
		// test reveive brunch when mood angry
		customer = createCustomer(CUSTOMER_WAITING);
		
		int angryRequiredPatiencePoint = customer.config.totalPatience <= 3 ? 2 : (customer.config.totalPatience - 1);
		long angryRequiredDuration = angryRequiredPatiencePoint * CustomerParameters.PATIENCE_TIME_UNIT;
		customer.update(angryRequiredDuration, null);
		assertEquals(MoodState.ANGRY, customer.mood);
		
		brunch.set(customer.expectedBrunch);
		result = customer.receiveBrunch(brunch);
		assertTrue(result);
		
		assertEquals(MoodState.NORMAL, customer.mood);
		assertEquals(ActionState.WALK, customer.action);
		assertEquals(0, customer.elapsedTime);
	}
	
	final int CUSTOMER_START = 1;
	final int CUSTOMER_ORDERING = 2;
	final int CUSTOMER_WAITING = 3;
	private Customer createCustomer(int state) {
		Customer customer = CustomerManager.setupCustomer(Type.BALLOON_BOY, GameEntity.MIN_GAME_ENTITY_Z_ORDER);
		
		Customer.OrderingSpec orderingSpec = new Customer.OrderingSpec();
		orderingSpec.addOptional(Food.BAGEL);
		orderingSpec.addOptional(Food.CROISSANT);
		orderingSpec.addOptional(Food.MILK);
		orderingSpec.addOptional(Food.COFFEE);
		CustomerSeat seat = new CustomerSeat(0);
		seat.setup(192, 67, 204, 210);
		customer.setup(orderingSpec, seat);
		
		customer.start();
		customer.walk(192);
		if (state == CUSTOMER_START) {
			return customer;
		}
		
		long requiredTime = (720 - (int)seat.getSeatPositionX()) * 1000 / (customer.config.walkSpeed);
		customer.update(requiredTime, null);
		if (state == CUSTOMER_ORDERING) {
			return customer;
		}
		
		// start makeing order
		requiredTime = customer.config.orderDelay;
		customer.update(requiredTime, null);
		assertEquals(ActionState.WAIT, customer.action);
		
		return customer;
	}
	
	public void testCustomerEvent() {
		// test super lady arrive for walking customer
		Customer customer = createCustomer(CUSTOMER_START);
		
		GameEvent event = GameEventSystem.getInstance().obtain(GameEvent.CUSTOMER_SUPER_LADY_ARRIVED);
		boolean result = customer.onCustomerEvent(event);
		assertFalse(result);
		assertEquals(MoodState.NORMAL, customer.mood);
		
		// test super lady arrive for ordering customer
		customer = createCustomer(CUSTOMER_ORDERING);
		
		result = customer.onCustomerEvent(event);
		assertFalse(result);
		assertFalse(customer.inTempMood);
		assertEquals(MoodState.NORMAL, customer.mood);
		assertEquals(0, customer.elapsedTime);
		
		// test super lady arrive for unhappy waiting customer
		customer = createCustomer(CUSTOMER_WAITING);
		long requiredTime = (customer.config.totalPatience - customer.config.unhappyPatience) * CustomerParameters.PATIENCE_TIME_UNIT;
		customer.update(requiredTime, null);
		result = customer.onCustomerEvent(event);
		assertTrue(result);
		assertTrue(customer.inTempMood);
		assertEquals(MoodState.EXCITED, customer.mood);
		assertEquals(requiredTime - CustomerParameters.PATIENCE_TIME_UNIT, customer.waitElapsedTime);
		
		customer.update(Customer.TEMP_MOOD_DURATION, null);
		assertFalse(customer.inTempMood);
		assertEquals(getMood(customer.config.totalPatience, 2000 - CustomerParameters.PATIENCE_TIME_UNIT + Customer.TEMP_MOOD_DURATION), customer.mood);
		assertEquals(requiredTime - CustomerParameters.PATIENCE_TIME_UNIT + Customer.TEMP_MOOD_DURATION, customer.waitElapsedTime);
		
		// test tramp arrive for walking customer
		customer = createCustomer(CUSTOMER_START);
		
		event = GameEventSystem.getInstance().obtain(GameEvent.CUSTOMER_TRAMP_ARRIVED);
		result = customer.onCustomerEvent(event);
		assertFalse(result);
		assertFalse(customer.inTempMood);
		assertEquals(MoodState.NORMAL, customer.mood);
		assertEquals(0, customer.waitElapsedTime);
		
		// test tramp arrive for ordering customer
		customer = createCustomer(CUSTOMER_ORDERING);
		
		result = customer.onCustomerEvent(event);
		assertFalse(result);
		assertFalse(customer.inTempMood);
		assertEquals(MoodState.NORMAL, customer.mood);
		
		// test tramp arrive for waiting customer
		customer = createCustomer(CUSTOMER_WAITING);
		
		result = customer.onCustomerEvent(event);
		assertTrue(result);
		assertTrue(customer.inTempMood);
		assertEquals(MoodState.SICK, customer.mood);
		assertEquals(CustomerParameters.PATIENCE_TIME_UNIT, customer.waitElapsedTime);
		
		customer.update(Customer.TEMP_MOOD_DURATION, null);
		assertFalse(customer.inTempMood);
		assertEquals(CustomerParameters.PATIENCE_TIME_UNIT + Customer.TEMP_MOOD_DURATION, customer.waitElapsedTime);
		assertEquals(getMood(customer.config.totalPatience, CustomerParameters.PATIENCE_TIME_UNIT + Customer.TEMP_MOOD_DURATION), customer.mood);
		
		// test customer event after customer received brunch
		customer = createCustomer(CUSTOMER_WAITING);
		
		Brunch brunch = new Brunch();
		brunch.set(customer.expectedBrunch);
		customer.receiveBrunch(brunch);
		
		event = GameEventSystem.getInstance().obtain(GameEvent.CUSTOMER_TRAMP_ARRIVED);
		customer.send(event);
		
		customer.update(0, null);
		assertFalse(customer.inTempMood);
		assertEquals(MoodState.EXCITED, customer.mood);
		assertEquals(2, customer.getCount());
		assertEquals(customer.animations.excited, customer.getObject(0));
		assertEquals(customer.excitedEffect, customer.getObject(1));
	}
	
	public void testCriticalCondition() {
		Customer customer = createCustomer(CUSTOMER_WAITING);
		
		// test mood change and customer event happen on same frame!
		long requiredTimeToUnhappy = (customer.config.totalPatience - customer.config.unhappyPatience) * CustomerParameters.PATIENCE_TIME_UNIT;
		customer.update(requiredTimeToUnhappy, null);
		assertEquals(MoodState.UNHAPPY, customer.mood);
		assertFalse(customer.inTempMood);
		assertEquals(customer.animations.normal, customer.getObject(0));
		
		GameEvent ladyArrived = GameEventSystem.getInstance().obtain(GameEvent.CUSTOMER_SUPER_LADY_ARRIVED, 0, null);
		customer.send(ladyArrived);
		
		customer.update(0, null);
		assertEquals(MoodState.EXCITED, customer.mood);
		assertTrue(customer.inTempMood);
		assertEquals(2, customer.getCount());
		assertEquals(customer.animations.excited, customer.getObject(0));
		assertEquals(customer.excitedEffect, customer.getObject(1));
		
		// test temp mood first and patient drop event next on same frame!
		customer = createCustomer(CUSTOMER_WAITING);
		customer.update(requiredTimeToUnhappy - 10, null);
		assertEquals(MoodState.NORMAL, customer.mood);
		
		customer.send(ladyArrived);
		customer.update(10, null);
		assertTrue(customer.inTempMood);
		assertEquals(MoodState.EXCITED, customer.mood);
		assertEquals(customer.animations.excited, customer.getObject(0));
		
		// test receive brunch and temp excited event in same frame
		customer = createCustomer(CUSTOMER_WAITING);
		customer.update(10, null);
		assertEquals(MoodState.NORMAL, customer.mood);
		
		customer.send(ladyArrived);
		customer.update(0, null);	// into temp excited mood
		
		Brunch brunch = new Brunch();
		brunch.set(customer.expectedBrunch);
		customer.receiveBrunch(brunch);
		customer.update(0, null);
		assertFalse(customer.inTempMood);	// temp mood should be canceled.
		assertEquals(MoodState.EXCITED, customer.mood);
		assertEquals(customer.animations.excited, customer.getObject(0));
		
		// test same condition for temp sick event.
		customer = createCustomer(CUSTOMER_WAITING);
		customer.update(10, null);
		assertEquals(MoodState.NORMAL, customer.mood);
		
		GameEvent trampArrived = GameEventSystem.getInstance().obtain(GameEvent.CUSTOMER_TRAMP_ARRIVED, 0, null);
		customer.send(trampArrived);
		customer.update(0, null);	// into temp sick mood
		assertEquals(MoodState.SICK, customer.mood);
		assertTrue(customer.inTempMood);
		
		brunch = new Brunch();
		brunch.set(customer.expectedBrunch);
		Log.e(TAG, "testCriticalCondition xxx");
		customer.receiveBrunch(brunch);
		customer.update(0, null);
		assertFalse(customer.inTempMood);	// temp mood should be canceled.
		assertEquals(MoodState.EXCITED, customer.mood);
		assertEquals(customer.animations.excited, customer.getObject(0));
	}
	
	public void testPickSingleFood() {
		Customer customer = CustomerManager.setupCustomer(Type.JOGGING_GIRL, GameEntity.MIN_GAME_ENTITY_Z_ORDER);
		forFood(customer, Food.BAGEL);
		forFood(customer, Food.CROISSANT);
		forFood(customer, Food.MUFFIN_CIRCLE);
		forFood(customer, Food.MUFFIN_SQUARE);
		forFood(customer, Food.TOAST_WHITE);
		forFood(customer, Food.TOAST_BLACK);
		forFood(customer, Food.TOAST_YELLOW);
	}
	
	private void forFood(Customer customer, int food) {
		Customer.OrderingSpec spec = new Customer.OrderingSpec();
		spec.addOptional(food);
		
		Brunch result = new Brunch();
		DecideBrunchException exception = null;
		try {
			customer.decideBrunch(spec, result);
		} catch (DecideBrunchException e) {
			exception = e;
		}
		assertNull("encounter exception:" + exception, exception);
		assertNotNull(result);
		
		Brunch correct = new Brunch();
		correct.addFood(food);
		if (correct.contains(FoodType.TOAST)) {
			correct.closeToast();
		}
		assertEquals(correct, result);
	}
	
	public void testMultipleFoods() {
		Customer customer = CustomerManager.setupCustomer(Type.JOGGING_GIRL, GameEntity.MIN_GAME_ENTITY_Z_ORDER);
		
		FoodSet set = new FoodSet();
		set.addFood(Food.TOAST_BLACK);
		set.addFood(Food.TOAST_YELLOW);
		set.addFood(Food.BUTTER);
		set.addFood(Food.TOMATO);
		set.addFood(Food.CHEESE);
		set.addFood(Food.HAM);
		set.addFood(Food.HOTDOG);
		set.addFood(Food.COFFEE);
		
		for (int c = 0; c < 10; c++) {
			forMultipleFoods(customer, set);
		}
	}
	
	private void forMultipleFoods(Customer customer, FoodSet set) {
		Customer.OrderingSpec spec = new Customer.OrderingSpec();
		spec.addOptional(set);
		
		Brunch result = new Brunch();
		DecideBrunchException exception = null;
		try {
			customer.decideBrunch(spec, result);
		} catch (DecideBrunchException e) {
			exception = e;
		}
		assertNull("encounter exception:" + exception, exception);
		assertNotNull(result);
		
		assertTrue(set.contains(result));
	}
	
	public void testFoodChooseMustHave() {
		Customer.OrderingSpec spec = new Customer.OrderingSpec();
		spec.addMustHave(Food.EGG);
		spec.addOptional(Food.TOAST_WHITE);
		spec.addOptional(Food.BUTTER);
		
		Customer customer = CustomerManager.setupCustomer(Type.JOGGING_GIRL, GameEntity.MIN_GAME_ENTITY_Z_ORDER);
		Brunch result = new Brunch();
		Exception exception = null;
		try {
			customer.decideBrunch(spec, result);
		} catch (DecideBrunchException e) {
			exception = e;
		}
		
		assertNull("exception:" + exception, exception);
		assertTrue(result.contains(Food.EGG));
		assertTrue(result.contains(Food.TOAST_WHITE));
	}
}
