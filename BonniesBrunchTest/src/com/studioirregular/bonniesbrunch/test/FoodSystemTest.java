package com.studioirregular.bonniesbrunch.test;

import junit.framework.TestCase;

import com.studioirregular.bonniesbrunch.FoodSystem.Brunch;
import com.studioirregular.bonniesbrunch.FoodSystem.Food;
import com.studioirregular.bonniesbrunch.FoodSystem.FoodSet;

public class FoodSystemTest extends TestCase {
	
	public void testFoodCount() {
		assertEquals(17, Food.TOTAL_TYPES);
	}
	
	public void testCreation() {
		assertNotNull(Food.BAGEL);
		assertNotNull(Food.CROISSANT);
		
		assertNotNull(Food.MUFFIN_CIRCLE);
		assertNotNull(Food.MUFFIN_SQUARE);
		
		assertNotNull(Food.TOAST_WHITE);
		assertNotNull(Food.TOAST_BLACK);
		assertNotNull(Food.TOAST_YELLOW);
		
		assertNotNull(Food.BUTTER);
		assertNotNull(Food.HONEY);
		assertNotNull(Food.TOMATO);
		
		assertNotNull(Food.LETTUCE);
		assertNotNull(Food.CHEESE);
		
		assertNotNull(Food.EGG);
		assertNotNull(Food.HAM);
		assertNotNull(Food.HOTDOG);
		
		assertNotNull(Food.MILK);
		assertNotNull(Food.COFFEE);
	}
	
	public void testFoodEquality() {
		assertEquals(Food.BAGEL, Food.BAGEL);
		assertEquals(Food.CROISSANT, Food.CROISSANT);
		
		assertEquals(Food.MUFFIN_CIRCLE, Food.MUFFIN_CIRCLE);
		assertEquals(Food.MUFFIN_SQUARE, Food.MUFFIN_SQUARE);
		
		assertEquals(Food.TOAST_WHITE, Food.TOAST_WHITE);
		assertEquals(Food.TOAST_BLACK, Food.TOAST_BLACK);
		assertEquals(Food.TOAST_YELLOW, Food.TOAST_YELLOW);
		
		assertEquals(Food.BUTTER, Food.BUTTER);
		assertEquals(Food.HONEY, Food.HONEY);
		assertEquals(Food.TOMATO, Food.TOMATO);
		
		assertEquals(Food.LETTUCE, Food.LETTUCE);
		assertEquals(Food.CHEESE, Food.CHEESE);
		
		assertEquals(Food.EGG, Food.EGG);
		assertEquals(Food.HAM, Food.HAM);
		assertEquals(Food.HOTDOG, Food.HOTDOG);
		
		assertEquals(Food.MILK, Food.MILK);
		assertEquals(Food.COFFEE, Food.COFFEE);
	}
	
	public void testInequality() {
		assertNotSame(Food.BAGEL, Food.CROISSANT);
		assertNotSame(Food.BAGEL, Food.MUFFIN_SQUARE);
		assertNotSame(Food.BAGEL, Food.MUFFIN_CIRCLE);
		assertNotSame(Food.BAGEL, Food.TOAST_WHITE);
		assertNotSame(Food.BAGEL, Food.TOAST_BLACK);
		assertNotSame(Food.BAGEL, Food.TOAST_YELLOW);
		assertNotSame(Food.BAGEL, Food.BUTTER);
		assertNotSame(Food.BAGEL, Food.HONEY);
		assertNotSame(Food.BAGEL, Food.TOMATO);
		assertNotSame(Food.BAGEL, Food.LETTUCE);
		assertNotSame(Food.BAGEL, Food.CHEESE);
		assertNotSame(Food.BAGEL, Food.EGG);
		assertNotSame(Food.BAGEL, Food.HAM);
		assertNotSame(Food.BAGEL, Food.HOTDOG);
		assertNotSame(Food.BAGEL, Food.MILK);
		assertNotSame(Food.BAGEL, Food.COFFEE);
	}
	
