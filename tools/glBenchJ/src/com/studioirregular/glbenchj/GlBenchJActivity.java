package com.studioirregular.glbenchj;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GlBenchJActivity extends Activity {
	private MyGLSurfaceView view;
	private TextView fpsView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        fpsView = (TextView)findViewById(R.id.fps);
        view = new MyGLSurfaceView(this);
        view.setRenderer(new MyRenderer(GlBenchJActivity.this, 16));
        
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.layout);
        layout.addView(view, 0, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		view.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		view.onPause();
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