package com.tekpub.mappers;

import android.database.Cursor;

import com.tekpub.models.Episode;
import com.tekpub.storage.DbConstants;

public class EpisodeCursorMapper implements IEpisodeCursorMapper {

	@Override
	public Episode MapFrom(Cursor c) {
		Episode e = new Episode(); 
	
		e.setChannelSlug(c.getString(c.getColumnIndex(DbConstants.KEY_EPISODES_CHANNEL_SLUG)));
		e.setCreatedAt(c.getString(c.getColumnIndex(DbConstants.KEY_EPISODES_CREATED_AT)));
		e.setDescription(c.getString(c.getColumnIndex(DbConstants.KEY_EPISODES_DESCRIPTION))); 
		e.setDownloads(c.getInt(c.getColumnIndex(DbConstants.KEY_EPISODES_DOWNLOADS)));
		e.setDuration(c.getInt(c.getColumnIndex(DbConstants.KEY_EPISODES_DURATION)));
		e.setHeight(c.getDouble(c.getColumnIndex(DbConstants.KEY_EPISODES_HEIGHT)));
		e.setId(c.getInt(c.getColumnIndex(DbConstants.KEY_EPISODES_EPISODE_ID)));
		e.setIsDownloading(c.getInt(c.getColumnIndex(DbConstants.KEY_EPISODES_IS_DOWNLOADING)) == 1 ? true : false); 
		e.setNotes(c.getString(c.getColumnIndex(DbConstants.KEY_EPISODES_NOTES))); 
		e.setNumber(c.getInt(c.getColumnIndex(DbConstants.KEY_EPISODES_NUMBER)));  
		e.setOverrideUrl(c.getString(c.getColumnIndex(DbConstants.KEY_EPISODES_OVERRIDE_URL))); 
		e.setProduction_id(c.getInt(c.getColumnIndex(DbConstants.KEY_EPISODES_PRODUCTION_ID))); 
		e.setReleased_at(c.getString(c.getColumnIndex(DbConstants.KEY_EPISODES_RELEASED_AT))); 
		e.setRowId(c.getInt(c.getColumnIndex(DbConstants.KEY_EPISODES_ROWID))); 
		e.setSlug(c.getString(c.getColumnIndex(DbConstants.KEY_EPISODES_SLUG))); 
		e.setTitle(c.getString(c.getColumnIndex(DbConstants.KEY_EPISODES_TITLE))); 
		e.setUpdatedAt(c.getString(c.getColumnIndex(DbConstants.KEY_EPISODES_UPDATED_AT))); 
		e.setViews(c.getInt(c.getColumnIndex(DbConstants.KEY_EPISODES_VIEWS))); 
		e.setWidth(c.getDouble(c.getColumnIndex(DbConstants.KEY_EPISODES_WIDTH))); 
		
		return e;
	}

}
