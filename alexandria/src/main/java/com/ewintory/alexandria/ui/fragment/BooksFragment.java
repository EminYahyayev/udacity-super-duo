package com.ewintory.alexandria.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ewintory.alexandria.R;
import com.ewintory.alexandria.provider.AlexandriaContract;
import com.ewintory.alexandria.service.BookService;
import com.ewintory.alexandria.ui.adapter.BooksAdapter;

import butterknife.Bind;


/**
 * Mistakes found:
 * <ul>
 * <li> {@code ContentResolver#query} was called on UI thread(in Fragment#onCreateView method)
 * </ul>
 */
public final class BooksFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        BooksAdapter.OnBookItemClickListener, SwipeRefreshLayout.OnRefreshListener, AppBarLayout.OnOffsetChangedListener {

    private static final String TAG = BooksFragment.class.getSimpleName();
    private static final int LOADER_ID = 10;

    @Bind(R.id.books_recycler_view) RecyclerView mBooksRecyclerView;
    @Bind(R.id.books_empty_view) View mEmptyView;
    @Bind(R.id.swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.app_bar) AppBarLayout mAppBarLayout;

    private BooksAdapter mBooksAdapter;

    public BooksFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_books, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipe_progress_colors));
        mSwipeRefreshLayout.post(new Runnable() {
            @Override public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        mBooksAdapter = new BooksAdapter(this);
        mBooksAdapter.setListener(this);

        mBooksRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(
                getResources().getInteger(R.integer.book_columns), StaggeredGridLayoutManager.VERTICAL));
        mBooksRecyclerView.setAdapter(mBooksAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_search == item.getItemId()) {
            Toast.makeText(getActivity(), "Searching feature", Toast.LENGTH_SHORT).show();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        mAppBarLayout.removeOnOffsetChangedListener(this);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        mBooksAdapter.setListener(DUMMY);
        super.onDestroyView();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        mSwipeRefreshLayout.setEnabled(i == 0);
    }

    @Override
    public void onBookItemDelete(int position) {
        Cursor cursor = mBooksAdapter.getCursor();
        if (cursor != null && cursor.moveToPosition(position)) {
            Intent bookIntent = new Intent(getActivity(), BookService.class);
            bookIntent.putExtra(BookService.EXTRA_EAN, cursor.getString(BooksAdapter.BooksQuery.ID));
            bookIntent.setAction(BookService.ACTION_DELETE_BOOK);
            getActivity().startService(bookIntent);
            Toast.makeText(getActivity(), R.string.message_book_removed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBookItemShare(String shareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(shareIntent);
    }

    @Override
    public void onRefresh() {
        restartLoader();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String selection = AlexandriaContract.BookEntry.TITLE + " LIKE ? OR " + AlexandriaContract.BookEntry.SUBTITLE + " LIKE ? ";
        String searchString = ""; //TODO: searchText.getText().toString();

        if (searchString.length() > 0) {
            searchString = "%" + searchString + "%";
            return new CursorLoader(
                    getActivity(),
                    AlexandriaContract.BookEntry.CONTENT_URI,
                    BooksAdapter.BooksQuery.PROJECTION,
                    selection,
                    new String[]{searchString, searchString},
                    null
            );
        }

        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.CONTENT_URI,
                BooksAdapter.BooksQuery.PROJECTION,
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        final int size = cursor.getCount();
        Log.v(TAG, String.format("%d books loaded", size));

        mSwipeRefreshLayout.post(new Runnable() {
            @Override public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        showEmptyView(size == 0);
        mBooksAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBooksAdapter.swapCursor(null);
        showEmptyView(true);
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    private void showEmptyView(boolean show) {
        if (mEmptyView != null) mEmptyView.animate().alpha(show ? 1 : 0).setDuration(200).start();
    }
}
