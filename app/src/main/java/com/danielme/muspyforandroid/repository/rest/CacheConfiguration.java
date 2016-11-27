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

/**
 * This class contains some options for the retrofit cache.
 */
public class CacheConfiguration {

  private String cachePath;
  private int seconds;

  public CacheConfiguration(String cachePath, int seconds) {
    this.cachePath = cachePath;
    this.seconds = seconds;
  }

  public String getCachePath() {
    return cachePath;
  }

  public int getSeconds() {
    return seconds;
  }

}