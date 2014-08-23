package com.tekpub.storage;

public class DbConstants {
	public static final String DATABASE_TABLE_PRODUCTIONS_NAME = "productions"; 
	// Columns
	public static final String KEY_PROD_ROWID = "_id";
	public static final String KEY_PROD_CAN_WATCH = "can_watch";
	public static final String KEY_PROD_DESCRIPTION = "description";
	public static final String KEY_PROD_PRICE = "price";
	public static final String KEY_PROD_SLUG = "slug";
	public static final String KEY_PROD_TITLE = "title";
	
	public static final String DATABASE_TABLE_EPISODES_NAME = "episodes"; 
	// Columns 
	public static final String KEY_EPISODES_ROWID = "_id";
	public static final String KEY_EPISODES_EPISODE_ID = "id"; 
	public static final String KEY_EPISODES_PRODUCTION_ID = "production_id"; 
	public static final String KEY_EPISODES_RELEASED_AT = "released_at"; 
	public static final String KEY_EPISODES_CREATED_AT = "created_at";
	public static final String KEY_EPISODES_UPDATED_AT = "updated_at"; 
	public static final String KEY_EPISODES_TITLE = "title"; 
	public static final String KEY_EPISODES_DESCRIPTION = "description";
	public static final String KEY_EPISODES_DURATION = "duration";
	public static final String KEY_EPISODES_WIDTH = "width"; 
	public static final String KEY_EPISODES_HEIGHT = "height"; 
	public static final String KEY_EPISODES_NOTES = "notes";
	public static final String KEY_EPISODES_NUMBER = "number";
	public static final String KEY_EPISODES_SLUG = "slug"; 
	public static final String KEY_EPISODES_IS_DOWNLOADING = "is_downloading"; 
	public static final String KEY_EPISODES_DOWNLOADS = "downloads"; 
	public static final String KEY_EPISODES_OVERRIDE_URL = "override_url"; 
	public static final String KEY_EPISODES_CHANNEL_SLUG = "channel_slug"; 
	public static final String KEY_EPISODES_VIEWS = "views"; 
	
	public static final String[] EPISODE_FIELDS = new String[] {
		KEY_EPISODES_ROWID,
		KEY_EPISODES_EPISODE_ID,
		KEY_EPISODES_PRODUCTION_ID,
		KEY_EPISODES_RELEASED_AT,
		KEY_EPISODES_CREATED_AT,
		KEY_EPISODES_UPDATED_AT,
		KEY_EPISODES_TITLE,
		KEY_EPISODES_DESCRIPTION,
		KEY_EPISODES_DURATION,
		KEY_EPISODES_WIDTH,
		KEY_EPISODES_HEIGHT,
		KEY_EPISODES_NOTES,
		KEY_EPISODES_NUMBER,
		KEY_EPISODES_SLUG,
		KEY_EPISODES_IS_DOWNLOADING,
		KEY_EPISODES_DOWNLOADS,
		KEY_EPISODES_OVERRIDE_URL,
		KEY_EPISODES_CHANNEL_SLUG,
		KEY_EPISODES_VIEWS
	};
	
	public static final String DATABASE_CREATE_EPISODES_TABLE = 
		"CREATE TABLE " +  DATABASE_TABLE_EPISODES_NAME + "("
			+ KEY_EPISODES_ROWID + " integer primary key autoincrement, "
			+ KEY_EPISODES_EPISODE_ID + " text not null, " 
			+ KEY_EPISODES_PRODUCTION_ID + " text not null, " 
			+ KEY_EPISODES_RELEASED_AT + " text not null, " 
			+ KEY_EPISODES_CREATED_AT + " text not null, " 
			+ KEY_EPISODES_UPDATED_AT + " text not null, "
			+ KEY_EPISODES_TITLE + " text not null, "
			+ KEY_EPISODES_DESCRIPTION + " text not null, " 
			+ KEY_EPISODES_DURATION + " int not null, " 
			+ KEY_EPISODES_WIDTH + " int not null, "
			+ KEY_EPISODES_HEIGHT + " int not null, "
			+ KEY_EPISODES_NOTES + " text null, "
			+ KEY_EPISODES_NUMBER + " int not null, "
			+ KEY_EPISODES_SLUG + " text not null, "
			+ KEY_EPISODES_IS_DOWNLOADING + " int DEFAULT 0, "
			+ KEY_EPISODES_DOWNLOADS + " int DEFAULT 0, "
			+ KEY_EPISODES_OVERRIDE_URL + " text null, "
			+ KEY_EPISODES_CHANNEL_SLUG + " text null, "
			+ KEY_EPISODES_VIEWS + " int DEFAULT 0, "
			+ "FOREIGN KEY(" + KEY_EPISODES_PRODUCTION_ID + ") REFERENCES " + DATABASE_TABLE_PRODUCTIONS_NAME + "(" + KEY_PROD_ROWID + "));"; 
	
	public static final String DATABASE_CREATE_PRODUCTIONS_TABLE = 
		"CREATE TABLE " + DATABASE_TABLE_PRODUCTIONS_NAME  + "("
			+ KEY_PROD_ROWID + " integer primary key autoincrement, "
			+ KEY_PROD_CAN_WATCH + " int null, " 
			+ KEY_PROD_DESCRIPTION + " text null, " 
			+ KEY_PROD_PRICE + " real null, " 
			+ KEY_PROD_SLUG + " text null, "
			+ KEY_PROD_TITLE + " text not null);"; 
}
