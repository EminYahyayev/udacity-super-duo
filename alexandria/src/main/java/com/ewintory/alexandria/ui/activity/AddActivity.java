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

package com.ewintory.alexandria.ui.activity;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ewintory.alexandria.R;
import com.ewintory.alexandria.camera.CaptureActivity;
import com.ewintory.alexandria.camera.Intents;
import com.ewintory.alexandria.provider.AlexandriaContract;
import com.ewintory.alexandria.service.BookService;
import com.ewintory.alexandria.utils.Utils;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public final class AddActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {

    public static final int REQUEST_CODE = 0x0000c0de; // Only use bottom 16 bits

    private final int LOADER_ID = 1;
    private final String EAN_CONTENT = "eanContent";

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.add_book_ean) EditText mEanView;
    @Bind(R.id.nested_scroll_view) NestedScrollView mNestedScrollView;


    @Bind(R.id.book_message) TextView mMessageView;
    @Bind(R.id.book_card) CardView mBookCardView;
    @Bind(R.id.book_title) TextView mBookTitleView;
    @Bind(R.id.book_subtitle) TextView mBookSubtitleView;
    @Bind(R.id.book_authors) TextView mBookAuthorsView;
    @Bind(R.id.book_categories) TextView mBookCategoriesView;
    @Bind(R.id.book_image) ImageView mBookImageView;

    private boolean mFoundBook = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showBookCard(false);

        if (savedInstanceState != null) {
            mEanView.setText(savedInstanceState.getString(EAN_CONTENT));
            mEanView.setHint("");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mEanView != null) {
            outState.putString(EAN_CONTENT, mEanView.getText().toString());
        }
    }

    @OnTextChanged(R.id.add_book_ean)
    public void searchBook(CharSequence s) {
        String ean = Utils.fixEanForISBN13(this, s.toString());
        if (ean.length() < 13) {
            return;
        }

        if (ean.length() < 13) {
            showBookCard(false);
            return;
        }

        mFoundBook = false;
        Utils.resetBookServiceStatus(this);

        //Once we have an ISBN, start a book intent
        Intent bookIntent = new Intent(AddActivity.this, BookService.class);
        bookIntent.putExtra(BookService.EXTRA_EAN, ean);
        bookIntent.setAction(BookService.ACTION_FETCH_BOOK);
        startService(bookIntent);
        restartLoader();
    }

    @OnClick(R.id.scan_book_fab)
    public void onScanButton() {
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @OnClick(R.id.save_button)
    public void saveBook() {
        Intent bookIntent = new Intent(this, BookService.class);
        bookIntent.putExtra(BookService.EXTRA_EAN, mEanView.getText().toString());
        bookIntent.setAction(BookService.ACTION_FETCH_BOOK);
        startService(bookIntent);
        Toast.makeText(this, R.string.message_book_saved, Toast.LENGTH_SHORT).show();
        showBookCard(false);
    }

    @OnClick(R.id.button_delete)
    public void deleteBook() {
        Intent bookIntent = new Intent(this, BookService.class);
        bookIntent.putExtra(BookService.EXTRA_EAN, mEanView.getText().toString());
        bookIntent.setAction(BookService.ACTION_DELETE_BOOK);
        startService(bookIntent);
        Toast.makeText(this, R.string.message_book_removed, Toast.LENGTH_SHORT).show();
        showBookCard(false);
        mEanView.setText("");
        mNestedScrollView.smoothScrollTo(0, 0);
    }

    @OnClick(R.id.dismiss_button)
    public void dismissBook() {
        mEanView.setText("");
        showBookCard(false);
        mNestedScrollView.smoothScrollTo(0, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                mEanView.setText(data.getStringExtra(Intents.Scan.RESULT));
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mEanView.getText().length() == 0) {
            return null;
        }

        String eanStr = Utils.fixEanForISBN13(this, mEanView.getText().toString());
        long eanLong = Utils.convertEanStringToLong(eanStr);
        if (eanLong == -1) {
            return null;
        }

        return new CursorLoader(this,
                AlexandriaContract.BookEntry.buildFullBookUri(eanLong),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            mFoundBook = false;
            return;
        }

        mFoundBook = true;
        clearMessage();

        String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        mBookTitleView.setText(bookTitle);

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        mBookSubtitleView.setText(bookSubTitle);

        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        String[] authorsArr = (authors != null) ? authors.split(",") : new String[0];
        mBookAuthorsView.setLines(authorsArr.length);
        mBookAuthorsView.setText((authors != null) ? authors.replace(",", "\n") : "");

        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if (Patterns.WEB_URL.matcher(imgUrl).matches()) {
            Glide.with(this)
                    .load(imgUrl)
                    .crossFade()
                    .into(mBookImageView);
        }

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        mBookCategoriesView.setText(categories);

        showBookCard(true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {/** ignore */}

    private void restartLoader() {
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    private void showBookCard(boolean show) {
        mBookCardView.setEnabled(show);
        mBookCardView.animate().alpha(show ? 1 : 0).setDuration(300).start();
    }

    private void displayMessage() {
        if (!mFoundBook) {
            int message = R.string.error_failed_scan;
            if (!Utils.isConnected(this)) {
                message = R.string.error_no_network;
            } else {
                switch (Utils.getBookServiceStatus(this)) {
                    case BookService.BOOK_SERVICE_STATUS_SERVER_INVALID:
                        message = R.string.error_server_invalid;
                        break;
                    case BookService.BOOK_SERVICE_STATUS_SERVER_DOWN:
                        message = R.string.error_server_down;
                        break;
                    case BookService.BOOK_SERVICE_STATUS_INVALID:
                        message = R.string.not_found;
                        break;
                }
            }

            showBookCard(false);
            mMessageView.setVisibility(View.VISIBLE);
            mMessageView.setText(message);
        }
    }

    private void clearMessage() {
        mMessageView.setText("");
        mMessageView.setVisibility(View.GONE);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_book_service_status_key))) {
            displayMessage();
        }
    }

}
