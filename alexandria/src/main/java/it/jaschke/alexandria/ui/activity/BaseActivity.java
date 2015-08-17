package it.jaschke.alexandria.ui.activity;

import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.jaschke.alexandria.AlexandriaApplication;
import it.jaschke.alexandria.R;

public abstract class BaseActivity extends AppCompatActivity {
    public static final String TAG = BaseActivity.class.getSimpleName();

    @Nullable @Bind(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AlexandriaApplication.get(this).getRefWatcher().watch(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Nullable
    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public void setTitle(int titleId) {
        ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setTitle(titleId);
        else
            super.setTitle(titleId);
    }

    private void setupToolbar() {
        if (mToolbar == null) {
            Log.w(TAG, "Didn't find a toolbar");
            return;
        }

        ViewCompat.setElevation(mToolbar, getResources().getDimension(R.dimen.toolbar_elevation));
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
        }
    }
}
