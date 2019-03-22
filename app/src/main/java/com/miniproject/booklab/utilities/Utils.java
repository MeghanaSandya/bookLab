package com.miniproject.booklab.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.miniproject.booklab.BookApplication;
import com.miniproject.booklab.model.Book;

import java.util.ArrayList;
import java.util.Map;

public class Utils {
    private static String FAV_BOOK_PREFS = "fav_books";

    // UI constants
    public static final String FAVOURITED = "Favourited";
    public static final String UN_FAVOURITED = "Un Favourited";
    public static final String NO_INTERNET_CONNECTION = "No internet connection";
    public static final String NETWORK_ERROR = "Some error occurred ";
    public static final String INVALID_BOOK_INPUT = "Invalid Book Name";
    public static final String RESPONSE_ERROR = "Something went wrong, Please try again";

    // parsing keys
    public static final String DOCS = "docs";
    public static final String AUTHOR_NAME = "author_name";
    public static final String TITLE = "title";
    public static final String SUGGESTED_TITLE = "title_suggest";
    public static final String COVER_EDITION_KEY = "cover_edition_key";
    public static final String EDITION_KEY = "edition_key";
    public static final String PUBLISHER = "publishers";
    public static final String NO_OF_PAGES = "number_of_pages";
    public static final String ISBN_KEY = "isbn";
    public static final String PUBLISHED_YER = "first_publish_year";

    public static final String SHARE_FORMAT = "Checkout %s by %s\n%s";
    public static final String SHARE_SUBJECT = "Shared from BookLab";
    public static final String SHARE_TEXT = "Share Book!!!";

    public static void storeBook(String key, Book book) {
        Context context = BookApplication.getAppContext();
        SharedPreferences.Editor editor = context.getSharedPreferences(FAV_BOOK_PREFS, Context.MODE_PRIVATE).edit();
        String value = BookApplication.getGsonInstance().toJson(book);
        editor.putString(key, value);
        editor.apply();
    }

    public static ArrayList<Book> getAllBooks() {
        Context context = BookApplication.getAppContext();
        SharedPreferences prefs = context.getSharedPreferences(FAV_BOOK_PREFS, Context.MODE_PRIVATE);
        ArrayList<Book> isbnValueList = new ArrayList<>();
        for (Map.Entry<String, ?> entry : prefs.getAll().entrySet()) {
            String value = entry.getValue().toString();
            Book book = BookApplication.getGsonInstance().fromJson(value, Book.class);
            isbnValueList.add(book);
        }
        return isbnValueList;
    }

    public static String getISBNValue(String key) {
        Context context = BookApplication.getAppContext();
        SharedPreferences prefs = context.getSharedPreferences(FAV_BOOK_PREFS, Context.MODE_PRIVATE);
        return prefs.getString(key, "");
    }

    public static void removeISBNValue(String key) {
        Context context = BookApplication.getAppContext();
        SharedPreferences prefs = context.getSharedPreferences(FAV_BOOK_PREFS, Context.MODE_PRIVATE);
        prefs.edit().remove(key).apply();
    }

    public static void displayNetworkError(VolleyError error) {
        Context context = BookApplication.getAppContext();
        String text = (error instanceof NoConnectionError) ? NO_INTERNET_CONNECTION : NETWORK_ERROR + error.getMessage();
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
}
