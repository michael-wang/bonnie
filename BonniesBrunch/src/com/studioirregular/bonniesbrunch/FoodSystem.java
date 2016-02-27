package com.studioirregular.bonniesbrunch;

import java.util.HashMap;
import java.util.Map;

public class FoodSystem {

	public abstract static class Food {
		public static final int TOTAL_TYPES		= 17;
		public static final int INVALID_TYPE	= 0;
		
		public static final int BAGEL			= 0x1 << 0;
		public static final int CROISSANT		= 0x1 << 1;
		public static final int MUFFIN_CIRCLE	= 0x1 << 2;
		public static final int MUFFIN_SQUARE	= 0x1 << 3;
		public static final int TOAST_WHITE		= 0x1 << 4;
		public static final int TOAST_BLACK		= 0x1 << 5;
		public static final int TOAST_YELLOW	= 0x1 << 6;
		public static final int LETTUCE			= 0x1 << 7;
		public static final int CHEESE			= 0x1 << 8;
		public static final int EGG				= 0x1 << 9;
		public static final int HAM				= 0x1 << 10;
		public static final int HOTDOG			= 0x1 << 11;
		public static final int BUTTER			= 0x1 << 12;
		public static final int HONEY			= 0x1 << 13;
		public static final int TOMATO			= 0x1 << 14;
		public static final int MILK			= 0x1 << 15;
		public static final int COFFEE			= 0x1 << 16;
		
		static final int FOOD_MASK_MAIN_INGREDIENT	= 0x0000007F;
		static final int FOOD_MASK_BREAD			= 0x00000003;
		static final int FOOD_MASK_MUFFIN			= 0x0000000C;
		static final int FOOD_MASK_TOAST			= 0x00000070;
		static final int FOOD_MASK_ADD_ON_A			= 0x00000180;
		static final int FOOD_MASK_ADD_ON_B			= 0x00000E00;
		static final int FOOD_MASK_SAUCE			= 0x00007000;
		static final int FOOD_MASK_BEVERAGE			= 0x00018000;
		
		private static Map<Integer, String> FOOD_NAME = new HashMap<Integer, String>();
		static {
			FOOD_NAME.put(INVALID_TYPE,		"INVALID_TYPE");
			FOOD_NAME.put(BAGEL,			"Bagel");
			FOOD_NAME.put(CROISSANT,		"Croissant");
			FOOD_NAME.put(MUFFIN_CIRCLE,	"Muffin-Circle");
			FOOD_NAME.put(MUFFIN_SQUARE,	"Muffin-Square");
			FOOD_NAME.put(TOAST_WHITE,		"Toast-White");
			FOOD_NAME.put(TOAST_BLACK,		"Toast-Black");
			FOOD_NAME.put(TOAST_YELLOW,		"Toast-Yellow");
			FOOD_NAME.put(BUTTER,			"Butter");
			FOOD_NAME.put(HONEY,			"Honey");
			FOOD_NAME.put(TOMATO,			"Tomato");
			FOOD_NAME.put(LETTUCE,			"Lettuce");
			FOOD_NAME.put(CHEESE,			"Cheese");
			FOOD_NAME.put(EGG,				"Ege");
			FOOD_NAME.put(HAM,				"Ham");
			FOOD_NAME.put(HOTDOG,			"HotDog");
			FOOD_NAME.put(MILK,				"Milk");
			FOOD_NAME.put(COFFEE,			"Coffee");
		}
		
		public static String getFoodName(int food) {
			return FOOD_NAME.get(food);
		}
		
		// bagel, croissant
		public static boolean isBread(int type) {
			return (type & FOOD_MASK_BREAD) != 0;
		}
		
		public static boolean isMuffin(int type) {
			return (type & FOOD_MASK_MUFFIN) != 0;
		}
		
		public static boolean isToast(int type) {
			return (type & FOOD_MASK_TOAST) != 0;
		}
		
		// lettuce, cheese
		public static boolean isAddOnA(int type) {
			return (type & FOOD_MASK_ADD_ON_A) != 0;
		}
		
		// egg, ham, hotdog
		public static boolean isAddOnB(int type) {
			return (type & FOOD_MASK_ADD_ON_B) != 0;
		}
		
