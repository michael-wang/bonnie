package com.studioirregular.bonniesbrunch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.studioirregular.bonniesbrunch.FoodSystem.Brunch;
import com.studioirregular.bonniesbrunch.FoodSystem.Food;
import com.studioirregular.bonniesbrunch.FoodSystem.FoodSet;
import com.studioirregular.bonniesbrunch.Game.StoredPurchaseState;
import com.studioirregular.bonniesbrunch.entity.Customer;

public class LevelSystem {

	private static final String TAG = "level-system";
	
	public static final int MIN_MAJOR_LEVEL = 1;
	public static final int MAX_MAJOR_LEVEL = 5;
	public static final int MIN_MINOR_LEVEL = 1;
	public static final int MAX_MINOR_LEVEL = 10;
	
	public static class GameLevel {
		public LevelNumber number = new LevelNumber(0, 0);
		public boolean specialLevel = false;
		public boolean hasTutorial = false;
		public String tempTextureResource;
		public LevelScore score = new LevelScore();
		public LevelTime time = new LevelTime();
		public TableConfig tableConfig = new TableConfig();
		public List<LevelCustomerConfig> randomAppearingCustomers = new ArrayList<LevelCustomerConfig>();
		public List<LevelCustomerConfig> fixedTimeAppearingCustomers = new ArrayList<LevelCustomerConfig>();
		public WhatsNew whatsNew;
		public LevelLockState lockState = LevelLockState.PURCHASE_LOCK;
		
		static final String XML_TAG = "Level";
		static final String XML_ATTR_MAJOR_LEVEL = "major";		// int
		static final String XML_ATTR_MINOR_LEVEL = "minor";		// int
		static final String XML_ATTR_SPECIAL_LEVEL = "special_level";	// [optional] boolean
		static final String XML_ATTR_HAS_TUTORIAL = "has_tutorial";		// [optional] boolean
		static final String XML_ATTR_TEMP_TEXTURE = "temp_texture";		// [optional] String
	}
	
	public static class LevelNumber {
		private static final int GAME_OVER_MAJOR = MAX_MAJOR_LEVEL + 1;
		private static final int GAME_OVER_MINOR = MAX_MINOR_LEVEL + 1;
		
		public int major = 0;
		public int minor = 0;
		
		public LevelNumber() {
		}
		
		public LevelNumber(int major, int minor) {
			this.major = major;
			this.minor = minor;
		}
		
		public LevelNumber(LevelNumber other) {
			major = other.major;
			minor = other.minor;
		}
		
		public boolean set(int major, int minor) {
			if (GAME_OVER_MAJOR == major && GAME_OVER_MINOR == minor) {
				this.major = GAME_OVER_MAJOR;
				this.minor = GAME_OVER_MINOR;
				return true;
			}
			
			if (MIN_MAJOR_LEVEL <= major && major <= MAX_MAJOR_LEVEL &&
					MIN_MINOR_LEVEL <= minor && minor <= MAX_MINOR_LEVEL) {
				this.major = major;
				this.minor = minor;
				return true;
			}
			return false;
		}
		
		public boolean isGameOver() {
			return (major == GAME_OVER_MAJOR) && (minor == GAME_OVER_MINOR);
		}
		
		public LevelNumber getNextLevel() {
			LevelNumber nextLevel = new LevelNumber();
			
			if (minor == MAX_MINOR_LEVEL && major == MAX_MAJOR_LEVEL) {
				nextLevel.set(GAME_OVER_MAJOR, GAME_OVER_MINOR);
				return nextLevel;
			}
			
			if (MAX_MINOR_LEVEL == minor) {
				nextLevel.set(major + 1, MIN_MINOR_LEVEL);
				return nextLevel;
			}
			
			nextLevel.set(major, minor + 1);
			return nextLevel;
		}
		
		public boolean previousLevel() {
			if (minor <= MIN_MINOR_LEVEL && major <= MIN_MAJOR_LEVEL) {
				return false;
			}
			
			if (MIN_MINOR_LEVEL == minor) {
				major--;
				minor = MAX_MINOR_LEVEL;
			} else {
				minor--;
			}
			return true;
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			
			if (!(o instanceof LevelNumber)) {
				return false;
			}
			
			LevelNumber lhs = (LevelNumber)o;
			return (this.major == lhs.major) &&
					(this.minor == lhs.minor);
		}
		
		@Override
		public int hashCode() {
			int result = 17;
			
			result = 31 * result + major;
			result = 31 * result + minor;
			return result;
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + ": " + major + "-" + minor;
		}
		
	}
	
	public static class LevelScore {
		public int min;
		public int med;
		public int high;
		public int max;
		
		public void clear() {
			min = 0;
			med = 0;
			high = 0;
			max = 0;
		}
		
		@Override
		public String toString() {
			return "LevelScore min:" + min + ",med:" + med + ",high:" + high + ",max:" + max;
		}
		
