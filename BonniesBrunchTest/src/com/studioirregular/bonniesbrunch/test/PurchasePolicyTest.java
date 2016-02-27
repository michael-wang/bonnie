package com.studioirregular.bonniesbrunch.test;

import junit.framework.TestCase;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.LevelSystem;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelNumber;
import com.studioirregular.bonniesbrunch.main.PurchasePolicy;

public class PurchasePolicyTest extends TestCase {

	public void testIsFreeToPlayLevel() {
		PurchasePolicy policy = new PurchasePolicy();
		
		for (int major = LevelSystem.MIN_MAJOR_LEVEL; major <= LevelSystem.MAX_MAJOR_LEVEL; major++) {
			for (int minor = LevelSystem.MIN_MINOR_LEVEL; minor <= LevelSystem.MAX_MINOR_LEVEL; minor++) {
				LevelNumber level = new LevelNumber(major, minor);
				boolean freeToPlay = policy.isFreeToPlayLevel(level);
				if ((major < Config.MAJOR_LEVEL_START_PURCHASE_LOCK) ||
					(major == Config.MAJOR_LEVEL_START_PURCHASE_LOCK && (minor < Config.MINOR_LEVEL_START_PURCHASE_LOCK))) {
					assertTrue("level:" + level + " should be free to play.", freeToPlay);
				} else {
					assertFalse("level:" + level + " should NOT be free to play.", freeToPlay);
				}
			}
		}
		
	}

	public void testShowPurchaseHint() {
		PurchasePolicy policy = new PurchasePolicy();
		
		for (int major = LevelSystem.MIN_MAJOR_LEVEL; major <= LevelSystem.MAX_MAJOR_LEVEL; major++) {
			for (int minor = LevelSystem.MIN_MINOR_LEVEL; minor <= LevelSystem.MAX_MINOR_LEVEL; minor++) {
				LevelNumber level = new LevelNumber(major, minor);
				boolean showPurchaseHint = policy.showPurchaseHint(level);
				if ((major < Config.MAJOR_LEVEL_START_HINT_PURCHASE) ||
					(major == Config.MAJOR_LEVEL_START_HINT_PURCHASE && (minor < Config.MINOR_LEVEL_START_HINT_PURCHASE))) {
					assertFalse("level:" + level + " should NOT show purchase hint.", showPurchaseHint);
				} else {
					assertTrue("level:" + level + " should show hint to purchase.", showPurchaseHint);
				}
			}
		}
		
	}

}
