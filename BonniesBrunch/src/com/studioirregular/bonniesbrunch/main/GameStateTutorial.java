package com.studioirregular.bonniesbrunch.main;

import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.SoundSystem;

public class GameStateTutorial extends GameState {

	private static final String TAG = "game-state-tutorial";
	
	public GameStateTutorial(GameStateHolder holder) {
		super(holder);
	}
	
	@Override
	public void start() {
		holder.sceneManager().loadTutorial(holder.gameLevel().number);
		holder.customerManager().setTutorialMode(true);
		holder.sceneManager().next();	// kick start!
		
		playMusic();
	}
	
	@Override
	public void playMusic() {
		SoundSystem.getInstance().playMusic("bgm_tutorial", true);
	}
	
	@Override
	public boolean allowDispatchTouchEvent() {
		return true;
	}
	
	@Override
	public boolean wantThisEvent(GameEvent event) {
		if (Config.DEBUG_LOG) Log.d(TAG, "wantThisEvent event:" + event);
		
		// for scene node to handle event map.
		holder.sceneManager().peekGameEvent(event);
		
		switch (event.what) {
		case GameEvent.SCENE_MANAGER_NEXT_NODE:
		case GameEvent.SCENE_MANAGER_REQUEST_SKIP:
		case GameEvent.SCENE_MANAGER_SCENE_END:
			return true;
		}
		return false;
	}
	
	@Override
	public void handleGameEvent(GameEvent event) {
		if (Config.DEBUG_LOG) Log.d(TAG, "handleGameEvent event:" + event);
		
		if (GameEvent.SCENE_MANAGER_NEXT_NODE == event.what) {
			holder.sceneManager().next();
		} else if (GameEvent.SCENE_MANAGER_REQUEST_SKIP == event.what) {
			holder.customerManager().removeAllCustomers();
			holder.sceneManager().jumpToEnd();
		} else if (GameEvent.SCENE_MANAGER_SCENE_END == event.what) {
			holder.requestFadeOut(event);
		}
	}
	
	@Override
	public boolean handlePendingEvent(GameEvent pendingEvent) {
		if (GameEvent.SCENE_MANAGER_SCENE_END == pendingEvent.what) {
			onTutorialEnd();
			return true;
		}
		return false;
	}
	
	private void onTutorialEnd() {
		holder.resetLevelState();
		
		GameState nextState = new GameStateRunning(holder);
		holder.changeGameState(nextState);
	}

}
