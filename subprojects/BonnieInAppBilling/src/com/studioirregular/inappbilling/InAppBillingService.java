package com.studioirregular.inappbilling;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.android.vending.billing.IMarketBillingService;
import com.studioirregular.inappbilling.Constants.BillingBroadcastReceiverRequest;
import com.studioirregular.inappbilling.Constants.BillingRequestMethod;
import com.studioirregular.inappbilling.Constants.ClientRequest;
import com.studioirregular.inappbilling.Constants.RequestBundle;
import com.studioirregular.inappbilling.Constants.ResponseCode;
import com.studioirregular.inappbilling.Constants.SyncResponse;
import com.studioirregular.inappbilling.Security.Purchase;

public class InAppBillingService extends Service implements ServiceConnection {

	private static final String TAG = "in-app-billing-service";
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.w(TAG, "onStartCommand intent:" + intent + ", flags:" + flags + ", startId:" + startId);
		
		Command request = null;
		
		final String action = intent.getAction();
		if (ClientRequest.ACTION_CHECK_BILLING_SUPPORTED.equals(action)) {
			request = new CheckBillingSupportCommand(intent, startId);
		} else if (ClientRequest.ACTION_REQUEST_PURCHASE.equals(action)) {
			request = new RequestPurchaseCommand(intent, startId);
		} else if (ClientRequest.ACTION_RESTORE_TRANSACTIONS.equals(action)) {
			request = new RestoreTransactionCommand(intent, startId);
		} else if (BillingBroadcastReceiverRequest.ACTION_SERVER_RESPONSE_CODE.equals(action)) {
			final long requestId = intent.getLongExtra(BillingBroadcastReceiverRequest.EXTRA_REQUEST_ID, 0);
			final int responseCodeIndex = intent.getIntExtra(BillingBroadcastReceiverRequest.EXTRA_RESPONSE_CODE_INDEX, -1);
			final ResponseCode responseCode = ResponseCode.valueOf(responseCodeIndex);
			checkResponseCode(requestId, responseCode);
		} else if (BillingBroadcastReceiverRequest.ACTION_GET_PURCHASE_INFORMATION.equals(action)) {
			final String notificationId = intent.getStringExtra(BillingBroadcastReceiverRequest.EXTRA_NOTIFICATION_ID);
			request = new GetPurchaseInformationRequest(intent, startId, notificationId);
		} else if (BillingBroadcastReceiverRequest.ACTION_PURCHASE_STATE_CHANGED.equals(action)) {
			final String signedData = intent.getStringExtra(BillingBroadcastReceiverRequest.EXTRA_IN_APP_SIGNED_DATA);
			final String signature = intent.getStringExtra(BillingBroadcastReceiverRequest.EXTRA_IN_APP_SIGNATURE);
			request = purchaseStateChanged(intent, startId, signedData, signature);
		}
		
		if (request != null) {
			pendingCommands.add(request);
		}
		
		if (marketBillingService == null) {
			if (bindToMarketBillingService() == false) {
				Log.e(TAG, "cannot find to market billing service, exception:" + bindToMarketBillingServiceException);
			}
		}
		
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		Log.w(TAG, "onDestroy");
		
		if (marketBillingService != null) {
			this.unbindService(this);
		}
		
		super.onDestroy();
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		Log.d(TAG, "onServiceConnected name:" + name);
		
