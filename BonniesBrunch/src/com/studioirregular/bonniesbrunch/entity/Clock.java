package com.studioirregular.bonniesbrunch.entity;

import java.text.DecimalFormat;
import java.util.List;

import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.LevelSystem.LevelTime;
import com.studioirregular.bonniesbrunch.LevelSystem.RushHour;
import com.studioirregular.bonniesbrunch.component.FrameAnimationComponent;
import com.studioirregular.bonniesbrunch.component.RenderComponent;

public class Clock extends GameEntity {

	public Clock(int zOrder) {
		super(zOrder);
	}
	
	public void setup(float x, float y, float width, float height, LevelTime levelTime) {
		super.setup(x, y, width, height);
		
		animation = new FrameAnimationComponent(MIN_GAME_COMPONENT_Z_ORDER);
		animation.setFillBefore(true);
		animation.setFillAfter(true);
		
		// total 13 frames.
		// duration divid by 12 for I want to show last frame when frameDuration time up.
		int frameDuration = levelTime.total * 1000 / 12;
		
		animation.addFrame("game_time_clock_start", width, height, frameDuration);
		animation.addFrame("game_time_clock_01", width, height, frameDuration);
		animation.addFrame("game_time_clock_02", width, height, frameDuration);
		animation.addFrame("game_time_clock_03", width, height, frameDuration);
		animation.addFrame("game_time_clock_04", width, height, frameDuration);
		animation.addFrame("game_time_clock_05", width, height, frameDuration);
		animation.addFrame("game_time_clock_06", width, height, frameDuration);
		animation.addFrame("game_time_clock_07", width, height, frameDuration);
		animation.addFrame("game_time_clock_08", width, height, frameDuration);
		animation.addFrame("game_time_clock_09", width, height, frameDuration);
		animation.addFrame("game_time_clock_10", width, height, frameDuration);
		animation.addFrame("game_time_clock_11", width, height, frameDuration);
		animation.addFrame("game_time_clock_12", width, height, frameDuration);
		add(animation);
		
		List<RushHour> rushHours = levelTime.rushHours;
		final String TEXTURE_NAME_BASE = "game_time_rush_";
		DecimalFormat format = rushHours.isEmpty() ? null : new DecimalFormat("00");
		for (RushHour rushHour : rushHours) {
			final int index = rushHour.rushClockIndex;
			if (0 < index && index <= 12) {
				String textureName = TEXTURE_NAME_BASE + format.format(index);
				
				RenderComponent rush = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
				rush.setup(66, 66);
				rush.setup(textureName);
				add(rush);
			}
		}
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		if (GameEvent.GAME_CLOCK_START == event.what) {
			return true;
		}
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		if (GameEvent.GAME_CLOCK_START == event.what) {
			animation.start();
		}
	}
	
	private FrameAnimationComponent animation;

}
