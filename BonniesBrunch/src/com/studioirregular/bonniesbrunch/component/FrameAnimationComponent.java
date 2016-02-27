package com.studioirregular.bonniesbrunch.component;

import java.util.ArrayList;

import android.util.Log;

import com.studioirregular.bonniesbrunch.RenderSystem;
import com.studioirregular.bonniesbrunch.RenderSystem.RenderObject;
import com.studioirregular.bonniesbrunch.TextureSystem;
import com.studioirregular.bonniesbrunch.TextureSystem.Texture;
import com.studioirregular.bonniesbrunch.TextureSystem.TexturePartition;
import com.studioirregular.bonniesbrunch.entity.GameEntity;

public class FrameAnimationComponent extends Animation {

	private static final String TAG = "frame-animation-component";
	
	public FrameAnimationComponent(int zOrder) {
		super(zOrder);
	}
	
	public void setupOffset(float dx, float dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	public boolean addFrame(String texturePartitionId, float width, float height, long duration) {
		TexturePartition part = TextureSystem.getInstance().getPartition(texturePartitionId);
		if (part == null) {
			Log.w(TAG, "cannot find texture partition:" + texturePartitionId);
		}
		
		Texture texture = null;
		if (part != null) {
			texture = TextureSystem.getInstance().getTexture(part.textureId);
		}
		
		if (texture == null) {
			Log.w(TAG, "cannot find texture for partition:" + texturePartitionId);
		}
		
		RenderObject render = new RenderObject(width, height);
		if (texture != null && part != null) {
			render.setTexture(texture, part);
		}
		frames.add(new Frame(render, this.duration));
		this.duration += duration;
		
		if (texture == null && part == null) {
			return false;
		}
		return true;
	}

	@Override
	public void update(long timeDelta, GameEntity parent) {
		if (onUpdate(timeDelta, parent) == false) {
			return;
		}
		
		if (parent.isVisible() == false) {
			return;
		}
		
		Frame newFrame = frames.get(0);
		for (int i = 0; i < frames.size(); i++) {
			final Frame frame = frames.get(i);
			
			if (frame.startTime < elapsedTime) {
				newFrame = frame;
			} else {
				break;
			}
		}
		
		if (newFrame == null) {
			Log.e(TAG, "update cannot find newFrame, elapsedTime:" + elapsedTime + ",duration:" + duration);
			return;
		}
		
		final RenderObject render = newFrame.render;
		render.setPosition(parent.getX() + dx, parent.getY() + dy);
		render.changeScale(parent.scale);
		RenderSystem.getInstance().scheduleRenderObject(render);
		
		currentFrame = newFrame;
	}
	
	@Override
	protected void onFillBefore(GameEntity parent) {
		if (frames.isEmpty() == false) {
			currentFrame = frames.get(0);
			final RenderObject render = currentFrame.render;
			
			render.setPosition(parent.getX() + dx, parent.getY() + dy);
			render.changeScale(parent.scale);
			RenderSystem.getInstance().scheduleRenderObject(render);
		}
	}
	
	@Override
	protected void onFillAfter(GameEntity parent) {
		if (frames.isEmpty() == false) {
			currentFrame = frames.get(frames.size() - 1);
			final RenderObject render = currentFrame.render;
			
			render.setPosition(parent.getX() + dx, parent.getY() + dy);
			render.changeScale(parent.scale);
			RenderSystem.getInstance().scheduleRenderObject(render);
		}
	}
	
	private class Frame {
		private RenderObject render;
		private int startTime;
		public Frame(RenderObject render, int startTime) {
			this.render = render;
			this.startTime = startTime;
		}
	}
	
	public ArrayList<Frame> frames = new ArrayList<Frame>();
	private Frame currentFrame;
	
	protected float dx  = 0, dy = 0;

}
