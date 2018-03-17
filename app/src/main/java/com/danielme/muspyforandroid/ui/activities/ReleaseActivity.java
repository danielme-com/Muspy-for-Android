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

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.danielme.muspyforandroid.MuspyApplication;
import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.model.Release;
import com.danielme.muspyforandroid.service.UserService;
import com.danielme.muspyforandroid.ui.ViewUtils;
import com.danielme.muspyforandroid.ui.fragments.ReleaseFragment;

import javax.inject.Inject;

/**
 * Displays the details of a release, including the tracklist, with a recyclerview in a fragment.
 */
public class ReleaseActivity extends AbstractBaseActivity {

  public static final String RELEASE_INTENT = "release";

  @Inject
  UserService userService;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_release);

    ((MuspyApplication) getApplicationContext()).getApplicationDaggerComponent().inject(this);

    Release release = getIntent().getExtras().getParcelable(RELEASE_INTENT);
    Toolbar toolbar = ViewUtils.initDefaultToolbarUpNavigationListener(this);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (ReleaseActivity.this.isTaskRoot()) {
          //this screen has been opened from the homescreen widget so there is no previous screen
          if (userService.userHasCredentials()) {
            navController.gotoHome(ReleaseActivity.this);
          } else {
            navController.gotoWelcome(ReleaseActivity.this);
          }
        } else {
          ReleaseActivity.this.onBackPressed();
        }
      }
    });
    //don't add the fragment when rotates
    if (savedInstanceState == null) {
      ReleaseFragment releaseFragment = new ReleaseFragment();
      Bundle bundle = new Bundle();
      bundle.putParcelable(RELEASE_INTENT, release);
      releaseFragment.setArguments(bundle);
      FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
      fragmentTransaction.add(R.id.layoutRelease, releaseFragment);
      fragmentTransaction.commit();
    }

  }

}