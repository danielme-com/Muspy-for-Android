/*
 * Copyright (C) 2012-2018 Daniel Medina <http://danielme.com>
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
package com.danielme.glide;

import android.content.Context;

import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;

import java.io.File;

/**
 * Use external cache if available.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class CustomCacheDiskCacheFactory extends DiskLruCacheFactory {

  public CustomCacheDiskCacheFactory(Context context) {
    this(context, DiskCache.Factory.DEFAULT_DISK_CACHE_DIR, DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE);
  }

  public CustomCacheDiskCacheFactory(Context context, int diskCacheSize) {
    this(context, DiskCache.Factory.DEFAULT_DISK_CACHE_DIR, diskCacheSize);
  }

  public CustomCacheDiskCacheFactory(final Context context, final String diskCacheName, int diskCacheSize) {
    super(new CacheDirectoryGetter() {
      @Override
      public File getCacheDirectory() {
        File cacheDirectory = context.getExternalCacheDir();
        if (cacheDirectory == null) {
           cacheDirectory = context.getCacheDir();
          if (cacheDirectory == null) {
            return null;
          }
        }
        if (diskCacheName != null) {
          return new File(cacheDirectory, diskCacheName);
        }
        return cacheDirectory;
      }
    }, diskCacheSize);
  }

}
