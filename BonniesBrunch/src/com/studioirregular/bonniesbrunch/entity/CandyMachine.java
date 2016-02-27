package com.studioirregular.bonniesbrunch.entity;

import android.graphics.PointF;
import android.graphics.RectF;

import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.SoundSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.SoundSystem.Sound;
import com.studioirregular.bonniesbrunch.base.ObjectBase;
import com.studioirregular.bonniesbrunch.component.Animation;
import com.studioirregular.bonniesbrunch.component.AnimationSet;
import com.studioirregular.bonniesbrunch.component.ButtonComponent;
import com.studioirregular.bonniesbrunch.component.FrameAnimationComponent;
import com.studioirregular.bonniesbrunch.component.RenderComponent;
import com.studioirregular.bonniesbrunch.component.ScaleAnimation;

public class CandyMachine extends GameEntity implements CandyHolder {

	public CandyMachine(int zOrder) {
		super(zOrder);
	}
	
	public void setup(float x, float y, float width, float height, int level) {
		super.setup(x, y, width, height);
		
		this.level = level;
		candies = new Candy[level];
		
		bgRender = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		bgRender.setup(width, height);
		
		cookingAnimation = new FrameAnimationComponent(MIN_GAME_COMPONENT_Z_ORDER);
		cookingAnimation.setLoop(true);
		
		if (level == 1) {
			bgRender.setup("game_table_candy_lv1_normal");
			cookingAnimation.addFrame("game_table_candy_lv1_work_1", width, height, COOK_ANIMATION_FRAME_DURATION);
			cookingAnimation.addFrame("game_table_candy_lv1_work_2", width, height, COOK_ANIMATION_FRAME_DURATION);
		} else if (level == 2) {
			bgRender.setup("game_table_candy_lv2_normal");
			cookingAnimation.addFrame("game_table_candy_lv2_work_1", width, height, COOK_ANIMATION_FRAME_DURATION);
			cookingAnimation.addFrame("game_table_candy_lv2_work_2", width, height, COOK_ANIMATION_FRAME_DURATION);
		} else if (level == 3) {
			bgRender.setup("game_table_candy_lv3_normal");
			cookingAnimation.addFrame("game_table_candy_lv3_work_1", width, height, COOK_ANIMATION_FRAME_DURATION);
			cookingAnimation.addFrame("game_table_candy_lv3_work_2", width, height, COOK_ANIMATION_FRAME_DURATION);
		}
		add(bgRender);
		
		ButtonComponent button = new ButtonComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
		button.setup(x, y, width, height);
		add(button);
		
		machineWorkingSound = SoundSystem.getInstance().load("game_table_candy_work_s1");
		machineDoneSound = SoundSystem.getInstance().load("game_table_machine_timeup_s1");
		
		clickAnimation = new AnimationSet(MIN_GAME_COMPONENT_Z_ORDER);
		Animation scaleUp = new ScaleAnimation(MIN_GAME_COMPONENT_Z_ORDER, 1.0f, 1.25f, 120);
		Animation scaleDown = new ScaleAnimation(MIN_GAME_COMPONENT_Z_ORDER, 1.25f, 1.0f, 120);
		((AnimationSet)clickAnimation).addAnimation(scaleUp);
		((AnimationSet)clickAnimation).addAnimation(scaleDown);
		clickAnimation.setLoop(false);
		clickAnimation.setFillBefore(true);
		clickAnimation.setFillAfter(true);
		add(clickAnimation);
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		if (GameEvent.DROP_ACCEPTED == event.what && (event.obj instanceof Candy)) {
			return true;
		}
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		if (GameEvent.BUTTON_UP == event.what) {
			requestCook();
			clickAnimation.start();
		} else if (GameEvent.DROP_ACCEPTED == event.what) {
			Candy candy = (Candy)event.obj;
			for (int i = 0; i < candies.length; i++) {
				if (candies[i] == candy) {
					remove(candy);
					candies[i] = null;
				}
			}
		}
	}
	
	@Override
	public void update(long timeDelta, ObjectBase parent) {
		super.update(timeDelta, parent);
		
		if (cooking) {
			elapsedCookTime += timeDelta;
			
			if (COOK_DURATION <= elapsedCookTime) {
				onCookDone();
			}
		}
	}
	
	@Override
	public void getCandyLocation(Candy candy, PointF loc) {
		switch (level) {
		case 1:
			if (candy.type == Candy.TYPE_I) {
				loc.set(37, 363);
			}
			break;
		case 2:
			if (candy.type == Candy.TYPE_I) {
				loc.set(25, 361);
			} else if (candy.type == Candy.TYPE_II) {
				loc.set(50, 361);
			}
			break;
		case 3:
			if (candy.type == Candy.TYPE_I) {
				loc.set(15, 362);
			} else if (candy.type == Candy.TYPE_II) {
				loc.set(36, 356);
			} else if (candy.type == Candy.TYPE_III) {
				loc.set(58, 362);
			}
			break;
		}
	}
	
	@Override
	public RectF getHolderBox() {
		return new RectF(box);
	}
	
	private boolean requestCook() {
		if (cooking) {
			return false;
		}
		for (int i = 0; i < level; i++) {
			if (candies[i] != null) {
				return false;
			}
		}
		
		turnOn();
		return true;
	}
	
	private void onCookDone() {
		for (int i = 0; i < level; i++) {
			candies[i] = new Candy(MIN_GAME_ENTITY_Z_ORDER + i, this, i + 1);
			add(candies[i]);
		}
		turnOff();
	}
	
	private void turnOn() {
		cooking = true;
		elapsedCookTime = 0;
		
		remove(bgRender);
		add(cookingAnimation);
		cookingAnimation.start();
		
		GameEventSystem.scheduleEvent(GameEvent.CANDY_MACHINE_START_COOKING);
		
		workingSoundStreamId = SoundSystem.getInstance().playSound(machineWorkingSound, true);
	}
	
	private void turnOff() {
		cooking = false;
		
		cookingAnimation.stop();
		remove(cookingAnimation);
		add(bgRender);
		
		GameEventSystem.scheduleEvent(GameEvent.CANDY_MACHINE_FINISH_COOKING);
		
		SoundSystem.getInstance().stopSound(workingSoundStreamId);
		SoundSystem.getInstance().playSound(machineDoneSound, false);
	}
	
	private static final int COOK_ANIMATION_FRAME_DURATION = 250;
	private static final int COOK_DURATION = 5000;
	
	private int level;
	private Candy[] candies;	// length == level
	
	private boolean cooking = false;
	private long elapsedCookTime = 0L;
	
	private RenderComponent bgRender;
	private FrameAnimationComponent cookingAnimation;
	
	private Sound machineWorkingSound;
	private int workingSoundStreamId;
	private Sound machineDoneSound;
	
	private Animation clickAnimation;
}
