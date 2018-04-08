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
package com.danielme.muspyforandroid.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Cover {

  private List<CoverImage> images;

  public List<CoverImage> getImages() {
    return images;
  }

  public void setImages(List<CoverImage> images) {
    this.images = images;
  }

  public static class CoverImage {
    private boolean front;
    private boolean back;
    private String image;
    private String id;
    @SerializedName("thumbnails")
    private Thumbnail thumbnail;

    public boolean isBack() {
      return back;
    }

    public void setBack(boolean back) {
      this.back = back;
    }

    public boolean isFront() {
      return front;
    }

    public void setFront(boolean front) {
      this.front = front;
    }

    public String getImage() {
      return image;
    }

    public void setImage(String image) {
      this.image = image;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public Thumbnail getThumbnail() {
      return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
      this.thumbnail = thumbnail;
    }
  }

  public static class Thumbnail {
    @SerializedName("250")
    private String _250;
    @SerializedName("500")
    private String _500;
    @SerializedName("1200")
    private String _1200;
    private String small;
    private String large;

    public String get_250() {
      return _250;
    }

    public void set_250(String _250) {
      this._250 = _250;
    }

    public String get_500() {
      return _500;
    }

    public void set_500(String _500) {
      this._500 = _500;
    }

    public String get_1200() {
      return _1200;
    }

    public void set_1200(String _1200) {
      this._1200 = _1200;
    }

    public String getSmall() {
      return small;
    }

    public void setSmall(String small) {
      this.small = small;
    }

    public String getLarge() {
      return large;
    }

    public void setLarge(String large) {
      this.large = large;
    }
  }
}
