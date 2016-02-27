package com.studioirregular.bonniesbrunch.command;

import java.util.ArrayList;
import java.util.List;

import com.studioirregular.bonniesbrunch.main.SceneManager;

public class RemoveEntity implements Command {

	public RemoveEntity(SceneManager manager, String entityName) {
		this.manager = manager;
		names.add(entityName);
	}
	
	public void and(String entityName) {
		names.add(entityName);
	}
	
	@Override
	public void execute() {
		for (String name : names) {
			manager.remove(name);
		}
	}
	
	private SceneManager manager;
	private List<String> names = new ArrayList<String>();

}
