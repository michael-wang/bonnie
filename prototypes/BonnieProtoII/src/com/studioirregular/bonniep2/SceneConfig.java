package com.studioirregular.bonniep2;

public class SceneConfig {

	public final int textureConfigResourceId;
	public final int levelConfigId;
	public final Class<? extends SceneParser> parserClass;
	
	public SceneConfig(int textureConfigId, int levelConfigId, Class<? extends SceneParser> parserClass) {
		this.textureConfigResourceId = textureConfigId;
		this.levelConfigId = levelConfigId;
		this.parserClass = parserClass;
	}
}
