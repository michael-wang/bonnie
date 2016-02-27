package com.studioirregular.inappbilling;



public class Constants {
	
	// initiated by client app.
	public static class ClientRequest {
    	public static final String ACTION_CHECK_BILLING_SUPPORTED	= "com.studioirregular.inappbilling.CHECK_BILLING_SUPPORTED";
    	
    	public static final String ACTION_REQUEST_PURCHASE			= "com.studioirregular.inappbilling.REQUEST_PURCHASE";
    	public static final String EXTRA_PRODUCT_ID					= "product_id";
    	
    	public static final String ACTION_RESTORE_TRANSACTIONS		= "com.studioirregular.inappbilling.RESTORE_TRANSACTIONS";
	}
	
	// initiated by billing broadcast receiver.
    public static class BillingBroadcastReceiverRequest {
    	public static final String ACTION_SERVER_RESPONSE_CODE		= "com.studioirregular.inappbilling.RESPONSE_CODE";
    	public static final String EXTRA_REQUEST_ID					= "request_id";
    	public static final String EXTRA_RESPONSE_CODE_INDEX		= "response_code_index";
    	
    	public static final String ACTION_GET_PURCHASE_INFORMATION	= "com.studioirregular.inappbilling.GET_PURCHASE_INFORMATION";
    	public static final String EXTRA_NOTIFICATION_ID			= "notification_id";
    	
    	public static final String ACTION_PURCHASE_STATE_CHANGED	= "com.studioirregular.inappbilling.ACTION_PURCHASE_STATE_CHANGED";
    	public static final String EXTRA_IN_APP_SIGNED_DATA			= "purchase_state_signed_data";
    	public static final String EXTRA_IN_APP_SIGNATURE			= "purchase_state_signature";
    }
    
    public static final String SERVICE_ACTION = "com.android.vending.billing.MarketBillingService.BIND";
    
    public static class RequestBundle {
        public static final String KEY_BILLING_REQUEST		= "BILLING_REQUEST";
        public static final String KEY_API_VERSION			= "API_VERSION";
        public static final String KEY_PACKAGE_NAME			= "PACKAGE_NAME";
        public static final String KEY_ITEM_ID				= "ITEM_ID";
        public static final String KEY_NONCE				= "NONCE";
        public static final String KEY_NOTIFY_IDS			= "NOTIFY_IDS";
        public static final String KEY_DEVELOPER_PAYLOAD	= "DEVELOPER_PAYLOAD";
    }
    
    public static class BillingRequestMethod {
        public static final String CHECK_BILLING_SUPPORTED	= "CHECK_BILLING_SUPPORTED";
        public static final String REQUEST_PURCHASE			= "REQUEST_PURCHASE";
        public static final String GET_PURCHASE_INFORMATION	= "GET_PURCHASE_INFORMATION";
        public static final String CONFIRM_NOTIFICATIONS	= "CONFIRM_NOTIFICATIONS";
        public static final String RESTORE_TRANSACTIONS		= "RESTORE_TRANSACTIONS";
    }
    
    public static class SyncResponse {
        public static final String KEY_RESPONSE_CODE	= "RESPONSE_CODE";
        public static final String KEY_PURCHASE_INTENT	= "PURCHASE_INTENT";
        public static final String KEY_REQUEST_ID		= "REQUEST_ID";
    }
    
    public static class AsyncResponse {
    	public static final String INTENT_ACTION_RESPONSE_CODE			= "com.android.vending.billing.RESPONSE_CODE";
    	public static final String EXTRA_REQUEST_ID						= "request_id";
    	public static final String EXTRA_RESPONSE_CODE					= "response_code";
    	
    	public static final String INTENT_ACTION_IN_APP_NOTIFY			= "com.android.vending.billing.IN_APP_NOTIFY";
    	public static final String EXTRA_NOTIFICATION_ID				= "notification_id";
    	
    	public static final String INTENT_ACTION_PURCHASE_STATE_CHANGED	= "com.android.vending.billing.PURCHASE_STATE_CHANGED";
    	public static final String EXTRA_IN_APP_SIGNED_DATA				= "inapp_signed_data";
    	public static final String EXTRA_IN_APP_SIGNATURE				= "inapp_signature";
    }
    
    public enum ResponseCode {
        RESULT_OK,
        RESULT_USER_CANCELED,
        RESULT_SERVICE_UNAVAILABLE,
        RESULT_BILLING_UNAVAILABLE,
        RESULT_ITEM_UNAVAILABLE,
        RESULT_DEVELOPER_ERROR,
        RESULT_ERROR;
        
        // Converts from an ordinal value to the ResponseCode
        public static ResponseCode valueOf(int index) {
            ResponseCode[] values = ResponseCode.values();
            if (index < 0 || index >= values.length) {
                return RESULT_ERROR;
            }
            return values[index];
        }
    }
    
    public enum PurchaseState {
        // Responses to requestPurchase or restoreTransactions.
        PURCHASED,   // User was charged for the order.
        CANCELED,    // The charge failed on the server.
        REFUNDED;    // User received a refund for the order.

        // Converts from an ordinal value to the PurchaseState
        public static PurchaseState valueOf(int index) {
            PurchaseState[] values = PurchaseState.values();
            if (index < 0 || index >= values.length) {
                return CANCELED;
            }
            return values[index];
        }
    }
    
}
