package com.studioirregular.bonniesbrunch.entity;

import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.LevelSystem.GameLevel;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelScore;
import com.studioirregular.bonniesbrunch.SoundSystem;
import com.studioirregular.bonniesbrunch.SoundSystem.Sound;
import com.studioirregular.bonniesbrunch.component.AnimationSet;
import com.studioirregular.bonniesbrunch.component.FrameAnimationComponent;
import com.studioirregular.bonniesbrunch.component.RenderComponent;

public class ScoreBar extends GameEntity {

	private static final String TAG = "score-bar";
	
	public ScoreBar(int zOrder) {
		super(zOrder);
	}
	
	public void setup(float x, float y, float width, float height, GameLevel gameLevel) {
		super.setup(x, y, width, height);
		
		this.gameLevel = gameLevel;
		score = 0;
		
		RenderComponent bg = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		bg.setup(288, 38);
		bg.setup(gameLevel.number.major < 2 ? "game_money_bar_empty_ep1" : "game_money_bar_empty_ep2");
		add(bg);
		
		barLv1 = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
		barLv1.setup(BAR_WIDTH, 15);
		barLv1.setup("game_money_bar_lv1");
		barLv1.setupOffset(69, 12);
		
		barLv2 = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
		barLv2.setup(BAR_WIDTH, 15);
		barLv2.setup("game_money_bar_lv2");
		barLv2.setupOffset(69, 12);
		barLv2.changeWidth(0);
		
		barLv3 = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
		barLv3.setup(BAR_WIDTH, 15);
		barLv3.setup("game_money_bar_lv3");
		barLv3.setupOffset(69, 12);
		barLv3.changeWidth(0);
		
		barFull = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
		barFull.setup(BAR_WIDTH, 15);
		barFull.setup("game_money_bar_full");
		barFull.setupOffset(69, 12);
		barFull.changeWidth(0);
		
		currentBar = null;
		
		stars = new RenderComponent[3];
		for (int i = 0; i < 3; i++) {
			RenderComponent star = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
			star.setup(18, 18);
			star.setup("game_money_bar_star");
			star.setupOffset(STAR_OFFSET_X[i], 10);
			star.setVisible(false);	// default hide.
			
			add(star);
			stars[i] = star;
		}
		starCount = 0;
		
		soundAddStar = SoundSystem.getInstance().load("report_menu_star_s1");
		onScoreChanged();
		
		addStarAnimation = new FrameAnimationComponent(MIN_GAME_COMPONENT_Z_ORDER + 2);
		addStarAnimation.addFrame("game_event_4_money_bar_star_001", 90, 96, 50);
		addStarAnimation.addFrame("game_event_4_money_bar_star_002", 90, 96, 50);
		addStarAnimation.addFrame("game_event_4_money_bar_star_003", 90, 96, 50);
		addStarAnimation.addFrame("game_event_4_money_bar_star_004", 90, 96, 50);
		addStarAnimation.addFrame("game_event_4_money_bar_star_005", 90, 96, 50);
		addStarAnimation.addFrame("game_event_4_money_bar_star_006", 90, 96, 50);
		addStarAnimation.addFrame("game_event_4_money_bar_star_007", 90, 96, 50);
		addStarAnimation.addFrame("game_event_4_money_bar_star_008", 90, 96, 50);
		addStarAnimation.setFillBefore(false);
		
		flameAnimation = new FrameAnimationComponent(MIN_GAME_COMPONENT_Z_ORDER + 2);
		flameAnimation.addFrame("report_event_1_star_001", 315, 267, 50);
		flameAnimation.addFrame("report_event_1_star_002", 315, 267, 100);
		flameAnimation.addFrame("report_event_1_star_003", 315, 267, 100);
		flameAnimation.addFrame("report_event_1_star_004", 315, 267, 100);
		flameAnimation.addFrame("report_event_1_star_005", 315, 267, 100);
		flameAnimation.addFrame("report_event_1_star_006", 315, 267, 100);
		flameAnimation.addFrame("report_event_1_star_007", 315, 267, 100);
		flameAnimation.setFillAfter(false);
		
		addStarEffect = new AnimationSet(MIN_GAME_COMPONENT_Z_ORDER + 2);
		addStarEffect.addAnimation(addStarAnimation);
		addStarEffect.addAnimation(flameAnimation);
		add(addStarEffect);
	}
	
