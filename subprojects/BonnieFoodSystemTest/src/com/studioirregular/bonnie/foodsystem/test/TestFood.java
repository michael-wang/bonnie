package com.studioirregular.bonnie.foodsystem.test;

import junit.framework.TestCase;

import com.studioirregular.bonnie.foodsystem.FoodSystem.Food;

// TODO: test inequality
public class TestFood extends TestCase {

	public void testFoodCount() {
		assertEquals(20, Food.TOTAL_TYPES);
	}
	
	public void testCreation() {
		assertNotNull(Food.getBagel());
		assertNotNull(Food.getCroissant());
		
		assertNotNull(Food.getMuffinCircle());
		assertNotNull(Food.getMuffinSquare());
		
		assertNotNull(Food.getToastWhite());
		assertNotNull(Food.getToastBlack());
		assertNotNull(Food.getToastYellow());
		
		assertNotNull(Food.getButter());
		assertNotNull(Food.getHoney());
		assertNotNull(Food.getTomato());
		
		assertNotNull(Food.getLettuce());
		assertNotNull(Food.getCheese());
		
		assertNotNull(Food.getEgg());
		assertNotNull(Food.getHam());
		assertNotNull(Food.getSausage());
		
		assertNotNull(Food.getMilk());
		assertNotNull(Food.getCoffee());
		
		assertNotNull(Food.getCandy(1));
	}
	
	public void testEquality() {
		assertEquals(Food.getBagel(), Food.getBagel());
		assertEquals(Food.getCroissant(), Food.getCroissant());
		
		assertEquals(Food.getMuffinCircle(), Food.getMuffinCircle());
		assertEquals(Food.getMuffinSquare(), Food.getMuffinSquare());
		
		assertEquals(Food.getToastWhite(), Food.getToastWhite());
		assertEquals(Food.getToastBlack(), Food.getToastBlack());
		assertEquals(Food.getToastYellow(), Food.getToastYellow());
		
		assertEquals(Food.getButter(), Food.getButter());
		assertEquals(Food.getHoney(), Food.getHoney());
		assertEquals(Food.getTomato(), Food.getTomato());
		
		assertEquals(Food.getLettuce(), Food.getLettuce());
		assertEquals(Food.getCheese(), Food.getCheese());
		
		assertEquals(Food.getEgg(), Food.getEgg());
		assertEquals(Food.getHam(), Food.getHam());
		assertEquals(Food.getSausage(), Food.getSausage());
		
		assertEquals(Food.getMilk(), Food.getMilk());
		assertEquals(Food.getCoffee(), Food.getCoffee());
		
		assertEquals(Food.getCandy(1), Food.getCandy(1));
		assertEquals(Food.getCandy(2), Food.getCandy(2));
		assertEquals(Food.getCandy(3), Food.getCandy(3));
	}
	
	public void testInequality() {
		assertNotSame(Food.getBagel(), Food.getCroissant());
		assertNotSame(Food.getBagel(), Food.getMuffinSquare());
		assertNotSame(Food.getBagel(), Food.getMuffinCircle());
		assertNotSame(Food.getBagel(), Food.getToastWhite());
		assertNotSame(Food.getBagel(), Food.getToastBlack());
		assertNotSame(Food.getBagel(), Food.getToastYellow());
		assertNotSame(Food.getBagel(), Food.getButter());
		assertNotSame(Food.getBagel(), Food.getHoney());
		assertNotSame(Food.getBagel(), Food.getTomato());
		assertNotSame(Food.getBagel(), Food.getLettuce());
		assertNotSame(Food.getBagel(), Food.getCheese());
		assertNotSame(Food.getBagel(), Food.getEgg());
		assertNotSame(Food.getBagel(), Food.getHam());
		assertNotSame(Food.getBagel(), Food.getSausage());
		assertNotSame(Food.getBagel(), Food.getMilk());
		assertNotSame(Food.getBagel(), Food.getCoffee());
		
		assertNotSame(Food.getCandy(1), Food.getCandy(2));
		assertNotSame(Food.getCandy(1), Food.getCandy(3));
		assertNotSame(Food.getCandy(2), Food.getCandy(3));
	}
	
	public void testHashCode() {
		assertTrue(Food.getBagel().hashCode() == Food.getBagel().hashCode());
		assertTrue(Food.getBagel().hashCode() != Food.getCroissant().hashCode());
		assertTrue(Food.getBagel().hashCode() != Food.getMuffinSquare().hashCode());
		assertTrue(Food.getBagel().hashCode() != Food.getMuffinCircle().hashCode());
		assertTrue(Food.getBagel().hashCode() != Food.getToastWhite().hashCode());
		assertTrue(Food.getBagel().hashCode() != Food.getToastBlack().hashCode());
		assertTrue(Food.getBagel().hashCode() != Food.getToastBlack().hashCode());
		assertTrue(Food.getBagel().hashCode() != Food.getCroissant().hashCode());
		assertTrue(Food.getBagel().hashCode() != Food.getButter().hashCode());
		assertTrue(Food.getBagel().hashCode() != Food.getHoney().hashCode());
		assertTrue(Food.getBagel().hashCode() != Food.getTomato().hashCode());
		assertTrue(Food.getBagel().hashCode() != Food.getLettuce().hashCode());
		assertTrue(Food.getBagel().hashCode() != Food.getCheese().hashCode());
		assertTrue(Food.getBagel().hashCode() != Food.getEgg().hashCode());
		assertTrue(Food.getBagel().hashCode() != Food.getHam().hashCode());
		assertTrue(Food.getBagel().hashCode() != Food.getSausage().hashCode());
		assertTrue(Food.getBagel().hashCode() != Food.getMilk().hashCode());
		assertTrue(Food.getBagel().hashCode() != Food.getCoffee().hashCode());
		assertTrue(Food.getCandy(1).hashCode() == Food.getCandy(1).hashCode());
		assertTrue(Food.getCandy(2).hashCode() == Food.getCandy(2).hashCode());
		assertTrue(Food.getCandy(3).hashCode() == Food.getCandy(3).hashCode());
	}
}
