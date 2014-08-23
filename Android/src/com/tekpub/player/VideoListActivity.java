package com.tekpub.player;


import roboguice.activity.RoboListActivity;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.tekpub.app.TekPub;
import com.tekpub.models.Episode;
import com.tekpub.models.Production;
import com.tekpub.storage.IProductionRepository;

public class VideoListActivity extends RoboListActivity {
	
	private Production mProduction; 
	private long mProductionRowId; 
	@Inject IProductionRepository mProductionRepository;  

	@InjectView(R.id.label)			TextView tv; 
	@InjectView(R.id.details_line)	TextView details;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_main);
		
		mProductionRowId = getIntent().getLongExtra(TekPub.VIEW_PRODUCTION, 0); 
		mProduction = mProductionRepository.getProduction(mProductionRowId);  //new MockCategoryRepository().getCategories().get(mPosition); 
	
		tv.setText(mProduction.getTitle()); 
		details.setText(Html.fromHtml(mProduction.getDescription()));
			
		setListAdapter(new VideoListAdapter());
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = new Intent(VideoListActivity.this, VideoActivity.class); 
		i.putExtra(TekPub.VIEW_PRODUCTION, mProductionRowId); 
    	i.putExtra(TekPub.VIEW_SELECTED_EPISODE, mProduction.getEpisodes().get(position).getRowId()); 
    	i.putExtra(TekPub.EPISODE_NUMBER, mProduction.getEpisodes().get(position).getNumber());
    	i.putExtra(TekPub.EPISODE_SLUG, mProduction.getEpisodes().get(position).getSlug()); 
    	startActivity(i); 
	}
	
	class VideoListAdapter extends ArrayAdapter<Episode> {
		VideoListAdapter() {
			super(VideoListActivity.this, R.layout.videorow, R.id.label, mProduction.getEpisodes()); 
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = getLayoutInflater();
  	      View row = inflater.inflate(R.layout.videorow, parent, false);

  	      TextView label=(TextView)row.findViewById(R.id.label);
  	      label.setText(mProduction.getEpisodes().get(position).getTitle());

  	      TextView description=(TextView)row.findViewById(R.id.video_description);
  	      description.setText(Html.fromHtml(mProduction.getEpisodes().get(position).getDescription()));

  	      
  	      return(row);
		}
	}
}
