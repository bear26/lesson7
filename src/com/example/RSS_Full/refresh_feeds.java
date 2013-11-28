package com.example.RSS_Full;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class refresh_feeds {

    ArrayList<Record> Records = new ArrayList<Record>();
    Context context;
    String encoding = "";

    class MySAXParser extends DefaultHandler {
        String element = null;
        Record record = new Record();
        boolean flag = false;

        @Override
        public void startDocument() {
            Records = new ArrayList<Record>();
        }

        @Override
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            element = localName;
            if (localName.equals("item") || localName.equals("entry")) {
                record = new Record();
                Records.add(new Record());
                flag = true;
            }
        }

        @Override
        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            if (localName.equals("item") || localName.equals("entry")) {
                flag = false;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String s = new String(ch, start, length);

            String v = null;
            try {
                v = new String(s.getBytes("ISO-8859-1"), encoding);
            } catch (UnsupportedEncodingException e) {
            }
            if (Records.size() > 0) {
                if (element.equals("title")) {
                    Records.get(Records.size() - 1).title += v;
                } else if (element.equals("link")) {
                    Records.get(Records.size() - 1).link += v;
                } else if (element.equals("description") || element.equals("summary")) {
                    Records.get(Records.size() - 1).description += v;
                }
            }
        }
    }

    void download(String url_S) throws ParserConfigurationException, SAXException, IOException {
        URL url;
        URLConnection conn = null;
        InputStream inputStream = null;
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        XMLReader xmlReader = parser.getXMLReader();
        xmlReader.setContentHandler(new MySAXParser());
        boolean f = false;
        try {
            encoding = "utf-8";
            url = new URL(url_S);
            conn = url.openConnection();
            conn.setConnectTimeout(15000);
            conn.connect();
            f = true;
            inputStream = conn.getInputStream();
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, "ISO-8859-1");
                boolean flag = false;
                encoding = "";
                while (!flag) {
                    String a = scanner.nextLine();
                    if (!a.contains("encoding=")) continue;
                    for (int i = a.indexOf("encoding=") + "encoding=".length() + 1; ; i++) {

                        if (a.charAt(i) == '"') {
                            flag = true;
                            break;
                        }
                        encoding += a.charAt(i);
                    }
                }
                scanner.close();
            }
            inputStream.close();
            conn = url.openConnection();
            conn.connect();
            inputStream = conn.getInputStream();
            if (inputStream != null) {
                InputSource inputSource = new InputSource(inputStream);
                inputSource.setEncoding("ISO-8859-1");
                xmlReader.parse(inputSource);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (f == true) {
                if (inputStream != null)
                    inputStream.close();

            }

        }
    }

    public refresh_feeds(Context c) {
        context = c;
    }

    public void update_channel(String url) {
        FeedsDBHelper feedsDB = new FeedsDBHelper(context);
        SQLiteDatabase sqLiteDatabase = feedsDB.getWritableDatabase();
        try {
            download(url);
            for (int i = 0; i < Records.size(); i++) {
                ContentValues cv = new ContentValues();
                cv.put(FeedsDBHelper.CHANNEL_URL, url);
                cv.put(FeedsDBHelper.TITLE, Records.get(i).getTitle());
                cv.put(FeedsDBHelper.DESCRIPTION, Records.get(i).getDescription());
                if (Records.get(i).getTitle() != null && Records.get(i).getDescription() != null && Records.get(i).getTitle() != "")
                    sqLiteDatabase.insert(FeedsDBHelper.DATABASE_NAME, null, cv);
            }
        } catch (ParserConfigurationException e) {

        } catch (SAXException e) {

        } catch (IOException e) {

        } finally {
            sqLiteDatabase.close();
            feedsDB.close();
        }
    }

}