	public void testFoodCombination() {
		FoodSet comb = new FoodSet();
		comb.addFood(Food.BAGEL);
		comb.addFood(Food.MUFFIN_CIRCLE);
		comb.addFood(Food.TOAST_BLACK);
		comb.addFood(Food.HONEY);
		comb.addFood(Food.CHEESE);
		comb.addFood(Food.HOTDOG);
		comb.addFood(Food.COFFEE);
		
		assertTrue(comb.contains(Food.BAGEL));
		assertTrue(comb.contains(Food.MUFFIN_CIRCLE));
		assertTrue(comb.contains(Food.TOAST_BLACK));
		assertTrue(comb.contains(Food.HONEY));
		assertTrue(comb.contains(Food.CHEESE));
		assertTrue(comb.contains(Food.HOTDOG));
		assertTrue(comb.contains(Food.COFFEE));
		
		assertFalse(comb.contains(Food.CROISSANT));
		assertFalse(comb.contains(Food.MUFFIN_SQUARE));
		assertFalse(comb.contains(Food.TOAST_WHITE));
		assertFalse(comb.contains(Food.TOAST_YELLOW));
		assertFalse(comb.contains(Food.BUTTER));
		assertFalse(comb.contains(Food.TOMATO));
		assertFalse(comb.contains(Food.LETTUCE));
		assertFalse(comb.contains(Food.EGG));
		assertFalse(comb.contains(Food.HAM));
		assertFalse(comb.contains(Food.MILK));
	}
	
	public void testBagelCombination() {
		Brunch meal = new Brunch();
		
		assertTrue(meal.addFood(Food.BAGEL));
		assertFalse(meal.addFood(Food.BAGEL));
		assertFalse(meal.addFood(Food.CROISSANT));
		assertFalse(meal.addFood(Food.MUFFIN_CIRCLE));
		assertFalse(meal.addFood(Food.MUFFIN_SQUARE));
		assertFalse(meal.addFood(Food.TOAST_WHITE));
		assertFalse(meal.addFood(Food.TOAST_BLACK));
		assertFalse(meal.addFood(Food.TOAST_YELLOW));
		assertFalse(meal.addFood(Food.LETTUCE));
		assertFalse(meal.addFood(Food.CHEESE));
		assertFalse(meal.addFood(Food.EGG));
		assertFalse(meal.addFood(Food.HAM));
		assertFalse(meal.addFood(Food.HOTDOG));
		meal.reset();
		
		assertTrue(meal.addFood(Food.BAGEL));
		assertTrue(meal.addFood(Food.BUTTER));
		assertFalse(meal.addFood(Food.HONEY));
		assertFalse(meal.addFood(Food.TOMATO));
		meal.reset();
		
		assertTrue(meal.addFood(Food.BAGEL));
		assertTrue(meal.addFood(Food.HONEY));
		assertFalse(meal.addFood(Food.BUTTER));
		assertFalse(meal.addFood(Food.TOMATO));
		meal.reset();
		
		assertTrue(meal.addFood(Food.BAGEL));
		assertTrue(meal.addFood(Food.TOMATO));
		assertFalse(meal.addFood(Food.BUTTER));
		assertFalse(meal.addFood(Food.HONEY));
		meal.reset();
		
		assertTrue(meal.addFood(Food.BAGEL));
		assertTrue(meal.addFood(Food.MILK));
		assertFalse(meal.addFood(Food.COFFEE));
		meal.reset();
		
		assertTrue(meal.addFood(Food.BAGEL));
		assertTrue(meal.addFood(Food.COFFEE));
		assertFalse(meal.addFood(Food.MILK));
		meal.reset();
	}
	
