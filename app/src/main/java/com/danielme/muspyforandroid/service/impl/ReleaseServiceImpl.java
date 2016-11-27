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
package com.danielme.muspyforandroid.service.impl;

import android.util.Log;

import com.danielme.muspyforandroid.exceptions.ForbiddenUnauthorizedException;
import com.danielme.muspyforandroid.exceptions.HttpStatusException;
import com.danielme.muspyforandroid.model.Credential;
import com.danielme.muspyforandroid.model.Release;
import com.danielme.muspyforandroid.model.ReleaseMB;
import com.danielme.muspyforandroid.model.ReleaseMBWrapper;
import com.danielme.muspyforandroid.repository.rest.muspy.resources.ReleaseResource;
import com.danielme.muspyforandroid.service.ReleaseService;
import com.danielme.muspyforandroid.service.UserService;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Singleton (Dagger) that handles the releases.
 */
public class ReleaseServiceImpl implements ReleaseService {

  private static final int WAIT_ATTEMP = 2000; //wait 2 seconds to retry the call to MB
  private static final int MAX_ATTEMPS = 4;

  private final UserService userService;
  private final ReleaseResource releaseResource;
  private final com.danielme.muspyforandroid.repository.rest.musicbrainz.resources.ReleaseResource
      releaseResourceMB;

  public ReleaseServiceImpl(ReleaseResource releaseResource,
                            com.danielme.muspyforandroid.repository.rest.musicbrainz.resources
                                .ReleaseResource releaseResourceMB, UserService userService) {
    this.releaseResource = releaseResource;
    this.releaseResourceMB = releaseResourceMB;
    this.userService = userService;
  }

  /**
   * Retrieves from Muspy the releases of the artists followed by the logged user.
   */
  @Override
  public ArrayList<Release> getReleasesByUser(int offset, int limit) throws IOException,
      HttpStatusException {
    Credential credential = userService.getCredentials();
    if (credential == null) {
      throw new ForbiddenUnauthorizedException("stored credential is null");
    }
    Call<ArrayList<Release>> releasesCall = releaseResource.getReleasesByUser(
        credential.getUserId(), offset, limit);
    Response<ArrayList<Release>> response = releasesCall.execute();
    if (response.code() != HttpURLConnection.HTTP_OK) {
      if (response.code() == HttpURLConnection.HTTP_GONE) {
        //in this case 410 is equivalent to 401
        throw new ForbiddenUnauthorizedException("releases url gone");
      } else {
        throw new HttpStatusException(response.code());
      }
    }
    return response.body();
  }

  /**
   * Retrieves from Muspy the releases of an artists.
   */
  @Override
  public ArrayList<Release> getReleasesByArtist(int offset, int limit, String mbid)
      throws IOException, HttpStatusException {
    Call<ArrayList<Release>> releasesCall = releaseResource.getReleasesByArtist(offset, limit,
        mbid);
    Response<ArrayList<Release>> response = releasesCall.execute();
    if (response.code() != HttpURLConnection.HTTP_OK) {
      throw new HttpStatusException(response.code());
    }
    return response.body();
  }

  /**
   * Retrieves from MusicBrainz the releases of a release-group including the tracklist. Sometimes
   * MusicBrainz returns 503 (rate limit), in this case we make up to {@link
   * ReleaseServiceImpl#MAX_ATTEMPS} attemps with a delay (@link ReleaseServiceImpl#WAIT_ATTEMP)
   */
  @Override
  public ReleaseMB getReleasesAndTracklist(Release release) throws HttpStatusException,
      IOException {
    ReleaseMB ret = null;

    boolean ok = false;
    int attemps = 1;

    while (!ok && attemps <= MAX_ATTEMPS) {
      Call<ReleaseMBWrapper> call = releaseResourceMB.getReleasesAndTracklist(release.getMbid());
      Response<ReleaseMBWrapper> response = call.execute();
      switch (response.code()) {
        case HttpURLConnection.HTTP_OK:
          ok = true;
          List<ReleaseMB> releases = response.body().getReleases();
          ret = chooseRelease(releases, release);
          break;
        case HttpURLConnection.HTTP_UNAVAILABLE:
          attemps++;
          if (attemps <= MAX_ATTEMPS) {
            try {
              Thread.sleep(WAIT_ATTEMP);
            } catch (InterruptedException ex) {
              Log.e(this.getClass().getCanonicalName(), "getReleasesAndTracklist: sleep", ex);
            }
          }

          break;
        default:
          throw new HttpStatusException(response.code());
      }
    }

    return ret;
  }

  /**
   * Chooses a release from a release group.
   */
  private ReleaseMB chooseRelease(List<ReleaseMB> releases, Release release) {
    ReleaseMB selected = null;
    if (releases != null && !releases.isEmpty()) {
      List<ReleaseMB> temp = new LinkedList<>();
      //filter the releases group by date
      for (ReleaseMB releasemb : releases) {
        if (releasemb.getDate() != null && releasemb.getDate().equals(release.getDate())) {
          temp.add(releasemb);
        }
      }

      if (temp.isEmpty()) {
        selected = releases.get(0);
      } else {
        selected = temp.get(0);
        for (ReleaseMB rel : temp) {
          if ("US".equals(rel.getCountry())) {
            selected = rel;
            break;
          }
        }
      }

      if (selected.getDate() == null) {
        selected.setDate(release.getDate());
      }

    }

    return selected;
  }

}