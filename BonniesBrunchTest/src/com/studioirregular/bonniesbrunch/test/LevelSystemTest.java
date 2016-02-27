package com.studioirregular.bonniesbrunch.test;

import java.io.IOException;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.FoodSystem.Food;
import com.studioirregular.bonniesbrunch.FoodSystem.FoodSet;
import com.studioirregular.bonniesbrunch.LevelSystem;
import com.studioirregular.bonniesbrunch.LevelSystem.AppearanceConfig;
import com.studioirregular.bonniesbrunch.LevelSystem.GameLevel;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelCustomerConfig;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelLockState;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelScore;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelTimePeriod;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelXmlFormatException;
import com.studioirregular.bonniesbrunch.LevelSystem.TableConfig;
import com.studioirregular.bonniesbrunch.LevelSystem.TableItem;
import com.studioirregular.bonniesbrunch.LevelSystem.TableLevelItem;
import com.studioirregular.bonniesbrunch.entity.Customer;

public class LevelSystemTest extends InstrumentationTestCase {

	public void testParseLevel() {
		Context context = getInstrumentation().getTargetContext();
		
		final int LEVEL_MAJOR = 0;
		final int LEVEL_MINOR = 0;
		Exception parseException = null;
		GameLevel level = null;
		try {
			level = LevelSystem.load(context, LEVEL_MAJOR, LEVEL_MINOR);
		} catch (LevelXmlFormatException e) {
			parseException = e;
		} catch (XmlPullParserException e) {
			parseException = e;
		} catch (IOException e) {
			parseException = e;
		}
		
		assertNull(parseException);
		assertNotNull(level);
		assertEquals(LEVEL_MAJOR, level.number.major);
		assertEquals(LEVEL_MINOR, level.number.minor);
		
		LevelScore score = level.score;
		assertTrue(score.min > 0);
		assertTrue(score.med > score.min);
		assertTrue(score.high > score.med);
		assertTrue(score.max > score.high);
		
		assertTrue(level.time.total > 0);
		assertEquals(1, level.time.periods.size());
		
		LevelTimePeriod period = level.time.periods.get(0);
		assertEquals(0, period.start);
		assertEquals(3, period.minCustomerInterval);
		assertEquals(3, period.maxCustomerInterval);
		
		TableConfig table = level.tableConfig;
		assertNotNull(table);
		
		List<TableItem> items = table.items;
		assertNotNull(items);
		assertEquals(15, items.size());
		assertEquals(TableItem.BAGEL,		items.get(0).type);
		assertEquals(TableItem.CROISSANT,	items.get(1).type);
		assertEquals(TableItem.BUTTER,		items.get(2).type);
		assertEquals(TableItem.HONEY,		items.get(3).type);
		assertEquals(TableItem.TOMATO,		items.get(4).type);
		assertEquals(TableItem.LETTUCE,		items.get(5).type);
		assertEquals(TableItem.CHEESE,		items.get(6).type);
		assertEquals(TableItem.MILK,		items.get(7).type);
		assertEquals(TableItem.COFFEE,		items.get(8).type);
		assertEquals(TableItem.MUFFIN_MACHINE,	items.get(9).type);
		assertEquals(2, ((TableLevelItem)items.get(9)).level);
		assertEquals(TableItem.TOAST_MACHINE,	items.get(10).type);
		assertEquals(3, ((TableLevelItem)items.get(10)).level);
		assertEquals(TableItem.FRYING_PAN,		items.get(11).type);
		assertEquals(3, ((TableLevelItem)items.get(11)).level);
		assertEquals(TableItem.CANDY_MACHINE,	items.get(12).type);
		assertEquals(3, ((TableLevelItem)items.get(12)).level);
		assertEquals(TableItem.PLATE,	items.get(13).type);
		assertEquals(TableItem.TRASHCAN,	items.get(14).type);
		
		assertEquals(1, level.fixedTimeAppearingCustomers.size());
		List<LevelCustomerConfig> customers = level.randomAppearingCustomers;
		assertEquals(9, customers.size());
		
		LevelCustomerConfig customer = customers.get(0);
		assertEquals(Customer.Type.JOGGING_GIRL, customer.type);
		assertEquals(3, customer.appearanceConfig.weight);
		FoodSet combination = new FoodSet();
		combination.addFood(Food.BAGEL);
		combination.addFood(Food.CROISSANT);
		combination.addFood(Food.HONEY);
		combination.addFood(Food.COFFEE);
		assertEquals(combination, customer.optionalSet);
		
		customer = customers.get(1);
		assertEquals(Customer.Type.BALLOON_BOY, customer.type);
		assertEquals(1, customer.appearanceConfig.weight);
		combination.reset();
		combination.addFood(Food.MUFFIN_CIRCLE);
		combination.addFood(Food.MUFFIN_SQUARE);
		combination.addFood(Food.TOAST_WHITE);
		combination.addFood(Food.CHEESE);
		combination.addFood(Food.MILK);
		assertEquals(combination, customer.optionalSet);
		
		customer = customers.get(2);
		assertEquals(Customer.Type.WORKING_MAN, customer.type);
		assertEquals(1, customer.appearanceConfig.weight);
		combination.reset();
		combination.addFood(Food.TOAST_BLACK);
		combination.addFood(Food.EGG);
		combination.addFood(Food.HAM);
		combination.addFood(Food.CHEESE);
		combination.addFood(Food.COFFEE);
		assertEquals(combination, customer.optionalSet);
		
		customer = customers.get(3);
		assertEquals(Customer.Type.GIRL_WITH_DOG, customer.type);
		assertEquals(3, customer.appearanceConfig.weight);
		combination.reset();
		combination.addFood(Food.MUFFIN_CIRCLE);
		combination.addFood(Food.MUFFIN_SQUARE);
		combination.addFood(Food.TOAST_YELLOW);
		combination.addFood(Food.LETTUCE);
		combination.addFood(Food.MILK);
		assertEquals(combination, customer.optionalSet);
		
		customer = customers.get(4);
		assertEquals(Customer.Type.GRANNY, customer.type);
		assertEquals(3, customer.appearanceConfig.weight);
		combination.reset();
		combination.addFood(Food.BAGEL);
		combination.addFood(Food.CROISSANT);
		combination.addFood(Food.HONEY);
		combination.addFood(Food.COFFEE);
		assertEquals(combination, customer.optionalSet);
		
		customer = customers.get(5);
		assertEquals(Customer.Type.SUPERSTAR_MAN, customer.type);
		assertEquals(1, customer.appearanceConfig.weight);
		combination.reset();
		combination.addFood(Food.TOAST_WHITE);
		combination.addFood(Food.HAM);
		combination.addFood(Food.HOTDOG);
		combination.addFood(Food.COFFEE);
		assertEquals(combination, customer.optionalSet);
		
		customer = customers.get(6);
		assertEquals(Customer.Type.PHYSICIST, customer.type);
		assertEquals(1, customer.appearanceConfig.weight);
		combination.reset();
		combination.addFood(Food.TOAST_BLACK);
		combination.addFood(Food.HAM);
		combination.addFood(Food.LETTUCE);
		combination.addFood(Food.HONEY);
		combination.addFood(Food.COFFEE);
		assertEquals(combination, customer.optionalSet);
		
		customer = customers.get(7);
		assertEquals(Customer.Type.SUPERSTAR_LADY, customer.type);
		assertEquals(3, customer.appearanceConfig.weight);
		combination.reset();
		combination.addFood(Food.BAGEL);
		combination.addFood(Food.CROISSANT);
		combination.addFood(Food.MUFFIN_CIRCLE);
		combination.addFood(Food.TOAST_WHITE);
		combination.addFood(Food.COFFEE);
		assertEquals(combination, customer.optionalSet);
		
		customer = customers.get(8);
		assertEquals(Customer.Type.FOOD_CRITIC, customer.type);
		assertEquals(1, customer.appearanceConfig.weight);
		combination.reset();
		combination.addFood(Food.TOAST_YELLOW);
		combination.addFood(Food.HOTDOG);
		combination.addFood(Food.LETTUCE);
		combination.addFood(Food.TOMATO);
		combination.addFood(Food.COFFEE);
		assertEquals(combination, customer.optionalSet);
		
		customer = level.fixedTimeAppearingCustomers.get(0);
		assertEquals(Customer.Type.TRAMP, customer.type);
		combination.reset();
		combination.addFood(Food.BAGEL);
		combination.addFood(Food.HONEY);
		combination.addFood(Food.COFFEE);
		assertEquals(combination, customer.optionalSet);
		assertEquals(AppearanceConfig.Type.FIXED, customer.appearanceConfig.type);
		assertEquals(9, customer.appearanceConfig.times.size());
	}
	