	public void testCroissantCombination() {
		Brunch meal = new Brunch();
		
		assertTrue(meal.addFood(Food.CROISSANT));
		assertFalse(meal.addFood(Food.BAGEL));
		assertFalse(meal.addFood(Food.CROISSANT));
		assertFalse(meal.addFood(Food.MUFFIN_CIRCLE));
		assertFalse(meal.addFood(Food.MUFFIN_SQUARE));
		assertFalse(meal.addFood(Food.TOAST_WHITE));
		assertFalse(meal.addFood(Food.TOAST_BLACK));
		assertFalse(meal.addFood(Food.TOAST_YELLOW));
		assertFalse(meal.addFood(Food.LETTUCE));
		assertFalse(meal.addFood(Food.CHEESE));
		assertFalse(meal.addFood(Food.EGG));
		assertFalse(meal.addFood(Food.HAM));
		assertFalse(meal.addFood(Food.HOTDOG));
		meal.reset();
		
		assertTrue(meal.addFood(Food.CROISSANT));
		assertTrue(meal.addFood(Food.BUTTER));
		assertFalse(meal.addFood(Food.HONEY));
		assertFalse(meal.addFood(Food.TOMATO));
		meal.reset();
		
		assertTrue(meal.addFood(Food.CROISSANT));
		assertTrue(meal.addFood(Food.HONEY));
		assertFalse(meal.addFood(Food.BUTTER));
		assertFalse(meal.addFood(Food.TOMATO));
		meal.reset();
		
		assertTrue(meal.addFood(Food.CROISSANT));
		assertTrue(meal.addFood(Food.TOMATO));
		assertFalse(meal.addFood(Food.BUTTER));
		assertFalse(meal.addFood(Food.HONEY));
		meal.reset();
		
		assertTrue(meal.addFood(Food.CROISSANT));
		assertTrue(meal.addFood(Food.MILK));
		assertFalse(meal.addFood(Food.COFFEE));
		meal.reset();
		
		assertTrue(meal.addFood(Food.CROISSANT));
		assertTrue(meal.addFood(Food.COFFEE));
		assertFalse(meal.addFood(Food.MILK));
		meal.reset();
	}
	
	public void testMuffinCircleCombination() {
		Brunch meal = new Brunch();
		
		assertTrue(meal.addFood(Food.MUFFIN_CIRCLE));
		assertFalse(meal.addFood(Food.BAGEL));
		assertFalse(meal.addFood(Food.CROISSANT));
		assertFalse(meal.addFood(Food.MUFFIN_CIRCLE));
		assertFalse(meal.addFood(Food.MUFFIN_SQUARE));
		assertFalse(meal.addFood(Food.TOAST_WHITE));
		assertFalse(meal.addFood(Food.TOAST_BLACK));
		assertFalse(meal.addFood(Food.TOAST_YELLOW));
		assertFalse(meal.addFood(Food.LETTUCE));
		assertFalse(meal.addFood(Food.CHEESE));
		assertFalse(meal.addFood(Food.EGG));
		assertFalse(meal.addFood(Food.HAM));
		assertFalse(meal.addFood(Food.HOTDOG));
		meal.reset();
		
		assertTrue(meal.addFood(Food.MUFFIN_CIRCLE));
		assertTrue(meal.addFood(Food.BUTTER));
		assertFalse(meal.addFood(Food.HONEY));
		assertFalse(meal.addFood(Food.TOMATO));
		meal.reset();
		
		assertTrue(meal.addFood(Food.MUFFIN_CIRCLE));
		assertTrue(meal.addFood(Food.HONEY));
		assertFalse(meal.addFood(Food.BUTTER));
		assertFalse(meal.addFood(Food.TOMATO));
		meal.reset();
		
		assertTrue(meal.addFood(Food.MUFFIN_CIRCLE));
		assertTrue(meal.addFood(Food.TOMATO));
		assertFalse(meal.addFood(Food.BUTTER));
		assertFalse(meal.addFood(Food.HONEY));
		meal.reset();
		
		assertTrue(meal.addFood(Food.MUFFIN_CIRCLE));
		assertTrue(meal.addFood(Food.MILK));
		assertFalse(meal.addFood(Food.COFFEE));
		meal.reset();
		
		assertTrue(meal.addFood(Food.MUFFIN_CIRCLE));
		assertTrue(meal.addFood(Food.COFFEE));
		assertFalse(meal.addFood(Food.MILK));
		meal.reset();
	}
	
