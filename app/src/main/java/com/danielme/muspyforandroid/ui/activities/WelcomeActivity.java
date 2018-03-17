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
package com.danielme.muspyforandroid.ui.activities;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.danielme.muspyforandroid.MuspyApplication;
import com.danielme.muspyforandroid.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends AbstractBaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcome);

    ((MuspyApplication) getApplicationContext()).getApplicationDaggerComponent().inject(this);
    ButterKnife.bind(this);
    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
      findViewById(R.id.imageViewLogo).setVisibility(View.GONE);
    }

    Typeface typefaceRegular = Typeface.createFromAsset(getAssets(), "ASAP_REGULAR.TTF");
    ((TextView)findViewById(R.id.textViewWelcome)).setTypeface(typefaceRegular);
  }

  @OnClick(R.id.buttonSignIn)
  public void signin() {
    navController.gotoLogin(this);
  }

  @OnClick(R.id.buttonSignUp)
  public void signup() {
    navController.gotoRegister(this);
  }
}
