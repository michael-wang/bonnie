package com.studioirregular.bonniesbrunch.main;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.ViewConfiguration;
import android.widget.Scroller;
import android.widget.Toast;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.Game;
import com.studioirregular.bonniesbrunch.Game.StoredPurchaseState;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.GameScoreSystem;
import com.studioirregular.bonniesbrunch.InputSystem;
import com.studioirregular.bonniesbrunch.InputSystem.TouchEvent;
import com.studioirregular.bonniesbrunch.LevelSystem;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelLockState;
import com.studioirregular.bonniesbrunch.R;
import com.studioirregular.bonniesbrunch.SoundSystem;
import com.studioirregular.bonniesbrunch.SoundSystem.Sound;
import com.studioirregular.bonniesbrunch.base.ObjectBase;
import com.studioirregular.bonniesbrunch.component.DecimalDigit.TextureConfig;
import com.studioirregular.bonniesbrunch.component.FrameAnimationComponent;
import com.studioirregular.bonniesbrunch.component.RenderComponent;
import com.studioirregular.bonniesbrunch.entity.ButtonEntity;
import com.studioirregular.bonniesbrunch.entity.DecimalNumber;
import com.studioirregular.bonniesbrunch.entity.DialogConfirm;
import com.studioirregular.bonniesbrunch.entity.GameEntity;
import com.studioirregular.bonniesbrunch.entity.ModelDialog;
import com.studioirregular.bonniesbrunch.entity.SimpleEntity;

public class RootSelectMajorLevel extends GameEntityRoot implements PurchaseResultListener {

	private static final String TAG = "root-select-major-level";
	
	public RootSelectMajorLevel(Game game, float lastCarOffset) {
		super(game);
		if (Config.DEBUG_LOG) Log.w(TAG, "lastCarOffset:" + lastCarOffset);
		carScrollBaseX = lastCarOffset;	// borrow carScrollBaseX, for it won't be used until first touch event, and it will by clean by then.
		scroller = new Scroller(game.getActivity());
	}
	
	@Override
	public boolean load() {
		setupBackground();
		setupMapObjects();
		setupScrollableObjects();
		setupTotalScore();
		
		viewConfig = ViewConfiguration.get(game.getActivity());
		
		SoundSystem.getInstance().playMusic("bgm_menu", true);
		clickSound = SoundSystem.getInstance().load("mainmenu_bt_s1");
		
		InputSystem.getInstance().setTrackingVelocity(true);
		return true;
	}
	
	@Override
	protected void onReadyToGo() {
		super.onReadyToGo();
		
		game.getAd().hideBanner();
	}
	
	@Override
	public void onSentFullVersionPurchaseRequest() {
		if (Config.DEBUG_LOG) Log.w(TAG, "onPurchaseFullVersionResult: success");
		remove(processingDialog);
	}
	
	@Override
	public void onFullVersionPurchaseStateChanged(StoredPurchaseState fullVersionPurchaseState) {
		if (Config.DEBUG_LOG) Log.w(TAG, "notifyFullVersionPurchaseState fullVersionPurchaseState:" + fullVersionPurchaseState);
		
		clearScrollableObjects();
		setupScrollableObjects();
	}
	
	@Override
	public void update(long timeDelta, ObjectBase parent) {
		super.update(timeDelta, parent);
		
		if (scroller.isFinished() == false) {
			scroller.computeScrollOffset();
			float carX = scroller.getCurrX();
			if (carX < MIN_CAR_SCROLL_X) {
				carX = MIN_CAR_SCROLL_X;
				scroller.forceFinished(true);
			} else if (MAX_CAR_SCROLL_X < carX) {
				carX = MAX_CAR_SCROLL_X;
				scroller.forceFinished(true);
			}
			
			moveObjectsTo(carX);
			game.rememberMajorLevelScrollOffset((int)carX);
		}
		
		if (schedulePurchase) {
			schedulePurchase = false;
			
			if (processingDialog == null) {
				ModelDialog processing = new ModelDialog(MIN_GAME_ENTITY_Z_ORDER + 1);
				processing.setup(0, 0, 720, 480);
				processing.setBackground("popup_menu_processing", 720, 480);
				processingDialog = processing;
			}
			add(processingDialog);
			
			game.requestPurchaseFullVersion();
		}
	}
	
