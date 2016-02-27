package com.studioirregular.bonniesbrunch.main;

import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.Game;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.R;
import com.studioirregular.bonniesbrunch.TextureLibrary;
import com.studioirregular.bonniesbrunch.TextureSystem;
import com.studioirregular.bonniesbrunch.component.ButtonComponent;
import com.studioirregular.bonniesbrunch.component.RenderComponent;

public class RootComics extends GameEntityRoot {

	private static final String TAG = "root-comit";
	
	public static enum Comic {
		STORY_START,
		STORY_END,
		STAGE_1_START,
		STAGE_2_START,
		STAGE_3_START,
		STAGE_4_START,
		STAGE_5_START
	}
	
	public RootComics(Game game, Comic comic) {
		super(game);
		this.comic = comic;
		pageCount = 0;
	}
	
	@Override
	public boolean load() {
		if (Config.DEBUG_LOG) Log.w(TAG, "load comic:" + comic);
		loadTexture();
		setPageCount();
		
		pageIndex = 0;
		loadPage();
		
		ButtonComponent button = new ButtonComponent(MIN_GAME_COMPONENT_Z_ORDER);
		button.setup(0, 0, 720, 480);
		add(button);
		
		return true;
	}
	
	@Override
	protected void onReadyToGo() {
		super.onReadyToGo();
		
		if (Config.SHOW_AD_IN_COMIC) {
			game.getAd().showBanner();
		} else {
			game.getAd().hideBanner();
		}
	}
	
	@Override
	public boolean onBack() {
		return true;	// eat back key here.
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		super.handleGameEvent(event);
		
		if (GameEvent.BUTTON_UP == event.what) {
			onNextPage();
		}
	}
	
	@Override
	protected boolean handlePendingEvent(GameEvent event) {
		if (Config.DEBUG_LOG) Log.w(TAG, "handlePendingEvent event:" + event);
		
		if (GameEvent.COMIC_FINISHED == event.what) {
			onFinish();
			return true;
		}
		return false;
	}
	
	private void loadTexture() {
		int textureRes = 0;
		
		if (Comic.STORY_START == comic) {
			textureRes = R.xml.textures_story_start;
		} else if (Comic.STORY_END == comic) {
			textureRes = R.xml.textures_story_end;
		} else if (Comic.STAGE_1_START == comic) {
			textureRes = R.xml.textures_stage_1;
		} else if (Comic.STAGE_2_START == comic) {
			textureRes = R.xml.textures_stage_2;
		} else if (Comic.STAGE_3_START == comic) {
			textureRes = R.xml.textures_stage_3;
		} else if (Comic.STAGE_4_START == comic) {
			textureRes = R.xml.textures_stage_4;
		} else if (Comic.STAGE_5_START == comic) {
			textureRes = R.xml.textures_stage_5;
		}
		
		textureLibrary = TextureLibrary.build(game.getActivity(), textureRes);
		TextureSystem.getInstance().load(textureLibrary);
	}
	
	private void setPageCount() {
		if (Comic.STORY_START == comic || Comic.STORY_END == comic) {
			pageCount = 2;
		} else {
			pageCount = 1;
		}
	}
	
	private void onNextPage() {
		if (Config.DEBUG_LOG) Log.w(TAG, "onNextPage pageCount:" + pageCount + ",pageIndex:" + pageIndex);
		
		pageIndex++;
		if (pageIndex >= pageCount) {
			doFadeOut(GameEventSystem.getInstance().obtain(GameEvent.COMIC_FINISHED));
		} else {
			loadPage();
		}
	}
	
	private void loadPage() {
		if (Config.DEBUG_LOG) Log.w(TAG, "loadPage comic:" + comic + ",pageIndex:" + pageIndex);
		
		if (page != null) {
			remove(page);
		}
		
		String texturePartition = null;
		if (Comic.STORY_START == comic) {
			if (pageIndex == 0) {
				texturePartition = "story_start_p1";
			} else {
				texturePartition = "story_start_p2";
			}
		} else if (Comic.STORY_END == comic) {
			if (pageIndex == 0) {
				texturePartition = "story_end_p1";
			} else {
				texturePartition = "story_end_p2";
			}
		} else if (Comic.STAGE_1_START == comic) {
			texturePartition = "opening_bg_ep1";
		} else if (Comic.STAGE_2_START == comic) {
			texturePartition = "opening_bg_ep2";
		} else if (Comic.STAGE_3_START == comic) {
			texturePartition = "opening_bg_ep3";
		} else if (Comic.STAGE_4_START == comic) {
			texturePartition = "opening_bg_ep4";
		} else if (Comic.STAGE_5_START == comic) {
			texturePartition = "opening_bg_ep5";
		}
		
		page = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		page.setup(720, 480);
		page.setup(texturePartition);
		add(page);
	}
	
	private void onFinish() {
		if (textureLibrary != null) {
			TextureSystem.getInstance().release(textureLibrary);
			textureLibrary = null;
		}
		game.comicEnds(comic);
	}
	
	private Comic comic;
	private int pageCount;
	
	private int pageIndex;	// 0 based
	private RenderComponent page;
}
