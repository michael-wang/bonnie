package com.studioirregular.bonniep2;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class Game implements View.OnTouchListener {
	
	private static final String TAG = "game";
	
	private MyGLSurfaceView view;
	private MyRenderer renderer;
	private GameHandler handler;
	private SceneBase scene;
	
	public Game(MyGLSurfaceView view, MyRenderer renderer, GameHandler handler) {
		this.view = view;
		this.renderer = renderer;
		this.handler = handler;
	}
	
	public boolean setSceneConfig(SceneConfig config) {
		Class<? extends SceneParser> clazz = config.parserClass;
		Constructor<? extends SceneParser> cont = null;
		try {
			cont = clazz.getConstructor(Integer.TYPE);
		} catch (SecurityException e) {
			Log.e(TAG, "failed to generate scene class:" + clazz.getName() + ", e:" + e);
			return false;
		} catch (NoSuchMethodException e) {
			Log.e(TAG, "failed to generate scene class:" + clazz.getName() + ", e:" + e);
			return false;
		}
		
		SceneParser parser = null;
		try {
			parser =  cont.newInstance(config.levelConfigId);
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "failed to generate scene class:" + clazz.getName() + ", e:" + e);
			return false;
		} catch (InstantiationException e) {
			Log.e(TAG, "failed to generate scene class:" + clazz.getName() + ", e:" + e);
			return false;
		} catch (IllegalAccessException e) {
			Log.e(TAG, "failed to generate scene class:" + clazz.getName() + ", e:" + e);
			return false;
		} catch (InvocationTargetException e) {
			Log.e(TAG, "failed to generate scene class:" + clazz.getName() + ", e:" + e);
			return false;
		}
		
		MainScene scene = new MainScene(this);
		scene.setTextureResource(config.textureConfigResourceId);
		scene.setSceneParser(parser);
		this.scene = scene;
		
		return true;
	}
	
	public void start() {
		renderer.setScene(scene);
		view.setOnTouchListener(this);
	}
	
	public void stop() {
		renderer.stop();
	}
	
	public void gameOver() {
		if (handler != null) {
			handler.sendEmptyMessage(GameHandler.MSG_GAME_OVER);
		}
	}
	
	public GameHandler getHandler() {
		return handler;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (scene != null) {
			scene.onTouch(event);
		}
		return true;
	}
}
