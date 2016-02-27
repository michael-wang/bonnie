package com.studioirregular.bonniesbrunch.command;

import com.studioirregular.bonniesbrunch.SoundSystem;
import com.studioirregular.bonniesbrunch.SoundSystem.Sound;

public class PlaySound implements Command {

	public PlaySound(String soundResource, boolean loop) {
		this.soundResource = soundResource;
		this.loop = loop;
	}
	
	@Override
	public void execute() {
		Sound sound = SoundSystem.getInstance().load(soundResource);
		SoundSystem.getInstance().playSound(sound, loop);
	}
	
	private String soundResource;
	private boolean loop;

}
