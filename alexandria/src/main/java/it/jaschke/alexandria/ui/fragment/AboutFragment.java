package it.jaschke.alexandria.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.jaschke.alexandria.R;

/**
 * Corrections:
 * <ul>
 * <li>Renamed from {@code About} to {@code AboutFragment}</li>
 * <li>Extended from {@code BaseFragment}</li>
 * </ul>
 */
public class AboutFragment extends BaseFragment {

    public AboutFragment() { }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.menu_item_about);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

}
