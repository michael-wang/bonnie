package com.studioirregular.bonnie.foodsystem.test;

import junit.framework.TestCase;
import android.util.Log;

import com.studioirregular.bonnie.foodsystem.FoodSystem;
import com.studioirregular.bonnie.foodsystem.FoodSystem.Brunch;
import com.studioirregular.bonnie.foodsystem.FoodSystem.Food;
import com.studioirregular.bonnie.foodsystem.FoodSystem.FoodCombination;

public class TestRandomGeneration extends TestCase {

	FoodSystem system;
	
	@Override
	protected void setUp() throws Exception {
		system = new FoodSystem();
	}
	
	@Override
	protected void tearDown() throws Exception {
		system = null;
	}
	
	public void testSingleFood() {
		forFood(Food.getBagel());
		forFood(Food.getCroissant());
		forFood(Food.getMuffinCircle());
		forFood(Food.getMuffinSquare());
		forFood(Food.getToastWhite());
		forFood(Food.getToastBlack());
		forFood(Food.getToastYellow());
	}
	
	private void forFood(Food food) {
		FoodCombination combination = new FoodCombination();
		combination.addFood(food);
		
		Brunch result = system.randomlyGenerate(combination);
		assertNotNull(result);
		
		Brunch correct = new Brunch();
		correct.addFood(food);
		assertEquals(correct, result);
	}
	
	public void testMultipleFoods() {
		FoodCombination combination = new FoodCombination();
		combination.addFood(Food.getBagel());
		combination.addFood(Food.getCroissant());
		combination.addFood(Food.getToastBlack());
		combination.addFood(Food.getToastYellow());
		combination.addFood(Food.getButter());
		combination.addFood(Food.getTomato());
		combination.addFood(Food.getCheese());
		combination.addFood(Food.getHam());
		combination.addFood(Food.getSausage());
		combination.addFood(Food.getCoffee());
		
		Log.d("test", "source:" + combination.toString());
		for (int c = 0; c < 10; c++) {
			forMultipleFoods(combination);
		}
	}
	
	private void forMultipleFoods(FoodCombination combination) {
		Brunch result = system.randomlyGenerate(combination);
		assertNotNull(result);
		
		Food[] resultCombination = result.getComposition();
		for (Food food : resultCombination) {
			assertTrue(combination.contains(food));
		}
		Log.d("test", "result:" + result.toString());
	}
	
}
