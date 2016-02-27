package com.studioirregular.bonniesbrunch.main;

import com.studioirregular.bonniesbrunch.Game;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.R;
import com.studioirregular.bonniesbrunch.TextureLibrary;
import com.studioirregular.bonniesbrunch.TextureSystem;
import com.studioirregular.bonniesbrunch.component.RenderComponent;
import com.studioirregular.bonniesbrunch.entity.ButtonEntity;

public class RootHelp extends GameEntityRoot {

	public RootHelp(Game game) {
		super(game);
	}
	
	@Override
	public boolean load() {
		textureLibrary = TextureLibrary.build(game.getActivity(), R.xml.textures_help);
		TextureSystem.getInstance().load(textureLibrary);
		
		setupButtons();
		
		cardIndex = 0;
		setupCard(cardIndex);
		
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
		} else if (GameEvent.HELP_PREV_CARD == event.what) {
			cardIndex--;
			setupCard(cardIndex);
		} else if (GameEvent.HELP_NEXT_CARD == event.what) {
			cardIndex++;
			setupCard(cardIndex);
		}
	}
	
	@Override
	protected boolean handlePendingEvent(GameEvent event) {
		if (GameEvent.ROOT_BACK == event.what) {
			onFinish();
			return true;
		}
		return false;
	}
	
	private void setupButtons() {
		ButtonEntity.Builder builder = new ButtonEntity.Builder(0, 440, 108, 39, "help_left", "help_left");
		builder.playSoundWhenClicked("mainmenu_bt_s1");
		builder.emitEventWhenPressed(GameEvent.MENU_BACK);
		builder.offsetTouchArea(-8, -24, 24, 8);
		ButtonEntity back = new ButtonEntity(MIN_GAME_ENTITY_Z_ORDER, builder);
		add(back);
		
		builder = new ButtonEntity.Builder(0, 213, 50, 64, "help_tap_back", "help_tap_back");
		builder.playSoundWhenClicked("mainmenu_bt_s1");
		builder.emitEventWhenPressed(GameEvent.HELP_PREV_CARD);
		builder.offsetTouchArea(-8, -32, 32, 32);
		prev = new ButtonEntity(MIN_GAME_ENTITY_Z_ORDER, builder);
		add(prev);
		
		builder = new ButtonEntity.Builder(670, 213, 50, 64, "help_tap_next", "help_tap_next");
		builder.playSoundWhenClicked("mainmenu_bt_s1");
		builder.emitEventWhenPressed(GameEvent.HELP_NEXT_CARD);
		builder.offsetTouchArea(-32, -32, 8, 32);
		next = new ButtonEntity(MIN_GAME_ENTITY_Z_ORDER, builder);
		add(next);
	}
	
	private void setupCard(int index) {
		if (card != null) {
			remove(card);
		}
		
		card = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		card.setup(720, 480);
		card.setup(PAGE_TEXTURE_BASE + Integer.toString(index + 1));
		add(card);
		
		if (index <= 0) {
			prev.hide();
		} else {
			prev.show();
		}
		
		if (7 <= index) {
			next.hide();
		} else {
			next.show();
		}
	}
	
	private void onFinish() {
		if (textureLibrary != null) {
			TextureSystem.getInstance().release(textureLibrary);
			textureLibrary = null;
		}
		
		game.gotoMainMenu();
		game.getAd().showBanner();
	}
	
	private static final String PAGE_TEXTURE_BASE = "help_card_";
	
	private int cardIndex;	// 0 ~ 7
	private RenderComponent card;
	private ButtonEntity prev, next;

}
