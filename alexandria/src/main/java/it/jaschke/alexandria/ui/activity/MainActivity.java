package it.jaschke.alexandria.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import it.jaschke.alexandria.ui.fragment.AddBookFragment;
import it.jaschke.alexandria.ui.fragment.BookDetailFragment;
import it.jaschke.alexandria.ui.fragment.ListOfBooksFragment;
import it.jaschke.alexandria.utils.PrefUtils;


public final class MainActivity extends BaseActivity
        implements OnBookClickListener, NavigationView.OnNavigationItemSelectedListener {

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";

    public static boolean IS_TABLET = false;

    private static String STATE_DRAWER_ITEM = "selected_navigation_drawer_item";

    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.navigation_view) NavigationView mNavigationView;

    private CharSequence title;
    private int mCurrentDrawerItem;

    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private BroadcastReceiver messageReciever;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IS_TABLET = findViewById(R.id.right_container) != null;

        setupNavDrawer();

        if (savedInstanceState != null) {
            mCurrentDrawerItem = savedInstanceState.getInt(STATE_DRAWER_ITEM);
        } else {
            MenuItem menuItem = mNavigationView.getMenu().findItem(
                    PrefUtils.getStartingDrawerItem(this, R.id.drawer_item_books)
            );
            onNavigationItemSelected(menuItem);
        }

        messageReciever = new MessageReceiver();
        IntentFilter filter = new IntentFilter(MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReciever, filter);

        title = getTitle();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_DRAWER_ITEM, mCurrentDrawerItem);
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
        Log.d(TAG, "onNavigationItemSelected: " + menuItem.getTitle());

        int itemId = menuItem.getItemId();
        if (itemId == mCurrentDrawerItem) {
            mDrawerLayout.closeDrawers();
            return false;
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment nextFragment;

        switch (itemId) {
            case R.id.drawer_item_books:
                nextFragment = new ListOfBooksFragment();
                break;
            case R.id.drawer_item_add_book:
                nextFragment = new AddBookFragment();
                break;
            case R.id.drawer_item_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return false;
            case R.id.drawer_item_about:
                startActivity(new Intent(this, AboutActivity.class));
                return false;
            default:
                Log.e(TAG, "No such navdrawer item found.");
                return false;
        }

        fm.beginTransaction()
                .replace(R.id.container, nextFragment)
                .commit();

        mCurrentDrawerItem = itemId;
        menuItem.setChecked(true);
        closeNavDrawer();
        return true;
    }

    public void setTitle(int titleId) {
        title = getString(titleId);
    }

    public void goBack(View view) {
        getSupportFragmentManager().popBackStack();
    }

    private void setupNavDrawer() {
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

    private class MessageReceiver extends BroadcastReceiver {
        @Override public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra(MESSAGE_KEY) != null) {
                Toast.makeText(MainActivity.this, intent.getStringExtra(MESSAGE_KEY), Toast.LENGTH_LONG).show();
            }
        }

    }
}