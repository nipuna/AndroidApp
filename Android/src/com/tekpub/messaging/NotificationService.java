package com.tekpub.messaging;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class NotificationService implements INotificationService {

	@Inject NotificationManager mNotificationManager;
	Context mContext; 
	private static final String TAG = NotificationService.class.getName(); 
	
	@Inject
	public NotificationService(Provider<Application> contextProvider) 
	{
		mContext = contextProvider.get(); 
	}

	@Override
	public void createSimpleNotification(int notificationId, Intent intent, String marquee, String title, String message, int iconId) { 
		
		Log.d(TAG, String.format("Setting notification. Title: %s. Message: %s", title, message));
		
		PendingIntent pi = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT ); 
		Notification note = new Notification(iconId, marquee, System.currentTimeMillis());
		note.setLatestEventInfo(mContext, title, message, pi); 
		note.defaults = Notification.DEFAULT_ALL; 
		note.flags = Notification.FLAG_AUTO_CANCEL;
		mNotificationManager.notify(notificationId, note);
	}


	
	

	

	

}
