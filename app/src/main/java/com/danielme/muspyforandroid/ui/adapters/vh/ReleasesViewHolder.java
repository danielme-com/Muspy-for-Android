/*
 * Copyright (C) 2012-2018 Daniel Medina <http://danielme.com>
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
package com.danielme.muspyforandroid.ui.adapters.vh;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.model.Release;
import com.danielme.muspyforandroid.service.ReleaseService;
import com.danielme.muspyforandroid.ui.ViewUtils;
import com.danielme.muspyforandroid.ui.adapters.RecyclerViewOnItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReleasesViewHolder extends ReleaseCoverViewHolder implements View.OnClickListener {

  @BindView(R.id.cover)
  ImageView imageViewCover;
  @BindView(R.id.date)
  TextView textViewDate;
  @BindView(R.id.artist)
  TextView textViewArtist;
  @BindView(R.id.release)
  TextView textViewRelease;

  private final RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;

  public ReleasesViewHolder(View itemView, ReleaseService releaseService,
                            RecyclerViewOnItemClickListener recyclerViewOnItemClickListener,
                            Activity actContext) {
    super(itemView, releaseService, actContext);
    ButterKnife.bind(this, itemView);
    itemView.setOnClickListener(this);
    this.recyclerViewOnItemClickListener = recyclerViewOnItemClickListener;
  }

  public void setRelease(Release release) {
    super.setRelease(release.getCoverUrl(), release.getMbid());
    String date = ViewUtils.localizedDate(release.getDate()).getString();
    textViewDate.setText(date);
    textViewRelease.setText(release.getName());
    textViewArtist.setText(release.getArtist().getName());
  }

  @Override
  public void onClick(View v) {
    recyclerViewOnItemClickListener.onClick(v, getAdapterPosition());
  }
}