	@Override
	protected boolean handlePendingEvent(GameEvent event) {
		if (GameEvent.LEVEL_MAJOR_SELECTED == event.what) {
			final int major = event.arg1;
			game.gotoMinorLevelSelection(major);
			
			InputSystem.getInstance().setTrackingVelocity(false);
			return true;
		} else if (GameEvent.ROOT_BACK == event.what) {
			onFinish();
			
			InputSystem.getInstance().setTrackingVelocity(false);
			return true;
		}
		return false;
	}
	
	private void setupBackground() {
		RenderComponent bg = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		bg.setup(720, 480);
		bg.setup("level_menu_bg");
		add(bg);
		
		// map background
		bg = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
		bg.setup(720, 177);
		bg.setupOffset(0, 303);
		bg.setup("level_menu_map_bg");
		add(bg);
	}
	
	private void setupMapObjects() {
		final int firstUnlockLevel = findFirstUnlockLevel();
		
		// 5 circles
		final float[] levelMarkPosition = {
			125, 400,
			261, 420,
			396, 410,
			528, 421,
			645, 414
		};
		
		RenderComponent levelMark = null;
		final String normalMarkTexture = "level_menu_map_mark";
		final String scoreLockedMarkTexture = "level_menu_map_mark_locked";
		
		for (int i = 0; i < 5; i++) {
			levelMark = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER + 2);
			levelMark.setup(38, 38);
			levelMark.setupOffset(levelMarkPosition[i * 2], levelMarkPosition[i * 2 + 1]);
			
			final boolean scoreLocked = firstUnlockLevel < (i + 1) ? true : false;
			levelMark.setup(scoreLocked ? scoreLockedMarkTexture : normalMarkTexture);
			add(levelMark);
		}
		
		// car
		final float[] carPosition = {
			0,   0,
			79,  334,
			214, 353,
			348, 343,
			480, 355,
			597, 348
		};
		
		FrameAnimationComponent car = new FrameAnimationComponent(MIN_GAME_COMPONENT_Z_ORDER + 3);
		car.setLoop(true);
		car.setupOffset(carPosition[firstUnlockLevel * 2], carPosition[firstUnlockLevel * 2 + 1]);
		car.addFrame("level_menu_map_car_01", 102, 69, 250);
		car.addFrame("level_menu_map_car_02", 102, 69, 250);
		car.start();
		add(car);
		
