package com.studioirregular.bonniesbrunch.main;

import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.ContextParameters;
import com.studioirregular.bonniesbrunch.Game;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.GameScoreSystem;
import com.studioirregular.bonniesbrunch.LevelSystem;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelLockState;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelNumber;
import com.studioirregular.bonniesbrunch.SoundSystem;
import com.studioirregular.bonniesbrunch.component.DecimalDigit.TextureConfig;
import com.studioirregular.bonniesbrunch.component.RenderComponent;
import com.studioirregular.bonniesbrunch.entity.ButtonEntity;
import com.studioirregular.bonniesbrunch.entity.DecimalNumber;


// need load param: level (Integer)
public class RootSelectMinorLevel extends GameEntityRoot {

	private static final String TAG = "root-select-minor-level";
	
	public RootSelectMinorLevel(Game game) {
		super(game);
	}
	
	@Override
	public boolean load() {
		majorLevel = (Integer)loadParam;
		if (Config.DEBUG_LOG) Log.d(TAG, "load levelMajor:" + majorLevel);
		
		setup(0, 0, ContextParameters.getInstance().gameWidth, ContextParameters.getInstance().gameHeight);
		setupBackground();
		setupLevelObjects();
		setupBackButton();
		setupTotalLevelScore();
		
		SoundSystem.getInstance().playMusic("bgm_menu", true);
		
		return true;
	}
	
	@Override
	protected void onReadyToGo() {
		super.onReadyToGo();
		
		game.getAd().hideBanner();
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		return super.wantThisEvent(event);
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		super.handleGameEvent(event);
		
		if (GameEvent.LEVEL_MINOR_SELECTED == event.what) {
			doFadeOut(event);
		} else if (GameEvent.MENU_BACK == event.what)  {
			onBack();
		}
	}
	
	@Override
	protected boolean handlePendingEvent(GameEvent event) {
		if (GameEvent.LEVEL_MINOR_SELECTED == event.what) {
			int minorLevel = event.arg1;
			game.gotoGameLevel(new LevelNumber(majorLevel, minorLevel));
			return true;
		} else if (GameEvent.ROOT_BACK == event.what) {
			onFinish();
			return true;
		}
		return false;
	}
	
	private void setupBackground() {
		RenderComponent scoreBG = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		scoreBG.setup(720, 480);
		scoreBG.setup("sub_level_menu_bg");
		add(scoreBG);
	}
	
	private void setupLevelObjects() {
		// title
		RenderComponent render = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
		if (majorLevel == 5) {
			render.setup(235, 44);
			render.setupOffset(243, 32);
		} else {
			render.setup(197, 44);
			render.setupOffset(260, 32);
		}
		render.setup(getTitleTexture(majorLevel));
		add(render);
		
		LevelLockState[] lockStates = new LevelLockState[10];
		LevelSystem.getLevelLockStates(majorLevel, lockStates);
		
		// level buttons 
		for (int i = 0; i < 10; i++) {
			final boolean specialLevel = ((i + 1) % 5 == 0);
			setupLevelButton(i + 1, specialLevel, lockStates[i] != LevelLockState.UNLOCK);
		}
	}
	
	// minorLevel: 1 - 10
	private void setupLevelButton(int minorLevel, boolean specialLevel, boolean lock) {
		if (Config.DEBUG_LOG) Log.d(TAG, "setupLevelButton minorLevel:" + minorLevel + ",specialLevel:" + specialLevel + ",lock:" + lock);
		
		final float x = 57 + ((minorLevel-1) % 5) * 123;
		final float y = (specialLevel ? 75 : 82) + (minorLevel > 5 ? 1 : 0) * 165;
		final String texture;
		if (lock) {
			texture = specialLevel ? "sub_level_menu_spsq_disable" : "sub_level_menu_sq_disable";
		} else {
			texture = specialLevel ? "sub_level_menu_spsq_enable" : "sub_level_menu_sq_enable";
		}
		
		ButtonEntity.Builder builder = new ButtonEntity.Builder(x, y, 105, 105, texture, texture);
		builder.playSoundWhenClicked("mainmenu_bt_s1");
		builder.emitEventWhenPressed(GameEvent.LEVEL_MINOR_SELECTED, minorLevel);
		builder.offsetTouchArea(-8, -16, 8, 16);
		
		LevelButton button = new LevelButton(MIN_GAME_ENTITY_Z_ORDER, builder);
		button.setup(minorLevel, specialLevel, lock);
		add(button);
	}
	
