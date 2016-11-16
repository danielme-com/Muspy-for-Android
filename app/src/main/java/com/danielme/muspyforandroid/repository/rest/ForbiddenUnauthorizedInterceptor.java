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
package com.danielme.muspyforandroid.repository.rest;

import com.danielme.muspyforandroid.exceptions.ForbiddenUnauthorizedException;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Throws an exception if the http code of a response is 401 or 403.
 */
class ForbiddenUnauthorizedInterceptor implements Interceptor {

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();

    Response response = chain.proceed(request);
    if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED
        || response.code() == HttpURLConnection.HTTP_FORBIDDEN) {
      //important: dont forget to close the response
      response.body().close();
      throw new ForbiddenUnauthorizedException(request.url().toString());
    }
    return response;
  }

}
