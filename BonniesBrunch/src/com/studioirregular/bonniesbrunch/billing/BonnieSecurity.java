package com.studioirregular.bonniesbrunch.billing;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.billing.Constants.PurchaseState;

public class BonnieSecurity {

	private static final String TAG = "security";
	
    private static final String KEY_FACTORY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
	private static final SecureRandom RANDOM = new SecureRandom();
	private static HashSet<Long> sKnownNonces = new HashSet<Long>();
	
	public static class Purchase {
		public String notificationId;
		public String orderId;
		public String productId;
		public long purchaseTime;
		public PurchaseState purchaseState;
		
		public Purchase(String notificationId, String orderId, String productId, long purchaseTime, PurchaseState purchaseState) {
			this.notificationId = notificationId;
			this.orderId = orderId;
			this.productId = productId;
			this.purchaseTime = purchaseTime;
			this.purchaseState = purchaseState;
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + ":\nnotificationId:"
					+ notificationId + "\norderId:" + orderId + "\nproductId:"
					+ productId + "\npurchaseTime:" + purchaseTime
					+ "\npurchaseState:" + purchaseState;
		}
		
	}
	
    public static long generateNonce() {
        long nonce = RANDOM.nextLong();
        sKnownNonces.add(nonce);
        return nonce;
    }

    public static void removeNonce(long nonce) {
        sKnownNonces.remove(nonce);
    }

    public static boolean isNonceKnown(long nonce) {
        return sKnownNonces.contains(nonce);
    }
	
	public static List<Purchase> parsePurchases(String signedData, String signature) {
		if (Config.DEBUG_LOG) Log.d(TAG, "parsePurchases");
		
		if (signedData == null) {
			if (Config.DEBUG_LOG) Log.e(TAG, "parsePurchases no signedData... cannot proceed.");
			return null;
		}
		
		boolean verified = false;
		if (signature != null && signature.length() > 0) {
			final String base64EncodedPublicKey = Config.GOOGLE_PLAY_PUBLIC_KEY;
			PublicKey key = BonnieSecurity.generatePublicKey(base64EncodedPublicKey);
			verified = BonnieSecurity.verify(key, signedData, signature);
			if (!verified) {
				if (Config.DEBUG_LOG) Log.e(TAG, "signature does not match data.");
				return null;
			}
		}
		
		List<Purchase> purchases = new ArrayList<Purchase>();
		
		long nonce = 0;
		JSONObject jObject;
		JSONArray jOrders;
		try {
			jObject = new JSONObject(signedData);
			nonce = jObject.optLong("nonce");
			jOrders = jObject.getJSONArray("orders");
		} catch (JSONException e) {
			Log.e(TAG, "Error creating JSONObject:" + e);
			return null;
		}
		
		if (BonnieSecurity.isNonceKnown(nonce) == false) {
			if (Config.DEBUG_LOG) Log.e(TAG, "Nonce not known:" + nonce);
			return null;
		}
		
		final int count = jOrders != null ? jOrders.length() : 0;
		if (Config.DEBUG_LOG) Log.d(TAG, "parsePurchases #jOrders:" + count);
		try {
			for (int i = 0; i < count; i++) {
				JSONObject jOrder = jOrders.getJSONObject(i);
				if (Config.DEBUG_LOG) Log.d(TAG, "i:" + i + ",jOrder:" + jOrder);
				
				final String notificationId = jOrder.optString("notificationId", "");
				final String orderId = jOrder.getString("orderId");
				final String productId = jOrder.getString("productId");
				final long purchaseTime = jOrder.getLong("purchaseTime");
				final PurchaseState purchaseState = PurchaseState.valueOf(jOrder.getInt("purchaseState"));
				
				Purchase purchase = new Purchase(notificationId, orderId, productId, purchaseTime, purchaseState);
				purchases.add(purchase);
			}
		} catch (JSONException e) {
			if (Config.DEBUG_LOG) Log.e(TAG, "Error parse jOrders:" + e);
			return null;
		}
		
		removeNonce(nonce);
		return purchases;
	}
	
    public static PublicKey generatePublicKey(String encodedPublicKey) {
    	if (Config.DEBUG_LOG) Log.d(TAG, "generatePublicKey encodedPublicKey: " + encodedPublicKey);
        try {
            byte[] decodedKey = Base64.decode(encodedPublicKey, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
            return keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
        	if (Config.DEBUG_LOG) Log.e(TAG, "Invalid key specification.");
            throw new IllegalArgumentException(e);
        }
    }
    
    public static boolean verify(PublicKey publicKey, String signedData, String signature) {
    	if (Config.DEBUG_LOG) Log.d(TAG, "verify signature: " + signature);
    	
        Signature sig;
        try {
            sig = Signature.getInstance(SIGNATURE_ALGORITHM);
            sig.initVerify(publicKey);
            sig.update(signedData.getBytes());
            if (!sig.verify(Base64.decode(signature, Base64.DEFAULT))) {
            	if (Config.DEBUG_LOG) Log.e(TAG, "Signature verification failed.");
                return false;
            }
            if (Config.DEBUG_LOG) Log.d(TAG, "signature pass verification!");
            return true;
        } catch (NoSuchAlgorithmException e) {
        	if (Config.DEBUG_LOG) Log.e(TAG, "NoSuchAlgorithmException.");
        } catch (InvalidKeyException e) {
        	if (Config.DEBUG_LOG) Log.e(TAG, "Invalid key specification.");
        } catch (SignatureException e) {
        	if (Config.DEBUG_LOG) Log.e(TAG, "Signature exception.");
        }
        return false;
    }
    
    public static String enchantPurchaseInfo(Context context, String productId, boolean purchased) {
    	if (Config.DEBUG_LOG) Log.w(TAG, "enchantPurchaseInfo productId:" + productId + ",purchased:" + purchased);
    	
    	String input = productId + 
    			(purchased ? " purchased for " : "not purchased for ") + 
    			Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    	if (Config.DEBUG_LOG) Log.w(TAG, "enchantPurchaseInfo before transform:" + input);
    	
    	MessageDigest md = null;
    	try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			if (Config.DEBUG_LOG) Log.e(TAG, "exception:" + e);
			return "";
		}
    	
    	md.update(input.getBytes());
    	
    	byte[] raw = md.digest();
    	return Base64.encodeToString(raw, Base64.DEFAULT);
    }
}
