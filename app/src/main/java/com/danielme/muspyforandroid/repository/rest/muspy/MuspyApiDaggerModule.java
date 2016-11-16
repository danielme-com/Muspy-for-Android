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

package com.danielme.muspyforandroid.repository.rest.muspy;

import android.support.annotation.NonNull;

import com.danielme.muspyforandroid.repository.rest.RetrofitFactory;
import com.danielme.muspyforandroid.repository.rest.muspy.resources.ArtistResource;
import com.danielme.muspyforandroid.repository.rest.muspy.resources.ReleaseResource;
import com.danielme.muspyforandroid.repository.rest.muspy.resources.UserResource;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module.
 */
@Module
public class MuspyApiDaggerModule {

  private static final String URL = "https://muspy.com/api/1/";
  private static final String AGENT = "Muspy for Android";

  @Provides
  @Singleton
  ArtistResource providesArtistResource() {
    return createResource(ArtistResource.class);
  }

  @Provides
  @Singleton
  ReleaseResource providesReleaseResource() {
    return createResource(ReleaseResource.class);
  }

  @Provides
  @Singleton
  UserResource providesUserResource() {
    return createResource(UserResource.class);
  }

  private <T> T createResource(@NonNull Class<T> resourceClass) {
    Map<String, String> headers = new HashMap<>(1);
    headers.put("User-Agent", AGENT);
    return RetrofitFactory.getResource(resourceClass, URL, null, headers, null);
  }
}