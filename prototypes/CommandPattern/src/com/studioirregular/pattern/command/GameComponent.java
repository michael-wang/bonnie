package com.studioirregular.pattern.command;

public class GameComponent {

	private GameEntity host;
	private String name;
	
	public GameComponent(GameEntity host, String name) {
		this.host = host;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return "GameComponent name:" + name;
	}
	
}
