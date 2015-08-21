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

package com.ewintory.footballscores.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.format.Time;
import android.util.Log;

import com.ewintory.footballscores.R;
import com.ewintory.footballscores.ui.fragment.ScoresFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class PagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = PagerAdapter.class.getSimpleName();

    public static final int POSITION_TODAY = 2;
    public static final int PAGE_SIZE = 5;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("EEEE", Locale.US);

    private final Context mContext;
    private Map<Integer, ScoresFragment> mPageReferenceMap = new HashMap<>();

    public PagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.roll(Calendar.DAY_OF_MONTH, position - POSITION_TODAY);
        ScoresFragment fragment = ScoresFragment.newInstance(DATE_FORMAT.format(calendar.getTime()));

        Log.v(TAG, "getItem: position=" + position + ", date=" + DATE_FORMAT.format(calendar.getTime())
                + ((position == POSITION_TODAY) ? "  <---- TODAY" : ""));

        mPageReferenceMap.put(position, fragment);
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGE_SIZE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.roll(Calendar.DAY_OF_WEEK, position - POSITION_TODAY);
        return getDayName(mContext, calendar.getTimeInMillis());
    }

    public ScoresFragment getFragment(int key) {
        return mPageReferenceMap.get(key);
    }

    public void onDestroy() {
        mPageReferenceMap.clear();
    }

    public String getDayName(Context context, long dateInMillis) {
        // If the date is today, return the localized version of "Today" instead of the actual
        // day name.

        Time t = new Time();
        t.setToNow();
        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
        if (julianDay == currentJulianDay) {
            return context.getString(R.string.today);
        } else if (julianDay == currentJulianDay + 1) {
            return context.getString(R.string.tomorrow);
        } else if (julianDay == currentJulianDay - 1) {
            return context.getString(R.string.yesterday);
        } else {
            Time time = new Time();
            time.setToNow();
            // Otherwise, the format is just the day of the week (e.g "Wednesday".
            return DAY_FORMAT.format(dateInMillis);
        }
    }
}
