package com.studioirregular.bonniep2;

import android.util.Log;

import com.studioirregular.bonnie.foodsystem.FoodSystem;
import com.studioirregular.bonnie.foodsystem.FoodSystem.Brunch;

public abstract class CustomerActionState {

	private static final String TAG = "customer-action-state";
	
	private static final String ORDER_BUBBLE_ID_PREFIX = "bubble";
	
	protected CustomerEntity entity;
	
	public CustomerActionState(CustomerEntity entity) {
		this.entity = entity;
	}
	
	public void update(long timeDiff) {
	}
	
	public void onEvent(Event event) {
		
	}
	
	public void startMovingToSeat() {
		
	}
	
	public void startOrderingFood() {
		
	}
	
	public FoodProductEntity makeOrder() {
		return null;
	}
	
	public boolean doYouWantToConsumeIt(FoodProductEntity food) {
		return false;
	}
	
	public void receivingFood(FoodProductEntity food) {
		
	}
	
	public void stopWaiting() {
		
	}
	
	public void walkOut() {
		
	}
	
	public FoodProductEntity changeMind() {
		return null;
	}
	
	protected String getOrderBubbleId() {
		return ORDER_BUBBLE_ID_PREFIX + entity.getSeat();
	}
	
	protected String getOrderFoodId() {
		return entity.getId() + "-ordered-food";
	}
	
	protected FoodProductEntity makeDecision() {
		if (CustomerEntity.DO_LOG) Log.d(TAG, "makeDecision preferredFood:" + entity.preferredFood);
		
		FoodProductEntity result = new FoodProductEntity(
				(SceneBase) entity.getScene(), getOrderFoodId(), entity.getOrderBubble(), entity);
		
		Brunch brunch = FoodSystem.randomlyGenerate(entity.preferredFood);
		if (CustomerEntity.DO_LOG) Log.w(TAG, "makeDecision brunch:" + brunch);
		
//		Food[] foods = brunch.getComposition();
//		for (Food food : foods) {
//			result.add(FoodComponent.newInstance(food));
//		}
		result.setBrunch(brunch);
		
		return result;
	}
}
