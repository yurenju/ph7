package org.ph7;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper {
	public static final String REPORTS_TABLE_NAME = "reports";
	public static final String REPORT = "report";
	
	private static final String DATABASE_NAME = "ph7_reports.db";
	private static final int DATABASE_VERSION = 2;
	private static final String REPORTS_TABLE_CREATE = 
		"CREATE TABLE " + REPORTS_TABLE_NAME + " (" +
		"local_id INTEGER PRIMARY KEY," +
		"remote_id INTEGER," +
		"remote_parent_id INTEGER," +
		"location TEXT," +
		"issue_type INTEGER," +
		"comment TEXT," +
		"longitude REAL," +
		"latitude REAL," +
		"accuracy REAL," +
		"image_filename TEXT," +
		"created_time INTEGER);";
		

	public DbOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(REPORTS_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + REPORTS_TABLE_NAME);
		onCreate(db);
	}

}
