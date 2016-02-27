package com.studioirregular.bonniesbrunch.ad;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.ads.InterstitialAd;
import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.Game;
import com.studioirregular.bonniesbrunch.Game.StoredPurchaseState;


/* 
 * Implement advertisement using AdMob SDK.
 * Also including business logic.
 * 
 * Two types of ad:
 *   Banner: installed onCreate and auto load/show.
 *   Full screen: need explicitly turn on/off and show/hide.
 */
public class BonniesAdvertisement {

	private static final String TAG = "bonnies-ad";
	
	public static final String ADMOB_PUBLISHER_ID_BANNER = "a14fab9a84d4dac";
	public static final String ADMOB_PUBLISHER_ID_FULL_SCREEN = "a14fab9cc9871ba";
	
	private static final String ANDROID_ID_NEXUS_S = "a74e26c63c7f1a8a";
	private static final String ADS_DEVICE_ID_NEXUS_S = "806139D7A48730208787005163B1C0F1";
	
	private static final String ANDROID_ID_GALAXY_TAB = "5fca44f8867f1614";
	private static final String ADS_DEVICE_ID_GALAXY_TAB = "F15D398B8FE87505B6FC7F999F643B66";
	
	public BonniesAdvertisement(Activity activity, RelativeLayout layout) {
		this.hostActivity = activity;
		this.hostLayout = layout;
	}
	
