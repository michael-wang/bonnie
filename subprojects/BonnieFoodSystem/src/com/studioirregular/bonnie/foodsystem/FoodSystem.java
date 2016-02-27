package com.studioirregular.bonnie.foodsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.util.Log;

public class FoodSystem {

//	private static final boolean DO_LOG = true;
	private static final String TAG = "food-system";
	
	// TODO: factory return static create Food object.
	public static class Food {
		// factory functions
		public static Food getBagel() {
			return new Food(BAGEL);
		}
		public static Food getCroissant() {
			return new Food(CROISSANT);
		}
		public static Food getMuffinCircle() {
			return new Food(MUFFIN_CIRCLE);
		}
		public static Food getMuffinSquare() {
			return new Food(MUFFIN_SQUARE);
		}
		public static Food getToastWhite() {
			return new Food(TOAST_WHITE);
		}
		public static Food getToastBlack() {
			return new Food(TOAST_BLACK);
		}
		public static Food getToastYellow() {
			return new Food(TOAST_YELLOW);
		}
		public static Food getButter() {
			return new Food(SAUCE_BUTTER);
		}
		public static Food getHoney() {
			return new Food(SAUCE_HONEY);
		}
		public static Food getTomato() {
			return new Food(SAUCE_TOMATO);
		}
		public static Food getLettuce() {
			return new Food(LETTUCE);
		}
		public static Food getCheese() {
			return new Food(CHEESE);
		}
		public static Food getEgg() {
			return new Food(EGG);
		}
		public static Food getHam() {
			return new Food(HAM);
		}
		public static Food getSausage() {
			return new Food(SAUSAGE);
		}
		public static Food getMilk() {
			return new Food(MILK);
		}
		public static Food getCoffee() {
			return new Food(COFFEE);
		}
		public static Food getCandy(int level) {
			if (level == 1) {
				return new Food(CANDY_1);
			} else if (level == 2) {
				return new Food(CANDY_2);
			}
			return new Food(CANDY_3);
		}
		
		// for internal usage
		static Food get(int typeValue) {
			return new Food(typeValue);
		}
		
		public static final int TOTAL_TYPES		= 20;
		
		static final int BAGEL			= 0x1 << 0;
		static final int CROISSANT		= 0x1 << 1;
		static final int MUFFIN_CIRCLE	= 0x1 << 2;
		static final int MUFFIN_SQUARE	= 0x1 << 3;
		static final int TOAST_WHITE	= 0x1 << 4;
		static final int TOAST_BLACK	= 0x1 << 5;
		static final int TOAST_YELLOW	= 0x1 << 6;
		static final int SAUCE_BUTTER	= 0x1 << 7;
		static final int SAUCE_HONEY	= 0x1 << 8;
		static final int SAUCE_TOMATO	= 0x1 << 9;
		static final int LETTUCE		= 0x1 << 10;
		static final int CHEESE			= 0x1 << 11;
		static final int EGG			= 0x1 << 12;
		static final int HAM			= 0x1 << 13;
		static final int SAUSAGE		= 0x1 << 14;
		static final int MILK			= 0x1 << 15;
		static final int COFFEE			= 0x1 << 16;
		static final int CANDY_1		= 0x1 << 17;
		static final int CANDY_2		= 0x1 << 18;
		static final int CANDY_3		= 0x1 << 19;
		
		static final int FOOD_MASK_MAIN_INGREDIENT	= 0x0000007F;
		static final int FOOD_MASK_SAUCE			= 0x00000380;
		static final int FOOD_MASK_ADD_ON_A			= 0x00000C00;
		static final int FOOD_MASK_ADD_ON_B			= 0x00007000;
		static final int FOOD_MASK_BEVERAGE			= 0x00018000;
		static final int FOOD_MASK_CANDY			= 0x000E0000;
		
		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			
			if (!(o instanceof Food)) {
				return false;
			}
			
			Food lhs = (Food)o;
			
			return this.type == lhs.type;
		}
		
