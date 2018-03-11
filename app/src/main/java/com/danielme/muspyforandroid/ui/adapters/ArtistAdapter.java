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

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.model.Artist;
import com.danielme.muspyforandroid.ui.recyclerview.Adapter;
import com.danielme.muspyforandroid.ui.recyclerview.Footer;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter that produces views to render a list of artists (followed artists, artists found)
 */
public class ArtistAdapter extends Adapter {

  private static final int TYPE_ARTIST = 0;
  private static final int TYPE_FOOTER = 1;
  private static final int TYPE_HEADER = 2; //header : total amount of artists

  private final RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;

  public ArtistAdapter(@NonNull ArrayList<Parcelable> data, @NonNull RecyclerViewOnItemClickListener
      recyclerViewOnItemClickListener) {
    super(data, Artist.class);
    this.recyclerViewOnItemClickListener = recyclerViewOnItemClickListener;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == TYPE_ARTIST) {
      View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_artist,
          parent, false);
      return new ArtistViewHolder(item);
    } else if (viewType == TYPE_HEADER) {
      View item = LayoutInflater.from(parent.getContext()).inflate(R.layout
          .recyclerview_header_results,
          parent, false);
      return new HeaderViewHolder(item);
    } else {
      View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_footer,
          parent, false);
      return new FooterViewHolder(item);
    }
  }

  @Override
  public int getItemViewType(int position) {
    if (getData().get(position) instanceof Artist) {
      return TYPE_ARTIST;
    } else if (getData().get(position) instanceof Footer) {
      return TYPE_FOOTER;
    } else if (getData().get(position) instanceof ResultsItem) {
      return TYPE_HEADER;
    } else {
      throw new RuntimeException("ItemViewType unknown " + getData().get(position));
    }
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof ArtistViewHolder) {
      ArtistViewHolder avh = (ArtistViewHolder) holder;
      Artist artist = (Artist) getData().get(position);
      avh.getTextViewArtist().setText(artist.getFullName());
    } else if (holder instanceof HeaderViewHolder) {
      HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
      ResultsItem results = (ResultsItem) getData().get(position);
      headerViewHolder.getTextViewResults().setText(results.getText());
    }
    //else FOOTER, do nothing
  }

  @Override
  public int getItemCount() {
    return getData().size();
  }

  public class ArtistViewHolder extends RecyclerView.ViewHolder implements View
      .OnClickListener {

    @BindView(R.id.artist)
    TextView textViewArtist;

    public ArtistViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      itemView.setOnClickListener(this);
    }

    public TextView getTextViewArtist() {
      return textViewArtist;
    }

    public void setTextViewArtist(TextView textViewArtist) {
      this.textViewArtist = textViewArtist;
    }

    @Override
    public void onClick(View v) {
      recyclerViewOnItemClickListener.onClick(v, getAdapterPosition());
    }

  }

  public static class HeaderViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.textViewResults)
    TextView textViewResults;

    public HeaderViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public TextView getTextViewResults() {
      return textViewResults;
    }

    public void setTextViewResults(TextView textViewResults) {
      this.textViewResults = textViewResults;
    }

  }

}