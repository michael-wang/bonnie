package com.studioirregular.bonniesbrunch.main;


public abstract class SceneLoader {

	public SceneLoader(SceneManager manager, GameEntityRoot root) {
		this.manager = manager;
		this.root = root;
	}
	
	public abstract void load();
	
	protected SceneManager manager;
	protected GameEntityRoot root;
}
