package com.studioirregular.bonniep2;


public class NextNodeCommand implements Command {

//	private static final String TAG = "next-node-command";
	
	private Scene scene;
	
	public NextNodeCommand(Scene scene) {
		this.scene = scene;
	}
	
	@Override
	public void execute() {
//		Log.d(TAG, "execute");
		scene.nextNode();
	}

}
