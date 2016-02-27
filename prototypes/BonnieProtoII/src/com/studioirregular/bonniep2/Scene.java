package com.studioirregular.bonniep2;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

public interface Scene {
	public void setSceneParser(SceneParser parser);
	public void setTextureResource(int id);
	public void load(Context context, GL10 gl);
	public boolean isLoaded();
	public void start();
	public void nextNode();
	public void stop(GL10 gl);
	public boolean isStop();
	
	public void update();
	public void draw(GL10 gl);
	
	// Invoker: setCommand
	public void addSceneNode(SceneNode node);
	
	// command receiver
	public void addEntity(Entity entity);
	public void addEntity(Entity entity, String afterEntityId);
	public void removeEntity(String entityId);
	public void removeEntity(Entity entity);
	public Entity getEntity(String entityId);
	
	public void scheduleCommand(Command command);
}
