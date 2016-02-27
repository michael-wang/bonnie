package com.studioirregular.bonniesbrunch.main;

import java.util.Random;

import android.util.Log;

import com.studioirregular.bonniesbrunch.BonniesBrunchActivity;
import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.Game;
import com.studioirregular.bonniesbrunch.Game.StoredPurchaseState;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.GameScoreSystem;
import com.studioirregular.bonniesbrunch.InputSystem.TouchEvent;
import com.studioirregular.bonniesbrunch.InputSystem.Touchable;
import com.studioirregular.bonniesbrunch.LevelSystem.GameLevel;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelNumber;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelScore;
import com.studioirregular.bonniesbrunch.LevelSystem.TableConfig;
import com.studioirregular.bonniesbrunch.RequestRating;
import com.studioirregular.bonniesbrunch.SoundSystem;
import com.studioirregular.bonniesbrunch.TextureLibrary;
import com.studioirregular.bonniesbrunch.TextureSystem;
import com.studioirregular.bonniesbrunch.base.ObjectBase;
import com.studioirregular.bonniesbrunch.component.RenderComponent;
import com.studioirregular.bonniesbrunch.entity.ButtonEntity;
import com.studioirregular.bonniesbrunch.entity.CustomerManager;
import com.studioirregular.bonniesbrunch.entity.CustomerManager.SeatConfig;
import com.studioirregular.bonniesbrunch.entity.DialogConfirm;
import com.studioirregular.bonniesbrunch.entity.DialogPause;
import com.studioirregular.bonniesbrunch.entity.GameEntity;
import com.studioirregular.bonniesbrunch.entity.HeadUpDisplay;
import com.studioirregular.bonniesbrunch.entity.LevelReport;
import com.studioirregular.bonniesbrunch.entity.ModelDialog;
import com.studioirregular.bonniesbrunch.entity.Table;
import com.studioirregular.bonniesbrunch.main.GameState.GameStateHolder;


/*
 * Main game play root. Holder of entity managers: head up display (manages clock, score bar and scores),
 * pause (manages pause button and pause dialog), customer manager (manages customers), 
 * table (manages food generators, food machines and brunches).
 * 
 * It also manages customer generation by using two customer generators (FixedCustomerGenerator and RandomCustomerGenerator).
 * 
 * To reduce complex logic of main game, GameState are used to divide logic into states: starting, running, ending.
 */
public class RootMainGame extends GameEntityRoot implements GameStateHolder, PurchaseResultListener {

	private static final String TAG = "root-main-game";
	
	public RootMainGame(Game game) {
		super(game);
	}
	
	@Override
	public boolean load() {
		GameLevel level = getGameLevel();
		if (level == null) {
			return false;
		}
		
		loadTempTexture(level);
		
		setupBackground(level);
		setupCustomerManager(level);
		setupTable(level.tableConfig, level.specialLevel);
		
		headUpDisplay = new HeadUpDisplay(GameEntity.MIN_GAME_ENTITY_Z_ORDER + 2);
		headUpDisplay.setup(0,  0, 360, 60, level);
		add(headUpDisplay);
		
		ButtonEntity.Builder builder = new ButtonEntity.Builder(645, 0, 66, 48, "game_pause_01", "game_pause_01_pressed");
		builder.playSoundWhenClicked("mainmenu_bt_s1");
		builder.emitEventWhenPressed(GameEvent.GAME_PAUSE);
		builder.offsetTouchArea(-16, 0, 16, 24);
		
		pauseButton = new ButtonEntity(GameEntity.MIN_GAME_ENTITY_Z_ORDER + 3, builder);
		pauseButton.setDisableTexture("game_pause_01_locked");
		add(pauseButton);
		
		setupDialogs(MIN_GAME_ENTITY_Z_ORDER + 0x70);
		
		currentState = new GameStateStart(this);
		
		return true;
	}
	
	@Override
	protected void onReadyToGo() {
		super.onReadyToGo();
		
		// turn off banner ad
		game.getAd().turnOffBanner();
	}
	
