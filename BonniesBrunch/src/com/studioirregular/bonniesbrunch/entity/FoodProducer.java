package com.studioirregular.bonniesbrunch.entity;

import com.studioirregular.bonniesbrunch.FoodSystem.Food;
import com.studioirregular.bonniesbrunch.FoodSystem.FoodType;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.SoundSystem;
import com.studioirregular.bonniesbrunch.SoundSystem.Sound;
import com.studioirregular.bonniesbrunch.component.Animation;
import com.studioirregular.bonniesbrunch.component.AnimationSet;
import com.studioirregular.bonniesbrunch.component.ButtonComponent;
import com.studioirregular.bonniesbrunch.component.RenderComponent;
import com.studioirregular.bonniesbrunch.component.ScaleAnimation;

public class FoodProducer extends GameEntity {

//	private static final boolean DO_LOG = false;
//	private static final String TAG = "food-producer";
	
	protected int foodType;
	
	private RenderComponent bg;
	private Sound clickSound;
	private Animation clickAnimation;
	
	public FoodProducer(int zOrder, int foodType) {
		super(zOrder);
		this.foodType = foodType;
	}
	
	@Override
	public void setup(float x, float y, float width, float height) {
		super.setup(x, y, width, height);
		
		bg = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		bg.setup(width, height);
		bg.setup(getTexturePartitionId());
		add(bg);
		
		if (FoodType.isType(FoodType.SAUCE, foodType)) {
			clickSound = SoundSystem.getInstance().load("game_table_sauce_s1");
		} else if (FoodType.isType(FoodType.BEVERAGE, foodType)) {
			clickSound = SoundSystem.getInstance().load("game_table_pouring_s1");
		} else {
			clickSound = SoundSystem.getInstance().load("game_table_pickup_s1");
		}
		
		clickAnimation = new AnimationSet(MIN_GAME_COMPONENT_Z_ORDER);
		Animation scaleUp = new ScaleAnimation(MIN_GAME_COMPONENT_Z_ORDER, 1.0f, 1.25f, 150);
		Animation scaleDown = new ScaleAnimation(MIN_GAME_COMPONENT_Z_ORDER, 1.25f, 1.0f, 150);
		((AnimationSet)clickAnimation).addAnimation(scaleUp);
		((AnimationSet)clickAnimation).addAnimation(scaleDown);
		clickAnimation.setLoop(false);
		clickAnimation.setFillBefore(true);
		clickAnimation.setFillAfter(true);
		add(clickAnimation);
	}
	
	public void setTouchArea(float offsetLeft, float offsetTop, float offsetRight, float offsetBottom) {
		ButtonComponent button = new ButtonComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
		final float touchWidth = box.width() - offsetLeft + offsetRight;
		final float touchHeight = box.height() - offsetTop + offsetBottom;
		button.setup(box.left + offsetLeft, box.top + offsetTop, touchWidth, touchHeight, box.left, box.top);
		add(button);
	}
	
	public int getFoodType() {
		return foodType;
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		if (event.what == GameEvent.FOOD_CONSUMED && event.obj == this) {
			return true;
		}
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		if (disable) {
			return;
		}
		
		if (event.what == GameEvent.BUTTON_UP) {
			GameEventSystem.scheduleEvent(GameEvent.FOOD_GENERATED, foodType, this);
			
			if (clickSound != null) {
				SoundSystem.getInstance().playSound(clickSound, false);
			}
			
			clickAnimation.start();
		}
	}
	
	protected String getTexturePartitionId() {
		if (foodType == Food.BAGEL) {
			return "game_table_bagel";
		} else if (foodType == Food.CROISSANT) {
			return "game_table_croissant";
		} else if (foodType == Food.BUTTER) {
			return "game_table_butter";
		} else if (foodType == Food.HONEY) {
			return "game_table_honey";
		} else if (foodType == Food.TOMATO) {
			return "game_table_tomato";
		} else if (foodType == Food.LETTUCE) {
			return "game_table_lettuce";
		} else if (foodType == Food.CHEESE) {
			return "game_table_cheese";
		} else if (foodType == Food.MILK) {
			return "game_table_milk";
		} else if (foodType == Food.COFFEE) {
			return "game_table_coffee";
		}
		return "";
	}

}
