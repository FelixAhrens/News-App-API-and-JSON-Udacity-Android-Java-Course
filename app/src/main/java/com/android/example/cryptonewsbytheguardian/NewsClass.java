package com.android.example.cryptonewsbytheguardian;

import java.util.Date;

public class NewsClass {

    private String mTitle;
    private String mAuthor;
    private String mSection;
    private String mDate;
    private String mUrl;

    //with all
    public NewsClass(String section, String title, String author, String date, String url) {
        mTitle = title;
        mAuthor = author;
        mSection = section;
        mDate = date;
        mUrl = url;
    }

    //sectionId, title, author and url
    public NewsClass(String section, String title, String author, String url) {
        mSection = section;
        mTitle = title;
        mAuthor = author;
        mUrl = url;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getSection() {
        return mSection;
    }

    public String getDate() {
        return mDate;
    }

    public String getUrl() {
        return mUrl;
    }


}