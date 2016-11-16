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
package com.danielme.muspyforandroid.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Comparator;


public class Artist implements Parcelable {

  private String mbid;
  private String name;
  private String disambiguation;

  @SerializedName("sort_name")
  private String sortName;

  //null: unknown, ask to muspy api
  private Boolean following;

  public Artist() {
    following = true;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDisambiguation() {
    return disambiguation;
  }

  public void setDisambiguation(String disambiguation) {
    this.disambiguation = disambiguation;
  }

  public String getSortName() {
    return sortName;
  }

  public void setSortName(String sortName) {
    this.sortName = sortName;
  }

  public String getMbid() {
    return mbid;
  }

  public void setMbid(String mbid) {
    this.mbid = mbid;
  }


  public Boolean isFollowing() {
    return following;
  }

  public void setFollowing(Boolean following) {
    this.following = following;
  }

  public String getFullName() {
    String fullName;
    if (!TextUtils.isEmpty(disambiguation)) {
      fullName = name + " ( " + disambiguation + " ) ";
    } else {
      fullName = name;
    }
    return fullName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Artist artist = (Artist) o;

    return mbid.equals(artist.mbid);
  }

  @Override
  public int hashCode() {
    return mbid.hashCode();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.mbid);
    dest.writeString(this.name);
    dest.writeString(this.disambiguation);
    dest.writeString(this.sortName);
    dest.writeByte(following ? (byte) 1 : (byte) 0);
  }

  protected Artist(Parcel in) {
    this.mbid = in.readString();
    this.name = in.readString();
    this.disambiguation = in.readString();
    this.sortName = in.readString();
    this.following = in.readByte() != 0;
  }

  public static final Creator<Artist> CREATOR = new Creator<Artist>() {
    @Override
    public Artist createFromParcel(Parcel source) {
      return new Artist(source);
    }

    @Override
    public Artist[] newArray(int size) {
      return new Artist[size];
    }
  };

  public static class NameComparator implements Comparator<Artist>, Serializable {

    @Override
    public int compare(Artist lhs, Artist rhs) {
      return lhs.getName().compareToIgnoreCase(rhs.getName());
    }
  }

}
