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

import com.danielme.muspyforandroid.model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Muspy rest api for users.
 */
public interface UserResource {

  @GET("user")
  Call<User> getUser(@Header("Authorization") String basic);

  @FormUrlEncoded
  @POST("user")
  Call<Void> createUser(@Field("email") String email,
                        @Field("password") String password,
                        @Field("activate") String activate);

  @FormUrlEncoded
  @PUT("user/{userId}")
  Call<Void> updateUser(@Header("Authorization") String basic,
                        @Path("userId") String userId,
                        @Field("email") String email,
                        @Field("notify") boolean notify,
                        @Field("notify_single") boolean single,
                        @Field("notify_other") boolean other,
                        @Field("notify_live") boolean live,
                        @Field("notify_ep") boolean ep,
                        @Field("notify_compilation") boolean compilation,
                        @Field("notify_album") boolean album,
                        @Field("notify_remix") boolean remix);
}