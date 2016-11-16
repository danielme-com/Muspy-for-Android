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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.model.Release;
import com.danielme.muspyforandroid.ui.ViewUtils;
import com.danielme.muspyforandroid.ui.recyclerview.Adapter;
import com.danielme.muspyforandroid.ui.recyclerview.Footer;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Adapter that produces views to render the releases..
 */
public class ReleasesAdapter extends Adapter {

  private static final int TYPE_RELEASE = 0;
  private static final int TYPE_FOOTER = 1;

  private final RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;

  public ReleasesAdapter(@NonNull List<Object> data,
                         @NonNull RecyclerViewOnItemClickListener recyclerViewOnItemClickListener) {
    super(data, Release.class);
    this.recyclerViewOnItemClickListener = recyclerViewOnItemClickListener;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == TYPE_RELEASE) {
      View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_releases,
          parent, false);
      return new ReleaseViewHolder(item);
    } else {
      View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_footer,
          parent, false);
      return new FooterViewHolder(item);
    }
  }

  @Override
  public int getItemViewType(int position) {
    if (getData().get(position) instanceof Release) {
      return TYPE_RELEASE;
    } else if (getData().get(position) instanceof Footer) {
      return TYPE_FOOTER;
    } else {
      throw new RuntimeException("ItemViewType unknown " + getData().get(position));
    }
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof ReleaseViewHolder) {
      ReleaseViewHolder rvh = (ReleaseViewHolder) holder;
      Release release = (Release) getData().get(position);
      String date = ViewUtils.localizedDate(release.getDate()).getString();
      rvh.getTextViewDate().setText(date);
      rvh.getTextViewRelease().setText(release.getName());
      rvh.getTextViewArtist().setText(release.getArtist().getName());

      Glide.with(rvh.getImageViewCover().getContext()).load(release.getCoverUrl())
          .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
          .into(rvh.getImageViewCover());
    }
    //else FOOTER, do nothing
  }

  @Override
  public int getItemCount() {
    return getData().size();
  }

  public class ReleaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @Bind(R.id.cover)
    ImageView imageViewCover;
    @Bind(R.id.date)
    TextView textViewDate;
    @Bind(R.id.artist)
    TextView textViewArtist;
    @Bind(R.id.release)
    TextView textViewRelease;

    public ReleaseViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      itemView.setOnClickListener(this);
    }

    public TextView getTextViewRelease() {
      return textViewRelease;
    }

    public void setTextViewRelease(TextView textViewRelease) {
      this.textViewRelease = textViewRelease;
    }

    public TextView getTextViewArtist() {
      return textViewArtist;
    }

    public void setTextViewArtist(TextView textViewArtist) {
      this.textViewArtist = textViewArtist;
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

    @Override
    public void onClick(View v) {
      recyclerViewOnItemClickListener.onClick(v, getAdapterPosition());
    }
  }

}