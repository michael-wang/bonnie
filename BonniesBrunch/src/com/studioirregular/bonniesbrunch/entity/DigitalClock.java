package com.studioirregular.bonniesbrunch.entity;

import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.base.ObjectBase;
import com.studioirregular.bonniesbrunch.component.DecimalDigit;
import com.studioirregular.bonniesbrunch.component.DecimalDigit.TextureConfig;
import com.studioirregular.bonniesbrunch.component.RenderComponent;

// To show time in "M:SS" format.
public class DigitalClock extends GameEntity {

	public DigitalClock(int zOrder) {
		super(zOrder);
	}
	
	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
		
		if (minDigit != null) {
			updateClock(totalTime, 0);
		}
	}
	
	@Override
	public void setup(float x, float y, float width, float height) {
		super.setup(x, y, width, height);
		
		RenderComponent bg = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		bg.setup(182, 117);
		bg.setup("sp_time_bg");
		add(bg);
		
		minDigit = new DecimalDigit(MIN_GAME_COMPONENT_Z_ORDER + 1);
		TextureConfig config = prepareNumberTexture();
		minDigit.setup(DIGIT_WIDTH, DIGIT_HEIGHT, 12, 57, config);
		add(minDigit);
		
		RenderComponent colon = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
		colon.setup(DIGIT_WIDTH, DIGIT_HEIGHT);
		colon.setup("sp_time_line");
		colon.setupOffset(51, 57);
		add(colon);
		
		secDigit1 = new DecimalDigit(MIN_GAME_COMPONENT_Z_ORDER + 1);
		secDigit1.setup(DIGIT_WIDTH, DIGIT_HEIGHT, 90, 57, config);
		add(secDigit1);
		
		secDigit2 = new DecimalDigit(MIN_GAME_COMPONENT_Z_ORDER + 1);
		secDigit2.setup(DIGIT_WIDTH, DIGIT_HEIGHT, 129, 57, config);
		add(secDigit2);
		
		updateClock(totalTime, 0);
	}
	
	@Override
	public void update(long timeDelta, ObjectBase parent) {
		super.update(timeDelta, parent);
		
		if (!running) {
			return;
		}
		
		elapsedTime += timeDelta;
		updateClock(totalTime, elapsedTime);
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		switch (event.what) {
		case GameEvent.GAME_CLOCK_START:
		case GameEvent.GAME_CLOCK_END:
			return true;
		}
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		if (GameEvent.GAME_CLOCK_START == event.what) {
			elapsedTime = 0;
			running = true;
		} else if (GameEvent.GAME_CLOCK_END == event.what) {
			running = false;
		}
	}
	
	private void updateClock(int total, int elapsed) {
		if (total < elapsed) {
			elapsed = total;
		}
		elapsed -= (elapsed % 1000); // round elapsed to 1000 ms.
		// in seconds
		final int remain = (total - elapsed) / 1000;
		
		int value = remain / 60;
		minDigit.setValue(value);
		
		value = (remain % 60) / 10;
		secDigit1.setValue(value);
		
		value = (remain % 60) % 10;
		secDigit2.setValue(value);
	}
	
	private TextureConfig prepareNumberTexture() {
		TextureConfig texConfig = new TextureConfig("head-up-display");
		final String partitionNameBase = "sp_time_";
		for (int i = 0; i < 10; i++) {
			texConfig.addPartition(i, partitionNameBase + Integer.toString(i));
		}
		return texConfig;
	}
	
	private static final int DIGIT_WIDTH = 38;
	private static final int DIGIT_HEIGHT = 45;
	
	private int totalTime;
	private int elapsedTime;
	private boolean running = false;
	
	private DecimalDigit minDigit;
	private DecimalDigit secDigit1;
	private DecimalDigit secDigit2;
}
