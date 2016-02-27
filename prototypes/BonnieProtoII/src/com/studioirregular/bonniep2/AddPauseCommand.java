package com.studioirregular.bonniep2;

public class AddPauseCommand implements Command {

	private SceneBase scene;
	
	public AddPauseCommand(SceneBase scene) {
		this.scene = scene;
	}

	@Override
	public void execute() {
		scene.onAddPauseButton();
	}

}
