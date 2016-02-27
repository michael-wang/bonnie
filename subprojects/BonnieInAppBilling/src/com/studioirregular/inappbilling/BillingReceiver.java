package com.studioirregular.inappbilling;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.studioirregular.inappbilling.Constants.AsyncResponse;
import com.studioirregular.inappbilling.Constants.BillingBroadcastReceiverRequest;
import com.studioirregular.inappbilling.Constants.ResponseCode;

public class BillingReceiver extends BroadcastReceiver {

	private static final String TAG = "billing-receiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.w(TAG, "onReceive intent:" + intent);
		
		final String action = intent.getAction();
		if (AsyncResponse.INTENT_ACTION_RESPONSE_CODE.equals(action)) {
			long requestId = intent.getLongExtra(AsyncResponse.EXTRA_REQUEST_ID, -1);
			int responseCodeIndex = intent.getIntExtra(AsyncResponse.EXTRA_RESPONSE_CODE, ResponseCode.RESULT_ERROR.ordinal());
			checkResponseCode(context, requestId, responseCodeIndex);
		} else if (AsyncResponse.INTENT_ACTION_IN_APP_NOTIFY.equals(action)) {
			String notificationId = intent.getStringExtra(AsyncResponse.EXTRA_NOTIFICATION_ID);
			inAppNotify(context, notificationId);
		} else if (AsyncResponse.INTENT_ACTION_PURCHASE_STATE_CHANGED.equals(action)) {
			String signedData = intent.getStringExtra(AsyncResponse.EXTRA_IN_APP_SIGNED_DATA);
			String signature = intent.getStringExtra(AsyncResponse.EXTRA_IN_APP_SIGNATURE);
			purchaseStateChanged(context, signedData, signature);
		}
	}
	
	private void checkResponseCode(Context context, long requestId, int responseCodeIndex) {
		Intent command = new Intent(BillingBroadcastReceiverRequest.ACTION_SERVER_RESPONSE_CODE);
		command.setClass(context, InAppBillingService.class);
		command.putExtra(BillingBroadcastReceiverRequest.EXTRA_REQUEST_ID, requestId);
		command.putExtra(BillingBroadcastReceiverRequest.EXTRA_RESPONSE_CODE_INDEX, responseCodeIndex);
		
		Log.d(TAG, "checkResponseCode requestId:" + requestId + ",responseCodeIndex:" + responseCodeIndex);
		context.startService(command);
	}
	
	private void inAppNotify(Context context, String notificationId) {
		Intent command = new Intent(BillingBroadcastReceiverRequest.ACTION_GET_PURCHASE_INFORMATION);
		command.setClass(context, InAppBillingService.class);
		command.putExtra(BillingBroadcastReceiverRequest.EXTRA_NOTIFICATION_ID, notificationId);
		
		Log.d(TAG, "inAppNotify notificationId:" + notificationId);
		context.startService(command);
	}
	
	private void purchaseStateChanged(Context context, String signedData, String signature) {
		Intent command = new Intent(BillingBroadcastReceiverRequest.ACTION_PURCHASE_STATE_CHANGED);
		command.setClass(context, InAppBillingService.class);
		command.putExtra(BillingBroadcastReceiverRequest.EXTRA_IN_APP_SIGNED_DATA, signedData);
		command.putExtra(BillingBroadcastReceiverRequest.EXTRA_IN_APP_SIGNATURE, signature);
		
		Log.d(TAG, "purchaseStateChanged signedData:" + signedData + ",signature:" + signature);
		context.startService(command);
	}

}
