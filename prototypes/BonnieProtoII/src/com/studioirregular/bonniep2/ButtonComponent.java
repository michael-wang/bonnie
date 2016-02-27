package com.studioirregular.bonniep2;

import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

public class ButtonComponent implements Component, OnTouchListener {

	private static final boolean DO_LOG = false;
	private static final String TAG = "button-component";
	
	private String id;
	RectF box;
	private int buttonState = ComponentEvent.BUTTON_UP;
	private boolean isDownOnMe = false;
	private EventHost eventHost;
	
	public ButtonComponent(String id, EventHost host, float left, float top, float width, float height) {
		this.id = id;
		box = new RectF(left, top, left + width, top + height);
		this.eventHost = host;
	}
	
	@Override
	public String toString() {
		return "ButtonComponent id:" + id + ", box:" + box + ", eventHost:" + eventHost;
	}
	
	@Override
	public boolean onTouch(MotionEvent event) {
		final int action = event.getAction();
		
		boolean consumed = false;
		boolean stateChanged = false;
		
		CoordinateConverter converter = CoordinateConverter.getInstance();
		final float x = converter.convertX(event.getX());
		final float y = converter.convertY(event.getY());
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (box.contains(x, y)) {
				buttonState = ComponentEvent.BUTTON_DOWN;
				isDownOnMe = true;
				stateChanged = true;
				consumed = true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (isDownOnMe) {
				if (box.contains(x, y) == false) {
					buttonState = ComponentEvent.BUTTON_CANCEL;
					isDownOnMe = false;
					stateChanged = true;
				} else {
					consumed = true;
				}
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			if (isDownOnMe) {
				buttonState = ComponentEvent.BUTTON_CANCEL;
				isDownOnMe = false;
				stateChanged = true;
				consumed = false;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (isDownOnMe) {
				buttonState = ComponentEvent.BUTTON_UP;
				isDownOnMe = false;
				stateChanged = true;
				consumed = true;
			}
			break;
		}
		
		if (stateChanged) {
			eventHost.send(this, new ComponentEvent(buttonState, id));
		}
		
		if (DO_LOG) Log.d(TAG, "onTouch x:" + x + ",y:" + y + " consumed:" + consumed);
		return consumed;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
}
