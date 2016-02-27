package com.studioirregular.bonniesbrunch;

public class Config {

	// post release changing: discount, request player to rating us.
	public static final boolean ENABLE_DISCOUNT = true;
	public static final int START_REQUEST_RATING_MAJOR = 1;
	public static final int START_REQUEST_RATING_MINOR = 5;
	public static final int START_REQUEST_STAR_COUNT = 2;		// when player earned >= **
	public static final int REQUEST_RATING_POSSIBILITY = 50;	// in %
	public static final int GOOGLE_LICENSING_CHECK_RATE = 30;	// 0 - 100
	
	// Splash
	public static final int DURATION_FOR_FUNNYLAB_LOGO = 2000;
	public static final int DURATION_FOR_STUDIO_IRREGULAR_LOGO = 2000;
	
	// Animation parameter
	public static final boolean LEVEL_SCORE_DO_ANIMATION = true;
	public static final int LEVEL_SCORE_ANIMATION_DURATION = 500;
	
	// for texture system
	public static final int TEXTURE_DEFAULT_WIDTH = 1024;
	public static final int TEXTURE_DEFAULT_HEIGHT = 1024;
	public static final boolean TEXTURE_SYSTEM_DETECT_PARTITION_COLLISION = true;
	public static final boolean TEXTURE_SYSTEM_SAVE_TEXTURE_TO_FILE = false;
	public static final String EXTERNAL_RESOURCES_PACKAGE = "com.studioirregular.bonniep2.res";
	public static final String EXTERNAL_RES_PREFIX = EXTERNAL_RESOURCES_PACKAGE + ":drawable/";
	
	// for music system
	public static final String EXTERNAL_SOUND_PREFIX = EXTERNAL_RESOURCES_PACKAGE + ":raw/";
	
	// debug flags
	public static final boolean DEBUG_LOG = false;
	public static final boolean DEBUG_LOAD_EXTERNAL_RESOURCE_FOR_TEXTURE = true;
	public static final boolean DEBUG_LOAD_EXTERNAL_RESOURCE_FOR_SOUND = true;
	public static final boolean DEBUG_SHOW_FPS = false;
	public static final boolean DEBUG_LOG_ALLOCATION = false;
	public static final boolean DEBUG_LOG_FRAME_TIME = false;
	public static final boolean DEBUG_DRAW_ENTITY_REGION = false;
	public static final float ENTITY_REGION_COLOR_R = 0.5f;
	public static final float ENTITY_REGION_COLOR_G = 1.0f;
	public static final float ENTITY_REGION_COLOR_B = 0.5f;
	public static final float ENTITY_REGION_COLOR_A = 0.5f;
	public static final boolean DEBUG_DRAW_TEXTURE_BOUND = false;
	public static final boolean DEBUG_UNLOCK_EVERYTHING = false;
	public static final boolean DEBUG_UNLOCK_FREE_LEVEL = false;
	public static final boolean DEBUG_SUPER_PASSPORT = false;	// let you pass each level very quickly.
	
	// see ContextParameters.debugDrawTouchArea
	public static final float TOUCH_REGION_COLOR_R = 1.0f;
	public static final float TOUCH_REGION_COLOR_G = 0.5f;
	public static final float TOUCH_REGION_COLOR_B = 0.5f;
	public static final float TOUCH_REGION_COLOR_A = 0.5f;
	
	public static final float TOUCH_REGION_COLOR_DOWN_R = 1.0f;
	public static final float TOUCH_REGION_COLOR_DOWN_G = 0.5f;
	public static final float TOUCH_REGION_COLOR_DOWN_B = 0.5f;
	public static final float TOUCH_REGION_COLOR_DOWN_A = 0.8f;
	
	public static final String PREFERENCE_NAME = "data";
	
	// purchase lock
	public static final int MAJOR_LEVEL_START_PURCHASE_LOCK = 2;
	public static final int MINOR_LEVEL_START_PURCHASE_LOCK = 6;
	public static final int MAJOR_LEVEL_START_HINT_PURCHASE = 2;
	public static final int MINOR_LEVEL_START_HINT_PURCHASE = 1;
	
	// advertisement
	public static final boolean ENABLE_BANNER_AD = true;
	public static final boolean ENABLE_INTERSTITAL_AD = false;
	public static final boolean SHOW_AD_IN_COMIC = true;	// for some version we want to remove ad here.
	public static final boolean DEBUG_AD = true;	// use debug ad on my devices.
	
	// score lock
	public static final int STAR_COUNT_TO_UNLOCK_LEVEL = 1;
	
	// In-app Billing related
//	public static final String GOOGLE_PLAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsdjePSxUagXVfU8bLOJRjhRuBBoWK8yIO1Ot9utfQZdu7d4C56tG6/qEOHmSt3CHQdTstCsBuX+t+Q3oWcg3E6u9yfe+HY4jeroBPob5zMkiQnz4upOM2G+7EQmpnZ7gS5TwV81NpILJ8blR5WHPjXFFwWD+qiwHAkmWRIFIc5js0utBJhOK6TKCRN5yJu2+tqXOe/vA+HFNLY2xoDx4k4mJtJSiEOc2WlTttPafyoqFnvCHs1oYTQWcO8bOWWYXCodT/4lMbvNaEqWdEWsuBsoRlSVLLqT8gIKYsiId/ifmzi/2ZubRXXNYGvkvtftc0XfqItNIoYjxIC+EPp6eGQIDAQAB";
	public static final String GOOGLE_PLAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqsSE6ZGoB8z7pBtf6d00h+FTOJOdvxL8XTZtw0CWjTT3G/zaibxtZqgzZBiYC1qThHOOlG1OBZ3z+5K+p1H3C8POA4SM/UwdKORGNFdURqqxdXcW57yL3HzZd4qxqH6+ppIYq9C+wr56Cr2auYL7s9b4iEeA/AnD7Pz5+abCUQmzbVVKrv3Xk6ZeAvQdSU44lUzpx+o5dPncvIUNk8z1eJ2ZMQTh/+KuFU5ZEQRpLqvHeWkXRBkhxVUB8moDlbkZzl6cF2UkQSi3oY4Zo91k5D17jbEuxzXaLQu+6a+8kwOFLQAOXbY1jxdU2LANOHN4nkt5RW4zujLM6Jxf8MBPlwIDAQAB";
	public static final String IN_APP_BILLING_PRODUCT_FULL_VERSION = "com.studioirregular.bonniesbrunch.fullversion";
	//public static final String IN_APP_BILLING_PRODUCT_FULL_VERSION = "android.test.purchased";
	
	// TEE shirt promotion
	public static final boolean ENABLE_PROMOTION_FOR_TEE_SHIRT = true;
	public static final String PROMOTE_TEE_URL = "http://www.thefirsthk.com/index.php?main_page=index&cPath=38&fb_source=message";
	
	private Config() {}
}
