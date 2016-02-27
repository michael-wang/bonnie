package com.studioirregular.inappbilling;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.vending.billing.IMarketBillingService;
import com.studioirregular.inappbilling.Constants.ClientRequest;

public class BonnieInAppBillingActivity extends Activity {
    
	private static final String TAG = "bonnie-in-app-billing-activity";
	
	private static final String PRODUCT_ID_1 = "android.test.purchased";
	private static final String PRODUCT_ID_2 = "power_up";
	private static final String PRODUCT_ID_3 = "full_game_levels_test";
	
	IMarketBillingService billingService;
	Button buy_1, buy_2, buy_3, restore;
	TextView text;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "onCreate");
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        text = (TextView)findViewById(R.id.text);
        
        buy_1 = (Button)findViewById(R.id.buy_button_1);
        buy_1.setEnabled(false);
        buy_1.setOnClickListener(buyClickListener);
        
        buy_2 = (Button)findViewById(R.id.buy_button_2);
        buy_2.setEnabled(false);
        buy_2.setOnClickListener(buyClickListener);
        
        buy_3 = (Button)findViewById(R.id.buy_button_3);
        buy_3.setEnabled(false);
        buy_3.setOnClickListener(buyClickListener);
        
        restore = (Button)findViewById(R.id.buy_button_4);
        restore.setEnabled(false);
        restore.setOnClickListener(buyClickListener);
        
        ServiceResponseHandler.registerObserver(new MyPurchaseObserver(this, new Handler()));
        Intent checkBillingSupport = new Intent(ClientRequest.ACTION_CHECK_BILLING_SUPPORTED);
        checkBillingSupport.setClass(this, InAppBillingService.class);
        startService(checkBillingSupport);
    }
    
    private View.OnClickListener buyClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (v == restore) {
				Intent restoreTransaction = new Intent(ClientRequest.ACTION_RESTORE_TRANSACTIONS);
				restoreTransaction.setClass(BonnieInAppBillingActivity.this, InAppBillingService.class);
				startService(restoreTransaction);
			} else {
				Intent requestPurchase = new Intent(ClientRequest.ACTION_REQUEST_PURCHASE);
				requestPurchase.setClass(BonnieInAppBillingActivity.this, InAppBillingService.class);
				if (v == buy_1) {
					requestPurchase.putExtra(ClientRequest.EXTRA_PRODUCT_ID, PRODUCT_ID_1);
				} else if (v == buy_2) {
					requestPurchase.putExtra(ClientRequest.EXTRA_PRODUCT_ID, PRODUCT_ID_2);
				} else if (v == buy_3) {
					requestPurchase.putExtra(ClientRequest.EXTRA_PRODUCT_ID, PRODUCT_ID_3);
				}
				startService(requestPurchase);
			}
		}
	};
    
    private class MyPurchaseObserver extends BillingRequestObserver {

		public MyPurchaseObserver(Activity hostActivity, Handler uiThreadHandler) {
			super(hostActivity, uiThreadHandler);
		}
		
		@Override
		public void onCheckBillingSupportResult(boolean support) {
			Log.d(TAG, "MyPurchaseObserver::onCheckBillingSupportResult support:" + support);
			buy_1.setEnabled(support);
			buy_2.setEnabled(support);
			buy_3.setEnabled(support);
			restore.setEnabled(true);
		}

		@Override
		public void onRequestPurchaseResult(String productId, int quantity,
				long purchaseTime) {
			Log.d(TAG, "MyPurchaseObserver::onRequestPurchaseResult productId:"
					+ productId + ",quantity:" + quantity + ",purchaseTime:"
					+ purchaseTime);
		}
		
    }
	
}