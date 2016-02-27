package com.studioirregular.bonniep2;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.util.Pair;

public class AddFrameAnimationComponentCommand implements Command {

	private static final boolean DO_LOG = false;
	private static final String TAG = "add-frame-animation-component-command";
	
	private Scene scene;
	private String entityId;
	private String componentId;
	private float left, top, width, height;
	private List< Pair<String, Long> > frameList = new ArrayList< Pair<String, Long> >();
	private boolean loop;
	private boolean startAfterAdded;
	
	public AddFrameAnimationComponentCommand(Scene scene, String entityId,
			String componentId, float left, float top, float width,
			float height, boolean loop, boolean startAfterAdded) {
		this.scene = scene;
		this.entityId = entityId;
		this.componentId = componentId;
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		this.loop = loop;
		this.startAfterAdded = startAfterAdded;
	}
	
	public void addFrame(String textureId, long duration) {
		if (DO_LOG) Log.d(TAG, "addFrame textureId:" + textureId + ", duration:" + duration);
		frameList.add(Pair.create(textureId, duration));
	}
	
	@Override
	public void execute() {
		if (DO_LOG) Log.d(TAG, "execute entityId:" + entityId + ", componentId:"
				+ componentId + ", width:" + width + ", height:" + height
				+ ", #frameList:" + frameList.size());
		
		BasicEntity entity = (BasicEntity)scene.getEntity(entityId);
		if (entity == null) {
			Log.e(TAG, "execute: cannot find entity:" + entityId);
			return;
		}
		
		if (entity instanceof EventHost == false) {
			Log.e(TAG, "execute: entity cannot cast to EventHost:" + entityId);
			return;
		}
		EventHost eventHost = (EventHost)entity;
		
		FrameAnimationComponent component = new FrameAnimationComponent(
				componentId, left, top, width, height, loop, eventHost);
		
		for (Pair<String, Long> frame : frameList) {
			GLTexture texture = TextureSystem.getInstance().getPart(frame.first);
			if (texture == null) {
				Log.e(TAG, "execute: cannot find texture:" + frame.first);
				return;
			}
			
			component.addFrame(texture, frame.second);
		}
		
		entity.add(component);
		
		if (startAfterAdded) {
			component.start();
		}
	}
	
	@Override
	public String toString() {
		return "AddFrameAnimationComponentCommand entityId:" + entityId + ", componentId:"
				+ componentId + ", width:" + width + ", height:" + height
				+ ", #frameList:" + frameList.size();
	}
	
}
