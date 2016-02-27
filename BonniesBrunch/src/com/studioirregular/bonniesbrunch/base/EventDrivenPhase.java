package com.studioirregular.bonniesbrunch.base;

import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;

public abstract class EventDrivenPhase extends PhasedObject {

	public EventDrivenPhase(int phase, PhaseObjectListener listener, GameEvent endEvent) {
		super(phase, listener);
		this.endEvent = endEvent;
	}
	
	@Override
	public boolean handleGameEvent(GameEvent event) {
		if (endEvent.equals(event)) {
			if (listener != null) {
				listener.onNextPhase(this);
			}
			return true;
		}
		return false;
	}
	
	protected GameEvent endEvent;

}
