package com.studioirregular.bonniep2;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class StatfulRenderComponent extends RenderComponent {

	private static final boolean DO_LOG = false;
	private static final String TAG = "statful-render-component";
	
	private final Integer DEFAULT_STATE;
	private Integer currentState;
	private Map<Integer, GLTexture> state2Texture = new HashMap<Integer, GLTexture>();
	
	public StatfulRenderComponent(String id, float left, float top, float width, float height, int defaultState, GLTexture texture) {
		super(id, left, top, width, height, null);
		
		DEFAULT_STATE = defaultState;
		currentState = defaultState;
		state2Texture.put(DEFAULT_STATE, texture);
	}
	
	@Override
	public String toString() {
		return "StatfulRenderComponent id:" + id + ", DEFAULT_STATE:" + DEFAULT_STATE + ", #state2Texture:" + state2Texture.size();
	}
	
	@Override
	public GLTexture getGLTexture() {
		GLTexture tex = state2Texture.get(currentState);
		if (tex == null) {
			return state2Texture.get(DEFAULT_STATE);
		}
		return tex;
	}
	
	public void addState(int state, GLTexture texture) {
		if (state2Texture.containsKey(state)) {
			Log.w(TAG, "state existed:" + state + ", will be replaced by new texture.");
		}
		state2Texture.put(state, texture);
	}
	
	public void setState(int newState) {
		if (DO_LOG) Log.d(TAG, "setState newState:" + newState + ", currentState:" + currentState);
		
		if (state2Texture.containsKey(newState)) {
			currentState = newState;
		} else {
			currentState = DEFAULT_STATE;
		}
		if (DO_LOG) Log.d(TAG, "AFTER setState currentState:" + currentState);
	}
	
}
