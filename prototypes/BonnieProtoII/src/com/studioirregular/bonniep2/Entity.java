package com.studioirregular.bonniep2;

import android.graphics.RectF;

public interface Entity {
	public String getId();
	public void add(Component component);
	public void add(Component component, String afterComponentId);
	public void remove(String componentId);
	public Component getComponent(String componentId);
	
	public void update(long timeDiff);
	public int getRenderableCount();
	public GLRenderable getRenderable(int index);
	public RectF getBoundingBox();
	
	public void setEnable(boolean enabled);
	public void setVisible(boolean visible);
}
