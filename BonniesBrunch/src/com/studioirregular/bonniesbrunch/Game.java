package com.studioirregular.bonniesbrunch;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

import com.studioirregular.bonniesbrunch.LevelSystem.GameLevel;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelNumber;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelXmlFormatException;
import com.studioirregular.bonniesbrunch.ad.BonniesAdvertisement;
import com.studioirregular.bonniesbrunch.billing.BonnieSecurity;
import com.studioirregular.bonniesbrunch.billing.Constants;
import com.studioirregular.bonniesbrunch.billing.Constants.ClientRequest;
import com.studioirregular.bonniesbrunch.billing.InAppBillingService;
import com.studioirregular.bonniesbrunch.billing.ServiceResponseHandler;
import com.studioirregular.bonniesbrunch.main.GameEntityRoot;
import com.studioirregular.bonniesbrunch.main.PurchaseResultListener;
import com.studioirregular.bonniesbrunch.main.RootComics;
import com.studioirregular.bonniesbrunch.main.RootComics.Comic;
import com.studioirregular.bonniesbrunch.main.RootCredit;
import com.studioirregular.bonniesbrunch.main.RootHelp;
import com.studioirregular.bonniesbrunch.main.RootMainGame;
import com.studioirregular.bonniesbrunch.main.RootMainMenu;
import com.studioirregular.bonniesbrunch.main.RootSelectMajorLevel;
import com.studioirregular.bonniesbrunch.main.RootSelectMinorLevel;
import com.studioirregular.bonniesbrunch.main.RootSplash;

public class Game {

	private static final String TAG = "game";
	
	// singleton
	public static Game getInstance() {
		if (sInstance == null) {
			sInstance = new Game();
		}
		return sInstance;
	}
	
	public static void releaseInstance() {
		if (sInstance != null) {
			sInstance = null;
		}
	}
	
	private static Game sInstance = null;
	
	private Game() {
		if (Config.DEBUG_LOG) Log.w(TAG, "Game allocated!!");
	}
	
	public void bootstrap(BonniesBrunchActivity context) {
		if (Config.DEBUG_LOG) Log.d(TAG, "bootstrap");
		
		this.context = context;
		
		if (bootstrapDone) {
			return;
		}
		
		renderer = new GameRenderer(context, this);
		root = new RootSplash(this);
		
		gameThread = new GameThread("game-thread", renderer);
		gameThread.setRoot(root);
		
		RenderSystem.getInstance().setup(context);
		
		if (GameScoreSystem.getInstance().loadGameScores(context) == false) {
			Log.e(TAG, "bootstrap: load game score failed!");
		}
		
		if (GameScoreSystem.getInstance().loadLevelsScoreThresholds(context) == false) {
			Log.e(TAG, "bootstrap: load game score threshold failed!");
		}
		
		SoundSystem.getInstance().setup(context);
		String[] sounds = context.getResources().getStringArray(R.array.sounds_list);
		for (String sound : sounds) {
			SoundSystem.getInstance().load(sound);
		}
		
		final boolean turnSoundOff = UserPreferences.getSoundPreference(context, false);
		SoundSystem.getInstance().disableSound(turnSoundOff);
		
		final boolean turnMusicOff = UserPreferences.getMusicPreference(context, false);
		SoundSystem.getInstance().disableMusic(turnMusicOff);
		
		final int DEFAULT_SCROLL_OFFSET = Config.ENABLE_PROMOTION_FOR_TEE_SHIRT ? -171 : 189;
		if (UserPreferences.hasCarOffsetPreference(context)) {
			majorLevelScrollOffset = UserPreferences.getCarOffsetPreference(context, -DEFAULT_SCROLL_OFFSET);
		} else {
			majorLevelScrollOffset = DEFAULT_SCROLL_OFFSET;
		}
		
		billingRequestObserver = new BonnieBillingRequestObserver(context, this);
		ServiceResponseHandler.registerObserver(billingRequestObserver);
		
		checkBillingSupport();
		
		if (getFullVersionPurchaseState(context) == StoredPurchaseState.UNKNOWN) {
			requestRestoreTransaction();
		}
		
		bootstrapDone = true;
		if (Config.DEBUG_LOG) Log.d(TAG, "bootstrap done");
	}
	
	public String getAndroidId() {
		return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	}
	
