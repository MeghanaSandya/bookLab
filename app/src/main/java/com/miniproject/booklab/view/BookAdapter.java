package com.miniproject.booklab.view;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.miniproject.booklab.R;
import com.miniproject.booklab.model.Book;
import com.miniproject.booklab.model.ImageLoaderManager;
import com.miniproject.booklab.utilities.Utils;


import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private List<Book> listItems;
    private ImageLoader mImageLoader;
    private ItemClickListener itemClickListener;

    BookAdapter() {
        mImageLoader = ImageLoaderManager.getInstance().getImageLoader();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_list, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, final int position) {
        final Book listItem = listItems.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(position, view, listItem);
                }
            }
        });

        holder.updateHolder(listItem);
    }


    private void toggleFavoriteButton(View view, Book book) {
        String toastText;
        String isbnValue = Utils.getISBNValue(book.getCoverEditionKey());
        if (TextUtils.isEmpty(isbnValue)) {
            toastText = Utils.FAVOURITED;
            view.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_star_filled));
            Utils.storeBook(book.getCoverEditionKey(), book);
        } else {
            toastText = Utils.UN_FAVOURITED;
            view.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_star_border));
            Utils.removeISBNValue(book.getCoverEditionKey());
        }
        Toast.makeText(view.getContext(), toastText, Toast.LENGTH_LONG).show();
    }

    @Override
    public int getItemCount() {
        return (listItems != null && listItems.size() > 0) ? listItems.size() : 0;
    }

    void setListItems(List<Book> listItems) {
        this.listItems = listItems;
        notifyDataSetChanged();
    }

    void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private void loadImage(String url, NetworkImageView bookImage) {
        mImageLoader.get(url, ImageLoader.getImageListener(bookImage,
                R.drawable.default_book_image, R.drawable
                        .ic_nocover_image));
        bookImage.setImageUrl(url, mImageLoader);
    }


    class BookViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mFavIcon;
        private final TextView mAuthorName, mBookTitle, mPublishedYear;
        private final NetworkImageView mBookImage;

        BookViewHolder(View itemView) {
            super(itemView);
            mAuthorName = itemView.findViewById(R.id.author_name);
            mBookTitle = itemView.findViewById(R.id.book_title);
            mBookImage = itemView.findViewById(R.id.book_imageView);
            mFavIcon = itemView.findViewById(R.id.favorite_icon);
            mPublishedYear = itemView.findViewById(R.id.publishedYear);
        }

        private void updateHolder(final Book book) {
            mAuthorName.setText(book.getAuthorName());
            mBookTitle.setText(book.getTitle());
            String publishedYear = String.valueOf(book.getPublishedYear());
            mPublishedYear.setText(publishedYear);
            loadImage(book.getCoverUrl(), mBookImage);

            String isbnValue = Utils.getISBNValue(book.getCoverEditionKey());
            if (TextUtils.isEmpty(isbnValue)) {
                mFavIcon.setBackground(ContextCompat.getDrawable(mFavIcon.getContext(), R.drawable.ic_star_border));
            } else {
                mFavIcon.setBackground(ContextCompat.getDrawable(mFavIcon.getContext(), R.drawable.ic_star_filled));
            }

            mFavIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleFavoriteButton(view, book);
                }
            });
        }
    }

    public interface ItemClickListener {
        void onItemClick(int position, View view, Book book);
    }
}

