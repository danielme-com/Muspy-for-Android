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

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReleaseMB {

  private String id;
  private String title;
  private String country;
  private String date;
  private List<Media> media;
  @SerializedName("label-info")
  private List<LabelWrapper> labels;

  //these fields must be setted manually
  private String artist;
  private String format;
  private String cover;
  private String type;
  private int totalLength;
  private String countryName;
  private String label;

  private String groupMbid;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public List<Media> getMedia() {
    return media;
  }

  public void setMedia(List<Media> media) {
    this.media = media;
  }

  public String getArtist() {
    return artist;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public String getCover() {
    return cover;
  }

  public void setCover(String cover) {
    this.cover = cover;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getTotalLength() {
    return totalLength;
  }

  public void setTotalLength(int totalLength) {
    this.totalLength = totalLength;
  }

  public String getCountryName() {
    return countryName;
  }

  public void setCountryName(String countryName) {
    this.countryName = countryName;
  }

  public List<LabelWrapper> getLabels() {
    return labels;
  }

  public void setLabels(List<LabelWrapper> labels) {
    this.labels = labels;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getGroupMbid() {
    return groupMbid;
  }

  public void setGroupMbid(String groupMbid) {
    this.groupMbid = groupMbid;
  }
}