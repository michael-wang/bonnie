package com.studioirregular.glbenchj;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class MyGLSurfaceView extends GLSurfaceView {

	private static final String TAG = "view";
	
	private MyRenderer renderer;
	
	public MyGLSurfaceView(Context context) {
		super(context);
	}
	
	@Override
	public void setRenderer(Renderer renderer) {
		super.setRenderer(renderer);
		this.renderer = (MyRenderer)renderer;
	}
	
}
