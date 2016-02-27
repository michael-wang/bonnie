package com.studioirregular.bonniesbrunch.entity;

import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.base.ObjectBase;
import com.studioirregular.bonniesbrunch.component.Animation;
import com.studioirregular.bonniesbrunch.component.DecimalDigit.TextureConfig;
import com.studioirregular.bonniesbrunch.component.RenderComponent;
import com.studioirregular.bonniesbrunch.component.TranslateAnimation;

public class MoneyEffect extends GameEntity {

	public MoneyEffect(int zOrder, int digitWidth, int digitHeight, int digitCount) {
		super(zOrder);
		DIGIT_WIDTH = digitWidth;
		DIGIT_HEIGHT = digitHeight;
		DIGIT_COUNT = digitCount;
	}
	
	public void addRenderComponent(int width, int height, String texture) {
		RenderComponent render = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		render.setup(width, height);
		render.setup(texture);
		add(render);
		
		numberOffsetY += height;
	}
	
	public void addNumber(String plusTexture, String numberTextureId, String numberTextureBase) {
		RenderComponent plus = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		plus.setup(DIGIT_WIDTH, DIGIT_HEIGHT);
		plus.setup(plusTexture);
		plus.setupOffset(0, numberOffsetY);
		add(plus);
		
		numberOffsetX += DIGIT_WIDTH;
		
		number = new DecimalNumber(MIN_GAME_ENTITY_Z_ORDER);
		TextureConfig textureConfig = new TextureConfig(numberTextureId);
		final String partitionNameBase = numberTextureBase;
		for (int i = 0; i < 10; i++) {
			textureConfig.addPartition(i, partitionNameBase + Integer.toString(i));
		}
		number.setup(0, 0, 
				DIGIT_WIDTH * DIGIT_COUNT, DIGIT_HEIGHT, 
				DIGIT_COUNT, DIGIT_WIDTH, DIGIT_HEIGHT, textureConfig);
		number.setNewValue(0);
		add(number);
		
		animation = new TranslateAnimation(MIN_GAME_COMPONENT_Z_ORDER, 0, -DIGIT_HEIGHT, 0);
		animation.setFillBefore(false);
		add(animation);
	}
	
	public void doEffect(int moneyValue, float x, float y, int duration, boolean doTranslate) {
		move(x, y);
		number.move(x + numberOffsetX, y + numberOffsetY);
		number.setNewValue(moneyValue);
		
		if (doTranslate) {
			animation.setDuration(duration);
			animation.start();
		}
		
		this.duration = duration;
		elapsedTime = 0;
		doingEffect = true;
		show();
	}
	
	@Override
	public void update(long timeDelta, ObjectBase parent) {
		super.update(timeDelta, parent);
		
		if (doingEffect) {
			elapsedTime += timeDelta;
			
			if (duration <= elapsedTime) {
				if (animation.isStarted()) {
					animation.stop();
				}
				hide();
				doingEffect = false;
			}
		}
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
	}
	
	private final int DIGIT_WIDTH;
	private final int DIGIT_HEIGHT;
	private final int DIGIT_COUNT;
	
	private float numberOffsetX, numberOffsetY;
	
	private DecimalNumber number;
	private Animation animation;
	private int duration;
	private int elapsedTime;
	private boolean doingEffect;
}
