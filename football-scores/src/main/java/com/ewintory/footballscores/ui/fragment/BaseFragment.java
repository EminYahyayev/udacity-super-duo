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
package com.ewintory.footballscores.ui.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.view.View;

import com.ewintory.footballscores.ScoresApplication;
import com.squareup.leakcanary.RefWatcher;

import butterknife.ButterKnife;

/**
 * Base class for all fragments.
 * Binds views and watches memory leaks
 *
 * @see ButterKnife
 * @see RefWatcher
 */
public abstract class BaseFragment extends Fragment {

    @CallSuper
    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @CallSuper
    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @CallSuper
    @Override public void onDestroy() {
        super.onDestroy();
        ScoresApplication.get(getActivity()).getRefWatcher().watch(this);
    }
}
