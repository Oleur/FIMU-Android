package com.fimu.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MusicGroupDBAdapter {

	/** The Constant BASE_VERSION. */
	private static final int BASE_VERSION = 1;
	
	/** The Constant DB_NAME. */
	private static final String DB_NAME = "music_group.db";
	
	/** The Constant TABLE_PICTURE. */
	private static final String TABLE_MUSIC_GROUP = "table_music_group";
	
	/** The Constant COLUMN_ID. */
	private static final String COLUMN_ID = "id";
	private static final int COLUMN_ID_ID = 0;
	
	/** The Constant COLUMN_NAME. */
	private static final String COLUMN_NAME = "name";
	private static final int COLUMN_NAME_ID = 1;
	
	/** The Constant COLUMN_STYLE. */
	private static final String COLUMN_STYLE = "style";
	private static final int COLUMN_STYLE_ID = 2;
	
	/** The Constant COLUMN_SCENE. */
	private static final String COLUMN_SCENE = "scene";
	private static final int COLUMN_SCENE_ID = 3;
	
	/** The Constant COLUMN_COUNTRY. */
	private static final String COLUMN_COUNTRY = "country";
	private static final int COLUMN_COUNTRY_ID = 4;
	
	/** The Constant COLUMN_LONGITUDE. */
	private static final String COLUMN_HOUR = "hour";
	private static final int COLUMN_HOUR_ID = 5;
	
	/** The Constant COLUMN_DATE. */
	private static final String COLUMN_DATE = "date";
	private static final int COLUMN_DATE_ID = 6;
	
	/** The my database. */
	private SQLiteDatabase myDatabase;
	
	/** The database helper. */
	private MusicGroupDatabaseHelper dbHelper;
	
	public MusicGroupDBAdapter(Context context) {
		this.dbHelper = new MusicGroupDatabaseHelper(context, DB_NAME, null, BASE_VERSION);
	}
	
	public void open() {
		myDatabase = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		myDatabase.close();
	}
	
	public SQLiteDatabase getDatabase() {
		return this.myDatabase;
	}
	
	public void dropMusicGroupTable() {
		this.myDatabase.delete(TABLE_MUSIC_GROUP, null, null);
	}
	
	public MusicGroup getMusicGroup(int id) {
		Cursor c = myDatabase.query(TABLE_MUSIC_GROUP, new String[] {
				COLUMN_ID, COLUMN_NAME, COLUMN_STYLE, COLUMN_SCENE, COLUMN_COUNTRY, COLUMN_HOUR, COLUMN_DATE
		}, COLUMN_ID + "=" + id, null, null, null, null);
		return cursorToMusicGroup(c);
	}
	
	public ArrayList<MusicGroup> getAllMusicGroupOrderByTime() {
		Cursor c = myDatabase.query(TABLE_MUSIC_GROUP, new String[] {
				COLUMN_ID, COLUMN_NAME, COLUMN_STYLE, COLUMN_SCENE, COLUMN_COUNTRY, COLUMN_HOUR, COLUMN_DATE
		}, null, null, null, null, COLUMN_DATE+" ASC, "+COLUMN_HOUR+" ASC");
		return cursorToMusicGroups(c);
	}
	
	public ArrayList<MusicGroup> getAllMusicGroupOrderByName() {
		Cursor c = myDatabase.query(TABLE_MUSIC_GROUP, new String[] {
				COLUMN_ID, COLUMN_NAME, COLUMN_STYLE, COLUMN_SCENE, COLUMN_COUNTRY, COLUMN_HOUR, COLUMN_DATE
		}, null, null, null, null, COLUMN_NAME);
		return cursorToMusicGroups(c);
	}
	
	public ArrayList<MusicGroup> getAllMusicGroups() {
		Cursor c = myDatabase.query(TABLE_MUSIC_GROUP, new String[] {
				COLUMN_ID, COLUMN_NAME, COLUMN_STYLE, COLUMN_SCENE, COLUMN_COUNTRY, COLUMN_HOUR, COLUMN_DATE
		}, null, null, null, null, null);
		return cursorToMusicGroups(c);
	}
	
	public void insert(MusicGroup musicGroup) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, musicGroup.getGroupName());
		values.put(COLUMN_STYLE, musicGroup.getMusicStyle());
		values.put(COLUMN_SCENE, musicGroup.getScene());
		values.put(COLUMN_COUNTRY, musicGroup.getCountry());
		values.put(COLUMN_HOUR, musicGroup.getHour());
		values.put(COLUMN_DATE, musicGroup.getDate());
		myDatabase.insert(TABLE_MUSIC_GROUP, null, values);
	}
	
	public void updateMusicGroup(int id, MusicGroup mGroup) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, mGroup.getGroupName());
		values.put(COLUMN_STYLE, mGroup.getMusicStyle());
		values.put(COLUMN_SCENE, mGroup.getScene());
		values.put(COLUMN_COUNTRY, mGroup.getCountry());
		values.put(COLUMN_HOUR, mGroup.getHour());
		values.put(COLUMN_DATE, mGroup.getDate());
		myDatabase.update(TABLE_MUSIC_GROUP, values, COLUMN_ID + " = " + id, null);
	}
	
	public void removeConcert(int id) {
		myDatabase.delete(TABLE_MUSIC_GROUP, COLUMN_ID + " = " + id, null);
	}
	
	public void removeConcert(String name, String date, String hour) {
		myDatabase.delete(TABLE_MUSIC_GROUP, COLUMN_NAME + " =? AND "
				+COLUMN_DATE+" =? AND "+COLUMN_HOUR+" =?", new String[] {name, date, hour});
	}

	private ArrayList<MusicGroup> cursorToMusicGroups(Cursor c) {
		if (c.getCount() == 0) {
			return null;
		}
		
		ArrayList<MusicGroup> mGroups = new ArrayList<MusicGroup>(c.getCount());
		c.moveToFirst();
		do {
			MusicGroup mgroup = new MusicGroup();
			mgroup.setId(c.getInt(COLUMN_ID_ID));
			mgroup.setGroupName(c.getString(COLUMN_NAME_ID));
			mgroup.setMusicStyle(c.getString(COLUMN_STYLE_ID));
			mgroup.setScene(c.getString(COLUMN_SCENE_ID));
			mgroup.setCountry(c.getString(COLUMN_COUNTRY_ID));
			mgroup.setHour(c.getString(COLUMN_HOUR_ID));
			mgroup.setDate(c.getString(COLUMN_DATE_ID));
			mGroups.add(mgroup);
		} while (c.moveToNext());
		
		c.close();
		return mGroups;
	}

	private MusicGroup cursorToMusicGroup(Cursor c) {
		if (c.getCount() == 0) {
			return null;
		}
		c.moveToFirst();
		MusicGroup mgroup = new MusicGroup();
		//Extract values from the cursor
		mgroup.setId(c.getInt(COLUMN_ID_ID));
		mgroup.setGroupName(c.getString(COLUMN_NAME_ID));
		mgroup.setMusicStyle(c.getString(COLUMN_STYLE_ID));
		mgroup.setScene(c.getString(COLUMN_SCENE_ID));
		mgroup.setCountry(c.getString(COLUMN_COUNTRY_ID));
		mgroup.setHour(c.getString(COLUMN_HOUR_ID));
		mgroup.setDate(c.getString(COLUMN_DATE_ID));
		//Close the cursor to free the data.
		c.close();
		return mgroup;
	}
}