	public void testAllLevels() {
		parseLevel(0, 0);
		for (int i = 1; i <= 4; i++) {
			for (int j = 1; j <= 10; j++) {
				if (j % 5  != 0) {	// escape special level for it's not complete
					parseLevel(i, j);
				}
			}
		}
	}
	
	private void parseLevel(int major, int minor) {
		Exception parseException = null;
		GameLevel level = null;
		try {
			level = LevelSystem.load(getInstrumentation().getTargetContext(), major, minor);
		} catch (LevelXmlFormatException e) {
			parseException = e;
		} catch (XmlPullParserException e) {
			parseException = e;
		} catch (IOException e) {
			parseException = e;
		}
		
		assertNull("exception while parse level:" + parseException, parseException);
		assertNotNull(level);
		assertEquals(major, level.number.major);
		assertEquals(minor, level.number.minor);
		
		int scoreMin = level.score.min;
		int scoreMed = level.score.med;
		int scoreHigh = level.score.high;
		int scoreMax = level.score.max;
		assertTrue (0 < scoreMin && scoreMin < scoreMed && scoreMed < scoreHigh && scoreHigh < scoreMax);
		
		assertTrue (0 < level.time.total);
		assertTrue (0 < level.time.periods.size());
		
		if (level.fixedTimeAppearingCustomers.isEmpty() == false) {
			for (LevelCustomerConfig spec : level.fixedTimeAppearingCustomers) {
				verifyCustomerSpec(spec);
			}
		}
	}
	
