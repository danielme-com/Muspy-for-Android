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
import com.danielme.muspyforandroid.model.Artist;
import com.danielme.muspyforandroid.model.ArtistMb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public interface ArtistService {

  ArrayList<Artist> getUserArtist() throws IOException;

  ArtistMb searchArtists(String name, int offset, int max) throws IOException, HttpStatusException;

  boolean followArtist(String mbid) throws IOException;

  boolean unfollowArtist(String mbid) throws IOException;

  List<Artist> artistConversor(ArtistMb artistMb);

  boolean importLastfm(String user, String period, String top) throws
      IOException;
}
