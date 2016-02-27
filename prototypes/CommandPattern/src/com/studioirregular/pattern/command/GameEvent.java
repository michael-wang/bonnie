package com.studioirregular.pattern.command;

import android.util.Log;

public abstract class GameEvent {

	private static final String TAG = "event";
	
	public int what;
	
	public GameEvent(int what) {
		this.what = what;
	}
	
	@Override
	public boolean equals(Object o) {
		Log.d(TAG, "equals me:" + this.toString() + ", o:" + o.toString());
		
		if (this == o) {
			return true;
		}
		
		if (!(o instanceof GameEvent)) {
			return false;
		}
		
		GameEvent lhs = (GameEvent)o;
		return what == lhs.what;
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		
		result = 31 * result + what;
		
		Log.d(TAG, "hashCode result:" + result);
		return result;
	}
	
	@Override
	public String toString() {
		return "GameEvent what:" + what;
	}
	
}
