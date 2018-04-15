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

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.danielme.muspyforandroid.MuspyApplication;
import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.service.UserService;
import com.danielme.muspyforandroid.ui.ViewUtils;
import com.danielme.muspyforandroid.ui.activities.LaunchActivity;
import com.securepreferences.SecurePreferences;

import javax.inject.Inject;

/**
 * Handles the homescreen widget. UI is updated by {@link WidgetIntentService}.
 */
public class WidgetProvider extends AppWidgetProvider {

  @Inject
  UserService userService;
  @Inject
  SecurePreferences securePreferences;



  @Override
  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);

    ((MuspyApplication) context.getApplicationContext()).getApplicationDaggerComponent()
        .inject(this);

    if (intent.getAction().equals(WidgetIntentService.INTENT_WIDGET)) {
      int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager
          .INVALID_APPWIDGET_ID);
      if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
        if (intent.getBooleanExtra(WidgetIntentService.INTENT_PARAM_REFRESH, false)
            && !userService.userHasCredentials()
            && securePreferences.getInt(WidgetIntentService.WIDGET_MSG, -1)
                == R.string.nocredentials
            && ViewUtils.isNetworkConnected(context)) {
          //button pressed, user is not logged and networking --> new login
          Intent appIntent = new Intent(context, LaunchActivity.class);
          appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          context.startActivity(appIntent);
        } else {
          updateWidget(context, widgetId);
        }
      }
    }
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    ((MuspyApplication) context.getApplicationContext()).getApplicationDaggerComponent()
            .inject(this);
    for (int i = 0; i < appWidgetIds.length; i++) {
      updateWidget(context, appWidgetIds[i]);
    }
  }

  /**
   * Shows progress bar and update widget UI immediately.
   */
  private void updateWidget(Context context, int appWidgetId) {
    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
    AppWidgetManager manager = AppWidgetManager.getInstance(context);
    manager.updateAppWidget(appWidgetId, remoteViews);

    //keep displaying the releases while updating
    if (securePreferences.getInt(WidgetIntentService.WIDGET_MSG, -1) != -1) {
      remoteViews.setTextViewText(R.id.textViewMessage, context.getString(R.string.loading));
      remoteViews.setViewVisibility(R.id.buttonRefresh, View.GONE);
      remoteViews.setViewVisibility(R.id.dataLayout, View.INVISIBLE);
      remoteViews.setViewVisibility(R.id.textViewMessage, View.VISIBLE);
      remoteViews.setViewVisibility(R.id.msgLayout, View.VISIBLE);
      manager.updateAppWidget(appWidgetId, remoteViews);
    }

    //asynchronous service
    Intent intent = new Intent(context.getApplicationContext(), WidgetIntentService.class);
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

    context.startService(intent);
  }

}