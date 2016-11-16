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

package com.danielme.muspyforandroid.service;

import android.content.Context;
import android.support.annotation.NonNull;

import com.danielme.muspyforandroid.model.Credential;
import com.danielme.muspyforandroid.model.User;

import java.io.IOException;


public interface UserService {

  int CODE_OK = 0;
  int CODE_EMAIL_DUPLICATE = 1;
  int CODE_ERROR = 2;

  boolean userHasCredentials();

  void storeCredentials(User user, String pass);

  Credential getCredentials();

  void deleteCredentials();

  User getUser(@NonNull Credential credential) throws IOException;

  User getUser() throws IOException;

  Integer createUser(@NonNull String email, @NonNull String password) throws IOException;

  Integer updateUser(@NonNull User user) throws IOException;

  boolean checkLastfmUser(@NonNull String user) throws IOException;

  boolean reset(@NonNull String email, Context context) throws IOException;
}
