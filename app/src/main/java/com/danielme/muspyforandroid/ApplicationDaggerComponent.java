/*
 *
 *  * Copyright (C) 2012-2016 Daniel Medina <http://danielme.com>
 *  *
 *  * This file is part of "Muspy for Android".
 *  *
 *  * "Muspy for Android" is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, version 3.
 *  *
 *  * "Muspy for Android" is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License version 3
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-3.0.html/>
 *
 */

package com.danielme.muspyforandroid;

import com.danielme.muspyforandroid.repository.rest.coverartarchive.CovertArchiveApiDaggerModule;
import com.danielme.muspyforandroid.repository.rest.lastfm.LastfmApiDaggerModule;
import com.danielme.muspyforandroid.repository.rest.musicbrainz.MusicBrainzApiDaggerModule;
import com.danielme.muspyforandroid.repository.rest.muspy.MuspyApiDaggerModule;
import com.danielme.muspyforandroid.service.ServiceDaggerModule;
import com.danielme.muspyforandroid.ui.activities.ArtistDetailActivity;
import com.danielme.muspyforandroid.ui.activities.HomeActivity;
import com.danielme.muspyforandroid.ui.activities.LaunchActivity;
import com.danielme.muspyforandroid.ui.activities.LoginActivity;
import com.danielme.muspyforandroid.ui.activities.RegisterActivity;
import com.danielme.muspyforandroid.ui.activities.ReleaseActivity;
import com.danielme.muspyforandroid.ui.activities.ResetActivity;
import com.danielme.muspyforandroid.ui.activities.WelcomeActivity;
import com.danielme.muspyforandroid.ui.fragments.AccountFragment;
import com.danielme.muspyforandroid.ui.fragments.ArtistReleasesFragment;
import com.danielme.muspyforandroid.ui.fragments.LastfmFragment;
import com.danielme.muspyforandroid.ui.fragments.MyArtistsFragment;
import com.danielme.muspyforandroid.ui.fragments.ReleaseFragment;
import com.danielme.muspyforandroid.ui.fragments.ReleasesFragment;
import com.danielme.muspyforandroid.ui.fragments.SearchArtistFragment;
import com.danielme.muspyforandroid.ui.recyclerview.GenericRecyclerViewFragment;
import com.danielme.muspyforandroid.widget.WidgetIntentService;
import com.danielme.muspyforandroid.widget.WidgetProvider;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationDaggerModule.class,
                      MuspyApiDaggerModule.class,
                      LastfmApiDaggerModule.class,
                      MusicBrainzApiDaggerModule.class,
                      ServiceDaggerModule.class,
                      CovertArchiveApiDaggerModule.class})
public interface ApplicationDaggerComponent {

  void inject(HomeActivity activity);
  void inject(LaunchActivity activity);
  void inject(ArtistDetailActivity artistDetailActivity);
  void inject(WidgetIntentService widgetIntentService);
  void inject(LoginActivity loginActivity);
  void inject(ResetActivity resetActivity);
  void inject(RegisterActivity registerActivity);
  void inject(AccountFragment accountFragment);
  void inject(LastfmFragment lastfmFragment);
  void inject(ReleasesFragment releasesFragment);
  void inject(ArtistReleasesFragment artistReleasesFragment);
  void inject(MyArtistsFragment myArtistsFragment);
  void inject(ReleaseFragment releaseFragment);
  void inject(SearchArtistFragment searchArtistFragment);
  void inject(ReleaseActivity releaseActivity);
  void inject(GenericRecyclerViewFragment genericRecyclerViewFragment);
  void inject(WidgetProvider widgetProvider);
  void inject(WelcomeActivity welcomeActivity);
}