package com.studioirregular.bonniesbrunch;

import android.content.Context;
import android.content.SharedPreferences;

/*
 * Sound: on/off
 * Music: on/off
 * Level car scrolling offset: integer. (in major level selection).
 */
public class UserPreferences {

	private static final String KEY_SOUND = "sound";
	private static final String KEY_MUSIC = "music";
	private static final String KEY_CAR_OFFSET = "car_offset";
	
	public static void saveSoundPreference(Context context, boolean turnOff) {
		SharedPreferences pref = context.getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		
		editor.putBoolean(KEY_SOUND, turnOff);
		editor.commit();
	}
	
	public static void saveMusicPreference(Context context, boolean turnOff) {
		SharedPreferences pref = context.getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		
		editor.putBoolean(KEY_MUSIC, turnOff);
		editor.commit();
	}
	
	public static void saveCarOffset(Context context, int offset) {
		SharedPreferences pref = context.getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		
		editor.putInt(KEY_CAR_OFFSET, offset);
		editor.commit();
	}
	
	public static boolean getSoundPreference(Context context, boolean defaultValue) {
		SharedPreferences pref = context.getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
		return pref.getBoolean(KEY_SOUND, defaultValue);
	}
	
	public static boolean getMusicPreference(Context context, boolean defaultValue) {
		SharedPreferences pref = context.getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
		return pref.getBoolean(KEY_MUSIC, defaultValue);
	}
	
	public static boolean hasCarOffsetPreference(Context context) {
		SharedPreferences pref = context.getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
		return pref.contains(KEY_CAR_OFFSET);
	}
	
	public static int getCarOffsetPreference(Context context, int defaultValue) {
		SharedPreferences pref = context.getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
		return pref.getInt(KEY_CAR_OFFSET, defaultValue);
	}
	
}
