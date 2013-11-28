package com.example.RSS_Full;

import java.util.Date;

public class Record {
    public String title;
    public String link;
    public String description;

    public Record() {
        title = "";
        link = "";
        description = "";
    }

    public Record(String _title, String _link, String _description) {
        title = _title;
        link = _link;
        description = _description;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String toString() {
        return title;
    }
}
