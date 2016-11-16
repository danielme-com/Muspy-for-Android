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

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.danielme.muspyforandroid.model.Credential;
import com.danielme.muspyforandroid.model.User;
import com.danielme.muspyforandroid.repository.rest.muspy.resources.UserResource;
import com.danielme.muspyforandroid.service.UserService;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.securepreferences.SecurePreferences;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Singleton that handles the users. Logged users credentials are stored in Shared Preferences.
 */
@Singleton
public class UserServiceImpl implements UserService {

  private static final String ACTIVATE = "1";
  private static final String ERROR_EMAIL = "email already in use";
  private static final String ERROR_EMAIL_DB = "UNIQUE constraint failed";
  private static final String SP_EMAIL = "SP_EMAIL";
  private static final String SP_PASS = "SP_PASS";
  private static final String SP_ID = "SP_ID";
  private static final String URL_RESET = "https://muspy.com/reset";
  private static final String RESET_RESPONSE_MSG = "email has been sent to";

  private Credential credentials;
  private final UserResource userResource;
  private final com.danielme.muspyforandroid.repository.rest.lastfm.resources.UserResource
      userResourceLfm;
  private final SecurePreferences securePreferences;

  @Inject
  public UserServiceImpl(SecurePreferences securePreferences, UserResource userResource,
                         com.danielme.muspyforandroid.repository.rest.lastfm.resources.UserResource
                             userResourceLfm) {
    this.securePreferences = securePreferences;
    this.userResource = userResource;
    this.userResourceLfm = userResourceLfm;
  }

  @Override
  public boolean userHasCredentials() {
    return securePreferences.getString(SP_EMAIL, null) != null
        && securePreferences.getString(SP_PASS, null) != null
        && securePreferences.getString(SP_ID, null) != null;
  }

  @Override
  public void storeCredentials(User user, String pass) {
    securePreferences.edit().putString(SP_EMAIL, user.getEmail()).apply();
    if (pass != null) {
      securePreferences.edit().putString(SP_PASS, pass).apply();
    }
    securePreferences.edit().putString(SP_ID, user.getId()).apply();
    credentials = new Credential(user.getId(), user.getEmail(), pass);
  }

  @Override
  public Credential getCredentials() {
    if (credentials == null && securePreferences.getString(SP_ID, null) != null) {
      credentials = new Credential(
          securePreferences.getString(SP_ID, null),
          securePreferences.getString(SP_EMAIL, null),
          securePreferences.getString(SP_PASS, null));
    }
    return credentials;
  }

  /**
   * Deletes stored credentials and exits the app (displays the LoginActivity).
   */
  @Override
  public void deleteCredentials() {
    credentials = null;

    securePreferences.edit().putString(SP_EMAIL, null).apply();
    securePreferences.edit().putString(SP_PASS, null).apply();
    securePreferences.edit().putString(SP_ID, null).apply();
  }

  /**
   * Retrieves the user data from Muspy.
   */
  @Override
  public User getUser(@NonNull Credential credential) throws IOException {
    Call<User> userCall = userResource.getUser(credential.getBasicToken());
    return userCall.execute().body();
  }

  /**
   * Retrieves the user data from Muspy.
   */
  @Override
  public User getUser() throws IOException {
    Credential credential = getCredentials();
    return getUser(credential);
  }


  /**
   * Signs up a new user.
   *
   * @return {@link UserServiceImpl#CODE_OK} {@link UserServiceImpl#CODE_EMAIL_DUPLICATE} {@link
   * UserServiceImpl#CODE_ERROR}
   */
  @Override
  public Integer createUser(@NonNull String email, @NonNull String password) throws IOException {
    Call<Void> userCall = userResource.createUser(email, password, ACTIVATE);

    Response<Void> execute = userCall.execute();
    if (execute.code() == HttpURLConnection.HTTP_CREATED) {
      return CODE_OK;
    } else {
      String msg = execute.errorBody().string();
      //unfortunaley muspy api doesn't return error codes :(
      if (msg != null && msg.contains(ERROR_EMAIL)) {
        return CODE_EMAIL_DUPLICATE;
      } else {
        return CODE_ERROR;
      }
    }
  }

