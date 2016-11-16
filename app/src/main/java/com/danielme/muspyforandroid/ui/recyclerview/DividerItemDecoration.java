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
package com.danielme.muspyforandroid.ui.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * http://stackoverflow.com/a/27037230
 * @author danielme.com
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

  private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

  private final Drawable mDivider;

  /**
   * Applys default divider
   */
  public DividerItemDecoration(Context context) {
    final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
    mDivider = styledAttributes.getDrawable(0);
    styledAttributes.recycle();
  }

  /**
   * Apply default  divider.
   */
  public DividerItemDecoration(Context context, int resId) {
    mDivider = ContextCompat.getDrawable(context, resId);
  }

  @Override
  public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

    if (parent.getLayoutManager() instanceof LinearLayoutManager) {
      if (((LinearLayoutManager) parent.getLayoutManager()).getOrientation()
          == LinearLayoutManager.VERTICAL) {
        drawVertical(c, parent);
      } else {
        drawHorizontal(c, parent);
      }
    }
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                             RecyclerView.State state) {
    outRect.bottom = mDivider.getIntrinsicHeight();
  }

  private void drawVertical(Canvas c, RecyclerView parent) {
    int left = parent.getPaddingLeft();
    int right = parent.getWidth() - parent.getPaddingRight();

    int childCount = parent.getChildCount();
    //for (int i = 0; i < childCount - 1; i++) {
      for (int i = 0; i < childCount; i++) { //draws divider after the last item
      View child = parent.getChildAt(i);
      RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
          .getLayoutParams();
      int top = child.getBottom() + params.bottomMargin;
      int bottom = top + mDivider.getIntrinsicHeight();
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(c);
    }
  }

  private void drawHorizontal(Canvas c, RecyclerView parent) {
    int top = parent.getPaddingTop();
    int bottom = parent.getHeight() - parent.getPaddingBottom();

    int childCount = parent.getChildCount();
    for (int i = 0; i < childCount - 1; i++) {
      View child = parent.getChildAt(i);
      RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
          .getLayoutParams();
      int left = child.getRight() + params.rightMargin;
      int right = left + mDivider.getIntrinsicHeight();
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(c);
    }
  }

}