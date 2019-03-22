package com.miniproject.booklab.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.miniproject.booklab.R;
import com.miniproject.booklab.model.Book;
import com.miniproject.booklab.model.ImageLoaderManager;
import com.miniproject.booklab.model.NetworkManager;
import com.miniproject.booklab.utilities.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class BookDetailActivity extends AppCompatActivity {

    private static final String Base_URL = "http://openlibrary.org/books/%s.json";
    private ImageLoader mImageLoader;
    private TextView mTitle, mPublishedBy, mPageCount, authorName;
    private Book mBook;
    private NetworkImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mImage = findViewById(R.id.coverImage);
        mTitle = findViewById(R.id.title);
        authorName = findViewById(R.id.authorName);
        mPublishedBy = findViewById(R.id.publishedBy);
        mPageCount = findViewById(R.id.pageNo);
        mImageLoader = ImageLoaderManager.getInstance().getImageLoader();
        mBook = (Book) getIntent().getSerializableExtra("book");
        String coverEditionKey = mBook.getCoverEditionKey();
        String url = String.format(Base_URL, coverEditionKey);
        fetchBookDetails(url);
        setTitle(mBook.getTitle());
    }


    private void fetchBookDetails(String url) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Data ...!");
        progressDialog.show();

        NetworkManager.getInstance().getNetworkRequester(this, Request.Method.GET, url,
                new NetworkManager.NetworkResponseListener() {
                    @Override
                    public void onResponse(String response) {
                        updateUI(response);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(VolleyError error) {
                        error.printStackTrace();
                        Utils.displayNetworkError(error);
                        progressDialog.dismiss();
                    }
                });
    }

    private void updateUI(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String publisher;
            loadImage(mBook.getLargeCoverUrl(), mImage);
            mTitle.setText(mBook.getTitle());
            authorName.setText(mBook.getAuthorName());

            if (jsonObject.optJSONArray(Utils.PUBLISHER) != null) {
                publisher = jsonObject.getJSONArray(Utils.PUBLISHER).getString(0);
                publisher = getString(R.string.published_by) + " " + publisher;
                mPublishedBy.setText(publisher);
            } else {
                mPublishedBy.setVisibility(View.GONE);
            }

            if (jsonObject.has(Utils.NO_OF_PAGES)) {
                int pageCount = jsonObject.getInt(Utils.NO_OF_PAGES);
                String pagesInfo = getString(R.string.no_of_pages) + " " + String.valueOf(pageCount);
                mPageCount.setText(pagesInfo);
            } else {
                mPageCount.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadImage(String url, NetworkImageView image) {
        mImageLoader.get(url, ImageLoader.getImageListener(image,
                R.drawable.default_book_image, R.drawable.ic_nocover_image));
        image.setImageUrl(url, mImageLoader);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.book_detail, menu);
        updateFavorite(menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void updateFavorite(Menu menu) {
        final MenuItem item = menu.findItem(R.id.menu_item_favorite);
        String isbnValue = Utils.getISBNValue(mBook.getCoverEditionKey());
        if (!TextUtils.isEmpty(isbnValue)) {
            item.setIcon(R.drawable.ic_star_filled_white);
        } else {
            item.setIcon(R.drawable.ic_star_border_white);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                launchShareChooser();
                break;
            case R.id.menu_item_favorite:
                toggleFavoriteButton(item);
                break;
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchShareChooser() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBodyText = String.format(Utils.SHARE_FORMAT, mBook.getTitle(), mBook.getAuthorName(), mBook.getCoverUrl());
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, Utils.SHARE_SUBJECT);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
        startActivity(Intent.createChooser(sharingIntent, Utils.SHARE_TEXT));
    }

    private void toggleFavoriteButton(MenuItem item) {
        String toastText;
        String isbnValue = Utils.getISBNValue(mBook.getCoverEditionKey());
        if (TextUtils.isEmpty(isbnValue)) {
            toastText = Utils.FAVOURITED;
            item.setIcon(R.drawable.ic_star_filled_white);
            Utils.storeBook(mBook.getCoverEditionKey(), mBook);
        } else {
            toastText = Utils.UN_FAVOURITED;
            item.setIcon(R.drawable.ic_star_border_white);
            Utils.removeISBNValue(mBook.getCoverEditionKey());
        }
        Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
    }
}