	public GameRenderer getRenderer() {
		if (Config.DEBUG_LOG) Log.d(TAG, "getRenderer");
		
		assert (renderer != null);
		return renderer;
	}
	
	public void onSurfaceReady() {
		if (Config.DEBUG_LOG) Log.d(TAG, "onSurfaceReady");
		
		if (!running) {
			start();
		} else {
			gameThread.resumeGame();
		}
	}
	
	public void start() {
		if (Config.DEBUG_LOG) Log.d(TAG, "start");
		
		if (gameThread != null) {
			gameThread.start();
			gameThread.startGame();
		}
		running = true;
	}
	
	public void stop() {
		if (Config.DEBUG_LOG) Log.d(TAG, "stop");
		
		running = false;
		
		ServiceResponseHandler.unregisterObserver(billingRequestObserver);
		
		if (gameThread != null) {
			gameThread.stopGame();
		}
		
		UserPreferences.saveSoundPreference(context, SoundSystem.getInstance().isSoundDisabled());
		UserPreferences.saveMusicPreference(context, SoundSystem.getInstance().isMusicDisabled());
		UserPreferences.saveCarOffset(context, majorLevelScrollOffset);
		
		SoundSystem.getInstance().stop();
		SoundSystem.releaseInstance();
		GameScoreSystem.releaseInstance();
		RenderSystem.releaseInstance();
		TextureSystem.releaseInstance();
		InputSystem.releaseInstance();
		releaseInstance();
		
		this.billingRequestObserver = null;
		this.gameThread = null;
		this.root = null;
		this.renderer = null;
		this.context = null;
		this.ads = null;
	}
	
	public void onPause() {
		if (Config.DEBUG_LOG) Log.d(TAG, "onPause");
		
		SoundSystem.getInstance().onPause();
		
		if (root != null) {
			root.onPause();
		}
		
		if (running && gameThread != null) {
			gameThread.pauseGame();
		}
	}
	
	public void onResume() {
		if (Config.DEBUG_LOG) Log.d(TAG, "onResume");
		
		if (appHasFocus) {
			SoundSystem.getInstance().onResume();
		}
		
		// rely onSurfaceReady
	}
	
	public void onAppFocusChanged(boolean hasFocus) {
		if (Config.DEBUG_LOG) Log.d(TAG, "onAppFocusChanged hasFocus:" + hasFocus);
		
		if (hasFocus && !appHasFocus) {
			SoundSystem.getInstance().onResume();
		}
		appHasFocus = hasFocus;
	}
	
	public boolean onBack() {
		if (root != null) {
			return root.onBack();
		}
		return false;
	}
	
	public void upadteFPS(float fps) {
		context.updateFPS(fps);
	}
	
	public void gotoMainMenu() {
		root = new RootMainMenu(this);
		gameThread.setRoot(root);
	}
	
	public void gotoHelp() {
		root = new RootHelp(this);
		gameThread.setRoot(root);
	}
	
	public void gotoCredit() {
		root = new RootCredit(this);
		gameThread.setRoot(root);
	}
	
	public void gotoMajorLevelSelection() {
		root = new RootSelectMajorLevel(this, majorLevelScrollOffset);
		gameThread.setRoot(root);
	}
	
	public void gotoMinorLevelSelection(int level) {
		root = new RootSelectMinorLevel(this);
		root.setLoadParam(level);
		gameThread.setRoot(root);
	}
	
	public void gotoGameLevel(LevelNumber levelNumber) {
		if (Config.DEBUG_LOG) Log.d(TAG, "goto level:" + levelNumber.major + "-" + levelNumber.minor);
		
		final int major = levelNumber.major;
		final int minor = levelNumber.minor;
		if (major == 1 && minor == 1) {
			root = new RootComics(this, Comic.STORY_START);
			gameThread.setRoot(root);
		} else if (major == 2 && minor == 1) {
			root = new RootComics(this, Comic.STAGE_2_START);
			gameThread.setRoot(root);
		} else if (major == 3 && minor == 1) {
			root = new RootComics(this, Comic.STAGE_3_START);
			gameThread.setRoot(root);
		} else if (major == 4 && minor == 1) {
			root = new RootComics(this, Comic.STAGE_4_START);
			gameThread.setRoot(root);
		} else if (major == 5 && minor == 1) {
			root = new RootComics(this, Comic.STAGE_5_START);
			gameThread.setRoot(root);
		} else {
			play(levelNumber);
		}
		
		LicensingCheckManager.MyPolicy.checkWhenNecessary(context);
	}
	
