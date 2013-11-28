package com.example.RSS_Full;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;

public class MyActivity extends Activity {
    ArrayList<channel> Channels = new ArrayList<channel>();
    ArrayAdapter<channel> arrayAdapter;
    ListView listView_channel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Channels = new ArrayList<channel>();

        final FeedsDBHelper feedsDBHelper = new FeedsDBHelper(this);

        ContentValues cv = new ContentValues();
        ContentValues cv1 = new ContentValues();
        ContentValues cv2 = new ContentValues();
        cv.put(FeedsDBHelper.CHANNEL_URL, "http://news.yandex.ru/sport.rss");
        cv1.put(FeedsDBHelper.CHANNEL_URL, "http://bash.im/rss");
        cv2.put(FeedsDBHelper.CHANNEL_URL, "http://lenta.ru/rss");
        SQLiteDatabase wdb = feedsDBHelper.getWritableDatabase();
        wdb.insert(FeedsDBHelper.DATABASE_NAME, null, cv);
        wdb.insert(FeedsDBHelper.DATABASE_NAME, null, cv1);
        wdb.insert(FeedsDBHelper.DATABASE_NAME, null, cv2);


        final Button button_add = (Button) findViewById(R.id.button_add_chnanel);
        final EditText editText_channel = (EditText) findViewById(R.id.new_channel);
        show_channels();
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Channels.add(new channel(editText_channel.getText().toString()));
                ContentValues cv = new ContentValues();
                cv.put(FeedsDBHelper.CHANNEL_URL, editText_channel.getText().toString());
                SQLiteDatabase wdb = feedsDBHelper.getWritableDatabase();
                wdb.insert(FeedsDBHelper.DATABASE_NAME, null, cv);
                show_channels();
            }
        });

        listView_channel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("URL", Channels.get(i).getUrl());
                intent.setClass(getApplicationContext(), Feeds.class);
                startActivity(intent);
            }
        });

        listView_channel.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("name", Channels.get(i).getUrl().toString());
                intent.setClass(getApplicationContext(), Change.class);

                startActivity(intent);
                show_channels();
                return true;
            }
        });
        Intent intent = new Intent(getApplicationContext(), MyIntent.class);
        intent.putExtra("all", true);
        intent.putExtra("URL", "");

        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 10000, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        show_channels();
    }

    public void show_channels() {
        Channels.clear();
        FeedsDBHelper feedsDBHelper = new FeedsDBHelper(this);
        SQLiteDatabase rdb = feedsDBHelper.getReadableDatabase();
        Cursor cursor = rdb.query(feedsDBHelper.DATABASE_NAME, null, null, null, null, null, null);
        int url_column = cursor.getColumnIndex(FeedsDBHelper.CHANNEL_URL);

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


        arrayAdapter = new ArrayAdapter<channel>(this, R.layout.list_item, Channels);

        listView_channel = (ListView) findViewById(R.id.listView_channel);
        listView_channel.setAdapter(arrayAdapter);
    }
}
