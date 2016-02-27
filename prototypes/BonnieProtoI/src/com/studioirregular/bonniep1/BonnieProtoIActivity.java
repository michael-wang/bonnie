package com.studioirregular.bonniep1;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class BonnieProtoIActivity extends Activity implements Game.GameStateListener {
    private MyGLSurfaceView surfaceView;
    private MyRenderer renderer;
    private Game game;
    private TextView fpsView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        surfaceView = (MyGLSurfaceView)findViewById(R.id.surfaceView);
        
        renderer = new MyRenderer(this);
        surfaceView.setRenderer(renderer);
        
        game = new Game(this);
        game.registerStateListener(this);
        game.start();
        
        fpsView = (TextView)findViewById(R.id.fps);
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		surfaceView.onResume();
		game.resume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		surfaceView.onPause();
		game.pause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		game.end();
	}
	
	@Override
	public void onSceneChanged(Scene scene) {
		renderer.setScene(scene);
		
		if (scene instanceof UserInput) {
			surfaceView.setGestureReceiver((UserInput)scene);
		}
	}
	
	public void updateFPS(float fps) {
		handler.sendMessage(handler.obtainMessage(0, (int)fps, 0));
	}
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			fpsView.setText("FPS:" + msg.arg1);
		}
		
	};
    
}