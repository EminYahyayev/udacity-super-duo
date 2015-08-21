package com.ewintory.alexandria;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public final class AlexandriaApplication extends Application {

    private RefWatcher mRefWatcher;

    public static AlexandriaApplication get(Context context) {
        return (AlexandriaApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mRefWatcher = installLeakCanary();
    }

    public RefWatcher getRefWatcher() {
        return mRefWatcher;
    }

    protected RefWatcher installLeakCanary() {
        return LeakCanary.install(this);
        //return RefWatcher.DISABLED;
    }
}
