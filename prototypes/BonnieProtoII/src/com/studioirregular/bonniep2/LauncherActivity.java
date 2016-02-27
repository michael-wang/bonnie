package com.studioirregular.bonniep2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class LauncherActivity extends Activity {

	static String[] SCENE_NAME = new String[] {
		"1-1 tutorial",
		"1-8 muffin machine",
		"2-8 physicist",
		"3-7 food critic",
		"4-4 tramp",
		"just table"
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		showDialog(DIALOG_SCENE_LIST);
	}
	
	private static final int DIALOG_SCENE_LIST = 1;
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		if (id == DIALOG_SCENE_LIST) {
			return new AlertDialog.Builder(this)
			.setTitle("Choose a Stage to play!")
			.setItems(SCENE_NAME, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent goBonnie = new Intent(LauncherActivity.this, BonnieProtoIIActivity.class);
					goBonnie.putExtra(BonnieProtoIIActivity.EXTRA_SCENE_NAME, SCENE_NAME[which]);
					startActivity(goBonnie);
					
					dialog.dismiss();
				}
			})
			.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					finish();
				}
			})
			.create();
		}
		return null;
	}

}