	public void testMuffinSquareCombination() {
		Brunch meal = new Brunch();
		
		assertTrue(meal.addFood(Food.MUFFIN_SQUARE));
		assertFalse(meal.addFood(Food.BAGEL));
		assertFalse(meal.addFood(Food.CROISSANT));
		assertFalse(meal.addFood(Food.MUFFIN_CIRCLE));
		assertFalse(meal.addFood(Food.MUFFIN_SQUARE));
		assertFalse(meal.addFood(Food.TOAST_WHITE));
		assertFalse(meal.addFood(Food.TOAST_BLACK));
		assertFalse(meal.addFood(Food.TOAST_YELLOW));
		assertFalse(meal.addFood(Food.LETTUCE));
		assertFalse(meal.addFood(Food.CHEESE));
		assertFalse(meal.addFood(Food.EGG));
		assertFalse(meal.addFood(Food.HAM));
		assertFalse(meal.addFood(Food.HOTDOG));
		meal.reset();
		
		assertTrue(meal.addFood(Food.MUFFIN_SQUARE));
		assertTrue(meal.addFood(Food.BUTTER));
		assertFalse(meal.addFood(Food.HONEY));
		assertFalse(meal.addFood(Food.TOMATO));
		meal.reset();
		
		assertTrue(meal.addFood(Food.MUFFIN_SQUARE));
		assertTrue(meal.addFood(Food.HONEY));
		assertFalse(meal.addFood(Food.BUTTER));
		assertFalse(meal.addFood(Food.TOMATO));
		meal.reset();
		
		assertTrue(meal.addFood(Food.MUFFIN_SQUARE));
		assertTrue(meal.addFood(Food.TOMATO));
		assertFalse(meal.addFood(Food.BUTTER));
		assertFalse(meal.addFood(Food.HONEY));
		meal.reset();
		
		assertTrue(meal.addFood(Food.MUFFIN_SQUARE));
		assertTrue(meal.addFood(Food.MILK));
		assertFalse(meal.addFood(Food.COFFEE));
		meal.reset();
		
		assertTrue(meal.addFood(Food.MUFFIN_SQUARE));
		assertTrue(meal.addFood(Food.COFFEE));
		assertFalse(meal.addFood(Food.MILK));
		meal.reset();
	}
	
