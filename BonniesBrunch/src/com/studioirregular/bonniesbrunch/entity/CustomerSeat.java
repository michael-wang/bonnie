package com.studioirregular.bonniesbrunch.entity;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.FoodSystem.Brunch;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;

public class CustomerSeat extends GameEntity {

	private static final String TAG = "seat";
	
	public CustomerSeat(int zOrder) {
		super(zOrder);
	}
	
	@Override
	public void setup(float x, float y, float width, float height) {
		setup(x, y, width, height, false, false);
	}
	
	public void setup(float x, float y, float width, float height, boolean allowLineUp, boolean forSpecialLevel) {
		super.setup(x, y, width, height);
		
		this.allowLineUp = allowLineUp;
		
//		RenderComponent bg = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
//		bg.setup(width, height);
//		bg.setup(new float[] {0, 0.5f, 0, 0.5f});
//		add(bg);
		
		bubble = new OrderBubble(MIN_GAME_ENTITY_Z_ORDER);
		bubble.setup(x - 10, y - 5, BUBBLE_WIDTH, BUBBLE_HEIGHT);
		bubble.hide();
		add(bubble);
		
		if (!forSpecialLevel) {
			hearts = new CustomerHearts(MIN_GAME_ENTITY_Z_ORDER);
			hearts.setup(x + 50, y - 19, HEARTS_WIDTH, HEARTS_HEIGHT);
			hearts.hide();
			add(hearts);
		} else {
			hearts = null;
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		
		customers.clear();
		bubble.hide();
		if (hearts != null) {
			hearts.hide();
		}
	}
	
	public boolean available() {
		if (customers.isEmpty() == false) {
			if (allowLineUp && allowAnotherCustomer()) {
				return true;
			}
			return false;
		}
		return true;
	}
	
	public boolean serve(Customer customer) {
		if (customers.isEmpty() == false) {
			if (!allowLineUp && allowAnotherCustomer() == false) {
				return false;
			}
		}
		
		if (customers.isEmpty()) {
			customer.walk(getSeatPositionX());
		} else {
			Customer last = customers.get(customers.size() - 1);
			customer.follow(last);
		}
		customers.add(customer);
		
		return true;
	}
	
	public void sendEventToFirstCustomer(GameEvent event) {
		if (customers.isEmpty() == false) {
			customers.get(0).send(event);
		}
	}
	
	// for customer to decide if he should stop and take seat.
	public float getSeatPositionX() {
		return box.left;
	}
	
	public void onCustomerStartOrdering() {
		if(Config.DEBUG_LOG) Log.d(TAG, "onCustomerStartOrdering");
		
		bubble.show();
		bubble.startThinking();
	}
	
	public void onCustomerMakeOrder(Customer customer, Brunch order) {
		if (Config.DEBUG_LOG) Log.d(TAG, "onCustomerMakeOrder order:" + order);
		
		bubble.makeDecision(order);
		
		if (hearts != null) {
			hearts.show();
			hearts.setup(customer);
		}
	}
	
	public void forceCustomerLeave() {
		bubble.hide();
		if (hearts != null) {
			hearts.setup(null);
			hearts.hide();
		}
	}
	
	public void onCustomerStartLeaving(Customer customer) {
		if (Config.DEBUG_LOG) Log.d(TAG, "onCustomerStartLeaving");
		
		assert customers.get(0) == customer;
		customers.remove(customer);
		if (customers.isEmpty() == false) {
			Customer head = customers.get(0);
			head.follow(null);
			head.walk(getSeatPositionX());
		}
		
		bubble.hide();
		
		if (hearts != null) {
			hearts.setup(null);
			hearts.hide();
		}
	}
	
	public void onCustomerChangeMindFocus(boolean onEquation) {
		bubble.thinkEquations(onEquation);
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
	}
	
	private boolean allowAnotherCustomer() {
		if (customers.isEmpty()) {
			return true;
		}
		
		Customer last = customers.get(customers.size() - 1);
		if (last.box.left < MAX_CUSTOMER_X) {
			return true;
		}
		if (Config.DEBUG_LOG) Log.w(TAG, "allowAnotherCustomer: last customer right:" + last.box.right);
		
		return false;
	}
	
	private static final float BUBBLE_WIDTH = 101;
	private static final float BUBBLE_HEIGHT = 112;
	private static final float HEARTS_WIDTH = 105;
	private static final float HEARTS_HEIGHT = 18;
	// this restrict length of customers in waiting line.
	private static final float MAX_CUSTOMER_X = 546;
	
	private boolean allowLineUp;
	private List<Customer> customers = new ArrayList<Customer>();
	
	private OrderBubble bubble;
	private CustomerHearts hearts;
}