	public void comicEnds(Comic comic) {
		if (Comic.STORY_START == comic) {
			root = new RootComics(this, Comic.STAGE_1_START);
			gameThread.setRoot(root);
		} else if (Comic.STAGE_1_START == comic) {
			LevelNumber level = new LevelNumber(1, 1);
			play(level);
		} else if (Comic.STAGE_2_START == comic) {
			LevelNumber level = new LevelNumber(2, 1);
			play(level);
		} else if (Comic.STAGE_3_START == comic) {
			LevelNumber level = new LevelNumber(3, 1);
			play(level);
		} else if (Comic.STAGE_4_START == comic) {
			LevelNumber level = new LevelNumber(4, 1);
			play(level);
		} else if (Comic.STAGE_5_START == comic) {
			LevelNumber level = new LevelNumber(5, 1);
			play(level);
		} else if (Comic.STORY_END == comic) {
			gotoMainMenu();
		}
	}
	
	public void play(LevelNumber levelNumber) {
		if (Config.DEBUG_LOG) Log.d(TAG, "play game level:" + levelNumber.major + "-" + levelNumber.minor);
		
		GameLevel level = loadLevel(levelNumber.major, levelNumber.minor);
		if (level != null) {
			root = new RootMainGame(this);
			root.setLoadParam(level);
			gameThread.setRoot(root);
		} else {
			if (Config.DEBUG_LOG) Log.e(TAG, "no level config for major:" + levelNumber.major + ",minor:" + levelNumber.minor);
		}
	}
	
	public void storyEnd() {
		root = new RootComics(this, Comic.STORY_END);
		gameThread.setRoot(root);
	}
	
	public Activity getActivity() {
		return context;
	}
	
	public void rememberMajorLevelScrollOffset(int carOffset) {
		majorLevelScrollOffset = carOffset;
	}
	
	public void requestToastMessage(int resId, int durationType) {
		context.toastMessage(resId, durationType);
	}
	
	public void setAdvertisement(BonniesAdvertisement ads) {
		this.ads = ads;
	}
	
	public BonniesAdvertisement getAd() {
		return ads;
	}
	
	public void notifyBillingSupport(boolean supported) {
		this.billingSupported = supported;
		if (root instanceof RootMainMenu) {
			((RootMainMenu)root).notifyBillingSupport(supported);
		}
	}
	
	public boolean isBillingSupported() {
		return this.billingSupported;
	}
	
	public StoredPurchaseState getFullVersionPurchaseState() {
		if (Config.DEBUG_UNLOCK_EVERYTHING) {
			return StoredPurchaseState.PURCHASED;
		}
		
		if (context == null) {
			if (Config.DEBUG_LOG) Log.e(TAG, "getFullVersionPurchaseState game not bootstrap!");
			return StoredPurchaseState.UNKNOWN;
		}
		return getFullVersionPurchaseState(context);
	}
	
	public boolean requestPurchaseFullVersion() {
		if (!billingSupported) {
			requestToastMessage(R.string.in_app_billing_not_supported, Toast.LENGTH_LONG);
			return false;
		}
		
		Intent requestPurchase = new Intent(ClientRequest.ACTION_REQUEST_PURCHASE);
		requestPurchase.setClass(context, InAppBillingService.class);
		requestPurchase.putExtra(ClientRequest.EXTRA_PRODUCT_ID, Config.IN_APP_BILLING_PRODUCT_FULL_VERSION);
		context.startService(requestPurchase);
		
		return true;
	}
	
	public void notifyRequestPurchaseResult(String productId, boolean success) {
		if (Config.DEBUG_LOG) Log.w(TAG, "notifyRequestPurchaseResult productId:" + productId + ",success:" + success);
		
		if (root instanceof PurchaseResultListener) {
			((PurchaseResultListener)root).onSentFullVersionPurchaseRequest();
		}
		
		Toast.makeText(context, 
				success ? R.string.purchase_full_version_success : R.string.purchase_full_version_canceled, 
				Toast.LENGTH_LONG)
			.show();
	}
	
