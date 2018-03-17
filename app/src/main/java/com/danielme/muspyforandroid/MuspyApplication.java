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
package com.danielme.muspyforandroid;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.squareup.leakcanary.LeakCanary;

import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

/**
 * Builds the dagger components.
 */
public class MuspyApplication extends Application {

  private ApplicationDaggerComponent applicationDaggerComponent;

  //cache with the covers url in MB resolved via scraping
  private static final Map<String, String> coversUrl = new HashMap<>();

  @Override
  public void onCreate() {
    super.onCreate();

    if (LeakCanary.isInAnalyzerProcess(this)) {
      // This process is dedicated to LeakCanary for heap analysis.
      // You should not init your app in this process.
      return;
    }
    LeakCanary.install(this);

    // Set up Crashlytics, disabled for debug builds
    Crashlytics crashlyticsKit = new Crashlytics.Builder()
        .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
        .build();

    // Initialize Fabric with the debug-disabled crashlytics.
    Fabric.with(this, crashlyticsKit);

    applicationDaggerComponent = DaggerApplicationDaggerComponent.builder()
        .applicationDaggerModule(new ApplicationDaggerModule(this))
        .build();

    //avoids memory leak, the Connectivity manager is asociated to the application context instead
    //of an Activity
    getSystemService(Context.CONNECTIVITY_SERVICE);
  }

  public ApplicationDaggerComponent getApplicationDaggerComponent() {
    return applicationDaggerComponent;
  }

  public static String getCoverUrl(String mbid) {
    return coversUrl.get(mbid);
  }

  public static synchronized void addCoverUrl(String mbid, String url) {
    coversUrl.put(mbid, url);
  }

}