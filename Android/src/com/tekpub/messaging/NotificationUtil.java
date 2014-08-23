package com.tekpub.messaging;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;

import com.tekpub.app.TekPub;
import com.tekpub.player.LoginActivity;
import com.tekpub.player.MainActivity;
import com.tekpub.player.R;

public class NotificationUtil {
	
	public static Dialog getLoadingDialog(Activity activity) {
		return ProgressDialog.show(activity, null, activity.getString(R.string.loading_message), true, false);  
	}
	
	public static Dialog getEpisodeUnavailableDialog(final Activity activity) {
		AlertDialog.Builder buidler 
		= new AlertDialog.Builder(activity)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(activity.getString(R.string.episode_unavailable_title))
			.setMessage(activity.getString(R.string.episode_unavailable_message))
			.setPositiveButton(activity.getString(R.string.episode_unavailable_login), 
					new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
			
							Intent i = new Intent(activity, LoginActivity.class);  
							activity.startActivity(i);
						}
					})
			.setNegativeButton(activity.getString(R.string.episode_unavailable_close),
					new OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
			});
		
		return buidler.create(); 
	}

	public static Dialog getGenericMessageWithOk(Activity activity, String title, String message) {
		AlertDialog.Builder builder 
			= new AlertDialog.Builder(activity)	
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle(title)
		.setMessage(message)
		.setPositiveButton(android.R.string.ok, 
		new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss(); 
				
				
			}
			
		});
		
		return builder.create(); 
		
		
		
	}

	public static Dialog getPositiveButtonWithCallback(Activity activity, String title, String message, String positiveButtonText, String negativeButtonText, final ICallbackCommand callback) {
		AlertDialog.Builder builder 
			= new AlertDialog.Builder(activity)	
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle(title)
		.setMessage(message)
		.setPositiveButton(positiveButtonText, 
		new OnClickListener() {
	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				callback.Execute(); 
				dialog.dismiss(); 
			}			
		})
		.setNegativeButton(negativeButtonText, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss(); 
			}
		});
	
	return builder.create(); 
		
	}
	
	public static Dialog getExceptionDialog(Activity activity, String title, String message) {
		AlertDialog.Builder builder = 
			new AlertDialog.Builder(activity)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle(title)
		.setMessage(message)
		.setPositiveButton(activity.getString(android.R.string.ok), 
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss(); 
						
					}
			
		}); 
		return builder.create(); 
	}

	public static Dialog getLoginQueryDialog(final Activity activity) {
		AlertDialog.Builder builder = 
			new AlertDialog.Builder(activity)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle(activity.getString(R.string.login_query_title))
		.setMessage(activity.getString(R.string.login_query_message))
		.setPositiveButton(activity.getString(R.string.login_query_positive_button), 
			new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) { 
					Intent i = new Intent(activity, LoginActivity.class); 
					activity.startActivity(i); 
				}

			
		})
		.setNegativeButton(activity.getString(R.string.login_query_negative_button), 
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						setLoginQueryPreferencesAsSkip(activity); 
						Intent i = new Intent(activity, MainActivity.class);
						activity.startActivity(i); 
					}
			
		}); 
		
		return builder.create();
	}
	

	public static void setLoginQueryPreferencesAsSkip(final Activity activity) {
		SharedPreferences prefs =  activity.getSharedPreferences(TekPub.LOGIN_PREFERENCES, Activity.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putBoolean(TekPub.PREFERENCE_QUERY_TO_LOGIN, false); 
		editor.commit();
	}

	public static Dialog getLogoutDialog(Activity activity) {
		return ProgressDialog.show(activity, null, activity.getString(R.string.logging_out_message), true, false);  
	}

	public static Dialog getConfirmatinDialog(Activity activity, OnClickListener positiveClickListener, OnClickListener negativeClickListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(activity.getString(R.string.confirmation_title))
			.setPositiveButton(activity.getString(android.R.string.ok),positiveClickListener)
			.setNegativeButton(activity.getString(android.R.string.cancel), negativeClickListener); 
		
		return builder.create();
	}

}
