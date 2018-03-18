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
package com.danielme.muspyforandroid.ui;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import com.danielme.muspyforandroid.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class ExternalSearch {

  private static final Map<String, String> AMAZON_DOMAINS;

  static {
    AMAZON_DOMAINS = new HashMap<>(14);
    AMAZON_DOMAINS.put("US", "com");
    AMAZON_DOMAINS.put("ES", "es");
    AMAZON_DOMAINS.put("RC", "cn");
    AMAZON_DOMAINS.put("IN", "in");
    AMAZON_DOMAINS.put("JP", "co.jp");
    AMAZON_DOMAINS.put("FR", "fr");
    AMAZON_DOMAINS.put("DE", "de");
    AMAZON_DOMAINS.put("IT", "it");
    AMAZON_DOMAINS.put("NL", "nl");
    AMAZON_DOMAINS.put("GB", "co.uk");
    AMAZON_DOMAINS.put("CA", "ca");
    AMAZON_DOMAINS.put("MX", "com.mx");
    AMAZON_DOMAINS.put("AU", "com.au");
    AMAZON_DOMAINS.put("BR", "com.br");
  }

  private final String query;

  public ExternalSearch(String query) {
    this.query = query.replace("/", " ");;
  }

  public void openGooglePlay(Activity activity) throws UnsupportedEncodingException {
    Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
    intent.setData(Uri.parse("market://search?c=music&q=" + query));
    try {
      activity.startActivity(intent);
    } catch (ActivityNotFoundException ex) {
      ViewUtils.launchUrlWithCustomTab("https://play.google.com/store/search?c=music&q="
              + URLEncoder.encode(query, "UTF-8"), activity);
    }
  }

  public void openYoutube(Activity activity) throws UnsupportedEncodingException {
    Intent intent = new Intent(Intent.ACTION_SEARCH);
    intent.setPackage("com.google.android.youtube");
    intent.putExtra("query", query);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    try {
      activity.startActivity(intent);
    } catch (ActivityNotFoundException ex) {
      //youtube app? no problem
      ViewUtils.launchUrlWithCustomTab("https://www.youtube.com/results?search_query="
              + URLEncoder.encode(query, "UTF-8"), activity);
    }
  }

  public void openSpotify(AppCompatActivity activity) throws UnsupportedEncodingException {
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.setAction(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
    intent.setComponent(new ComponentName(
            "com.spotify.music",
            "com.spotify.music.MainActivity"));
    intent.putExtra(SearchManager.QUERY, query);
    try {
      activity.startActivity(intent);
    } catch (ActivityNotFoundException ex) {
      ViewUtils.showYesDialogFragment(activity, R.string.info, R.string.spotify_noapp, null);
    }
  }

  public void openDeezer(AppCompatActivity activity) {
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("deezer://www.deezer.com/search/" + query));
    try {
      activity.startActivity(intent);
    } catch (ActivityNotFoundException ex) {
      ViewUtils.showYesDialogFragment(activity, R.string.info, R.string.deezer_noapp, null);
    }
  }

  public void openLastFmRelease(Activity activity) throws UnsupportedEncodingException {
    ViewUtils.launchUrlWithCustomTab("http://www.lastfm.com/search/albums?q="
            + URLEncoder.encode(query, "UTF-8"), activity);
  }

  public void openLastFmArtists(Activity activity) throws UnsupportedEncodingException {
    ViewUtils.launchUrlWithCustomTab("http://www.lastfm.com/search/artists?q="
            + URLEncoder.encode(query, "UTF-8"), activity);
  }

  public void openGoogle(Activity activity) throws UnsupportedEncodingException {
    ViewUtils.launchUrlWithCustomTab("https://www.google.com/search?q="
            + URLEncoder.encode(query, "UTF-8"), activity);
  }

  public void openMusicBrainzArtist(Activity activity, String mbid) {
    ViewUtils.launchUrlWithCustomTab("https://musicbrainz.org/artist/"
            + mbid, activity);
  }

  public void openMusicBrainzRelease(Activity activity, String mbid) {
    ViewUtils.launchUrlWithCustomTab("https://musicbrainz.org/release/"
            + mbid, activity);
  }

  public void openAmazon(Activity activity) throws UnsupportedEncodingException {
    ViewUtils.launchUrlWithCustomTab("https://www.amazon." + getAmazonDomain(activity)
            + "/gp/aw/s/?k=" + URLEncoder.encode(query, "UTF-8"), activity);
  }

  private String getAmazonDomain(Activity activity) {
    String locale = activity.getResources().getConfiguration().locale.getCountry();
    String domain = AMAZON_DOMAINS.get(locale);
    if (domain == null) {
      domain = AMAZON_DOMAINS.get("US");
    }
    return domain;
  }

}
