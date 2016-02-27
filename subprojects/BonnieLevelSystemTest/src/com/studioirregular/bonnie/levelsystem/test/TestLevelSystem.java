package com.studioirregular.bonnie.levelsystem.test;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.XmlResourceParser;
import android.test.InstrumentationTestCase;

import com.studioirregular.bonnie.foodsystem.FoodSystem.Food;
import com.studioirregular.bonnie.foodsystem.FoodSystem.FoodCombination;
import com.studioirregular.bonnie.levelsystem.Level;
import com.studioirregular.bonnie.levelsystem.Level.Customer;
import com.studioirregular.bonnie.levelsystem.Level.LevelType;
import com.studioirregular.bonnie.levelsystem.Level.Period;
import com.studioirregular.bonnie.levelsystem.Level.WhatsNewDialog;
import com.studioirregular.bonnie.levelsystem.LevelSystem;

public class TestLevelSystem extends InstrumentationTestCase {

	LevelSystem levelSystem;
	
	protected void setUp() throws Exception {
		levelSystem = new LevelSystem();
	}

	protected void tearDown() throws Exception {
		levelSystem = null;
	}
	
	// read a well formatted tag
	public void testLevelTag_NormalCase() {
		XmlResourceParser input = getInstrumentation().getContext().getResources().getXml(R.xml.test_level);
		assertNotNull("input == null", input);
		
		Level level = null;
		try {
			fastForwardToTargetTag(input, Level.TAG_NAME_LEVEL);
			level = levelSystem.parseLevelTag(input);
		} catch (XmlPullParserException e) {
			fail("parseLevelTag exception:" + e);
		} catch (IOException e) {
			fail("parseLevelTag exception:" + e);
		}
		
		assertNotNull(level);
		assertEquals(1, level.level);
		assertEquals(1, level.subLevel);
		assertEquals(LevelType.NORMAL_WITH_TUTORIAL, level.type);
		assertEquals(20 * 1000, level.totalTime);
	}
	
	public void testLevelTag_MissingAttribute_1() {
		XmlResourceParser input = getInstrumentation().getContext().getResources().getXml(R.xml.test_level_1);
		
		Level level = null;
		
		try {
			fastForwardToTargetTag(input, Level.TAG_NAME_LEVEL);
			level = levelSystem.parseLevelTag(input);
		} catch (XmlPullParserException e) {
			fail("parseLevelTag exception:" + e);
		} catch (IOException e) {
			fail("parseLevelTag exception:" + e);
		}
		
		assertNull(level);
	}
	
	public void testLevelTag_MissingAttribute_2() {
		XmlResourceParser input = getInstrumentation().getContext().getResources().getXml(R.xml.test_level_2);
		
		Level level = null;
		
		try {
			fastForwardToTargetTag(input, Level.TAG_NAME_LEVEL);
			level = levelSystem.parseLevelTag(input);
		} catch (XmlPullParserException e) {
			fail("parseLevelTag exception:" + e);
		} catch (IOException e) {
			fail("parseLevelTag exception:" + e);
		}
		
		assertNull(level);
	}
	
	public void testLevelTag_MissingAttribute_3() {
		XmlResourceParser input = getInstrumentation().getContext().getResources().getXml(R.xml.test_level_3);
		
		Level level = null;
		
		try {
			fastForwardToTargetTag(input, Level.TAG_NAME_LEVEL);
			level = levelSystem.parseLevelTag(input);
		} catch (XmlPullParserException e) {
			fail("parseLevelTag exception:" + e);
		} catch (IOException e) {
			fail("parseLevelTag exception:" + e);
		}
		
		assertNull(level);
	}
	
	public void testLevelTag_MissingAttribute_4() {
		XmlResourceParser input = getInstrumentation().getContext().getResources().getXml(R.xml.test_level_4);
		
		Level level = null;
		
		try {
			fastForwardToTargetTag(input, Level.TAG_NAME_LEVEL);
			level = levelSystem.parseLevelTag(input);
		} catch (XmlPullParserException e) {
			fail("parseLevelTag exception:" + e);
		} catch (IOException e) {
			fail("parseLevelTag exception:" + e);
		}
		
		assertNull(level);
	}
	
