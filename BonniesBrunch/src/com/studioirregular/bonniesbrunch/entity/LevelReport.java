package com.studioirregular.bonniesbrunch.entity;

import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.GameScoreSystem;
import com.studioirregular.bonniesbrunch.LevelSystem;
import com.studioirregular.bonniesbrunch.LevelSystem.GameLevel;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelLockState;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelNumber;
import com.studioirregular.bonniesbrunch.SoundSystem;
import com.studioirregular.bonniesbrunch.SoundSystem.Sound;
import com.studioirregular.bonniesbrunch.base.ObjectArray;
import com.studioirregular.bonniesbrunch.base.ObjectArray.OutOfCapacityException;
import com.studioirregular.bonniesbrunch.component.Animation;
import com.studioirregular.bonniesbrunch.component.DecimalDigit.TextureConfig;
import com.studioirregular.bonniesbrunch.component.FrameAnimationComponent;
import com.studioirregular.bonniesbrunch.component.RenderComponent;

public class LevelReport extends GameEntity {

	private static final String TAG = "level-report";
	
	public LevelReport(int zOrder) {
		super(zOrder);
	}
	
	public void setup(float x, float y, float width, float height, GameLevel gameLevel) {
		super.setup(x, y, width, height);
		
		this.gameLevel = gameLevel;
		
		setupBackground();
		setupLevelTitle();
		setupButtons();
		setupWaitingBonnie();
		setupStarsAndFlameEffect();
		
		soundTotalScoreRunning = SoundSystem.getInstance().load("stat_accum");
		soundAddStar = SoundSystem.getInstance().load("report_menu_star_s1");
		soundFlame = SoundSystem.getInstance().load("report_event_1_star_s1");
		soundBonnieExcited = SoundSystem.getInstance().load("report_menu_happy_s1");
		soundBonnieSad = SoundSystem.getInstance().load("report_menu_sad_s1");
	}
	
	public void prepareToReport() {
		updateNextButton();
	}
	
	public void showCustomerLost() {
		TextureConfig textureConfig = new TextureConfig("head-up-display");
		final String partitionNameBase = "number_s_";
		for (int i = 0; i < 10; i++) {
			textureConfig.addPartition(i, partitionNameBase + Integer.toString(i));
		}
		
		DecimalNumber lostCount = new DecimalNumber(MIN_GAME_ENTITY_Z_ORDER);
		lostCount.setup(400, 90, 20 * 3, 26, 3, 20, 26, textureConfig);
		lostCount.setNewValue(GameScoreSystem.getInstance().getLostCustomerCount());
		add(lostCount);
	}
	
	public void showCustomerServed() {
		TextureConfig textureConfig = new TextureConfig("head-up-display");
		final String partitionNameBase = "number_s_";
		for (int i = 0; i < 10; i++) {
			textureConfig.addPartition(i, partitionNameBase + Integer.toString(i));
		}
		
		DecimalNumber servedCount = new DecimalNumber(MIN_GAME_ENTITY_Z_ORDER);
		servedCount.setup(400, 125, 20 * 3, 26, 3, 20, 26, textureConfig);
		servedCount.setNewValue(GameScoreSystem.getInstance().getServedCustomerCount());
		add(servedCount);
	}
	
	public void showMoneyEarned() {
		TextureConfig textureConfig = new TextureConfig("select_minor");
		final String partitionNameBase = "sub_level_menu_number_";
		for (int i = 0; i < 10; i++) {
			textureConfig.addPartition(i, partitionNameBase + Integer.toString(i));
		}
		
		DecimalNumber moneyEarned = new DecimalNumber(MIN_GAME_ENTITY_Z_ORDER);
		moneyEarned.setup(336, 170, 27 * 5, 30, 5, 27, 30, textureConfig);
		moneyEarned.setNewValue(GameScoreSystem.getInstance().getAccumulatedMoney());
		add(moneyEarned);
	}
	
	public void showTipsEarned() {
		TextureConfig textureConfig = new TextureConfig("select_minor");
		final String partitionNameBase = "sub_level_menu_number_";
		for (int i = 0; i < 10; i++) {
			textureConfig.addPartition(i, partitionNameBase + Integer.toString(i));
		}
		
		DecimalNumber tipsEarned = new DecimalNumber(MIN_GAME_ENTITY_Z_ORDER);
		tipsEarned.setup(336, 215, 27 * 5, 30, 5, 27, 30, textureConfig);
		tipsEarned.setNewValue(GameScoreSystem.getInstance().getAccumulatedTip());
		add(tipsEarned);
	}
	
