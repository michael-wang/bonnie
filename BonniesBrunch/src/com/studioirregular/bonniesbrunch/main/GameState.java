package com.studioirregular.bonniesbrunch.main;

import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.LevelSystem.GameLevel;
import com.studioirregular.bonniesbrunch.base.ObjectBase;
import com.studioirregular.bonniesbrunch.component.AnimationSet;
import com.studioirregular.bonniesbrunch.component.HoldAnimation;
import com.studioirregular.bonniesbrunch.component.TranslateAnimation;
import com.studioirregular.bonniesbrunch.entity.CustomerManager;
import com.studioirregular.bonniesbrunch.entity.GameEntity;
import com.studioirregular.bonniesbrunch.entity.HeadUpDisplay;
import com.studioirregular.bonniesbrunch.entity.LevelReport;
import com.studioirregular.bonniesbrunch.entity.SimpleEntity;
import com.studioirregular.bonniesbrunch.entity.Table;

public abstract class GameState {
	
	public static interface GameStateHolder {
		public GameLevel gameLevel();
		public SceneManager sceneManager();
		public HeadUpDisplay headUpDisplay();
		public Table table();
		public CustomerManager customerManager();
		public LevelReport levelReport();
		
		public void add(ObjectBase obj);
		public void remove(ObjectBase obj);
		public void propagate(GameEvent event);
		
		public void disablePauseButton(boolean disable);
		public void changeGameState(GameState newState);
		public void onGamePause(boolean paused);
		public void onConfirmingEvent(GameEvent event);
		public void onConfirmingEventRejected();
		public void onConfirmingEventAccepted();
		
		// restore entity state to level starting state.
		public void resetLevelState();
		public void releaseTempTexture();
		public void requestFadeOut(GameEvent pendingEvent);
	}
	
	public GameState(GameStateHolder holder) {
		this.holder = holder;
	}
	
	public void update(long timeDelta, ObjectBase parent) {
		if (!boot) {
			start();
			boot = true;
		}
	}
	
	public abstract boolean wantThisEvent(GameEvent event);
	public abstract void handleGameEvent(GameEvent event);
	public abstract boolean handlePendingEvent(GameEvent pendingEvent);
	
	protected void showGameText(String texture, float width, float height, float x, float y, int entityDoneCode) {
		if (gameText != null) {
			holder.remove(gameText);
		}
		
		gameText = new SimpleEntity(GameEntity.MIN_GAME_ENTITY_Z_ORDER + 3);
		gameText.setup(-width, y, width, height, texture);
		gameText.installEventMap(
				GameEventSystem.getInstance().obtain(GameEvent.ANIMATION_END, 0),
				GameEventSystem.getInstance().obtain(GameEvent.ENTITY_DONE, entityDoneCode));
		holder.add(gameText);
		
		AnimationSet set = new AnimationSet(GameEntity.MIN_GAME_COMPONENT_Z_ORDER);
		set.removeSelfWhenFinished(true);
		set.notifyWhenFinished(true, entityDoneCode);
		
		TranslateAnimation translate = new TranslateAnimation(GameEntity.MIN_GAME_COMPONENT_Z_ORDER, -width, y, x, y, 300);
		set.addAnimation(translate);
		
		HoldAnimation hold = new HoldAnimation(GameEntity.MIN_GAME_COMPONENT_Z_ORDER);
		hold.setDuration(1000);
		set.addAnimation(hold);
		
		translate = new TranslateAnimation(GameEntity.MIN_GAME_COMPONENT_Z_ORDER, x, y, 720, y, 300);
		set.addAnimation(translate);
		
		set.start();
		gameText.add(set);
		
		gameTextElapsed = 1600;
	}
	
	public abstract void start();
	public abstract boolean allowDispatchTouchEvent();
	public abstract void playMusic();
	
	protected boolean boot = false;
	protected GameStateHolder holder;
	
	protected SimpleEntity gameText;
	protected int gameTextElapsed;
}
