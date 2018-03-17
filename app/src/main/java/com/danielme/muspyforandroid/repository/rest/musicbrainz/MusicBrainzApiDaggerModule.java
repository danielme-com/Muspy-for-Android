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
package com.danielme.muspyforandroid.repository.rest.musicbrainz;

import android.app.Application;
import android.support.annotation.NonNull;

import com.danielme.muspyforandroid.repository.rest.CacheConfiguration;
import com.danielme.muspyforandroid.repository.rest.RetrofitFactory;
import com.danielme.muspyforandroid.repository.rest.musicbrainz.resources.ArtistResource;
import com.danielme.muspyforandroid.repository.rest.musicbrainz.resources.HtmlResource;
import com.danielme.muspyforandroid.repository.rest.musicbrainz.resources.ReleaseResource;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module.
 */
@Module
public final class MusicBrainzApiDaggerModule {

  private static final String AGENT = "MuspyForAndroid/2.1 ( danielme_com@yahoo.com )";
  private static final String URL = "https://musicbrainz.org/ws/2/";
  private static final int CACHE_TIME = 72 * 60 * 60; //3 days

  @Provides
  @Singleton
  public ArtistResource providesArtistResource(Application application) {
    return createResource(ArtistResource.class, application);
  }

  @Provides
  @Singleton
  public ReleaseResource createReleaseResource(Application application) {
    return createResource(ReleaseResource.class, application);
  }

  @Provides
  @Singleton
  public HtmlResource createHtmlResource(Application application) {
    return RetrofitFactory.getResource(HtmlResource.class, null, null, null,
            buildCache(application));
  }


  private <T> T createResource(@NonNull Class<T> resourceClass, Application application) {
    Map<String, String> headers = new HashMap<>(1);
    headers.put("User-Agent", AGENT);

    return RetrofitFactory.getResource(resourceClass, URL, null, headers,
            buildCache(application));
  }

  private CacheConfiguration buildCache(Application application) {
    return new CacheConfiguration(application.getCacheDir().getAbsolutePath(), CACHE_TIME);
  }

}