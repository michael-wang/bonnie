package com.studioirregular.bonniep1;

import android.content.Context;
import android.media.MediaPlayer;

public class Game {

	public interface GameStateListener {
		public void onSceneChanged(Scene scene);
	}
	
	private Context context;
	private Scene scene;
	private GameStateListener listener;
	private MediaPlayer mediaPlayer;
	private boolean musicOn = true;
	
	public Game(Context context) {
		this.context = context;
	}
	
	public void registerStateListener(GameStateListener listener) {
		this.listener = listener;
	}
	
	public void unregisterStateListener() {
		this.listener = null;
	}
	
	public void start() {
		scene = new SceneMainMenu(context, this);
//		scene = new SceneSelectLevel(context, this);
		if (listener != null) {
			listener.onSceneChanged(scene);
		}
	}
	
	public void end() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
		}
	}
	
	public void pause() {
		if (mediaPlayer != null) {
			mediaPlayer.pause();
		}
	}
	
	public void resume() {
		if (mediaPlayer != null && (scene != null && scene.isLoaded())) {
			mediaPlayer.start();
		}
	}
	
	public void onSceneReady() {
		if(musicOn) {
			startMusic();
		}
	}
	
	public void nextScene() {
		if (scene instanceof SceneMainMenu) {
			scene = new SceneSelectLevel(context, this);
		}
		
		if (listener != null) {
			listener.onSceneChanged(scene);
		}
	}
	
	public void backScene() {
		if (scene instanceof SceneSelectLevel) {
			scene = new SceneMainMenu(context, this);
		}
		
		if (listener != null) {
			listener.onSceneChanged(scene);
		}
	}
	
	public void music(boolean on) {
		if (on) {
			startMusic();
		} else {
			if (mediaPlayer != null) {
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
			}
		}
	}
	
	private void startMusic() {
		if (mediaPlayer == null) {
			mediaPlayer = MediaPlayer.create(context, R.raw.bgm_menu);
			mediaPlayer.setLooping(true);
		}
		mediaPlayer.start();
	}
	
}
