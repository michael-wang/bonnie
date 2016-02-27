package com.studioirregular.bonniesbrunch.base;

public class NullPhase extends PhasedObject {

	public NullPhase(int phase, PhaseObjectListener listener) {
		super(phase, listener);
	}
	
	@Override
	public void update(long timeDelta, ObjectBase parent) {
		super.update(timeDelta, parent);
	}
	
	@Override
	protected void onStart() {
		finished = true;
	}

}
