package com.studioirregular.bonniep2;

import java.util.Random;

import android.util.Log;

import com.studioirregular.bonnie.levelsystem.Level;
import com.studioirregular.bonnie.levelsystem.Level.Customer;
import com.studioirregular.bonnie.timingsystem.TimingSystem;


public class MainScene extends SceneBase {

	private static final String TAG = "scene-main";
	
	private static final String GAME_BACKGROUND_ID = "background";
	private static final String CUSTOMER_ID_PREFIX = "customer";
	
	protected Level level;
	protected Level.Period currentPeriod;
	protected TimingSystem time = new TimingSystem();
	protected int accumulatedCustomerCount;
	protected Random random = new Random(System.currentTimeMillis());
	
	public void setLevel(Level level) {
		this.level = level;
	}
	
	public MainScene(Game game) {
		super(game);
		
		width = 720;
		height = 480;
	}
	
	public String getBackgroundEntityId() {
		return GAME_BACKGROUND_ID;
	}
	
	@Override
	void addEntityInternal(Entity entity) {
		super.addEntityInternal(entity);
		if (entity instanceof CustomerEntity) {
			handleCustomerEntity((CustomerEntity)entity);
		}
	}
	
	@Override
	void addEntityInternal(Entity entity, String afterEntityId) {
		super.addEntityInternal(entity, afterEntityId);
		
		if (entity instanceof CustomerEntity) {
			handleCustomerEntity((CustomerEntity)entity);
		}
	}
	
	protected void handleCustomerEntity(CustomerEntity customer) {
		int seat = CustomerSeat.randomlyPickSeat();
		if (seat == CustomerSeat.INVALID_SEAT) {
			Log.w(TAG, "no seat available, drop customer:" + customer);
			this.removeEntity(customer.getId());
		}
		
		customer.takeSeat(seat);
		CustomerSeat.takeSeat(seat);
	}
	
	@Override
	public void onEvent(Event event) {
		super.onEvent(event);
		
		if (event.what == SceneEvent.LEVEL_START) {
			onLevelStart();
			CustomerSeat.cleanSeats();
			time.start();
		} else if (event.what == SceneEvent.LEVEL_TIME_UP) {
			time.stop();
		}
	}
	
	@Override
	public void update() {
		super.update();
		
		if (isStop || isPause) {
			return;
		}
		
		if (time.isRunning()) {
			updatePeriod();
			time.update(timeDiff);
		}
	}
	
	private void onLevelStart() {
		CustomerSeat.clearSeats();
		
		time.schedule(levelTimeUp, null, level.totalTime);
		if (level.periods != null && level.periods.length > 0) {
			currentPeriod = level.periods[0];
			time.schedule(addCustomer, currentPeriod, currentPeriod.minCustomerInterval);
		}
		
		// add customer on first so user won't have to wait for first time period to start play.
		addRandomCustomer();
	}
	
	private void updatePeriod() {
		final long elapsedTime = time.getElapsedTime();
		
		Level.Period[] periods = level.periods;
		
		int index = 0;
		while (periods[index].startFrom <= elapsedTime) {
			index++;
			if (index >= level.periods.length) {
				break;
			}
		}
		
		Level.Period inPeriod = periods[--index];
		if (inPeriod != currentPeriod) {
			currentPeriod = inPeriod;
			time.schedule(addCustomer, currentPeriod, currentPeriod.minCustomerInterval);
		}
	}
	
	private TimingSystem.Callback addCustomer = new TimingSystem.Callback() {

		@Override
		public void onTimeup(Object obj) {
			assert obj instanceof Customer;
			Level.Period period = (Level.Period)obj;
			if (period.equals(currentPeriod)) {
				addRandomCustomer();
				
				// TODO: randomly pick interval between customer's min/max interval
				time.schedule(addCustomer, currentPeriod, currentPeriod.minCustomerInterval);
			}
		}
		
	};
	
	private void addRandomCustomer() {
		if (level.customers == null || level.customers.length == 0) {
			return;
		}
		
		final int seat = CustomerSeat.randomlyPickSeat();
		if (seat == CustomerSeat.INVALID_SEAT) {
			Log.w(TAG, "addCustomerEntity: all seats taken...");
			return;
		}
		
		int customerIndex = random.nextInt(level.customers.length);
		Customer customer = level.customers[customerIndex];
		
		accumulatedCustomerCount++;
		AddCustomerEntityCommand addCustomer = new AddCustomerEntityCommand(
				this, CUSTOMER_ID_PREFIX + accumulatedCustomerCount,
				GAME_BACKGROUND_ID, 
				CustomerComponent.getCustomerType(customer), customer.prefferedFood);
		
		scheduleCommand(addCustomer);
	}
	
	private TimingSystem.Callback levelTimeUp = new TimingSystem.Callback() {
		
		@Override
		public void onTimeup(Object obj) {
			time.stop();
			MainScene.this.send(MainScene.this, new SceneEvent(SceneEvent.LEVEL_TIME_UP));
		}
	};
		
}