		static final String XML_TAG = "Score";
		static final String XML_ATTR_MIN = "min";
		static final String XML_ATTR_MED = "med";
		static final String XML_ATTR_HIGH = "high";
		static final String XML_ATTR_MAX = "max";
	}
	
	public static class LevelTime {
		public int total;	// seconds
		public List<LevelTimePeriod> periods = new ArrayList<LevelTimePeriod>();
		public List<RushHour> rushHours = new ArrayList<RushHour>();
		
		static final String XML_TAG = "Time";
		static final String XML_ATTR_TOTAL = "total";
	}
	
	public static class LevelTimePeriod {
		public int start;	// seconds
		public int minCustomerInterval;	// seconds
		public int maxCustomerInterval;	// seconds
		
		static final String XML_TAG = "Period";
		static final String XML_ATTR_START = "start";
		static final String XML_ATTR_MIN = "min";
		static final String XML_ATTR_MAX = "max";
	}
	
	public static class RushHour {
		public int rushClockIndex;	// 1 - 12: used for textures: game_time_rush_xx
		
		static final String XML_TAG = "RushHour";
		static final String XML_ATTR_CLOCK_INDEX = "clock_index";
	}
	
	public static class TableConfig {
		public List<TableItem> items = new ArrayList<TableItem>();
		
		static String XML_TAG = "Table";
	}
	
	public static class TableItem {
		public static final int BAGEL			= 1;
		public static final int CROISSANT		= 2;
		public static final int LETTUCE			= 3;
		public static final int CHEESE			= 4;
		public static final int BUTTER			= 5;
		public static final int HONEY			= 6;
		public static final int TOMATO			= 7;
		public static final int MILK			= 8;
		public static final int COFFEE			= 9;
		
		public static final int MUFFIN_MACHINE	= 10;
		public static final int TOAST_MACHINE	= 11;
		public static final int FRYING_PAN		= 12;
		public static final int CANDY_MACHINE	= 13;
		
		public static final int PLATE			= 14;
		public static final int TRASHCAN		= 15;
		
		public static final int BRUNCH			= 16;
		public static final int TOAST_TOP		= 17;
		
		public int type;
		
		static final String XML_TAG = "Item";
		static final String XML_ATTR_TYPE = "type";
		static Map<String, Integer> ATTR_NAME_2_TYPE = new HashMap<String, Integer>();
		static {
			ATTR_NAME_2_TYPE.put("Bagel",			BAGEL);
			ATTR_NAME_2_TYPE.put("Croissant",		CROISSANT);
			ATTR_NAME_2_TYPE.put("Lettuce",			LETTUCE);
			ATTR_NAME_2_TYPE.put("Cheese",			CHEESE);
			ATTR_NAME_2_TYPE.put("Butter",			BUTTER);
			ATTR_NAME_2_TYPE.put("Honey",			HONEY);
			ATTR_NAME_2_TYPE.put("Tomato",			TOMATO);
			ATTR_NAME_2_TYPE.put("Milk",			MILK);
			ATTR_NAME_2_TYPE.put("Coffee",			COFFEE);
			ATTR_NAME_2_TYPE.put("MuffinMachine",	MUFFIN_MACHINE);
			ATTR_NAME_2_TYPE.put("ToastMachine",	TOAST_MACHINE);
			ATTR_NAME_2_TYPE.put("FryingPan",		FRYING_PAN);
			ATTR_NAME_2_TYPE.put("CandyMachine",	CANDY_MACHINE);
			
			ATTR_NAME_2_TYPE.put("Plate",			PLATE);
			ATTR_NAME_2_TYPE.put("Trashcan",		TRASHCAN);
		}
	}
	
	public static class TableLevelItem extends TableItem {
		public int level;
		
		static final String XML_TAG = "LevelItem";
		static final String XML_ATTR_LEVEL = "level";
	}
	
	// Appears at fixed-predefined time.
	// There are two types of ordering behavior:
	// 1. randomly pick from preferredFoods.
	// 2. order fixed brunch (in this case preferredFoods is brunch).
	public static class LevelCustomerConfig {
		public Customer.Type type = Customer.Type.INVALID_TYPE;
		public FoodSet optionalSet = new FoodSet();
		public Brunch mustOrder = new Brunch();
		public AppearanceConfig appearanceConfig;
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + " type:" + type + ",mustOrder:" + mustOrder + ",optionalSet:" + optionalSet + ", appearanceConfig:" + appearanceConfig;
		}
		
		static final String XML_TAG = "Customer";
		static final String XML_ATTR_TYPE = "type";
		static final String XML_TAG_ORDER_SPEC = "OrderSpec";
		static final String XML_ATTR_MUST_ORDER_NAME = "must_order";	// optional, for FOOD_TAG
		
