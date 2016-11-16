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

package com.danielme.muspyforandroid.service;

import com.danielme.muspyforandroid.repository.rest.muspy.resources.ArtistResource;
import com.danielme.muspyforandroid.repository.rest.muspy.resources.ReleaseResource;
import com.danielme.muspyforandroid.repository.rest.muspy.resources.UserResource;
import com.danielme.muspyforandroid.service.impl.ArtistServiceImpl;
import com.danielme.muspyforandroid.service.impl.ReleaseServiceImpl;
import com.danielme.muspyforandroid.service.impl.UserServiceImpl;
import com.securepreferences.SecurePreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module.
 */
@Module
public class ServiceDaggerModule {

  @Provides
  @Singleton
  ArtistService providesArtistService(ArtistResource artistResource,
              com.danielme.muspyforandroid.repository.rest.musicbrainz.resources.ArtistResource
                                          artistResourceMB, UserService userService) {
    return new ArtistServiceImpl(artistResource, artistResourceMB, userService);
  }

  @Provides
  @Singleton
  ReleaseService providesReleaseService(ReleaseResource releaseResource,
             com.danielme.muspyforandroid.repository.rest.musicbrainz.resources.ReleaseResource
                                            releaseResourceMB, UserService userService) {
    return new ReleaseServiceImpl(releaseResource, releaseResourceMB, userService);
  }

  @Provides
  @Singleton
  UserService providesUserService(SecurePreferences securePreferences, UserResource userResource,
            com.danielme.muspyforandroid.repository.rest.lastfm.resources.UserResource
                userResourceLfm) {
    return new UserServiceImpl(securePreferences, userResource, userResourceLfm);
  }

}