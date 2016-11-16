/*
 *
 *  * Copyright (C) 2012-2016 Daniel Medina <http://danielme.com>
 *  *
 *  * This file is part of "Muspy for Android".
 *  *
 *  * "Muspy for Android" is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, version 3.
 *  *
 *  * "Muspy for Android" is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License version 3
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-3.0.html/>
 *
 */
package com.danielme.muspyforandroid.ui.adapters;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Header for the recyclerview with the number of elements.
 */
public class ResultsItem implements Parcelable {

  private String text;

  public ResultsItem() {
    super();
  }

  public ResultsItem(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.text);
  }

  protected ResultsItem(Parcel in) {
    this.text = in.readString();
  }

  public static final Parcelable.Creator<ResultsItem> CREATOR =
      new Parcelable.Creator<ResultsItem>() {
    @Override
    public ResultsItem createFromParcel(Parcel source) {
      return new ResultsItem(source);
    }

    @Override
    public ResultsItem[] newArray(int size) {
      return new ResultsItem[size];
    }
  };

}