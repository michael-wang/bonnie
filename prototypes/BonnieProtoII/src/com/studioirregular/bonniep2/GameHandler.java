package com.studioirregular.bonniep2;

import android.os.Handler;
import android.os.Message;

public class GameHandler extends Handler {
	
	public static final int MSG_GAME_START = 1;
	public static final int MSG_GAME_OVER = 2;
	public static final int MSG_UPDATE_FPS = 3;
	public static final int MSG_SCHEDULE_COMMAND = 4;
	
	private BonnieProtoIIActivity activity;
	private Game game;
	
	public GameHandler(BonnieProtoIIActivity activity) {
		this.activity = activity;
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_GAME_START:
			SceneConfig config = (SceneConfig)msg.obj;
			game.setSceneConfig(config);
			game.start();
			break;
		case MSG_GAME_OVER:
			activity.finish();
			break;
		case MSG_UPDATE_FPS:
			activity.updateFPS(msg.arg1);
			break;
		case MSG_SCHEDULE_COMMAND:
			Command command = (Command)msg.obj;
			command.execute();
			break;
		}
	}
}