	public void setupAd() {
		if (doBannerAd()) {
			
			hostActivity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					installBannerAd();
					loadBannerAd();
					
					isBannerAdOn = true;
				}
				
			});
			
		}
		
		if (doInterstitalAd()) {
			
			hostActivity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					installFullScreenAd();
				}
				
			});
			
		}
	}
	
	public void onDestroy() {
		uninstallBannerAd();
		uninstallFullScreenAd();
	}
	
	public void hideBanner() {
		if (Config.DEBUG_LOG) Log.w(TAG, "hideBanner");
		
		if (bannerAd == null || !isBannerAdOn) {
			return;
		}
		
		hostActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				bannerAd.setVisibility(View.GONE);
			}
			
		});
	}
	
	public void showBanner() {
		if (Config.DEBUG_LOG) Log.w(TAG, "showBanner");
		
		if (bannerAd == null || !isBannerAdOn) {
			return;
		}
		
		hostActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				bannerAd.setVisibility(View.VISIBLE);
			}
			
		});
	}
	
	public void turnOffBanner() {
		if (Config.DEBUG_LOG) Log.w(TAG, "turnOffBanner");
		
		if (bannerAd == null) {
			return;
		}
		
		hideBanner();
		
		if (!isBannerAdOn) {
			return;
		}
		
		isBannerAdOn = false;
		
		hostActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				bannerAd.stopLoading();
			}
			
		});
	}
	
	public void turnOnBanner() {
		if (Config.DEBUG_LOG) Log.w(TAG, "turnOnBanner");
		
		if (bannerAd == null) {
			return;
		}
		
		showBanner();
		
		if (isBannerAdOn) {
			return;
		}
		
		hostActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				loadBannerAd();
			}
			
		});
		
		isBannerAdOn = true;
	}
	
	public static abstract class FullScreenAdListener {
		public void onReady() {
			
		}
		public void onLoadFailed() {
			
		}
		public void onReturnFromAd() {
			
		}
	}
	
	public void prepareFullScreenAd() {
		if (fullScreenAd == null) {
			return;
		}
		
		hostActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				loadFullScreenAd();
			}
			
		});
	}
	
	public boolean isFullScreenAdReady() {
		return interstitalAdAgent == null ? false : interstitalAdAgent.isReady;
	}
	
	public void registerListener(FullScreenAdListener client) {
		if (interstitalAdAgent != null) {
			interstitalAdAgent.setListener(client);
		}
	}
	
	public void showFullScreenAd() {
		if (fullScreenAd == null) {
			return;
		}
		
		hostActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				fullScreenAd.show();
			}
			
		});
	}
	
	private boolean doBannerAd() {
		if (Game.getInstance().getFullVersionPurchaseState().equals(StoredPurchaseState.PURCHASED)) {
			return false;
		}
		return Config.ENABLE_BANNER_AD;
	}
	
	private boolean doInterstitalAd() {
		if (Game.getInstance().getFullVersionPurchaseState().equals(StoredPurchaseState.PURCHASED)) {
			return false;
		}
		return Config.ENABLE_INTERSTITAL_AD;
	}
	
	public void notifyFullVersionPurchaseStateChanged() {
		if (Config.DEBUG_LOG) Log.w(TAG, "notifyFullVersionPurchaseStateChanged");
		
		if (doBannerAd()) {
			installBannerAd();
			loadBannerAd();
			isBannerAdOn = true;
		} else {
			uninstallBannerAd();
			isBannerAdOn = false;
		}
		
		if (doInterstitalAd()) {
			installFullScreenAd();
		} else {
			uninstallFullScreenAd();
		}
	}
	
	private void installBannerAd() {
		if (Config.DEBUG_LOG) Log.w(TAG, "installBannerAd");
		bannerAd = new AdView(hostActivity, AdSize.BANNER,
				ADMOB_PUBLISHER_ID_BANNER);

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		hostLayout.addView(bannerAd, lp);
	}
	
	private void uninstallBannerAd() {
		if (Config.DEBUG_LOG) Log.w(TAG, "uninstallBannerAd bannerAd:" + bannerAd + ",isBannerAdOn:" + isBannerAdOn);
		
		if (bannerAd == null) {
			return;
		}
		
		this.turnOffBanner();
		
		hostActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				bannerAd.stopLoading();
				bannerAd.destroy();
				hostLayout.removeView(bannerAd);
				bannerAd = null;
			}
			
		});
	}
	
	private void installFullScreenAd() {
		if (Config.DEBUG_LOG) Log.w(TAG, "installFullScreenAd");
		fullScreenAd = new InterstitialAd(hostActivity, ADMOB_PUBLISHER_ID_FULL_SCREEN);
		
		interstitalAdAgent = new InterstitialAdAgent();
		fullScreenAd.setAdListener(interstitalAdAgent);
	}
	
	private void uninstallFullScreenAd() {
		if (Config.DEBUG_LOG) Log.w(TAG, "uninstallFullScreenAd");
		if (fullScreenAd != null) {
			fullScreenAd.stopLoading();
			fullScreenAd = null;
		}
		
		interstitalAdAgent = null;
	}
	
	private void loadBannerAd() {
		if (Config.DEBUG_LOG) Log.w(TAG, "loadBannerAd");
		AdRequest request = new AdRequest();
		
		if (Config.DEBUG_AD) {
			final String ANDROID_ID = Game.getInstance().getAndroidId();
			
			if (ANDROID_ID_NEXUS_S.equals(ANDROID_ID)) {
				request.addTestDevice(ADS_DEVICE_ID_NEXUS_S);
			} else if (ANDROID_ID_GALAXY_TAB.equals(ANDROID_ID)) {
				request.addTestDevice(ADS_DEVICE_ID_GALAXY_TAB);
			}
		}
		
		bannerAd.loadAd(request);
	}
	
	private void loadFullScreenAd() {
		if (Config.DEBUG_LOG) Log.w(TAG, "loadFullScreenAd");
		AdRequest request = new AdRequest();
		
		if (Config.DEBUG_AD) {
			final String ANDROID_ID = Game.getInstance().getAndroidId();
			
			if (ANDROID_ID_NEXUS_S.equals(ANDROID_ID)) {
				request.addTestDevice(ADS_DEVICE_ID_NEXUS_S);
			} else if (ANDROID_ID_GALAXY_TAB.equals(ANDROID_ID)) {
				request.addTestDevice(ADS_DEVICE_ID_GALAXY_TAB);
			}
		}
		
		fullScreenAd.loadAd(request);
	}
	
	private class InterstitialAdAgent implements AdListener {

		@Override
		public void onReceiveAd(Ad arg0) {
			if (Config.DEBUG_LOG) Log.w(TAG, "onReceiveAd");
			
			isReady = true;
			if (client != null) {
				client.onReady();
			}
		}
		
		@Override
		public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
			if (Config.DEBUG_LOG) Log.w(TAG, "onFailedToReceiveAd errorCode:" + arg1);
			
			isReady = false;
			
			if (client != null) {
				client.onLoadFailed();
			}
		}
		
		@Override
		public void onPresentScreen(Ad arg0) {
			if (Config.DEBUG_LOG) Log.w(TAG, "onPresentScreen");
			
			isReady = false;
		}
		
		@Override
		public void onLeaveApplication(Ad arg0) {
			if (Config.DEBUG_LOG) Log.w(TAG, "onLeaveApplication");
		}
		
		@Override
		public void onDismissScreen(Ad arg0) {
			if (Config.DEBUG_LOG) Log.w(TAG, "onDismissScreen");
			
			isReady = false;
			
			if (client != null) {
				client.onReturnFromAd();
			}
		}
		
		public void setListener(FullScreenAdListener client) {
			this.client = client;
		}
		
		private FullScreenAdListener client;
		private boolean isReady = false;
	}
	
	private Activity hostActivity;
	private RelativeLayout hostLayout;
	
	private AdView bannerAd;
	private boolean isBannerAdOn;
	private InterstitialAd fullScreenAd;
	private InterstitialAdAgent interstitalAdAgent;
}
