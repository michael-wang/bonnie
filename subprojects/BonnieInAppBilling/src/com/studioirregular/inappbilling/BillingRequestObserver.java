package com.studioirregular.inappbilling;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Handler;
import android.util.Log;

public abstract class BillingRequestObserver {

	private static final String TAG = "billing-request-observer";
	
	public BillingRequestObserver(Activity hostActivity, Handler uiThreadHandler) {
		this.hostActivity = hostActivity;
		this.uiThreadHandler = uiThreadHandler;
	}
	
	public abstract void onCheckBillingSupportResult(boolean support);
	public abstract void onRequestPurchaseResult(String productId, int quantity, long purchaseTime);
	
	public void startPurchaseActivity(PendingIntent pendingIntent) {
		try {
			hostActivity.startIntentSender(pendingIntent.getIntentSender(), new Intent(), 0, 0, 0);
		} catch (SendIntentException e) {
			Log.e(TAG, "startPurchaseActivity exception:" + e);
		}
	}
	
	private Activity hostActivity;
	private Handler uiThreadHandler;
}
