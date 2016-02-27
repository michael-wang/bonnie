package com.studioirregular.bonniesbrunch.main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.EventMap;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.command.Command;

public class SceneNode {

	public void add(Command command) {
		commands.add(command);
	}
	
	public void run() {
		for (Command command : commands) {
			command.execute();
		}
	}
	
	public void addEventMap(GameEvent key, GameEvent value) {
		EventMap map = new EventMap(key, value);
		eventMaps.add(map);
	}
	
	public void handleEventMap(GameEvent event) {
		for (EventMap map : eventMaps) {
			final GameEvent key = map.key;
			// we can't check equality here, for many event's obj field is some entity's "this".
			// which can't be known when scene node is constructed.
			if (key.what == event.what && key.arg1 == event.arg1) {
				final GameEvent emit = map.value;
				GameEventSystem.scheduleEvent(emit.what, emit.arg1, emit.obj);
			}
		}
	}
	
	private LinkedList<Command> commands = new LinkedList<Command>();
	private List<EventMap> eventMaps = new ArrayList<EventMap>();
}
