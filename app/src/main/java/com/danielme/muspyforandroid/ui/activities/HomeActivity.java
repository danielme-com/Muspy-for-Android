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
import android.os.Parcel;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.danielme.muspyforandroid.MuspyApplication;
import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.service.UserService;
import com.danielme.muspyforandroid.ui.ViewUtils;
import com.danielme.muspyforandroid.ui.fragments.AccountFragment;
import com.danielme.muspyforandroid.ui.fragments.MyArtistsFragment;
import com.danielme.muspyforandroid.ui.fragments.ReleasesFragment;
import com.danielme.muspyforandroid.ui.recyclerview.DialogFragment;

import java.util.NoSuchElementException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This is the main screen of the app. It relays on a ViewPager with fragments. Only the data of
 * the first page-fragment is loaded when this activity is created.
 */
public class HomeActivity extends AbstractBaseActivity {

  private static final int ALPHA_SELECTED = 255;
  private static final int ALPHA_UNSELECTED = 128;
  private static final int NUM_TABS = 3;

  @Inject
  UserService userService;

  @BindView(R.id.tabs)
  TabLayout tabLayout;
  @BindView(R.id.viewpager)
  ViewPager viewPager;

  private Menu menu;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ((MuspyApplication) getApplication()).getApplicationDaggerComponent().inject(this);

    setContentView(R.layout.activity_home);
    ButterKnife.bind(this);
    ViewUtils.initDefaultToolbarUpNavigationListener(this, null);

    viewPager.setAdapter(new HomePageAdapter(getSupportFragmentManager()));
    viewPager.setOffscreenPageLimit(NUM_TABS);

    viewPager.setPageMargin(getResources()
            .getDimensionPixelSize(R.dimen.view_pager_margin));
    viewPager.setPageMarginDrawable(R.drawable.view_page_margin);

    tabLayout.setTabMode(TabLayout.MODE_FIXED);
    tabLayout.setupWithViewPager(viewPager);
    tabLayout.getTabAt(0).setIcon(R.drawable.tab_releases);
    tabLayout.getTabAt(1).setIcon(R.drawable.tab_artists);
    tabLayout.getTabAt(2).setIcon(R.drawable.tab_account);

    selectIcon(0);

    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //nothing here
      }

      @Override
      public void onPageSelected(int position) {
        selectIcon(position);
      }

      @Override
      public void onPageScrollStateChanged(int state) {
        //nothing here
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    this.menu = menu;
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.activity_home_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem menuItem) {
    switch (menuItem.getItemId()) {
      case R.id.action_import:
        navController.gotoLastfm(this);
        return true;
      case R.id.action_about:
        navController.gotoAbout(this);
        return true;
      case R.id.action_logout:
        ViewUtils.showYesNoDialogFragment(this, android.R.string.dialog_alert_title,
            R.string.logout_question, new DialogFragment.OkDialogFragmentListener() {
          @Override
          public void onOkDialogFragment() {
            userService.deleteCredentials();
            navController.gotoWelcome(HomeActivity.this);
          }

          @Override
          public int describeContents() {
            return 0;
          }

          @Override
          public void writeToParcel(Parcel dest, int flags) {
            //nothing here
          }
        });
        return true;
      default:
        return super.onOptionsItemSelected(menuItem);
    }
  }

  public Menu getMenu() {
    return menu;
  }

  /**
   * Changes the icon alpha to indicates which tab is selected.
   */
  private void selectIcon(int position) {
    for (int i = 0; i < tabLayout.getTabCount(); i++) {
      if (i == position) {
        tabLayout.getTabAt(i).getIcon().setAlpha(ALPHA_SELECTED);
      } else {
        tabLayout.getTabAt(i).getIcon().setAlpha(ALPHA_UNSELECTED);
      }
    }
  }

  static class HomePageAdapter extends FragmentPagerAdapter {

    HomePageAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public int getCount() {
      return NUM_TABS;
    }

    @Override
    public Fragment getItem(int position) {
      switch (position) {
        case 0:
          return new ReleasesFragment();
        case 1:
          return new MyArtistsFragment();
        case 2:
          return new AccountFragment();
        default:
          throw new NoSuchElementException("the requested fragment does not exist");
      }
    }

  }

}