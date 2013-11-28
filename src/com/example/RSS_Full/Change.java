package com.example.RSS_Full;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Change extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change);

        TextView textView = (TextView) findViewById(R.id.Channel_change);
        textView.setText(getIntent().getStringExtra("name"));
        Button button_delete = (Button) findViewById(R.id.button_delete);
        final EditText editText = (EditText) findViewById(R.id.newChannel);
        Button button_change = (Button) findViewById(R.id.button_change);

        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FeedsDBHelper feedsDBHelper = new FeedsDBHelper(getApplicationContext());
                SQLiteDatabase rdb = feedsDBHelper.getReadableDatabase();
                SQLiteDatabase wdb = feedsDBHelper.getWritableDatabase();
                Cursor cursor = rdb.query(feedsDBHelper.DATABASE_NAME, null, null, null, null, null, null);
                int url_column = cursor.getColumnIndex(FeedsDBHelper.CHANNEL_URL);
                int id_column = cursor.getColumnIndex(FeedsDBHelper._ID);
                while (cursor.moveToNext()) {
                    if (cursor.getString(url_column).equals(getIntent().getStringExtra("name"))) {
                        wdb.delete(FeedsDBHelper.DATABASE_NAME, FeedsDBHelper._ID + "=" + cursor.getString(id_column), null);
                    }
                }
                finish();
            }
        });

        button_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FeedsDBHelper feedsDBHelper = new FeedsDBHelper(getApplicationContext());
                SQLiteDatabase rdb = feedsDBHelper.getReadableDatabase();
                SQLiteDatabase wdb = feedsDBHelper.getWritableDatabase();
                Cursor cursor = rdb.query(feedsDBHelper.DATABASE_NAME, null, null, null, null, null, null);
                int url_column = cursor.getColumnIndex(FeedsDBHelper.CHANNEL_URL);
                int id_column = cursor.getColumnIndex(FeedsDBHelper._ID);
                while (cursor.moveToNext()) {
                    if (cursor.getString(url_column).equals(getIntent().getStringExtra("name"))) {
                        wdb.delete(FeedsDBHelper.DATABASE_NAME, FeedsDBHelper._ID + "=" + cursor.getString(id_column), null);
                    }
                }
                ContentValues cv = new ContentValues();
                cv.put(FeedsDBHelper.CHANNEL_URL, editText.getText().toString());
                wdb.insert(FeedsDBHelper.DATABASE_NAME, null, cv);

                finish();
            }
        });
    }
}
