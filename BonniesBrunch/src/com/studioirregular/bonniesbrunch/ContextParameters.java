package com.studioirregular.bonniesbrunch;

import android.util.Log;

public class ContextParameters {
	
	private static final String TAG = "context-parameters";
	
	// singleton
	public static ContextParameters getInstance() {
		if (sInstance == null) {
			sInstance = new ContextParameters();
		}
		return sInstance;
	}
	private static ContextParameters sInstance = null;
	
	private ContextParameters() {
		if (Config.DEBUG_LOG) Log.d(TAG, "ContextParameters allocated");
	}
	
	// world dimension
	public final float gameWidth = 720.0f;
	public final float gameHeight = 480.0f;
	
	// screen dimension
	public float viewportWidth = 720;
	public float viewportHeight = 480;
	public float viewportOffsetX = 0;
	public float viewportOffsetY = 0;
	
	// for touch event to convert coordinates from screen to game coordinates.
	public float scaleViewToGameX = 1.0f;
	public float scaleViewToGameY = 1.0f;
	
	// debug parameters
	public boolean debugDrawTouchArea = false;
	public boolean disableLevelScoreLock = false;

}
