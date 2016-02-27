package com.studioirregular.bonniep2;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.util.Pair;

public class AddStatfulRenderComponentCommand extends AddRenderComponentCommand {

	private static final String TAG = "add-statful-render-component-command";
	
	private int defaultState;
	private String defaultTextureId;
	private List<Pair<Integer, String> > pairList = new ArrayList<Pair<Integer, String> >();
	
	public AddStatfulRenderComponentCommand(Scene scene, String entityName,
			String componentName, float left, float top, float width, float height, int defaultState, String defaultTextureId) {
		super(scene, entityName, componentName, left, top, width, height, defaultTextureId);
		this.defaultState = defaultState;
		this.defaultTextureId = defaultTextureId;
	}
	
	public void addStateTexturePair(int state, String textureId) {
		pairList.add(Pair.create(state, textureId));
	}
	
	@Override
	public void execute() {
		Log.d(TAG, "execute componentId:" + componentId + ",entityId:" + entityId);
		
		GLTexture defaultTexture = TextureSystem.getInstance().getPart(defaultTextureId);
		if (defaultTexture == null) {
			Log.e(TAG, "execute: cannot load texture:" + textureId);
			return;
		}
		
		StatfulRenderComponent render = new StatfulRenderComponent(componentId,
				left, top, width, height, defaultState, defaultTexture);
		
		for (Pair<Integer, String> pair : pairList) {
			GLTexture texture = TextureSystem.getInstance().getPart(pair.second);
			if (texture == null) {
				Log.e(TAG, "execute: cannot load texture:" + textureId);
				return;
			}
			render.addState(pair.first, texture);
		}
		
		Entity entity = scene.getEntity(entityId);
		if (entity == null) {
			Log.e(TAG, "execute: cannot find entity:" + entityId);
			return;
		}
		
		entity.add(render);
	}
	
	@Override
	public String toString() {
		return "AddStatfulRenderComponentCommand entityId:" + entityId
				+ ", componentId:" + componentId + ", left:" + left + ", top:"
				+ top + ", width:" + width + ", height:" + height
				+ ", textureId:" + textureId;
	}

}
