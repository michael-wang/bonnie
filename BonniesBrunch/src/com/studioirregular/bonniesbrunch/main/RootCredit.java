package com.studioirregular.bonniesbrunch.main;

import com.studioirregular.bonniesbrunch.Game;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.R;
import com.studioirregular.bonniesbrunch.TextureLibrary;
import com.studioirregular.bonniesbrunch.TextureSystem;
import com.studioirregular.bonniesbrunch.component.RenderComponent;
import com.studioirregular.bonniesbrunch.entity.ButtonEntity;

public class RootCredit extends GameEntityRoot {

	public RootCredit(Game game) {
		super(game);
	}
	
	@Override
	public boolean load() {
		textureLibrary = TextureLibrary.build(game.getActivity(), R.xml.textures_credit);
		TextureSystem.getInstance().load(textureLibrary);
		
		RenderComponent bg = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		bg.setup(720, 480);
		bg.setup("credits_bg");
		add(bg);
		
		ButtonEntity.Builder builder = new ButtonEntity.Builder(0, 440, 108, 39, "help_left", "help_left");
		builder.playSoundWhenClicked("mainmenu_bt_s1");
		builder.emitEventWhenPressed(GameEvent.MENU_BACK);
		builder.offsetTouchArea(-8, -24, 24, 8);
		ButtonEntity back = new ButtonEntity(MIN_GAME_ENTITY_Z_ORDER, builder);
		add(back);
		
		return true;
	}
	
	@Override
	protected void onReadyToGo() {
		super.onReadyToGo();
		
		game.getAd().hideBanner();
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		super.handleGameEvent(event);
		if (GameEvent.MENU_BACK == event.what) {
			onBack();
		}
	}
	
	@Override
	protected boolean handlePendingEvent(GameEvent event) {
		onFinish();
		return true;
	}
	
	private void onFinish() {
		TextureSystem.getInstance().release(textureLibrary);
		textureLibrary = null;
		
		game.gotoMainMenu();
		game.getAd().showBanner();
	}

}
