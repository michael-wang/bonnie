package com.studioirregular.bonniep2;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class BonnieProtoIIActivity extends Activity {
    
	public static final String EXTRA_SCENE_NAME = "scene_name";
	
	// map: scene name (String) to SceneConfig
	public static Map<String, SceneConfig> mapSceneConfig;
	static {
		mapSceneConfig = new HashMap<String, SceneConfig>();
		mapSceneConfig.put("just table", new SceneConfig(
				R.xml.texture_table_food, R.xml.level_table, SceneParserBase.class));
		mapSceneConfig.put("4-4 tramp", new SceneConfig(R.xml.texture_4_4,
				R.xml.level_4_4, SceneParserBase.class));
		mapSceneConfig.put("3-7 food critic", new SceneConfig(
				R.xml.texture_3_7, R.xml.level_3_7, SceneParserBase.class));
		mapSceneConfig.put("2-8 physicist", new SceneConfig(R.xml.texture_2_8,
				R.xml.level_2_8, SceneParserBase.class));
		mapSceneConfig.put("1-8 muffin machine", new SceneConfig(
				R.xml.texture_1_8, R.xml.level_1_8, SceneParserBase.class));
		mapSceneConfig.put("1-1 tutorial", new SceneConfig(R.xml.texture_1_1, 0,
				SceneParser1_1.class));
	}
	private static final String TAG = "bonnie-proto-II-activity";
	
	private MyGLSurfaceView surfaceView;
	private MyRenderer renderer;
	private Game game;
	private GameHandler handler;
	private TextView fpsView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "onCreate");
        
        setContentView(R.layout.main);
        
        handler = new GameHandler(this);
        
        surfaceView = (MyGLSurfaceView)findViewById(R.id.surfaceView);
        surfaceView.setEGLConfigChooser(false);
        
        renderer = new MyRenderer(this, handler);
        surfaceView.setRenderer(renderer);
        
        game = new Game(surfaceView, renderer, handler);
        handler.setGame(game);
        
        final Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_SCENE_NAME)) {
        	final String sceneName = intent.getStringExtra(EXTRA_SCENE_NAME);
        	SceneConfig scene = mapSceneConfig.get(sceneName);
        	if (scene == null) {
        		Toast.makeText(this, "no level found:" + sceneName, Toast.LENGTH_LONG).show();
        	} else {
        		handler.sendMessage(handler.obtainMessage(GameHandler.MSG_GAME_START, scene));
        	}
        } else {
        	Toast.makeText(this, "BonnieProtoIIActivity needs string extra: scene_name.", Toast.LENGTH_LONG).show();
        	finish();
        }
        
        fpsView = (TextView)findViewById(R.id.fps);
    }

	@Override
	protected void onResume() {
		super.onResume();
		Log.w(TAG, "onResume");
		surfaceView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.w(TAG, "onPause");
		surfaceView.onPause();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.w(TAG, "onStart");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.w(TAG, "onStop");
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.w(TAG, "onRestart");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.w(TAG, "onDestroy");
		game.stop();
	}

	public void updateFPS(int fps) {
		fpsView.setText("FPS:" + fps);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		game.stop();
	}
	
}