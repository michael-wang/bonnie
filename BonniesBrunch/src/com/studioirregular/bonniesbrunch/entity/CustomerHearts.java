package com.studioirregular.bonniesbrunch.entity;

import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.base.ObjectBase;
import com.studioirregular.bonniesbrunch.component.CustomerHeart;
import com.studioirregular.bonniesbrunch.component.CustomerHeart.State;
import com.studioirregular.bonniesbrunch.entity.Customer.CustomerParameters;


/* Draw hearts which represent reflect customer patient status.
 * Customer has at least 3 at most 5 total patient points.
 * Initially all hearts are full-hearted. With customer patient points drop,
 * heart can be half-hearted or empty-hearted.
 * For example: customer with 5 patient points we draw 5 full hearts.
 * When he drops half patient point, right most heart becomes
 * half-hearted. When he drops another half point, right most heart becomes
 * hollow heart. And some thing goes on for 2nd right most heart.
 * 
 * Implementation details: I try to update heart status when customer drops
 * another half point, instead of update on every frame.
 */
public class CustomerHearts extends GameEntity {

//	private static final String TAG = "customer-hearts";
	
	public CustomerHearts(int zOrder) {
		super(zOrder);
		
		timeUnit = CustomerParameters.PATIENCE_TIME_UNIT;
		halfTimeUnit = CustomerParameters.PATIENCE_TIME_UNIT / 2;
	}
	
	@Override
	public void setup(float x, float y, float width, float height) {
		super.setup(x, y, width, height);
		
		hearts[0] = new CustomerHeart(MIN_GAME_COMPONENT_Z_ORDER);
		hearts[0].setup(0, 0);
		
		hearts[1] = new CustomerHeart(MIN_GAME_COMPONENT_Z_ORDER);
		hearts[1].setup(21, 0);
		
		hearts[2] = new CustomerHeart(MIN_GAME_COMPONENT_Z_ORDER);
		hearts[2].setup(42, 0);
		
		hearts[3] = new CustomerHeart(MIN_GAME_COMPONENT_Z_ORDER);
		hearts[3].setup(63, 0);
		
		hearts[4] = new CustomerHeart(MIN_GAME_COMPONENT_Z_ORDER);
		hearts[4].setup(84, 0);
	}
	
	public void setup(Customer customer) {
		this.customer = customer;
		if (customer == null) {
			return;
		}
		
		total = customer.config.totalPatience * CustomerParameters.PATIENCE_TIME_UNIT;
		assert(total <= 5 * CustomerParameters.PATIENCE_TIME_UNIT);
		assert(0 <= total);
		this.last = total - customer.waitElapsedTime;
		
		clear();
		for (int i = 0; i < customer.config.totalPatience; i++) {
			add(hearts[i]);
		}
		
		updateHeartsStates(last);
	}
	
	@Override
	public void update(long timeDelta, ObjectBase parent) {
		super.update(timeDelta, parent);
		
		if (customer == null) {
			return;
		}
		
		long current = total - customer.waitElapsedTime;
		
		int currentHalfCount = (int)current / halfTimeUnit;
		int lastHalfCount = (int)last / halfTimeUnit;
		
		if (lastHalfCount != currentHalfCount) {
			updateHeartsStates(current);
		}
		
		last = current;
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
	}
	
	private void clear() {
		for (int i = 0; i < getCount(); i++) {
			remove(getObject(i));
		}
	}
	
	private void updateHeartsStates(long current) {
		final int count = customer.config.totalPatience;
		long remain = current;
		for (int i = 0; i < count; i++) {
			if (remain > halfTimeUnit) {
				hearts[i].set(State.Full);
			} else if (remain > 0) {
				hearts[i].set(State.Half);
			} else {
				hearts[i].set(State.Hollow);
			}
			
			remain -= timeUnit;
		}
	}
	
	private long total;		// total patient points * PATIENCE_TIME_UNIT
	private long last;	// total - waitElapsedTime
	private Customer customer;
	private int timeUnit;
	private int halfTimeUnit;
	
	private CustomerHeart[] hearts = new CustomerHeart[5];

}
