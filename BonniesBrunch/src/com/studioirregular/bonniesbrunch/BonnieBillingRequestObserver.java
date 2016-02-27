package com.studioirregular.bonniesbrunch;

import android.app.Activity;
import android.util.Log;

import com.studioirregular.bonniesbrunch.billing.BillingRequestObserver;
import com.studioirregular.bonniesbrunch.billing.Constants.ResponseCode;

public class BonnieBillingRequestObserver extends BillingRequestObserver {

	public BonnieBillingRequestObserver(Activity hostActivity, Game game) {
		super(hostActivity);
		this.game = game;
	}
	
	@Override
	public void onCheckBillingSupportResult(boolean supported) {
		if (Config.DEBUG_LOG) Log.w(TAG, "BonnieBillingRequestObserver::onCheckBillingSupportResult support:" + supported);
		game.notifyBillingSupport(supported);
	}
	
	@Override
	public void onRequestPurchaseResult(String productId, boolean success) {
		if (Config.DEBUG_LOG) Log.w(TAG, "onRequestPurchaseResult productId:" + productId + ", success:" + success);
		game.notifyRequestPurchaseResult(productId, success);
	}
	
	@Override
	public void onPurchaseStateChanged(String productId, boolean purchased) {
		if (Config.DEBUG_LOG) Log.w(TAG, "onPurchaseStateChanged productId:" + productId + ", purchased:" + purchased);
		game.notifyPurchasedStateChanged(productId);
	}
	
	@Override
	public void onRestoreTransactionError(long requestId, ResponseCode code) {
		super.onRestoreTransactionError(requestId, code);
		
		game.notifyRestoreTransactionFailed();
	}
	
	private Game game;

}
