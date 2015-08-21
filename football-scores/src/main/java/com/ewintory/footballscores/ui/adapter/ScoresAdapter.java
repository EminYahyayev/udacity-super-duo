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

import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;
import com.ewintory.footballscores.R;
import com.ewintory.footballscores.provider.ScoresContract;
import com.ewintory.footballscores.util.Utilities;
import com.ewintory.footballscores.util.svg.SvgDecoder;
import com.ewintory.footballscores.util.svg.SvgDrawableTranscoder;
import com.ewintory.footballscores.util.svg.SvgSoftwareLayerSetter;

import java.io.InputStream;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class ScoresAdapter extends RecyclerView.Adapter<ScoresAdapter.ViewHolder> {

    public interface OnScoreItemClickListener {
        void onScoreItemClicked(int position, View view);

        void onShareScoreItemClicked(String shareText);

        OnScoreItemClickListener DUMMY = new OnScoreItemClickListener() {
            @Override public void onScoreItemClicked(int position, View view) { }

            @Override public void onShareScoreItemClicked(String shareText) {}
        };
    }

    private final Fragment mFragment;
    private final LayoutInflater mInflater;
    private final GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;

    private Cursor mCursor;
    private OnScoreItemClickListener mListener = OnScoreItemClickListener.DUMMY;

    public ScoresAdapter(Fragment fragment) {
        mInflater = LayoutInflater.from(fragment.getActivity());
        mFragment = fragment;

        requestBuilder = Glide.with(fragment)
                .using(Glide.buildStreamModelLoader(Uri.class, fragment.getActivity()), InputStream.class)
                .from(Uri.class)
                .as(SVG.class)
                .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new FileToStreamDecoder<SVG>(new SvgDecoder()))
                .decoder(new SvgDecoder())
                .placeholder(R.color.primary_light)
                .error(R.drawable.no_icon)
                .animate(android.R.anim.fade_in)
                .listener(new SvgSoftwareLayerSetter<Uri>());
    }

    public ScoresAdapter setListener(OnScoreItemClickListener listener) {
        mListener = listener;
        return this;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_score, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Resources res = mFragment.getResources();
        mCursor.moveToPosition(position);

        holder.mLeagueName.setText(mCursor.getString(mCursor.getColumnIndex(ScoresContract.ScoreEntry.COLUMN_LEAGUE_CAPTION)));
        holder.mHomeName.setText(mCursor.getString(mCursor.getColumnIndex(ScoresContract.ScoreEntry.COLUMN_HOME)));
        holder.mAwayName.setText(mCursor.getString(mCursor.getColumnIndex(ScoresContract.ScoreEntry.COLUMN_AWAY)));
        holder.mMatchDay.setText(res.getString(R.string.match_day,
                mCursor.getInt(mCursor.getColumnIndex(ScoresContract.ScoreEntry.COLUMN_MATCH_DAY))));
        holder.mScore.setText(Utilities.getScores(res,
                mCursor.getInt(mCursor.getColumnIndex(ScoresContract.ScoreEntry.COLUMN_HOME_GOALS)),
                mCursor.getInt(mCursor.getColumnIndex(ScoresContract.ScoreEntry.COLUMN_AWAY_GOALS))));

        Uri uri = Uri.parse(mCursor.getString(mCursor.getColumnIndex(ScoresContract.ScoreEntry.COLUMN_HOME_CREST)));
        //Log.d("ScoresAdapter", "Home crest url=" + mCursor.getString(mCursor.getColumnIndex(ScoresContract.ScoreEntry.COLUMN_HOME_CREST)));
        requestBuilder
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .load(uri)
                .into(holder.mHomeCrest);


        uri = Uri.parse(mCursor.getString(mCursor.getColumnIndex(ScoresContract.ScoreEntry.COLUMN_AWAY_CREST)));
        //Log.d("ScoresAdapter", "Away crest url=" + uri);
        requestBuilder
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .load(uri)
                .into(holder.mAwayCrest);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.score_item_league) TextView mLeagueName;
        @Bind(R.id.score_item_home_name) TextView mHomeName;
        @Bind(R.id.score_item_away_name) TextView mAwayName;
        @Bind(R.id.score_item_matchday) TextView mMatchDay;
        @Bind(R.id.score_item_score) TextView mScore;
        @Bind(R.id.score_item_home_crest) ImageView mHomeCrest;
        @Bind(R.id.score_item_away_crest) ImageView mAwayCrest;

        @BindColor(R.color.secondary_text) int mTintColor;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            DrawableCompat.setTint(mMatchDay.getCompoundDrawables()[0], mTintColor);
            view.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    mListener.onScoreItemClicked(getAdapterPosition(), v);
                }
            });
        }

        @OnClick(R.id.share_button) void onShare() {
            mListener.onShareScoreItemClicked(String.format("%s %s %s",
                    mHomeName.getText(), mScore.getText(), mAwayName.getText()));
        }
    }
}