	// allow unknown attribute
	public void testLevelTag_UnknownAttribute() {
		XmlResourceParser input = getInstrumentation().getContext().getResources().getXml(R.xml.test_level_5);
		
		Level level = null;
		
		try {
			fastForwardToTargetTag(input, Level.TAG_NAME_LEVEL);
			level = levelSystem.parseLevelTag(input);
		} catch (XmlPullParserException e) {
			fail("parseLevelTag exception:" + e);
		} catch (IOException e) {
			fail("parseLevelTag exception:" + e);
		}
		
		assertNotNull(level);
	}
	
	public void testWhatsNewDialogNormalCase() {
		XmlResourceParser input = getInstrumentation().getContext().getResources().getXml(R.xml.test_whatsnew);
		try {
			fastForwardToTargetTag(input, Level.TAG_NAME_WHATSNEWDIALOG);
		} catch (XmlPullParserException e) {
			fail("fastForwardToTargetTag raise exception:" + e);
		} catch (IOException e) {
			fail("fastForwardToTargetTag raise exception:" + e);
		}
		
		assertEquals(Level.TAG_NAME_WHATSNEWDIALOG, input.getName());
		
		WhatsNewDialog result = null;
		try {
			result = levelSystem.parseWhatsNewDialog(input);
		} catch (XmlPullParserException e) {
			fail("raise exception:" + e);
		} catch (IOException e) {
			fail("raise exception:" + e);
		}
		
		assertNotNull(result);
		assertEquals("resource_1", result.drawableResourceName);
	}
	
	public void testTableConfig_NormalCase() {
		XmlResourceParser input = getInstrumentation().getContext().getResources().getXml(R.xml.test_table_config);
		
		try {
			fastForwardToTargetTag(input, Level.TAG_NAME_TABLE_ITEM_CONFIG);
		} catch (XmlPullParserException e) {
			fail("fastForwardToTargetTag raise exception:" + e);
		} catch (IOException e) {
			fail("fastForwardToTargetTag raise exception:" + e);
		}
		
		assertEquals(Level.TAG_NAME_TABLE_ITEM_CONFIG, input.getName());
		
		Level level = new Level();
		
		try {
			levelSystem.parseTableConfig(input, level);
		} catch (XmlPullParserException e) {
			fail("parseTableConfigTag raise exception:" + e);
		} catch (IOException e) {
			fail("parseTableConfigTag raise exception:" + e);
		}
		
		int correctTableConfig = Level.TABLE_ITEM_BAGEL
				| Level.TABLE_ITEM_CROISSANT | Level.TABLE_ITEM_SAUCE_BUTTER
				| Level.TABLE_ITEM_SAUCE_HONEY | Level.TABLE_ITEM_SAUCE_TOMATO
				| Level.TABLE_ITEM_LETTUCE | Level.TABLE_ITEM_CHEESE
				| Level.TABLE_ITEM_MUFFIN_LV2 | Level.TABLE_ITEM_TOAST_LV3
				| Level.TABLE_ITEM_PAN_LV3 | Level.TABLE_ITEM_MILK
				| Level.TABLE_ITEM_COFFEE | Level.TABLE_ITEM_CANDY_LV1
				| Level.TABLE_ITEM_TRASHCAN;
		assertEquals(correctTableConfig, level.tableConfig);
		
		assertTrue((correctTableConfig & Level.TABLE_ITEM_MASK_MUFFIN) != 0);
		assertTrue((correctTableConfig & Level.TABLE_ITEM_MASK_TOAST) != 0);
		assertTrue((correctTableConfig & Level.TABLE_ITEM_MASK_PAN) != 0);
		assertTrue((correctTableConfig & Level.TABLE_ITEM_MASK_CANDY) != 0);
		
		// check if input stay in next tag after table item config tag.
		try {
			int event = input.next();
			assertEquals(event, XmlPullParser.START_TAG);
			assertEquals("Oops, parsing function eat up next tag after target tag.", "Dummy", input.getName());
		} catch (XmlPullParserException e) {
			fail("Oops, parsing function eat up next tag after target tag.");
		} catch (IOException e) {
			fail("Oops, parsing function eat up next tag after target tag.");
		}
	}
	
	public void testPeriod_NormalCase() {
		XmlResourceParser input = getInstrumentation().getContext().getResources().getXml(R.xml.test_period);
		
		Period period = null;
		try {
			fastForwardToTargetTag(input, Level.TAG_NAME_PERIOD);
			period = levelSystem.parsePeriodTag(input);
		} catch (XmlPullParserException e) {
			fail("parseCustomerTag raise exception:" + e);
		} catch (IOException e) {
			fail("parseCustomerTag raise exception:" + e);
		}
		
		assertNotNull(period);
		assertEquals(0, period.startFrom);
		assertEquals(5000, period.minCustomerInterval);
		assertEquals(6000, period.maxCustomerInterval);
	}
	
