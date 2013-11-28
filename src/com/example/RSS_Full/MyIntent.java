package com.example.RSS_Full;


import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;


public class MyIntent extends IntentService {
    public static final String upd_action = "upd_action";

    public MyIntent(String name) {
        super(name);
    }

    public MyIntent() {
        super("d");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String url_channel = intent.getStringExtra("URL");
        boolean flag = intent.getBooleanExtra("all", false);
        if (flag == false) {
            FeedsDBHelper fdb = new FeedsDBHelper(getApplicationContext());
            SQLiteDatabase wdb = fdb.getWritableDatabase();

            Cursor cursor = wdb.query(FeedsDBHelper.DATABASE_NAME, null, null, null, null, null, null);
            int url_column = cursor.getColumnIndex(FeedsDBHelper.CHANNEL_URL);
            int id_column = cursor.getColumnIndex(FeedsDBHelper._ID);
            try {
                while (cursor.moveToNext()) {
                    if (cursor.getString(url_column).equals(url_channel)) {
                        wdb.delete(FeedsDBHelper.DATABASE_NAME, FeedsDBHelper._ID + "=" + cursor.getString(id_column), null);
                    }
                }
            } catch (Exception e) {

            }
            ContentValues cv = new ContentValues();
            cv.put(FeedsDBHelper.CHANNEL_URL, url_channel);
            wdb.insert(FeedsDBHelper.DATABASE_NAME, null, cv);

            refresh_feeds rf = new refresh_feeds(getApplicationContext());
            rf.update_channel(url_channel);
            Intent intent1 = new Intent();
            intent1.setAction(upd_action + "_" + url_channel);
            sendBroadcast(intent1);
        } else {
            FeedsDBHelper fdb = new FeedsDBHelper(getApplicationContext());
            SQLiteDatabase wdb = fdb.getWritableDatabase();

            Cursor cursor = wdb.query(FeedsDBHelper.DATABASE_NAME, null, null, null, null, null, null);

            int url_column = cursor.getColumnIndex(FeedsDBHelper.CHANNEL_URL);
            ArrayList<channel> Channels = new ArrayList<channel>();
            while (cursor.moveToNext()) {
                if (cursor.getString(url_column) != null && cursor.getString(url_column) != "") {
                    boolean f = false;
                    for (int i = 0; i < Channels.size(); i++)
                        if (Channels.get(i).getUrl().equals(cursor.getString(url_column))) f = true;
                    if (!f) {
                        Channels.add(new channel(cursor.getString(url_column)));
                    }
                }
            }
            for (int i = 0; i < Channels.size(); i++) {
                ContentValues cv = new ContentValues();
                cv.put(FeedsDBHelper.CHANNEL_URL, Channels.get(i).getUrl());
                wdb.insert(FeedsDBHelper.DATABASE_NAME, null, cv);

                refresh_feeds rf = new refresh_feeds(getApplicationContext());
                rf.update_channel(Channels.get(i).getUrl());
                Intent intent1 = new Intent();
                intent1.setAction(upd_action + "_" + Channels.get(i).getUrl());
                sendBroadcast(intent1);
            }
        }

    }


}
