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

import com.danielme.muspyforandroid.exceptions.HttpStatusException;
import com.danielme.muspyforandroid.model.Release;
import com.danielme.muspyforandroid.model.ReleaseMB;

import java.io.IOException;
import java.util.ArrayList;


public interface ReleaseService {

  ArrayList<Release> getReleasesByUser(int offset, int limit) throws IOException,
          HttpStatusException;

  ArrayList<Release> getReleasesByArtist(int offset, int limit, String mbid) throws IOException,
          HttpStatusException;

  ReleaseMB getReleasesAndTracklist(Release release) throws HttpStatusException,
          IOException;

  /**
   * Gets the cover link from coverartarchive.org.
   */
  String getCover(String mbidGroup);
}
