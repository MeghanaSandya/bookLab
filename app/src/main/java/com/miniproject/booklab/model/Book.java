package com.miniproject.booklab.model;

import java.io.Serializable;

public class Book implements Serializable {

    private final static String BASE_URL = "http://covers.openlibrary.org/b/olid/";
    private final static String COVER_URL_EXTENSION = "-M.jpg?default=false";
    private final static String LARGE_COVER_URL_EXTENSION = "-L.jpg?default=false";
    private String mAuthorName;
    private String mTitle;
    private String mCoverEditionKey;
    private String mIsbn;
    private int mPublishedYear;


    public Book(String title, String authorName, String coverEditionKey, String isbn, int publishedYear) {
        this.mAuthorName = authorName;
        this.mTitle = title;
        this.mCoverEditionKey = coverEditionKey;
        this.mIsbn = isbn;
        this.mPublishedYear = publishedYear;
    }

    public String getAuthorName() {
        return mAuthorName;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getPublishedYear() {
        return mPublishedYear;
    }

    public String getCoverEditionKey() {
        return mCoverEditionKey;
    }

    public String getIsbn() {
        return mIsbn;
    }

    public String getCoverUrl() {
        return BASE_URL + mCoverEditionKey + COVER_URL_EXTENSION;
    }

    public String getLargeCoverUrl() {
        return BASE_URL + mCoverEditionKey + LARGE_COVER_URL_EXTENSION;
    }

}