		marketBillingService = IMarketBillingService.Stub.asInterface(service);
		runPendingRequests();
	}
	
	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.d(TAG, "onServiceDisconnected name:" + name);
		
		marketBillingService = null;
	}
	
	private SecurityException bindToMarketBillingServiceException = null;
    private boolean bindToMarketBillingService() {
    	Log.d(TAG, "bindToMarketBillingService marketBillingService:" + marketBillingService);
    	
    	bindToMarketBillingServiceException = null;
        try {
            boolean bindResult = bindService(
                    new Intent(Constants.SERVICE_ACTION),
                    this,  // ServiceConnection.
                    Context.BIND_AUTO_CREATE);

            if (bindResult) {
                return true;
            } else {
                Log.e(TAG, "Could not bind to service.");
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception: " + e);
            bindToMarketBillingServiceException = e;
        }
        return false;
    }
    
	private void runPendingRequests() {
		Log.d(TAG, "runPendingRequest #pendingRequests:" + pendingCommands.size());
		
		Command command = null;
		while ((command = pendingCommands.peek()) != null) {
			try {
				command.execute();
			} catch (RemoteException e) {
				Log.e(TAG, "command execution failed for startIntent:" + command.startIntent + ", exception:" + e);
				command.onRemoveException(e);
			}
			pendingCommands.remove();
		}
		
		stopSelf();
	}
	
	private void checkResponseCode(long requestId, ResponseCode responseCode) {
		Log.d(TAG, "checkResponseCode requestId:" + Long.toHexString(requestId) + ",responseCode:" + responseCode);
		
		Command command = requestSentCommands.get(requestId);
		if (command != null) {
			command.responseCodeReceived(responseCode);
		}
		
		requestSentCommands.remove(requestId);
	}
	
	private Command purchaseStateChanged(Intent intent, int startId, String signedData, String signature) {
		Log.d(TAG, "purchaseStateChanged signedData:" + signedData + ",signature:" + signature);
		
		List<Purchase> purchases = Security.parsePurchases(signedData, signature);
		if (purchases == null) {
			Log.d(TAG, "purchaseStateChanged Security.parsePurchases returns null");
			return null;
		}
		
		String[] notificationIds = new String[purchases.size()];
		for (int i = 0; i < purchases.size(); i++) {
			Purchase purchase = purchases.get(i);
			Log.d(TAG, "i:" + i + ", purchase:" + purchase);
			notificationIds[i] = purchase.notificationId;
		}
		return new ConfirmNotificationCommand(intent, startId, notificationIds);
	}
	
	abstract class Command {
		public Intent startIntent;
		public int startId;
		
		public Command(Intent intent, int id) {
			startIntent = intent;
			startId = id;
		}
		
		// return execution result. 
		public abstract boolean execute() throws RemoteException;
		protected abstract void responseCodeReceived(ResponseCode responseCode);
		
		public void onRemoveException(RemoteException e) {
			Log.e(TAG, getClass().getSimpleName() + ": remote exception:" + e);
		}
	}
	
	class CheckBillingSupportCommand extends Command {

		public CheckBillingSupportCommand(Intent intent, int id) {
			super(intent, id);
		}
		
		// return execution result, not billing supported or not, which notified by ServiceResultObservable.
		@Override
		public boolean execute()  throws RemoteException {
			assert marketBillingService != null;
			
	        Bundle request = new Bundle();
	        request.putString(RequestBundle.KEY_BILLING_REQUEST, BillingRequestMethod.CHECK_BILLING_SUPPORTED);
	        request.putInt(RequestBundle.KEY_API_VERSION, 1);
	        request.putString(RequestBundle.KEY_PACKAGE_NAME, getPackageName());
	        
	        Bundle response = marketBillingService.sendBillingRequest(request);
	        
	        final int responseCode = response.getInt(SyncResponse.KEY_RESPONSE_CODE);
	        Log.d(TAG, "checkIfBillingSupported responseCode:" + responseCode);
	        
	        final boolean billingSupported = (responseCode == ResponseCode.RESULT_OK.ordinal());
	        ServiceResponseHandler.checkBillingSupportResult(billingSupported);
	        
	        Log.d(TAG, getClass().getSimpleName() + ":execute responseCode:" + ResponseCode.valueOf(responseCode));
			return true;
		}
		
		@Override
		protected void responseCodeReceived(ResponseCode responseCode) {
		}
		
	}
	
	class RequestPurchaseCommand extends Command {

		private String productId;
		
		public RequestPurchaseCommand(Intent intent, int id) {
			super(intent, id);
			
			productId = intent.getStringExtra(ClientRequest.EXTRA_PRODUCT_ID);
		}
		
		@Override
		public boolean execute() throws RemoteException {
			assert marketBillingService != null;
			
	        Bundle request = new Bundle();
	        request.putString(RequestBundle.KEY_BILLING_REQUEST, BillingRequestMethod.REQUEST_PURCHASE);
	        request.putInt(RequestBundle.KEY_API_VERSION, 1);
	        request.putString(RequestBundle.KEY_PACKAGE_NAME, getPackageName());
	        request.putString(RequestBundle.KEY_ITEM_ID, productId);
	        
	        Bundle response = marketBillingService.sendBillingRequest(request);
	        
	        final int responseCode = response.getInt(SyncResponse.KEY_RESPONSE_CODE);
	        final long requestId = response.getLong(SyncResponse.KEY_REQUEST_ID);
	        PendingIntent pendingIntent = response.getParcelable(SyncResponse.KEY_PURCHASE_INTENT);
	        
	        ServiceResponseHandler.onRequestPurchaseSyncResponse(pendingIntent);
	        
	        Log.d(TAG, getClass().getSimpleName() + ":execute requestId:" + Long.toHexString(requestId) + ",responseCode:" + ResponseCode.valueOf(responseCode));
	        if (ResponseCode.RESULT_OK.ordinal() == responseCode) {
	        	requestSentCommands.put(requestId, this);
	        	return true;
	        } else {
	        	return false;
	        }
		}
		
		@Override
		protected void responseCodeReceived(ResponseCode responseCode) {
			if (ResponseCode.RESULT_OK != responseCode) {
				Log.e(TAG, getClass().getSimpleName() + ":responseCodeReceived responseCode:" + responseCode);
			} else {
				Log.d(TAG, getClass().getSimpleName() + ":responseCodeReceived responseCode:" + responseCode);
			}
		}
		
	}
	
	class GetPurchaseInformationRequest extends Command {
		private String notificationId;
		private long nonce;
		
		public GetPurchaseInformationRequest(Intent intent, int id, String notificationId) {
			super(intent, id);
			this.notificationId = notificationId;
		}
		
		@Override
		public boolean execute() throws RemoteException {
			assert marketBillingService != null;
			
			nonce = Security.generateNonce();
			
	        Bundle request = new Bundle();
	        request.putString(RequestBundle.KEY_BILLING_REQUEST, BillingRequestMethod.GET_PURCHASE_INFORMATION);
	        request.putInt(RequestBundle.KEY_API_VERSION, 1);
	        request.putString(RequestBundle.KEY_PACKAGE_NAME, getPackageName());
	        request.putLong(RequestBundle.KEY_NONCE, nonce);
	        request.putStringArray(RequestBundle.KEY_NOTIFY_IDS, new String[] {notificationId});
	        
	        Bundle response = marketBillingService.sendBillingRequest(request);
	        
	        final int responseCode = response.getInt(SyncResponse.KEY_RESPONSE_CODE);
	        final long requestId = response.getLong(SyncResponse.KEY_REQUEST_ID);
	        
	        Log.d(TAG, getClass().getSimpleName() + ":execute requestId:" + Long.toHexString(requestId) + ",responseCode:" + ResponseCode.valueOf(responseCode));
	        if (ResponseCode.RESULT_OK.ordinal() == responseCode) {
	        	requestSentCommands.put(requestId, this);
	        	return true;
	        } else {
	        	return false;
	        }
		}
		
		@Override
		protected void responseCodeReceived(ResponseCode responseCode) {
			if (ResponseCode.RESULT_OK != responseCode) {
				Log.e(TAG, getClass().getSimpleName() + ":responseCodeReceived responseCode:" + responseCode);
			} else {
				Log.d(TAG, getClass().getSimpleName() + ":responseCodeReceived responseCode:" + responseCode);
			}
		}
		
		@Override
		public void onRemoveException(RemoteException e) {
			super.onRemoveException(e);
			Security.removeNonce(nonce);
		}
		
	}
	
	class ConfirmNotificationCommand extends Command {
		private String[] notificationIds;
		
		public ConfirmNotificationCommand(Intent intent, int id, String[] notificationIds) {
			super(intent, id);
			this.notificationIds = notificationIds;
		}
		
		@Override
		public boolean execute() throws RemoteException {
			assert marketBillingService != null;
			
			Log.d(TAG, getClass().getSimpleName() + ":execute notificationIds:" + notificationIds);
			
	        Bundle request = new Bundle();
	        request.putString(RequestBundle.KEY_BILLING_REQUEST, BillingRequestMethod.CONFIRM_NOTIFICATIONS);
	        request.putInt(RequestBundle.KEY_API_VERSION, 1);
	        request.putString(RequestBundle.KEY_PACKAGE_NAME, getPackageName());
	        request.putStringArray(RequestBundle.KEY_NOTIFY_IDS, notificationIds);
	        
	        Bundle response = marketBillingService.sendBillingRequest(request);
	        
	        final int responseCode = response.getInt(SyncResponse.KEY_RESPONSE_CODE);
	        final long requestId = response.getLong(SyncResponse.KEY_REQUEST_ID);
	        
	        Log.d(TAG, getClass().getSimpleName() + ":execute responseCode:" + ResponseCode.valueOf(responseCode) + ",requestId:" + requestId);
	        if (ResponseCode.RESULT_OK.ordinal() == responseCode) {
	        	requestSentCommands.put(requestId, this);
	        	return true;
	        } else {
	        	return false;
	        }
		}
		
		@Override
		protected void responseCodeReceived(ResponseCode responseCode) {
			if (ResponseCode.RESULT_OK != responseCode) {
				Log.e(TAG, getClass().getSimpleName() + ":responseCodeReceived responseCode:" + responseCode);
			} else {
				Log.d(TAG, getClass().getSimpleName() + ":responseCodeReceived responseCode:" + responseCode);
			}
		}
		
	}
	
	class RestoreTransactionCommand extends Command {

		private long nonce;
		
		public RestoreTransactionCommand(Intent intent, int id) {
			super(intent, id);
		}
		
		@Override
		public boolean execute() throws RemoteException {
			assert marketBillingService != null;
			
			nonce = Security.generateNonce();
			
	        Bundle request = new Bundle();
	        request.putString(RequestBundle.KEY_BILLING_REQUEST, BillingRequestMethod.RESTORE_TRANSACTIONS);
	        request.putInt(RequestBundle.KEY_API_VERSION, 1);
	        request.putString(RequestBundle.KEY_PACKAGE_NAME, getPackageName());
	        request.putLong(RequestBundle.KEY_NONCE, nonce);
	        
	        Bundle response = marketBillingService.sendBillingRequest(request);
	        
	        final int responseCode = response.getInt(SyncResponse.KEY_RESPONSE_CODE);
	        final long requestId = response.getLong(SyncResponse.KEY_REQUEST_ID);
	        
	        Log.d(TAG, getClass().getSimpleName() + ":execute requestId:" + Long.toHexString(requestId) + ",responseCode:" + ResponseCode.valueOf(responseCode));
	        if (ResponseCode.RESULT_OK.ordinal() == responseCode) {
	        	requestSentCommands.put(requestId, this);
	        	return true;
	        } else {
	        	return false;
	        }
		}
		
		@Override
		protected void responseCodeReceived(ResponseCode responseCode) {
			if (ResponseCode.RESULT_OK != responseCode) {
				Log.e(TAG, getClass().getSimpleName() + ":responseCodeReceived responseCode:" + responseCode);
			} else {
				Log.d(TAG, getClass().getSimpleName() + ":responseCodeReceived responseCode:" + responseCode);
			}
		}
		
		@Override
		public void onRemoveException(RemoteException e) {
			super.onRemoveException(e);
			Security.removeNonce(nonce);
		}
		
	}
	
	private IMarketBillingService marketBillingService;
	private static LinkedList<Command> pendingCommands = new LinkedList<Command>();
	// request id to command mapping.
	private static Map<Long, Command> requestSentCommands = new HashMap<Long, Command>();

}
