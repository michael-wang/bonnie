package com.studioirregular.bonniep2;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyGLSurfaceView extends GLSurfaceView {

	private View.OnTouchListener touchListener;
	
	public MyGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setTouchListener(View.OnTouchListener listener) {
		this.touchListener = listener;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (touchListener != null) {
			return touchListener.onTouch(this, event);
		}
		return super.onTouchEvent(event);
	}
	

}
