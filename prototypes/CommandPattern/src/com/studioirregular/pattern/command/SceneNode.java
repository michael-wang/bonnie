package com.studioirregular.pattern.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

public class SceneNode {

	private static final String TAG = "node";
	
	private String name;
	private List<Command> commands = new ArrayList<Command>();
	private Map<GameEvent, Command> event2Command = new HashMap<GameEvent, Command>();
	
	public SceneNode(String name) {
		this.name = name;
	}
	
	public String getName() {
		 return name;
	}
	
	public void addCommand(Command command) {
		Log.d(TAG, "addCommand:" + command);
		commands.add(command);
	}
	
	public void addEventCommandPair(GameEvent event, Command command) {
		Log.d(TAG, "addEventCommandPair event:" + event + ", command:" + command);
		event2Command.put(event, command);
	}
	
	public void execute() {
		Log.d(TAG, "execute");
		for (Command command : commands) {
			command.execute();
		}
	}
	
	public Command getMappedCommand(GameEvent event) {
		return event2Command.get(event);
	}
	
	@Override
	public String toString() {
		return "SceneNode #commands:" + commands.size() + ", #event2Command:" + event2Command.size();
	}
	
}
