package com.studioirregular.bonniesbrunch.main;

import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.Game;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.GameScoreSystem;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelNumber;
import com.studioirregular.bonniesbrunch.SoundSystem;
import com.studioirregular.bonniesbrunch.SoundSystem.Sound;
import com.studioirregular.bonniesbrunch.ad.BonniesAdvertisement;
import com.studioirregular.bonniesbrunch.ad.BonniesAdvertisement.FullScreenAdListener;
import com.studioirregular.bonniesbrunch.base.EventDrivenPhase;
import com.studioirregular.bonniesbrunch.base.NullPhase;
import com.studioirregular.bonniesbrunch.base.ObjectBase;
import com.studioirregular.bonniesbrunch.base.PhasedObject;
import com.studioirregular.bonniesbrunch.base.TimeFramedPhase;

public class GameStateEnd extends GameState implements PhasedObject.PhaseObjectListener {

	private static final String TAG = "game-state-end";
	
	public GameStateEnd(GameStateHolder holder) {
		super(holder);
	}
	
	@Override
	public void start() {
		if (Config.ENABLE_BANNER_AD) {
			Game.getInstance().getAd().turnOnBanner();
		}
		
		if (Config.ENABLE_INTERSTITAL_AD) {
			adListener = new AdListener();
			Game.getInstance().getAd().registerListener(adListener);
			Game.getInstance().getAd().prepareFullScreenAd();
		}
		
		newScoreRecord = GameScoreSystem.getInstance().saveLevelScore(Game.getInstance().getActivity(), holder.gameLevel().number);
		
		phase = createPhasedObject(ENDING_TEXT);
		
		Sound soundShopClosed = SoundSystem.getInstance().load("game_text_2_close_s1");
		SoundSystem.getInstance().playSound(soundShopClosed, false);
	}
	
	@Override
	public void playMusic() {
		SoundSystem.getInstance().playMusic("bgm_stat", false);
	}
	
	@Override
	public void update(long timeDelta, ObjectBase parent) {
		super.update(timeDelta, parent);
		
		if (phase != null) {
			phase.update(timeDelta, parent);
		}
		
		if (gotoNextLevel) {
			gotoNextLevel = false;
			
			final LevelNumber nextLevel = (LevelNumber)pendingEventForAd.obj;
			Game.getInstance().gotoGameLevel(nextLevel);
		} else if (restartLevel) {
			restartLevel = false;
			
			Game.getInstance().gotoGameLevel(holder.gameLevel().number);
		}
	}
	
	@Override
	public boolean wantThisEvent(GameEvent event) {
		if (Config.DEBUG_LOG) Log.d(TAG, "wantThisEvent event:" + event);
		if (phase != null && phase.handleGameEvent(event)) {
			return true;
		}
		
		switch (event.what) {
		case GameEvent.GAME_RESTART:
		case GameEvent.GAME_EXIT:
		case GameEvent.GAME_NEXT_LEVEL:
		case GameEvent.GAME_CONFIRM_YES:
		case GameEvent.GAME_CONFIRM_NO:
			return true;
		}
		return false;
	}
	
	@Override
	public void handleGameEvent(GameEvent event) {
		switch (event.what) {
		case GameEvent.GAME_RESTART:
		case GameEvent.GAME_EXIT:
		case GameEvent.GAME_NEXT_LEVEL:
			holder.requestFadeOut(event);
		}
		
		if (GameEvent.GAME_CONFIRM_NO == event.what) {
			holder.onConfirmingEventRejected();
		} else if (GameEvent.GAME_CONFIRM_YES == event.what) {
			holder.onConfirmingEventAccepted();
		}
	}
	
