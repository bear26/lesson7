package com.example.RSS_Full;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.jar.Attributes;

public class Feeds extends Activity {
    ArrayList<Record> Records;
    ArrayAdapter<Record> arrayAdapter;
    ListView listView;
    String url_channel;
    long old_time=System.currentTimeMillis()-10000;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_feed);

        url_channel = getIntent().getStringExtra("URL");
        show_feeds();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("description", Records.get(i).getDescription());
                intent.setClass(getApplicationContext(), Feed.class);
                startActivity(intent);
            }
        });
        final Button button_refresh = (Button) findViewById(R.id.refresh_channel);
        button_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (System.currentTimeMillis()-old_time>=10000)
                {
                    old_time=System.currentTimeMillis();
                    Records.clear();
                    listView.setAdapter(arrayAdapter);
                    Intent intent = new Intent(getApplicationContext(), MyIntent.class);
                    intent.putExtra("URL", url_channel);
                    intent.putExtra("all", false);
                    startService(intent);
                }
            }
        });

    }

    public void show_feeds() {
        TextView textView = (TextView) findViewById(R.id.this_channel);
        textView.setText(url_channel);
        url_channel = getIntent().getStringExtra("URL");
        listView = (ListView) findViewById(R.id.listView_feed);
        Records = new ArrayList<Record>();
        FeedsDBHelper feedsDBHelper = new FeedsDBHelper(this);
        SQLiteDatabase rdb = feedsDBHelper.getReadableDatabase();
        Cursor cursor = rdb.query(feedsDBHelper.DATABASE_NAME, null, null, null, null, null, null);
        int url_column = cursor.getColumnIndex(FeedsDBHelper.CHANNEL_URL);
        int title_column = cursor.getColumnIndex(FeedsDBHelper.TITLE);
        int description_column = cursor.getColumnIndex(FeedsDBHelper.DESCRIPTION);
        try {
            while (cursor.moveToNext()) {
                if (cursor.getString(url_column).equals(url_channel) && cursor.getString(title_column) != null && cursor.getString(description_column) != null) {
                    Records.add(new Record(cursor.getString(title_column), null, cursor.getString(description_column)));
                }
            }
        } catch (Exception e) {

        }
        cursor.close();
        arrayAdapter = new ArrayAdapter<Record>(this, R.layout.list_item, Records);
        listView.setAdapter(arrayAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyIntent.upd_action + "_" + url_channel);
        registerReceiver(broadcastReceiver, intentFilter);
        show_feeds();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            show_feeds();
            Toast.makeText(context, "update completed", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onPause() {
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }
}
