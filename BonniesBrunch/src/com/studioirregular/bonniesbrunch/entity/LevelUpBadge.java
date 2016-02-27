package com.studioirregular.bonniesbrunch.entity;

import android.util.Log;

import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.base.ObjectBase;
import com.studioirregular.bonniesbrunch.component.RenderComponent;

public class LevelUpBadge extends GameEntity {

	private static final String TAG = "level-up-badge";
	
	private static final int DURATION_ROLL_DOWN	= 200;
	private static final int DURATION_HOLD		= 1500;
	private static final int DURATION_ROLL_UP	= 200;
	
	public LevelUpBadge(int zOrder) {
		super(zOrder);
	}
	
	@Override
	public void update(long timeDelta, ObjectBase parent) {
		super.update(timeDelta, parent);
		
		if (animating) {
			elapsedTime += timeDelta;
			
			if (animationPhase == ANIMATION_ROLL_DOWN) {
				float height = 0;
				if (DURATION_ROLL_DOWN <= elapsedTime) {
					height = BADGE_HEIGHT;
					animationPhase = ANIMATION_HOLD;
					elapsedTime = 0;
				} else {
					height = BADGE_HEIGHT * elapsedTime / DURATION_ROLL_DOWN;
				}
				badge.changeHeight(height);
				badge.offsetTextureY(true, BADGE_HEIGHT - height);
			} else if (animationPhase == ANIMATION_HOLD) {
				if (DURATION_HOLD <= elapsedTime) {
					animationPhase = ANIMATION_ROLL_UP;
					elapsedTime = 0;
				}
			} else if (animationPhase == ANIMATION_ROLL_UP) {
				float height = 0;
				if (DURATION_ROLL_UP <= elapsedTime) {
					animating = false;
					remove(badge);
					hide();
				} else {
					height = BADGE_HEIGHT * (DURATION_ROLL_UP - elapsedTime) / DURATION_ROLL_UP;
				}
				badge.changeHeight(height);
				badge.offsetTextureY(true, BADGE_HEIGHT - height);
			}
		}
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		if (GameEvent.GAME_SCORE_LEVEL_UP == event.what) {
			setupBadge(event.arg1);
		}
	}
	
	private void setupBadge(int level) {
		if (level < 1 || 3 < level) {
			Log.e(TAG, "setupBadge illegal level:" + level);
			return;
		}
		
		if(badge != null) {
			remove(badge);
		}
		
		badge = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		badge.setup(185, BADGE_HEIGHT);
		badge.setup(getTexture(level));
		add(badge);
		
		elapsedTime = 0;
		animating = true;
		animationPhase = ANIMATION_ROLL_DOWN;
		
		show();
	}
	
	private String getTexture(int level) {
		if (level == 1) {
			return "game_money_lv_good";
		} else if (level == 2) {
			return "game_money_lv_expert";
		} else if (level == 3) {
			return "game_money_lv_master";
		}
		return "";
	}
	
	private RenderComponent badge;
	
	private boolean animating = false;
	private int elapsedTime;
	
	private static final float BADGE_HEIGHT = 33;
	
	private static final int ANIMATION_ROLL_DOWN	= 1;
	private static final int ANIMATION_HOLD			= 2;
	private static final int ANIMATION_ROLL_UP		= 3;
	
	private int animationPhase;

}
