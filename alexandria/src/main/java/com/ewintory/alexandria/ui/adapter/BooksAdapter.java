/*
 * Copyright 2015.  Emin Yahyayev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ewintory.alexandria.ui.adapter;


import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ewintory.alexandria.R;
import com.ewintory.alexandria.provider.AlexandriaContract;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BooksAdapter extends CursorAdapter<BooksAdapter.BookHolder> {

    public interface OnBookItemClickListener {
        void onBookItemClicked(int position, View view);

        OnBookItemClickListener DUMMY = new OnBookItemClickListener() {
            @Override public void onBookItemClicked(int position, View view) { }
        };
    }

    public interface BooksQuery {
        String[] PROJECTION = {
                AlexandriaContract.BookEntry._ID,
                AlexandriaContract.BookEntry.TITLE,
                AlexandriaContract.BookEntry.SUBTITLE,
                AlexandriaContract.BookEntry.IMAGE_URL
        };

        int ID = 0;
        int TITLE = 1;
        int SUBTITLE = 2;
        int IMAGE_URL = 3;
    }

    private final Fragment mFragment;
    private OnBookItemClickListener mListener = OnBookItemClickListener.DUMMY;

    public BooksAdapter(Fragment fragment) {
        super(fragment.getActivity());
        mFragment = fragment;
        setHasStableIds(true);
    }

    public BooksAdapter setListener(@NonNull OnBookItemClickListener listener) {
        mListener = listener;
        return this;
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(BooksQuery.ID);
    }

    @Override
    public BookHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new BookHolder(mInflater.inflate(R.layout.item_book_v2, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(BookHolder holder, int position) {
        mCursor.moveToPosition(position);

        String imgUrl = mCursor.getString(BooksQuery.IMAGE_URL);
        Glide.with(mFragment)
                .load(imgUrl)
                .crossFade()
                .placeholder(R.color.book_cover_placeholder)
                .into(holder.bookCover);

        String bookTitle = mCursor.getString(BooksQuery.TITLE);
        holder.bookTitle.setText(bookTitle);

        String bookSubTitle = mCursor.getString(BooksQuery.SUBTITLE);
        holder.bookSubTitle.setText(bookSubTitle);
    }

    class BookHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.book_item_cover) ImageView bookCover;
        @Bind(R.id.book_item_title) TextView bookTitle;
        @Bind(R.id.book_item_subtitle) TextView bookSubTitle;

        public BookHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    mListener.onBookItemClicked(getAdapterPosition(), v);
                }
            });
        }
    }
}
