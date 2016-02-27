package com.studioirregular.bonniesbrunch.billing;

import java.util.List;

import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.billing.BonnieSecurity.Purchase;
import com.studioirregular.bonniesbrunch.billing.Constants.PurchaseState;
import com.studioirregular.bonniesbrunch.billing.Constants.ResponseCode;

// Handle both sync and async response from Google Play.
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
	
	static void onRequestPurchaseAsyncResponse(String productId, boolean success) {
		if (sObserver != null) {
			sObserver.onRequestPurchaseResult(productId, success);
		}
	}
	
	static void purchaseStateChanged(final Context context, List<Purchase> purchases) {
		final int count = purchases.size();
		for (int i = 0; i < count; i++) {
			Purchase purchase = purchases.get(i);
			
			final String productID = purchase.productId;
			final boolean purchased = purchase.purchaseState.equals(PurchaseState.PURCHASED);
			
			if (Config.IN_APP_BILLING_PRODUCT_FULL_VERSION.equals(productID)) {
				storeFullVersionPurchaseState(context, productID, purchased);
			}
			
			if (sObserver != null) {
				sObserver.onPurchaseStateChanged(productID, purchased);
			}
		}
	}
	
	private static boolean storeFullVersionPurchaseState(Context context, String productId, boolean purchased) {
		SharedPreferences pref = context.getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		
		final String value = BonnieSecurity.enchantPurchaseInfo(context, productId, purchased);
		editor.putString(productId, value);
		
		return editor.commit();
	}
	
	static void restoreTransactionError(long requestId, ResponseCode code) {
		if (sObserver != null) {
			sObserver.onRestoreTransactionError(requestId, code);
		}
	}
}