	@Override
	public void onPause() {
		if (Config.DEBUG_LOG) Log.w(TAG, "onPause");
		
		if (pauseButton.isDisabled()) {
			return;
		}
		
		if (!gamePaused) {
			onGamePause(true);
		}
	}
	
	@Override
	public boolean onBack() {
		if (!gamePaused && pauseButton.isDisabled()) {
			return true;
		}
		
		onGamePause(!gamePaused);
		return true;
	}
	
	@Override
	public void update(long timeDelta, ObjectBase parent) {
		if (gamePaused) {
			timeDelta = 0;
		}
		
		super.update(timeDelta, parent);
		
		if (currentState != null) {
			currentState.update(timeDelta, parent);
		}
	}
	
	@Override
	public GameLevel gameLevel() {
		return getGameLevel();
	}
	
	@Override
	public SceneManager sceneManager() {
		return sceneManager;
	}
	
	@Override
	public HeadUpDisplay headUpDisplay() {
		return headUpDisplay;
	}
	
	@Override
	public Table table() {
		return table;
	}
	
	@Override
	public CustomerManager customerManager() {
		return customerManager;
	}
	
	@Override
	public LevelReport levelReport() {
		return levelReport;
	}
	
	@Override
	public void add(ObjectBase obj) {
		super.add(obj);
	}
	
	@Override
	public void remove(ObjectBase obj) {
		super.remove(obj);
	}
	
	@Override
	public void propagate(GameEvent event) {
		this.propagate(event, this);
	}
	
	@Override
	public void disablePauseButton(boolean disable) {
		if (pauseButton != null) {
			pauseButton.setDisable(disable);
		}
	}
	
	@Override
	public void changeGameState(GameState newState) {
		currentState = newState;
	}
	
	@Override
	public void onGamePause(boolean paused) {
		if (paused) {
			gamePaused = true;
			SoundSystem.getInstance().pauseAllLoopingSounds();
			pauseButton.setDisable(true);
			add(pauseDialog);
		} else {
			gamePaused = false;
			SoundSystem.getInstance().resumeAllLoopingSounds();
			remove(pauseDialog);
			pauseButton.setDisable(false);
		}
	}
	
	@Override
	public void onConfirmingEvent(GameEvent event) {
		confirmingEvent = event;
		
		if (GameEvent.GAME_RESTART == event.what) {
			add(confirmRestartDialog);
		} else if (GameEvent.GAME_BACK_TO_MENU == event.what) {
			add(confirmBackDialog);
		} else if (GameEvent.MAIN_MENU_BUY == event.what) {
			add(confirmBuyDialog);
		}
	}
	
	@Override
	public void onConfirmingEventRejected() {
		if (GameEvent.GAME_BACK_TO_MENU == confirmingEvent.what) {
			remove(confirmBackDialog);
		} else if (GameEvent.GAME_RESTART == confirmingEvent.what) {
			remove(confirmRestartDialog);
		} else if (GameEvent.MAIN_MENU_BUY == confirmingEvent.what) {
			remove(confirmBuyDialog);
			if (confirmingEvent.arg1 == 1) {
				// allow purchase later, goto next level now.
				final LevelNumber nextLevel = getGameLevel().number.getNextLevel();
				GameEventSystem.scheduleEvent(GameEvent.GAME_NEXT_LEVEL, 0, nextLevel);
			} else {
				// don't allow play anymore, back to main menu.
				final GameEvent backToMenu = GameEventSystem.getInstance().obtain(GameEvent.GAME_BACK_TO_MENU);
				doFadeOut(backToMenu);
			}
		}
	}
	
	@Override
	public void onConfirmingEventAccepted() {
		if (GameEvent.GAME_BACK_TO_MENU == confirmingEvent.what) {
			doFadeOut(confirmingEvent);
		} else if (GameEvent.GAME_RESTART == confirmingEvent.what) {
			doFadeOut(confirmingEvent);
		} else if (GameEvent.MAIN_MENU_BUY == confirmingEvent.what) {
			remove(confirmBuyDialog);
			
			Game.getInstance().requestPurchaseFullVersion();
			
			if (dialogProcessing == null) {
				ModelDialog processing = new ModelDialog(GameEntity.MIN_GAME_ENTITY_Z_ORDER + 0x80);
				processing.setup(0, 0, 720, 480);
				processing.setBackground("popup_menu_processing", 720, 480);
				dialogProcessing = processing;
			}
			add(dialogProcessing);
		}
	}
	
