package com.studioirregular.inappbilling;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
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

import android.util.Base64;
import android.util.Log;

import com.studioirregular.inappbilling.Constants.PurchaseState;

public class Security {

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
		Log.d(TAG, "parsePurchases");
		
		if (signedData == null) {
			Log.e(TAG, "");
			return null;
		}
		
		boolean verified = false;
		if (signature != null && signature.length() > 0) {
			final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsdjePSxUagXVfU8bLOJRjhRuBBoWK8yIO1Ot9utfQZdu7d4C56tG6/qEOHmSt3CHQdTstCsBuX+t+Q3oWcg3E6u9yfe+HY4jeroBPob5zMkiQnz4upOM2G+7EQmpnZ7gS5TwV81NpILJ8blR5WHPjXFFwWD+qiwHAkmWRIFIc5js0utBJhOK6TKCRN5yJu2+tqXOe/vA+HFNLY2xoDx4k4mJtJSiEOc2WlTttPafyoqFnvCHs1oYTQWcO8bOWWYXCodT/4lMbvNaEqWdEWsuBsoRlSVLLqT8gIKYsiId/ifmzi/2ZubRXXNYGvkvtftc0XfqItNIoYjxIC+EPp6eGQIDAQAB";
			PublicKey key = Security.generatePublicKey(base64EncodedPublicKey);
			verified = Security.verify(key, signedData, signature);
			if (!verified) {
				Log.w(TAG, "signature does not match data.");
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
		
		if (Security.isNonceKnown(nonce) == false) {
			Log.e(TAG, "Nonce not known:" + nonce);
			return null;
		}
		
		final int count = jOrders != null ? jOrders.length() : 0;
		Log.d(TAG, "parsePurchases #jOrders:" + count);
		try {
			for (int i = 0; i < count; i++) {
				JSONObject jOrder = jOrders.getJSONObject(i);
				Log.d(TAG, "i:" + i + ",jOrder:" + jOrder);
				
				final String notificationId = jOrder.optString("notificationId", "");
				final String orderId = jOrder.getString("orderId");
				final String productId = jOrder.getString("productId");
				final long purchaseTime = jOrder.getLong("purchaseTime");
				final PurchaseState purchaseState = PurchaseState.valueOf(jOrder.getInt("purchaseState"));
				
				Purchase purchase = new Purchase(notificationId, orderId, productId, purchaseTime, purchaseState);
				purchases.add(purchase);
			}
		} catch (JSONException e) {
			Log.e(TAG, "Error parse jOrders:" + e);
			return null;
		}
		
		removeNonce(nonce);
		return purchases;
	}
	
    public static PublicKey generatePublicKey(String encodedPublicKey) {
    	Log.d(TAG, "generatePublicKey encodedPublicKey: " + encodedPublicKey);
        try {
            byte[] decodedKey = Base64.decode(encodedPublicKey, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
            return keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            Log.e(TAG, "Invalid key specification.");
            throw new IllegalArgumentException(e);
        }
    }
    
    public static boolean verify(PublicKey publicKey, String signedData, String signature) {
    	Log.d(TAG, "verify signature: " + signature);
    	
        Signature sig;
        try {
            sig = Signature.getInstance(SIGNATURE_ALGORITHM);
            sig.initVerify(publicKey);
            sig.update(signedData.getBytes());
            if (!sig.verify(Base64.decode(signature, Base64.DEFAULT))) {
                Log.e(TAG, "Signature verification failed.");
                return false;
            }
            Log.d(TAG, "signature pass verification!");
            return true;
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "NoSuchAlgorithmException.");
        } catch (InvalidKeyException e) {
            Log.e(TAG, "Invalid key specification.");
        } catch (SignatureException e) {
            Log.e(TAG, "Signature exception.");
        }
        return false;
    }
}
