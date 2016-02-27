package com.studioirregular.bonniesbrunch.component;

import java.util.List;

import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelCustomerConfig;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelTime;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelTimePeriod;
import com.studioirregular.bonniesbrunch.entity.Customer;
import com.studioirregular.bonniesbrunch.entity.CustomerManager.CustomerSpec;
import com.studioirregular.bonniesbrunch.entity.GameEntity;

// Component to encapsulate customer generation for GameGraphRoot.
public class RandomCustomerGenerator extends GameComponent {

	private static final String TAG = "random-customer-generator";
	
	public RandomCustomerGenerator(int zOrder) {
		super(zOrder);
	}
	
	public void setup(LevelTime levelTime, boolean forSpecialLevel, List<LevelCustomerConfig> customers) {
		if (Config.DEBUG_LOG) Log.d(TAG, "setup levelTime:" + levelTime + ", #customers:" + customers.size());
		
		totalTime = levelTime.total * 1000;
		this.forSpecialLevel = forSpecialLevel;
		
		List<LevelTimePeriod> periods = levelTime.periods;
		timeSpecs = new TimeSpec[periods.size()];
		for (int i = 0; i < periods.size(); i++) {
			LevelTimePeriod period = periods.get(i);
			TimeSpec spec = new TimeSpec();
			if (i < (periods.size() - 1)) {
				spec.end = periods.get(i + 1).start * 1000;
			} else {
				spec.end = totalTime;
			}
			
			spec.minInterval = period.minCustomerInterval * 1000;
			spec.maxInterval = period.maxCustomerInterval * 1000;
			
			timeSpecs[i] = spec;
		}
		
		this.appearanceSpecs = new RandomAppearanceSpec[customers.size()];
		for (int i = 0; i < customers.size(); i++) {
			appearanceSpecs[i] = RandomAppearanceSpec.adapter(customers.get(i));
		}
		
		totalCustomerWeight = 0;
		for (LevelCustomerConfig c : customers) {
			totalCustomerWeight += c.appearanceConfig.weight;
		}
	}
	
	public void levelStart() {
		if (Config.DEBUG_LOG) Log.d(TAG, "levelStart");
		
		if (timeSpecs != null && timeSpecs.length > 0) {
			genCustomer();
			scheduleNext(timeSpecs[0]);
		}
		totalElapsedTime = 0;
		started = true;
	}
	
	public void levelStop() {
		started = false;
	}
	
	// assume parent is GameGraphRoot.
	@Override
	public void update(long timeDelta, GameEntity parent) {
		if (!started || targetPeriodElapsedTime <= 0) {
			return;
		}
		
		totalElapsedTime += timeDelta;
		
		if (totalTime <= totalElapsedTime) {
			started = false;
		}
		
		if (targetPeriodElapsedTime <= totalElapsedTime) {
			genCustomer();
			
			TimeSpec spec = getTimeSpec(totalElapsedTime);
			if (spec != null) {
				scheduleNext(spec);
			} else {
				targetPeriodElapsedTime = Integer.MAX_VALUE;
			}
		}
	}
	
	@Override
	public void reset() {
		totalElapsedTime = 0;
		started = false;
		targetPeriodElapsedTime = 0;
	}
	
	private void genCustomer() {
		RandomAppearanceSpec customer = pickCustomer();
		if (customer != null) {
			CustomerSpec spec = new CustomerSpec(customer.type,
					customer.orderingSpec,
					forSpecialLevel ? CustomerSpec.Mode.SpecialLevel
							: CustomerSpec.Mode.Normal, -1);
			GameEventSystem.scheduleEvent(GameEvent.CUSTOMER_MANAGER_ADD_CUSTOMER, 0, spec);
		}
	}
	
	private RandomAppearanceSpec pickCustomer() {
		int randomWeight = (int)(Math.random() * totalCustomerWeight);
		// find customer spec by randomWeight
		int accumulatedWeight = 0;
		for (int i = 0; i < appearanceSpecs.length; i++) {
			accumulatedWeight += appearanceSpecs[i].weight;
			if (randomWeight < accumulatedWeight) {
				return appearanceSpecs[i];
			}
		}
		if (Config.DEBUG_LOG) Log.w(TAG, "pickCustomer failed, randomWeight" + randomWeight);
		return null;
	}
	
	private void scheduleNext(TimeSpec spec) {
		targetPeriodElapsedTime = totalElapsedTime + spec.minInterval + (int)(Math.random() * (spec.maxInterval- spec.minInterval));
	}
	
	private TimeSpec getTimeSpec(long gameElapsedTime) {
		for (int i = 0; i < timeSpecs.length; i++) {
			if (gameElapsedTime < timeSpecs[i].end) {
				return timeSpecs[i];
			}
		}
		return null;
	}
	
	private static class RandomAppearanceSpec {
		public int weight;
		public Customer.Type type;
		public Customer.OrderingSpec orderingSpec;
		
		static RandomAppearanceSpec adapter(LevelCustomerConfig config) {
			if (Config.DEBUG_LOG) Log.d(TAG, "adapter config:" + config);
			RandomAppearanceSpec result = new RandomAppearanceSpec();
			result.weight = config.appearanceConfig.weight;
			result.type = config.type;
			
			result.orderingSpec = new Customer.OrderingSpec();
			result.orderingSpec.addMustHave(config.mustOrder);
			result.orderingSpec.addOptional(config.optionalSet);
			
			return result;
		}
	}
	
	// all in ms
	private class TimeSpec {
		public int end;
		public int minInterval;
		public int maxInterval;
	}
	
	private boolean forSpecialLevel;
	private int totalTime;	// ms
	private int totalElapsedTime;
	private TimeSpec[] timeSpecs;
	private RandomAppearanceSpec[] appearanceSpecs;
	private int totalCustomerWeight;
	
	private boolean started = false;
	private int targetPeriodElapsedTime;

}
