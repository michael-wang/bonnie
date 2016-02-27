package com.studioirregular.bonniep2;

import java.util.Random;

public class CustomerPhysicistComponent extends CustomerComponent {

	protected CustomerPhysicistComponent(String id, EventHost host) {
		super(id, PHYSICIST, host);
		random = new Random(System.currentTimeMillis());
	}
	
	@Override
	public void startWaiting() {
		super.startWaiting();
		focusDuration = random.nextInt(MAX_FOCUS_DURATION - MIN_FOCUS_DURATION) + MIN_FOCUS_DURATION;
		focusElapsed = 0;
	}
	
	public boolean isThinkingPhysics() {
		return thinkingPhysics;
	}
	
	@Override
	public void update(long timeDiff) {
		super.update(timeDiff);
		
		focusElapsed += timeDiff;
		if (focusElapsed >= focusDuration) {
			switchFocus();
		}
	}
	
	private void switchFocus() {
		thinkingPhysics = !thinkingPhysics;
		focusDuration = random.nextInt(MAX_FOCUS_DURATION - MIN_FOCUS_DURATION) + MIN_FOCUS_DURATION;
		focusElapsed = 0;
		
		host.send(this, new ComponentEvent(ComponentEvent.CUSTOMER_PHYSICIST_CHANGE_THINKING, getId()));
	}
	
	private static final int MIN_FOCUS_DURATION = 2000;
	private static final int MAX_FOCUS_DURATION = 4000;
	
	private long focusDuration;
	private long focusElapsed;
	private Random random;
	private boolean thinkingPhysics = false;

}
