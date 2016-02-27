package com.studioirregular.bonniep2;

public class SceneEvent extends Event {

	public static final int LEVEL_START				= 0x3001;
	public static final int LEVEL_TIME_UP			= 0x3002;
	public static final int SCENE_PAUSE				= 0x3003;
	public static final int SCENE_RESUME			= 0x3004;
	
	public SceneEvent(int what) {
		super(what);
	}

}