		// back button
		ButtonEntity.Builder builder = new ButtonEntity.Builder(9, 380, 99, 98, "level_menu_left_normal", "level_menu_left_normal");
		builder.playSoundWhenClicked("mainmenu_bt_s1");
		builder.emitEventWhenPressed(GameEvent.MENU_BACK);
		builder.offsetTouchArea(-16, -16, 8, 8);
		ButtonEntity button = new ButtonEntity(MIN_GAME_ENTITY_Z_ORDER, builder);
		add(button);
	}
	
	private int findFirstUnlockLevel() {
		int level = LevelSystem.MIN_MAJOR_LEVEL;
		while (level <= LevelSystem.MAX_MAJOR_LEVEL) {
			if (LevelSystem.getMajorLevelLockState(level) != LevelLockState.UNLOCK) {
				assert level > LevelSystem.MIN_MAJOR_LEVEL;
				return level - 1;
			}
			level++;
		}
		return LevelSystem.MIN_MAJOR_LEVEL;
	}
	
	private void setupScrollableObjects() {
		float baseX = carScrollBaseX;
		
		if (Config.ENABLE_PROMOTION_FOR_TEE_SHIRT) {
			MyButton promote = new MyButton(MIN_GAME_ENTITY_Z_ORDER);
			promote.setup(baseX, CAR_Y, CAR_WIDTH, CAR_HEIGHT, "level_menu_teead");
			add(promote);
			this.promoteTee = promote;
			baseX += (CAR_WIDTH + CAR_INTERVAL);
		}
		
		for (int i = 0; i < TOTAL_LEVELS; i++) {
			LevelCar car = new LevelCar(MIN_GAME_ENTITY_Z_ORDER);
			car.setup(baseX + (i * (CAR_WIDTH + CAR_INTERVAL)), CAR_Y, CAR_WIDTH, CAR_HEIGHT, i + 1);
			add(car);
			
			cars[i] = car;
		}
	}
	
	private void clearScrollableObjects() {
		if (promoteTee != null) {
			remove(promoteTee);
		}
		
		if (cars != null && cars.length > 0) {
			for (int i = 0; i < cars.length; i++) {
				remove(cars[i]);
				cars[i] = null;
			}
		}
	}
	
	private void setupTotalScore() {
		RenderComponent scoreBar = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER + 4);
		scoreBar.setup(462, 35);
		scoreBar.setup("level_menu_showmoney");
		scoreBar.setupOffset(5, 3);
		add(scoreBar);
		
		RenderComponent dollar = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER + 5);
		dollar.setup(20, 26);
		dollar.setup("number_symbol_money_s");
		dollar.setupOffset(229, 8);
		add(dollar);
		
		DecimalNumber number = new DecimalNumber(MIN_GAME_ENTITY_Z_ORDER);
		TextureConfig texConfig = new TextureConfig("head-up-display");
		final String partitionNameBase = "number_s_";
		for (int i = 0; i < 10; i++) {
			texConfig.addPartition(i, partitionNameBase + Integer.toString(i));
		}
		
		final float DIGIT_WIDTH = 20;
		final float DIGIT_HEIGHT = 26;
		number.setup(249, 8, DIGIT_WIDTH * 7, DIGIT_HEIGHT, 
				7, DIGIT_WIDTH, DIGIT_HEIGHT, texConfig);
		number.setNewValue(GameScoreSystem.getInstance().getTotalLevelScore());
		add(number);
	}
	
	private void requestPurchaseFullVersion() {
		if (confirmBuyDialog == null) {
			DialogConfirm dialog = new DialogConfirm(MIN_GAME_ENTITY_Z_ORDER + 1);
			dialog.setup(0, 0, 720, 480);
			dialog.setContent("popup_menu_buy_bg", 720, 480);
			confirmBuyDialog = dialog;
		}
		add(confirmBuyDialog);
	}
	
	@Override
	public boolean dispatch(TouchEvent event, GameEntity parent) {
		if (super.dispatch(event, parent)) {
			return true;
		} else if (isDoingFadeOut) {
			return false;
		}
		
		final float x = event.x;
		final float y = event.y;
		
		if (TouchEvent.DOWN == event.type) {
			downX = x;
			downY = y;
			final GameEntity firstEntity = promoteTee != null ? promoteTee : cars[0];
			carScrollBaseX = firstEntity.box.left;
			withinTapRange = true;
			
			scroller.forceFinished(true);
		} else if (TouchEvent.MOVE == event.type) {
			final float dx = x - downX;
			
			if (withinTapRange) {
				final float dy = y - downY;
				if ((viewConfig.getScaledTouchSlop() < Math.abs(dx)) ||
					(viewConfig.getScaledTouchSlop() < Math.abs(dy))) {
					withinTapRange = false;
				}
			} else {
				float carX = carScrollBaseX + dx;
				
				if (MAX_CAR_SCROLL_X < carX) {
					carX = MAX_CAR_SCROLL_X;
				} else if (carX < MIN_CAR_SCROLL_X) {
					carX = MIN_CAR_SCROLL_X;
				}
				moveObjectsTo(carX);
			}
		} else if (TouchEvent.UP == event.type) {
			if (withinTapRange) {
				onTap(downX, downY);
			} else {
				final float dx = x - downX;
				
				float carX = carScrollBaseX + dx;
				if (MAX_CAR_SCROLL_X < carX) {
					carX = MAX_CAR_SCROLL_X;
				} else if (carX < MIN_CAR_SCROLL_X) {
					carX = MIN_CAR_SCROLL_X;
				}
				
				moveObjectsTo(carX);
				game.rememberMajorLevelScrollOffset((int)carX);
				
				InputSystem.getInstance().computeVelocity();
				final float xVelocity = InputSystem.getInstance().getXVelocity();
				fling(carX, xVelocity);
			}
		}
		return true;
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		switch (event.what) {
		case GameEvent.GAME_CONFIRM_YES:
		case GameEvent.GAME_CONFIRM_NO:
			return true;
		}
		return super.wantThisEvent(event);
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		super.handleGameEvent(event);
		
		if (GameEvent.MENU_BACK == event.what) {
			onBack();
		} else if (GameEvent.GAME_CONFIRM_NO == event.what) {
			if (confirmBuyDialog != null) {
				remove(confirmBuyDialog);
			}
		} else if (GameEvent.GAME_CONFIRM_YES == event.what) {
			if (confirmBuyDialog != null) {
				remove(confirmBuyDialog);
			}
			
			schedulePurchase = true;
		}
	}
	
	private void moveObjectsTo(float x) {
		if (promoteTee != null) {
			promoteTee.move(x, CAR_Y);
			for (int i = 0; i < TOTAL_LEVELS; i++) {
				cars[i].move(x + (i + 1) * (CAR_WIDTH + CAR_INTERVAL), CAR_Y);
			}
		} else {
			for (int i = 0; i < TOTAL_LEVELS; i++) {
				cars[i].move(x + i * (CAR_WIDTH + CAR_INTERVAL), CAR_Y);
			}
		}
	}
	
	private void fling(float x, float velocityX) {
		//Log.d(TAG, "fling x:" + x + ",velocityX:" + velocityX);
		scroller.fling((int) x, (int) CAR_Y, (int) velocityX, (int) 0,
				(int) MIN_CAR_FLING_X, (int) MAX_CAR_FLING_X, (int) CAR_Y, (int) CAR_Y);
	}
	
	private void onTap(float x, float y) {
		if (promoteTee != null && promoteTee.box.contains(x, y)) {
			promoteTee.onTap();
		} else {
			for (int i = 0; i < TOTAL_LEVELS; i++) {
				LevelCar car = cars[i];
				if (car.box.contains(x, y)) {
					car.onTap();
				}
			}
		}
	}
	
	private void buyTee() {
		if (Config.ENABLE_PROMOTION_FOR_TEE_SHIRT) {
			Intent openURL = new Intent(Intent.ACTION_VIEW);
			openURL.setData(Uri.parse(Config.PROMOTE_TEE_URL));
			game.getActivity().startActivity(openURL);
		}
	}
	
	private void onFinish() {
		game.gotoMainMenu();
	}
	
	private class LevelCar extends GameEntity {
		
		public LevelCar(int zOrder) {
			super(zOrder);
		}
		
		public void setup(float x, float y, float width, float height, int major) {
			super.setup(x, y, width, height);
			
			this.major = major;
			lockState = LevelSystem.getMajorLevelLockState(major);
			if (Config.DEBUG_LOG) Log.w(TAG, "LevelCar major:" + major + ",lockState:" + lockState);
			
			RenderComponent bg = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
			bg.setup(width, height);
			bg.setup(getTexture(major, lockState));
			add(bg);
			
			if (lockState != LevelLockState.PURCHASE_LOCK) {
				setupStarCount(x, y);
			}
		}
		
		public void onTap() {
			SoundSystem.getInstance().playSound(clickSound, false);
			
			if (lockState == LevelLockState.UNLOCK) {
				doFadeOut(GameEventSystem.getInstance().obtain(GameEvent.LEVEL_MAJOR_SELECTED, major));
			} else if (lockState == LevelLockState.PURCHASE_LOCK) {
				requestPurchaseFullVersion();
			} else {
				game.requestToastMessage(R.string.hint_score_lock, Toast.LENGTH_SHORT);
			}
		}
		
		private String getTexture(int major, LevelLockState lockState) {
			switch (major) {
			case 1:
				return "level_menu_ep1";
			case 2:
				if (lockState == LevelLockState.PURCHASE_LOCK) {
					return "level_menu_ep2_locked";
				} else if (lockState == LevelLockState.SCORE_LOCK) {
					return "level_menu_ep2_locked";
				} else {
					return "level_menu_ep2";
				}
			case 3:
				if (lockState == LevelLockState.PURCHASE_LOCK) {
					return "level_menu_ep3_buy";
				} else if (lockState == LevelLockState.SCORE_LOCK) {
					return "level_menu_ep3_locked";
				} else {
					return "level_menu_ep3";
				}
			case 4:
				if (lockState == LevelLockState.PURCHASE_LOCK) {
					return "level_menu_ep4_buy";
				} else if (lockState == LevelLockState.SCORE_LOCK) {
					return "level_menu_ep4_locked";
				} else {
					return "level_menu_ep4";
				}
			case 5:
				if (lockState == LevelLockState.PURCHASE_LOCK) {
					return "level_menu_ep5_buy";
				} else if (lockState == LevelLockState.SCORE_LOCK) {
					return "level_menu_ep5_locked";
				} else {
					return "level_menu_ep5";
				}
			}
			return "";
		}
		
		private void setupStarCount(float carX, float carY) {
			RenderComponent slash = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
			slash.setup(24, 29);
			slash.setup("number_bonus_line");
			slash.setupOffset(184, 175);
			add(slash);
			
			int starCount = GameScoreSystem.getInstance().getMajorLevelStarCount(major);
			setupStarCount(carX + 136, carY + 175, starCount);
			
			// total 30 stars
			setupStarCount(carX + 208, carY + 175, 30);
		}
		
		private void setupStarCount(float x, float y, int count) {
			DecimalNumber number = new DecimalNumber(MIN_GAME_ENTITY_Z_ORDER);
			TextureConfig texConfig = new TextureConfig("select_major_level");
			final String partitionNameBase = "number_bonus_";
			for (int i = 0; i < 10; i++) {
				texConfig.addPartition(i, partitionNameBase + Integer.toString(i));
			}
			
			final float DIGIT_WIDTH = 24;
			final float DIGIT_HEIGHT = 29;
			number.setup(x, y, DIGIT_WIDTH * 2, DIGIT_HEIGHT, 
					2, DIGIT_WIDTH, DIGIT_HEIGHT, texConfig);
			number.setNewValue(count);
			add(number);
		}
		
		@Override
		protected boolean wantThisEvent(GameEvent event) {
			return false;
		}
		
		@Override
		protected void handleGameEvent(GameEvent event) {
		}
		
		private int major;
		private LevelLockState lockState;
	}
	
	private class MyButton extends SimpleEntity {

		public MyButton(int zOrder) {
			super(zOrder);
		}
		
		public void onTap() {
			SoundSystem.getInstance().playSound(clickSound, false);
			buyTee();
		}
		
		@Override
		protected boolean wantThisEvent(GameEvent event) {
			return false;
		}
		
		@Override
		protected void handleGameEvent(GameEvent event) {
		}
		
	}
	
	private static final int TOTAL_LEVELS = 5;
	private static final float CAR_Y = 61;
	private static final float CAR_WIDTH = 342;
	private static final float CAR_HEIGHT = 272;
	private static final float CAR_INTERVAL = 85;
	private static final float MAX_CAR_FLING_X = 720;
	private static final float MIN_CAR_FLING_X = -CAR_WIDTH * (TOTAL_LEVELS + (Config.ENABLE_PROMOTION_FOR_TEE_SHIRT ? 1 : 0)) - CAR_INTERVAL * (TOTAL_LEVELS - (Config.ENABLE_PROMOTION_FOR_TEE_SHIRT ? 0 : 1));
	private static final float MAX_CAR_SCROLL_X = (720 - CAR_WIDTH) / 2;
	private static final float MIN_CAR_SCROLL_X = -(CAR_WIDTH) * (TOTAL_LEVELS + (Config.ENABLE_PROMOTION_FOR_TEE_SHIRT ? 1 : 0)) - CAR_INTERVAL * (TOTAL_LEVELS - (Config.ENABLE_PROMOTION_FOR_TEE_SHIRT ? 0 : 1)) + (720 + CAR_WIDTH) / 2;
	
	// BuyTEE-Car1-Car2-...-Car5
	private MyButton promoteTee;	// a button to promote T-Shirt, scrolling with cars.
	private LevelCar[] cars = new LevelCar[TOTAL_LEVELS];
	
	private float downX, downY;
	private float carScrollBaseX;
	private boolean withinTapRange;
	private ViewConfiguration viewConfig;
	private Sound clickSound;
	private boolean schedulePurchase;
	
	private Scroller scroller;
	
	private GameEntity confirmBuyDialog;
	private GameEntity processingDialog;
}