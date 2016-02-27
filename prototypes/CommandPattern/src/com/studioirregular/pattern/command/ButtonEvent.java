package com.studioirregular.pattern.command;

import android.util.Log;

public class ButtonEvent extends GameEvent {

	public static final int BUTTON_CLICKED = 1;
	
	private static final String TAG = "button-event";
	
	private String entityName;
	
	public ButtonEvent(int what, String entityName) {
		super(what);
		this.entityName = entityName;
	}
	
	@Override
	public boolean equals(Object o) {
		Log.d(TAG, "equals me:" + this.toString() + ", o:" + o.toString());
		
		if (super.equals(o) == false) {
			return false;
		}
		
		ButtonEvent lhs = (ButtonEvent)o;
		
		return (entityName == null ? lhs.entityName == null : entityName.equals(lhs.entityName));
	}

	@Override
	public int hashCode() {
		int result = 17;
		
		result = 31 * result + what;
		result = 31 * result + (entityName == null ? 0 : entityName.hashCode());
		
		Log.d(TAG, "hashCode result:" + result);
		return result;
	}
	
	@Override
	public String toString() {
		return "ButtonEvent what:" + what + ", entityName:" + entityName;
	}
	
}
