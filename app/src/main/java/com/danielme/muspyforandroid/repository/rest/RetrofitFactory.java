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

import android.support.annotation.NonNull;

import com.danielme.muspyforandroid.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A factory to isolate the creation of retrofit 2 resources. This factory should be used only in
 * Dagger modules.
 */
public final class RetrofitFactory {

  private static final int TIMEOUT = 20; //seconds
  private static final int CACHE_SIZE = 3 * 1024 * 1024; //5 MB

  private static final Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
      .addConverterFactory(GsonConverterFactory.create());

  private static final ForbiddenUnauthorizedInterceptor forbiddenInteceptor = new
      ForbiddenUnauthorizedInterceptor();

  private static Cache cache;

  private RetrofitFactory() {
    super();
  }

  /**
   * @param resourceClass resource interface
   * @param url          optional base url
   * @param interceptors optional interceptors
   * @param headers      optional headers FOR ALL REQUESTS
   * @param cacheConfiguration enables the disk lru cache with the provided configuration
   */
  public static <T> T getResource(@NonNull Class<T> resourceClass, String url,
                                  List<Interceptor> interceptors, Map<String, String> headers,
                                  CacheConfiguration cacheConfiguration) {
    if (url != null) {
      retrofitBuilder.baseUrl(url);
    }


    OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
    if (interceptors != null) {
      for (Interceptor interceptor : interceptors) {
        httpClientBuilder.addInterceptor(interceptor);
      }
    }

    if (cacheConfiguration != null) {
       applyCache(httpClientBuilder, cacheConfiguration);
    }

    httpClientBuilder.addInterceptor(forbiddenInteceptor);

    if (headers != null && !headers.isEmpty()) {
      httpClientBuilder.addInterceptor(new HeaderInterceptor(headers));
    }

    httpClientBuilder.readTimeout(TIMEOUT, TimeUnit.SECONDS);
    httpClientBuilder.connectTimeout(TIMEOUT, TimeUnit.SECONDS);

    if (BuildConfig.DEBUG) {
      HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
      loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
      httpClientBuilder.addInterceptor(loggingInterceptor);
    }

    OkHttpClient okHttpClient = httpClientBuilder.build();

    return retrofitBuilder.client(okHttpClient)
        .build()
        .create(resourceClass);
  }

  /**
   * http://stackoverflow.com/questions/29891139/retrofit-okhttp-response-cache-not-working
   */
  private static void applyCache(OkHttpClient.Builder httpClientBuilder, final CacheConfiguration cacheConfiguration) {
    if (cache == null) {
      File httpCacheDirectory = new File(cacheConfiguration.getCachePath(), "responses");
      cache = new Cache(httpCacheDirectory, CACHE_SIZE);
    }

    httpClientBuilder.cache(cache);

    httpClientBuilder.networkInterceptors().add(new Interceptor() {
      @Override
      public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
            .removeHeader("Access-Control-Allow-Origin")
            .removeHeader("Vary")
            .removeHeader("Age")
            .removeHeader("Via")
            .removeHeader("C3-Request")
            .removeHeader("C3-Domain")
            .removeHeader("C3-Date")
            .removeHeader("C3-Hostname")
            .removeHeader("C3-Cache-Control")
            .removeHeader("X-Varnish-back")
            .removeHeader("X-Varnish")
            .removeHeader("X-Cache")
            .removeHeader("X-Cache-Hit")
            .removeHeader("X-Varnish-front")
            .removeHeader("Connection")
            .removeHeader("Accept-Ranges")
            .removeHeader("Transfer-Encoding")
            .header("Cache-Control", "public, max-age=" + cacheConfiguration.getSeconds())
            .build();
      }
    });
  }

  private static class HeaderInterceptor implements Interceptor {

    private final Map<String, String> headers;

    HeaderInterceptor(@NonNull Map<String, String> headers) {
      this.headers = headers;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
      Request original = chain.request();

      Request.Builder requestBuilder = original.newBuilder()
          .method(original.method(), original.body());

      for (Map.Entry<String, String> header : headers.entrySet()) {
        requestBuilder.header(header.getKey(), header.getValue());
      }

      return chain.proceed(requestBuilder.build());
    }

  }

}