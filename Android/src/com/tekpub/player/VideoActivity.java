package com.tekpub.player;


import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.inject.Inject;
import com.google.inject.internal.Nullable;
import com.tekpub.app.TekPub;
import com.tekpub.http.ITekPubApi;
import com.tekpub.messaging.ICallbackCommand;
import com.tekpub.messaging.MessagingConstants;
import com.tekpub.messaging.NotificationUtil;
import com.tekpub.models.Episode;
import com.tekpub.models.MessageType;
import com.tekpub.models.Production;
import com.tekpub.models.VideoResult;
import com.tekpub.services.DownloadEpisodeService;
import com.tekpub.storage.DbConstants;
import com.tekpub.storage.IProductionRepository;

public class VideoActivity extends RoboActivity {


	@Inject IProductionRepository mProductionRepository; 
	@Inject ITekPubApi mApi; 
	
	@InjectExtra(TekPub.VIEW_SELECTED_EPISODE)		
	protected int mEpisodeId; 
	
	@InjectExtra(TekPub.VIEW_PRODUCTION)
	protected long mProductionRowId; 
	
	@InjectExtra(value = TekPub.MESSAGE_KEY, optional = true)
	@Nullable
	protected String mMessage; 
	
	@InjectExtra(value = TekPub.MESSAGE_TYPE, optional = true)
	@Nullable
	protected Integer mMessageType; 
	
	@InjectExtra(value = TekPub.EPISODE_NUMBER, optional = true)
	@Nullable
	protected Integer mEpisodeNumber; 
	
	@InjectExtra(value = TekPub.EPISODE_SLUG, optional = true)
	@Nullable
	protected String mEpisodeSlug; 
	
	// View injection 
	@InjectView(R.id.watch_button) 					Button mWatchButton;
	@InjectView(R.id.unavailable_button)			Button mUnavailableButton; 
	@InjectView(R.id.episode_title)					TextView mEpisodeTitle; 
	@InjectView(R.id.production_title)				TextView mProductionTitle; 
	@InjectView(R.id.episode_details)				TextView mEpisodeDetails;
	@InjectView(R.id.episode_download_progress_bar)	ProgressBar mProgressBar; 
	
	@InjectResource(R.string.loading_message) String mLoadingMessage;
	
	private Episode mEpisode; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.video);
		
		setListeners();
		renderView(); 
		
		if(mMessage != null && mMessageType != null) {
			showDialog(mMessageType);
		}
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(receiver, new IntentFilter(MessagingConstants.VIEW_EPISODE_ACTION)); 
	}
	
	@Override
	protected void onPause() {
		unregisterReceiver(receiver); 
		super.onPause();
	}
	
	private void setListeners() {
		mUnavailableButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(MessageType.UNAVAILABLE);
			}
		});
		
		mWatchButton.setOnClickListener(new OnClickListener() {
			
			@Override 
			public void onClick(View v) {
				// Fire off a background service 
				//startDownload();
				GetVideoUrlTask viewVideoTask = new GetVideoUrlTask(mEpisodeNumber, mEpisodeSlug);
				viewVideoTask.execute(); 
			}
		}); 
		
	}
	

	public class GetVideoUrlTask extends RoboAsyncTask<VideoResult> {

		private int number; 
		private String slug;
		private ProgressDialog dialog; 
		
		public GetVideoUrlTask(int number, String slug) {
			this.number = number; 
			this.slug = slug; 
		}
		
		@Override
		protected void onPreExecute() throws Exception {
			mWatchButton.setEnabled(false);
			dialog = ProgressDialog.show(VideoActivity.this, null, mLoadingMessage, true, false);  
		}
		
		@Override
		public VideoResult call() throws Exception {
			return mApi.getVideo(slug, number);
		}
		
		@Override
		protected void onSuccess(VideoResult videoResult) throws Exception {
			if(videoResult.videoWasFound()) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setDataAndType(Uri.parse(videoResult.getVideoUrl()),"video/mp4"); 
				//i.setData(Uri.parse(videoResult.getVideoUrl())); 
				startActivity(Intent.createChooser(i, VideoActivity.this.getString(R.string.play_video_with)));
			} else {
				// Show alert 
				AlertDialog.Builder builder = new AlertDialog.Builder(VideoActivity.this); 
				builder.setMessage(videoResult.getErrorMessage()).create().show(); 
			}
		}
		
		@Override
		protected void onFinally() throws RuntimeException {
			mWatchButton.setEnabled(true); 
			if(dialog.isShowing()) {
				dialog.dismiss(); 
			}
		}
		
		@Override
		protected void onException(Exception e) throws RuntimeException {
			AlertDialog.Builder builder = new AlertDialog.Builder(VideoActivity.this); 
			builder.setMessage(e.getMessage()).create().show(); 
		}
		
		
	}
	
	
	
	private void startDownload() {
		Intent i = new Intent(VideoActivity.this, DownloadEpisodeService.class); 
		i.putExtra(DbConstants.KEY_EPISODES_EPISODE_ID, mEpisode.getId());  
		i.putExtra(DbConstants.KEY_EPISODES_ROWID, mEpisode.getRowId()); 
		i.putExtra(DbConstants.KEY_EPISODES_TITLE, mEpisode.getTitle()); 
		i.putExtra(DbConstants.KEY_EPISODES_PRODUCTION_ID, mProductionRowId); 
		VideoActivity.this.startService(i);
	}

	/**
	 * Takes the episode details and renders them to the screen.
	 */
	private void renderView() {
		Production production = mProductionRepository.getProduction(mProductionRowId);
		mEpisode = production.getEpisode(mEpisodeId);
		
		mEpisodeTitle.setText(Html.fromHtml(mEpisode.getTitle())); 
		mEpisodeDetails.setText(Html.fromHtml(mEpisode.getDescription())); 
		mProductionTitle.setText(Html.fromHtml(production.getTitle())); 
		
		if(production.getCan_watch() == false) {
			mWatchButton.setVisibility(View.GONE);
			mUnavailableButton.setVisibility(View.VISIBLE); 
		}
		
		
		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case MessageType.CONNECTION_NOT_FOUND: 
				return NotificationUtil.getPositiveButtonWithCallback(VideoActivity.this, getString(R.string.generic_alert_dialog_title), mMessage, getString(android.R.string.yes), getString(android.R.string.cancel),
						new ICallbackCommand() {
							
							@Override
							public void Execute() {
								// Retry
								startDownload(); 
							}
						});
			case MessageType.UNAVAILABLE: 
				return NotificationUtil.getEpisodeUnavailableDialog(VideoActivity.this);
			case MessageType.GENERIC: 
				return NotificationUtil.getGenericMessageWithOk(VideoActivity.this, getString(R.string.generic_alert_dialog_title), mMessage); 
		}
		return super.onCreateDialog(id);
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			 
			Bundle extras = intent.getExtras();
			int episodeId = Integer.parseInt(extras.getString(DbConstants.KEY_EPISODES_EPISODE_ID));
			if(episodeId == mEpisodeId) {
				// The information broadcast about the episode is the same 
				// episode that the user is currently looking at. Therefore, 
				// update the progress bar. 
				mProgressBar.setVisibility(View.VISIBLE); 
				int progress = extras.getInt(MessagingConstants.EPISODE_DOWNLOAD_PROGRESS_KEY);
				mProgressBar.setProgress(progress); 
			}
		}
	}; 
}