	public void testCustomerType() {
		XmlResourceParser input = getInstrumentation().getContext().getResources().getXml(R.xml.test_customer_type);
		
		Customer customer = null;
		try {
			fastForwardToTargetTag(input, Level.TAG_NAME_CUSTOMER);
			customer = levelSystem.parseCustomer(input);
			assertEquals(Level.CUSTOMER_JOGGING_GIRL, customer.customerType);
			
			fastForwardToTargetTag(input, Level.TAG_NAME_CUSTOMER);
			customer = levelSystem.parseCustomer(input);
			assertEquals(Level.CUSTOMER_WORKING_MAN, customer.customerType);
			
			fastForwardToTargetTag(input, Level.TAG_NAME_CUSTOMER);
			customer = levelSystem.parseCustomer(input);
			assertEquals(Level.CUSTOMER_BALLOON_BOY, customer.customerType);
			
			fastForwardToTargetTag(input, Level.TAG_NAME_CUSTOMER);
			customer = levelSystem.parseCustomer(input);
			assertEquals(Level.CUSTOMER_GIRL_WITH_DOG, customer.customerType);
			
			fastForwardToTargetTag(input, Level.TAG_NAME_CUSTOMER);
			customer = levelSystem.parseCustomer(input);
			assertEquals(Level.CUSTOMER_FOOD_CRITIC, customer.customerType);
			
			fastForwardToTargetTag(input, Level.TAG_NAME_CUSTOMER);
			customer = levelSystem.parseCustomer(input);
			assertEquals(Level.CUSTOMER_SUPERSTAR_LADY, customer.customerType);
			
			fastForwardToTargetTag(input, Level.TAG_NAME_CUSTOMER);
			customer = levelSystem.parseCustomer(input);
			assertEquals(Level.CUSTOMER_PHYSICIST, customer.customerType);
			
			fastForwardToTargetTag(input, Level.TAG_NAME_CUSTOMER);
			customer = levelSystem.parseCustomer(input);
			assertEquals(Level.CUSTOMER_TRAMP, customer.customerType);
			
			fastForwardToTargetTag(input, Level.TAG_NAME_CUSTOMER);
			customer = levelSystem.parseCustomer(input);
			assertEquals(Level.CUSTOMER_GRANNY, customer.customerType);
			
			fastForwardToTargetTag(input, Level.TAG_NAME_CUSTOMER);
			customer = levelSystem.parseCustomer(input);
			assertEquals(Level.CUSTOMER_SUPERSTAR_MAN, customer.customerType);
		} catch (XmlPullParserException e) {
			fail("parseCustomerTag raise exception:" + e);
		} catch (IOException e) {
			fail("parseCustomerTag raise exception:" + e);
		}
	}
	
	public void testCustomerFood_NormalCase() {
		XmlResourceParser input = getInstrumentation().getContext().getResources().getXml(R.xml.test_customer_food);
		
		Customer customer = null;
		try {
			fastForwardToTargetTag(input, Level.TAG_NAME_CUSTOMER);
			customer = levelSystem.parseCustomer(input);
		} catch (XmlPullParserException e) {
			fail("parseCustomerTag raise exception:" + e);
		} catch (IOException e) {
			fail("parseCustomerTag raise exception:" + e);
		}
		
		assertNotNull(customer);
		assertEquals(Level.CUSTOMER_GIRL_WITH_DOG, customer.customerType);
		
		FoodCombination combination = customer.prefferedFood;
		assertNotNull(combination);
		assertTrue(combination.contains(Food.getBagel()));
		assertTrue(combination.contains(Food.getCroissant()));
		assertTrue(combination.contains(Food.getMuffinCircle()));
		assertTrue(combination.contains(Food.getMuffinSquare()));
		assertTrue(combination.contains(Food.getToastWhite()));
		assertTrue(combination.contains(Food.getToastBlack()));
		assertTrue(combination.contains(Food.getToastYellow()));
		assertTrue(combination.contains(Food.getEgg()));
		assertTrue(combination.contains(Food.getHam()));
		assertTrue(combination.contains(Food.getSausage()));
		assertTrue(combination.contains(Food.getButter()));
		assertTrue(combination.contains(Food.getHoney()));
		assertTrue(combination.contains(Food.getTomato()));
		assertTrue(combination.contains(Food.getLettuce()));
		assertTrue(combination.contains(Food.getCheese()));
		assertTrue(combination.contains(Food.getMilk()));
		assertTrue(combination.contains(Food.getCoffee()));
		
		// check if input stay in next tag after table item config tag.
		try {
			int event = input.next();
			assertEquals(event, XmlPullParser.START_TAG);
			assertEquals("Oops, parsing function eat up next tag after target tag.", "Dummy", input.getName());
		} catch (XmlPullParserException e) {
			fail("Oops, parsing function eat up next tag after target tag.");
		} catch (IOException e) {
			fail("Oops, parsing function eat up next tag after target tag.");
		}
	}
	
