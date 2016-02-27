package com.studioirregular.bonniesbrunch.main;

import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.Game;
import com.studioirregular.bonniesbrunch.Game.StoredPurchaseState;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.SoundSystem;
import com.studioirregular.bonniesbrunch.component.Animation;
import com.studioirregular.bonniesbrunch.component.FrameAnimationComponent;
import com.studioirregular.bonniesbrunch.component.RenderComponent;
import com.studioirregular.bonniesbrunch.component.TranslateAnimation;
import com.studioirregular.bonniesbrunch.entity.ButtonEntity;
import com.studioirregular.bonniesbrunch.entity.DialogConfirm;
import com.studioirregular.bonniesbrunch.entity.GameEntity;
import com.studioirregular.bonniesbrunch.entity.ModelDialog;
import com.studioirregular.bonniesbrunch.entity.SimpleEntity;
import com.studioirregular.bonniesbrunch.entity.ToggleButton;

public class RootMainMenu extends GameEntityRoot implements PurchaseResultListener {

	private static final String TAG = "root-main-menu";
	
	public RootMainMenu(Game game) {
		super(game);
	}
	
	@Override
	public boolean load() {
		setupBackground();
		setupMenuButtons();
		setupBonniesAnimation();
		setupDialogs();
		
		if (SoundSystem.getInstance().isMusicDisabled() == false) {
			SoundSystem.getInstance().playMusic("bgm_menu", true);
		}
		
		return true;
	}
	
	@Override
	protected void onReadyToGo() {
		super.onReadyToGo();
		
		game.getAd().showBanner();
	}
	
	@Override
	public boolean onBack() {
		return false;
	}
	
	public void notifyBillingSupport(boolean supported) {
		if (buyButton != null) {
			buyButton.setDisable(!supported);
		}
	}
	
	@Override
	public void onSentFullVersionPurchaseRequest() {
		if (Config.DEBUG_LOG) Log.w(TAG, "onPurchaseFullVersionResult");
		remove(dialogProcessing);
	}
	
	@Override
	public void onFullVersionPurchaseStateChanged(StoredPurchaseState fullVersionPurchaseState) {
		if (Config.DEBUG_LOG) Log.w(TAG, "notifyFullVersionPurchaseState fullVersionPurchaseState:" + fullVersionPurchaseState);
		
		final boolean fullVersionPurchased = fullVersionPurchaseState.equals(StoredPurchaseState.PURCHASED);
		updateBuyButton(fullVersionPurchased);
	}
	
	private void updateBuyButton(boolean purchased) {
		if (buyButton != null) {
			remove(buyButton);
		}
		buyButton = setupBuyButton(purchased);
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		switch (event.what) {
		case GameEvent.MAIN_MENU_BUY:
		case GameEvent.MAIN_MENU_MUSIC:
		case GameEvent.MAIN_MENU_SOUND:
		case GameEvent.GAME_CONFIRM_YES:
		case GameEvent.GAME_CONFIRM_NO:
			if (Config.DEBUG_LOG) Log.d(TAG, "wantThisEvent event:" + event);
			return true;
		}
		return super.wantThisEvent(event);
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		super.handleGameEvent(event);
		if (Config.DEBUG_LOG) Log.d(TAG, "handleGameEvent event:" + event);
		
		switch (event.what) {
		case GameEvent.MAIN_MENU_PLAY:
		case GameEvent.MAIN_MENU_HELP:
		case GameEvent.MAIN_MENU_CREDIT:
			doFadeOut(event);
			return;
		}
		
		if (GameEvent.MAIN_MENU_BUY == event.what) {
			add(confirmBuyDialog);
		} else if (GameEvent.MAIN_MENU_MUSIC == event.what) {
			if (event.arg1 == 0) {
				SoundSystem.getInstance().disableMusic(true);
			} else {
				SoundSystem.getInstance().disableMusic(false);
				SoundSystem.getInstance().playMusic("bgm_menu", true);
			}
		} else if (GameEvent.MAIN_MENU_SOUND == event.what) {
			if (event.arg1 == 0) {
				SoundSystem.getInstance().disableSound(true);
			} else {
				SoundSystem.getInstance().disableSound(false);
			}
		} else if (GameEvent.GAME_CONFIRM_NO == event.what) {
			remove(confirmBuyDialog);
		} else if (GameEvent.GAME_CONFIRM_YES == event.what) {
			remove(confirmBuyDialog);
			game.requestPurchaseFullVersion();
			add(dialogProcessing);
		}
	}
	
