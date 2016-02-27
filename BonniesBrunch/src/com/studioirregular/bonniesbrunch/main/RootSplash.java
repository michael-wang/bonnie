package com.studioirregular.bonniesbrunch.main;

import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.Game;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.R;
import com.studioirregular.bonniesbrunch.TextureLibrary;
import com.studioirregular.bonniesbrunch.TextureSystem;
import com.studioirregular.bonniesbrunch.base.ObjectBase;
import com.studioirregular.bonniesbrunch.component.RenderComponent;

/* 
 * Load main textures, in the same time,
 * Display funnlylab logo and then studio irregular logo for a defined of period (in Config.java).
 * If texture still loading after logo pages, switch to loading page: 
 * background image with car running from left to right at the bottom of screen.
 */
public class RootSplash extends GameEntityRoot {

	private static final String TAG = "root-splash";
	
	public RootSplash(Game game) {
		super(game);
	}
	
	@Override
	public boolean load() {
		textureLibrary = TextureLibrary.build(game.getActivity(), R.xml.textures_splash);
		TextureSystem.getInstance().load(textureLibrary);
		mainTextureLoadingDone = false;
		return true;
	}
	
	@Override
	public boolean onBack() {
		return false;
	}
	
	@Override
	public void update(long timeDelta, ObjectBase parent) {
		super.update(timeDelta, parent);
		
		if (currentLogo == 0) {
			return;
		}
		
		logoDisplayElapsedTime += timeDelta;
		if (logoDisplayDuration <= logoDisplayElapsedTime) {
			if (Config.DEBUG_LOG) Log.w(TAG, "logoDisplayDuration:" + logoDisplayDuration + 
					",logoDisplayElapsedTime:" + logoDisplayElapsedTime + ",currentLogo:" + currentLogo);
			
			if (currentLogo == LOGO_FUNNYLAB) {
				TextureSystem.getInstance().suspendLoading(true);
				doFadeOut(GameEventSystem.getInstance().obtain(GameEvent.SPLASH_FADE_OUT_DONE, LOGO_FUNNYLAB));
				currentLogo = 0;
			} else if (currentLogo == LOGO_STUDIO_IRREGULAR) {
				TextureSystem.getInstance().suspendLoading(true);
				doFadeOut(GameEventSystem.getInstance().obtain(GameEvent.SPLASH_FADE_OUT_DONE, LOGO_STUDIO_IRREGULAR));
				currentLogo = 0;
			}
		}
	}
	
	@Override
	protected boolean showTextureLibraryLoadingProgress(TextureLibrary library) {
		return false;	// splash do loading progress by itself for special logic.
	}
	
	@Override
	protected boolean isOurTextureLibrary(TextureLibrary library) {
		return false;
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		switch (event.what) {
		case GameEvent.TEXTURE_LIBRARY_LOAD_BEGIN:
		case GameEvent.TEXTURE_LIBRARY_LOAD_END:
		case GameEvent.TEXTURE_LOADING:
			return true;
		}
		return super.wantThisEvent(event);
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		if (Config.DEBUG_LOG) Log.d(TAG, "handleGameEvent event:" + event);
		
		super.handleGameEvent(event);
		
		if (GameEvent.TEXTURE_LIBRARY_LOAD_BEGIN == event.what) {
			remainingTextureCount = event.arg1;
		} else if (GameEvent.TEXTURE_LIBRARY_LOAD_END == event.what) {
			final TextureLibrary library = (TextureLibrary)event.obj;
			if (library == textureLibrary) {
				showLogo(LOGO_FUNNYLAB);
				loadMainTextures();
			} else if (library == mainTextureLibrary) {
				mainTextureLoadingDone = true;
				if (currentLogo == 0) {
					doFadeOut(event);
				}
			}
		} else if (GameEvent.TEXTURE_LOADING == event.what) {
			remainingTextureCount = event.arg1;
			if (showLoadingProgress) {
				updateTextureLoadingProgress(totalTextureCount, remainingTextureCount);
			}
		}
	}
	
	@Override
	protected boolean handlePendingEvent(GameEvent event) {
		if (Config.DEBUG_LOG) Log.d(TAG, "handlePendingEvent event:" + event);
		
		if (GameEvent.SPLASH_FADE_OUT_DONE == event.what) {
			TextureSystem.getInstance().suspendLoading(false);
			if (LOGO_FUNNYLAB == event.arg1) {
				Game.getInstance().restoreEffectFadeOut();
				showLogo(LOGO_STUDIO_IRREGULAR);
			} else if (LOGO_STUDIO_IRREGULAR == event.arg1) {
				stopShowingLogo();
				
				if (mainTextureLoadingDone) {
					Game.getInstance().getAd().setupAd();
					game.gotoMainMenu();
				} else {
					commitUpdate();
					Game.getInstance().restoreEffectFadeOut();
					
					showLoadingProgress = true;
					totalTextureCount = remainingTextureCount;
					updateTextureLoadingProgress(totalTextureCount, remainingTextureCount);
				}
			}
			return true;
		} else if (GameEvent.TEXTURE_LIBRARY_LOAD_END == event.what) {
			final TextureLibrary library = (TextureLibrary)event.obj;
			if (mainTextureLibrary == library) {
				Game.getInstance().getAd().setupAd();
				TextureSystem.getInstance().release(textureLibrary);
				game.gotoMainMenu();
			}
			return true;
		}
		return false;
	}
	
	private void loadMainTextures() {
		mainTextureLibrary = TextureLibrary.build(game.getActivity(), R.xml.textures);
		TextureSystem.getInstance().load(mainTextureLibrary);
	}
	
	// logo: LOGO_FUNNYLAB or LOGO_STUDIO_IRREGULAR
	private void showLogo(int logo) {
		if (Config.DEBUG_LOG) Log.d(TAG, "showLogo logo:" + logo);
		
		if (splash != null) {
			remove(splash);
		}
		
		if (logo != LOGO_FUNNYLAB && logo != LOGO_STUDIO_IRREGULAR) {
			return;
		}
		
		final String texture = logo == LOGO_FUNNYLAB ? "funnylab_logo_b1" : "studioirregular_logo";
		
		splash = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		splash.setup(720, 480);
		splash.setup(texture);
		add(splash);
		
		logoDisplayElapsedTime = 0;
		logoDisplayDuration = logo == LOGO_FUNNYLAB ? Config.DURATION_FOR_FUNNYLAB_LOGO : Config.DURATION_FOR_STUDIO_IRREGULAR_LOGO;
		
		currentLogo = logo;
	}
	
	private void stopShowingLogo() {
		if (splash != null) {
			remove(splash);
			splash = null;
		}
		
		currentLogo = 0;
	}
	
	private TextureLibrary mainTextureLibrary;
	
	private RenderComponent splash;
	
	private static final int LOGO_FUNNYLAB = 1;
	private static final int LOGO_STUDIO_IRREGULAR = 2;
	private int currentLogo = 0;
	private int logoDisplayElapsedTime;
	private int logoDisplayDuration;
	
	private boolean mainTextureLoadingDone;
	private boolean showLoadingProgress;
	private int remainingTextureCount;
	private int totalTextureCount;	// not know until logo display done.
}
