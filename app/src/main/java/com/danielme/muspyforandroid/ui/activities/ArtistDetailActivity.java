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

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.danielme.muspyforandroid.MuspyApplication;
import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.exceptions.ForbiddenUnauthorizedException;
import com.danielme.muspyforandroid.model.Artist;
import com.danielme.muspyforandroid.service.ArtistService;
import com.danielme.muspyforandroid.service.UserService;
import com.danielme.muspyforandroid.ui.ViewUtils;
import com.danielme.muspyforandroid.ui.fragments.ArtistReleasesFragment;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * Displays the artists information (i.e. releases) and allows foollow/unfollow.
 */
public class ArtistDetailActivity extends AbstractBaseActivity {

  public static final String ARTIST_INTENT = "artist";
  public static final String BROADCAST_ACTION_ARTIST = "com.danielme.muspyforandroid.ACTION_ARTIST";

  @Inject
  ArtistService artistService;
  @Inject
  UserService userService;

  private Artist artist;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((MuspyApplication) getApplicationContext()).getApplicationDaggerComponent().inject(this);

    setContentView(R.layout.activity_artist_detail);
    ButterKnife.bind(this);
    artist = getIntent().getExtras().getParcelable(ARTIST_INTENT);
    ViewUtils.initDefaultToolbarUpNavigationListener(this, artist.getName());

    ArtistReleasesFragment artistReleasesFragment = new ArtistReleasesFragment();
    Bundle bundle = new Bundle();
    bundle.putParcelable(ARTIST_INTENT, artist);
    artistReleasesFragment.setArguments(bundle);
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.add(R.id.layoutArtistDetail, artistReleasesFragment);
    fragmentTransaction.commit();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.activity_artist_detail_menu, menu);
    if (artist.isFollowing()) {
      menu.findItem(R.id.action_follow).setVisible(false);
    } else {
      menu.findItem(R.id.action_unfollow).setVisible(false);
    }
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem menuItem) {
    switch (menuItem.getItemId()) {
      case R.id.action_follow:
      case R.id.action_unfollow:
        (new EditArtistAsyncTask(menuItem)).execute();
        return true;
      default:
        return super.onOptionsItemSelected(menuItem);
    }
  }

  /**
   * Follow and unfollow action. If the operation is succesful sends a broadcast
   * {@link ArtistDetailActivity#BROADCAST_ACTION_ARTIST} to update  any dependent UI-
   */
  class EditArtistAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private final MenuItem menuItem;

    EditArtistAsyncTask(MenuItem menuItem) {
      this.menuItem = menuItem;
    }

    @Override
    protected void onPreExecute() {
      if (!ViewUtils.isNetworkConnected(ArtistDetailActivity.this)) {
        cancel(true);
        ViewUtils.showYesDialogFragment(ArtistDetailActivity.this, android.R.string
                .dialog_alert_title, R.string.noconnection, null);
      } else {
        menuItem.setActionView(R.layout.progressbar);
        menuItem.expandActionView();
      }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
      if (!isCancelled()) {
        try {
          if (menuItem.getItemId() == R.id.action_follow) {
            return artistService.followArtist(artist.getMbid());
          } else {
              return artistService.unfollowArtist(artist.getMbid());
          }
        } catch (ForbiddenUnauthorizedException ex) {
            userService.deleteCredentials();
            navController.gotoWelcome(ArtistDetailActivity.this);
        } catch (Exception ex) {
            Log.e(ArtistDetailActivity.class.getCanonicalName(), "error adding a new artist "
                + artist.getMbid(), ex);
        }
      }
      return Boolean.FALSE;
    }

    @Override
    protected void onPostExecute(Boolean result) {
      ArtistDetailActivity.this.invalidateOptionsMenu();
      String msg;
      if (result) {
        artist.setFollowing(!artist.isFollowing());
        notifyResult();
        if (menuItem.getItemId() == R.id.action_follow) {
          msg = getString(R.string.follow_success, artist.getName());
        } else {
          msg = getString(R.string.unfollow_success, artist.getName());
        }
      } else {
        if (menuItem.getItemId() == R.id.action_follow) {
          msg = getString(R.string.follow_error, artist.getName());
        } else {
          msg = getString(R.string.unfollow_error, artist.getName());
        }
      }
      if (userService.userHasCredentials()) {
        Toast.makeText(ArtistDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
      }
    }

    /**
     * Sends broadcast {@link ArtistDetailActivity#BROADCAST_ACTION_ARTIST}. The intent includes
     * the artist ({@link ArtistDetailActivity#ARTIST_INTENT}).
     */
    private void notifyResult() {
      Intent intent = new Intent();
      intent.setAction(BROADCAST_ACTION_ARTIST);
      intent.putExtra(ARTIST_INTENT, artist);
      LocalBroadcastManager.getInstance(ArtistDetailActivity.this).sendBroadcast(intent);
    }

  }

}