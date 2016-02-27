package com.studioirregular.bonniep1;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

	private UserInput gestureReceiver;
	
	public MyGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setGestureReceiver(UserInput receiver) {
		gestureReceiver = receiver;
	}
	
	private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			if (gestureReceiver != null) {
				gestureReceiver.onDown(e.getX(), e.getY());
			}
			return true;
		}
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (gestureReceiver != null) {
				gestureReceiver.onScroll(e2.getX(), e2.getY(), distanceX, distanceY);
			}
 			return true;
		}
		
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (gestureReceiver != null) {
				gestureReceiver.onTap(e.getX(), e.getY());
			}
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			if (gestureReceiver != null) {
				gestureReceiver.onUp(e.getX(), e.getY());
			}
			return true;
		}
		
	}
	private GestureDetector gestureDetector = new GestureDetector(new MyGestureListener());
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

}
