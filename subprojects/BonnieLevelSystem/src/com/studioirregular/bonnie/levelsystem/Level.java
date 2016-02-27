package com.studioirregular.bonnie.levelsystem;

import java.util.HashMap;
import java.util.Map;

import com.studioirregular.bonnie.foodsystem.FoodSystem.Food;
import com.studioirregular.bonnie.foodsystem.FoodSystem.FoodCombination;

public class Level {

	public static final int LEVEL_INVALID_VALUE = 0;
	public static final int SUB_LEVEL_INVALID_VALUE = 0;
	public static final int TYPE_INVALID_VALUE = 0;
	public static final int TOTAL_TIME_INVALID_VALUE = 0;
	
	public static enum LevelType {
		INVALID,
		NORMAL,
		NORMAL_WITH_TUTORIAL,
		SPECIAL
	};
	
	// XML tag/attributes
	public static final String	TAG_NAME_LEVEL					= "Level";
	public static final String		ATTR_NAME_LEVEL_LEVEL		= "level";
	public static final String		ATTR_NAME_LEVEL_SUBLEVEL	= "sub_level";
	public static final String		ATTR_NAME_LEVEL_TYPE		= "type";
	public static final String		ATTR_NAME_LEVEL_TOTAL_TIME	= "total_time";
	
	public static final String	TAG_NAME_WHATSNEWDIALOG			= "WhatsNewDialog";
	public static final String		ATTR_NAME_WHATSNEWDIALOG_DRAWABLE	= "drawable";
	
	public static final String	TAG_NAME_TABLE_ITEM_CONFIG			= "TableConfiguration";
	public static final String		TAG_NAME_TABLE_ITEM_BAGEL		= "Bagel";
	public static final String		TAG_NAME_TABLE_ITEM_CROISSANT	= "Croissant";
	public static final String		TAG_NAME_TABLE_ITEM_BUTTER		= "Butter";
	public static final String		TAG_NAME_TABLE_ITEM_HONEY		= "Honey";
	public static final String		TAG_NAME_TABLE_ITEM_TOMATO		= "Tomato";
	public static final String		TAG_NAME_TABLE_ITEM_LATTUCE		= "Lettuce";
	public static final String		TAG_NAME_TABLE_ITEM_CHEESE		= "Cheese";
	public static final String		TAG_NAME_TABLE_ITEM_MUFFIN_LV1	= "MuffinLv1";
	public static final String		TAG_NAME_TABLE_ITEM_MUFFIN_LV2	= "MuffinLv2";
	public static final String		TAG_NAME_TABLE_ITEM_TOAST_LV1	= "ToastLv1";
	public static final String		TAG_NAME_TABLE_ITEM_TOAST_LV2	= "ToastLv2";
	public static final String		TAG_NAME_TABLE_ITEM_TOAST_LV3	= "ToastLv3";
	public static final String		TAG_NAME_TABLE_ITEM_PAN_LV1		= "PanLv1";
	public static final String		TAG_NAME_TABLE_ITEM_PAN_LV2		= "PanLv2";
	public static final String		TAG_NAME_TABLE_ITEM_PAN_LV3		= "PanLv3";
	public static final String		TAG_NAME_TABLE_ITEM_MILK		= "Milk";
	public static final String		TAG_NAME_TABLE_ITEM_COFFEE		= "Coffee";
	public static final String		TAG_NAME_TABLE_ITEM_CANDY_LV1	= "CandyLv1";
	public static final String		TAG_NAME_TABLE_ITEM_CANDY_LV2	= "CandyLv2";
	public static final String		TAG_NAME_TABLE_ITEM_CANDY_LV3	= "CandyLv3";
	public static final String		TAG_NAME_TABLE_ITEM_TRASHCAN	= "Trashcan";
	public static final String			ATTR_NAME_TABLE_ITEM_ENABLE	= "enable";
	
