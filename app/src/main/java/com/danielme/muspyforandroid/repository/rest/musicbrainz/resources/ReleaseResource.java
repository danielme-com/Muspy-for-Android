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
package com.danielme.muspyforandroid.repository.rest.musicbrainz.resources;

import com.danielme.muspyforandroid.model.ReleaseMBWrapper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * MusiucBrainz rest api for releases.
 */
public interface ReleaseResource {

  /**
   * Retrieves all the releses that belong to a certain group, includes tracklist.
   * @param releaseGroupId mbid release group. It's the id of a release in Muspy.
   */
  @GET("release?fmt=json&inc=recordings+labels")
  Call<ReleaseMBWrapper> getReleasesAndTracklist(@Query("release-group") String releaseGroupId);

}
