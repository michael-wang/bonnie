package com.studioirregular.bonniep2;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import android.graphics.PointF;
import android.util.Log;

import com.studioirregular.bonnie.foodsystem.FoodSystem.Food;
import com.studioirregular.bonniep2.FoodProductHolder.Size;

public abstract class FoodComponent implements Component, Comparable<FoodComponent> {

	protected static final float FOOD_WIDTH_NORMAL = 90;
	protected static final float FOOD_HEIGHT_NORMAL = 90;
	protected static final float FOOD_WIDTH_MEDIUM = 69;
	protected static final float FOOD_HEIGHT_MEDIUM = 69;
	
	protected static final float BEVERAGE_WIDTH_NORMAL = 53;
	protected static final float BEVERAGE_HEIGHT_NORMAL = 60;
	protected static final float BEVERAGE_WIDTH_MEDIUM = 39;
	protected static final float BEVERAGE_HEIGHT_MEDIUM = 45;
	
	protected static final float CANDY_WIDTH			= 23;
	protected static final float CANDY_HEIGHT			= 23;
	
	protected String id;
	
	public FoodComponent(String id) {
		this.id = id;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	public abstract String getTextureId(FoodProductHolder.Size type);
	public void getTextureSize(FoodProductHolder.Size type, PointF size) {
		if (type == Size.Normal) {
			if (getCategory() == Category.Beverage) {
				size.set(BEVERAGE_WIDTH_NORMAL, BEVERAGE_HEIGHT_NORMAL);
			} else {
				size.set(FOOD_WIDTH_NORMAL, FOOD_HEIGHT_NORMAL);
			}
		} else if (type == Size.Medium) {
			if (getCategory() == Category.Beverage) {
				size.set(BEVERAGE_WIDTH_MEDIUM, BEVERAGE_HEIGHT_MEDIUM);
			} else {
				size.set(FOOD_WIDTH_MEDIUM, FOOD_HEIGHT_MEDIUM);
			}
		} else if (type == Size.Candy) {
			size.set(CANDY_WIDTH, CANDY_HEIGHT);
		}
	}
	public abstract int getPrice();
	public abstract boolean isCombinable(FoodComponent food);
	
	protected static enum Category {
		Bread,
		Muffin,
		Toast,
		AddOnA,
		AddOnB,
		Sauce,
		Beverage,
		Candy
	}
	protected boolean isMainIngredient() {
		Category cat = getCategory();
		return (cat == Category.Bread) || (cat == Category.Muffin) || (cat == Category.Toast);
	}
	
	protected static enum Type {
		// Bread
		Bagel,
		Croissant,
		MuffinCircle,
		MuffinSquare,
		ToastWhite,
		ToastBlack,
		ToastYellow,
		
		// Sauce
		Butter,
		Honey,
		Tomato,
		
		// Add on A
		Lettuce,
		Cheese,
		
		// Add on B
		Egg,
		Ham,
		Sausage,
		
		// Beverage
		Milk,
		Coffee,
		
		// Candy
		Candy1,
		Candy2,
		Candy3
	}
	
	protected abstract Category getCategory();
	protected abstract Type getType();
	
	protected static final Map<Food, Type> FOOD_MAP = new HashMap<Food, Type>();
	static {
		FOOD_MAP.put(Food.getBagel(),		Type.Bagel);
		FOOD_MAP.put(Food.getCroissant(),	Type.Croissant);
		FOOD_MAP.put(Food.getMuffinCircle(),Type.MuffinCircle);
		FOOD_MAP.put(Food.getMuffinSquare(),Type.MuffinSquare);
		FOOD_MAP.put(Food.getToastWhite(),	Type.ToastWhite);
		FOOD_MAP.put(Food.getToastBlack(),	Type.ToastBlack);
		FOOD_MAP.put(Food.getToastYellow(),	Type.ToastYellow);
		FOOD_MAP.put(Food.getButter(),		Type.Butter);
		FOOD_MAP.put(Food.getHoney(),		Type.Honey);
		FOOD_MAP.put(Food.getTomato(),		Type.Tomato);
		FOOD_MAP.put(Food.getLettuce(),		Type.Lettuce);
		FOOD_MAP.put(Food.getCheese(),		Type.Cheese);
		FOOD_MAP.put(Food.getEgg(),			Type.Egg);
		FOOD_MAP.put(Food.getHam(),			Type.Ham);
		FOOD_MAP.put(Food.getSausage(),		Type.Sausage);
		FOOD_MAP.put(Food.getMilk(),		Type.Milk);
		FOOD_MAP.put(Food.getCoffee(),		Type.Coffee);
		FOOD_MAP.put(Food.getCandy(1),		Type.Candy1);
		FOOD_MAP.put(Food.getCandy(2),		Type.Candy2);
		FOOD_MAP.put(Food.getCandy(3),		Type.Candy3);
	}
	public static FoodComponent newInstance(Food food) {
		Type type = FOOD_MAP.get(food);
		if (type != null) {
			return newInstance(type);
		}
		Log.w(TAG, "newInstance failed, food:" + food);
		return null;
	}
	
	public static FoodComponent newInstance(Type type) {
		if (type == Type.Candy1 || type == Type.Candy2 || type == Type.Candy3) {
			return new FoodCandyComponent(FoodCandyComponent.class.getSimpleName(), type);
		}
		
		Class<? extends FoodComponent> foodClass = MAPPING.get(type);
		Class<?>[] paramTypes = { String.class };
		Constructor<? extends FoodComponent> cont = null;
		try {
			cont = foodClass.getConstructor(paramTypes);
		} catch (SecurityException e) {
			Log.e(TAG, "failed to generate food of type:" + type + ", e:" + e);
			return null;
		} catch (NoSuchMethodException e) {
			Log.e(TAG, "failed to generate food of type:" + type + ", e:" + e);
			return null;
		}
		
		try {
			return cont.newInstance(foodClass.getSimpleName());
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "failed to generate food of type:" + type + ", e:" + e);
		} catch (InstantiationException e) {
			Log.e(TAG, "failed to generate food of type:" + type + ", e:" + e);
		} catch (IllegalAccessException e) {
			Log.e(TAG, "failed to generate food of type:" + type + ", e:" + e);
		} catch (InvocationTargetException e) {
			Log.e(TAG, "failed to generate food of type:" + type + ", e:" + e);
		}
		return null;
	}
	
