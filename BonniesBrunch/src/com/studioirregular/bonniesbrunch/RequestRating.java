package com.studioirregular.bonniesbrunch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class RequestRating {

	private static final String KEY_ALREADY_RATED = "rated";
	
	public boolean alreadyRated(Context context) {
		SharedPreferences pref = context.getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
		if (pref.contains(KEY_ALREADY_RATED)) {
			return pref.getBoolean(KEY_ALREADY_RATED, false);
		}
		return false;
	}
	
	public void doneRating(Context context) {
		SharedPreferences pref = context.getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putBoolean(KEY_ALREADY_RATED, true);
		editor.commit();
	}
	
	public interface RequestResultListener {
		public void onAccept();
		public void onReject();
	}
	
	public void requestRating(final BonniesBrunchActivity activity, RequestResultListener listener) {
		this.listener = listener;
		
		activity.runOnUiThread(new Runnable() {
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				AlertDialog dialog = builder.setTitle(R.string.request_rating_title)
					.setMessage(R.string.request_rating_message)
					.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (RequestRating.this.listener != null) {
								RequestRating.this.listener.onAccept();
							}
							activity.goToGooglePlayForRating();
						}
					})
					.setNegativeButton(R.string.not_now, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (RequestRating.this.listener != null) {
								RequestRating.this.listener.onReject();
							}
						}
					})
					.setCancelable(true)
					.setOnCancelListener(new DialogInterface.OnCancelListener() {
						
						@Override
						public void onCancel(DialogInterface dialog) {
							if (RequestRating.this.listener != null) {
								RequestRating.this.listener.onReject();
							}
						}
					})
					.create();
				dialog.show();
			}
		});

	}
	
	private RequestResultListener listener;
	
}
