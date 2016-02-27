package com.studioirregular.bonniesbrunch.component;

import android.util.Log;

import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.entity.GameEntity;

public abstract class Animation extends GameComponent {

	private static final String TAG = "animation";
	
	public Animation(int zOrder) {
		super(zOrder);
	}
	
	public void setLoop(boolean loop) {
		this.loop = loop;
	}
	
	public boolean isLoop() {
		return loop;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public void setFillBefore(boolean fill) {
		fillBefore = fill;
	}
	
	public void setFillAfter(boolean fill) {
		fillAfter = fill;
	}
	
	// offset: in ms.
	public void setStartOffset(int offset) {
		startOffset = offset;
	}
	
	public void removeSelfWhenFinished(boolean enable) {
		removeWhenFinished = enable;
	}
	
	public void notifyWhenFinished(boolean notify, int id) {
		notifyWhenFinished = notify;
		this.notifyId = id;
	}
	
	public void start() {
		started = true;
		finished = false;
		elapsedTime = 0;
		schduleRemove = false;
		startOffsetDone = false;
		if (duration == 0) {
			Log.e(TAG, "duration == 0, stop animation.");
			stop();
		}
	}
	
	public void stop() {
		started = false;
		finished = true;
		if (removeWhenFinished) {
			schduleRemove = true;
		}
	}
	
	public void rewind() {
		started = false;
		finished = false;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public boolean isStarted() {
		return started;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	protected boolean onUpdate(long timeDelta, GameEntity parent) {
		if (schduleRemove) {
			schduleRemove = false;
			parent.remove(this);
			return false;
		}
		
		if (finished) {
			if (fillAfter) {
				onFillAfter(parent);
			}
			return false;
		}
		if (!started) {
			if (fillBefore && parent.isVisible()) {
				onFillBefore(parent);
			}
			return false;
		}
		
		elapsedTime += timeDelta;
		
		if (!startOffsetDone) {
			if (startOffset <= elapsedTime) {
				startOffsetDone = true;
				elapsedTime = 0;
			}
			return false;
		}
		
		if (duration < elapsedTime) {
			if (!loop) {
				onFinished(parent);
				stop();
				return false;
			} else {
				elapsedTime = elapsedTime % duration;
			}
		}
		
		return true;
	}
	
	protected void onFinished(GameEntity parent) {
		if (notifyWhenFinished) {
			parent.send(GameEventSystem.getInstance().obtain(GameEvent.ANIMATION_END, notifyId));
		}
	}
	
	protected abstract void onFillBefore(GameEntity parent);
	protected abstract void onFillAfter(GameEntity parent);
	
	@Override
	public void reset() {
		started = false;
		finished = false;
		elapsedTime = 0;
		schduleRemove = false;
	}
	
	protected boolean fillBefore;
	protected boolean fillAfter;
	protected boolean loop;
	protected int startOffset = 0;
	protected boolean removeWhenFinished;
	protected boolean notifyWhenFinished;
	protected int notifyId = 0;
	
	protected int duration = 0;
	protected boolean started = false;
	protected boolean finished = false;
	protected int elapsedTime;
	protected boolean schduleRemove;
	protected boolean startOffsetDone;

}