	@Override
	protected boolean handlePendingEvent(GameEvent event) {
		if (Config.DEBUG_LOG) Log.w(TAG, "handlePendingEvent event:" + event);
		
		if (GameEvent.MAIN_MENU_PLAY == event.what) {
			game.gotoMajorLevelSelection();
			return true;
		} else if (GameEvent.MAIN_MENU_HELP == event.what) {
			game.gotoHelp();
			return true;
		} else if (GameEvent.MAIN_MENU_CREDIT == event.what) {
			game.gotoCredit();
			return true;
		} else {
			Log.e(TAG, "Unrecognized pending event:" + event);
		}
		return false;
	}
	
	private void setupBackground() {
		RenderComponent bg = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		bg.setup(720, 480);
		bg.setup("main_menu_bg");
		add(bg);
	}
	
	private void setupMenuButtons() {
		ButtonEntity.Builder builder = new ButtonEntity.Builder(720, 63, 228, 63, "main_menu_bt1", "main_menu_bt1_pressed");
		builder.playSoundWhenClicked("mainmenu_bt_s1");
		builder.emitEventWhenPressed(GameEvent.MAIN_MENU_PLAY);
		builder.offsetTouchArea(-8, -3, 8, 3);
		ButtonEntity button = new ButtonEntity(MIN_GAME_ENTITY_Z_ORDER, builder);
		add(button);
		
		TranslateAnimation slideIn = new TranslateAnimation(MIN_GAME_COMPONENT_Z_ORDER, 720, 63, 492, 63, 300);
		slideIn.start();
		button.add(slideIn);
		
		builder = new ButtonEntity.Builder(720, 133, 228, 63, "main_menu_bt3", "main_menu_bt3_pressed");
		builder.playSoundWhenClicked("mainmenu_bt_s1");
		builder.emitEventWhenPressed(GameEvent.MAIN_MENU_HELP);
		builder.offsetTouchArea(-8, -3, 8, 3);
		button = new ButtonEntity(MIN_GAME_ENTITY_Z_ORDER, builder);
		add(button);
		
		slideIn = new TranslateAnimation(MIN_GAME_COMPONENT_Z_ORDER, 720, 133, 492, 133, 300);
		slideIn.setStartOffset(150);
		slideIn.start();
		button.add(slideIn);
		
		builder = new ButtonEntity.Builder(720, 204, 228, 63, "main_menu_bt4", "main_menu_bt4_pressed");
		builder.playSoundWhenClicked("mainmenu_bt_s1");
		builder.emitEventWhenPressed(GameEvent.MAIN_MENU_CREDIT);
		builder.offsetTouchArea(-8, -3, 8, 3);
		button = new ButtonEntity(MIN_GAME_ENTITY_Z_ORDER, builder);
		add(button);
		
		slideIn = new TranslateAnimation(MIN_GAME_COMPONENT_Z_ORDER, 720, 204, 492, 204, 300);
		slideIn.setStartOffset(300);
		slideIn.start();
		button.add(slideIn);
		
		final boolean fullVersionPurchased = game.getFullVersionPurchaseState().equals(StoredPurchaseState.PURCHASED);
		buyButton = setupBuyButton(fullVersionPurchased);
		
		final boolean soundDisabled = SoundSystem.getInstance().isSoundDisabled();
		
		ToggleButton toggleButton = new ToggleButton(MIN_GAME_ENTITY_Z_ORDER, soundDisabled);
		toggleButton.setup(525, 359, 71, 72);
		toggleButton.setTouchArea(-12, -12, 8, 8);
		toggleButton.setupTextures("main_menu_bt_option1_enable",
				"main_menu_bt_option1_disable", 71, 72);
		toggleButton.setupEvents(GameEvent.MAIN_MENU_SOUND, 0,
				GameEvent.MAIN_MENU_SOUND, 1);
		add(toggleButton);
		
		final boolean musicDisabled = SoundSystem.getInstance().isMusicDisabled();
		
		toggleButton = new ToggleButton(MIN_GAME_ENTITY_Z_ORDER, musicDisabled);
		toggleButton.setup(614, 359, 71, 72);
		toggleButton.setTouchArea(-8, -12, 8, 8);
		toggleButton.setupTextures("main_menu_bt_option2_enable",
				"main_menu_bt_option2_disable", 71, 72);
		toggleButton.setupEvents(GameEvent.MAIN_MENU_MUSIC, 0,
				GameEvent.MAIN_MENU_MUSIC, 1);
		add(toggleButton);
	}
	
