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

import com.google.gson.annotations.SerializedName;

public class Release implements Parcelable {

  private static final String COVER_URL = "https://muspy.com/cover?id=";

  //multiple patterns.
  private String date;

  private String mbid;

  private String name;

  private Type type;

  private Artist artist;


  public enum Type {
    @SerializedName("Album")
    ALBUM("Album"),
    @SerializedName("EP")
    EP("EP"),
    @SerializedName("Single")
    SINGLE("Single"),
    @SerializedName("Live")
    LIVE("Live"),
    @SerializedName("Compilation")
    COMPILATION("Compilation"),
    @SerializedName("Remix")
    REMIX("Remix"),
    @SerializedName("Other")
    OTHER("Other");

    private final String name;

    Type(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  public Artist getArtist() {
    return artist;
  }

  public void setArtist(Artist artist) {
    this.artist = artist;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMbid() {
    return mbid;
  }

  public void setMbid(String mbid) {
    this.mbid = mbid;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getCoverUrl() {
    return COVER_URL + mbid;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.date);
    dest.writeString(this.mbid);
    dest.writeString(this.name);
    dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    dest.writeParcelable(this.artist, flags);
  }

  public Release() {
    super();
  }

  protected Release(Parcel in) {
    this.date = in.readString();
    this.mbid = in.readString();
    this.name = in.readString();
    int tmpType = in.readInt();
    this.type = tmpType == -1 ? null : Type.values()[tmpType];
    this.artist = in.readParcelable(Artist.class.getClassLoader());
  }

  public static final Parcelable.Creator<Release> CREATOR = new Parcelable.Creator<Release>() {
    @Override
    public Release createFromParcel(Parcel source) {
      return new Release(source);
    }

    @Override
    public Release[] newArray(int size) {
      return new Release[size];
    }
  };

}