package com.studioirregular.bonniesbrunch.billing;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.billing.Constants.ResponseCode;

public abstract class BillingRequestObserver {

	protected static final String TAG = "billing-request-observer";
	
	public BillingRequestObserver(Activity hostActivity) {
		this.hostActivity = hostActivity;
	}
	
	public abstract void onCheckBillingSupportResult(boolean support);
	public abstract void onRequestPurchaseResult(String productId, boolean success);
	public abstract void onPurchaseStateChanged(String productId, boolean purchased);
	
	public void startPurchaseActivity(PendingIntent pendingIntent) {
		try {
			hostActivity.startIntentSender(pendingIntent.getIntentSender(), new Intent(), 0, 0, 0);
		} catch (SendIntentException e) {
			if (Config.DEBUG_LOG) Log.e(TAG, "startPurchaseActivity exception:" + e);
		}
	}
	
	public void onRestoreTransactionError(long requestId, ResponseCode code) {
		if (Config.DEBUG_LOG) Log.w(TAG, "onRestoreTransactionError requestId:" + requestId + ",code:" + code);
	}
	
	protected Activity hostActivity;
}