		@Override
		public int hashCode() {
			int result = 17;
			
			result = 31 * result + type;
			
			return result;
		}
		
		private static Map<Integer, String> FOOD_NAME = new HashMap<Integer, String>();
		static {
			FOOD_NAME.put(BAGEL,			"Bagel");
			FOOD_NAME.put(CROISSANT,		"Croissant");
			FOOD_NAME.put(MUFFIN_CIRCLE,	"Muffin-Circle");
			FOOD_NAME.put(MUFFIN_SQUARE,	"Muffin-Square");
			FOOD_NAME.put(TOAST_WHITE,		"Toast-White");
			FOOD_NAME.put(TOAST_BLACK,		"Toast-Black");
			FOOD_NAME.put(TOAST_YELLOW,		"Toast-Yellow");
			FOOD_NAME.put(SAUCE_BUTTER,		"Butter");
			FOOD_NAME.put(SAUCE_HONEY,		"Honey");
			FOOD_NAME.put(SAUCE_TOMATO,		"Tomato");
			FOOD_NAME.put(LETTUCE,			"Lettuce");
			FOOD_NAME.put(CHEESE,			"Cheese");
			FOOD_NAME.put(EGG,				"Ege");
			FOOD_NAME.put(HAM,				"Ham");
			FOOD_NAME.put(SAUSAGE,			"Sausage");
			FOOD_NAME.put(MILK,				"Milk");
			FOOD_NAME.put(COFFEE,			"Coffee");
			FOOD_NAME.put(CANDY_1,			"Candy-1");
			FOOD_NAME.put(CANDY_2,			"Candy-2");
			FOOD_NAME.put(CANDY_3,			"Candy-3");
		}
		
		@Override
		public String toString() {
			return FOOD_NAME.get(type);
		}
		
		private Food(int type) {
			this.type = type;
		}
		
		int getType() {
			return type;
		}
		
