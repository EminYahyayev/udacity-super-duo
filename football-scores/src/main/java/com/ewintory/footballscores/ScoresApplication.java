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

package com.ewintory.footballscores;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public final class ScoresApplication extends Application {

    private RefWatcher mRefWatcher;

    public static ScoresApplication get(Context context) {
        return (ScoresApplication) context.getApplicationContext();
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
