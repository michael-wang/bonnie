package com.studioirregular.bonniesbrunch.base;

import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;

public abstract class ObjectBase {

	public ObjectBase() {
		if (Config.DEBUG_LOG_ALLOCATION) {
			Log.d("ObjectBase", "object allocation:" + getClass().getName());
		}
	}
	
	public void update(long timeDelta, ObjectBase parent) {
		// do nothing here.
	}
	
	public abstract void reset();
	
}
