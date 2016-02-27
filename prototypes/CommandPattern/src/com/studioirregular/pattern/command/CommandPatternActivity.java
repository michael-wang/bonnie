package com.studioirregular.pattern.command;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

public class CommandPatternActivity extends Activity {
    
	private static final String TAG = "command-pattern";
	
	private Scene scene;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        scene = new Scene();
        SceneParser parser = new SceneParser();
        parser.parse(scene);
        scene.start();
    }
    
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		
		if (action == MotionEvent.ACTION_UP) {
			String nodeName = scene.getCurrentNode().getName();
			if (nodeName.equals("node_1")) {
				scene.onEvent(new ButtonEvent(ButtonEvent.BUTTON_CLICKED, "story_p1"));
			} else if (nodeName.equals("node_2")) {
				scene.onEvent(new ButtonEvent(ButtonEvent.BUTTON_CLICKED, "story_p2"));
			}
		}
		
		return true;
	}
    
}