	public void showTotalMoneyEarned(int animationDuration) {
		SoundSystem.getInstance().playSound(soundTotalScoreRunning, false);
		
		TextureConfig textureConfig = new TextureConfig("select_minor");
		final String partitionNameBase = "sub_level_menu_number_";
		for (int i = 0; i < 10; i++) {
			textureConfig.addPartition(i, partitionNameBase + Integer.toString(i));
		}
		
		DecimalNumber total = new DecimalNumber(MIN_GAME_ENTITY_Z_ORDER);
		total.setup(336, 275, 37 * 5, 30, 5, 27, 30, textureConfig);
		total.animateToNewValue(
				GameScoreSystem.getInstance().getAccumulatedMoney()
				+ GameScoreSystem.getInstance().getAccumulatedTip(),
				animationDuration);
		add(total);
	}
	
	public void showAnimatedBonnie() {
		remove(staticBonnie);
		
		SimpleEntity reportingBonnie = new SimpleEntity(MIN_GAME_ENTITY_Z_ORDER + 3);
		reportingBonnie.setup(442, 137, 278, 343);
		add(reportingBonnie);
		
		FrameAnimationComponent animation = new FrameAnimationComponent(MIN_GAME_COMPONENT_Z_ORDER);
		animation.setLoop(true);
		
		int total = GameScoreSystem.getInstance().getAccumulatedMoney() + GameScoreSystem.getInstance().getAccumulatedTip();
		if (total <= gameLevel.score.min) {
			animation.addFrame("bonnie_w2_sad_1", 278, 343, 300);
			animation.addFrame("bonnie_w2_sad_2", 278, 343, 300);
			animation.addFrame("bonnie_w2_sad_1", 278, 343, 300);
			animation.addFrame("bonnie_w2_sad_2", 278, 343, 300);
			animation.addFrame("bonnie_w2_sad_1", 278, 343, 300);
			animation.addFrame("bonnie_w2_sad_3", 278, 343, 200);
			
			SoundSystem.getInstance().playSound(soundBonnieSad, false);
		} else if (total < gameLevel.score.high) {
			animation.addFrame("bonnie_w1_normal_1", 278, 343, 500);
			animation.addFrame("bonnie_w1_normal_2", 278, 343, 500);
		} else {
			animation.addFrame("bonnie_w3_happy_1", 278, 343, 200);
			animation.addFrame("bonnie_w3_happy_2", 278, 343, 200);
			animation.addFrame("bonnie_w3_happy_3", 278, 343, 200);
			
			SoundSystem.getInstance().playSound(soundBonnieExcited, false);
		}
		reportingBonnie.add(animation);
		animation.start();
	}
	
	public void showStars(int entityDoneCode) {
		starEffectEndCode = entityDoneCode;
		
		final int score = GameScoreSystem.getInstance().getAccumulatedMoney()
				+ GameScoreSystem.getInstance().getAccumulatedTip();
		
		if (score < gameLevel.score.min) {
			starCount = 0;
		} else if (score < gameLevel.score.med) {
			starCount = 1;
		} else if (score < gameLevel.score.high) {
			starCount = 2;
		} else {
			starCount = 3;
		}
		
		if (starCount == 0) {
			GameEventSystem.scheduleEvent(GameEvent.ENTITY_DONE, starEffectEndCode);
			return;
		}
		
		for (int i = 0; i < starCount; i++) {
			final StarEffectContainer effect = starEffects.get(i);
			effect.entity.move(113 + i * 87, 236);
			
			effect.animation.notifyWhenFinished(true, ADD_STAR_END);
			
			effect.entity.installEventMap(
					GameEventSystem.getInstance().obtain(GameEvent.ANIMATION_END, ADD_STAR_END),
					GameEventSystem.getInstance().obtain(GameEvent.ANIMATION_END, ADD_STAR_END));
		}
		
		add(starEffects.get(0).star);
		starEffectIndex = 0;
		starEffects.get(0).animation.start();
		SoundSystem.getInstance().playSound(soundAddStar, false);
	}
	