	public static final int TABLE_ITEM_INVALIDE			= 0;
	public static final int TABLE_ITEM_BAGEL			= 0x1 << 0;
	public static final int TABLE_ITEM_CROISSANT		= 0x1 << 1;
	public static final int TABLE_ITEM_MUFFIN_LV1		= 0x1 << 2;
	public static final int TABLE_ITEM_MUFFIN_LV2		= 0x1 << 3;
	public static final int TABLE_ITEM_TOAST_LV1		= 0x1 << 4;
	public static final int TABLE_ITEM_TOAST_LV2		= 0x1 << 5;
	public static final int TABLE_ITEM_TOAST_LV3		= 0x1 << 6;
	public static final int TABLE_ITEM_SAUCE_BUTTER		= 0x1 << 7;
	public static final int TABLE_ITEM_SAUCE_HONEY		= 0x1 << 8;
	public static final int TABLE_ITEM_SAUCE_TOMATO		= 0x1 << 9;
	public static final int TABLE_ITEM_LETTUCE			= 0x1 << 10;
	public static final int TABLE_ITEM_CHEESE			= 0x1 << 11;
	public static final int TABLE_ITEM_PAN_LV1			= 0x1 << 12;
	public static final int TABLE_ITEM_PAN_LV2			= 0x1 << 13;
	public static final int TABLE_ITEM_PAN_LV3			= 0x1 << 14;
	public static final int TABLE_ITEM_TRASHCAN			= 0x1 << 15;
	public static final int TABLE_ITEM_CANDY_LV1		= 0x1 << 16;
	public static final int TABLE_ITEM_CANDY_LV2		= 0x1 << 17;
	public static final int TABLE_ITEM_CANDY_LV3		= 0x1 << 18;
	public static final int TABLE_ITEM_MILK				= 0x1 << 19;
	public static final int TABLE_ITEM_COFFEE			= 0x1 << 20;
	
	public static final int TABLE_ITEM_MASK_MUFFIN		= TABLE_ITEM_MUFFIN_LV1 | TABLE_ITEM_MUFFIN_LV2;
	public static final int TABLE_ITEM_MASK_TOAST		= TABLE_ITEM_TOAST_LV1 | TABLE_ITEM_TOAST_LV2 | TABLE_ITEM_TOAST_LV3;
	public static final int TABLE_ITEM_MASK_PAN			= TABLE_ITEM_PAN_LV1 | TABLE_ITEM_PAN_LV2 | TABLE_ITEM_PAN_LV3;
	public static final int TABLE_ITEM_MASK_CANDY		= TABLE_ITEM_CANDY_LV1 | TABLE_ITEM_CANDY_LV2 | TABLE_ITEM_CANDY_LV3;
	
