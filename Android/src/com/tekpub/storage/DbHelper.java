package com.tekpub.storage;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class DbHelper extends SQLiteOpenHelper implements IDbHelper {

	private static final String DB_NAME = "tekpub";
	private static final int DB_VERSION = 1; 
	private SQLiteDatabase mDb; 
	private String TAG = DbHelper.class.getName(); 
	
	//@Inject protected static Provider<Context> contextProvider; 
	
	@Inject
	public DbHelper(Provider<Application> contextProvider) {
		super(contextProvider.get(), DB_NAME, null, DB_VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		 db.execSQL(DbConstants.DATABASE_CREATE_PRODUCTIONS_TABLE);
		 db.execSQL(DbConstants.DATABASE_CREATE_EPISODES_TABLE); 
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Nothing to do here, no need to upgrade anything. 
		// Once we decide we need to upgrade the db, the alter/update scripts will be
		// executed here. 
	}

	@Override
	public SQLiteDatabase open() {
		mDb = getWritableDatabase();
		return mDb; 
	}

	@Override
	public void runInTransaction(IDbCommand command) {
		if(mDb != null & mDb.isOpen()) {
			Log.d(TAG, "Running in transaction, mDb was not null and was open." );
			try {
				mDb.beginTransaction(); 
				command.execute(); 
				mDb.setTransactionSuccessful(); 
			} finally {
				mDb.endTransaction(); 
			}
		} else {
			Log.d(TAG, "Could not run in transaction, DB was closed.");
		}
		
	}


	


	
}