	@Override
	public void resetLevelState() {
		headUpDisplay.send(GameEventSystem.getInstance().obtain(GameEvent.GAME_SCORE_CLEAR));
		table.resetLevelState();
		customerManager.resetLevelState();
	}
	
	@Override
	public void releaseTempTexture() {
		if (textureLibrary != null) {
			TextureSystem.getInstance().release(textureLibrary);
			textureLibrary = null;
		}
	}
	
	@Override
	public void requestFadeOut(GameEvent pendingEvent) {
		doFadeOut(pendingEvent);
	}
	
	private GameLevel getGameLevel() {
		if (loadParam == null || !(loadParam instanceof GameLevel)) {
			return null;
		}
		return (GameLevel)loadParam;
	}
	
	private void loadTempTexture(GameLevel level) {
		if (Config.DEBUG_LOG) Log.d(TAG, "loadLevelTexture level:" + level.number.major + "-" + level.number.minor + ",level.tempTextureResource:" + level.tempTextureResource);
		
		if (level.tempTextureResource != null && level.tempTextureResource.length() > 0) {
			textureLibrary = TextureLibrary.build(game.getActivity(), level.tempTextureResource);
			if (textureLibrary != null) {
				TextureSystem.getInstance().load(textureLibrary);
			}
		}
	}
	
	private void setupBackground(GameLevel level) {
		RenderComponent bg = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		bg.setup(720, 260);
		
		final int majorLevel = level.number.major;
		
		switch (majorLevel) {
		case 1:
			bg.setup("game_bg_01");
			break;
		case 2:
			bg.setup("game_bg_02");
			break;
		case 3:
			bg.setup("game_bg_03");
			break;
		case 4:
			bg.setup("game_bg_04");
			break;
		case 5:
			bg.setup("game_bg_05");
			break;
		default:
			bg.setup("game_bg_05");
			break;
		}
		add(bg);
		
		if (level.specialLevel) {
			bg = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
			bg.setup(720, 260);
			bg.setup("game_bg_sp");
			add(bg);
		}
	}
	
