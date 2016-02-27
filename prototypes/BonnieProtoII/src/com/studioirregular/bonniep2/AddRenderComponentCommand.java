package com.studioirregular.bonniep2;

import android.util.Log;

public class AddRenderComponentCommand implements Command {

	private static final boolean DO_LOG = false;
	private static final String TAG = "add-component-command";
	
	protected Scene scene;
	protected String entityId;
	
	// method I: deley creation of render component
	protected String componentId;
	protected float left, top, width, height;
	protected String textureId;
	
	// method II: created render component
	protected RenderComponent render;
	
	public AddRenderComponentCommand(Scene scene, String entityId, String componentId,
			float left, float top, float width, float height, String textureId) {
		
		this(scene, entityId, null);
		
		this.componentId = componentId;
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		this.textureId = textureId;
	}
	
	public AddRenderComponentCommand(Scene scene, String entityId, RenderComponent render) {
		this.scene = scene;
		this.entityId = entityId;
		this.render = render;
	}
	
	@Override
	public void execute() {
		if (DO_LOG) Log.d(TAG, "execute componentId:" + componentId + ",entityId:" + entityId);
		
		Entity entity = scene.getEntity(entityId);
		if (entity == null) {
			Log.e(TAG, "execute: cannot find entity:" + entityId);
			return;
		}
		
		if (render == null) {
			// method I
			GLTexture texture = TextureSystem.getInstance().getPart(textureId);
			if (texture == null) {
				Log.e(TAG, "execute: cannot load texture:" + textureId);
				return;
			}
			
			render = new RenderComponent(componentId, left, top, width, height, texture);
		}
		
		entity.add(render);
	}
	
	@Override
	public String toString() {
		return "AddRenderComponentCommand entityId:" + entityId
				+ ", componentId:" + componentId + ", left:" + left + ", top:"
				+ top + ", width:" + width + ", height:" + height
				+ ", textureId:" + textureId;
	}
	
}
