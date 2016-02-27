package com.studioirregular.bonniep2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

public class SceneNode {

	private static final boolean DO_LOG = false;
	private static final String TAG = "node";
	
	private String name;
	private List<Command> commands = new ArrayList<Command>();
	private Map<Event, Command> event2Command = new HashMap<Event, Command>();
	
	public SceneNode(String name) {
		this.name = name;
	}
	
	public String getName() {
		 return name;
	}
	
	public void addCommand(Command command) {
		if (DO_LOG) Log.d(TAG, "addCommand:" + command);
		commands.add(command);
	}
	
	public void addEventCommandPair(Event event, Command command) {
		if (DO_LOG) Log.d(TAG, "addEventCommandPair event:" + event + ", command:" + command);
		event2Command.put(event, command);
	}
	
	public void execute(Scene scene) {
		if (DO_LOG) Log.d(TAG, "execute");
		for (Command command : commands) {
			scene.scheduleCommand(command);
		}
	}
	
	public Command getMappedCommand(Event event) {
		return event2Command.get(event);
	}
	
	@Override
	public String toString() {
		return "SceneNode #commands:" + commands.size() + ", #event2Command:" + event2Command.size();
	}
	
}
