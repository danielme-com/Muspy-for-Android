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
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.model.Release;
import com.danielme.muspyforandroid.service.ReleaseService;
import com.danielme.muspyforandroid.ui.adapters.vh.ReleasesViewHolder;
import com.danielme.muspyforandroid.ui.recyclerview.Adapter;
import com.danielme.muspyforandroid.ui.recyclerview.Footer;

import java.util.List;

/**
 * Adapter that produces views to render the releases..
 */
public class ReleasesAdapter extends Adapter {

  private static final int TYPE_RELEASE = 0;
  private static final int TYPE_FOOTER = 1;

  private final RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;
  private final ReleaseService releaseService;
  private final Activity context;

  public ReleasesAdapter(@NonNull List<Object> data,
                         @NonNull RecyclerViewOnItemClickListener recyclerViewOnItemClickListener,
                         ReleaseService releaseService,
                         Activity context) {
    super(data, Release.class);
    this.recyclerViewOnItemClickListener = recyclerViewOnItemClickListener;
    this.releaseService = releaseService;
    this.context = context;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == TYPE_RELEASE) {
      View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_releases,
          parent, false);
      return new ReleasesViewHolder(item, releaseService, recyclerViewOnItemClickListener, context);
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
    if (holder instanceof ReleasesViewHolder) {
      ReleasesViewHolder rvh = (ReleasesViewHolder) holder;
      rvh.setRelease((Release) getData().get(position));
    }
    //else FOOTER, do nothing
  }

  @Override
  public int getItemCount() {
    return getData().size();
  }


}