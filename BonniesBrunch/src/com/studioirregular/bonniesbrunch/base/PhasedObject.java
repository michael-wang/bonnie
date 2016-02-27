package com.studioirregular.bonniesbrunch.base;

import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;

public abstract class PhasedObject extends ObjectBase {

	public static interface PhaseObjectListener {
		public void onNextPhase(PhasedObject phaseObject);
	}
	
	public PhasedObject(int phase, PhaseObjectListener listener) {
		this.phase = phase;
		this.listener = listener;
		this.started = false;
		this.finished = false;
	}
	
	public int getPhase() {
		return phase;
	}
	
	@Override
	public void reset() {
	}

	@Override
	public void update(long timeDelta, ObjectBase parent) {
		super.update(timeDelta, parent);
		
		if (!started) {
			onStart();
			started = true;
		}
	}
	
	public boolean handleGameEvent(GameEvent event) {
		return false;
	}
	
	protected abstract void onStart();
	
	protected final int phase;
	protected PhaseObjectListener listener;
	protected boolean started;
	protected boolean finished;

}