	@Override
	public void reset() {
		super.reset();
		score = 0;
		onScoreChanged();
		onStarCountChanged();
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
		if (GameEvent.GAME_SCORE_ADD == event.what) {
			score += event.arg1;
			onScoreChanged();
		} else if (GameEvent.GAME_SCORE_CLEAR == event.what) {
			score = 0;
			onScoreChanged();
		}
	}
	
	private void onScoreChanged() {
		if (Config.DEBUG_LOG) Log.d(TAG, "onScoreChanged score:" + score);
		
		final LevelScore levelScore = gameLevel.score;
		RenderComponent newBar = null;
		int newStarCount = -1;
		
		if (score < levelScore.min) {
			newBar = barLv1;
			final float newWidth = BAR_WIDTH * score / levelScore.max;
			newBar.changeWidth(newWidth);
			newBar.offsetTextureX(false, newWidth - BAR_WIDTH);
			newStarCount = 0;
		} else if (score < levelScore.med) {
			newBar = barLv2;
			final float newWidth = BAR_WIDTH * score / levelScore.max;
			newBar.changeWidth(newWidth);
			newBar.offsetTextureX(false, newWidth - BAR_WIDTH);
			newStarCount = 1;
		} else if (score < levelScore.high) {
			newBar = barLv3;
			final float newWidth = BAR_WIDTH * score / levelScore.max;
			newBar.changeWidth(newWidth);
			newBar.offsetTextureX(false, newWidth - BAR_WIDTH);
			newStarCount = 2;
		} else {
			newBar = barFull;
			if (score > levelScore.max) {
				score = levelScore.max;
			}
			final float newWidth = BAR_WIDTH * score / levelScore.max;
			newBar.changeWidth(newWidth);
			newBar.offsetTextureX(false, newWidth - BAR_WIDTH);
			newStarCount = 3;
		}
		
		if (newBar != currentBar) {
			if (currentBar != null) {
				remove(currentBar);
			}
			currentBar = newBar;
			add(newBar);
		}
		
		if (newStarCount != starCount) {
			starCount = newStarCount;
			onStarCountChanged();
			
			doStarEffect();
			SoundSystem.getInstance().playSound(soundAddStar, false);
			
			if (newStarCount > 0) {
				GameEventSystem.scheduleEvent(GameEvent.GAME_SCORE_LEVEL_UP, newStarCount);
			}
		}
	}
	
	private void onStarCountChanged() {
		if (Config.DEBUG_LOG) Log.d(TAG, "onStarCountChanged starCount:" + starCount);
		
		for (int i = 0; i < 3; i++) {
			if (i < starCount) {
				stars[i].setVisible(true);
			} else {
				stars[i].setVisible(false);
			}
		}
	}
	
	private void doStarEffect() {
		if (Config.DEBUG_LOG) Log.d(TAG, "doStarEffect starCount:" + starCount);
		if (starCount <= 0 || 3 < starCount) {
			return;
		}
		
		if (addStarAnimation != null) {
			addStarAnimation.setupOffset(STAR_ANIMATION_OFFSET_X[starCount - 1], 11);
		}
		if (flameAnimation != null) {
			flameAnimation.setupOffset(FLAME_OFFSET_X[starCount - 1], -71);
		}
		if (addStarEffect != null) {
			addStarEffect.start();
		}
	}
	
	private GameLevel gameLevel;
	private int score;
	private int starCount;	// 0 - 3: higher score get more stars.
	
	private static final float BAR_WIDTH = 213;
	private RenderComponent barLv1;
	private RenderComponent barLv2;
	private RenderComponent barLv3;
	private RenderComponent barFull;
	private RenderComponent currentBar;
	
	private static float[] STAR_OFFSET_X = new float[] {
		9, 27, 45
	};
	private RenderComponent[] stars;
	
	private static float[] STAR_ANIMATION_OFFSET_X = new float[] {
		-28, -6, 12
	};
	private FrameAnimationComponent addStarAnimation;
	
	private static float[] FLAME_OFFSET_X = new float[] {
		-134, -116, -98
	};
	private FrameAnimationComponent flameAnimation;
	private AnimationSet addStarEffect;
	private Sound soundAddStar;
}