	private void verifyCustomerSpec(LevelCustomerConfig customer) {
		if (customer.appearanceConfig.type == AppearanceConfig.Type.RANDOM) {
			assertTrue(customer.appearanceConfig.weight > 0);
			assertTrue(customer.appearanceConfig.times.isEmpty());
		} else if (customer.appearanceConfig.type == AppearanceConfig.Type.FIXED) {
			assertTrue(customer.appearanceConfig.weight == 0);
			assertFalse(customer.appearanceConfig.times.isEmpty());
		}
	}
	
	public void testLockState() {
		boolean isFullVersion = false;
		for (int major = LevelSystem.MIN_MAJOR_LEVEL; major <= LevelSystem.MAX_MAJOR_LEVEL; major++) {
			for (int minor = LevelSystem.MIN_MINOR_LEVEL; minor <= LevelSystem.MAX_MINOR_LEVEL; minor++) {
				LevelLockState lockState = LevelSystem.getLevelLockState(isFullVersion, major, minor);
				assertTrue("verifyLockState failed: M:" + major + ",m:" + minor + ",state:" + lockState, verifyLockState(major, minor, isFullVersion, lockState));
			}
		}
		
		isFullVersion = true;
		for (int major = LevelSystem.MIN_MAJOR_LEVEL; major <= LevelSystem.MAX_MAJOR_LEVEL; major++) {
			for (int minor = LevelSystem.MIN_MINOR_LEVEL; minor <= LevelSystem.MAX_MINOR_LEVEL; minor++) {
				LevelLockState lockState = LevelSystem.getLevelLockState(isFullVersion, major, minor);
				assertTrue("verifyLockState failed: M:" + major + ",m:" + minor + ",state:" + lockState, verifyLockState(major, minor, isFullVersion, lockState));
			}
		}
	}
	
	private boolean verifyLockState(int major, int minor, boolean isFullVersion, LevelLockState state) {
		if (major == 1 && minor == 1) {
			return state == LevelLockState.UNLOCK;
		}
		
		if (major < Config.MAJOR_LEVEL_START_PURCHASE_LOCK) {
			return state == LevelLockState.SCORE_LOCK;
		} else if ((major == Config.MAJOR_LEVEL_START_PURCHASE_LOCK) && (minor < Config.MINOR_LEVEL_START_PURCHASE_LOCK)) {
			return state == LevelLockState.SCORE_LOCK;
		} else {
			if (isFullVersion) {
				return state == LevelLockState.SCORE_LOCK;
			} else {
				return state == LevelLockState.PURCHASE_LOCK;
			}
		}
	}
}