	public void testToastWhiteCombination() {
		Brunch meal = new Brunch();
		
		assertTrue(meal.addFood(Food.TOAST_WHITE));
		assertFalse(meal.addFood(Food.BAGEL));
		assertFalse(meal.addFood(Food.CROISSANT));
		assertFalse(meal.addFood(Food.MUFFIN_CIRCLE));
		assertFalse(meal.addFood(Food.MUFFIN_SQUARE));
		assertFalse(meal.addFood(Food.TOAST_WHITE));
		assertFalse(meal.addFood(Food.TOAST_BLACK));
		assertFalse(meal.addFood(Food.TOAST_YELLOW));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_WHITE));
		assertTrue(meal.addFood(Food.LETTUCE));
		assertFalse(meal.addFood(Food.CHEESE));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_WHITE));
		assertTrue(meal.addFood(Food.CHEESE));
		assertFalse(meal.addFood(Food.LETTUCE));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_WHITE));
		assertTrue(meal.addFood(Food.EGG));
		assertFalse(meal.addFood(Food.HAM));
		assertFalse(meal.addFood(Food.HOTDOG));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_WHITE));
		assertTrue(meal.addFood(Food.HAM));
		assertFalse(meal.addFood(Food.EGG));
		assertFalse(meal.addFood(Food.HOTDOG));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_WHITE));
		assertTrue(meal.addFood(Food.HOTDOG));
		assertFalse(meal.addFood(Food.EGG));
		assertFalse(meal.addFood(Food.HAM));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_WHITE));
		assertTrue(meal.addFood(Food.BUTTER));
		assertFalse(meal.addFood(Food.HONEY));
		assertFalse(meal.addFood(Food.TOMATO));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_WHITE));
		assertTrue(meal.addFood(Food.HONEY));
		assertFalse(meal.addFood(Food.BUTTER));
		assertFalse(meal.addFood(Food.TOMATO));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_WHITE));
		assertTrue(meal.addFood(Food.TOMATO));
		assertFalse(meal.addFood(Food.BUTTER));
		assertFalse(meal.addFood(Food.HONEY));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_WHITE));
		assertTrue(meal.addFood(Food.MILK));
		assertFalse(meal.addFood(Food.COFFEE));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_WHITE));
		assertTrue(meal.addFood(Food.COFFEE));
		assertFalse(meal.addFood(Food.MILK));
		meal.reset();
	}
	
	public void testToastBlackCombination() {
		Brunch meal = new Brunch();
		
		assertTrue(meal.addFood(Food.TOAST_BLACK));
		assertFalse(meal.addFood(Food.BAGEL));
		assertFalse(meal.addFood(Food.CROISSANT));
		assertFalse(meal.addFood(Food.MUFFIN_CIRCLE));
		assertFalse(meal.addFood(Food.MUFFIN_SQUARE));
		assertFalse(meal.addFood(Food.TOAST_WHITE));
		assertFalse(meal.addFood(Food.TOAST_BLACK));
		assertFalse(meal.addFood(Food.TOAST_YELLOW));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_BLACK));
		assertTrue(meal.addFood(Food.LETTUCE));
		assertFalse(meal.addFood(Food.CHEESE));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_BLACK));
		assertTrue(meal.addFood(Food.CHEESE));
		assertFalse(meal.addFood(Food.LETTUCE));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_BLACK));
		assertTrue(meal.addFood(Food.EGG));
		assertFalse(meal.addFood(Food.HAM));
		assertFalse(meal.addFood(Food.HOTDOG));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_BLACK));
		assertTrue(meal.addFood(Food.HAM));
		assertFalse(meal.addFood(Food.EGG));
		assertFalse(meal.addFood(Food.HOTDOG));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_BLACK));
		assertTrue(meal.addFood(Food.HOTDOG));
		assertFalse(meal.addFood(Food.EGG));
		assertFalse(meal.addFood(Food.HAM));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_BLACK));
		assertTrue(meal.addFood(Food.BUTTER));
		assertFalse(meal.addFood(Food.HONEY));
		assertFalse(meal.addFood(Food.TOMATO));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_BLACK));
		assertTrue(meal.addFood(Food.HONEY));
		assertFalse(meal.addFood(Food.BUTTER));
		assertFalse(meal.addFood(Food.TOMATO));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_BLACK));
		assertTrue(meal.addFood(Food.TOMATO));
		assertFalse(meal.addFood(Food.BUTTER));
		assertFalse(meal.addFood(Food.HONEY));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_BLACK));
		assertTrue(meal.addFood(Food.MILK));
		assertFalse(meal.addFood(Food.COFFEE));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_BLACK));
		assertTrue(meal.addFood(Food.COFFEE));
		assertFalse(meal.addFood(Food.MILK));
		meal.reset();
	}
	
	public void testToastYellowCombination() {
		Brunch meal = new Brunch();
		
		assertTrue(meal.addFood(Food.TOAST_YELLOW));
		assertFalse(meal.addFood(Food.BAGEL));
		assertFalse(meal.addFood(Food.CROISSANT));
		assertFalse(meal.addFood(Food.MUFFIN_CIRCLE));
		assertFalse(meal.addFood(Food.MUFFIN_SQUARE));
		assertFalse(meal.addFood(Food.TOAST_WHITE));
		assertFalse(meal.addFood(Food.TOAST_BLACK));
		assertFalse(meal.addFood(Food.TOAST_YELLOW));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_YELLOW));
		assertTrue(meal.addFood(Food.LETTUCE));
		assertFalse(meal.addFood(Food.CHEESE));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_YELLOW));
		assertTrue(meal.addFood(Food.CHEESE));
		assertFalse(meal.addFood(Food.LETTUCE));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_YELLOW));
		assertTrue(meal.addFood(Food.EGG));
		assertFalse(meal.addFood(Food.HAM));
		assertFalse(meal.addFood(Food.HOTDOG));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_YELLOW));
		assertTrue(meal.addFood(Food.HAM));
		assertFalse(meal.addFood(Food.EGG));
		assertFalse(meal.addFood(Food.HOTDOG));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_YELLOW));
		assertTrue(meal.addFood(Food.HOTDOG));
		assertFalse(meal.addFood(Food.EGG));
		assertFalse(meal.addFood(Food.HAM));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_YELLOW));
		assertTrue(meal.addFood(Food.BUTTER));
		assertFalse(meal.addFood(Food.HONEY));
		assertFalse(meal.addFood(Food.TOMATO));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_YELLOW));
		assertTrue(meal.addFood(Food.HONEY));
		assertFalse(meal.addFood(Food.BUTTER));
		assertFalse(meal.addFood(Food.TOMATO));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_YELLOW));
		assertTrue(meal.addFood(Food.TOMATO));
		assertFalse(meal.addFood(Food.BUTTER));
		assertFalse(meal.addFood(Food.HONEY));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_YELLOW));
		assertTrue(meal.addFood(Food.MILK));
		assertFalse(meal.addFood(Food.COFFEE));
		meal.reset();
		
		assertTrue(meal.addFood(Food.TOAST_YELLOW));
		assertTrue(meal.addFood(Food.COFFEE));
		assertFalse(meal.addFood(Food.MILK));
		meal.reset();
	}
	
	public void testLettuceCombination() {
		Brunch meal = new Brunch();
		
		assertTrue(meal.addFood(Food.LETTUCE));
		assertFalse(meal.addFood(Food.BAGEL));
		assertFalse(meal.addFood(Food.CROISSANT));
		assertFalse(meal.addFood(Food.MUFFIN_CIRCLE));
		assertFalse(meal.addFood(Food.MUFFIN_SQUARE));
		assertTrue(meal.addFood(Food.TOAST_WHITE));
		meal.reset();
		
		assertTrue(meal.addFood(Food.LETTUCE));
		assertTrue(meal.addFood(Food.TOAST_BLACK));
		meal.reset();
		
		assertTrue(meal.addFood(Food.LETTUCE));
		assertTrue(meal.addFood(Food.TOAST_YELLOW));
		meal.reset();
		
		assertTrue(meal.addFood(Food.LETTUCE));
		assertFalse(meal.addFood(Food.CHEESE));
		assertTrue(meal.addFood(Food.EGG));
		meal.reset();
		
		assertTrue(meal.addFood(Food.LETTUCE));
		assertTrue(meal.addFood(Food.HAM));
		meal.reset();
		
		assertTrue(meal.addFood(Food.LETTUCE));
		assertTrue(meal.addFood(Food.HOTDOG));
		meal.reset();
	}
	
	public void testCheeseCombination() {
		Brunch meal = new Brunch();
		
		assertTrue(meal.addFood(Food.CHEESE));
		assertFalse(meal.addFood(Food.BAGEL));
		assertFalse(meal.addFood(Food.CROISSANT));
		assertFalse(meal.addFood(Food.MUFFIN_CIRCLE));
		assertFalse(meal.addFood(Food.MUFFIN_SQUARE));
		assertTrue(meal.addFood(Food.TOAST_WHITE));
		meal.reset();
		
		assertTrue(meal.addFood(Food.CHEESE));
		assertTrue(meal.addFood(Food.TOAST_BLACK));
		meal.reset();
		
		assertTrue(meal.addFood(Food.CHEESE));
		assertTrue(meal.addFood(Food.TOAST_YELLOW));
		meal.reset();
		
		assertTrue(meal.addFood(Food.CHEESE));
		assertFalse(meal.addFood(Food.LETTUCE));
		assertTrue(meal.addFood(Food.EGG));
		meal.reset();
		
		assertTrue(meal.addFood(Food.CHEESE));
		assertTrue(meal.addFood(Food.HAM));
		meal.reset();
		
		assertTrue(meal.addFood(Food.CHEESE));
		assertTrue(meal.addFood(Food.HOTDOG));
		meal.reset();
	}
	
	public void testEggCombination() {
		Brunch meal = new Brunch();
		
		assertTrue(meal.addFood(Food.EGG));
		assertFalse(meal.addFood(Food.BAGEL));
		assertFalse(meal.addFood(Food.CROISSANT));
		assertFalse(meal.addFood(Food.MUFFIN_CIRCLE));
		assertFalse(meal.addFood(Food.MUFFIN_SQUARE));
		assertTrue(meal.addFood(Food.TOAST_WHITE));
		meal.reset();
		
		assertTrue(meal.addFood(Food.EGG));
		assertTrue(meal.addFood(Food.TOAST_BLACK));
		meal.reset();
		
		assertTrue(meal.addFood(Food.EGG));
		assertTrue(meal.addFood(Food.TOAST_YELLOW));
		meal.reset();
		
		assertTrue(meal.addFood(Food.EGG));
		assertFalse(meal.addFood(Food.HAM));
		assertFalse(meal.addFood(Food.HOTDOG));
		assertTrue(meal.addFood(Food.LETTUCE));
		meal.reset();
		
		assertTrue(meal.addFood(Food.EGG));
		assertTrue(meal.addFood(Food.CHEESE));
		meal.reset();
	}
	
	public void testHamCombination() {
		Brunch meal = new Brunch();
		
		assertTrue(meal.addFood(Food.HAM));
		assertFalse(meal.addFood(Food.BAGEL));
		assertFalse(meal.addFood(Food.CROISSANT));
		assertFalse(meal.addFood(Food.MUFFIN_CIRCLE));
		assertFalse(meal.addFood(Food.MUFFIN_SQUARE));
		assertTrue(meal.addFood(Food.TOAST_WHITE));
		meal.reset();
		
		assertTrue(meal.addFood(Food.HAM));
		assertTrue(meal.addFood(Food.TOAST_BLACK));
		meal.reset();
		
		assertTrue(meal.addFood(Food.HAM));
		assertTrue(meal.addFood(Food.TOAST_YELLOW));
		meal.reset();
		
		assertTrue(meal.addFood(Food.HAM));
		assertFalse(meal.addFood(Food.EGG));
		assertFalse(meal.addFood(Food.HOTDOG));
		assertTrue(meal.addFood(Food.LETTUCE));
		meal.reset();
		
		assertTrue(meal.addFood(Food.HAM));
		assertTrue(meal.addFood(Food.CHEESE));
		meal.reset();
	}
	
	public void testHotdogCombination() {
		Brunch meal = new Brunch();
		
		assertTrue(meal.addFood(Food.HOTDOG));
		assertFalse(meal.addFood(Food.BAGEL));
		assertFalse(meal.addFood(Food.CROISSANT));
		assertFalse(meal.addFood(Food.MUFFIN_CIRCLE));
		assertFalse(meal.addFood(Food.MUFFIN_SQUARE));
		assertTrue(meal.addFood(Food.TOAST_WHITE));
		meal.reset();
		
		assertTrue(meal.addFood(Food.HOTDOG));
		assertTrue(meal.addFood(Food.TOAST_BLACK));
		meal.reset();
		
		assertTrue(meal.addFood(Food.HOTDOG));
		assertTrue(meal.addFood(Food.TOAST_YELLOW));
		meal.reset();
		
		assertTrue(meal.addFood(Food.HOTDOG));
		assertFalse(meal.addFood(Food.EGG));
		assertFalse(meal.addFood(Food.HAM));
		assertTrue(meal.addFood(Food.LETTUCE));
		meal.reset();
		
		assertTrue(meal.addFood(Food.HOTDOG));
		assertTrue(meal.addFood(Food.CHEESE));
		meal.reset();
	}
	
	public void testGetSetItem() {
		FoodSet set = new FoodSet();
		set.addFood(Food.BAGEL);
		set.addFood(Food.CROISSANT);
		
		assertEquals(2, set.size());
		assertEquals(Food.BAGEL, set.get(0));
		assertEquals(Food.CROISSANT, set.get(1));
		assertEquals(Food.INVALID_TYPE, set.get(2));
	}
	
	public void testBrunchEquality() {
		Brunch mealA = new Brunch();
		assertTrue(mealA.addFood(Food.TOAST_WHITE));
		assertTrue(mealA.addFood(Food.HOTDOG));
		assertTrue(mealA.addFood(Food.CHEESE));
		assertTrue(mealA.addFood(Food.MILK));
		
		Brunch mealB = new Brunch();
		assertTrue(mealB.addFood(Food.TOAST_WHITE));
		assertTrue(mealB.addFood(Food.HOTDOG));
		assertTrue(mealB.addFood(Food.CHEESE));
		assertTrue(mealB.addFood(Food.MILK));
		
		assertEquals(mealA, mealB);
		
		Brunch mealC = new Brunch();
		mealC.addFood(Food.TOAST_WHITE);
		mealC.addFood(Food.HOTDOG);
		mealC.addFood(Food.CHEESE);
		
		assertNotSame(mealC, mealA);
	}
	
}
