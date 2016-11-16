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

import com.danielme.muspyforandroid.exceptions.ForbiddenUnauthorizedException;
import com.danielme.muspyforandroid.repository.rest.muspy.resources.ArtistResource;
import com.danielme.muspyforandroid.exceptions.HttpStatusException;
import com.danielme.muspyforandroid.model.Artist;
import com.danielme.muspyforandroid.model.ArtistMb;
import com.danielme.muspyforandroid.model.Credential;
import com.danielme.muspyforandroid.service.ArtistService;
import com.danielme.muspyforandroid.service.UserService;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Singleton class (Dagger) that handles the artists.
 */
@Singleton
public class ArtistServiceImpl implements ArtistService {

  private static final String DISAMBIGUATION_INFO = "if you don";
  private static final String IMPORT_LASTFM = "last.fm";

  private final ArtistResource artistResource;
  private final UserService userService;
  private final com.danielme.muspyforandroid.repository.rest.musicbrainz.resources.ArtistResource
      artistResourceMB;

  @Inject
  public ArtistServiceImpl(ArtistResource artistResource,
             com.danielme.muspyforandroid.repository.rest.musicbrainz.resources.ArtistResource
                           artistResourceMB, UserService userService) {
    this.artistResource = artistResource;
    this.artistResourceMB = artistResourceMB;
    this.userService = userService;
  }

  /**
   * Retrieves from Muspy the artists followed by the logged user.
   */
  @Override
  public ArrayList<Artist> getUserArtist() throws IOException {
    Credential credential = userService.getCredentials();
    if (credential == null) {
      throw new ForbiddenUnauthorizedException("stored credential is null");
    }

    Call<ArrayList<Artist>> releasesCall = artistResource.getArtists(credential.getBasicToken(),
        credential.getUserId());
    ArrayList results = releasesCall.execute().body();
    if (results == null) {
      results = new ArrayList(0);
    }
    Collections.sort(results, new Artist.NameComparator());
    return results;
  }

  /**
   * Searches an artists in MusicBrainz.
   */
  @Override
  public ArtistMb searchArtists(String name, int offset, int max) throws IOException,
      HttpStatusException {
    Call<ArtistMb> call = artistResourceMB.getArtists(name, max, offset);

    Response<ArtistMb> response = call.execute();
    if (response.code() != HttpURLConnection.HTTP_OK) {
      throw new HttpStatusException(response.code());
    }
    return response.body();
  }

  /**
   * Adds an artist in Muspy to the logged user.
   */
  @Override
  public boolean followArtist(String mbid) throws IOException {
    Credential credential = userService.getCredentials();

    Call<Void> followCall = artistResource.followArtist(credential.getBasicToken(),
        credential.getUserId(), mbid);
    return followCall.execute().code() == HttpURLConnection.HTTP_OK;
  }

  /**
   * Deletes an artist in Muspy for the logged user.
   */
  @Override
  public boolean unfollowArtist(String mbid) throws IOException {
    Credential credential = userService.getCredentials();
    Call<Void> followCall = artistResource.unfollowArtist(credential.getBasicToken(),
        credential.getUserId(), mbid);
    return followCall.execute().code() == HttpURLConnection.HTTP_NO_CONTENT;
  }

  /**
   * Just performs a conversion.
   */
  @Override
  public List<Artist> artistConversor(ArtistMb artistMb) {
    List<Artist> artists = new ArrayList<>();
    if (artistMb.getArtists() != null) {
      for (ArtistMb.Artist artistmb : artistMb.getArtists()) {
        Artist artist = new Artist();
        artist.setMbid(artistmb.getId());
        artist.setName(artistmb.getName());
        if (artistmb.getDisambiguation() != null && !artistmb.getDisambiguation().contains(
            DISAMBIGUATION_INFO)) {
          artist.setDisambiguation(artistmb.getDisambiguation());
        }
        artists.add(artist);
      }
    }
    return artists;
  }

  /**
   * Import the artists of a lastfm user into the muspy logged user.
   */
  @Override
  public boolean importLastfm(String user, String period, String top) throws IOException {
    Credential credential = userService.getCredentials();
    Call<Void> followCall = artistResource.importFromLastfm(credential.getBasicToken(),
        credential.getUserId(), IMPORT_LASTFM, user, top, period);
    return followCall.execute().code() == HttpURLConnection.HTTP_OK;
  }

}