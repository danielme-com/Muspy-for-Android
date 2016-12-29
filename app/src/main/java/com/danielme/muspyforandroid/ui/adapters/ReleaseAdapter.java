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

import android.content.Context;
import android.graphics.drawable.InsetDrawable;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.model.Media;
import com.danielme.muspyforandroid.model.ReleaseMB;
import com.danielme.muspyforandroid.model.Track;
import com.danielme.muspyforandroid.ui.ViewUtils;
import com.danielme.muspyforandroid.ui.fragments.ReleaseSearchBottomSheetFragment;
import com.danielme.muspyforandroid.ui.recyclerview.Adapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Adapter that produces views to render the details of a Release. These views are not clickable.
 */
public class ReleaseAdapter extends Adapter {

  private static final int TYPE_DETAIL = 0;
  private static final int TYPE_MEDIA = 1;
  private static final int TYPE_TRACK = 2;

  private final Context context;

  public ReleaseAdapter(@NonNull ArrayList<Parcelable> data, Context context) {
    super(data, ReleaseMB.class);
    this.context = context;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    if (viewType == TYPE_DETAIL) {
      View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_release,
          parent, false);
      return new ReleaseViewHolder(item);
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

  public class ReleaseViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.artist)
    TextView textViewArtist;
    @Bind(R.id.title)
    TextView textViewTitle;
    @Bind(R.id.countryContent)
    TextView textViewCountry;
    @Bind(R.id.labelContent)
    TextView textViewLabel;
    @Bind(R.id.formatContent)
    TextView textViewFormat;
    @Bind(R.id.typeContent)
    TextView textViewType;
    @Bind(R.id.date)
    TextView textViewDate;
    @Bind(R.id.cover)
    ImageView imageViewCover;
    @Bind(R.id.length)
    TextView textViewLength;

    public ReleaseViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);

      textViewCountry.setSelected(true);
      textViewFormat.setSelected(true);
      textViewLabel.setSelected(true);
    }

    public void setRelease(ReleaseMB release) {
      textViewTitle.setText(release.getTitle());
      textViewLabel.setText(release.getLabel());
      textViewArtist.setText(release.getArtist());
      textViewDate.setText(ViewUtils.localizedDate(release.getDate()).getString());
      textViewType.setText(release.getType());
      textViewFormat.setText(release.getFormat());
      textViewCountry.setText(release.getCountryName());
      textViewLength.setText(ViewUtils.formatMilliseconds(release.getTotalLength()));
      Glide.with(imageViewCover.getContext()).load(release.getCover())
          .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
          .into(imageViewCover);
    }

    @OnClick(R.id.searchInfo)
    public void searchInfo(View view) {
      ReleaseSearchBottomSheetFragment frg = ReleaseSearchBottomSheetFragment.newInstance(
          textViewArtist.getText() + " " + textViewTitle.getText(),
          ((ReleaseMB) getData().get(0)).getId());
      frg.show(((AppCompatActivity) context).getSupportFragmentManager(), "moreInfo");
    }

    public TextView getTextViewArtist() {
      return textViewArtist;
    }

    public void setTextViewArtist(TextView textViewArtist) {
      this.textViewArtist = textViewArtist;
    }

    public TextView getTextViewTitle() {
      return textViewTitle;
    }

    public void setTextViewTitle(TextView textViewTitle) {
      this.textViewTitle = textViewTitle;
    }

    public TextView getTextViewCountry() {
      return textViewCountry;
    }

    public void setTextViewCountry(TextView textViewCountry) {
      this.textViewCountry = textViewCountry;
    }

    public TextView getTextViewLabel() {
      return textViewLabel;
    }

    public void setTextViewLabel(TextView textViewLabel) {
      this.textViewLabel = textViewLabel;
    }

    public TextView getTextViewFormat() {
      return textViewFormat;
    }

    public void setTextViewFormat(TextView textViewFormat) {
      this.textViewFormat = textViewFormat;
    }

    public TextView getTextViewDate() {
      return textViewDate;
    }

    public void setTextViewDate(TextView textViewDate) {
      this.textViewDate = textViewDate;
    }

    public ImageView getImageViewCover() {
      return imageViewCover;
    }

    public void setImageViewCover(ImageView imageViewCover) {
      this.imageViewCover = imageViewCover;
    }

    public TextView getTextViewType() {
      return textViewType;
    }

    public void setTextViewType(TextView textViewType) {
      this.textViewType = textViewType;
    }

    public TextView getTextViewLength() {
      return textViewLength;
    }

    public void setTextViewLength(TextView textViewLength) {
      this.textViewLength = textViewLength;
    }

  }

  public class MediaViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.format)
    TextView textViewFormat;
    @Bind(R.id.length)
    TextView textViewLength;
    @Bind(R.id.title)
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

    @Bind(R.id.title)
    TextView textViewTitle;
    @Bind(R.id.position)
    TextView textViewPosition;
    @Bind(R.id.length)
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