package com.studioirregular.bonniesbrunch.base;


public abstract class TimeFramedPhase extends PhasedObject {

	public TimeFramedPhase(int phase, PhaseObjectListener listener, int duration) {
		super(phase, listener);
		this.duration = duration;
	}
	
	@Override
	public void update(long timeDelta, ObjectBase parent) {
		super.update(timeDelta, parent);
		
		if (finished) {
			return;
		}
		
		elapsedTime += timeDelta;
		if (duration <= elapsedTime) {
			if (listener != null) {
				listener.onNextPhase(this);
				finished = true;
			}
		}
	}

	private final int duration;
	private int elapsedTime;

}
