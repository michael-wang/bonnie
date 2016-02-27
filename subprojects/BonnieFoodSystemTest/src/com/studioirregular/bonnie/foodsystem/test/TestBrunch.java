package com.studioirregular.bonnie.foodsystem.test;

import junit.framework.TestCase;

import com.studioirregular.bonnie.foodsystem.FoodSystem.Brunch;
import com.studioirregular.bonnie.foodsystem.FoodSystem.Food;

public class TestBrunch extends TestCase {

	public void testBasicCombination() {
		Brunch mealA = new Brunch();
		assertTrue(mealA.addFood(Food.getBagel()));
		assertTrue(mealA.addFood(Food.getHoney()));
		assertFalse(mealA.addFood(Food.getCroissant()));
		assertFalse(mealA.addFood(Food.getButter()));
		assertFalse(mealA.addFood(Food.getCandy(1)));
		
		assertTrue(mealA.contains(Food.getBagel()));
		assertTrue(mealA.contains(Food.getHoney()));
		assertFalse(mealA.contains(Food.getCroissant()));
		assertFalse(mealA.contains(Food.getButter()));
		
		Brunch mealB = new Brunch();
		assertTrue(mealB.addFood(Food.getToastWhite()));
		assertTrue(mealB.addFood(Food.getSausage()));
		assertTrue(mealB.addFood(Food.getCheese()));
		assertTrue(mealB.addFood(Food.getMilk()));
		assertFalse(mealB.addFood(Food.getToastBlack()));
		assertFalse(mealB.addFood(Food.getHam()));
		assertFalse(mealB.addFood(Food.getCoffee()));
		
		assertTrue(mealB.contains(Food.getMilk()));
		assertTrue(mealB.contains(Food.getCheese()));
		assertTrue(mealB.contains(Food.getSausage()));
		assertTrue(mealB.contains(Food.getToastWhite()));
		assertFalse(mealB.contains(Food.getToastBlack()));
		assertFalse(mealB.contains(Food.getHam()));
		assertFalse(mealB.contains(Food.getCoffee()));
	}
	
	public void testEquality() {
		Brunch mealA = new Brunch();
		assertTrue(mealA.addFood(Food.getToastWhite()));
		assertTrue(mealA.addFood(Food.getSausage()));
		assertTrue(mealA.addFood(Food.getCheese()));
		assertTrue(mealA.addFood(Food.getMilk()));
		
		Brunch mealB = new Brunch();
		assertTrue(mealB.addFood(Food.getToastWhite()));
		assertTrue(mealB.addFood(Food.getSausage()));
		assertTrue(mealB.addFood(Food.getCheese()));
		assertTrue(mealB.addFood(Food.getMilk()));
		
		assertEquals(mealA, mealB);
		
		Brunch mealC = new Brunch();
		mealC.addFood(Food.getToastWhite());
		mealC.addFood(Food.getSausage());
		mealC.addFood(Food.getCheese());
		
		assertNotSame(mealC, mealA);
	}
}
