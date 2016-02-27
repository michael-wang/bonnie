package com.studioirregular.bonniep1;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class StatfulRenderComponent extends RenderComponent implements GameEntity.StateChangedListener {

	private Map<Integer, Texture> state2Texture;
	private Integer state;
	
	public StatfulRenderComponent(Texture texture, Integer defaultState) {
		super(texture);
		this.state = defaultState;
		state2Texture = new HashMap<Integer, Texture>();
	}
	
	public void addState(Integer state, Texture texture) {
		state2Texture.put(state, texture);
	}
	
	@Override
	public int getTextureId() {
		return state2Texture.get(state).id;
	}
	
	@Override
	public FloatBuffer getTextureCoordinates() {
		return state2Texture.get(state).coordinates;
	}
	
	@Override
	public void onStateChanged(Integer newState) {
		if (state2Texture.containsKey(newState)) {
			state = newState;
		}
	}

}
