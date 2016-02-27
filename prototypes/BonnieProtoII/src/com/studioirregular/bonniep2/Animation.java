package com.studioirregular.bonniep2;

public interface Animation {

	public void setLoop(boolean loop);
	public boolean isStarted();
	public void start();	// should send ComponentEvent.ANIMATION_STARTED here
	public void stop();		// should send ComponentEvent.ANIMATION_ENDED here
	public void update(long timeDiff);	// should send ComponentEvent.ANIMATION_ENDED here
}
