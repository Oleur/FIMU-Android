package com.fimu.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MusicGroupDatabaseHelper extends SQLiteOpenHelper {
	
	/** The Constant TABLE_MUSIC_GROUP. */
	private static final String TABLE_MUSIC_GROUP = "table_music_group";
	
	/** The Constant COLUMN_ID. */
	private static final String COLUMN_ID = "id";
	
	/** The Constant COLUMN_NAME. */
	private static final String COLUMN_NAME = "name";
	
	/** The Constant COLUMN_STYLE. */
	private static final String COLUMN_STYLE = "style";
	
	/** The Constant COLUMN_SCENE. */
	private static final String COLUMN_SCENE = "scene";
	
	/** The Constant COLUMN_COUNTRY. */
	private static final String COLUMN_COUNTRY = "country";
	
	/** The Constant COLUMN_LONGITUDE. */
	private static final String COLUMN_HOUR = "hour";
	
	/** The Constant COLUMN_DATE. */
	private static final String COLUMN_DATE = "date";

	/** The Constant REQUEST_CREATION_DB. */
	private static final String REQUEST_CREATION_DB = "CREATE TABLE "
			+ TABLE_MUSIC_GROUP + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_NAME + " TEXT NOT NULL, " + COLUMN_STYLE + " TEXT, " + COLUMN_SCENE + " TEXT NOT NULL, "
			+ COLUMN_COUNTRY + " TEXT NOT NULL, " + COLUMN_DATE + " TEXT NOT NULL, "
			+ COLUMN_HOUR + " TEXT NOT NULL);";
	
	public MusicGroupDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(REQUEST_CREATION_DB);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE " + REQUEST_CREATION_DB + ";");
		onCreate(db);
	}

}
