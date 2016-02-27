package com.studioirregular.bonniep2;


public class FrameRateCalculator {

	private static final long UPDATE_INTERVAL = 1000L;	// 1 sec
	
	private long startTime;
	private long count;
	private float fps;
	
	public void start() {
		startTime = System.currentTimeMillis();
	}
	
	public void addFrame() {
		count++;
		final long now = System.currentTimeMillis();
		if (now - startTime > UPDATE_INTERVAL) {
			updateFPS(now);
			count = 0;
			startTime = now;
		}
	}
	
	public float getFPS() {
		return fps;
	}
	
	private void updateFPS(long now) {
		fps = count;
	}
}