	private void setupCustomerManager(GameLevel level) {
		CustomerManager manager = new CustomerManager(GameEntity.MIN_GAME_ENTITY_Z_ORDER);
		manager.setup(0, 0, 720, 277);
		
//		setupDebugButtons(manager);
		
		SeatConfig[] configs = null;
		if (level.specialLevel) {
			configs = new SeatConfig[1];
			configs[0] = new SeatConfig(218, 67, 204, 210, true);
		} else {
			configs = new SeatConfig[4];
			configs[0] = new SeatConfig(10, 67, 204, 210);
			configs[1] = new SeatConfig(192, 67, 204, 210);
			configs[2] = new SeatConfig(374, 67, 204, 210);
			configs[3] = new SeatConfig(556, 67, 204, 210);
		}
		
		manager.setup(level, configs, level.specialLevel);
		add(manager);
		
		customerManager = manager;
	}
	
//	private void setupDebugButtons(CustomerManager manager) {
//		Customer.OrderingSpec orderingSpec = new Customer.OrderingSpec();
//		orderingSpec.addOptional(Food.BAGEL);
//		orderingSpec.addOptional(Food.CROISSANT);
//		orderingSpec.addOptional(Food.MUFFIN_CIRCLE);
//		orderingSpec.addOptional(Food.MUFFIN_SQUARE);
//		orderingSpec.addOptional(Food.TOAST_WHITE);
//		orderingSpec.addOptional(Food.TOAST_BLACK);
//		orderingSpec.addOptional(Food.TOAST_YELLOW);
//		orderingSpec.addOptional(Food.LETTUCE);
//		orderingSpec.addOptional(Food.CHEESE);
//		orderingSpec.addOptional(Food.EGG);
//		orderingSpec.addOptional(Food.HAM);
//		orderingSpec.addOptional(Food.HOTDOG);
//		orderingSpec.addOptional(Food.BUTTER);
//		orderingSpec.addOptional(Food.HONEY);
//		orderingSpec.addOptional(Food.TOMATO);
//		orderingSpec.addOptional(Food.MILK);
//		orderingSpec.addOptional(Food.COFFEE);
//		
//		CustomerSpec[] triggerButtons = new CustomerSpec[10];
//		triggerButtons[0] = new CustomerSpec(Customer.Type.JOGGING_GIRL, orderingSpec);
//		triggerButtons[1] = new CustomerSpec(Customer.Type.WORKING_MAN, orderingSpec);
//		triggerButtons[2] = new CustomerSpec(Customer.Type.BALLOON_BOY, orderingSpec);
//		triggerButtons[3] = new CustomerSpec(Customer.Type.GIRL_WITH_DOG, orderingSpec);
//		triggerButtons[4] = new CustomerSpec(Customer.Type.GRANNY, orderingSpec);
//		triggerButtons[5] = new CustomerSpec(Customer.Type.SUPERSTAR_MAN, orderingSpec);
//		triggerButtons[6] = new CustomerSpec(Customer.Type.PHYSICIST, orderingSpec);
//		triggerButtons[7] = new CustomerSpec(Customer.Type.SUPERSTAR_LADY, orderingSpec);
//		triggerButtons[8] = new CustomerSpec(Customer.Type.FOOD_CRITIC, orderingSpec);
//		triggerButtons[9] = new CustomerSpec(Customer.Type.TRAMP, orderingSpec);
//		manager.debugSetup(triggerButtons);
//	}
	
	private void setupTable(TableConfig tableConfig, boolean forSpecialLevel) {
		table = new Table(GameEntity.MIN_GAME_ENTITY_Z_ORDER + 1);
		table.setup(0, 250, 720, 230, tableConfig, forSpecialLevel);
		add(table);
	}
	
	private void setupDialogs(int zOrder) {
		pauseDialog = new DialogPause(zOrder);
		pauseDialog.setup(0, 0, 720, 480);
		
		confirmBackDialog = new DialogConfirm(zOrder + 1);
		confirmBackDialog.setup(119, 106, 482, 267);
		confirmBackDialog.setContent("popup_menu_back_bg", 482, 267);
		
		confirmRestartDialog = new DialogConfirm(zOrder + 1);
		confirmRestartDialog.setup(119, 106, 482, 267);
		confirmRestartDialog.setContent("popup_menu_restart_bg", 482, 267);
		
		levelReport = new LevelReport(zOrder + 1);
		levelReport.setup(0, 0, 720, 480, (GameLevel)loadParam);
	}
	
	@Override
	public void onSentFullVersionPurchaseRequest() {
		if (Config.DEBUG_LOG) Log.w(TAG, "onPurchaseFullVersionResult");
		remove(dialogProcessing);
	}
	
	@Override
	public void onFullVersionPurchaseStateChanged(StoredPurchaseState fullVersionPurchaseState) {
		if (Config.DEBUG_LOG) Log.w(TAG, "notifyFullVersionPurchaseState fullVersionPurchaseState:" + fullVersionPurchaseState);
		
		if (levelReport() != null) {
			levelReport().prepareToReport();
		}
	}
		