	private static final String TAG = "food-component";
	
	private static Map<Type, Class<? extends FoodComponent>> MAPPING = new HashMap<Type, Class<? extends FoodComponent>>();
	static {
		MAPPING.put(Type.Bagel,			FoodBagelComponent.class);
		MAPPING.put(Type.Croissant,		FoodCroissantComponent.class);
		MAPPING.put(Type.MuffinCircle,	FoodMuffinCircleComponent.class);
		MAPPING.put(Type.MuffinSquare,	FoodMuffinSquareComponent.class);
		MAPPING.put(Type.ToastWhite,	FoodToastWhiteComponent.class);
		MAPPING.put(Type.ToastBlack,	FoodToastBlackComponent.class);
		MAPPING.put(Type.ToastYellow,	FoodToastYellowComponent.class);
		
		MAPPING.put(Type.Butter,		FoodButterComponent.class);
		MAPPING.put(Type.Honey,			FoodHoneyComponent.class);
		MAPPING.put(Type.Tomato,		FoodTomatoComponent.class);
		
		MAPPING.put(Type.Lettuce,		FoodLettuceComponent.class);
		MAPPING.put(Type.Cheese,		FoodCheeseComponent.class);
		
		MAPPING.put(Type.Egg,			FoodEggComponent.class);
		MAPPING.put(Type.Ham,			FoodHamComponent.class);
		MAPPING.put(Type.Sausage,		FoodSausageComponent.class);
		
		MAPPING.put(Type.Milk,			FoodMilkComponent.class);
		MAPPING.put(Type.Coffee,		FoodCoffeeComponent.class);
	}
	
	@Override
	public int compareTo(FoodComponent another) {
		int myCategory = getCategory().ordinal();
		int anotherCategory = another.getCategory().ordinal();
		
		if (myCategory < anotherCategory) {
			return -1;
		} else if (myCategory == anotherCategory) {
			return 0;
		} else {
			return 1;
		}
	}
	
}
