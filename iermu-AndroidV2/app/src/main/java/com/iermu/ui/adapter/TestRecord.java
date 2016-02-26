package com.iermu.ui.adapter;

/** This is just a simple class for holding data that is used to render our custom view */
public class TestRecord {
    private String imageUrl;
    private String time;

    public TestRecord(String imageUrl, String time) {
        this.imageUrl = imageUrl;
        this.time = time;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
