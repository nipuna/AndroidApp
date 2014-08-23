package com.tekpub.storage;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.tekpub.models.Episode;

/**
 * Contract for Episodes repository
 * @author donnfelker
 *
 */
public interface IEpisodeRepository {

	List<Episode> getEpisodesFrom(long productionRowId);
	void saveEpisodesFor(SQLiteDatabase db, long productionId, List<Episode> episodes);
	
}
