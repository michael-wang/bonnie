package com.studioirregular.bonniesbrunch.main;

import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.Game;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.InputSystem.TouchEvent;
import com.studioirregular.bonniesbrunch.TextureLibrary;
import com.studioirregular.bonniesbrunch.base.ObjectBase;
import com.studioirregular.bonniesbrunch.entity.GameEntity;
import com.studioirregular.bonniesbrunch.entity.SimpleEntity;


public abstract class GameEntityRoot extends GameEntity {

	private static final String TAG = "game-entity-root";
	
	protected Game game;
	protected Object loadParam;
	protected TextureLibrary textureLibrary;
	protected boolean textureLibraryLoading;
	protected boolean textureReloading;
	protected int textureLoadingTotalCount;
	private GameEvent pendingEvent;
	
	public GameEntityRoot(Game game) {
		super(0);
		this.game = game;
		textureReloading = false;
	}
	
	public void setLoadParam(Object param) {
		loadParam = param;
	}
	
	public void onPause() {
		
	}
	
	public abstract boolean load();
	public void onLoadComplete() {
		if (textureLibrary != null) {
			textureLibraryLoading = true;
		} else {
			textureLibraryLoading = false;
			Game.getInstance().restoreEffectFadeOut();
			onReadyToGo();
		}
	}
	
	@Override
	public boolean dispatch(TouchEvent event, GameEntity parent) {
		if (isDoingFadeOut) {
			return false;
		}
		return super.dispatch(event, parent);
	}
	
	@Override
	public void update(long timeDelta, ObjectBase parent) {
		if (textureReloading) {
			if (gameEvents.isEmpty() == false) {
				for (GameEvent event : gameEvents) {
					handleGameEvent(event);
				}
				gameEvents.clear();
			}
			commitUpdate();
			
			if (textureLoadingProgress != null) {
				textureLoadingProgress.update(timeDelta, this);
			}
			
			return;
		}
		
		super.update(timeDelta, parent);
	}
	
	// called when user press back key.
	// default behavior is do fade out, when done, call handlePendingEvent with ROOT_BACK event.
	// override if need customize behavior.
	// return true indicates handled, return false cause main activity finish.
	public boolean onBack() {
		if (pendingEvent == null) {
			doFadeOut(GameEventSystem.getInstance().obtain(GameEvent.ROOT_BACK));
		}
		return true;
	}
	
	// override if you want to show progress. (also override isOurTextureLibrary).
	protected boolean showTextureLibraryLoadingProgress(TextureLibrary library) {
		return false;
	}
	
	// override and help check if it's our library if want to show progress.
	protected boolean isOurTextureLibrary(TextureLibrary library) {
		return library == this.textureLibrary;
	}
	
	// called after texture loaded (if any)
	protected void onReadyToGo() {
		
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		switch (event.what) {
		case GameEvent.RENDERER_FADE_OUT_DONE:
		case GameEvent.TEXTURE_RELOAD_BEGIN:
		case GameEvent.TEXTURE_RELOAD_END:
		case GameEvent.TEXTURE_LOADING:
			return true;
		}
		
		if (GameEvent.TEXTURE_LIBRARY_LOAD_BEGIN == event.what || GameEvent.TEXTURE_LIBRARY_LOAD_END == event.what) {
			if (isOurTextureLibrary((TextureLibrary)event.obj)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		if (GameEvent.RENDERER_FADE_OUT_DONE == event.what) {
			isDoingFadeOut = false;
			
			if (pendingEvent != null) {
				handlePendingEvent(pendingEvent);
				pendingEvent = null;
			} else {
				if (Config.DEBUG_LOG) Log.e(TAG, "fade out done, but no pending event...");
			}
		} else if (GameEvent.TEXTURE_RELOAD_BEGIN == event.what) {
			textureLoadingTotalCount = event.arg1;
			textureReloading = true;
			showTextureLoadingProgress = true;
			updateTextureLoadingProgress(textureLoadingTotalCount, textureLoadingTotalCount);
		} else if (GameEvent.TEXTURE_RELOAD_END == event.what) {
			textureReloading = false;
			cleanUpTextureLoadingProgress();
		} else if (GameEvent.TEXTURE_LIBRARY_LOAD_BEGIN == event.what) {
			textureLoadingTotalCount = event.arg1;
			textureLibraryLoading = true;
			showTextureLoadingProgress = showTextureLibraryLoadingProgress((TextureLibrary)event.obj);
			if (showTextureLoadingProgress) {
				updateTextureLoadingProgress(textureLoadingTotalCount, textureLoadingTotalCount);
			}
		} else if (GameEvent.TEXTURE_LIBRARY_LOAD_END == event.what) {
			textureLibraryLoading = false;
			Game.getInstance().restoreEffectFadeOut();
			if (showTextureLibraryLoadingProgress((TextureLibrary)event.obj)) {
				cleanUpTextureLoadingProgress();
			}
			onReadyToGo();
		} else if (GameEvent.TEXTURE_LOADING == event.what) {
			if (showTextureLoadingProgress) {
				updateTextureLoadingProgress(textureLoadingTotalCount, event.arg1);
			}
		}
	}
	
	protected void doFadeOut(GameEvent pendingEvent) {
		Game.getInstance().doEffectFadeOut();
		this.pendingEvent = pendingEvent;
		isDoingFadeOut = true;
	}
	
	protected static final int PROGRESS_HINT_X_START= 0;
	protected static final int PROGRESS_HINT_X_END	= 720;
	protected static final int PROGRESS_HINT_Y		= 411;
	
	protected void updateTextureLoadingProgress(int total, int remainder) {
		if (Config.DEBUG_LOG) Log.d(TAG, "updateTextureLoadingProgress total:" + total + ",remainder:" + remainder);
		
		if (total <= 0) {
			if (Config.DEBUG_LOG) Log.e(TAG, "updateTextureLoadingProgress failed, total:" + total);
			return;
		}
		
		if (textureLoadingProgress == null) {
			textureLoadingProgress = new SimpleEntity(MIN_GAME_ENTITY_Z_ORDER + 100);
			textureLoadingProgress.setup(PROGRESS_HINT_X_START, PROGRESS_HINT_Y, 102, 69, "level_menu_map_car_01");
			add(textureLoadingProgress);
		}
		
		final int step = (PROGRESS_HINT_X_END - PROGRESS_HINT_X_START) / total;
		final int newX = PROGRESS_HINT_X_END - step * remainder;
		
		textureLoadingProgress.move(newX, PROGRESS_HINT_Y);
	}
	
	protected void cleanUpTextureLoadingProgress() {
		if (textureLoadingProgress != null) {
			remove(textureLoadingProgress);
			textureLoadingProgress = null;
		}
	}
	
	// derived class should handle at least ROOT_BACK event (unless it override onBack).
	// watch out: pendingEvent will be cleaned after this call, so do not issue another pending event (such as doFadeOut).
	protected abstract boolean handlePendingEvent(GameEvent event);
	
	private boolean showTextureLoadingProgress;
	private SimpleEntity textureLoadingProgress;
	protected boolean isDoingFadeOut = false;
}
