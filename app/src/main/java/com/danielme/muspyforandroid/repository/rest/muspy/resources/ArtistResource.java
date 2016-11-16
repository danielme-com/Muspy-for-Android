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
package com.danielme.muspyforandroid.repository.rest.muspy.resources;

import com.danielme.muspyforandroid.model.Artist;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Muspy rest api for artists.
 */
public interface ArtistResource {


  @GET("artists/{userId}")
  Call<ArrayList<Artist>> getArtists(@Header("Authorization") String basic,
                                     @Path("userId") String userId);

  @PUT("artists/{userId}/{mbid}")
  Call<Void> followArtist(@Header("Authorization") String basic,
                          @Path("userId") String userId,
                          @Path("mbid") String mbid);

  @DELETE("artists/{userId}/{mbid}")
  Call<Void> unfollowArtist(@Header("Authorization") String basic,
                            @Path("userId") String userId,
                            @Path("mbid") String mbid);

  @FormUrlEncoded
  @PUT("artists/{userId}")
  Call<Void> importFromLastfm(@Header("Authorization") String basic,
                              @Path("userId") String userId,
                              @Field("import") String type,
                              @Field("username") String username,
                              @Field("count") String top,
                              @Field("period") String period);

}