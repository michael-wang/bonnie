package com.studioirregular.bonniesbrunch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.Obfuscator;
import com.google.android.vending.licensing.Policy;
import com.google.android.vending.licensing.ServerManagedPolicy;
import com.studioirregular.bonniesbrunch.Game.StoredPurchaseState;

public class LicensingCheckManager implements LicenseCheckerCallback {

	private static final String TAG = "license-checking";
	
    // singleton
    private static class InstanceHolder {
        public static final LicensingCheckManager instance = new LicensingCheckManager();
    }
    public static LicensingCheckManager getInstance() {
        return InstanceHolder.instance;
    }
    
    public void onCreate(Context context) {
    	if (Config.DEBUG_LOG) Log.w(TAG, "onCreate");
    	
		Obfuscator obfuscator = new AESObfuscator(SALT,
						context.getPackageName(), 
						Settings.Secure.getString(context.getContentResolver(),
						Settings.Secure.ANDROID_ID));
		Policy policy = new ServerManagedPolicy(context, obfuscator);
		licenseChecker = new LicenseChecker(context, policy,
				Config.GOOGLE_PLAY_PUBLIC_KEY);
    }
    
    public void onDestroy() {
    	if (Config.DEBUG_LOG) Log.w(TAG, "onDestroy");
    	
		if (licenseChecker != null) {
			licenseChecker.onDestroy();
		}
		hostActivity = null;
    }
    
	public static class MyPolicy {
		public static void checkWhenNecessary(Activity activity) {
			if (Config.DEBUG_LOG) Log.w(TAG, "MyPolicy::checkWhenNecessary");
			
			if (Game.getInstance().getFullVersionPurchaseState()
					.equals(StoredPurchaseState.PURCHASED) == false) {
				
				boolean checkThisTime = (Math.random()*100) >= Config.GOOGLE_LICENSING_CHECK_RATE;
				if (Config.DEBUG_LOG) Log.w(TAG, "MyPolicy::checkWhenNecessary doCheck:" + checkThisTime);
				if (checkThisTime) {
					LicensingCheckManager.getInstance().check(activity);
				}
			}
		}
	}
    
    public void check(Activity activity) {
    	if (hostActivity != null) {
    		if (Config.DEBUG_LOG) Log.w(TAG, "check canceled, previous check not finished.");
    		return;
    	}
    	
    	if (Config.DEBUG_LOG) Log.w(TAG, "check!");
    	
        if (licenseChecker != null) {
        	licenseChecker.checkAccess(this);
        }
        hostActivity = activity;
        retryCount = 0;
    }
    
	@Override
	public void allow(int reason) {
		if (Config.DEBUG_LOG) Log.w(TAG, "allow, reason:" + reason);
		hostActivity = null;
	}

	@Override
	public void dontAllow(int reason) {
		if (Config.DEBUG_LOG) Log.w(TAG, "dontAllow, reason:" + reason);
		
		if (hostActivity == null || hostActivity.isFinishing()) {
			return;
		}
		
		if (Policy.RETRY == reason) {
			retryCheck();
		} else {
			onCheckingFailed();
		}
	}

	@Override
	public void applicationError(int errorCode) {
		if (Config.DEBUG_LOG) Log.w(TAG, "applicationError, errorCode:" + errorCode);
		hostActivity = null;
	}
	
	private void retryCheck() {
		if (Config.DEBUG_LOG) Log.w(TAG, "retry licensing check!");
		retryCount++;
		if (licenseChecker != null && retryCount < MAX_RETRY) {
			licenseChecker.checkAccess(this);
		} else {
			onCheckingFailed();
		}
	}
	
	private void onCheckingFailed() {
		if (Config.DEBUG_LOG) Log.w(TAG, "google licensing check failed..");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(hostActivity);
		builder.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.license_check_failed_title)
			.setMessage(R.string.license_check_failed_message)
			.setCancelable(false)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
							"https://play.google.com/store/apps/details?id=" + hostActivity.getPackageName()));
					hostActivity.startActivity(marketIntent);
				}
			})
			.setNegativeButton(R.string.license_check_later, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		builder.show();
		
		hostActivity = null;
	}
	
	private static final byte[] SALT = new byte[] { 101, 3, 117, -63, 62, 72, -113,
		39, 81, 32, 125, -99, 62, -122, 3, 77, 81, -11, 67, 127 };
	private static final int MAX_RETRY = 3;
	
	private LicenseChecker licenseChecker;
	private Activity hostActivity;	// tricky: if not null means a check is going on, so set null on every exist of check.
	private int retryCount;

}
