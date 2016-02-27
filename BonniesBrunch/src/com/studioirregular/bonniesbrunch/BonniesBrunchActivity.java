package com.studioirregular.bonniesbrunch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.studioirregular.bonniesbrunch.ad.BonniesAdvertisement;

public class BonniesBrunchActivity extends Activity/* implements LicenseCheckerCallback*/ {
    
	private static final String TAG = "activity";
	
	private GLSurfaceView surfaceView;
	private TextView fpsView;
	private BonniesAdvertisement ads;
	
	@SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Config.DEBUG_LOG) Log.d(TAG, "onCreate");
        
        // so user can change sound volume by hardware key.
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
        setContentView(R.layout.main);
        
        surfaceView = (GLSurfaceView)findViewById(R.id.surfaceView);
        if (Build.VERSION_CODES.HONEYCOMB <= Build.VERSION.SDK_INT) {
        	if (Config.DEBUG_LOG) Log.d(TAG, "Build.VERSION.SDK_INT:" + Build.VERSION.SDK_INT + ",setPreserveEGLContextOnPause.");
        	surfaceView.setPreserveEGLContextOnPause(true);
        }
        
        fpsView = (TextView)findViewById(R.id.fps);
        if (!Config.DEBUG_SHOW_FPS) {
        	fpsView.setVisibility(View.GONE);
        }
        
        Game game = Game.getInstance();
        game.bootstrap(this);
        surfaceView.setRenderer(game.getRenderer());
        
        ads = new BonniesAdvertisement(this, (RelativeLayout)findViewById(R.id.layout));
        game.setAdvertisement(ads);
        
        LicensingCheckManager.getInstance().onCreate(this);
    }
    
	@Override
	protected void onResume() {
		if (Config.DEBUG_LOG) Log.d(TAG, "onResume");
		super.onResume();
		
		surfaceView.onResume();
		Game.getInstance().onResume();
	}
	
	@Override
	protected void onPause() {
		if (Config.DEBUG_LOG) Log.d(TAG, "onPause");
		super.onPause();
		
		surfaceView.onPause();
		Game.getInstance().onPause();
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (Config.DEBUG_LOG) Log.d(TAG, "onWindowFocusChanged hasFocus:" + hasFocus);
		
		Game.getInstance().onAppFocusChanged(hasFocus);
	}
	
	@Override
	protected void onDestroy() {
		if (Config.DEBUG_LOG) Log.d(TAG, "onDestroy");
		
		LicensingCheckManager.getInstance().onDestroy();
		
		if (ads != null) {
			ads.onDestroy();
		}
		Game.getInstance().stop();
		
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		if (Game.getInstance().onBack() == false) {
			super.onBackPressed();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		InputSystem.getInstance().onTouchEvent(event);
		return true;
	}
	
	private static final int MENU_TOGGLE_TOUCH_AREA = 1;
	private static final int MENU_DISABLE_SCORE_LOCK = 2;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_TOGGLE_TOUCH_AREA, Menu.NONE, "Toggle Touch Area");
		menu.add(Menu.NONE, MENU_DISABLE_SCORE_LOCK, Menu.NONE, "Disable Score Lock");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int id = item.getItemId();
		
		if (MENU_TOGGLE_TOUCH_AREA == id) {
			ContextParameters.getInstance().debugDrawTouchArea = !ContextParameters.getInstance().debugDrawTouchArea;
			return true;
		} else if (MENU_DISABLE_SCORE_LOCK == id) {
			ContextParameters.getInstance().disableLevelScoreLock = true;
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void updateFPS(float fps) {
		this.fps = fps;
		runOnUiThread(fpsUpdated);
	}
	
	private float fps = 0;
	private Runnable fpsUpdated = new Runnable() {
		
		@Override
		public void run() {
			if (fpsView != null) {
				fpsView.setText(Float.toString(fps));
			}
		}
		
	};
	
	public void toastMessage(final int resId, final int durationType) {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(BonniesBrunchActivity.this, resId, durationType).show();
			}
			
		});
	}
	
	private static final int REQUEST_RATING_CODE = 0x05;	// hope its a 5-star rating!
	public void goToGooglePlayForRating() {
		Intent rating = new Intent(Intent.ACTION_VIEW);
		rating.setData(Uri.parse("market://details?id=" + getPackageName()));
		startActivityForResult(rating, REQUEST_RATING_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (REQUEST_RATING_CODE == requestCode) {
			RequestRating rating = new RequestRating();
			rating.doneRating(this);
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
	
}