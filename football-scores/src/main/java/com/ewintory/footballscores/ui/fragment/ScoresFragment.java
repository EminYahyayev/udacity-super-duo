package com.ewintory.footballscores.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
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

import com.ewintory.footballscores.R;
import com.ewintory.footballscores.provider.ScoresContract;
import com.ewintory.footballscores.service.FetchService;
import com.ewintory.footballscores.ui.adapter.ScoresAdapter;
import com.ewintory.footballscores.ui.widget.MultiSwipeRefreshLayout;

import butterknife.Bind;

public class ScoresFragment extends BaseFragment
        implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener, ScoresAdapter.OnScoreItemClickListener, MultiSwipeRefreshLayout.CanChildScrollUpCallback {

    public static final String ARG_DATE = "ARG_DATE";

    private static final int LOADER_SCORES = 0;

    @Bind(R.id.scores_recycler_view) RecyclerView mRecyclerView;
    @Bind(R.id.swipe_refresh_layout) MultiSwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.scores_empty_view) View mEmptyView;

    private String mLogTag = ScoresFragment.class.getSimpleName();
    private ScoresAdapter mScoresAdapter;
    private String mScoresDate;

    public static ScoresFragment newInstance(String date) {
        Bundle args = new Bundle();
        args.putString(ARG_DATE, date);

        ScoresFragment fragment = new ScoresFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ScoresFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mScoresDate = getArguments().getString(ARG_DATE);
        mLogTag = mLogTag.concat("#" + mScoresDate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scores, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipe_progress_colors));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setCanChildScrollUpCallback(this);
        postRefreshing(true);

        mScoresAdapter = new ScoresAdapter(this);
        mScoresAdapter.setListener(this);

        mRecyclerView.setAdapter(mScoresAdapter);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(
                getResources().getInteger(R.integer.score_columns), StaggeredGridLayoutManager.VERTICAL));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
        // the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
        // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
        // we do this in onActivityCreated.
        getLoaderManager().initLoader(LOADER_SCORES, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_scores, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_refresh == item.getItemId()) {
            postRefreshing(true);
            onRefresh();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(),
                ScoresContract.ScoreEntry.buildScoreWithDate(),
                ScoresAdapter.ScoresQuery.PROJECTION,
                null,
                new String[]{mScoresDate},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        final int size = cursor.getCount();
        Log.v(mLogTag, String.format("Scores loaded, %d items", size));

        mScoresAdapter.swapCursor(cursor);
        showEmptyView(size == 0);
        postRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mScoresAdapter.swapCursor(null);
        showEmptyView(true);
        postRefreshing(false);
    }

    @Override
    public void onScoreItemClicked(int position, View view) {
        ScoresAdapter.ViewHolder selected = (ScoresAdapter.ViewHolder) view.getTag();
    }

    @Override
    public void onShareScoreItemClicked(String shareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(shareIntent);
    }

    @Override
    public void onRefresh() {
        updateScores();
    }

    @Override
    public boolean canSwipeRefreshChildScrollUp() {
        return mRecyclerView != null && ViewCompat.canScrollVertically(mRecyclerView, -1);
    }

    public void setSwipeToRefreshEnabled(boolean enabled) {
        mSwipeRefreshLayout.setEnabled(enabled);
    }

    private void showEmptyView(boolean show) {
        if (mEmptyView != null) mEmptyView.animate().alpha(show ? 1 : 0).setDuration(200).start();
    }

    private void postRefreshing(final boolean refreshing) {
        //Log.v(mLogTag, "postRefreshing: refreshing=" + refreshing);
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.post(new Runnable() {
                @Override public void run() {
                    if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.setRefreshing(refreshing);
                }
            });
    }

    private void updateScores() {
        Intent intent = new Intent(getActivity(), FetchService.class);
        getActivity().startService(intent);
    }
}