		// butter, honey, tomato
		public static boolean isSauce(int type) {
			return (type & FOOD_MASK_SAUCE) != 0;
		}
		
		// milk, coffee
		public static boolean isBeverage(int type) {
			return (type & FOOD_MASK_BEVERAGE) != 0;
		}
		
		private static Map<Integer, Integer> FOOD_COST = new HashMap<Integer, Integer>();
		static {
			FOOD_COST.put(BAGEL,			200);
			FOOD_COST.put(CROISSANT,		200);
			FOOD_COST.put(MUFFIN_CIRCLE,	300);
			FOOD_COST.put(MUFFIN_SQUARE,	300);
			FOOD_COST.put(TOAST_WHITE,		300);
			FOOD_COST.put(TOAST_BLACK,		300);
			FOOD_COST.put(TOAST_YELLOW,		300);
			FOOD_COST.put(LETTUCE,			100);
			FOOD_COST.put(CHEESE,			50);
			FOOD_COST.put(EGG,				100);
			FOOD_COST.put(HAM,				150);
			FOOD_COST.put(HOTDOG,			200);
			FOOD_COST.put(BUTTER,			50);
			FOOD_COST.put(HONEY,			50);
			FOOD_COST.put(TOMATO,			50);
			FOOD_COST.put(MILK,				300);
			FOOD_COST.put(COFFEE,			300);
		}
		public static int getCost(int type) {
			if (FOOD_COST.containsKey(type)) {
				return FOOD_COST.get(type);
			}
			return 0;
		}
		
	}
	
	public static abstract class FoodType {
		public static final int MAIN_INGREDIENT = Food.FOOD_MASK_MAIN_INGREDIENT;
		
		public static final int BREAD		= Food.FOOD_MASK_BREAD;
		public static final int MUFFIN		= Food.FOOD_MASK_MUFFIN;
		public static final int TOAST		= Food.FOOD_MASK_TOAST;
		public static final int ADD_ON_A	= Food.FOOD_MASK_ADD_ON_A;
		public static final int ADD_ON_B	= Food.FOOD_MASK_ADD_ON_B;
		public static final int SAUCE		= Food.FOOD_MASK_SAUCE;
		public static final int BEVERAGE	= Food.FOOD_MASK_BEVERAGE;
		
		public static boolean isType(int type, int foodType) {
			return (type & foodType) != 0;
		}
	}
	
	// Set of foods.
	public static class FoodSet {
		public boolean addFood(int food) {
			if (contains(food)) {
				return false;
			}
			combination |= food;
			return true;
		}
		
		public boolean add(FoodSet other) {
			if ((combination & other.combination) != 0) {
				return false;
			}
			combination |= other.combination;
			return true;
		}
		
		// food: constants from Food or FoodType.
		public boolean contains(int food) {
			return (combination & food) != 0;
		}
		
		public boolean contains(FoodSet other) {
			Iterator itr = new Iterator(other.combination);
			while (itr.hasNext()) {
				if (contains(itr.next()) == false) {
					return false;
				}
			}
			return true;
		}
		
		public int size() {
			return Integer.bitCount(combination);
		}
		
		public int get(int index) {
			int tmp = combination;
			while (index >= 0 && Integer.bitCount(tmp) != 0) {
				int next = Integer.lowestOneBit(tmp);
				if (index == 0) {
					return next;
				}
				index--;
				tmp = tmp & (~next);
			}
			return Food.INVALID_TYPE;
		}
		
		// foodType must be constants from FoodType.
		public void getByType(int foodType, FoodSet result) {
			result.combination = combination & foodType;
		}
		
		@Override
		public String toString() {
			StringBuilder result = new StringBuilder(getClass().getSimpleName() + ":");
			Iterator itr = new Iterator(combination);
			while (itr.hasNext()) {
				int food = itr.next();
				result.append(Food.getFoodName(food));
				result.append(", ");
			}
			return result.toString();
		}
		
		public void set(FoodSet other) {
			combination = other.combination;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}
			
			if (!(o instanceof FoodSet)) {
				return false;
			}
			
			FoodSet lhs = (FoodSet)o;
			return (this.combination == lhs.combination);
		}
		
