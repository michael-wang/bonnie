package com.studioirregular.bonniesbrunch.main;

import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.base.ObjectBase;

public class GameStateStart extends GameState {

	private static final String TAG = "game-state-start";
	
	public GameStateStart(GameStateHolder holder) {
		super(holder);
	}
	
	@Override
	public void start() {
		holder.disablePauseButton(true);
		
		holder.sceneManager().loadLevelStart();
		holder.sceneManager().next();	// kick start!
	}
	
	@Override
	public void playMusic() {
	}
	
	@Override
	public void update(long timeDelta, ObjectBase parent) {
		super.update(timeDelta, parent);
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
		} else if (GameEvent.SCENE_MANAGER_SCENE_END == event.what) {
			if (holder.gameLevel().hasTutorial) {
				holder.changeGameState(new GameStateTutorial(holder));
			} else {
				holder.changeGameState(new GameStateRunning(holder));
			}
		}
	}
	
	@Override
	public boolean handlePendingEvent(GameEvent pendingEvent) {
		return false;
	}
	
}
