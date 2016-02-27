package com.studioirregular.bonniesbrunch.entity;

import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.base.ObjectBase;
import com.studioirregular.bonniesbrunch.component.DecimalDigit;
import com.studioirregular.bonniesbrunch.component.DecimalDigit.TextureConfig;

public class DecimalNumber extends GameEntity {

	public DecimalNumber(int zOrder) {
		super(zOrder);
	}
	
	public void setup(float x, float y, float width, float height,
			int maxDigits, float digitWidth, float digitHeight,
			TextureConfig textureConfig) {
		super.setup(x, y, width, height);
		
		assert maxDigits > 0;
		this.maxDigits = maxDigits;
		
		digits = new DecimalDigit[maxDigits];
		for (int i = 0; i < maxDigits; i++) {
			DecimalDigit digit = new DecimalDigit(MIN_GAME_COMPONENT_Z_ORDER);
			digit.setup(digitWidth, digitHeight, i * digitWidth, 0, textureConfig);
			digit.setValue(0);
			digit.setVisible(false);
			add(digit);
			
			digits[i] = digit;
		}
	}
	
	public void showLeadingZero(boolean show) {
		showLeadingZero = show;
	}
	
	public void setNewValue(int newValue) {
		value = newValue;
		updateDigits();
	}
	
	public void animateToNewValue(int newValue, int duration) {
		if (animating) {
			animationValueStart += animationValueOffset;
			animationValueOffset = newValue - animationValueStart;
		} else {
			animationValueStart = value;
			animationValueOffset = newValue - value;
		}
		animationDuration = duration;
		animationElapsedTime = 0;
		animating = true;
	}
	
	@Override
	public void update(long timeDelta, ObjectBase parent) {
		super.update(timeDelta, parent);
		
		if (animating) {
			animationElapsedTime += timeDelta;
			if (animationDuration <= animationElapsedTime) {
				animating = false;
				value = animationValueStart + animationValueOffset;
			} else {
				value = animationValueStart + animationValueOffset * animationElapsedTime / animationDuration;
			}
			updateDigits();
		}
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
	}
	
	private void updateDigits() {
		String valueString = Integer.toString(value);
		final int MAX_DIGITS = maxDigits;
		
		if (MAX_DIGITS < valueString.length()) {
			valueString = valueString.substring(maxDigits - valueString.length());
		}
		
		if (showLeadingZero) {
			final int diff = MAX_DIGITS - valueString.length();
			for (int i = 0; i < MAX_DIGITS; i++) {
				if (i < diff) {
					digits[i].setValue(0);
					digits[i].setVisible(true);
				} else {
					char digitChar = valueString.charAt(i - diff);
					int digit = (int)(digitChar - ZERO);
					digits[i].setValue(digit);
					digits[i].setVisible(true);
				}
			}
		} else {
			for (int i = 0; i < MAX_DIGITS; i++) {
				if (i < valueString.length()) {
					char digitChar = valueString.charAt(i);
					int digit = (int)(digitChar - ZERO);
					digits[i].setValue(digit);
					digits[i].setVisible(true);
				} else {
					digits[i].setVisible(false);
				}
			}
		}
	}
	
	private static final char ZERO = '0';
	
	private int maxDigits;
	private int value;
	
	private boolean animating = false;
	private int animationValueStart = 0;
	private int animationValueOffset = 0;
	private int animationDuration = 0;
	private int animationElapsedTime = 0;
	
	private DecimalDigit[] digits;
	private boolean showLeadingZero = false;

}
