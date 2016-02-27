package com.studioirregular.bonniep1;

import java.util.ArrayList;
import java.util.List;

public class GameEntity {

	// touch states
	static final int STATE_TOUCH_DOWN		= 1;
	static final int STATE_TOUCH_MOVE		= 2;
	static final int STATE_TOUCH_UP			= 3;
	static final int STATE_TOUCH_CANCEL		= 4;
	// button states
	static final int STATE_BUTTON_UP		= 5;
	static final int STATE_BUTTON_DOWN		= 6;
	static final int STATE_BUTTON_DOWN_CANCEL= 7;
	
	// events which component can send
	static final int EVENT_STATE_CHANGE	= 1;
	interface StateChangedListener {
		public void onStateChanged(Integer newState);
	}
	
	private static final String TAG = "entity";
	private static final boolean DO_LOG = false;
	
	private List<Object> components = new ArrayList<Object>();
	private StateChangedListener listener;
	
	public GameEntity() {
		;
	}
	
	public void addComponent(Object component) {
		components.add(component);
	}
	
	public Object getComponent(Class< ? > componentClass) {
		for (Object component : components) {
			if (componentClass.isInstance(component)) {
				return component;
			}
		}
		return null;
	}
	
	public void registerStateChangedListener(StateChangedListener listener) {
		this.listener = listener;
	}
	
	public void unregisterStateChangedListener(StateChangedListener listener) {
		if (this.listener == listener) {
			this.listener = null;
		}
	}
	
	void sendEvent(int event, Object obj) {
		if (event == EVENT_STATE_CHANGE) {
			for (Object component : components) {
				if (component instanceof StateChangedListener) {
					((StateChangedListener)component).onStateChanged((Integer)obj);
				}
			}
			
			if (listener != null) {
				listener.onStateChanged((Integer)obj);
			}
		}
		
	}
	
}