	private void setupBackButton() {
		ButtonEntity.Builder builder = new ButtonEntity.Builder(9, 380, 99, 98, "sub_level_menu_left_normal", "sub_level_menu_left_normal");
		builder.playSoundWhenClicked("mainmenu_bt_s1");
		builder.emitEventWhenPressed(GameEvent.MENU_BACK);
		builder.offsetTouchArea(-16, -16, 8, 8);
		ButtonEntity back = new ButtonEntity(MIN_GAME_ENTITY_Z_ORDER, builder);
		add(back);
	}
	
	private String getTitleTexture(int level) {
		switch (level) {
		case 1:
			return "sub_level_menu_ep1";
		case 2:
			return "sub_level_menu_ep2";
		case 3:
			return "sub_level_menu_ep3";
		case 4:
			return "sub_level_menu_ep4";
		case 5:
			return "sub_level_menu_ep5";
		}
		return "";
	}
	
	private class LevelButton extends ButtonEntity {

		public LevelButton(int zOrder, Builder builder) {
			super(zOrder, builder);
		}
		
		public void setup(int minorLevel, boolean isSpecialLevel, boolean lock) {
			setDisable(lock);
			
			// lock level does not show level number.
			if (lock) {
				return;
			}
			
			RenderComponent number = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
			number.setup(27, 30);
			if (minorLevel == 10) {
				number.setup(getNumberTexture(1));
				number.setupOffset(25, 37);
			} else {
				number.setup(getNumberTexture(minorLevel));
				number.setupOffset(isSpecialLevel ? 39 : 33, isSpecialLevel ? 37 : 32);
			}
			add(number);
			
			// add 0 for level 10
			if (minorLevel == 10) {
				number = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
				number.setup(27, 30);
				number.setup(getNumberTexture(0));
				number.setupOffset(52, 37);
				add(number);
			}
			
			if (levelWorkspace.set(majorLevel, minorLevel)) {
				int starCount = GameScoreSystem.getInstance().getMinorLevelStar(levelWorkspace);
				setupStars(starCount, 2, 106, 31);
			}
		}
		
		// number: 1-9, 0
		private String getNumberTexture(int number) {
			if (number < 0 || 9 < number) {
				return "";
			}
			return "sub_level_menu_number_" + Integer.toString(number);
		}
		
		private void setupStars(int starCount, float offsetX, float offsetY, float intervalX) {
			for (int i = 0; i < starCount; i++) {
				RenderComponent star = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
				star.setup(31, 31);
				star.setup("sub_level_menu_star");
				star.setupOffset(offsetX + intervalX * i,  offsetY);
				add(star);
			}
		}
	}
	
	private void setupTotalLevelScore() {
		int score = GameScoreSystem.getInstance().getMajorLevelScore(majorLevel);
		
		DecimalNumber number = new DecimalNumber(MIN_GAME_ENTITY_Z_ORDER);
		TextureConfig texConfig = new TextureConfig("head-up-display");
		setupScoreTexturePartitions(texConfig);
		final float DIGIT_WIDTH = 20;
		final float DIGIT_HEIGHT = 26;
		number.setup(255, 394, DIGIT_WIDTH * 6, DIGIT_HEIGHT, 
				6, DIGIT_WIDTH, DIGIT_HEIGHT, texConfig);
		number.setNewValue(score);
		add(number);
	}
	
	private void setupScoreTexturePartitions(TextureConfig config) {
		final String partitionNameBase = "number_s_";
		for (int i = 0; i < 10; i++) {
			config.addPartition(i, partitionNameBase + Integer.toString(i));
		}
	}
	
	private void onFinish() {
		game.gotoMajorLevelSelection();
	}
	
	private int majorLevel;
	private LevelNumber levelWorkspace = new LevelNumber();
}
