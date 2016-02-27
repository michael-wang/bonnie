package com.studioirregular.bonniesbrunch.main;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.Game;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.GameScoreSystem;
import com.studioirregular.bonniesbrunch.LevelSystem.GameLevel;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelTime;
import com.studioirregular.bonniesbrunch.LevelSystem.RushHour;
import com.studioirregular.bonniesbrunch.SoundSystem;
import com.studioirregular.bonniesbrunch.SoundSystem.Sound;
import com.studioirregular.bonniesbrunch.base.ObjectBase;

public class GameStateRunning extends GameState {

	private static final String TAG = "game-state-running";
	
	public GameStateRunning(GameStateHolder holder) {
		super(holder);
	}
	
	@Override
	public void start() {
		if (Config.DEBUG_LOG) Log.d(TAG, "start");
		
		playMusic();
		soundRushHour = SoundSystem.getInstance().load("game_text_3_rushtime_s1");
		
		holder.sceneManager().loadLevelRunning();
		holder.sceneManager().next();	// kick start!
		
		holder.releaseTempTexture();
		
		Game.getInstance().restoreEffectFadeOut();
	}
	
	@Override
	public void playMusic() {
		if (holder.gameLevel().specialLevel) {
			SoundSystem.getInstance().playMusic("bgm_sp", true);
		} else {
			SoundSystem.getInstance().playMusic("bgm_game", true);
		}
	}
	
	@Override
	public void update(long timeDelta, ObjectBase parent) {
		super.update(timeDelta, parent);
		
		if (!clockStarted) {
			return;
		}
		
		levelElapsedTime += timeDelta;
//		gameTime.setNewValue(levelElapsedTime/1000);
		
		// check if time up.
		if (levelTotalTime <= levelElapsedTime) {
			GameLevel level = holder.gameLevel();
			
			if (level.specialLevel == false) {
				if (holder.customerManager().isAnyPendingCustomer() == false) {
					onLevelTimeUp();
				}
			} else {
				onLevelTimeUp();
			}
		}
		
		for (int i = rushHourTimeOffset.size()-1; i >= 0; i--) {
			final int rushTime = rushHourTimeOffset.get(i);
			if (rushTime <= levelElapsedTime) {
				showGameText("game_text_3_rushtime", 387, 110, 166, 185, 0);
				
				SoundSystem.getInstance().playSound(soundRushHour, false);
				
				rushHourTimeOffset.remove(i);
				break;
			}
		}
	}
	
	@Override
	public boolean allowDispatchTouchEvent() {
		return true;
	}
	
	@Override
	public boolean wantThisEvent(GameEvent event) {
		if (Config.DEBUG_LOG) Log.d(TAG, "wantThisEvent event:" + event);
		
		switch (event.what) {
		case GameEvent.GAME_RESTART:
		case GameEvent.GAME_BACK_TO_MENU:
		case GameEvent.GAME_CONFIRM_YES:
		case GameEvent.GAME_CONFIRM_NO:
		case GameEvent.GAME_CLOCK_START:
		case GameEvent.GAME_CLOCK_END:
		case GameEvent.SCENE_MANAGER_NEXT_NODE:
		case GameEvent.SCENE_MANAGER_SCENE_END:
			return true;
		}
		return false;
	}
	
	@Override
	public void handleGameEvent(GameEvent event) {
		if (Config.DEBUG_LOG) Log.d(TAG, "handleGameEvent event:" + event);
		
		if (GameEvent.GAME_BACK_TO_MENU == event.what) {
			if (event.arg1 == 1) {
				holder.onConfirmingEvent(event);
			} else {
				Game.getInstance().gotoMainMenu();
			}
		} else if (GameEvent.GAME_RESTART == event.what) {
			if (event.arg1 == 1) {
				holder.onConfirmingEvent(event);
			} else {
				Game.getInstance().gotoGameLevel(holder.gameLevel().number);
			}
		} else if (GameEvent.GAME_CONFIRM_NO == event.what) {
			holder.onConfirmingEventRejected();
		} else if (GameEvent.GAME_CONFIRM_YES == event.what) {
			holder.onConfirmingEventAccepted();
		} else if (GameEvent.GAME_CLOCK_START == event.what ||
				GameEvent.GAME_CLOCK_END == event.what) {
			holder.propagate(event);
		} else if (GameEvent.SCENE_MANAGER_NEXT_NODE == event.what) {
				holder.sceneManager().next();
		} else if (GameEvent.SCENE_MANAGER_SCENE_END == event.what) {
			onLevelStart();
		}
	}
	
	@Override
	public boolean handlePendingEvent(GameEvent pendingEvent) {
		return false;
	}
	
	private void onLevelStart() {
		if (Config.DEBUG_LOG) Log.d(TAG, "letsGo");
		
		// for debug game clock
/*		gameTime = new DecimalNumber(GameEntity.MIN_GAME_ENTITY_Z_ORDER + 0x10);
		TextureConfig texConfig = new TextureConfig("head-up-display");
		setupTexturePartitions(texConfig);
		gameTime.setup(16, 70, 20*3, 26, 3, 20, 26, texConfig);
		gameTime.setNewValue(0);
		holder.add(gameTime);
		*/
		final LevelTime levelTime = holder.gameLevel().time;
		
		levelTotalTime = levelTime.total * 1000;
		levelElapsedTime = 0;
		
		List<RushHour> rushHours = holder.gameLevel().time.rushHours;
		final int TIME_STEP = levelTime.total * 1000 / 12;
		
		for (RushHour rushHour : rushHours) {
			rushHourTimeOffset.add(rushHour.rushClockIndex * TIME_STEP);
		}
		
		holder.disablePauseButton(false);
		holder.customerManager().onLevelStart();
		GameScoreSystem.getInstance().clear();
		
		GameEventSystem.scheduleEvent(GameEvent.GAME_CLOCK_START);
		clockStarted = true;
	}
	
//	private void setupTexturePartitions(TextureConfig config) {
//		final String partitionNameBase = "number_s_";
//		for (int i = 0; i < 10; i++) {
//			config.addPartition(i, partitionNameBase + Integer.toString(i));
//		}
//	}
	
	private void onLevelTimeUp() {
		holder.disablePauseButton(true);
		holder.customerManager().onLevelStop();
		
		// drop brunch if user is dragging it around.
		holder.table().send(GameEventSystem.getInstance().obtain(GameEvent.BRUNCH_TIME_TO_LEAVE));
		
		// time up! don't let user cook any food.
		holder.table().setDisable(true);
		
		holder.changeGameState(new GameStateEnd(holder));
	}
	
	private boolean clockStarted = false;
	private List<Integer> rushHourTimeOffset = new ArrayList<Integer>();	// ms
	private int levelTotalTime;
	private int levelElapsedTime;
//	private DecimalNumber gameTime;	// TODO: remove this for release.
	private Sound soundRushHour;
}
