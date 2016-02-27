package com.studioirregular.bonniep2;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.util.Pair;

public class FrameAnimationComponent extends RenderComponent implements Animation {

	private static final String TAG = "frame-animation-component";
	private static final boolean DO_LOG = false;
	
	private EventHost eventHost;
	
	private boolean isStarted = false;
	private boolean loop = false;
	// each frame consisted by a texture and time offset.
	private List< Pair<GLTexture, Long> >frameList = new ArrayList< Pair<GLTexture, Long> >();
	private GLTexture currentFrame;
	
	private long elapsedTime;
	private long totalDuration = 0;
	
	public FrameAnimationComponent(String id, float left, float top,
			float width, float height, boolean loop, EventHost eventHost) {
		
		super(id, left, top, width, height, null);
		this.loop = loop;
		this.eventHost = eventHost;
	}
	
	public void addFrame(GLTexture texture, long duration) {
		if (DO_LOG) Log.d(TAG, "addFrame duration:" + duration + ", totalDuration:" + totalDuration);
		
		frameList.add(Pair.create(texture, totalDuration));
		
		totalDuration += duration;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " id:" + id + ", isStarted:"
				+ isStarted + ", #frameList:" + frameList.size() + ",loop:" + loop;
	}
	
	@Override
	public GLTexture getGLTexture() {
		if (!isStarted && frameList.isEmpty() == false) {
			currentFrame = frameList.get(0).first;
		}
		return currentFrame;
	}
	
	// Animation interface
	@Override
	public void setLoop(boolean loop) {
		this.loop = loop;
	}
	
	@Override
	public boolean isStarted() {
		return isStarted;
	}

	@Override
	public void start() {
		if (DO_LOG) Log.d(TAG, "start");
		
		if (frameList.isEmpty()) {
			Log.w(TAG, "start: empty frameList, cannot start animation!");
			return;
		}
		
		isStarted = true;
		if (eventHost != null) {
			eventHost.send(this, new ComponentEvent(ComponentEvent.ANIMATION_STARTED, id));
		}
		elapsedTime = 0;
	}
	
	@Override
	public void stop() {
		onStop();
	}
	
	@Override
	public void update(long timeDiff) {
		elapsedTime += timeDiff;
		if (elapsedTime > totalDuration) {
			if (!loop) {
				if (DO_LOG) Log.d(TAG, "update totalDuration reached!");
				onStop();
				return;
			} else {
				elapsedTime = (elapsedTime % totalDuration);
			}
		}
		
		GLTexture newFrame = frameList.get(0).first;
		for (Pair<GLTexture, Long> frame : frameList) {
			final long startTimeOffset = frame.second;
			if (startTimeOffset < elapsedTime) {
				newFrame = frame.first;
			} else {
				break;
			}
		}
		
		currentFrame = newFrame;
	}
	
	protected void onStop() {
		isStarted = false;
		if (eventHost != null) {
			eventHost.send(this, new ComponentEvent(ComponentEvent.ANIMATION_ENDED, id));
		}
	}
	
}
