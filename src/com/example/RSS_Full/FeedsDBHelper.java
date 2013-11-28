package com.example.RSS_Full;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FeedsDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    public static final String _ID = "_id";
    public static final String DATABASE_NAME = "feedsdb";
    public static final String CHANNEL_URL = "channel_url";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";

    public static final String CREATE_DATABASE = "CREATE TABLE " + DATABASE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + CHANNEL_URL + " INTEGER," + TITLE + " TEXT," + DESCRIPTION + " TEXT);";

    public static final String DROP_DATABASE = "DROP TABLE IF EXISTS " + DATABASE_NAME;

    public FeedsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old_v, int new_v) {
        if (old_v != new_v) {
            db.execSQL(DROP_DATABASE);
            onCreate(db);
        }
    }
}
