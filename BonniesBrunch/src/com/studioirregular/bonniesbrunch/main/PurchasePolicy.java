package com.studioirregular.bonniesbrunch.main;

import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelNumber;

public class PurchasePolicy {

	private static final String TAG = "purchase-policy";
	
	public boolean isFreeToPlayLevel(LevelNumber level) {
		if (Config.DEBUG_LOG) Log.w(TAG, "isFreeToPlayLevel level:" + level);
		return (level.major < Config.MAJOR_LEVEL_START_PURCHASE_LOCK) || 
				(level.major == Config.MAJOR_LEVEL_START_PURCHASE_LOCK && level.minor < Config.MINOR_LEVEL_START_PURCHASE_LOCK);
	}
	
	public boolean showPurchaseHint(LevelNumber level) {
		if (Config.DEBUG_LOG) Log.w(TAG, "showPurchaseHint level:" + level);
		return Config.MAJOR_LEVEL_START_HINT_PURCHASE <= level.major && Config.MINOR_LEVEL_START_HINT_PURCHASE <= level.minor;
	}
}
