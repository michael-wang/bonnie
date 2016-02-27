package com.studioirregular.bonniesbrunch.component;

import java.util.Arrays;
import java.util.List;

import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelCustomerConfig;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelTime;
import com.studioirregular.bonniesbrunch.base.ObjectArray;
import com.studioirregular.bonniesbrunch.base.ObjectArray.OutOfCapacityException;
import com.studioirregular.bonniesbrunch.entity.Customer;
import com.studioirregular.bonniesbrunch.entity.CustomerManager;
import com.studioirregular.bonniesbrunch.entity.CustomerManager.CustomerSpec;
import com.studioirregular.bonniesbrunch.entity.GameEntity;

public class FixedCustomerGenerator extends GameComponent {

	private static final String TAG = "fixed-customer-generator";
	
	public FixedCustomerGenerator(int zOrder) {
		super(zOrder);
	}
	
	public void setup(LevelTime levelTime, boolean forSpecialLevel, List<LevelCustomerConfig> customers) {
		if (Config.DEBUG_LOG) Log.d(TAG, "setup #customers:" + customers.size());
		
		this.totalLevelTime = levelTime.total * 1000;
		
		specs = new FixedTimeAppearanceSpec[customers.size()];
		for (int i = 0; i < customers.size(); i++) {
			specs[i] = FixedTimeAppearanceSpec.adapter(customers.get(i));
			if (Config.DEBUG_LOG) {
				for (int j = 0; j < specs[i].appearanceTimes.size(); j++) {
					Log.d(TAG, "customer:" + specs[i].type + ", appearance time:" + specs[i].appearanceTimes.get(j));
				}
			}
		}
	}
	
	public void levelStart() {
		if (Config.DEBUG_LOG) Log.d(TAG, "levelStart");
		
		isStarted = true;
		levelElapsedTime = 0;
	}
	
	public void levelStop() {
		isStarted = false;
	}
	
	@Override
	public void update(long timeDelta, GameEntity parent) {
		if (!isStarted) {
			return;
		}
		
		levelElapsedTime += timeDelta;
		
		if (totalLevelTime <= levelElapsedTime) {
			isStarted = false;
		}
		
		for (FixedTimeAppearanceSpec spec : specs) {
			ObjectArray<Integer> appearTimes = spec.appearanceTimes;
			if (appearTimes.size() > 0) {
				if (appearTimes.getLast() <= levelElapsedTime) {
					genCustomer(spec);
					appearTimes.removeLast();
				}
			}
		}
	}
	
	private void genCustomer(FixedTimeAppearanceSpec spec) {
		if (Config.DEBUG_LOG) Log.d(TAG, "genCustomer spec:" + spec);
		
		CustomerManager.CustomerSpec customer = new CustomerManager.CustomerSpec(
				spec.type, spec.orderingSpec,
				forSpecialLevel ? CustomerSpec.Mode.SpecialLevel
						: CustomerSpec.Mode.Normal, -1);
		GameEventSystem.scheduleEvent(
				GameEvent.CUSTOMER_MANAGER_ADD_CUSTOMER, 0, customer);
	}
	
	@Override
	public void reset() {
		isStarted = false;
		levelElapsedTime = 0;
	}
	
	private static class FixedTimeAppearanceSpec {
		public Customer.Type type;
		public Customer.OrderingSpec orderingSpec;
		public ObjectArray<Integer> appearanceTimes;	// in ms
		
		public static FixedTimeAppearanceSpec adapter(LevelCustomerConfig config) {
			if (Config.DEBUG_LOG) Log.d(TAG, "adapter config:" + config);
			
			FixedTimeAppearanceSpec result = new FixedTimeAppearanceSpec();
			result.type = config.type;
			
			result.orderingSpec = new Customer.OrderingSpec();
			result.orderingSpec.addMustHave(config.mustOrder);
			result.orderingSpec.addOptional(config.optionalSet);
			
			result.appearanceTimes = buildAppearanceTimes(config.appearanceConfig.times);
			return result;
		}
		
		private static ObjectArray<Integer> buildAppearanceTimes(List<Integer> times) {
			Integer[] array = new Integer[times.size()];
			times.toArray(array);
			Arrays.sort(array);
			
			ObjectArray<Integer> result = new ObjectArray<Integer>(array.length);
			try {
				for (int i = array.length-1; i >= 0; i--) {
					result.add(array[i] * 1000);	// convert sec to ms.
				}
			} catch (OutOfCapacityException e) {
				Log.e(TAG, "FixedTimeAppearanceSpec::buildAppearanceTimes exception:" + e);
			}
			return result;
		}
	}
	
	private boolean forSpecialLevel;
	private boolean isStarted;
	private int totalLevelTime;
	private int levelElapsedTime;
	private FixedTimeAppearanceSpec[] specs;

}
