package com.studioirregular.bonniesbrunch.component;

import java.util.HashMap;
import java.util.Map;

import android.graphics.PointF;

import com.studioirregular.bonniesbrunch.FoodSystem.Food;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.entity.BrunchHolder.Size;
import com.studioirregular.bonniesbrunch.entity.GameEntity;

public class FoodComponent extends RenderComponent {

	public FoodComponent(int zOrder) {
		super(zOrder);
	}
	
	public void setup(int food, Size size) {
		setup(food, size, 5000);
	}
	
	public void setup(int food, Size size, int cookDuration) {
		this.food = food;
		this.cookDuration = cookDuration;
		
		PointF dim = new PointF();
		getTextureDimension(size, dim);
		super.setup(dim.x, dim.y);
		
		super.setup(getTextureId(size));
	}
	
	public int getFoodType() {
		return food;
	}
	
	public boolean isDone() {
		return ready;
	}
	
	public boolean isCooking() {
		return cooking;
	}
	
	public boolean cook() {
		if (Food.isMuffin(food) == false && Food.isToast(food) == false && Food.isAddOnB(food) == false) {
			return false;
		}
		
		cooking = true;
		ready = false;
		elapsedCookTime = 0;
		return true;
	}
	
	@Override
	public void update(long timeDelta, GameEntity parent) {
		super.update(timeDelta, parent);
		
		if (cooking) {
			elapsedCookTime += timeDelta;
			if (cookDuration <= elapsedCookTime) {
				ready = true;
				cooking = false;
				
				GameEvent readyEvent = GameEventSystem.getInstance().obtain(GameEvent.FOOD_DONE_COOKING, food);
				parent.send(readyEvent);
			}
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		food = Food.INVALID_TYPE;
		elapsedCookTime = 0;
		ready = false;
		cooking = false;
	}
	
	private static Map<Integer, String> TEXTURE_RES_NAME;
	static {
		TEXTURE_RES_NAME = new HashMap<Integer, String>();
		TEXTURE_RES_NAME.put(Food.BAGEL,			"food_3_bagel_");
		TEXTURE_RES_NAME.put(Food.CROISSANT,		"food_3_croissant_");
		TEXTURE_RES_NAME.put(Food.MUFFIN_CIRCLE,	"food_2_waffle_circular_");
		TEXTURE_RES_NAME.put(Food.MUFFIN_SQUARE,	"food_2_waffle_square_");
		TEXTURE_RES_NAME.put(Food.TOAST_WHITE,		"food_1_toast_white_double_");
		TEXTURE_RES_NAME.put(Food.TOAST_BLACK,		"food_1_toast_black_double_");
		TEXTURE_RES_NAME.put(Food.TOAST_YELLOW,		"food_1_toast_yellow_double_");
		TEXTURE_RES_NAME.put(Food.BUTTER,			"food_6_butter_");
		TEXTURE_RES_NAME.put(Food.HONEY,			"food_6_honey_");
		TEXTURE_RES_NAME.put(Food.TOMATO,			"food_6_tomato_");
		TEXTURE_RES_NAME.put(Food.LETTUCE,			"food_5_lettuce_");
		TEXTURE_RES_NAME.put(Food.CHEESE,			"food_5_cheese_");
		TEXTURE_RES_NAME.put(Food.EGG,				"food_4_egg_");
		TEXTURE_RES_NAME.put(Food.HAM,				"food_4_ham_");
		TEXTURE_RES_NAME.put(Food.HOTDOG,			"food_4_hotdog_");
		TEXTURE_RES_NAME.put(Food.MILK,				"food_7_milk_");
		TEXTURE_RES_NAME.put(Food.COFFEE,			"food_7_coffee_");
	}
	
	protected String getTextureId(Size size) {
		if (TEXTURE_RES_NAME.containsKey(food)) {
			return TEXTURE_RES_NAME.get(food) + (size == Size.Normal ? "b" : "m");
		}
		return "";
	}
	
	private void getTextureDimension(Size size, PointF dim) {
		if (food == Food.MILK || food == Food.COFFEE) {
			if (size == Size.Normal) {
				dim.set(53, 60);
			} else {
				dim.set(39, 45);
			}
		} else {
			if (size == Size.Normal) {
				dim.set(90, 90);
			} else {
				dim.set(69, 69);
			}
		}
	}
	
	protected int food;
	protected int cookDuration = 5000;
	protected long elapsedCookTime = 0L;
	protected boolean ready = false;
	protected boolean cooking = false;

}
