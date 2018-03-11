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

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.ui.ViewUtils;
import com.danielme.muspyforandroid.ui.fragments.WebViewFragment;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AboutActivity extends AbstractBaseActivity {

  private static final int NUM_TABS = 2;

  @BindView(R.id.tabs)
  TabLayout tabLayout;
  @BindView(R.id.viewpager)
  ViewPager viewPager;

  private Menu menu;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_about);
    ButterKnife.bind(this);
    Toolbar toolbar = ViewUtils.initDefaultToolbarUpNavigationListener(this);

    //issue: toolbar elevation is displayed between the toolbar and the tab layout
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      toolbar.setElevation(0f);
    }

    viewPager.setAdapter(new AboutPageAdapter(getSupportFragmentManager()));
    viewPager.setOffscreenPageLimit(NUM_TABS);

    tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    tabLayout.setupWithViewPager(viewPager);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    this.menu = menu;
    getMenuInflater().inflate(R.menu.activity_about_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem menuItem) {
    switch (menuItem.getItemId()) {
      case R.id.action_play:
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.danielme.muspyforandroid"));
        try {
          startActivity(intent);
        } catch (ActivityNotFoundException e) {
          ViewUtils.launchUrlWithCustomTab("https://play.google.com/store/apps/details?id=com"
              + ".danielme.muspyforandroid", this);
        }
        return true;
      default:
        return super.onOptionsItemSelected(menuItem);
    }
  }

  public Menu getMenu() {
    return menu;
  }

  class AboutPageAdapter extends FragmentPagerAdapter {
    private final int[] titles = {R.string.tab_details, R.string.tab_license};

    AboutPageAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public int getCount() {
      return NUM_TABS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return getString(titles[position]);
    }

    @Override
    public Fragment getItem(int position) {
      WebViewFragment webViewlFragment = new WebViewFragment();
      String html;
      try {
        if (position == 0) {
          html = ViewUtils.loadFromAssets(AboutActivity.this, "about.html").replace(
              "@about_muspy_main", getString(R.string.about_muspy));
          html = html.replace("@about_muspy_android", getString(R.string.about_muspy_android));
          html = html.replace("@about_libraries", getString(R.string.about_libraries));
          html = html.replace("@about_pp", getString(R.string.about_pp));
        } else {
          html = ViewUtils.loadFromAssets(AboutActivity.this, "license.html");
        }
      } catch (Exception ex) {
        Log.e(AboutActivity.this.getClass().getCanonicalName(), ex.getMessage(), ex);
        Crashlytics.logException(ex);
        html = "";
      }
      webViewlFragment.setHtml(html);
      return webViewlFragment;
    }

  }

}
