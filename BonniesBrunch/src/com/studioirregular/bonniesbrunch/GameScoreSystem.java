package com.studioirregular.bonniesbrunch;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.studioirregular.bonniesbrunch.LevelSystem.LevelNumber;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelScore;


public class GameScoreSystem {

	private static final String TAG = "game-score-system";
	
	// singleton
	public static GameScoreSystem getInstance() {
		if (sInstance == null) {
			sInstance = new GameScoreSystem();
		}
		return sInstance;
	}
	
	public static void releaseInstance() {
		if (sInstance != null) {
			sInstance = null;
		}
	}
	
	public void accumulateCustomer(boolean served) {
		if (served) {
			servedCustomerCount++;
		} else {
			lostCustomerCount++;
		}
	}
	
	public void debugSetCustomer(int served, int lost) {
		servedCustomerCount = served;
		lostCustomerCount = lost;
	}
	
	public void accumulate(int money, int tip) {
		accumulatedMoney += money;
		accumulatedTip += tip;
	}
	
	public void clear() {
		servedCustomerCount = 0;
		lostCustomerCount = 0;
		accumulatedMoney = 0;
		accumulatedTip = 0;
	}
	
	public int getServedCustomerCount() {
		return servedCustomerCount;
	}
	
	public int getLostCustomerCount() {
		return lostCustomerCount;
	}
	
	public int getAccumulatedMoney() {
		return accumulatedMoney;
	}
	
	public int getAccumulatedTip() {
		return accumulatedTip;
	}
	
	public boolean loadGameScores(Context context) {
		levelScores.clear();
		
		SharedPreferences pref = context.getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
		
		for (int major = LevelSystem.MIN_MAJOR_LEVEL; major <= LevelSystem.MAX_MAJOR_LEVEL; major++) {
			for (int minor = LevelSystem.MIN_MINOR_LEVEL; minor <= LevelSystem.MAX_MINOR_LEVEL; minor++) {
				final String key = getLevelKey(major, minor);
				final int score = pref.getInt(key, 0);
				
				LevelNumber level = new LevelNumber(major, minor);
				if (score != 0) {
					if (Config.DEBUG_LOG) Log.d(TAG, "loadGameScores level:" + level + ", score:" + score);
				}
				
				levelScores.put(level, score);
			}
		}
		return true;
	}
	
	// return true if it's new record and the score is saved.
	public boolean saveLevelScore(Context context, LevelNumber level) {
		final int score = accumulatedMoney + accumulatedTip;
		if (Config.DEBUG_LOG) Log.d(TAG, "saveLevelScore level:" + level + ",score:" + score);
		
		if (levelScores.containsKey(level)) {
			int savedScore = levelScores.get(level);
			if (score <= savedScore) {
				if (Config.DEBUG_LOG) Log.w(TAG, "not high score for level:" + level + ", high:" + getMinorLevelScore(level) + ", current:" + score);
				return false;
			}
		}
		
		levelScores.put(new LevelNumber(level), score);
		
		SharedPreferences pref = context.getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		
		final String key = getLevelKey(level.major, level.minor);
		editor.putInt(key, score);
		if (editor.commit()) {
			if (Config.DEBUG_LOG) Log.w(TAG, "saveLevelScore new high score:" + score + " saved for level:" + level);
		}
		return true;
	}
	
	public boolean loadLevelsScoreThresholds(Context context) {
		return LevelSystem.loadAllLevelScoreThresholds(context, levelScoreThresholds);
	}
	
	public int getMinorLevelScore(LevelNumber level) {
		if (levelScores.containsKey(level)) {
			return levelScores.get(level);
		}
		return 0;
	}
	
	public int getMinorLevelStar(LevelNumber level) {
		if (levelScoreThresholds.isEmpty()) {
			Log.e(TAG, "getMinorLevelStar level score thresholds should be loaded first.");
			return 0;
		}
		
		LevelScore threshold = levelScoreThresholds.get(level);
		if (threshold == null) {
			Log.e(TAG, "getMinorLevelStar cannot find score threshold for level:" + level);
			Set<LevelNumber> keySet = levelScoreThresholds.keySet();
			for (LevelNumber key : keySet) {
				Log.w(TAG, "valid key:" + key);
			}
			return 0;
		}
		
		int score = getMinorLevelScore(level);
		int starCount = 0;
		if (threshold.high <= score) {
			starCount = 3;
		} else if (threshold.med <= score) {
			starCount = 2;
		} else if (threshold.min <= score) {
			starCount = 1;
		}
		if (Config.DEBUG_LOG) Log.d(TAG, "getMinorLevelStar level:" + level + ",score:" + score + ", threshold:" + threshold);
		return starCount;
	}
	
	public int getMajorLevelScore(int majorLevel) {
		int result = 0;
		LevelNumber level = new LevelNumber();
		
		for (int minor = LevelSystem.MIN_MINOR_LEVEL; minor <= LevelSystem.MAX_MINOR_LEVEL; minor++) {
			if (level.set(majorLevel, minor) == false) {
				Log.w(TAG, "getMajorLevelScore invalid level:" + majorLevel + "," + minor);
				continue;
			}
			
			Integer levelScore = levelScores.get(level);
			if (levelScore == null) {
				Log.w(TAG, "getMajorLevelScore cannot find score for level:" + level);
				continue;
			}
			
			result += levelScore;
		}
		
		return result;
	}
	
	public int getMajorLevelStarCount(int major) {
		int result = 0;
		LevelNumber level = new LevelNumber();
		for (int minor = LevelSystem.MIN_MINOR_LEVEL; minor <= LevelSystem.MAX_MINOR_LEVEL; minor++) {
			level.set(major, minor);
			result += getMinorLevelStar(level);
		}
		return result;
	}
	
	public int getTotalLevelScore() {
		int result = 0;
		for (int major = LevelSystem.MIN_MAJOR_LEVEL; major <= LevelSystem.MAX_MAJOR_LEVEL; major++) {
			result += getMajorLevelScore(major);
		}
		return result;
	}
	
	private String getLevelKey(int major, int minor) {
		return Integer.toString(major) + Integer.toHexString(minor);
	}
	
	private static GameScoreSystem sInstance = null;
	private GameScoreSystem() {}
	
	private int servedCustomerCount;
	private int lostCustomerCount;
	private int accumulatedMoney;
	private int accumulatedTip;
	
	private Map<LevelNumber, Integer> levelScores = new HashMap<LevelNumber, Integer>();
	private Map<LevelNumber, LevelScore> levelScoreThresholds = new HashMap<LevelNumber, LevelScore>();
}
