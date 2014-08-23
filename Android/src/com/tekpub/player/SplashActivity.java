package com.tekpub.player;

import roboguice.activity.RoboActivity;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.tekpub.app.TekPub;
import com.tekpub.messaging.NotificationUtil;

public class SplashActivity extends RoboActivity {
	// ===========================================================
	// Fields
	// ===========================================================

	private final int SPLASH_DISPLAY_LENGTH = 2500;
	private final int DIALOG_LOGIN_QUERY = 10; 

	// ===========================================================
	// "Constructors"
	// ===========================================================

	/** Called when the activity is first created. */
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.splash);
		ImageView iv = (ImageView) findViewById(R.id.logo);

		setLayoutAnimationFor(iv);
 
		/*
		 * New Handler to start the Menu-Activity and close this Splash-Screen
		 * after some seconds.
		 */
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				
		        
				
				SharedPreferences prefs = getSharedPreferences(TekPub.LOGIN_PREFERENCES, MODE_PRIVATE); 
				boolean shouldQueryToLogin = prefs.getBoolean(TekPub.PREFERENCE_QUERY_TO_LOGIN, true); 
				
		        AccountManager am = AccountManager.get(SplashActivity.this); 
		        Account[] accounts = am.getAccountsByType(getString(R.string.ACCOUNT_TYPE)); 
		       
		        // Ask them if they want to login, otherwise, just show the 
		        // Login screen
		        if(shouldQueryToLogin && accounts.length == 0) {
		        	// User does not have an account, ask them if they'd like to login. 
		        	showDialog(DIALOG_LOGIN_QUERY);
		        } else {
		        	// Create an Intent that will start the Menu-Activity.
					Intent mainIntent = new Intent(SplashActivity.this,
							MainActivity.class);
					SplashActivity.this.startActivity(mainIntent);
					SplashActivity.this.finish();
		        }
				
				
			}
		}, SPLASH_DISPLAY_LENGTH);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_LOGIN_QUERY:
				return NotificationUtil.getLoginQueryDialog(this);
		}
		return super.onCreateDialog(id);
	}
	
	private void setLayoutAnimationFor(ImageView iv) {
		AnimationSet set = new AnimationSet(true);

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(300);
		set.addAnimation(animation);

		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				-1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(300);
		set.addAnimation(animation);
		iv.startAnimation(set);

	}
}