	static Map<String, Integer> TABLE_ITEM_TAG_2_ID;
	static {
		TABLE_ITEM_TAG_2_ID = new HashMap<String, Integer>();
		
		TABLE_ITEM_TAG_2_ID.put(TAG_NAME_TABLE_ITEM_BAGEL,			TABLE_ITEM_BAGEL);
		TABLE_ITEM_TAG_2_ID.put(TAG_NAME_TABLE_ITEM_CROISSANT,		TABLE_ITEM_CROISSANT);
		TABLE_ITEM_TAG_2_ID.put(TAG_NAME_TABLE_ITEM_BUTTER,			TABLE_ITEM_SAUCE_BUTTER);
		TABLE_ITEM_TAG_2_ID.put(TAG_NAME_TABLE_ITEM_HONEY,			TABLE_ITEM_SAUCE_HONEY);
		TABLE_ITEM_TAG_2_ID.put(TAG_NAME_TABLE_ITEM_TOMATO,			TABLE_ITEM_SAUCE_TOMATO);
		TABLE_ITEM_TAG_2_ID.put(TAG_NAME_TABLE_ITEM_LATTUCE,		TABLE_ITEM_LETTUCE);
		TABLE_ITEM_TAG_2_ID.put(TAG_NAME_TABLE_ITEM_CHEESE,			TABLE_ITEM_CHEESE);
		TABLE_ITEM_TAG_2_ID.put(TAG_NAME_TABLE_ITEM_MUFFIN_LV1,		TABLE_ITEM_MUFFIN_LV1);
		TABLE_ITEM_TAG_2_ID.put(TAG_NAME_TABLE_ITEM_MUFFIN_LV2,		TABLE_ITEM_MUFFIN_LV2);
		TABLE_ITEM_TAG_2_ID.put(TAG_NAME_TABLE_ITEM_TOAST_LV1,		TABLE_ITEM_TOAST_LV1);
		TABLE_ITEM_TAG_2_ID.put(TAG_NAME_TABLE_ITEM_TOAST_LV2,		TABLE_ITEM_TOAST_LV2);
		TABLE_ITEM_TAG_2_ID.put(TAG_NAME_TABLE_ITEM_TOAST_LV3,		TABLE_ITEM_TOAST_LV3);
		TABLE_ITEM_TAG_2_ID.put(TAG_NAME_TABLE_ITEM_PAN_LV1,		TABLE_ITEM_PAN_LV1);
		TABLE_ITEM_TAG_2_ID.put(TAG_NAME_TABLE_ITEM_PAN_LV2,		TABLE_ITEM_PAN_LV2);
		TABLE_ITEM_TAG_2_ID.put(TAG_NAME_TABLE_ITEM_PAN_LV3,		TABLE_ITEM_PAN_LV3);
		TABLE_ITEM_TAG_2_ID.put(TAG_NAME_TABLE_ITEM_MILK,			TABLE_ITEM_MILK);
		TABLE_ITEM_TAG_2_ID.put(TAG_NAME_TABLE_ITEM_COFFEE,			TABLE_ITEM_COFFEE);
		TABLE_ITEM_TAG_2_ID.put(TAG_NAME_TABLE_ITEM_CANDY_LV1,		TABLE_ITEM_CANDY_LV1);
		TABLE_ITEM_TAG_2_ID.put(TAG_NAME_TABLE_ITEM_CANDY_LV2,		TABLE_ITEM_CANDY_LV2);
		TABLE_ITEM_TAG_2_ID.put(TAG_NAME_TABLE_ITEM_CANDY_LV3,		TABLE_ITEM_CANDY_LV3);
		TABLE_ITEM_TAG_2_ID.put(TAG_NAME_TABLE_ITEM_TRASHCAN,		TABLE_ITEM_TRASHCAN);
	}
	
	// time period
	public static final String	TAG_NAME_PERIOD						= "Period";
	public static final String		ATTR_NAME_PERDIO_START_FROM				= "start_from";
	public static final String		ATTR_NAME_PERDIO_MIN_CUSTOMER_INTERVAL	= "min_customer_interval";
	public static final String		ATTR_NAME_PERDIO_MAX_CUSTOMER_INTERVAL	= "max_customer_interval";
	
	public static class Period {
		public int startFrom;
		public int minCustomerInterval;
		public int maxCustomerInterval;
		
		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			
			if (!(o instanceof Period)) {
				return false;
			}
			
