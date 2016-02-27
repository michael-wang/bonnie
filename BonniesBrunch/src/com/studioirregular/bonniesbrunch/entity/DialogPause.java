package com.studioirregular.bonniesbrunch.entity;

import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.SoundSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.InputSystem.TouchEvent;
import com.studioirregular.bonniesbrunch.component.RenderComponent;

public class DialogPause extends GameEntity {

	public DialogPause(int zOrder) {
		super(zOrder);
	}
	
	@Override
	public void setup(float x, float y, float width, float height) {
		super.setup(x, y, width, height);
		
		setupBackground();
		setupMainButtons();
		setupOptionButtons();
	}
	
	@Override
	public boolean dispatch(TouchEvent event, GameEntity parent) {
		super.dispatch(event, parent);
		return true;	// eat up all events.
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		switch (event.what) {
		case GameEvent.GAME_RESUME:
		case GameEvent.GAME_RESTART:
		case GameEvent.GAME_BACK_TO_MENU:
			GameEventSystem.scheduleEvent(event.what, event.arg1);
		}
	}
	
	private void setupBackground() {
		RenderComponent bg = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		bg.setup(720, 480);
		bg.setup("pause_menu_bg");
		add(bg);
	}
	
	private void setupMainButtons() {
		ButtonEntity.Builder builder = new ButtonEntity.Builder(200, 80, 317, 75, "pause_menu_bt1_resume", "pause_menu_bt1_resume_pressed");
		builder.playSoundWhenClicked("mainmenu_bt_s1");
		builder.emitEventWhenPressed(GameEvent.GAME_RESUME);
		builder.offsetTouchArea(-16, 0, 16, 0);
		ButtonEntity button = new ButtonEntity(MIN_GAME_ENTITY_Z_ORDER, builder);
		add(button);
		
		builder = new ButtonEntity.Builder(200, 158, 317, 75, "pause_menu_bt2_restart_normal", "pause_menu_bt2_restart_pressed");
		builder.playSoundWhenClicked("mainmenu_bt_s1");
		builder.emitEventWhenPressed(GameEvent.GAME_RESTART, 1);
		builder.offsetTouchArea(-16, 0, 16, 0);
		button = new ButtonEntity(MIN_GAME_ENTITY_Z_ORDER, builder);
		add(button);

		builder = new ButtonEntity.Builder(200, 238, 317, 75, "pause_menu_bt3_exit", "pause_menu_bt3_exit_pressed");
		builder.playSoundWhenClicked("mainmenu_bt_s1");
		builder.emitEventWhenPressed(GameEvent.GAME_BACK_TO_MENU, 1);
		builder.offsetTouchArea(-16, 0, 16, 0);
		button = new ButtonEntity(MIN_GAME_ENTITY_Z_ORDER, builder);
		add(button);
	}
	
	private void setupOptionButtons() {
		ToggleButton toggleButton = new ToggleButton(MIN_GAME_ENTITY_Z_ORDER, SoundSystem.getInstance().isSoundDisabled());
		toggleButton.setup(261, 332, 71, 72);
		toggleButton.setTouchArea(-12, -12, 8, 8);
		toggleButton.setupTextures("main_menu_bt_option1_enable",
				"main_menu_bt_option1_disable", 71, 72);
		toggleButton.setupEvents(GameEvent.MAIN_MENU_SOUND, 0,
				GameEvent.MAIN_MENU_SOUND, 1);
		add(toggleButton);

		toggleButton = new ToggleButton(MIN_GAME_ENTITY_Z_ORDER, SoundSystem.getInstance().isMusicDisabled());
		toggleButton.setup(388, 332, 71, 72);
		toggleButton.setTouchArea(-12, -12, 8, 8);
		toggleButton.setupTextures("main_menu_bt_option2_enable",
				"main_menu_bt_option2_disable", 71, 72);
		toggleButton.setupEvents(GameEvent.MAIN_MENU_MUSIC, 0,
				GameEvent.MAIN_MENU_MUSIC, 1);
		add(toggleButton);
	}
	
}
