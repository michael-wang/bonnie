package com.studioirregular.pattern.command;

import android.graphics.RectF;
import android.util.Log;

public class AddEntityCommand implements Command {

	private static final String TAG = "add-entity-command";
	
	private Scene scene;
	private String name;
	private RectF box;
	
	public AddEntityCommand(Scene scene, String name, float left, float top, float right, float bottom) {
		this.scene = scene;
		this.name = name;
		this.box = new RectF(left, top, right, bottom);
	}
	
	@Override
	public void execute() {
		Log.d(TAG, "execute name:" + name + ",box:" + box);
		
		GameEntity entity = new GameEntity(name, box.left, box.top, box.right, box.bottom);
		scene.addEntity(entity);
	}
	
	@Override
	public String toString() {
		return "AddEntityCommand name:" + name + ", box:" + box;
	}
	
}