		static final Map<String, Customer.Type> STRING_2_CUSTOMER_TYPE = new HashMap<String, Customer.Type>();
		static Map<String, Integer> FOOD_TAG_2_FOOD_TYPE = new HashMap<String, Integer>();
		static {
			STRING_2_CUSTOMER_TYPE.put("JoggingGirl",	Customer.Type.JOGGING_GIRL);
			STRING_2_CUSTOMER_TYPE.put("WorkingMan",	Customer.Type.WORKING_MAN);
			STRING_2_CUSTOMER_TYPE.put("BalloonBoy",	Customer.Type.BALLOON_BOY);
			STRING_2_CUSTOMER_TYPE.put("GirlWithDog",	Customer.Type.GIRL_WITH_DOG);
			STRING_2_CUSTOMER_TYPE.put("FoodCritic",	Customer.Type.FOOD_CRITIC);
			STRING_2_CUSTOMER_TYPE.put("SuperLady",		Customer.Type.SUPERSTAR_LADY);
			STRING_2_CUSTOMER_TYPE.put("Physicist",		Customer.Type.PHYSICIST);
			STRING_2_CUSTOMER_TYPE.put("Tramp",			Customer.Type.TRAMP);
			STRING_2_CUSTOMER_TYPE.put("Granny",		Customer.Type.GRANNY);
			STRING_2_CUSTOMER_TYPE.put("SuperMan",		Customer.Type.SUPERSTAR_MAN);
			
			FOOD_TAG_2_FOOD_TYPE.put("Bagel",			Food.BAGEL);
			FOOD_TAG_2_FOOD_TYPE.put("Croissant",		Food.CROISSANT);
			FOOD_TAG_2_FOOD_TYPE.put("MuffinCircle",	Food.MUFFIN_CIRCLE);
			FOOD_TAG_2_FOOD_TYPE.put("MuffinSquare",	Food.MUFFIN_SQUARE);
			FOOD_TAG_2_FOOD_TYPE.put("ToastWhite",		Food.TOAST_WHITE);
			FOOD_TAG_2_FOOD_TYPE.put("ToastBlack",		Food.TOAST_BLACK);
			FOOD_TAG_2_FOOD_TYPE.put("ToastYellow",		Food.TOAST_YELLOW);
			FOOD_TAG_2_FOOD_TYPE.put("Lettuce",			Food.LETTUCE);
			FOOD_TAG_2_FOOD_TYPE.put("Cheese",			Food.CHEESE);
			FOOD_TAG_2_FOOD_TYPE.put("Egg",				Food.EGG);
			FOOD_TAG_2_FOOD_TYPE.put("Ham",				Food.HAM);
			FOOD_TAG_2_FOOD_TYPE.put("Hotdog",			Food.HOTDOG);
			FOOD_TAG_2_FOOD_TYPE.put("Butter",			Food.BUTTER);
			FOOD_TAG_2_FOOD_TYPE.put("Honey",			Food.HONEY);
			FOOD_TAG_2_FOOD_TYPE.put("Tomato",			Food.TOMATO);
			FOOD_TAG_2_FOOD_TYPE.put("Milk",			Food.MILK);
			FOOD_TAG_2_FOOD_TYPE.put("Coffee",			Food.COFFEE);
		}
	}
	
	public static class AppearanceConfig {
		public static enum Type {
			RANDOM,
			FIXED
		}
		
		public AppearanceConfig(Type type) {
			this.type = type;
		}
		
		public Type type;
		public int weight;
		public List<Integer> times = new ArrayList<Integer>();	// in seconds
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + " type:" + type + ", weight:" + weight + ", #times:" + times.size();
		}
		
		static final String XML_TAG = "AppearanceConfig";
		static final String XML_ATTR_TYPE = "type";
		static final String XML_ATTR_TYPE_VALUE_RANDOM = "random";
		static final String XML_ATTR_TYPE_VALUE_FIXED = "fixed";
		static final String XML_TAG_WEIGHT = "Weight";
		static final String XML_ATTR_WEIGHT_VALUE = "value";
		static final String XML_TAG_TIME = "Time";
		static final String XML_ATTR_TIME_VALUE = "value";
	}
	
	public static class WhatsNew {
		public List<String> partitionList = new ArrayList<String>();
		
		static final String XML_TAG = "WhatsNew";
		static final String XML_ITEM_TAG = "Item";
		static final String XML_ATTR_PARTITION = "partition";
	}
	
	public static enum LevelLockState {
		SCORE_LOCK,
		PURCHASE_LOCK,
		UNLOCK
	}
	
	@SuppressWarnings("serial")
	public static class LevelXmlFormatException extends Exception {
		public LevelXmlFormatException(String msg) {
			super(msg);
		}
	}
	
	public static LevelLockState getMajorLevelLockState(int major) {
		if (Config.DEBUG_LOG) Log.d(TAG, "getMajorLevelLockState major:" + major);
		assert MIN_MAJOR_LEVEL <= major && major <= MAX_MAJOR_LEVEL;
		
		if (Config.DEBUG_UNLOCK_EVERYTHING) {
			return LevelLockState.UNLOCK;
		} else if (Config.DEBUG_UNLOCK_FREE_LEVEL && major <= Config.MAJOR_LEVEL_START_PURCHASE_LOCK) {
			return LevelLockState.UNLOCK;
		}
		
		final boolean disableScoreLock = ContextParameters.getInstance().disableLevelScoreLock;
		
		// for Config.MAJOR_LEVEL_START_PURCHASE_LOCK minor levels which are not purchase locked.
		if (major <= Config.MAJOR_LEVEL_START_PURCHASE_LOCK) {
			if (isScoreLocked(major, 1) && !disableScoreLock) {
				return LevelLockState.SCORE_LOCK;
			} else {
				return LevelLockState.UNLOCK;
			}
		} else {
			if (isMajorLevelPurchased(major)) {
				if (isScoreLocked(major, 1) && !disableScoreLock) {
					return LevelLockState.SCORE_LOCK;
				} else {
					return LevelLockState.UNLOCK;
				}
			} else {
				return LevelLockState.PURCHASE_LOCK;
			}
		}
	}
	
	private static boolean isMajorLevelPurchased(int major) {
		return Game.getInstance().getFullVersionPurchaseState().equals(StoredPurchaseState.PURCHASED);
	}
	
	public static boolean getLevelLockStates(int major, LevelLockState[] states) {
		assert MIN_MAJOR_LEVEL <= major && major <= MAX_MAJOR_LEVEL;
		assert states.length == 10;
		
		final boolean isFullVersion = Game.getInstance().getFullVersionPurchaseState().equals(StoredPurchaseState.PURCHASED);
		for (int i = 0; i < MAX_MINOR_LEVEL; i++) {
			final int minor = i + 1;
			states[i] = getLevelLockState(isFullVersion, major, minor);
		}
		
		return true;
	}
	
	public static LevelLockState getLevelLockState(int major, int minor) {
		final boolean isFullVersion = Game.getInstance().getFullVersionPurchaseState().equals(StoredPurchaseState.PURCHASED); 
		return getLevelLockState(isFullVersion, major, minor);
	}
	
	// The rule is: check purchase locked first, then score lock.
	public static LevelLockState getLevelLockState(final boolean isFullVersion, int major, int minor) {
		if (Config.DEBUG_UNLOCK_EVERYTHING) {
			return LevelLockState.UNLOCK;
		} else if (Config.DEBUG_UNLOCK_FREE_LEVEL) {
			if (major < Config.MAJOR_LEVEL_START_PURCHASE_LOCK) {
				return LevelLockState.UNLOCK;
			} else if (major == Config.MAJOR_LEVEL_START_PURCHASE_LOCK && minor < Config.MINOR_LEVEL_START_PURCHASE_LOCK) {
				return LevelLockState.UNLOCK;
			}
		}
		
		final boolean disableScoreLock = ContextParameters.getInstance().disableLevelScoreLock;
		
		if (major < Config.MAJOR_LEVEL_START_PURCHASE_LOCK) {
			if (isScoreLocked(major, minor) && !disableScoreLock) {
				return LevelLockState.SCORE_LOCK;
			} else {
				return LevelLockState.UNLOCK;
			}
		} else if ((Config.MAJOR_LEVEL_START_PURCHASE_LOCK == major) && (minor < Config.MINOR_LEVEL_START_PURCHASE_LOCK)) {
			if (isScoreLocked(major, minor) && !disableScoreLock) {
				return LevelLockState.SCORE_LOCK;
			} else {
				return LevelLockState.UNLOCK;
			}
		} else {
			// Config.MAJOR_LEVEL_START_PURCHASE_LOCK == major && Config.MINOR_LEVEL_START_PURCHASE_LOCK <= minor
			// or
			// Config.MAJOR_LEVEL_START_PURCHASE_LOCK < major.
			if (!isFullVersion) {
				return LevelLockState.PURCHASE_LOCK;
			} else {
				if (isScoreLocked(major, minor) && !disableScoreLock) {
					return LevelLockState.SCORE_LOCK;
				} else {
					return LevelLockState.UNLOCK;
				}
			}
		}
	}
	
	private static boolean isScoreLocked(int major, int minor) {
		if (ContextParameters.getInstance().disableLevelScoreLock) {
			return false;
		}
		
		if (MIN_MAJOR_LEVEL == major && MIN_MINOR_LEVEL == minor) {
			return false;
		}
		
		LevelNumber level = new LevelNumber(major, minor);
		if (level.previousLevel() == false) {
			Log.e(TAG, "getScoreLockState cannot find previous level for:" + major + "-" + minor);
			return true;	// assume lock
		}
		
		final int starCount = GameScoreSystem.getInstance().getMinorLevelStar(level);
		if (Config.DEBUG_LOG) Log.w(TAG, "isScoreLocked level:" + level + ", starCount:" + starCount);
		if (Config.STAR_COUNT_TO_UNLOCK_LEVEL <= starCount) {
			return false;
		}
		return true;
	}
	
	public static GameLevel load(Context context, int major, int minor)
			throws LevelXmlFormatException, XmlPullParserException, IOException {
		LevelParser parser = new LevelParser();
		GameLevel result = parser.parse(context, major, minor);
		return result;
	}
	
	private static class LevelParser {
		public LevelParser() {
		}
		
		public GameLevel parse(Context context, int majorLevel, int minorLevel) throws LevelXmlFormatException,
				XmlPullParserException, IOException {
			targetMajorLevel = majorLevel;
			targetMinorLevel = minorLevel;
			targetLevelFound = false;
			
			result = new GameLevel();
			
			XmlResourceParser xml = context.getResources().getXml(LEVEL_XML_RESOURCE);
			int parserEvent = xml.getEventType();
			while (!finished && parserEvent != XmlPullParser.END_DOCUMENT) {
				switch (parserEvent) {
				case XmlPullParser.START_TAG:
					onStartTag(xml);
					break;
				case XmlPullParser.END_TAG:
					onEndTag(xml);
					break;
				}
				parserEvent = xml.next();
			}
			xml.close();
			
			if (!targetLevelFound) {
				throw new LevelXmlFormatException("cannot find target level major:" + targetMajorLevel + ",minor:" + targetMinorLevel);
			}
			return result;
		}
		
		private void onStartTag(XmlResourceParser xml) throws LevelXmlFormatException {
			if (!inTargetLevel) {
				if (GameLevel.XML_TAG.equals(xml.getName())) {
					parseLevelTag(xml);
				}
				return;
			}
			
			if (inTimeTag) {
				handleTagWithinTime(xml);
			} else if (inTableTag) {
				handleTagInTable(xml);
			} else if (inCustomerConfig) {
				final String tagName = xml.getName();
				if (inCustomerConfigOrderSpec) {
					handleTagInOrderSpec(xml);
				} else if (inCustomerConfigAppearanceSpec) {
					handleTagInAppearanceSpec(xml);
				} else if (LevelCustomerConfig.XML_TAG_ORDER_SPEC.equals(tagName)) {
					parseOrderSpec(xml);
				} else if (AppearanceConfig.XML_TAG.equals(tagName)) {
					parseAppearanceConfig(xml);
				}
			} else if (inWhatsNew) {
				handleTagInWhatsNew(xml);
			} else {
				final String tagName = xml.getName();
				if (LevelTime.XML_TAG.equals(tagName)) {
					parseTimeTag(xml);
				} else if (LevelScore.XML_TAG.equals(tagName)) {
					parseScoreTag(xml);
				} else if (TableConfig.XML_TAG.equals(tagName)) {
					parseTableTag(xml);
				} else if (LevelCustomerConfig.XML_TAG.equals(tagName)) {
					parseCustomerTag(xml);
				} else if (WhatsNew.XML_TAG.equals(tagName)) {
					parseWhatsNewTag(xml);
				}
			}
		}
		
		private void onEndTag(XmlResourceParser xml) {
			if (inTargetLevel) {
				final String tagName = xml.getName();
				if (GameLevel.XML_TAG.equals(tagName)) {
					finished = true;
					inTargetLevel = false;
				} else if (LevelTime.XML_TAG.equals(tagName)) {
					inTimeTag = false;
				} else if (TableConfig.XML_TAG.equals(tagName)) {
					inTableTag = false;
				} else if (LevelCustomerConfig.XML_TAG.equals(tagName)) {
					inCustomerConfig = false;
					if (customerConfig.appearanceConfig.type == AppearanceConfig.Type.RANDOM) {
						result.randomAppearingCustomers.add(customerConfig);
					} else if (customerConfig.appearanceConfig.type == AppearanceConfig.Type.FIXED) {
						result.fixedTimeAppearingCustomers.add(customerConfig);
					}
					customerConfig = null;
				} else if (LevelCustomerConfig.XML_TAG_ORDER_SPEC.equals(tagName)) {
					inCustomerConfigOrderSpec = false;
				} else if (AppearanceConfig.XML_TAG.equals(tagName)) {
					inCustomerConfigAppearanceSpec = false;
				} else if (WhatsNew.XML_TAG.equals(tagName)) {
					inWhatsNew = false;
				}
			}
		}
		
		private void parseLevelTag(XmlResourceParser xml) {
			int level = 0;
			int subLevel = 0;
			boolean specialLevel = false;
			boolean hasTutorial = false;
			String tempTexture = "";
			
			final int count = xml.getAttributeCount();
			for (int i = 0; i < count; i++) {
				final String attrName = xml.getAttributeName(i);
				if (GameLevel.XML_ATTR_MAJOR_LEVEL.equals(attrName)) {
					level = xml.getAttributeIntValue(i, 0);
				} else if (GameLevel.XML_ATTR_MINOR_LEVEL.equals(attrName)) {
					subLevel = xml.getAttributeIntValue(i, 0);
				} else if (GameLevel.XML_ATTR_SPECIAL_LEVEL.equals(attrName)) {
					specialLevel = xml.getAttributeBooleanValue(i, false);
				} else if (GameLevel.XML_ATTR_HAS_TUTORIAL.equals(attrName)) {
					hasTutorial = xml.getAttributeBooleanValue(i, false);
				} else if (GameLevel.XML_ATTR_TEMP_TEXTURE.equals(attrName)) {
					tempTexture = xml.getAttributeValue(i);
				}
			}
			
			if (targetMajorLevel == level && targetMinorLevel == subLevel) {
				inTargetLevel = true;
				targetLevelFound = true;
				result.number.set(level, subLevel);
				result.specialLevel = specialLevel;
				result.hasTutorial = hasTutorial;
				result.tempTextureResource = tempTexture;
			}
		}
		
		private void parseScoreTag(XmlResourceParser xml) {
			final int count = xml.getAttributeCount();
			LevelScore score = result.score;
			for (int i = 0; i < count; i++) {
				String attrName = xml.getAttributeName(i);
				if (LevelScore.XML_ATTR_MIN.equals(attrName)) {
					score.min = xml.getAttributeIntValue(i, 0);
				} else if (LevelScore.XML_ATTR_MED.equals(attrName)) {
					score.med = xml.getAttributeIntValue(i, 0);
				} else if (LevelScore.XML_ATTR_HIGH.equals(attrName)) {
					score.high = xml.getAttributeIntValue(i, 0);
				} else if (LevelScore.XML_ATTR_MAX.equals(attrName)) {
					score.max = xml.getAttributeIntValue(i, 0);
				}
				
				if (Config.DEBUG_SUPER_PASSPORT) {
					score.min = 0;
				}
			}
		}
		
		private void parseTimeTag(XmlResourceParser xml) {
			assert xml.getAttributeCount() == 1;
			result.time.total = xml.getAttributeIntValue(0, 0);
			inTimeTag = true;
			
			if (Config.DEBUG_SUPER_PASSPORT) {
				result.time.total = 4;
			}
		}
		
		private void handleTagWithinTime(XmlResourceParser xml) {
			final String tagName = xml.getName();
			if (LevelTimePeriod.XML_TAG.equals(tagName)) {
				LevelTimePeriod period = new LevelTimePeriod();
				final int count = xml.getAttributeCount();
				for (int i = 0; i < count; i++) {
					final String attrName = xml.getAttributeName(i);
					if (LevelTimePeriod.XML_ATTR_START.equals(attrName)) {
						period.start = xml.getAttributeIntValue(i, 0);
					} else if (LevelTimePeriod.XML_ATTR_MIN.equals(attrName)) {
						period.minCustomerInterval = xml.getAttributeIntValue(i, 0);
					} else if (LevelTimePeriod.XML_ATTR_MAX.equals(attrName)) {
						period.maxCustomerInterval = xml.getAttributeIntValue(i, 0);
					}
				}
				
				result.time.periods.add(period);
			} else if (RushHour.XML_TAG.equals(tagName)) {
				RushHour rushHour = new RushHour();
				final int count = xml.getAttributeCount();
				for (int i = 0; i < count; i++) {
					final String attrName = xml.getAttributeName(i);
					if (RushHour.XML_ATTR_CLOCK_INDEX.equals(attrName)) {
						rushHour.rushClockIndex = xml.getAttributeIntValue(i, 0);
					}
				}
				
				result.time.rushHours.add(rushHour);
			}
		}
		
		private void parseTableTag(XmlResourceParser xml) {
			inTableTag = true;
		}
		
		private void handleTagInTable(XmlResourceParser xml) throws LevelXmlFormatException {
			final String tagName = xml.getName();
			
			TableItem item = null;
			if (TableItem.XML_TAG.equals(tagName)) {
				item = new TableItem();
			} else if (TableLevelItem.XML_TAG.equals(tagName)) {
				item = new TableLevelItem();
			} else {
				throw new LevelXmlFormatException("handleTagInTable unknown table item tag:" + tagName);
			}
			
			final int count = xml.getAttributeCount();
			for (int i = 0; i < count; i++) {
				String attrName = xml.getAttributeName(i);
				if (TableItem.XML_ATTR_TYPE.equals(attrName)) {
					String typeValue = xml.getAttributeValue(i);
					if (TableItem.ATTR_NAME_2_TYPE.containsKey(typeValue)) {
						item.type = TableItem.ATTR_NAME_2_TYPE.get(typeValue);
					} else {
						throw new LevelXmlFormatException("handleTagInTable unknown table item attribute value:" + typeValue);
					}
				} else if (TableLevelItem.XML_ATTR_LEVEL.equals(attrName)) {
					final int level = xml.getAttributeIntValue(i, 0);
					assert (1 <= level && level <= 3);
					((TableLevelItem)item).level = level;
				}
			}
			result.tableConfig.items.add(item);
		}
		
		private void parseCustomerTag(XmlResourceParser xml) throws LevelXmlFormatException {
			inCustomerConfig = true;
			customerConfig = new LevelCustomerConfig();
			
			assert xml.getAttributeCount() == 1;
			final String attrName = xml.getAttributeName(0);
			if (LevelCustomerConfig.XML_ATTR_TYPE.equals(attrName) == false) {
				throw new LevelXmlFormatException("parseCustomerTag unknown attribute name:" + attrName);
			}
			
			final String typeValue = xml.getAttributeValue(0);
			if (LevelCustomerConfig.STRING_2_CUSTOMER_TYPE.containsKey(typeValue) == false) {
				throw new LevelXmlFormatException("parseCustomerTag unknown type value:" + typeValue);
			}
			
			customerConfig.type = LevelCustomerConfig.STRING_2_CUSTOMER_TYPE.get(typeValue);
		}
		
		private void handleTagInOrderSpec(XmlResourceParser xml) throws LevelXmlFormatException {
			final String tagName = xml.getName();
			if (LevelCustomerConfig.FOOD_TAG_2_FOOD_TYPE.containsKey(tagName) == false) {
				throw new LevelXmlFormatException("handleTagInOrderSpec unknown food type:" + tagName);
			}
			
			int foodType = LevelCustomerConfig.FOOD_TAG_2_FOOD_TYPE.get(tagName);
			boolean mustOrder = false;
			if (xml.getAttributeCount() > 0 && LevelCustomerConfig.XML_ATTR_MUST_ORDER_NAME.equals(xml.getAttributeName(0))) {
				mustOrder = xml.getAttributeBooleanValue(0, false);
			}
			
			boolean success = false;
			if (mustOrder) {
				success = customerConfig.mustOrder.addFood(foodType);
			} else {
				success = customerConfig.optionalSet.addFood(foodType);
			}
			if (!success) {
				Log.e(TAG, "handleTagInOrderSpec failed to add food:" + foodType + ", must order:" + mustOrder);
			}
		}
		
		private void handleTagInAppearanceSpec(XmlResourceParser xml) throws LevelXmlFormatException {
			final String tagName = xml.getName();
			if (AppearanceConfig.XML_TAG_WEIGHT.equals(tagName)) {
				final String attrName = xml.getAttributeName(0);
				if (AppearanceConfig.XML_ATTR_WEIGHT_VALUE.equals(attrName)) {
					customerConfig.appearanceConfig.weight = xml.getAttributeIntValue(0, 1);
				} else {
					throw new LevelXmlFormatException("handleTagInAppearanceSpec unknown Weight attribute:" + attrName);
				}
			} else if (AppearanceConfig.XML_TAG_TIME.equals(tagName)) {
				final String attrName = xml.getAttributeName(0);
				if (AppearanceConfig.XML_ATTR_TIME_VALUE.equals(attrName)) {
					customerConfig.appearanceConfig.times.add(xml.getAttributeIntValue(0, 0));
				} else {
					throw new LevelXmlFormatException("handleTagInAppearanceSpec unknown Time attribute:" + attrName);
				}
			} else {
				throw new LevelXmlFormatException("handleTagInAppearanceSpec unknown tag name:" + tagName);
			}
		}
		
		private void parseOrderSpec(XmlResourceParser xml) throws LevelXmlFormatException {
			inCustomerConfigOrderSpec = true;
		}
		
		private void parseAppearanceConfig(XmlResourceParser xml) throws LevelXmlFormatException {
			inCustomerConfigAppearanceSpec = true;
			
			assert xml.getAttributeCount() == 1;
			final String attrName = xml.getAttributeName(0);
			if (AppearanceConfig.XML_ATTR_TYPE.equals(attrName)) {
				final String attrValue = xml.getAttributeValue(0);
				if (AppearanceConfig.XML_ATTR_TYPE_VALUE_RANDOM.equals(attrValue)) {
					customerConfig.appearanceConfig = new AppearanceConfig(AppearanceConfig.Type.RANDOM);
				} else if (AppearanceConfig.XML_ATTR_TYPE_VALUE_FIXED.equals(attrValue)) {
					customerConfig.appearanceConfig = new AppearanceConfig(AppearanceConfig.Type.FIXED);
				} else {
					throw new LevelXmlFormatException("parseAppearanceConfig unknown AppearanceConfig type attribute value:" + attrValue);
				}
			} else {
				throw new LevelXmlFormatException("parseAppearanceConfig unknown attribute:" + attrName);
			}
		}
		
		private void parseWhatsNewTag(XmlResourceParser xml) throws LevelXmlFormatException {
			assert xml.getAttributeCount() == 1;
			
			inWhatsNew = true;
			result.whatsNew = new WhatsNew();
		}
		
		private void handleTagInWhatsNew(XmlResourceParser xml) throws LevelXmlFormatException {
			final String tagName = xml.getName();
			assert WhatsNew.XML_ITEM_TAG.equals(tagName);
			assert xml.getAttributeCount() == 1;
			
			final String attrName = xml.getAttributeName(0);
			if (WhatsNew.XML_ATTR_PARTITION.equals(attrName)) {
				String partition = xml.getAttributeValue(0);
				result.whatsNew.partitionList.add(partition);
			}
		}
		
		private int targetMajorLevel, targetMinorLevel;
		private boolean targetLevelFound = false;
		private GameLevel result;
		
		private boolean finished;
		private boolean inTargetLevel;
		private boolean inTimeTag;
		private boolean inTableTag;
		
		private boolean inCustomerConfig;
		private boolean inCustomerConfigOrderSpec;
		private boolean inCustomerConfigAppearanceSpec;
		private LevelCustomerConfig customerConfig;
		
		private boolean inWhatsNew;
	}
	
	public static boolean loadAllLevelScoreThresholds(Context context, Map<LevelNumber, LevelScore> result) {
		if (Config.DEBUG_LOG) Log.d(TAG, "loadAllLevelScoreThresholds");
		
		LevelScoreThresholdParser parser = new LevelScoreThresholdParser();
		try {
			parser.parse(context, result);
		} catch (LevelXmlFormatException e) {
			Log.e(TAG, "loadAllLevelScoreThresholds exception:" + e);
			return false;
		} catch (XmlPullParserException e) {
			Log.e(TAG, "loadAllLevelScoreThresholds exception:" + e);
			return false;
		} catch (IOException e) {
			Log.e(TAG, "loadAllLevelScoreThresholds exception:" + e);
			return false;
		}
		
		return true;
	}
	
	// parse all levels' LevelScore in one pass
	private static class LevelScoreThresholdParser {
		
		public void parse(Context context, Map<LevelNumber, LevelScore> result)
				throws LevelXmlFormatException, XmlPullParserException,
				IOException {
			this.result = result;
			
			XmlResourceParser xml = context.getResources().getXml(LEVEL_XML_RESOURCE);
			int parserEvent = xml.getEventType();
			while (parserEvent != XmlPullParser.END_DOCUMENT) {
				switch (parserEvent) {
				case XmlPullParser.START_TAG:
					onStartTag(xml);
					break;
				case XmlPullParser.END_TAG:
					onEndTag(xml);
					break;
				}
				parserEvent = xml.next();
			}
			xml.close();
		}
		
		private void onStartTag(XmlResourceParser xml) throws LevelXmlFormatException {
			final String tagName = xml.getName();
			
			if (GameLevel.XML_TAG.equals(tagName)) {
				handleGameLevelTag(xml);
			} else if (LevelScore.XML_TAG.equals(tagName)) {
				handleLevelScoreTag(xml);
			}
		}
		
		private void onEndTag(XmlResourceParser xml) {
			final String tagName = xml.getName();
			
			if (GameLevel.XML_TAG.equals(tagName)) {
				inLegalGameLevelTag = false;
			}
		}
		
		private void handleGameLevelTag(XmlResourceParser xml) throws LevelXmlFormatException {
			int major = 0;
			int minor = 0;
			for (int i = 0; i < xml.getAttributeCount(); i++) {
				final String attrName = xml.getAttributeName(i);
				if (GameLevel.XML_ATTR_MAJOR_LEVEL.equals(attrName)) {
					major = xml.getAttributeIntValue(i, 0);
				} else if (GameLevel.XML_ATTR_MINOR_LEVEL.equals(attrName)) {
					minor = xml.getAttributeIntValue(i, 0);
				}
			}
			
			inLegalGameLevelTag = level.set(major, minor);
		}
		
		private void handleLevelScoreTag(XmlResourceParser xml) throws LevelXmlFormatException {
			if (!inLegalGameLevelTag || result == null) {
				return;
			}
			
			LevelScore threshold = new LevelScore();
			
			for (int i = 0; i < xml.getAttributeCount(); i++) {
				final String attrName = xml.getAttributeName(i);
				if (LevelScore.XML_ATTR_MIN.equals(attrName)) {
					threshold.min = xml.getAttributeIntValue(i, 0);
				} else if (LevelScore.XML_ATTR_MED.equals(attrName)) {
					threshold.med = xml.getAttributeIntValue(i, 0);
				} else if (LevelScore.XML_ATTR_HIGH.equals(attrName)) {
					threshold.high = xml.getAttributeIntValue(i, 0);
				} else if (LevelScore.XML_ATTR_MAX.equals(attrName)) {
					threshold.max = xml.getAttributeIntValue(i, 0);
				}
				
				if (Config.DEBUG_SUPER_PASSPORT) {
					threshold.min = 0;
				}
			}
			
			result.put(new LevelNumber(level), threshold);
		}
		
		private Map<LevelNumber, LevelScore> result = null;
		private boolean inLegalGameLevelTag = false;
		private LevelNumber level = new LevelNumber(0, 0);
	}
	
	private static final int LEVEL_XML_RESOURCE = R.xml.levels;
}
