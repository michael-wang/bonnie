package com.studioirregular.bonniep1;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class AnimationComponent extends RenderComponent {

	private static final String TAG = "comp-animation";
	private static final boolean DO_LOG = false;
	
	public AnimationComponent(Texture texture, boolean loop) {
		super(texture);
		this.loop = loop;
	}
	
	public void addKeyFrame(Texture texture, Long duration) {
		Animation anim = new Animation();
		anim.texture = texture;
		anim.startTimeOffset = this.duration;
		animations.add(anim);
		
		this.duration += duration;
		if (DO_LOG) Log.d(TAG, "addKeyFrame endTimeOffset" + anim.startTimeOffset);
	}
	
	public void startAnimation() {
		currentFrame = animations.get(0).texture;
		startTime = System.currentTimeMillis();
		state = STATE_STARTED;
	}
	
	public void update() {
		if (state != STATE_STARTED) {
			return;
		}
		
		Long timeOffset = System.currentTimeMillis() - startTime;
		if (timeOffset > duration) {
			if (!loop) {
				state = STATE_FINISHED;
				return;
			} else {
				timeOffset = (timeOffset % duration);
			}
		}
		if (DO_LOG) Log.d(TAG, "update timeOffset:" + timeOffset);
		
		Texture newFrame = animations.get(0).texture;
		for (Animation animation : animations) {
			if (animation.startTimeOffset < timeOffset) {
				newFrame = animation.texture;
			} else {
				break;
			}
		}
		
		currentFrame = newFrame;
	}
	
	@Override
	public int getTextureId() {
		if (state == STATE_NONE) {
			return super.getTextureId();
		}
		return currentFrame.id;
	}
	
	@Override
	public FloatBuffer getTextureCoordinates() {
		if (state == STATE_NONE) {
			return super.getTextureCoordinates();
		}
		return currentFrame.coordinates;
	}
	
	private static final int STATE_NONE = 0;
	private static final int STATE_STARTED = 1;
	private static final int STATE_FINISHED = 2;
	private int state = STATE_NONE;
	private Long startTime = 0L;
	private Long duration = 0L;
	private boolean loop = false;
	
	private class Animation {
		Texture texture;
		Long startTimeOffset;
	}
	private List<Animation> animations = new ArrayList<Animation>();
	private Texture currentFrame;
}
