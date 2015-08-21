package com.ewintory.footballscores.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ewintory.footballscores.R;
import com.ewintory.footballscores.ui.adapter.PagerAdapter;
import com.ewintory.footballscores.util.OnTabSelectedListener;

import butterknife.Bind;

public final class PagerFragment extends BaseFragment
        implements AppBarLayout.OnOffsetChangedListener {
    private static final String STATE_CURRENT_PAGE = "state_current_page";

    @Bind(R.id.pager) ViewPager mViewPager;
    @Bind(R.id.app_bar) AppBarLayout mAppBarLayout;
    @Bind(R.id.tab_layout) TabLayout mTabLayout;

    private PagerAdapter mPagerAdapter;
    private int mCurrentPage = PagerAdapter.POSITION_TODAY;
    private int mAppBarOffset = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_CURRENT_PAGE))
            mCurrentPage = savedInstanceState.getInt(STATE_CURRENT_PAGE);

        mPagerAdapter = new PagerAdapter(getActivity(), getChildFragmentManager());

        mTabLayout.setTabsFromPagerAdapter(mPagerAdapter);
        mTabLayout.setOnTabSelectedListener(new OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }
        });

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mViewPager.setOffscreenPageLimit(PagerAdapter.PAGE_SIZE);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(mCurrentPage);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onStop() {
        mAppBarLayout.removeOnOffsetChangedListener(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mPagerAdapter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        mAppBarOffset = offset;
    }

    //TODO: Decide what to do with this. With ScoreFragment#canSwipeRefreshChildScrollUp there is no need in this method
    public void dispatchTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                ScoresFragment pageFragment = mPagerAdapter.getFragment(mViewPager.getCurrentItem());
                if (pageFragment != null) {
                    //Log.d("PagerFragment", "counter=" + (counter++));
                    pageFragment.setSwipeToRefreshEnabled(mAppBarOffset == 0);
                }
                break;
        }
    }
}