	@Override
	public boolean handlePendingEvent(GameEvent pendingEvent) {
		if (Config.DEBUG_LOG) Log.w(TAG, "handlePendingEvent event:" + pendingEvent);
		
		if (GameEvent.GAME_RESTART == pendingEvent.what) {
			requestRestartLevel(pendingEvent);
			return true;
		} else if (GameEvent.GAME_EXIT == pendingEvent.what) {
			Game.getInstance().gotoMinorLevelSelection(holder.gameLevel().number.major);
			return true;
		} else if (GameEvent.GAME_NEXT_LEVEL == pendingEvent.what) {
			LevelNumber nextLevel = (LevelNumber)pendingEvent.obj;
			if (nextLevel.isGameOver()) {
				Game.getInstance().storyEnd();
			} else {
				requestGotoNextLevel(pendingEvent);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean allowDispatchTouchEvent() {
		return enableTouch;
	}
	
//	private void handlePhase(int phase) {
//		switch (phase) {
//		case ENDING_TEXT:
//			showGameText("game_text_2_close", 544, 110, 88, 185);
//			enableTouch = false;
//			break;
//		case PHASE_CUSTOMER_LOST:
//			holder.disablePauseButton(true);
//			holder.add(holder.levelReport());
//			holder.levelReport().prepareToReport();
//			enableTouch = true;
//			
//			holder.levelReport().showCustomerLost();
//			break;
//		case PHASE_CUSTOMER_SERVED:
//			holder.levelReport().showCustomerServed();
//			break;
//		case PHASE_MONEY_EARNED:
//			holder.levelReport().showMoneyEarned();
//			break;
//		case PHASE_TIPS_EARNED:
//			holder.levelReport().showTipsEarned();
//			break;
//		case PHASE_TOTAL_SCORE:
//			holder.levelReport().showTotalMoneyEarned();
//			break;
//		case PHASE_STAR_EARNED:
//			holder.levelReport().showStars();
//			break;
//		case PHASE_FLAME_EFFECT:
//			holder.levelReport().showAnimatedBonnie();
//			holder.levelReport().showFlameEffect();
//			break;
//		}
//	}
	
	@Override
	public void onNextPhase(PhasedObject phaseObject) {
		if (Config.DEBUG_LOG) Log.d(TAG, "onNextPhase phase:" + phaseObject.getPhase());
		
		if (PHASE_FINAL != phaseObject.getPhase()) {
			phase = createPhasedObject(phaseObject.getPhase() + 1);
		}
	}
	
	private PhasedObject createPhasedObject(int phase) {
		PhasedObject result = null;
		switch (phase) {
		case ENDING_TEXT:
			result = new EventDrivenPhase(ENDING_TEXT, this, GameEventSystem.getInstance().obtain(GameEvent.ENTITY_DONE, 0)) {

				@Override
				protected void onStart() {
					if (Config.DEBUG_LOG) Log.d(TAG, "ENDING_TEXT onStart");
					showGameText("game_text_2_close", 544, 110, 88, 185, 0);
				}
				
			};
			break;
		case PHASE_PREPARE_REPORT:
			result = new TimeFramedPhase(PHASE_PREPARE_REPORT, this, 100) {
				@Override
				protected void onStart() {
					if (Config.DEBUG_LOG) Log.d(TAG, "PHASE_PREPARE_REPORT onStart");
					holder.disablePauseButton(true);
					holder.add(holder.levelReport());
					holder.levelReport().prepareToReport();
					
					playMusic();
				}
				
			};
			break;
		case PHASE_CUSTOMER_LOST:
			result = new TimeFramedPhase(PHASE_CUSTOMER_LOST, this, REPORT_OTHERS_DURATION) {
				@Override
				protected void onStart() {
					if (Config.DEBUG_LOG) Log.d(TAG, "PHASE_CUSTOMER_LOST onStart");
					holder.levelReport().showCustomerLost();
				}
				
			};
			break;
		case PHASE_CUSTOMER_SERVED:
			result = new TimeFramedPhase(PHASE_CUSTOMER_SERVED, this, REPORT_OTHERS_DURATION) {
				@Override
				protected void onStart() {
					holder.levelReport().showCustomerServed();
				}
				
			};
			break;
		case PHASE_MONEY_EARNED:
			result = new TimeFramedPhase(PHASE_MONEY_EARNED, this, REPORT_OTHERS_DURATION) {
				@Override
				protected void onStart() {
					holder.levelReport().showMoneyEarned();
				}
				
			};
			break;
		case PHASE_TIPS_EARNED:
			result = new TimeFramedPhase(PHASE_TIPS_EARNED, this, REPORT_OTHERS_DURATION) {
				@Override
				protected void onStart() {
					holder.levelReport().showTipsEarned();
				}
				
			};
			break;
		case PHASE_TOTAL_SCORE:
			result = new TimeFramedPhase(PHASE_TOTAL_SCORE, this, REPORT_TOTAL_SCORE_DURATION) {
				@Override
				protected void onStart() {
					if (Config.DEBUG_LOG) Log.d(TAG, "PHASE_TOTAL_SCORE onStart");
					holder.levelReport().showTotalMoneyEarned(REPORT_TOTAL_SCORE_DURATION);
				}
				
			};
			break;
		case PHASE_STAR_EARNED:
			result = new EventDrivenPhase(PHASE_STAR_EARNED, this, GameEventSystem.getInstance().obtain(GameEvent.ENTITY_DONE, 0)) {
				@Override
				protected void onStart() {
					if (Config.DEBUG_LOG) Log.d(TAG, "PHASE_STAR_EARNED onStart");
					holder.levelReport().showStars(0);
				}
				
			};
			break;
		case PHASE_FLAME_EFFECT:
			result = new EventDrivenPhase(PHASE_FLAME_EFFECT, this, GameEventSystem.getInstance().obtain(GameEvent.ENTITY_DONE, 0)) {
				@Override
				protected void onStart() {
					if (Config.DEBUG_LOG) Log.d(TAG, "PHASE_FLAME_EFFECT onStart");
					if (newScoreRecord) {
						holder.levelReport().stampNewScoreRecord();
					}
					holder.levelReport().showAnimatedBonnie();
					holder.levelReport().showFlameEffect(0);
				}
				
			};
			break;
		case PHASE_FINAL:
			result = new NullPhase(PHASE_FINAL, this);
			break;
		}
		return result;
	}
	
	private void requestRestartLevel(GameEvent event) {
		final BonniesAdvertisement ad = Game.getInstance().getAd();
		if (ad.isFullScreenAdReady()) {
			
			pendingEventForAd = event;
			
			ad.showFullScreenAd();
		} else {
			Game.getInstance().gotoGameLevel(holder.gameLevel().number);
		}
	}
	
	private void requestGotoNextLevel(GameEvent event) {
		final BonniesAdvertisement ad = Game.getInstance().getAd();
		if (ad.isFullScreenAdReady()) {
			
			pendingEventForAd = event;
			
			ad.showFullScreenAd();
		} else {
			Game.getInstance().gotoGameLevel((LevelNumber)event.obj);
		}
	}
	
	private boolean enableTouch = true;
	
	private static final int ENDING_TEXT			= 1;
	private static final int PHASE_PREPARE_REPORT	= 2;
	private static final int PHASE_CUSTOMER_LOST	= 3;
	private static final int PHASE_CUSTOMER_SERVED	= 4;
	private static final int PHASE_MONEY_EARNED		= 5;
	private static final int PHASE_TIPS_EARNED		= 6;
	private static final int PHASE_TOTAL_SCORE		= 7;
	private static final int PHASE_STAR_EARNED		= 8;
	private static final int PHASE_FLAME_EFFECT		= 9;
	private static final int PHASE_FINAL			= 10;
	
	private static final int REPORT_TOTAL_SCORE_DURATION = 1300;
	private static final int REPORT_OTHERS_DURATION = 500;
	
	private PhasedObject phase;
	private boolean newScoreRecord = false;
	
	private class AdListener extends FullScreenAdListener {

		@Override
		public void onReturnFromAd() {
			if (GameEvent.GAME_RESTART == pendingEventForAd.what) {
				restartLevel = true;
			} else if (GameEvent.GAME_NEXT_LEVEL == pendingEventForAd.what) {
				gotoNextLevel = true;
			}
		}
	}
	
	private AdListener adListener;
	
	// game.gotoGameLevel need to be called from game thread.
	private boolean gotoNextLevel = false;
	private boolean restartLevel = false;
	private GameEvent pendingEventForAd;
}
