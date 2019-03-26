package com.miniproject.booklab.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.miniproject.booklab.R;
import com.miniproject.booklab.model.Book;
import com.miniproject.booklab.utilities.Utils;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavoritesBookActivity extends AppCompatActivity implements BookAdapter.ItemClickListener {
    private RecyclerView mRecyclerView;
    private BookAdapter mBookAdapter;
    private ImageView noFavoritesImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        setTitle(R.string.menu_favorite);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        mRecyclerView = findViewById(R.id.recyclerView);
        noFavoritesImage = findViewById(R.id.no_favorites);
        TextView welcomeMessageView = findViewById(R.id.textView);
        welcomeMessageView.setVisibility(View.GONE);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycler_view_divider));
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBookAdapter = new BookAdapter();
        mRecyclerView.setAdapter(mBookAdapter);
        mBookAdapter.setItemClickListener(this);
        displayBooks();
    }

    private void displayBooks() {
        ArrayList<Book> bookList = Utils.getAllBooks();
        if (!bookList.isEmpty()) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mBookAdapter.setListItems(bookList);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            noFavoritesImage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayBooks();
        mBookAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position, View view, Book book) {
        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.putExtra("book", book);
        startActivity(intent);
    }

    @Override
    public void notifyAdapter() {
        displayBooks();
        if (mBookAdapter != null) {
            mBookAdapter.notifyDataSetChanged();
        }
    }
}
