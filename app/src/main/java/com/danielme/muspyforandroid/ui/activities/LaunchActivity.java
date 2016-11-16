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
package com.danielme.muspyforandroid.ui.activities;

import android.app.Activity;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.danielme.muspyforandroid.MuspyApplication;
import com.danielme.muspyforandroid.NavigationController;
import com.danielme.muspyforandroid.service.UserService;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;


/**
 * Entry point of the app. Checks the credentials an redirects to the correct screen.
 * Important: this activity doesn't have an UI so it should use a transparent theme.
 */
public class LaunchActivity extends Activity {

  @Inject
  UserService userService;
  @Inject
  NavigationController navController;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Fabric.with(this, new Crashlytics());
    ((MuspyApplication) getApplication()).getApplicationDaggerComponent().inject(this);

    if (userService.userHasCredentials()) {
      navController.gotoHome(this);
    } else {
      navController.gotoLogin(this);
    }
  }

}