			Period lhs = (Period)o;
			return (startFrom == lhs.startFrom)
					&& (minCustomerInterval == lhs.minCustomerInterval)
					&& (maxCustomerInterval == lhs.maxCustomerInterval);
		}
	}
	
	// customer
	public static final String	TAG_NAME_CUSTOMER					= "Customer";
	public static final String		ATTR_NAME_CUSTOMER_TYPE				= "type";
	public static final String		ATTR_VALUE_CUSTOMER_TYPE_1			= "JoggingGirl";
	public static final String		ATTR_VALUE_CUSTOMER_TYPE_2			= "WorkingMan";
	public static final String		ATTR_VALUE_CUSTOMER_TYPE_3			= "BalloonBoy";
	public static final String		ATTR_VALUE_CUSTOMER_TYPE_4			= "GirlWithDog";
	public static final String		ATTR_VALUE_CUSTOMER_TYPE_5			= "FoodCritic";
	public static final String		ATTR_VALUE_CUSTOMER_TYPE_6			= "SuperStarLady";
	public static final String		ATTR_VALUE_CUSTOMER_TYPE_7			= "Physicist";
	public static final String		ATTR_VALUE_CUSTOMER_TYPE_8			= "Tramp";
	public static final String		ATTR_VALUE_CUSTOMER_TYPE_9			= "Granny";
	public static final String		ATTR_VALUE_CUSTOMER_TYPE_10			= "SuperStarMan";
	
	public static final int CUSTOMER_TYPE_INVALID			= 0;
	public static final int CUSTOMER_JOGGING_GIRL			= 1;
	public static final int CUSTOMER_WORKING_MAN			= 2;
	public static final int CUSTOMER_BALLOON_BOY			= 3;
	public static final int CUSTOMER_GIRL_WITH_DOG			= 4;
	public static final int CUSTOMER_FOOD_CRITIC			= 5;
	public static final int CUSTOMER_SUPERSTAR_LADY			= 6;
	public static final int CUSTOMER_PHYSICIST				= 7;
	public static final int CUSTOMER_TRAMP					= 8;
	public static final int CUSTOMER_GRANNY					= 9;
	public static final int CUSTOMER_SUPERSTAR_MAN			= 10;
	
	static Map<String, Integer> TAG_2_CUSTOMER_TYPE;
	static {
		TAG_2_CUSTOMER_TYPE = new HashMap<String, Integer>();
		TAG_2_CUSTOMER_TYPE.put(ATTR_VALUE_CUSTOMER_TYPE_1,	CUSTOMER_JOGGING_GIRL);
		TAG_2_CUSTOMER_TYPE.put(ATTR_VALUE_CUSTOMER_TYPE_2,	CUSTOMER_WORKING_MAN);
		TAG_2_CUSTOMER_TYPE.put(ATTR_VALUE_CUSTOMER_TYPE_3,	CUSTOMER_BALLOON_BOY);
		TAG_2_CUSTOMER_TYPE.put(ATTR_VALUE_CUSTOMER_TYPE_4,	CUSTOMER_GIRL_WITH_DOG);
		TAG_2_CUSTOMER_TYPE.put(ATTR_VALUE_CUSTOMER_TYPE_5,	CUSTOMER_FOOD_CRITIC);
		TAG_2_CUSTOMER_TYPE.put(ATTR_VALUE_CUSTOMER_TYPE_6,	CUSTOMER_SUPERSTAR_LADY);
		TAG_2_CUSTOMER_TYPE.put(ATTR_VALUE_CUSTOMER_TYPE_7,	CUSTOMER_PHYSICIST);
		TAG_2_CUSTOMER_TYPE.put(ATTR_VALUE_CUSTOMER_TYPE_8,	CUSTOMER_TRAMP);
		TAG_2_CUSTOMER_TYPE.put(ATTR_VALUE_CUSTOMER_TYPE_9,	CUSTOMER_GRANNY);
		TAG_2_CUSTOMER_TYPE.put(ATTR_VALUE_CUSTOMER_TYPE_10,CUSTOMER_SUPERSTAR_MAN);
	}
	
	public static final String		TAG_NAME_FOOD_BAGEL				= "Bagel";
	public static final String		TAG_NAME_FOOD_CROISSANT			= "Croissant";
	public static final String		TAG_NAME_FOOD_MUFFIN_CIRCLE		= "MuffinCircle";
	public static final String		TAG_NAME_FOOD_MUFFIN_SQUARE		= "MuffinSquare";
	public static final String		TAG_NAME_FOOD_TOAST_WHITE		= "ToastWhite";
	public static final String		TAG_NAME_FOOD_TOAST_BLACK		= "ToastBlack";
	public static final String		TAG_NAME_FOOD_TOAST_YELLOW		= "ToastYellow";
	public static final String		TAG_NAME_FOOD_EGG				= "Egg";
	public static final String		TAG_NAME_FOOD_HAM				= "Ham";
	public static final String		TAG_NAME_FOOD_SAUSAGE			= "Sausage";
	public static final String		TAG_NAME_FOOD_BUTTER			= "Butter";
	public static final String		TAG_NAME_FOOD_HONEY				= "Honey";
	public static final String		TAG_NAME_FOOD_TOMATO			= "Tomato";
	public static final String		TAG_NAME_FOOD_LETTUCE			= "Lettuce";
	public static final String		TAG_NAME_FOOD_CHEESE			= "Cheese";
	public static final String		TAG_NAME_FOOD_MILK				= "Milk";
	public static final String		TAG_NAME_FOOD_COFFEE			= "Coffee";
	
	static Map<String, Food> TAG_2_FOOD;
	static {
		TAG_2_FOOD = new HashMap<String, Food>();
		
		TAG_2_FOOD.put(TAG_NAME_FOOD_BAGEL,			Food.getBagel());
		TAG_2_FOOD.put(TAG_NAME_FOOD_CROISSANT,		Food.getCroissant());
		TAG_2_FOOD.put(TAG_NAME_FOOD_MUFFIN_CIRCLE,	Food.getMuffinCircle());
		TAG_2_FOOD.put(TAG_NAME_FOOD_MUFFIN_SQUARE,	Food.getMuffinSquare());
		TAG_2_FOOD.put(TAG_NAME_FOOD_TOAST_WHITE,	Food.getToastWhite());
		TAG_2_FOOD.put(TAG_NAME_FOOD_TOAST_BLACK,	Food.getToastBlack());
		TAG_2_FOOD.put(TAG_NAME_FOOD_TOAST_YELLOW,	Food.getToastYellow());
		TAG_2_FOOD.put(TAG_NAME_FOOD_EGG,			Food.getEgg());
		TAG_2_FOOD.put(TAG_NAME_FOOD_HAM,			Food.getHam());
		TAG_2_FOOD.put(TAG_NAME_FOOD_SAUSAGE,		Food.getSausage());
		TAG_2_FOOD.put(TAG_NAME_FOOD_BUTTER,		Food.getButter());
		TAG_2_FOOD.put(TAG_NAME_FOOD_HONEY,			Food.getHoney());
		TAG_2_FOOD.put(TAG_NAME_FOOD_TOMATO,		Food.getTomato());
		TAG_2_FOOD.put(TAG_NAME_FOOD_LETTUCE,		Food.getLettuce());
		TAG_2_FOOD.put(TAG_NAME_FOOD_CHEESE,		Food.getCheese());
		TAG_2_FOOD.put(TAG_NAME_FOOD_MILK,			Food.getMilk());
		TAG_2_FOOD.put(TAG_NAME_FOOD_COFFEE,		Food.getCoffee());
	}
	
	public static class Customer {
		public int customerType;
		public FoodCombination prefferedFood = new FoodCombination();
	}
	
	public static class WhatsNewDialog {
		public String drawableResourceName;
	}
	
	public Level() {}
	
	public Level(int level, int subLevel, int type, int totalSeconds) {
		this.level = level;
		this.subLevel = subLevel;
		this.type = getType(type);
		totalTime = totalSeconds * 1000;
	}
	
	public void addTableItem(int item) {
		tableConfig |= item;
	}
	
	private LevelType getType(int type) {
		switch (type) {
		case 1:
			return LevelType.NORMAL;
		case 2:
			return LevelType.NORMAL_WITH_TUTORIAL;
		case 3:
			return LevelType.SPECIAL;
		}
		return LevelType.INVALID;
	}
	
	public int level;
	public int subLevel;
	public LevelType type;
	public long totalTime;
	
	public WhatsNewDialog[] whatsNewDialogs;
	
	public int tableConfig = TABLE_ITEM_INVALIDE;
	public Period[] periods;
	public Customer[] customers;
}
