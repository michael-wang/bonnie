package com.studioirregular.bonniep1;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;

public class MyRenderer implements Renderer {

	private static final String TAG = "renderer";
	private static final boolean DO_LOG = false;
	
	private BonnieProtoIActivity activity;
	private FrameRateCalculator fpsCalculator;
	private float fps;
	private Scene scene;
	private float width, height;
	
	public MyRenderer(BonnieProtoIActivity activity) {
		this.activity = activity;
		fpsCalculator = new FrameRateCalculator();
	}
	
	public void setScene(Scene scene) {
		this.scene = scene;
		scene.setSurfaceDimension(width, height);
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		fpsCalculator.start();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if (height == 0) {
			height = 1;
		}
		this.width = width;
		this.height = height;
		
		if (scene != null) {
			scene.setSurfaceDimension(width, height);
		}
		
		setupViewport(gl, width, height);
		
		final boolean isLandscape = width >= height;
		float surfaceLeft, surfaceRight, surfaceTop, surfaceBottom;
		if (isLandscape) {
			surfaceTop = Scene.HEIGHT / 2;
			surfaceBottom = -surfaceTop;
			surfaceRight = Scene.WIDTH / 2;
			surfaceLeft = -surfaceRight;
		} else {
			surfaceRight = Scene.WIDTH / 2;
			surfaceLeft = -surfaceRight;
			surfaceTop = Scene.HEIGHT / 2;
			surfaceBottom = -surfaceTop;
		}
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, surfaceLeft, surfaceRight, surfaceBottom, surfaceTop);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		if (scene != null) {
			if (scene.isLoaded() == false) {
				scene.load(gl);
			}
			scene.update();
			scene.draw(gl);
		}
		
		fpsCalculator.addFrame();
		if (fps != fpsCalculator.getFPS()) {
			fps = fpsCalculator.getFPS();
			activity.updateFPS(fps);
		}
	}
	
	private void setupViewport(GL10 gl, int width, int height) {
		int viewportWidth, viewportHeight;
		final boolean isLandscape = width >= height;
		float sceneAspect = isLandscape ? Scene.WIDTH / Scene.HEIGHT : Scene.HEIGHT / Scene.WIDTH;
		
		if (isLandscape) {
			viewportWidth = (int)((float)height * sceneAspect);
			viewportHeight = height;
		} else {
			viewportWidth = width;
			viewportHeight = (int)((float)width * sceneAspect);
		}
		
		if (DO_LOG) Log.d(TAG, "setupViewport viewportWidth:" + viewportWidth + ",viewportHeight:" + viewportHeight);
		gl.glViewport((width - viewportWidth) / 2, (height - viewportHeight) / 2, 
				viewportWidth, viewportHeight);
	}

}