  /**
   * Updates the user settings.
   *
   * @return {@link UserServiceImpl#CODE_OK} {@link UserServiceImpl#CODE_EMAIL_DUPLICATE} {@link
   * UserServiceImpl#CODE_ERROR}
   */
  @Override
  public Integer updateUser(@NonNull User user) throws IOException {
    Integer result = CODE_OK;
    Credential credential = getCredentials();
    String newEmail = null;
    //important: only sends the email if changed
    if (!credential.getEmail().equals(user.getEmail())) {
      newEmail = user.getEmail();
    }
    Call<Void> userCall = userResource.updateUser(credential.getBasicToken(),
        credential.getUserId(), newEmail, user.isNotifications(), user.isFilterSingle(),
        user.isFilterOther(), user.isFilterLive(), user.isFilterEP(), user.isFilterCompilation(),
        user.isFilterAlbum(), user.isFilterRemix());

    Response<Void> execute = userCall.execute();
    if (execute.code() != HttpURLConnection.HTTP_OK) {
      String body = execute.errorBody().string();
      if (body != null && body.contains(ERROR_EMAIL_DB)) {
        result = CODE_EMAIL_DUPLICATE;
      } else {
        result = CODE_ERROR;
      }
    } else if (newEmail != null) {
      updateEmail(newEmail);
    }

    return result;
  }

  private void updateEmail(String newEmail) {
    securePreferences.edit().putString(SP_EMAIL, newEmail).apply();
    credentials = null;
  }

  /**
   * Checks if a lastfm user exists.
   */
  @Override
  public boolean checkLastfmUser(@NonNull String user) throws IOException {
    Response<Void> execute = userResourceLfm.checkUser(user).execute();
    return execute.code() != HttpURLConnection.HTTP_NOT_FOUND;
  }

  /**
   * Resets the password. Muspy api doesn't offer this feature so it's implemented using scraping.
   */
  @Override
  public boolean reset(@NonNull String email, Context context) throws IOException {
    boolean res = false;
    String csrf = getCsrf(context);
    if (!TextUtils.isEmpty(csrf)) {
      ClearableCookieJar cookieJar =
          new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));

      OkHttpClient okHttpClient = new OkHttpClient.Builder().cookieJar(cookieJar).build();
      RequestBody formBody = new FormBody.Builder()
          .add("email", email)
          .add("csrfmiddlewaretoken", csrf)
          .build();
      Request request = new Request.Builder()
          .url(URL_RESET)
          .addHeader("Referer", URL_RESET)
          .post(formBody)
          .build();
      okhttp3.Response response = okHttpClient.newCall(request).execute();
      //checks the status and the message of the html
      if (response.isSuccessful() && response.body().string().contains(RESET_RESPONSE_MSG)) {
        res = true;
      }
      cookieJar.clear();
    }
    return res;
  }

  /**
   * Gets the csrf code that is required to reset the password.
   */
  private String getCsrf(Context context) {
    try {
      Request request = new Request.Builder()
          .url(URL_RESET)
          .build();
      //this cookie must be used in the POST
      ClearableCookieJar cookieJar =
          new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
      OkHttpClient okHttpClient = new OkHttpClient.Builder()
          .cookieJar(cookieJar)
          .build();

      okhttp3.Response response = okHttpClient.newCall(request).execute();

      if (response.code() == HttpURLConnection.HTTP_OK) {
        Document doc = Jsoup.parse(response.body().string());
        Elements elementsByTag = doc.getElementsByTag("input");
        for (Element element : elementsByTag) {
          if ("csrfmiddlewaretoken".equals(element.attr("name"))) {
            return element.attr("value");
          }
        }
      }
    } catch (Exception ex) {
      Log.e(this.getClass().getCanonicalName(), "cannot obtain csrf", ex);
      Crashlytics.logException(ex);
    }
    return null;
  }

}
