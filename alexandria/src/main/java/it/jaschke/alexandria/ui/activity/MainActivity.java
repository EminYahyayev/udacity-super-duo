package it.jaschke.alexandria.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import butterknife.Bind;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.ui.adapter.OnBookClickListener;
import it.jaschke.alexandria.ui.fragment.AboutFragment;
import it.jaschke.alexandria.ui.fragment.AddBookFragment;
import it.jaschke.alexandria.ui.fragment.BookDetailFragment;
import it.jaschke.alexandria.ui.fragment.ListOfBooksFragment;
import it.jaschke.alexandria.utils.PrefUtils;


public final class MainActivity extends BaseActivity
        implements OnBookClickListener, NavigationView.OnNavigationItemSelectedListener {

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";

    public static boolean IS_TABLET = false;

    private static String STATE_SELECTED_POSITION = "state_position";

    @Nullable @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Nullable @Bind(R.id.navigation_view) NavigationView mNavigationView;

    private CharSequence title;
    private int mCurrentSelectedPosition = 0;

    private boolean mUserLearnedDrawer = false;
    private BroadcastReceiver messageReciever;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IS_TABLET = findViewById(R.id.right_container) != null;

        messageReciever = new MessageReceiver();
        IntentFilter filter = new IntentFilter(MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReciever, filter);

        title = getTitle();
        setupNavDrawer();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReciever);
        super.onDestroy();
    }

    @Override
    public void onItemSelected(String ean) {
        Bundle args = new Bundle();
        args.putString(BookDetailFragment.EAN_KEY, ean);

        BookDetailFragment fragment = new BookDetailFragment();
        fragment.setArguments(args);

        int id = R.id.container;
        if (findViewById(R.id.right_container) != null) {
            id = R.id.right_container;
        }
        getSupportFragmentManager().beginTransaction()
                .replace(id, fragment)
                .addToBackStack("Book Detail")
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else if (getSupportFragmentManager().getBackStackEntryCount() < 2) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment nextFragment;

        int itemId = menuItem.getItemId();
        switch (itemId) {
            case R.id.drawer_item_books:
                nextFragment = new ListOfBooksFragment();
                break;
            case R.id.drawer_item_add_book:
                nextFragment = new AddBookFragment();
                break;
            case R.id.drawer_item_settings:
                nextFragment = new AboutFragment();
                break;
            case R.id.drawer_item_about:
                startActivity(new Intent(this, SettingsActivity.class));
                return false;
            default:
                Log.e(TAG, "No such navdrawer item found.");
                return false;
        }

        fm.beginTransaction()
                .replace(R.id.container, nextFragment)
                .addToBackStack((String) title)
                .commit();

        return true;
    }

    public void setTitle(int titleId) {
        title = getString(titleId);
    }

    public void goBack(View view) {
        getSupportFragmentManager().popBackStack();
    }

    private void setupNavDrawer() {
        if (mDrawerLayout == null || mNavigationView == null) {
            return;
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerToggle.syncState();

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override public void onDrawerSlide(View drawerView, float slideOffset) {
                mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
            }

            @Override public void onDrawerOpened(View drawerView) {
                mDrawerToggle.onDrawerOpened(drawerView);
            }

            @Override public void onDrawerClosed(View drawerView) {
                mDrawerToggle.onDrawerClosed(drawerView);
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    PrefUtils.markDrawerLearned(MainActivity.this);
                }
            }

            @Override public void onDrawerStateChanged(int newState) {
                mDrawerToggle.onDrawerStateChanged(newState);
            }
        });

        mNavigationView.setNavigationItemSelectedListener(this);
        mUserLearnedDrawer = PrefUtils.isDrawerLearned(this);
        if (!mUserLearnedDrawer) openNavDrawer();
    }

    private boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    private void openNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * @deprecated In favor of layout configuration qualifiers
     */
    @Deprecated
    private boolean isTablet() {
        return (getApplicationContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private class MessageReceiver extends BroadcastReceiver {
        @Override public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra(MESSAGE_KEY) != null) {
                Toast.makeText(MainActivity.this, intent.getStringExtra(MESSAGE_KEY), Toast.LENGTH_LONG).show();
            }
        }

    }
}