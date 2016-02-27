package com.studioirregular.inappbilling;

import android.app.PendingIntent;

public class ServiceResponseHandler {

	private static BillingRequestObserver sObserver;
	
	public static void registerObserver(BillingRequestObserver observer) {
		sObserver = observer;
	}
	
	public static void unregisterObserver(BillingRequestObserver observer) {
		assert sObserver == observer;
		sObserver = null;
	}
	
	static void checkBillingSupportResult(boolean support) {
		if (sObserver != null) {
			sObserver.onCheckBillingSupportResult(support);
		}
	}
	
	static void onRequestPurchaseSyncResponse(PendingIntent pendingIntent) {
		if (sObserver != null) {
			sObserver.startPurchaseActivity(pendingIntent);
		}
	}
}
