package com.studioirregular.bonniesbrunch.command;

import com.studioirregular.bonniesbrunch.GameEventSystem;

public class SendGameEvent implements Command {

	public SendGameEvent(int what, int arg1, Object obj) {
		this.what = what;
		this.arg1 = arg1;
		this.obj = obj;
	}
	
	@Override
	public void execute() {
		GameEventSystem.scheduleEvent(what, arg1, obj);
	}
	
	private int what;
	private int arg1;
	private Object obj;

}