	public void testAll_NormalCase() {
		XmlResourceParser input = getInstrumentation().getContext().getResources().getXml(R.xml.test_level_all);
		
		Level level = null;
		try {
			level = levelSystem.parse(input);
		} catch (XmlPullParserException e) {
			fail("parse raise exception:" + e);
		} catch (IOException e) {
			fail("parse raise exception:" + e);
		}
		
		assertNotNull(level);
		assertEquals(1, level.level);
		assertEquals(3, level.subLevel);
		assertEquals(LevelType.NORMAL, level.type);
		assertEquals(40000, level.totalTime);
		
		int tableConfig = level.tableConfig;
		assertEquals(Level.TABLE_ITEM_BAGEL, tableConfig & Level.TABLE_ITEM_BAGEL);
		assertEquals(Level.TABLE_ITEM_CROISSANT, tableConfig & Level.TABLE_ITEM_CROISSANT);
		assertEquals(Level.TABLE_ITEM_SAUCE_BUTTER, tableConfig & Level.TABLE_ITEM_SAUCE_BUTTER);
		assertEquals(Level.TABLE_ITEM_SAUCE_HONEY, tableConfig & Level.TABLE_ITEM_SAUCE_HONEY);
		assertEquals(Level.TABLE_ITEM_TRASHCAN, tableConfig & Level.TABLE_ITEM_TRASHCAN);
		
		Period[] periods = level.periods;
		assertEquals(3, periods.length);
		
		assertEquals(0, periods[0].startFrom);
		assertEquals(5000, periods[0].minCustomerInterval);
		assertEquals(6000, periods[0].maxCustomerInterval);
		
		assertEquals(15000, periods[1].startFrom);
		assertEquals(3000, periods[1].minCustomerInterval);
		assertEquals(4000, periods[1].maxCustomerInterval);
		
		assertEquals(30000, periods[2].startFrom);
		assertEquals(5000, periods[2].minCustomerInterval);
		assertEquals(6000, periods[2].maxCustomerInterval);
		
		Customer[] customers = level.customers;
		assertEquals(3, customers.length);
		
		Customer joggingGirl = customers[0];
		assertEquals(Level.CUSTOMER_JOGGING_GIRL, joggingGirl.customerType);
		
		FoodCombination foodCombination = joggingGirl.prefferedFood;
		assertTrue(foodCombination.contains(Food.getBagel()));
		assertTrue(foodCombination.contains(Food.getButter()));
		
		Customer balloonBoy = customers[1];
		assertEquals(Level.CUSTOMER_BALLOON_BOY, balloonBoy.customerType);
		
		foodCombination = balloonBoy.prefferedFood;
		assertTrue(foodCombination.contains(Food.getBagel()));
		assertTrue(foodCombination.contains(Food.getCroissant()));
		assertTrue(foodCombination.contains(Food.getButter()));
		
		Customer workingMan = customers[2];
		assertEquals(Level.CUSTOMER_WORKING_MAN, workingMan.customerType);
		
		foodCombination = workingMan.prefferedFood;
		assertTrue(foodCombination.contains(Food.getCroissant()));
		assertTrue(foodCombination.contains(Food.getHoney()));
	}
	
	private boolean fastForwardToTargetTag(XmlResourceParser input, String tagName) throws XmlPullParserException, IOException {
		int event = input.next();
		while (event != XmlPullParser.END_DOCUMENT) {
			if (event == XmlPullParser.START_TAG) {
				if (tagName.equals(input.getName())) {
					return true;
				}
			}
			event = input.next();
		}
		return false;
	}

}
