package com.studioirregular.bonniep2;

import android.util.Log;


public abstract class CookableFoodComponent extends FoodComponent {

	private static final boolean DO_LOG = false;
	private static final String TAG = "cookable-food-component";
	
	public static final int COOK_STAGE_INVALID = -1;
	public static final int COOK_STAGE_START = 0;
	public static final int COOK_STAGE_FINISH = 11;
	
	public CookableFoodComponent(String id) {
		super(id);
	}
	
	public boolean isCooking() {
		return isCooking;
	}
	
	public boolean doneCooking() {
		return cookDone;
	}
	
	public void startCook() {
		elapsedTime = 0;
		isCooking = true;
		cookDone = false;
	}
	
	public int getCurrentCookStage() {
		if (!isCooking) {
			return COOK_STAGE_INVALID;
		}
		return cookStage;
	}
	
	public void update(long timeDiff, Entity host) {
		if (!isCooking) {
			return;
		}
		
		elapsedTime += timeDiff;
		if (elapsedTime >= totalCookTime) {
			cookStage = COOK_STAGE_FINISH;
			if (DO_LOG) Log.d(TAG, "elapsed time:" + elapsedTime + " > totalTime:" + totalCookTime);
			isCooking = false;
			cookDone = true;
		} else {
			cookStage = (int)((COOK_STAGE_FINISH - COOK_STAGE_START) * elapsedTime / totalCookTime);
		}
	}
	
	protected boolean isCooking = false;
	protected boolean cookDone = false;
	protected int cookStage;
	protected long totalCookTime = 5000;	// ms
//	protected long startCookTime = Long.MAX_VALUE;	// ms
	protected long elapsedTime;

}
