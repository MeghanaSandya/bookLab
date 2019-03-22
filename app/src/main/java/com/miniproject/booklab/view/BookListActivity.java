package com.miniproject.booklab.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.miniproject.booklab.R;
import com.miniproject.booklab.model.Book;
import com.miniproject.booklab.model.NetworkManager;
import com.miniproject.booklab.utilities.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BookListActivity extends AppCompatActivity implements BookAdapter.ItemClickListener {

    private static final String TAG = BookListActivity.class.getSimpleName();
    private static final String BASE_URL = "http://openlibrary.org/search.json?title=%s";
    private BookAdapter bookAdapter;
    private TextView searchTextView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        recyclerView = findViewById(R.id.recyclerView);
        searchTextView = findViewById(R.id.textView);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycler_view_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookAdapter = new BookAdapter();
        recyclerView.setAdapter(bookAdapter);
        bookAdapter.setItemClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        bookAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        final MenuItem item = menu.findItem(R.id.menu_search);

        final SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String url = String.format(BASE_URL, query);
                fetchBooks(url);
                searchView.setQuery("", true);
                searchView.setIconified(true);
                item.collapseActionView();
                setTitle(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_item_favorite:
                Intent intent = new Intent(this, FavoritesBookActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    private void fetchBooks(String url) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Data ...!");
        progressDialog.show();

        NetworkManager.getInstance().getNetworkRequester(this, Request.Method.GET, url,
                new NetworkManager.NetworkResponseListener() {
                    @Override
                    public void onResponse(String response) {
                        prepareData(response, progressDialog);
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressDialog.dismiss();
                        Utils.displayNetworkError(error);
                        error.printStackTrace();
                    }
                });
    }

    private void prepareData(String response, ProgressDialog progressDialog) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray docsArray = jsonObject.getJSONArray(Utils.DOCS);
            List<Book> bookList = new ArrayList<>();

            if (docsArray.length() > 0) {
                for (int i = 0; i < docsArray.length(); i++) {
                    JSONObject documentObject = docsArray.getJSONObject(i);
                    String authorName = "";
                    if (documentObject.optJSONArray(Utils.AUTHOR_NAME) != null) {
                        authorName = documentObject.getJSONArray(Utils.AUTHOR_NAME).getString(0);
                    }

                    String title = "";
                    if (documentObject.has(Utils.TITLE)) {
                        title = documentObject.getString(Utils.TITLE);
                    } else if (documentObject.has(Utils.SUGGESTED_TITLE)) {
                        title = documentObject.getString(Utils.SUGGESTED_TITLE);
                    }

                    String coverEditionKey = "";
                    if (documentObject.has(Utils.COVER_EDITION_KEY)) {
                        coverEditionKey = documentObject.getString(Utils.COVER_EDITION_KEY);
                    } else if (documentObject.optJSONArray(Utils.EDITION_KEY) != null) {
                        coverEditionKey = documentObject.getJSONArray(Utils.EDITION_KEY).getString(0);
                    }

                    String isbnKey = "";
                    if (documentObject.optJSONArray(Utils.ISBN_KEY) != null) {
                        isbnKey = documentObject.getJSONArray(Utils.ISBN_KEY).getString(0);
                    }
                    int publishedYear = 0;
                    if (documentObject.has(Utils.PUBLISHED_YER)) {
                        publishedYear = documentObject.getInt(Utils.PUBLISHED_YER);
                    }
                    Book item = new Book(title, authorName, coverEditionKey, isbnKey, publishedYear);
                    bookList.add(item);
                }
                searchTextView.setVisibility(View.GONE);
                progressDialog.dismiss();
                recyclerView.setVisibility(View.VISIBLE);
                bookAdapter.setListItems(bookList);
            } else {
                Toast.makeText(getParent(), Utils.INVALID_BOOK_INPUT, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            progressDialog.dismiss();
            e.printStackTrace();
            Toast.makeText(getParent(), Utils.RESPONSE_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(int position, View view, Book book) {
        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.putExtra("book", book);
        startActivity(intent);
    }
}

