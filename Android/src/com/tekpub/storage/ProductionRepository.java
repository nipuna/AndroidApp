package com.tekpub.storage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.inject.Inject;
import com.tekpub.exception.TekPubApiException;
import com.tekpub.http.ITekPubApi;
import com.tekpub.mappers.IProductionCursorMapper;
import com.tekpub.models.NullProduction;
import com.tekpub.models.Production;

public class ProductionRepository implements IProductionRepository {
	
	private static String TAG = ProductionRepository.class.getName(); 
	
	private IDbHelper mDbHelper;
	private IProductionCursorMapper mProductionCursorMapper; 
	private ITekPubApi mApi; 
	private SQLiteDatabase mDb; 
	private IEpisodeRepository mEpisodeRepository; 
	
	@Inject
	public ProductionRepository(IDbHelper dbHelper, IProductionCursorMapper productionCursorMapper, ITekPubApi api, IEpisodeRepository episodeRepository) {
		this.mDbHelper = dbHelper; 
		this.mProductionCursorMapper = productionCursorMapper;
		this.mApi = api; 
		this.mEpisodeRepository = episodeRepository; 
	}
	
	private ProductionRepository open() throws SQLException {
		mDb = mDbHelper.open();  
		return this; 
	}
	
	private void close() {
		mDbHelper.close(); 
	}
	
	/**
	 * Returns a Production object with a fully hydrated episodes
	 * list.  
	 * @param slug
	 * @return
	 */
	public Production getProduction(String slug) {
		
		Cursor c = null; 
		try { 
			open();
			c = mDb.query(DbConstants.DATABASE_TABLE_PRODUCTIONS_NAME, new String[] {
					DbConstants.KEY_PROD_CAN_WATCH, 
					DbConstants.KEY_PROD_DESCRIPTION, 
					DbConstants.KEY_PROD_PRICE, 
					DbConstants.KEY_PROD_ROWID, 
					DbConstants.KEY_PROD_SLUG, 
					DbConstants.KEY_PROD_TITLE}, 
					DbConstants.KEY_PROD_SLUG + "=" + slug, 
					null, 
					null, 
					null, 
					null);
			
				return mProductionCursorMapper.MapFrom(c); 
		} finally { 
			if(c != null && c.isClosed() == false) {
				c.close();
			}
			close(); 
		}
	}

	/**
	 * Gets the list of {@link Production} objects from the DB if they exist. 
	 * If they do not exist a call to the API will be made to retrieve them. 
	 * @throws TekPubApiException  
	 * @throws IOException 
	 * @throws IllegalStateException 
	 * @throws URISyntaxException 
	 */
	@Override
	public List<Production> getProductions() throws TekPubApiException, IllegalStateException, IOException, URISyntaxException {
		final List<Production> productions = new ArrayList<Production>(); 
		Cursor c = null; 
		try{
			open();
			c = mDb.query(DbConstants.DATABASE_TABLE_PRODUCTIONS_NAME, new String[] {
					DbConstants.KEY_PROD_CAN_WATCH, 
					DbConstants.KEY_PROD_DESCRIPTION, 
					DbConstants.KEY_PROD_PRICE, 
					DbConstants.KEY_PROD_ROWID, 
					DbConstants.KEY_PROD_SLUG, 
					DbConstants.KEY_PROD_TITLE}, 
					null, 
					null, 
					null, 
					null, 
					DbConstants.KEY_PROD_CAN_WATCH + "= 1,"
					+ DbConstants.KEY_PROD_TITLE + " ASC");
			
			if(c != null && c.isAfterLast() == false) {
				// We have results to return, load those. 
				c.moveToFirst(); 
				while(c.isAfterLast() == false) {
					Production p = mProductionCursorMapper.MapFrom(c);
					productions.add(p);  
					c.moveToNext(); 
				}
				 
			} else {
				
				// We do not have any results. We should reload them from the API. 
				productions.addAll(mApi.getProductions());
				
				mDbHelper.runInTransaction(new IDbCommand() {

					@Override
					public void execute() {
						saveProductionsToDb(productions); 
					}
					
				}); 
				
				
				
			}
			
			
		} catch (OperationCanceledException e) {
			// TODO: Error handling.
			Log.e(TAG, e.getMessage(), e);
		} catch (AuthenticatorException e) {
			// TODO: Error handling.
			Log.e(TAG, e.getMessage(), e);
		} finally {
			if(c != null && c.isClosed() == false) {
				c.close();
			}
			close(); 
		}
		
		return productions; 
	}

	private void saveProductionsToDb(List<Production> productions) {
			for(Production production : productions) {
				saveProduction(production); 
			}	
	}

	private void saveProduction(Production production) {
		ContentValues values = new ContentValues();
		values.put(DbConstants.KEY_PROD_CAN_WATCH, production.getCan_watch());
		values.put(DbConstants.KEY_PROD_DESCRIPTION, production.getDescription()); 
		values.put(DbConstants.KEY_PROD_PRICE, production.getPrice());
		values.put(DbConstants.KEY_PROD_SLUG, production.getSlug());
		values.put(DbConstants.KEY_PROD_TITLE, production.getTitle());
		
		long rowId = mDb.insert(DbConstants.DATABASE_TABLE_PRODUCTIONS_NAME, null, values); 
		if(rowId != -1)
			production.setId(rowId);
		
		mEpisodeRepository.saveEpisodesFor(mDb, production.getId(), production.getEpisodes()); 
	}

	@Override
	public Production getProduction(long productionRowId) {
		Cursor c = null;
		try { 
			open();
			c = mDb.query(DbConstants.DATABASE_TABLE_PRODUCTIONS_NAME, new String[] {
					DbConstants.KEY_PROD_ROWID,
					DbConstants.KEY_PROD_CAN_WATCH, 
					DbConstants.KEY_PROD_DESCRIPTION, 
					DbConstants.KEY_PROD_PRICE, 
					DbConstants.KEY_PROD_SLUG, 
					DbConstants.KEY_PROD_TITLE }, 
					DbConstants.KEY_PROD_ROWID + "=" + productionRowId, 
					null, 
					null, 
					null, 
					null);
			
			if(c != null && c.isAfterLast() == false) {
				// We have results to return, load those. 
				c.moveToFirst();
				Production production = mProductionCursorMapper.MapFrom(c);
				production.setEpisodes(mEpisodeRepository.getEpisodesFrom(productionRowId));
				c.close(); 
				return production;
			}
			return new NullProduction();
			
		} finally { 
			if(c!= null && c.isClosed() == false) {
				c.close(); 
			}
			close(); 
		}
	}

	@Override
	public void deleteAllProductions() {
		open();
		mDb.beginTransaction(); 
		try{
			// Delete all episodes and productions
			mDb.delete(DbConstants.DATABASE_TABLE_EPISODES_NAME, null, null);
			mDb.delete(DbConstants.DATABASE_TABLE_PRODUCTIONS_NAME, null, null); 
			mDb.setTransactionSuccessful(); 
		} finally {
			mDb.endTransaction(); 
			close(); 
		}
		
	}
	

}