	private void setupBonniesAnimation() {
		FrameAnimationComponent animation = new FrameAnimationComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
		animation.setLoop(true);
		animation.setupOffset(79, 114);
		animation.addFrame("main_menu_bonnie_01", 123, 63, 200);
		animation.addFrame("main_menu_bonnie_02", 123, 63, 100);
		animation.addFrame("main_menu_bonnie_01", 123, 63, 50);
		animation.addFrame("main_menu_bonnie_02", 123, 63, 100);
		animation.addFrame("main_menu_bonnie_01", 123, 63, 3000);
		animation.start();
		add(animation);
	}
	
	private void setupDialogs() {
		DialogConfirm dialog = new DialogConfirm(MIN_GAME_ENTITY_Z_ORDER + 10);
		dialog.setup(0, 0, 720, 480);
		dialog.setContent("popup_menu_buy_bg", 720, 480);
		confirmBuyDialog = dialog;
		
		ModelDialog processing = new ModelDialog(MIN_GAME_ENTITY_Z_ORDER + 10);
		processing.setup(0, 0, 720, 480);
		processing.setBackground("popup_menu_processing", 720, 480);
		dialogProcessing = processing;
	}
	
	private ButtonEntity setupBuyButton(boolean purchased) {
		if (Config.DEBUG_LOG) Log.w(TAG, "setupBuyButton purchased:" + purchased);
		
		ButtonEntity button = null;
		
		if (purchased) {
			ButtonEntity.Builder builder = new ButtonEntity.Builder(720, 275, 228, 63, "main_menu_bt5_buy_done", "main_menu_bt5_buy_done");
			button = new ButtonEntity(MIN_GAME_ENTITY_Z_ORDER, builder);
		} else {
			ButtonEntity.Builder builder = new ButtonEntity.Builder(720, 275, 228, 63, "main_menu_bt5_buy", "main_menu_bt5_buy_pressed");
			builder.playSoundWhenClicked("mainmenu_bt_s1");
			builder.emitEventWhenPressed(GameEvent.MAIN_MENU_BUY);
			
			button = new ButtonEntity(MIN_GAME_ENTITY_Z_ORDER, builder);
			button.setDisable(game.isBillingSupported() == false);
			
			if (Config.ENABLE_DISCOUNT) {
				setupDiscountBadge();
			}
		}
		
		add(button);
		
		Animation slideIn = new TranslateAnimation(MIN_GAME_COMPONENT_Z_ORDER, 720, 275, 492, 275, 300);
		slideIn.setStartOffset(450);
		slideIn.start();
		button.add(slideIn);
		
		return button;
	}
	
	private void setupDiscountBadge() {
		SimpleEntity badge = new SimpleEntity(MIN_GAME_ENTITY_Z_ORDER + 1);
		badge.setup(436, 259, 109, 106, "main_menu_discount");
		add(badge);
	}
	
	private ButtonEntity buyButton;
	private GameEntity confirmBuyDialog;
	private GameEntity dialogProcessing;
}
