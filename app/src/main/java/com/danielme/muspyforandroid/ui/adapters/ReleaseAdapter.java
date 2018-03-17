/*
 * Copyright (C) 2012-2016 Daniel Medina <http://danielme.com>
 *
 * This file is part of "Muspy for Android".
 *
 * "Muspy for Android" is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * "Muspy for Android" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 3
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-3.0.html/>
 */
package com.danielme.muspyforandroid.ui.adapters;

import android.app.Activity;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.model.Media;
import com.danielme.muspyforandroid.model.ReleaseMB;
import com.danielme.muspyforandroid.model.Track;
import com.danielme.muspyforandroid.service.ReleaseService;
import com.danielme.muspyforandroid.ui.ViewUtils;
import com.danielme.muspyforandroid.ui.adapters.vh.ReleaseViewHolder;
import com.danielme.muspyforandroid.ui.recyclerview.Adapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter that produces views to render the details of a Release. These views are not clickable.
 */
public class ReleaseAdapter extends Adapter {

  private static final int TYPE_DETAIL = 0;
  private static final int TYPE_MEDIA = 1;
  private static final int TYPE_TRACK = 2;

  private final ReleaseService releaseService;
  private final Activity context;

  public ReleaseAdapter(@NonNull ArrayList<Parcelable> data, ReleaseService releaseService,
                        Activity context) {
    super(data, ReleaseMB.class);
    this.releaseService = releaseService;
    this.context = context;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    if (viewType == TYPE_DETAIL) {
      View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_release,
              parent, false);
      return new ReleaseViewHolder(item, releaseService, context);
    } else if (viewType == TYPE_MEDIA) {
      View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_media,
              parent, false);
      return new MediaViewHolder(item);
    } else {
      View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_track,
              parent, false);
      return new TrackViewHolder(item);
    }
  }

  @Override
  public int getItemViewType(int position) {
    if (getData().get(position) instanceof ReleaseMB) {
      return TYPE_DETAIL;
    } else if (getData().get(position) instanceof Media) {
      return TYPE_MEDIA;
    } else if (getData().get(position) instanceof Track) {
      return TYPE_TRACK;
    } else {
      throw new RuntimeException("ItemViewType unknown " + getData().get(position));
    }
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof TrackViewHolder) {
      TrackViewHolder vh = (TrackViewHolder) holder;
      vh.setTrack((Track) getData().get(position));

    } else if (holder instanceof ReleaseViewHolder) {
      ReleaseViewHolder vh = (ReleaseViewHolder) holder;
      vh.setRelease((ReleaseMB) getData().get(position));

    } else if (holder instanceof MediaViewHolder) {
      MediaViewHolder vh = (MediaViewHolder) holder;
      vh.setMedia((Media) getData().get(position));
    } else {
      throw new IllegalArgumentException("unknown holder type");
    }
  }

  @Override
  public int getItemCount() {
    return getData().size();
  }



  public class MediaViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.format)
    TextView textViewFormat;
    @BindView(R.id.length)
    TextView textViewLength;
    @BindView(R.id.title)
    TextView textViewTitle;

    public MediaViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void setMedia(Media media) {
      textViewFormat.setText(media.getFormatWithNumber());
      textViewLength.setText(ViewUtils.formatMilliseconds(media.getTotalLength()));
      //the media only displays its name if it's different than the release title.
      if (!TextUtils.isEmpty(media.getTitle())
              && !media.getTitle().toUpperCase().equals(((ReleaseMB) getData().get(0)).getTitle()
              .toUpperCase())) {
        textViewTitle.setText(media.getTitle());
        textViewTitle.setVisibility(View.VISIBLE);
      } else {
        textViewTitle.setText(null);
        textViewTitle.setVisibility(View.GONE);
      }
    }

    public TextView getTextViewFormat() {
      return textViewFormat;
    }

    public void setTextViewFormat(TextView textViewFormat) {
      this.textViewFormat = textViewFormat;
    }

    public TextView getTextViewLength() {
      return textViewLength;
    }

    public void setTextViewLength(TextView textViewLength) {
      this.textViewLength = textViewLength;
    }

    public TextView getTextViewTitle() {
      return textViewTitle;
    }

    public void setTextViewTitle(TextView textViewTitle) {
      this.textViewTitle = textViewTitle;
    }
  }

  public static class TrackViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.title)
    TextView textViewTitle;
    @BindView(R.id.position)
    TextView textViewPosition;
    @BindView(R.id.length)
    TextView textViewLength;

    public TrackViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void setTrack(Track track) {
      textViewTitle.setText(track.getTitle());
      textViewPosition.setText(track.getNumber());
      textViewLength.setText(ViewUtils.formatMilliseconds(track.getLength()));
    }

    public TextView getTextViewLength() {
      return textViewLength;
    }

    public void setTextViewLength(TextView textViewLength) {
      this.textViewLength = textViewLength;
    }

    public TextView getTextViewPosition() {
      return textViewPosition;
    }

    public void setTextViewPosition(TextView textViewPosition) {
      this.textViewPosition = textViewPosition;
    }

    public TextView getTextViewTitle() {
      return textViewTitle;
    }

    public void setTextViewTitle(TextView textViewTitle) {
      this.textViewTitle = textViewTitle;
    }
  }

}