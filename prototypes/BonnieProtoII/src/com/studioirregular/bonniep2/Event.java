package com.studioirregular.bonniep2;


public abstract class Event {

//	private static final String TAG = "event";
	
	public int what;
	public Event(int what) {
		this.what = what;
	}
	
	@Override
	public boolean equals(Object o) {
//		Log.d(TAG, "equals me:" + this.toString() + ", o:" + o.toString());
		
		if (this == o) {
			return true;
		}
		
		if (!(o instanceof Event)) {
			return false;
		}
		
		Event lhs = (Event)o;
		return what == lhs.what;
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		
		result = 31 * result + what;
		
//		Log.d(TAG, "hashCode result:" + result);
		return result;
	}
	
	@Override
	public String toString() {
		return getClass().getName() + " what:" + what;
	}
	
}
