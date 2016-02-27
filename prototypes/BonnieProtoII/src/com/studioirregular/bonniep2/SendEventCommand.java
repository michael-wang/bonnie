package com.studioirregular.bonniep2;


// send event to EventListener
public class SendEventCommand implements Command {

	
	private Event event;
	private EventListener listener;
	
	public SendEventCommand(Event event, EventListener listener) {
		this.event = event;
		this.listener = listener;
	}
	
	@Override
	public void execute() {
		listener.onEvent(event);
	}
	
	@Override
	public String toString() {
		return "SendEventCommand event:" + event + ", listener:" + listener;
	}
	
}