	public void notifyPurchasedStateChanged(String productId) {
		if (Config.DEBUG_LOG) Log.w(TAG, "notifyPurchasedStateChanged productId:" + productId);
		
		if (Config.IN_APP_BILLING_PRODUCT_FULL_VERSION.equals(productId) == false) {
			return;
		}
		
		StoredPurchaseState purchaseState = getFullVersionPurchaseState(context);
		
		if (ads != null) {
			ads.notifyFullVersionPurchaseStateChanged();
		}
		
		if (root instanceof PurchaseResultListener) {
			((PurchaseResultListener)root).onFullVersionPurchaseStateChanged(purchaseState);
		}
		
		if (purchaseState.equals(StoredPurchaseState.NOT_PURCHASED)) {
			Toast.makeText(context, R.string.purchase_full_version_canceled, Toast.LENGTH_LONG)
				.show();
		}
	}
	
	private static final int MAX_RETRY_COUNT_FOR_RESTORE_TRANSACTION = 4;
	private int retryCountForRestoreTransaction = 0;
	
	public void notifyRestoreTransactionFailed() {
		if (Config.DEBUG_LOG) Log.w(TAG, "notifyRestoreTransactionFailed, try again.");
		
		retryCountForRestoreTransaction++;
		if (retryCountForRestoreTransaction <= MAX_RETRY_COUNT_FOR_RESTORE_TRANSACTION) {
			requestRestoreTransaction();
		} else {
			if (Config.DEBUG_LOG) Log.w(TAG, "failed to restore purchase transaction.");
		}
	}
	
	private void requestRestoreTransaction() {
		Intent restorePurchaseTransaction = new Intent(Constants.ClientRequest.ACTION_RESTORE_TRANSACTIONS);
		restorePurchaseTransaction.setClass(context, InAppBillingService.class);
		context.startService(restorePurchaseTransaction);
	}
	
	private GameLevel loadLevel(int majorLevel, int minorLevel) {
		if (Config.DEBUG_LOG) Log.w(TAG, "loadLevel mojor:" + majorLevel + ",minor:" + minorLevel);
		
		GameLevel level = null;
		try {
			level = LevelSystem.load(context, majorLevel, minorLevel);
		} catch (LevelXmlFormatException e) {
			if (Config.DEBUG_LOG) Log.w(TAG, "loadLevel failed, e:" + e);
		} catch (XmlPullParserException e) {
			if (Config.DEBUG_LOG) Log.w(TAG, "loadLevel failed, e:" + e);
		} catch (IOException e) {
			if (Config.DEBUG_LOG) Log.w(TAG, "loadLevel failed, e:" + e);
		}
		
		return level;
	}
	
	private void checkBillingSupport() {
        Intent checkBillingSupport = new Intent(ClientRequest.ACTION_CHECK_BILLING_SUPPORTED);
        checkBillingSupport.setClass(context, InAppBillingService.class);
        context.startService(checkBillingSupport);
	}
	
	public static enum StoredPurchaseState {
		UNKNOWN,
		NOT_PURCHASED,
		PURCHASED
	}
	
	private StoredPurchaseState getFullVersionPurchaseState(Context context) {
		final String productId = Config.IN_APP_BILLING_PRODUCT_FULL_VERSION;
		StoredPurchaseState result = StoredPurchaseState.UNKNOWN;
		
		SharedPreferences pref = context.getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
		if (pref.contains(productId) == false) {
			result = StoredPurchaseState.UNKNOWN;
		} else {
			String obtainedValue = pref.getString(productId, "");
			String purchasedValue = BonnieSecurity.enchantPurchaseInfo(context, productId, true);
			result = purchasedValue.equals(obtainedValue) ? StoredPurchaseState.PURCHASED : StoredPurchaseState.NOT_PURCHASED;
		}
		return result;
	}
	
	public void doEffectFadeOut() {
		if (renderer != null) {
			renderer.doEffectFadeOut();
		}
	}
	
	public void restoreEffectFadeOut() {
		if (renderer != null) {
			renderer.restoreEffectFadeOut();
		}
	}
	
	private BonniesBrunchActivity context;
	private GameRenderer renderer;
	private GameThread gameThread;
	private GameEntityRoot root;
	private BonnieBillingRequestObserver billingRequestObserver;
	
	private boolean bootstrapDone = false;
	private boolean running = false;
	private boolean appHasFocus = false;
	
	// remember player's last scroll offset, 
	// so we can stay there next time.
	private int majorLevelScrollOffset;
	
	private boolean billingSupported = false;
	
	private BonniesAdvertisement ads;
}
