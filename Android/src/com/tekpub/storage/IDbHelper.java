package com.tekpub.storage;

import android.database.sqlite.SQLiteDatabase;

public interface IDbHelper {
	SQLiteDatabase open(); 
	void close();
	void runInTransaction(IDbCommand command); 
}