	@Override
	public boolean dispatch(TouchEvent event, GameEntity parent) {
		if (isDoingFadeOut) {
			return false;
		}
		
		if (currentState != null) {
			if (currentState.allowDispatchTouchEvent() == false) {
				return false;
			}
		}
		
		final int count = objects.size();
		for (int i = count - 1; i >= 0; i--) {
			ObjectBase obj = objects.get(i);
			if (obj instanceof Touchable) {
				if (((Touchable)obj).dispatch(event, this)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean dispatch(GameEvent event, GameEntity parent) {
		if (super.dispatch(event, parent) == false) {
			if (Config.DEBUG_LOG) Log.w(TAG, "no one catch this event:" + event);
			
			if (event.what == GameEvent.DROP_GAME_ENTITY) {
				GameEventSystem.scheduleEvent(GameEvent.DROP_REJECTED, 0, event.obj);
			}
			
			return false;
		}
		return true;
	}

	@Override
	protected void getRegionColor(float[] color) {
		color[0] = 1.0f;
		color[1] = 1.0f;
		color[2] = 1.0f;
		color[3] = 1.0f;
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		if (Config.DEBUG_LOG) Log.d(TAG, "wantThisEvent event:" + event);
		
		switch (event.what) {
		case GameEvent.GAME_PAUSE:
		case GameEvent.GAME_RESUME:
		case GameEvent.MAIN_MENU_SOUND:
		case GameEvent.MAIN_MENU_MUSIC:
		case GameEvent.MAIN_MENU_BUY:
		case GameEvent.REQUEST_GOTO_NEXT_LEVEL:
			return true;
		}
		
		if (currentState != null && currentState.wantThisEvent(event)) {
			return true;
		}
		
		return super.wantThisEvent(event);
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		if (Config.DEBUG_LOG) Log.d(TAG, "handleGameEvent event:" + event);
		
		super.handleGameEvent(event);
		
		if (GameEvent.GAME_PAUSE == event.what) {
			onGamePause(true);
		} else if (GameEvent.GAME_RESUME == event.what) {
			onGamePause(false);
		} else if (GameEvent.MAIN_MENU_MUSIC == event.what) {
			if (event.arg1 == 0) {
				SoundSystem.getInstance().disableMusic(true);
			} else {
				SoundSystem.getInstance().disableMusic(false);
				if (currentState != null) {
					currentState.playMusic();
				}
			}
			return;
		} else if (GameEvent.MAIN_MENU_SOUND == event.what) {
			if (event.arg1 == 0) {
				SoundSystem.getInstance().disableSound(true);
			} else {
				SoundSystem.getInstance().disableSound(false);
			}
			return;
		} else if (GameEvent.REQUEST_GOTO_NEXT_LEVEL == event.what) {
			if (!requestRating()) {
				handleRequestGotoNextLevel();
			}
		}
		
		if (currentState != null) {
			currentState.handleGameEvent(event);
		}
	}
	
	@Override
	protected boolean handlePendingEvent(GameEvent event) {
		if (currentState != null) {
			if (currentState.handlePendingEvent(event)) {
				return true;
			}
		}
		
		if (GameEvent.GAME_BACK_TO_MENU == event.what) {
			game.gotoMainMenu();
			
			// turn on ad
			game.getAd().turnOnBanner();
			return true;
		} else if (GameEvent.GAME_RESTART == event.what) {
			GameLevel level = getGameLevel();
			game.gotoGameLevel(level.number);
			game.getAd().turnOnBanner();
			return true;
		}
		return false;
	}
	
	/* ask players to give us 5-star rating if they haven't done so.
	 * only ask them to do so if:
	 *   1) they did well (score >= some threshold)
	 *   2) they had played a few levels (major, minor) >= (Config.START_REQUEST_RATING_MAJOR, START_REQUEST_RATING_MINOR)
	 *   3) occasionally (Config.REQUEST_RATING_POSSIBILITY / 100 if above rules meet)
	 *   
	 * return true if asking user for rating, false if not so you can proceed. 
	 */
	private boolean requestRating() {
		final GameLevel level = gameLevel();
		final int score = GameScoreSystem.getInstance().getAccumulatedMoney() + GameScoreSystem.getInstance().getAccumulatedTip();
		
		final boolean playerDoingWell = score >= getRequestRatingScoreThreshold();
		final boolean playedEnoughLevel = ((Config.START_REQUEST_RATING_MAJOR < level.number.major) || 
				(Config.START_REQUEST_RATING_MAJOR == level.number.major &&
				 Config.START_REQUEST_RATING_MINOR <= level.number.minor));
		
		if (playerDoingWell && playedEnoughLevel) {
			final boolean occasionally = new Random().nextInt(100) <= Config.REQUEST_RATING_POSSIBILITY;
			if (!occasionally) {
				return false;
			}
			
			RequestRating rating = new RequestRating();
			if (!rating.alreadyRated(Game.getInstance().getActivity())) {
				RequestRating.RequestResultListener listener = new RequestRating.RequestResultListener() {

					@Override
					public void onAccept() {
					}

					@Override
					public void onReject() {
						handleRequestGotoNextLevel();
					}
					
				};
				rating.requestRating((BonniesBrunchActivity)Game.getInstance().getActivity(), listener);
				return true;
			}
		}
		return false;
	}
	
	private int getRequestRatingScoreThreshold() {
		final LevelScore levelScore = gameLevel().score;
		
		switch (Config.START_REQUEST_STAR_COUNT) {
		case 1:
			return levelScore.min;
		case 2:
			return levelScore.med;
		case 3:
			return levelScore.high;
		default:
			return levelScore.max;
		}
	}
	
	private void handleRequestGotoNextLevel() {
		final boolean fullVersion = Game.getInstance().getFullVersionPurchaseState() == StoredPurchaseState.PURCHASED;
		
		final LevelNumber currentLevel = getGameLevel().number;
		final LevelNumber nextLevel = currentLevel.getNextLevel();
		if (fullVersion) {
			GameEventSystem.scheduleEvent(GameEvent.GAME_NEXT_LEVEL, 0, nextLevel);
			return;
		}
		
		PurchasePolicy policy = new PurchasePolicy();
		
		if (policy.isFreeToPlayLevel(nextLevel)) {
			if (policy.showPurchaseHint(currentLevel)) {
				confirmBuyDialog = buildHintPurchaseDialog();
				onConfirmingEvent(GameEventSystem.getInstance().obtain(GameEvent.MAIN_MENU_BUY, 1));
			} else {
				GameEventSystem.scheduleEvent(GameEvent.GAME_NEXT_LEVEL, 0, nextLevel);
			}
		} else {
			confirmBuyDialog = buildForcePurchaseDialog();
			onConfirmingEvent(GameEventSystem.getInstance().obtain(GameEvent.MAIN_MENU_BUY, 0));
		}
	}
	
	private DialogConfirm buildHintPurchaseDialog() {
		if (Config.DEBUG_LOG) Log.w(TAG, "buildHintPurchaseDialog");
		
		DialogConfirm dialog = new DialogConfirm(GameEntity.MIN_GAME_ENTITY_Z_ORDER + 0x80);
		dialog.setup(0, 0, 720, 480, "popup_menu_bt2_yes_disable", "popup_menu_bt2_yes_enable",
				"popup_menu_bt3_later_disable", "popup_menu_bt3_later_enable");
		dialog.setContent("popup_menu_buy_bg", 720, 480);
		return dialog;
	}
	
	private DialogConfirm buildForcePurchaseDialog() {
		if (Config.DEBUG_LOG) Log.w(TAG, "buildForcePurchaseDialog");
		
		DialogConfirm dialog = new DialogConfirm(GameEntity.MIN_GAME_ENTITY_Z_ORDER + 0x80);
		dialog.setup(0, 0, 720, 480, "popup_menu_bt2_yes_disable", "popup_menu_bt2_yes_enable",
				"popup_menu_bt4_exit_disable", "popup_menu_bt4_exit_enable");
		dialog.setContent("popup_menu_buy_bg", 720, 480);
		return dialog;
	}
	
	private GameState currentState = null;
	private Table table;
	private SceneManager sceneManager = new SceneManager(this);
	
	private CustomerManager customerManager;
	
	private boolean gamePaused;
	private HeadUpDisplay headUpDisplay;
	private ButtonEntity pauseButton;
	private DialogPause pauseDialog;
	private DialogConfirm confirmBackDialog;
	private DialogConfirm confirmRestartDialog;
	private GameEvent confirmingEvent;
	private LevelReport levelReport;
	
	// for purchase full version.
	private GameEntity confirmBuyDialog;
	private GameEntity dialogProcessing;
}
