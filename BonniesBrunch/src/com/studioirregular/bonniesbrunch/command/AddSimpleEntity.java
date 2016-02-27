package com.studioirregular.bonniesbrunch.command;

import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.entity.SimpleEntity;
import com.studioirregular.bonniesbrunch.main.SceneManager;

public class AddSimpleEntity implements Command {

	public AddSimpleEntity(SceneManager manager, String name, int zOrder, float x, float y, float w, float h, String texture) {
		this.manager = manager;
		this.name = name;
		this.zOrder = zOrder;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.texture = texture;
	}
	
	public void installEventMap(GameEvent receive, GameEvent emit) {
		this.receive = receive;
		this.emit = emit;
	}
	
	@Override
	public void execute() {
		SimpleEntity entity = new SimpleEntity(zOrder);
		entity.setup(x, y, w, h, texture);
		
		if (receive != null && emit != null) {
			entity.installEventMap(receive, emit);
		}
		
		manager.add(name, entity);
	}
	
	private SceneManager manager;
	private String name;
	private int zOrder;
	private float x, y, w, h;
	private String texture;
	
	private GameEvent receive, emit;

}
