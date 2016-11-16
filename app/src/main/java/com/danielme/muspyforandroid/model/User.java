package com.danielme.muspyforandroid.model;

import com.google.gson.annotations.SerializedName;

public class User {

  @SerializedName("userid")
  private String id;

  private String email;
  @SerializedName("notify")
  private boolean notifications;

  @SerializedName("notify_album")
  private boolean filterAlbum;

  @SerializedName("notify_single")
  private boolean filterSingle;

  @SerializedName("notify_ep")
  private boolean filterEP;

  @SerializedName("notify_live")
  private boolean filterLive;

  @SerializedName("notify_compilation")
  private boolean filterCompilation;

  @SerializedName("notify_other")
  private boolean filterOther;

  @SerializedName("notify_remix")
  private boolean filterRemix;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public boolean isNotifications() {
    return notifications;
  }

  public void setNotifications(boolean notifications) {
    this.notifications = notifications;
  }

  public boolean isFilterAlbum() {
    return filterAlbum;
  }

  public void setFilterAlbum(boolean filterAlbum) {
    this.filterAlbum = filterAlbum;
  }

  public boolean isFilterSingle() {
    return filterSingle;
  }

  public void setFilterSingle(boolean filterSingle) {
    this.filterSingle = filterSingle;
  }

  public boolean isFilterEP() {
    return filterEP;
  }

  public void setFilterEP(boolean filterEP) {
    this.filterEP = filterEP;
  }

  public boolean isFilterLive() {
    return filterLive;
  }

  public void setFilterLive(boolean filterLive) {
    this.filterLive = filterLive;
  }

  public boolean isFilterCompilation() {
    return filterCompilation;
  }

  public void setFilterCompilation(boolean filterCompilation) {
    this.filterCompilation = filterCompilation;
  }

  public boolean isFilterOther() {
    return filterOther;
  }

  public void setFilterOther(boolean filterOther) {
    this.filterOther = filterOther;
  }

  public boolean isFilterRemix() {
    return filterRemix;
  }

  public void setFilterRemix(boolean filterRemix) {
    this.filterRemix = filterRemix;
  }

}
