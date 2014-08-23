package com.tekpub.services;

import java.io.IOException;

import roboguice.inject.InjectExtra;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.inject.Inject;
import com.tekpub.app.TekPub;
import com.tekpub.exception.TekPubFileNotFoundException;
import com.tekpub.exception.TekPubFreeSpaceException;
import com.tekpub.http.ITekPubApi;
import com.tekpub.messaging.INotificationService;
import com.tekpub.messaging.IProgressNotifier;
import com.tekpub.messaging.MessagingConstants;
import com.tekpub.models.MessageType;
import com.tekpub.models.Production;
import com.tekpub.player.R;
import com.tekpub.player.VideoActivity;
import com.tekpub.storage.DbConstants;
import com.tekpub.storage.IProductionRepository;

/**
 * Background service that is responsible for downloading a file. 
 * @author donnfelker
 *
 */
public class DownloadEpisodeService extends WakefulntentService implements IProgressNotifier {

	@Inject ITekPubApi mApi; 
	@Inject NotificationManager mNotificationManager; 
	@InjectExtra(DbConstants.KEY_EPISODES_EPISODE_ID) String mEpisodeProductionid;
	@InjectExtra(DbConstants.KEY_EPISODES_TITLE) String mEpisodeTitle; 
	@Inject INotificationService mNotificationService; 
	@Inject IProductionRepository mProductionRepository; 
	
	private static String TAG = DownloadEpisodeService.class.getName(); 
	

	public DownloadEpisodeService() {
		super("DownloadEpisodeService");
	}

	@Override
	void doWork(Intent intent) {


		// While the file is downloading, have it publish progress back to this 
		// service so we can update the notification bar
		try {
			DownloadEpisodeService.acquireStaticLock(this); 
			Log.d(TAG, "Starting download"); 
			mApi.downloadEpisode(intent.getExtras().getString(DbConstants.KEY_EPISODES_EPISODE_ID), this, this);
		} catch (TekPubFileNotFoundException e) {
			Log.d("test", "test");
			Log.e(TAG, e.getMessage(), e); 
			
			createSimpleNotification(intent, getString(R.string.download_file_not_found_message),  getString(R.string.download_file_not_found_message), MessageType.GENERIC); 
			
		} catch (TekPubFreeSpaceException e) {
			// Update Notification bar of problem. 
			e.printStackTrace();
		} catch (IOException e) {
			// Usually this occurs if the app cannot get to the tekpub api. (Connection lost, site down, api down, etc). 
			// doc: http://download.oracle.com/javase/1.4.2/docs/api/java/io/IOException.html
			createSimpleNotification(intent, getString(R.string.cannot_connect_to_api_message), getString(R.string.cannot_connect_to_api_message), MessageType.CONNECTION_NOT_FOUND);
		}
		
	}
	
	private void createSimpleNotification(Intent intent, String marquee, String dialogMessage, int messageType) {
		String episodeId = intent.getStringExtra(DbConstants.KEY_EPISODES_EPISODE_ID); 
		int episodeRowId = intent.getIntExtra(DbConstants.KEY_EPISODES_ROWID, -1); 
		long productionId = intent.getLongExtra(DbConstants.KEY_EPISODES_PRODUCTION_ID, -1);
		String episodeTitle = intent.getStringExtra(DbConstants.KEY_EPISODES_TITLE); 

		Production p = mProductionRepository.getProduction(productionId); 
		episodeTitle = String.format("%s - %s", p.getTitle(), episodeTitle);
		
		Intent whenTapped = new Intent(this, VideoActivity.class);
		whenTapped.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		whenTapped.putExtra(TekPub.VIEW_SELECTED_EPISODE, episodeRowId); 
		whenTapped.putExtra(TekPub.VIEW_PRODUCTION, productionId); 
		whenTapped.putExtra(DbConstants.KEY_EPISODES_EPISODE_ID, episodeId); 
		whenTapped.putExtra(TekPub.MESSAGE_KEY, dialogMessage); 
		whenTapped.putExtra(TekPub.MESSAGE_TYPE, messageType);
		
		mNotificationService.createSimpleNotification(episodeRowId, whenTapped, marquee, episodeTitle, dialogMessage, android.R.drawable.stat_sys_warning);
	}

	@Override
	public void publishProgress(int progress) {
		Log.d(TAG, "Publishing progress: " + progress); 
		Intent notificationIntent = new Intent(this, VideoActivity.class); 
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		notificationIntent.putExtra(DbConstants.KEY_EPISODES_EPISODE_ID, mEpisodeProductionid); 
		
		PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT); 
		
		Notification note = null;
		
		if(progress < 100) {
			// Update progress in status bar. 
			Log.d(TAG, "Creating remote views: " + progress); 
			RemoteViews downloadEpisodeView = new RemoteViews(getPackageName(), R.layout.download_notification_view);
			downloadEpisodeView.setProgressBar(R.id.download_progress_bar, 100, progress, false); 
			
			note=new Notification(android.R.drawable.stat_sys_download, String.format(getString(R.string.download_episode_ticker_format), mEpisodeTitle), System.currentTimeMillis());
			note.contentIntent = pi; 
			note.contentView = downloadEpisodeView; 
			
			note.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT; // Do not let the user "clear" the notification.  
			
			
		} else {
			// The download is done. Provide a cancelable icon that will take them 
			// to the episode page. 
			String episodeDownloaded = String.format(getString(R.string.download_complete_format)); 
			note = new Notification(android.R.drawable.stat_sys_download_done, episodeDownloaded, System.currentTimeMillis());
			note.setLatestEventInfo(this, mEpisodeTitle, episodeDownloaded, pi); 
		}
	
		// An issue could occur if user ever enters over 2,147,483,647 TekPub episodes. (Max int value). 
		// I highly doubt this will ever happen. But is good to note. 
		int id = Integer.parseInt(mEpisodeProductionid); 
		mNotificationManager.notify(id, note);
		
		// Notify the activity if its open to update its own progress bar
		notificationIntent.setAction(MessagingConstants.VIEW_EPISODE_ACTION); 
		notificationIntent.putExtra(MessagingConstants.EPISODE_DOWNLOAD_PROGRESS_KEY, progress); 
		sendBroadcast(notificationIntent);
		
	}
}
