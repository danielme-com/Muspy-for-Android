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
package com.danielme.muspyforandroid.ui.adapters.vh;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.danielme.muspyforandroid.MuspyApplication;
import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.service.ReleaseService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReleaseCoverViewHolder extends RecyclerView.ViewHolder {

  @BindView(R.id.cover)
  ImageView imageViewCover;

  private final ReleaseService releaseService;
  private final WeakReference<Activity> actContext;

  ReleaseCoverViewHolder(View itemView, ReleaseService releaseService, Activity actContext) {
    super(itemView);
    this.releaseService = releaseService;
    this.actContext = new WeakReference<>(actContext);
    ButterKnife.bind(this, itemView);
  }

  void setRelease(final String coverUrlMuspy, final String mbidGroup) {
    imageViewCover.setTag(R.id.cover, mbidGroup);

    imageViewCover.setImageResource(R.drawable.loadingcover);
    asyncThread(mbidGroup, coverUrlMuspy);
  }

  private void asyncThread(final String id, final String urlInMuspy) {
    Thread thread = new Thread() {
      public void run() {
        final String link;
        if (MuspyApplication.getCoverUrl(id) != null) { //cache
          link = MuspyApplication.getCoverUrl(id);
        } else {
          link = releaseService.getCover(id);
        }

        //cache the cover url in MB
        MuspyApplication.addCoverUrl(id, link);

        if (!TextUtils.isEmpty(link)) {
          new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
              if (isValidContextForGlide(actContext.get())
                      && imageViewCover.getTag(R.id.cover).equals(id)) {
                Glide.with(actContext.get())
                        .load(link)
                        .placeholder(R.drawable.loadingcover)
                        .error(R.drawable.loadingcover)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(imageViewCover);
              }
            }
          });
        }

      }
    };

    thread.start();
  }


  private boolean isValidContextForGlide(final Context context) {
    if (context == null) {
      return false;
    }
    if (context instanceof Activity) {
      final Activity activity = (Activity) context;
      if (activity.isDestroyed() || activity.isFinishing()) {
        return false;
      }
    }
    return true;
  }

}