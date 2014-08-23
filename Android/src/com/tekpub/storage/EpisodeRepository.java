package com.tekpub.storage;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.inject.Inject;
import com.tekpub.mappers.IEpisodeCursorMapper;
import com.tekpub.models.Episode;

public class EpisodeRepository implements IEpisodeRepository {

	private IDbHelper mDbHelper; 
	private SQLiteDatabase mDb; 
	private IEpisodeCursorMapper mCursorMapper; 
	
	@Inject 
	public EpisodeRepository(IDbHelper dbHelper, IEpisodeCursorMapper cursorMapper) {
		this.mDbHelper = dbHelper; 
		this.mCursorMapper = cursorMapper;
	}
	
	private void open() {
			mDb = mDbHelper.open();
	}
	
	private void close() { 
		mDbHelper.close(); 
	}
	
	@Override
	public List<Episode> getEpisodesFrom(long productionRowId) {
		List<Episode> episodes = new ArrayList<Episode>(); 
		try { 
			open();
			Cursor c = mDb.query(DbConstants.DATABASE_TABLE_EPISODES_NAME, DbConstants.EPISODE_FIELDS, 
					DbConstants.KEY_EPISODES_PRODUCTION_ID + "=" + productionRowId, 
					null, 
					null, 
					null, 
					DbConstants.KEY_EPISODES_NUMBER + " ASC");
			
			if(c != null && c.isAfterLast() == false) {
				// We have results to return, load those. 
				c.moveToFirst();
				while(c.isAfterLast() == false) {
					Episode e = mCursorMapper.MapFrom(c);
					episodes.add(e);  
					c.moveToNext(); 
				}
				
				
			}
			c.close();
			return episodes;
			
		} finally { 
			close(); 
		}
	}

	@Override
	public void saveEpisodesFor(SQLiteDatabase db, long productionId, List<Episode> episodes) {

		mDb = db; 
		
		for(Episode e : episodes) {
			saveEpisode(productionId, e);
		}
	
	}

	private void saveEpisode(long productionId, Episode e) {
		ContentValues values = new ContentValues(); 
		values.put(DbConstants.KEY_EPISODES_PRODUCTION_ID, productionId); 
		values.put(DbConstants.KEY_EPISODES_CHANNEL_SLUG, e.getChannelSlug());
		values.put(DbConstants.KEY_EPISODES_CREATED_AT, e.getCreatedAt());
		values.put(DbConstants.KEY_EPISODES_DESCRIPTION, e.getDescription()); 
		values.put(DbConstants.KEY_EPISODES_DOWNLOADS, e.getDownloads());
		values.put(DbConstants.KEY_EPISODES_DURATION, e.getDuration());
		values.put(DbConstants.KEY_EPISODES_EPISODE_ID, e.getId()); 
		values.put(DbConstants.KEY_EPISODES_HEIGHT, e.getHeight()); 
		values.put(DbConstants.KEY_EPISODES_NOTES, e.getNotes()); 
		values.put(DbConstants.KEY_EPISODES_NUMBER,e.getNumber()); 
		values.put(DbConstants.KEY_EPISODES_OVERRIDE_URL, e.getOverrideUrl());
		values.put(DbConstants.KEY_EPISODES_RELEASED_AT, e.getReleased_at());
		values.put(DbConstants.KEY_EPISODES_SLUG, e.getSlug());
		values.put(DbConstants.KEY_EPISODES_TITLE, e.getTitle()); 
		values.put(DbConstants.KEY_EPISODES_UPDATED_AT, e.getUpdatedAt());
		values.put(DbConstants.KEY_EPISODES_VIEWS, e.getViews());
		values.put(DbConstants.KEY_EPISODES_WIDTH, e.getWidth()); 
		
		mDb.insert(DbConstants.DATABASE_TABLE_EPISODES_NAME, null, values);
	}
	
	


}
