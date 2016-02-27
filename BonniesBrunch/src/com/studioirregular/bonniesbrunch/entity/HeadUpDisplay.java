package com.studioirregular.bonniesbrunch.entity;

import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.InputSystem.TouchEvent;
import com.studioirregular.bonniesbrunch.LevelSystem.GameLevel;


public class HeadUpDisplay extends GameEntity {

	public HeadUpDisplay(int zOrder) {
		super(zOrder);
	}
	
	public void setup(float x, float y, float width, float height, GameLevel level) {
		super.setup(x, y, width, height);
		
		Clock clock = new Clock(MIN_GAME_ENTITY_Z_ORDER);
		clock.setup(0, 0, 66, 66, level.time);
		add(clock);
		
		if (level.specialLevel) {
			DigitalClock digital = new DigitalClock(MIN_GAME_ENTITY_Z_ORDER);
			digital.setup(12, 71, 182, 117);
			digital.setTotalTime(level.time.total * 1000);
			add(digital);
		}
		
		scoreBar = new ScoreBar(MIN_GAME_ENTITY_Z_ORDER + 2);
		scoreBar.setup(75, 0, 288, 38, level);
		add(scoreBar);
		
		score = new GameScore(MIN_GAME_ENTITY_Z_ORDER);
		score.setup(390, 7, 120, 26, 5);
		add(score);
		
		levelUpBadge = new LevelUpBadge(MIN_GAME_ENTITY_Z_ORDER + 1);
		levelUpBadge.setup(159, 32, 185, 33);
		levelUpBadge.hide();
		add(levelUpBadge);
	}
	
	@Override
	public boolean dispatch(TouchEvent event, GameEntity parent) {
		// HUD won't handle any touch event.
		return false;
	}
	
	@Override
	protected void getRegionColor(float[] color) {
		color[0] = 0.5f;
		color[1] = 0.5f;
		color[2] = 1.0f;
		color[3] = 0.5f;
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		switch (event.what) {
		case GameEvent.GAME_SCORE_ADD:
		case GameEvent.GAME_SCORE_CLEAR:
		case GameEvent.GAME_SCORE_LEVEL_UP:
			return true;
		}
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		if (GameEvent.GAME_SCORE_LEVEL_UP == event.what) {
			levelUpBadge.send(event);
		} else {
			scoreBar.send(event);
			score.send(event);
		}
	}
	
	private ScoreBar scoreBar;
	private GameScore score;
	private LevelUpBadge levelUpBadge;

}
