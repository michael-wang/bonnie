package com.studioirregular.bonniesbrunch;

import android.util.Log;

import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.InputSystem.TouchEvent;
import com.studioirregular.bonniesbrunch.main.GameEntityRoot;


public class GameThread extends Thread {
	
	private static final String TAG = "game-thread";
	
	public GameThread(String threadName, GameRenderer renderer) {
		super(threadName);
		
		if (Config.DEBUG_LOG) Log.d(TAG, "GameThread created threadName:" + threadName);
		this.renderer = renderer;
	}
	
	public void setRoot(GameEntityRoot root) {
		if (Config.DEBUG_LOG) Log.d(TAG, "setRoot root:" + root);
		
		this.newRoot = root;
		rootLoaded = false;
	}
	
	public void startGame() {
		if (Config.DEBUG_LOG) Log.d(TAG, "startGame");
		finished = false;
	}
	
	public void stopGame() {
		if (Config.DEBUG_LOG) Log.d(TAG, "stopGame");
		
		synchronized (pauseLock) {
			finished = true;
			paused = false;
			pauseLock.notifyAll();
		}
	}
	
	public void pauseGame() {
		if (Config.DEBUG_LOG) Log.d(TAG, "pauseGame");
		
		synchronized (pauseLock) {
			paused = true;
		}
	}
	
	public void resumeGame() {
		if (Config.DEBUG_LOG) Log.d(TAG, "resumeGame");
		
		synchronized (pauseLock) {
			paused = false;
			pauseLock.notifyAll();
		}
	}
	
	@Override
	public void run() {
		if (Config.DEBUG_LOG) Log.d(TAG, "run!");
		
		lastFrameTime = System.currentTimeMillis();
		
		while(!finished) {
			renderer.waitDrawFrame();
			
			final long frameStartTime = System.currentTimeMillis();
			long timeDelta = frameStartTime - lastFrameTime;
			lastFrameTime = frameStartTime;
			
			if (root != null) {
				if (!rootLoaded) {
					root.load();
					rootLoaded = true;
					
					root.onLoadComplete();
				}
				
				// dispatch touch events
				TouchEvent event = InputSystem.getInstance().next();
				while (event != null) {
					root.dispatch(event, null);
					event = InputSystem.getInstance().next();
				}
				
				// dispatch game events
				GameEvent gEvent = GameEventSystem.getInstance().next();
				while (gEvent != null) {
					root.dispatch(gEvent, null);
					gEvent = GameEventSystem.getInstance().next();
				}
				
				// update game graph.
				root.update(timeDelta, null);
			}
			
			RenderSystem.getInstance().swap(renderer);
			
			if (newRoot != null) {
				root = newRoot;
				newRoot = null;
			}
			
			final long timeDiff = System.currentTimeMillis() - frameStartTime;
			if (Config.DEBUG_LOG_FRAME_TIME) Log.w(TAG, "game thread frame timeDiff:" + timeDiff);
			
			synchronized (pauseLock) {
				if (paused) {
					while (paused) {
						try {
							pauseLock.wait();
						} catch (InterruptedException e) {
							;
						}
					}
				}
			}	// synchronized (pauseLock)
		}
		if (Config.DEBUG_LOG) Log.w(TAG, "game thread about terminates");
	}
	
	private GameRenderer renderer;
	private GameEntityRoot root;
	private GameEntityRoot newRoot;
	private boolean rootLoaded = false;
	private boolean finished = false;
	private boolean paused = false;
	private Object pauseLock = new Object();
	private long lastFrameTime;

}
