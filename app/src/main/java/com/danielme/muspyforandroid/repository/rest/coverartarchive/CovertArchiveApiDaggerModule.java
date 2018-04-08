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
package com.danielme.muspyforandroid.repository.rest.coverartarchive;

import android.app.Application;

import com.danielme.muspyforandroid.repository.rest.CacheConfiguration;
import com.danielme.muspyforandroid.repository.rest.RetrofitFactory;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module.
 */
@Module
public final class CovertArchiveApiDaggerModule {

  private static final String AGENT = "MuspyForAndroid/2.2 ( danielme_com@yahoo.com )";
  private static final String URL = "https://coverartarchive.org";
  private static final int CACHE_TIME = 240 * 60 * 60; //10 days

  @Provides
  @Singleton
  public CoverResource createCoverResource(Application application) {
    Map<String, String> headers = new HashMap<>(1);
    headers.put("User-Agent", AGENT);

    return RetrofitFactory.getResource(CoverResource.class, URL, null, headers,
            buildCache(application));
  }

  private CacheConfiguration buildCache(Application application) {
    return new CacheConfiguration(application.getCacheDir().getAbsolutePath(), CACHE_TIME);
  }

}