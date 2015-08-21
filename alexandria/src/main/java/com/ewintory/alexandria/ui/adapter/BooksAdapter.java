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


import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ewintory.alexandria.R;
import com.ewintory.alexandria.provider.AlexandriaContract;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookHolder> {

    public interface OnBookItemClickListener {
        void onBookItemClicked(int position, View view);

        OnBookItemClickListener DUMMY = new OnBookItemClickListener() {
            @Override public void onBookItemClicked(int position, View view) { }
        };
    }

    private final Fragment mFragment;
    private final LayoutInflater mInflater;

    private Cursor mCursor;
    private OnBookItemClickListener mListener = OnBookItemClickListener.DUMMY;

    public BooksAdapter(Fragment fragment) {
        mInflater = LayoutInflater.from(fragment.getActivity());
        mFragment = fragment;
    }

    public BooksAdapter setListener(@NonNull OnBookItemClickListener listener) {
        mListener = listener;
        return this;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    @Override
    public BookHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new BookHolder(mInflater.inflate(R.layout.item_book, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(BookHolder holder, int position) {
        mCursor.moveToPosition(position);

        String imgUrl = mCursor.getString(mCursor.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        Glide.with(mFragment)
                .load(imgUrl)
                .crossFade()
                .placeholder(R.color.book_cover_placeholder)
                .into(holder.bookCover);

        String bookTitle = mCursor.getString(mCursor.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        holder.bookTitle.setText(bookTitle);

        String bookSubTitle = mCursor.getString(mCursor.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
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
