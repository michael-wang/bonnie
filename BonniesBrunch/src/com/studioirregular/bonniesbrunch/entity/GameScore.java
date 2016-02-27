package com.studioirregular.bonniesbrunch.entity;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.component.DecimalDigit.TextureConfig;
import com.studioirregular.bonniesbrunch.component.RenderComponent;

public class GameScore extends GameEntity {

//	private static final String TAG = "game-score";
	
	public GameScore(int zOrder) {
		super(zOrder);
	}
	
	public void setup(float x, float y, float width, float height, int maxDigits) {
		super.setup(x, y, width, height);
		
		RenderComponent dollarSign = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		dollarSign.setup(DOLLAR_SIGN_WIDTH, DOLLAR_SIGN_HEIGHT);
		dollarSign.setup("number_symbol_money_s");
		add(dollarSign);
		
		number = new DecimalNumber(MIN_GAME_ENTITY_Z_ORDER);
		TextureConfig texConfig = new TextureConfig("head-up-display");
		setupTexturePartitions(texConfig);
		number.setup(x + DOLLAR_SIGN_WIDTH + 5, y, 
				DOLLAR_SIGN_WIDTH * maxDigits, DOLLAR_SIGN_HEIGHT, 
				maxDigits, DOLLAR_SIGN_WIDTH, 
				DOLLAR_SIGN_HEIGHT, texConfig);
		number.setNewValue(0);
		add(number);
	}
	
	@Override
	public void reset() {
		score = 0;
		onScoreChanged();
		super.reset();
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		switch (event.what) {
		case GameEvent.GAME_SCORE_ADD:
		case GameEvent.GAME_SCORE_CLEAR:
			return true;
		}
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
//		Log.d(TAG, "handleGameEvent event:" + event);
		
		if (GameEvent.GAME_SCORE_ADD == event.what) {
			score += event.arg1;
			onScoreChanged();
		} else if (GameEvent.GAME_SCORE_CLEAR == event.what) {
			score = 0;
			onScoreChanged();
		}
	}
	
	private void onScoreChanged() {
		if (Config.LEVEL_SCORE_DO_ANIMATION) {
			number.animateToNewValue(score, Config.LEVEL_SCORE_ANIMATION_DURATION);
		} else {
			number.setNewValue(score);
		}
	}
	
	private void setupTexturePartitions(TextureConfig config) {
		final String partitionNameBase = "number_s_";
		for (int i = 0; i < 10; i++) {
			config.addPartition(i, partitionNameBase + Integer.toString(i));
		}
	}
	
	private static final float DOLLAR_SIGN_WIDTH = 20;
	private static final float DOLLAR_SIGN_HEIGHT = 26;
	
	private int score;
	
	private DecimalNumber number;

}
