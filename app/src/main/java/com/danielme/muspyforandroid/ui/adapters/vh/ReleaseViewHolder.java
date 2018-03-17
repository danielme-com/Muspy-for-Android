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
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.model.ReleaseMB;
import com.danielme.muspyforandroid.service.ReleaseService;
import com.danielme.muspyforandroid.ui.ViewUtils;
import com.danielme.muspyforandroid.ui.fragments.ReleaseSearchBottomSheetFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReleaseViewHolder extends ReleaseCoverViewHolder {

  @BindView(R.id.artist)
  TextView textViewArtist;
  @BindView(R.id.title)
  TextView textViewTitle;
  @BindView(R.id.countryContent)
  TextView textViewCountry;
  @BindView(R.id.labelContent)
  TextView textViewLabel;
  @BindView(R.id.formatContent)
  TextView textViewFormat;
  @BindView(R.id.typeContent)
  TextView textViewType;
  @BindView(R.id.date)
  TextView textViewDate;
  @BindView(R.id.cover)
  ImageView imageViewCover;
  @BindView(R.id.length)
  TextView textViewLength;
  @BindView(R.id.searchInfo)
  Button buttonSearch;

  Context context;

  public ReleaseViewHolder(View itemView, ReleaseService releaseService, Activity context) {
    super(itemView, releaseService, context);
    this.context = context;
    ButterKnife.bind(this, itemView);

    textViewCountry.setSelected(true);
    textViewFormat.setSelected(true);
    textViewLabel.setSelected(true);
  }


  public void setRelease(final ReleaseMB release) {
    super.setRelease(release.getCover(), release.getGroupMbid());
    textViewTitle.setText(release.getTitle());
    textViewLabel.setText(release.getLabel());
    textViewArtist.setText(release.getArtist());
    textViewDate.setText(ViewUtils.localizedDate(release.getDate()).getString());
    textViewType.setText(release.getType());
    textViewFormat.setText(release.getFormat());
    textViewCountry.setText(release.getCountryName());
    textViewLength.setText(ViewUtils.formatMilliseconds(release.getTotalLength()));
    buttonSearch.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ReleaseSearchBottomSheetFragment frg = ReleaseSearchBottomSheetFragment.newInstance(
                textViewArtist.getText() + " " + textViewTitle.getText(),
                release.getId());
        frg.show(((AppCompatActivity) context).getSupportFragmentManager(), "moreInfo");
      }
    });
  }

}
