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

package com.danielme.muspyforandroid;


import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.danielme.muspyforandroid.model.Artist;
import com.danielme.muspyforandroid.model.Release;
import com.danielme.muspyforandroid.ui.activities.AboutActivity;
import com.danielme.muspyforandroid.ui.activities.ArtistDetailActivity;
import com.danielme.muspyforandroid.ui.activities.HomeActivity;
import com.danielme.muspyforandroid.ui.activities.LastfmActivity;
import com.danielme.muspyforandroid.ui.activities.LoginActivity;
import com.danielme.muspyforandroid.ui.activities.RegisterActivity;
import com.danielme.muspyforandroid.ui.activities.ReleaseActivity;
import com.danielme.muspyforandroid.ui.activities.ResetActivity;
import com.danielme.muspyforandroid.ui.activities.SearchArtistActivity;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Handles the navigation between activities inside the app.
 */
@Singleton
public class NavigationController {

  @Inject
  public NavigationController() {
    //used by dagger
  }

  public void gotoLastfm(@NonNull AppCompatActivity activity) {
    activity.startActivity(new Intent(activity, LastfmActivity.class));
  }

  public void gotoAbout(@NonNull AppCompatActivity activity) {
    activity.startActivity(new Intent(activity, AboutActivity.class));
  }

  /**
   * LoginActivity will be on top.
   */
  public void gotoLogin(@NonNull Activity activity) {
    Intent intent = new Intent(activity, LoginActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    activity.startActivity(intent);
  }

  /**
   * HomeActivity will be on top.
   */
  public void gotoHome(@NonNull Activity activity) {
    Intent intent = new Intent(activity, HomeActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    activity.startActivity(intent);
  }

  public void gotoRegister(@NonNull Activity activity) {
    activity.startActivity(new Intent(activity, RegisterActivity.class));
  }

  public void gotoReset(@NonNull Activity activity, String email) {
    Intent intent = new Intent(activity, ResetActivity.class);
    intent.putExtra(ResetActivity.EXTRA_EMAIL, email);
    activity.startActivity(intent);
  }

  public void gotoReleaseDetail(@NonNull FragmentActivity activity, Release release) {
    Intent intent = new Intent(activity, ReleaseActivity.class);
    intent.putExtra(ReleaseActivity.RELEASE_INTENT, release);
    activity.startActivity(intent);
  }

  public void gotoArtistDetail(@NonNull FragmentActivity activity, Artist artist) {
    Intent intent = new Intent(activity, ArtistDetailActivity.class);
    intent.putExtra(ArtistDetailActivity.ARTIST_INTENT, artist);
    activity.startActivity(intent);
  }

  /**
   * The followed artists are sent to the search activity, this avoid the needing of calling
   * to muspy api.
   */
  public void gotoSearchArtist(@NonNull FragmentActivity activity,
                               ArrayList<Parcelable> artistsFromAdapter) {
    Intent intent = new Intent(activity, SearchArtistActivity.class);
    intent.putParcelableArrayListExtra(SearchArtistActivity.INTENT_ARTISTS, artistsFromAdapter);
    activity.startActivity(intent);
  }

}