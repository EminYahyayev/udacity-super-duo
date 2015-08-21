package com.ewintory.footballscores.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.ewintory.footballscores.R;
import com.ewintory.footballscores.ui.fragment.PagerFragment;

import butterknife.Bind;

public final class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String FRAGMENT_TAG = "fragment_tag";

    private static final String STATE_CURRENT_PAGE = "state_current_page";
    private static final String STATE_SELECTED_MATCH = "state_selected_match";

    @Bind(R.id.toolbar) Toolbar mToolbar;

    private PagerFragment mPagerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(mToolbar);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mPagerFragment = (PagerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_scores_pager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_about == item.getItemId()) {
            Intent start_about = new Intent(this, AboutActivity.class);
            startActivity(start_about);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mPagerFragment != null) mPagerFragment.dispatchTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
}