		private int type;
	}
	
	// combination of food without rules.
	public static class FoodCombination {
		public boolean addFood(Food food) {
			combination |= food.getType();
			return true;
		}
		
		public boolean contains(Food food) {
			return (combination & food.getType()) != 0;
		}
		
		public Food[] getComposition() {
			int count = 0;
			int x = combination;
			while (x > 0) {
				x &= (x-1);
				count++;
			}
			
			Food[] result = new Food[count];
			count = 0;
			x = 0x01;
			for (int i = 0; i < Food.TOTAL_TYPES; i++) {
				if ((combination & x) != 0) {
					result[count] = Food.get(x);
					count++;
				}
				x = x << 1;
			}
			return result;
		}
		
		@Override
		public String toString() {
			StringBuilder result = new StringBuilder("FoodCombination: ");
			Food[] foods = getComposition();
			for (Food food : foods) {
				result.append(food.toString());
				result.append(", ");
			}
			return result.toString();
		}
		
		protected int combination = 0;
	}
	
	// Brunch is food combination with certain rules.
	public static class Brunch extends FoodCombination {

		@Override
		public boolean addFood(Food food) {
			if ((food.getType() & Food.FOOD_MASK_CANDY) != 0) {
				return false;
			}
			
			boolean isMainIngredient = (food.getType() & Food.FOOD_MASK_MAIN_INGREDIENT) != 0;
			boolean alreadyHasMainIngredient = (combination & Food.FOOD_MASK_MAIN_INGREDIENT) != 0;
			
			if (isMainIngredient && alreadyHasMainIngredient) {
				return false;
			}
			
			boolean isSauce = (food.getType() & Food.FOOD_MASK_SAUCE) != 0;
			boolean alreadyHasSauce = (combination & Food.FOOD_MASK_SAUCE) != 0;
			if (isSauce && alreadyHasSauce) {
				return false;
			}
			
			boolean isAddOnA = (food.getType() & Food.FOOD_MASK_ADD_ON_A) != 0;
			boolean alreadyHasAddOnA = (combination & Food.FOOD_MASK_ADD_ON_A) != 0;
			if (isAddOnA && alreadyHasAddOnA) {
				return false;
			}
			
			boolean isAddOnB = (food.getType() & Food.FOOD_MASK_ADD_ON_B) != 0;
			boolean alreadyHasAddOnB = (combination & Food.FOOD_MASK_ADD_ON_B) != 0;
			if (isAddOnB && alreadyHasAddOnB) {
				return false;
			}
			
			boolean isBeverage = (food.getType() & Food.FOOD_MASK_BEVERAGE) != 0;
			boolean alreadyHasBeverage = (combination & Food.FOOD_MASK_BEVERAGE) != 0;
			if (isBeverage && alreadyHasBeverage) {
				return false;
			}
			
			return super.addFood(food);
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
			return this.combination == lhs.combination;
		}
		
		@Override
		public String toString() {
			StringBuilder result = new StringBuilder("Brunch: ");
			Food[] foods = getComposition();
			for (Food food : foods) {
				result.append(food.toString());
				result.append(", ");
			}
			return result.toString();
		}
		
	}
	
	public static Brunch randomlyGenerate(FoodCombination combination) {
		Brunch result = new Brunch();
		
		if (random == null) {
			random = new Random(System.currentTimeMillis());
		}
		
		List<Food> candidates = new ArrayList<Food>();
		
		if (combination.contains(Food.getBagel())) {
			candidates.add(Food.getBagel());
		}
		if (combination.contains(Food.getCroissant())) {
			candidates.add(Food.getCroissant());
		}
		if (combination.contains(Food.getMuffinCircle())) {
			candidates.add(Food.getMuffinCircle());
		}
		if (combination.contains(Food.getMuffinSquare())) {
			candidates.add(Food.getMuffinSquare());
		}
		if (combination.contains(Food.getToastWhite())) {
			candidates.add(Food.getToastWhite());
		}
		if (combination.contains(Food.getToastBlack())) {
			candidates.add(Food.getToastBlack());
		}
		if (combination.contains(Food.getToastYellow())) {
			candidates.add(Food.getToastYellow());
		}
		
		if (candidates.isEmpty()) {
			Log.e(TAG, "randomlyGenerate no main ingredient:" + combination);
			return null;
		}
		int index = random.nextInt(candidates.size());
		result.addFood(candidates.get(index));
		candidates.clear();
		
		if (combination.contains(Food.getButter())) {
			candidates.add(Food.getButter());
		}
		if (combination.contains(Food.getHoney())) {
			candidates.add(Food.getHoney());
		}
		if (combination.contains(Food.getTomato())) {
			candidates.add(Food.getTomato());
		}
		
		if (!candidates.isEmpty()) {
			index = random.nextInt(candidates.size());
			result.addFood(candidates.get(index));
			candidates.clear();
		}
		
		if (combination.contains(Food.getLettuce())) {
			candidates.add(Food.getLettuce());
		}
		if (combination.contains(Food.getCheese())) {
			candidates.add(Food.getCheese());
		}
		
		if (!candidates.isEmpty()) {
			index = random.nextInt(candidates.size());
			result.addFood(candidates.get(index));
			candidates.clear();
		}
		
		if (combination.contains(Food.getEgg())) {
			candidates.add(Food.getEgg());
		}
		if (combination.contains(Food.getHam())) {
			candidates.add(Food.getHam());
		}
		if (combination.contains(Food.getSausage())) {
			candidates.add(Food.getSausage());
		}
		
		if (!candidates.isEmpty()) {
			index = random.nextInt(candidates.size());
			result.addFood(candidates.get(index));
			candidates.clear();
		}
		
		if (combination.contains(Food.getMilk())) {
			candidates.add(Food.getMilk());
		}
		if (combination.contains(Food.getCoffee())) {
			candidates.add(Food.getCoffee());
		}
		
		if (!candidates.isEmpty()) {
			index = random.nextInt(candidates.size());
			result.addFood(candidates.get(index));
			candidates.clear();
		}
		
		return result;
	}
	
	private static Random random = null;
}
