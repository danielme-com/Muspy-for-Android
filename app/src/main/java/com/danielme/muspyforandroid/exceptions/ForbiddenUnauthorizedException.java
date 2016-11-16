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
package com.danielme.muspyforandroid.exceptions;

import android.util.Log;

import java.io.IOException;


/**
 * This Exception extends IOException instead of Exception to be used in OkHttp interceptors. When
 * this excepciton is thrown the user must be logout,
 */
public class ForbiddenUnauthorizedException extends IOException {

  public ForbiddenUnauthorizedException(String msg) {
    super();
    Log.e("ForbiddenUnauthorized", msg);
  }

}