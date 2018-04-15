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
 *
 */
package com.danielme.muspyforandroid.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.crashlytics.android.Crashlytics;
import com.danielme.muspyforandroid.MuspyApplication;
import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.exceptions.ForbiddenUnauthorizedException;
import com.danielme.muspyforandroid.model.Credential;
import com.danielme.muspyforandroid.model.Release;
import com.danielme.muspyforandroid.service.ReleaseService;
import com.danielme.muspyforandroid.service.UserService;
import com.danielme.muspyforandroid.ui.ViewUtils;
import com.danielme.muspyforandroid.ui.activities.ReleaseActivity;
import com.securepreferences.SecurePreferences;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import javax.inject.Inject;


/**
 * IntentService --> asynchronous An {@code IntentService} that updates the homscreen widget.
 */
public class WidgetIntentService extends IntentService {

  public static final String INTENT_WIDGET = "com.danielme.muspyforandroid.UPDATE_WIDGET";
  public static final String INTENT_PARAM_REFRESH = "refresh";
  public static final String WIDGET_MSG = "WIDGET_MSG";

  private static final String TAG = "Muspy Widget";


  @Inject
  UserService userService;
  @Inject
  ReleaseService releaseService;
  @Inject
  SecurePreferences securePreferences;

  public WidgetIntentService(String name) {
    super(name);
  }

  public WidgetIntentService() {
    super("WidgetIntentService");
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();

    ((MuspyApplication) getApplicationContext()).getApplicationDaggerComponent().inject(this);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    String userID = null;
    intent.getExtras();

    Credential credentials = userService.getCredentials();
    if (credentials != null) {
      userID = credentials.getUserId();
    }

    RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(),
        R.layout.widget);
    if (!ViewUtils.isNetworkConnected(this)) {
      if (securePreferences.getInt(WidgetIntentService.WIDGET_MSG, -1) == -1) {
        //keep displaying the previous data
        remoteViews.setViewVisibility(R.id.buttonRefresh, View.INVISIBLE);
        remoteViews.setViewVisibility(R.id.dataLayout, View.VISIBLE);
        remoteViews.setViewVisibility(R.id.textViewMessage, View.INVISIBLE);
        remoteViews.setViewVisibility(R.id.msgLayout, View.INVISIBLE);
      } else {
        showMessageWidget(remoteViews, R.string.noconnection, getApplicationContext());
      }
    } else if (userID == null) {
      showMessageWidget(remoteViews, R.string.nocredentials, getApplicationContext());
    } else {
      loadReleasesFromMuspy(remoteViews);
    }

    //refresh button
    Intent intentRefresh = new Intent(INTENT_WIDGET);
    //informs the widget provider that the intent has been send with the widget button
    intentRefresh.putExtra(INTENT_PARAM_REFRESH, true);

    int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
    intentRefresh.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
            appWidgetId, intentRefresh, PendingIntent.FLAG_UPDATE_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.buttonRefresh, pendingIntent);

    //dont forget to update the widget!!!
    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());
    appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    Log.d(TAG, "updating widget");
    stopSelf();
  }

  private void loadReleasesFromMuspy(RemoteViews remoteViews) {
    try {
      List<Release> releases = releaseService.getReleasesByUser(0, 3);

      if (!releases.isEmpty()) {
        remoteViews.setViewVisibility(R.id.dataLayout, View.VISIBLE);
        remoteViews.setViewVisibility(R.id.buttonRefresh, View.GONE);
        remoteViews.setViewVisibility(R.id.msgLayout, View.GONE);
        securePreferences.edit().putInt(WIDGET_MSG, -1).apply();
        showRelease(remoteViews, releases.get(0), R.id.textViewRelease1Date1, R.id.textViewRelease1,
                R.id.cover1, R.id.dataLayout1);
        if (releases.size() > 1) {
          showRelease(remoteViews, releases.get(1), R.id.textViewRelease1Date2,
                  R.id.textViewRelease2, R.id.cover2, R.id.dataLayout2);
          if (releases.size() > 2) {
            showRelease(remoteViews, releases.get(2), R.id.textViewDate3, R.id.textViewRelease3,
                    R.id.cover3, R.id.dataLayout3);
          } else {
            hideRelease(remoteViews, R.id.dataLayout3);
          }
        } else {
          hideRelease(remoteViews, R.id.dataLayout2);
          hideRelease(remoteViews, R.id.dataLayout3);
        }
      } else {
        showMessageWidget(remoteViews, R.string.nodata, getApplicationContext());
      }
    } catch (ForbiddenUnauthorizedException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      userService.deleteCredentials();
      showMessageWidget(remoteViews, R.string.nocredentials, getApplicationContext());
    } catch (UnknownHostException  | SocketTimeoutException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      showMessageWidget(remoteViews, R.string.noconnection, getApplicationContext());
    } catch (Exception ex) {
      Log.e(TAG, ex.getMessage(), ex);
      Crashlytics.logException(ex);
      showMessageWidget(remoteViews, R.string.unknown_error, getApplicationContext());
    }
  }

  private void hideRelease(RemoteViews remoteViews, int layout) {
    remoteViews.setViewVisibility(layout, View.INVISIBLE);
  }

  private void showRelease(RemoteViews remoteViews, Release release, int textViewRelease1Date,
                           int textViewRelease, int cover, int layout) throws IOException {
    remoteViews.setViewVisibility(layout, View.VISIBLE);
    remoteViews.setTextViewText(textViewRelease1Date, ViewUtils.localizedDate(
            release.getDate()).getString());
    remoteViews.setTextViewText(textViewRelease, release.getArtist().getName());

    setCover(remoteViews, release, cover);

    //Intent for showing release details inside Muspy app when clicking the release in the widget
    Intent intentRelease = new Intent(this, ReleaseActivity.class);
    intentRelease.putExtra(ReleaseActivity.RELEASE_INTENT, release);
    remoteViews.setOnClickPendingIntent(layout, PendingIntent.getActivity(
            getApplicationContext(), layout, intentRelease, PendingIntent.FLAG_UPDATE_CURRENT));
  }

  private void showMessageWidget(RemoteViews remoteView, int stringId, Context context) {
    remoteView.setTextViewText(R.id.textViewMessage, context.getString(stringId));
    remoteView.setViewVisibility(R.id.buttonRefresh, View.VISIBLE);
    remoteView.setViewVisibility(R.id.dataLayout, View.INVISIBLE);
    remoteView.setViewVisibility(R.id.textViewMessage, View.VISIBLE);
    remoteView.setViewVisibility(R.id.msgLayout, View.VISIBLE);

    securePreferences.edit().putInt(WIDGET_MSG, stringId).apply();
  }

  public void setCover(RemoteViews remoteViews, Release release, int coverId) {
    String link = releaseService.getCover(release.getMbid());
    if (TextUtils.isEmpty(link)) {
      link = release.getCoverUrl();
    }

    Bitmap bitmap;
    try {
      bitmap = BitmapFactory.decodeStream(new URL(link)
              .openConnection()
              .getInputStream());
    } catch (Exception ex) {
      Log.e(TAG, ex.getMessage(), ex);
      bitmap = BitmapFactory.decodeResource(this.getResources(),
              R.drawable.cover_example);
    }
    remoteViews.setImageViewBitmap(coverId, Bitmap.createScaledBitmap(bitmap, 120, 120, false));
  }

}