		public void reset() {
			combination = 0;
		}
		
		protected class Iterator {
			Iterator(int set) {
				this.set = set;
			}
			
			boolean hasNext() {
				return Integer.bitCount(set) != 0;
			}
			
			int next() {
				int next = Integer.lowestOneBit(set);
				set = set & (~next);	// clear bit
				return next;
			}
			private int set;
		}
		
		protected int combination = 0;
	}
	
	// Brunch is food combination with certain rules.
	public static class Brunch extends FoodSet {

		private boolean toastClosed = false;
		
		@Override
		public boolean addFood(int food) {
			if (food == Food.INVALID_TYPE) {
				return false;
			}
			
			int rejectMask = 0;
			if (contains(Food.FOOD_MASK_BREAD)) {
				rejectMask |= Food.FOOD_MASK_MAIN_INGREDIENT;
				rejectMask |= Food.FOOD_MASK_ADD_ON_A;
				rejectMask |= Food.FOOD_MASK_ADD_ON_B;
			}
			if (contains(Food.FOOD_MASK_MUFFIN)) {
				rejectMask |= Food.FOOD_MASK_MAIN_INGREDIENT;
				rejectMask |= Food.FOOD_MASK_ADD_ON_A;
				rejectMask |= Food.FOOD_MASK_ADD_ON_B;
			}
			if (contains(Food.FOOD_MASK_TOAST)) {
				rejectMask |= Food.FOOD_MASK_MAIN_INGREDIENT;
			}
			if (contains(Food.FOOD_MASK_ADD_ON_A)) {
				rejectMask |= Food.FOOD_MASK_ADD_ON_A;
				rejectMask |= Food.FOOD_MASK_BREAD;
				rejectMask |= Food.FOOD_MASK_MUFFIN;
			}
			if (contains(Food.FOOD_MASK_ADD_ON_B)) {
				rejectMask |= Food.FOOD_MASK_ADD_ON_B;
				rejectMask |= Food.FOOD_MASK_BREAD;
				rejectMask |= Food.FOOD_MASK_MUFFIN;
			}
			if (contains(Food.FOOD_MASK_SAUCE)) {
				rejectMask |= Food.FOOD_MASK_SAUCE;
			}
			if (contains(Food.FOOD_MASK_BEVERAGE)) {
				rejectMask |= Food.FOOD_MASK_BEVERAGE;
			}
			
			if ((rejectMask & food) != 0) {
				return false;
			}
			
			return super.addFood(food);
		}
		
		@Override
		public boolean add(FoodSet other) {
			if (other.size() == 0) {
				return true;
			}
			
			Iterator itr = new Iterator(other.combination);
			while (itr.hasNext()) {
				if (addFood(itr.next()) == false) {
					return false;
				}
			}
			return true;
		}
		
		public int getMainIngredient() {
			return combination & Food.FOOD_MASK_MAIN_INGREDIENT;
		}
		
		public int getAddOnA() {
			return combination & Food.FOOD_MASK_ADD_ON_A;
		}
		
		public int getAddOnB() {
			return combination & Food.FOOD_MASK_ADD_ON_B;
		}
		
		public int getSauce() {
			return combination & Food.FOOD_MASK_SAUCE;
		}
		
		public int getBeverage() {
			return combination & Food.FOOD_MASK_BEVERAGE;
		}
		
		public void closeToast() {
			toastClosed = true;
		}
		
		public boolean isToastClosed() {
			assert (combination & Food.FOOD_MASK_TOAST) != 0;
			return toastClosed;
		}
		
		public void set(Brunch other) {
			this.combination = other.combination;
			this.toastClosed = other.toastClosed;
		}
		
		public int getCost() {
			int cost = 0;
			Iterator itr = new Iterator(combination);
			while (itr.hasNext()) {
				cost += Food.getCost(itr.next());
			}
			return cost;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}
			
			if (!(o instanceof Brunch)) {
				return false;
			}
			
			Brunch lhs = (Brunch)o;
			return (this.combination == lhs.combination) && (this.toastClosed == lhs.toastClosed);
		}
		
		@Override
		public String toString() {
			return super.toString() + ", toastClosed:" + toastClosed;
		}
		
	}
	
}
