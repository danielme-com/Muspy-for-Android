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
package com.danielme.muspyforandroid.ui.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.danielme.muspyforandroid.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Abstract adapter for {@link GenericRecyclerViewFragment}
 */
public abstract class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private final List data;
  private final Class dataClass;

  /**
   *
   * @param dataClass the "Main class" of the list excluding headers, footers an section dividers.
   */
  protected Adapter(@NonNull List data, Class dataClass) {
    this.data = data;
    this.dataClass = dataClass;
  }

  public List getData() {
    return data;
  }

  /**
   * Counts the instances of the main class.
   */
  public int countRealData() {
    int count = 0;
    for (Object item : data) {
      if (dataClass.isInstance(item)) {
        count++;
      }
    }
    return count;
  }

  /**
   * The footer for endless recycler view is always the same.
   */
  public static class FooterViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.footer)
    ProgressBar progressBar;

    public FooterViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public ProgressBar getProgressBar() {
      return progressBar;
    }

  }

}