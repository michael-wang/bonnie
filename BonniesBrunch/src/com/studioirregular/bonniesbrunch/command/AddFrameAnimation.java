package com.studioirregular.bonniesbrunch.command;

import java.util.ArrayList;
import java.util.List;

import com.studioirregular.bonniesbrunch.component.FrameAnimationComponent;
import com.studioirregular.bonniesbrunch.main.SceneManager;

public class AddFrameAnimation implements Command {

	public AddFrameAnimation(SceneManager manager, String entityName,
			int zOrder, float dx, float dy, float w, float h, boolean loop,
			boolean fillBefore, boolean fillAfter) {
		this.manager = manager;
		this.entityName = entityName;
		this.zOrder = zOrder;
		this.dx = dx;
		this.dy = dy;
		this.w = w;
		this.h = h;
		
		this.loop = loop;
		this.fillBefore = fillBefore;
		this.fillAfter = fillAfter;
	}
	
	public void addFrame(String texturePartitionId, int duration) {
		Frame frame = new Frame(texturePartitionId, duration);
		frames.add(frame);
	}
	
	@Override
	public void execute() {
		FrameAnimationComponent component = new FrameAnimationComponent(zOrder);
		component.setupOffset(dx, dy);
		component.setLoop(loop);
		component.setFillBefore(fillBefore);
		component.setFillAfter(fillAfter);
		
		for (Frame frame : frames) {
			component.addFrame(frame.texture, w, h, frame.duration);
		}
		
		manager.addComponent(entityName, component);
		
		component.start();
	}
	
	private SceneManager manager;
	private String entityName;
	private int zOrder;
	private float dx, dy, w, h;
	
	private boolean loop, fillBefore, fillAfter;
	
	private class Frame {
		String texture;
		int duration;
		public Frame(String texture, int duration) {
			this.texture = texture;
			this.duration = duration;
		}
	}
	
	private List<Frame> frames = new ArrayList<Frame>();

}