	public void showFlameEffect(int entityDoneCode) {
		starEffectEndCode = entityDoneCode;
		
		if (starCount == 0) {
			GameEventSystem.scheduleEvent(GameEvent.ENTITY_DONE, entityDoneCode);
			return;
		}
		
		float[] flamePosition = {
			38, 12,
			377, 128,
			124, 229
		};
		
		for (int i = 0; i < starCount; i++) {
			final float x = flamePosition[i * 2];
			final float y = flamePosition[i * 2 + 1];
			StarEffectContainer effect = starEffects.get(i);
			effect.entity.move(x, y);
			
			effect.animation.notifyWhenFinished(true, FLAME_EFFECT_END);
			
			effect.entity.installEventMap(
					GameEventSystem.getInstance().obtain(GameEvent.ANIMATION_END, FLAME_EFFECT_END),
					GameEventSystem.getInstance().obtain(GameEvent.ANIMATION_END, FLAME_EFFECT_END));
		}
		
		starEffectIndex = 0;
		starEffects.get(0).animation.start();
		SoundSystem.getInstance().playSound(soundFlame, false);
	}
	
	public void stampNewScoreRecord() {
		RenderComponent stamp = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
		stamp.setup(88, 84);
		stamp.setup("report_menu_newrecord");
		stamp.setupOffset(111, 274);
		add(stamp);
		
		Sound soundNewRecord = SoundSystem.getInstance().load("report_menu_newrecord_s1");
		SoundSystem.getInstance().playSound(soundNewRecord, false);
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		if (GameEvent.ANIMATION_END == event.what) {
			if (ADD_STAR_END == event.arg1 || FLAME_EFFECT_END == event.arg1) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		if (GameEvent.ANIMATION_END == event.what) {
			if (ADD_STAR_END == event.arg1) {
				starEffectIndex++;
				if (starEffectIndex < starCount) {
					StarEffectContainer effect = starEffects.get(starEffectIndex);
					add(effect.star);
					effect.animation.start();
					
					SoundSystem.getInstance().playSound(soundAddStar, false);
				} else {
					GameEventSystem.scheduleEvent(GameEvent.ENTITY_DONE, starEffectEndCode);
				}
			} else if (FLAME_EFFECT_END == event.arg1) {
				starEffectIndex++;
				if (starEffectIndex < starCount) {
					StarEffectContainer effect = starEffects.get(starEffectIndex);
					effect.animation.start();
					
					SoundSystem.getInstance().playSound(soundFlame, false);
				} else {
					starEffectIndex = 0;
					GameEventSystem.scheduleEvent(GameEvent.ENTITY_DONE, starEffectEndCode);
				}
			}
		} else if (GameEvent.GAME_EXIT == event.what) {
			GameEventSystem.scheduleEvent(event.what, event.arg1);
		} else if (GameEvent.GAME_RESTART == event.what) {
			GameEventSystem.scheduleEvent(event.what, event.arg1);
		} else if (GameEvent.REQUEST_GOTO_NEXT_LEVEL == event.what) {
			GameEventSystem.scheduleEvent(event.what);
		}
	}
	
	private void setupBackground() {
		RenderComponent bg = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		bg.setup(720, 480);
		bg.setup("report_menu_bg");
		add(bg);
	}
	
	private void setupLevelTitle() {
		TextureConfig textureConfig = new TextureConfig("report-1");
		final String partitionNameBase = "report_menu_number_";
		for (int i = 0; i < 10; i++) {
			textureConfig.addPartition(i, partitionNameBase + Integer.toString(i));
		}
		
		DecimalNumber majorLevel = new DecimalNumber(MIN_GAME_ENTITY_Z_ORDER);
		majorLevel.setup(445, 37, 15 * 2, 20, 2, 15, 20, textureConfig);
		majorLevel.setNewValue(gameLevel.number.major);
		add(majorLevel);
		
		DecimalNumber minorLevel = new DecimalNumber(MIN_GAME_ENTITY_Z_ORDER);
		minorLevel.setup(470, 37, 15 * 2, 20, 2, 15, 20, textureConfig);
		minorLevel.setNewValue(gameLevel.number.minor);
		add(minorLevel);
	}
	
	private void setupButtons() {
		ButtonEntity.Builder builder = new ButtonEntity.Builder(180, 382, 77, 77, "report_menu_bt1_back_normal", "report_menu_bt1_back_press");
		builder.playSoundWhenClicked("mainmenu_bt_s1");
		builder.emitEventWhenPressed(GameEvent.GAME_EXIT);
		builder.offsetTouchArea(-16, -16, 16, 16);
		ButtonEntity button = new ButtonEntity(MIN_GAME_ENTITY_Z_ORDER, builder);
		add(button);

		builder = new ButtonEntity.Builder(320, 382, 77, 77, "report_menu_bt2_reload_normal", "report_menu_bt2_reload_press");
		builder.playSoundWhenClicked("mainmenu_bt_s1");
		builder.emitEventWhenPressed(GameEvent.GAME_RESTART);
		builder.offsetTouchArea(-16, -16, 16, 16);
		button = new ButtonEntity(MIN_GAME_ENTITY_Z_ORDER, builder);
		add(button);

		builder = new ButtonEntity.Builder(460, 382, 77, 77, "report_menu_bt3_next_normal", "report_menu_bt3_next_press");
		builder.playSoundWhenClicked("mainmenu_bt_s1");
		builder.offsetTouchArea(-16, -16, 16, 16);
		button = new ButtonEntity(MIN_GAME_ENTITY_Z_ORDER, builder);
		button.setDisableTexture("report_menu_bt3_next_disable");
		add(button);
		nextButton = button;
	}
	
	private void setupWaitingBonnie() {
		staticBonnie = new SimpleEntity(MIN_GAME_ENTITY_Z_ORDER + 2);
		staticBonnie.setup(442, 137, 278, 343, "bonnie_w4_wait");
		add(staticBonnie);
	}
	
	private void setupStarsAndFlameEffect() {
		try {
			for (int i = 0; i < 3; i++) {
				// stars
				RenderComponent star = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
				star.setup(56, 54);
				star.setup("report_menu_star");
				star.setupOffset(245 + i * 85, 318);
				
				// effects
				SimpleEntity entity = new SimpleEntity(MIN_GAME_ENTITY_Z_ORDER + 2);
				entity.setup(0, 0, 315, 267);
				
				FrameAnimationComponent animation = new FrameAnimationComponent(MIN_GAME_COMPONENT_Z_ORDER);
				animation.setFillBefore(false);
				animation.setFillAfter(false);
				animation.addFrame("report_event_1_star_001", 315, 267, 50);
				animation.addFrame("report_event_1_star_002", 315, 267, 100);
				animation.addFrame("report_event_1_star_003", 315, 267, 100);
				animation.addFrame("report_event_1_star_004", 315, 267, 100);
				animation.addFrame("report_event_1_star_005", 315, 267, 100);
				animation.addFrame("report_event_1_star_006", 315, 267, 100);
				animation.addFrame("report_event_1_star_007", 315, 267, 100);
				entity.add(animation);
				
				add(entity);
				
				StarEffectContainer effect = new StarEffectContainer(star, entity, animation);
				starEffects.add(effect);
			}
		} catch (OutOfCapacityException e) {
			if (Config.DEBUG_LOG) Log.e(TAG, "setupStarsAndFlameEffect e:" + e);
		}
	}
	
	/* If the score player earned is under minimum requirement, next button is disabled.
	 * Otherwise, send REQUEST_GOTO_NEXT_LEVEL event.
	 */
	private void updateNextButton() {
		final LevelNumber nextLevel = gameLevel.number.getNextLevel();
		
		if (nextLevel.isGameOver()) {
			nextButton.setDisable(false);
			nextButton.setEmitEventWhenPressed(GameEvent.REQUEST_GOTO_NEXT_LEVEL, 0);
			return;
		}
		
		final boolean scoreLocked = (LevelSystem.getLevelLockState(nextLevel.major, nextLevel.minor) == LevelLockState.SCORE_LOCK);
		
		if (scoreLocked) {
			nextButton.setDisable(true);
		} else {
			nextButton.setDisable(false);
			nextButton.setEmitEventWhenPressed(GameEvent.REQUEST_GOTO_NEXT_LEVEL, 0);
		}
	}
	
	private GameLevel gameLevel;
	private SimpleEntity staticBonnie;
	private ButtonEntity nextButton;
	
	private int starCount = 0;
	
	private static final int ADD_STAR_END		= 0;
	private static final int FLAME_EFFECT_END	= 1;
	
	private class StarEffectContainer {
		public StarEffectContainer(RenderComponent star, SimpleEntity effectEntity, Animation effectAnimation) {
			this.star = star;
			this.entity = effectEntity;
			this.animation = effectAnimation;
		}
		private RenderComponent star;
		private SimpleEntity entity;
		private Animation animation;
	}
	private ObjectArray<StarEffectContainer> starEffects = new ObjectArray<StarEffectContainer>(3);
	private int starEffectEndCode;
	private int starEffectIndex;
	
	private Sound soundTotalScoreRunning;
	private Sound soundAddStar;
	private Sound soundFlame;
	private Sound soundBonnieExcited;
	private Sound soundBonnieSad;
}
