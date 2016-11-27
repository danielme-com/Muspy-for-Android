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
package com.danielme.muspyforandroid.ui.recyclerview;

import com.danielme.muspyforandroid.R;

/**
 * This class provides a builder to configurate a fragment that extends {@link
 * GenericRecyclerViewFragment}.
 */
public class GenericRecyclerViewConfiguration {

  private final boolean autoscrollToFooter;
  private final boolean autoscrollAfterSwipe;
  private final boolean supportSwipe;
  private final boolean supportEndless;
  private final boolean enableRefreshNoData;
  private final boolean loadOnCreate;
  private final boolean hasOptionsMenu;
  private final boolean cancelOnDestroy;
  private final int pageSize;
  private final int[] swipeColorScheme;
  private final Integer dividerId; //null: no divider
  private final DividerItemDecoration.DividerExclusions dividerExclusions;

  private final int msgNoConnection;
  private final int msgNoData;
  private final int msgRetryButton;
  private final int msgUnknownError;
  private final int msgRefreshButton;

  /**
   * Private, use builder instead.
   */
  private GenericRecyclerViewConfiguration(boolean autoscrollToFooter,
                                           boolean autoscrollAfterSwipe,
                                           boolean supportSwipe,
                                           boolean enableRefreshNoData,
                                           boolean loadOnCreate,
                                           boolean hasOptionsMenu,
                                           boolean supportEndless,
                                           boolean cancelOnDestroy,
                                           int pageSize,
                                           int[] swipeColorScheme,
                                           Integer dividerId,
                                           DividerItemDecoration.DividerExclusions
                                               dividerExclusions,
                                           int msgNoConnection,
                                           int msgNoData,
                                           int msgRetryButton,
                                           int msgUnknownError,
                                           int msgRefreshButton) {
    this.autoscrollToFooter = autoscrollToFooter;
    this.autoscrollAfterSwipe = autoscrollAfterSwipe;
    this.supportSwipe = supportSwipe;
    this.enableRefreshNoData = enableRefreshNoData;
    this.loadOnCreate = loadOnCreate;
    this.hasOptionsMenu = hasOptionsMenu;
    this.supportEndless = supportEndless;
    this.pageSize = pageSize;
    this.swipeColorScheme = swipeColorScheme;
    this.dividerId = dividerId;
    this.msgNoData = msgNoData;
    this.msgRetryButton = msgRetryButton;
    this.msgUnknownError = msgUnknownError;
    this.msgRefreshButton = msgRefreshButton;
    this.msgNoConnection = msgNoConnection;
    this.cancelOnDestroy = cancelOnDestroy;
    this.dividerExclusions = dividerExclusions;
  }

  public boolean isAutoscrollToFooter() {
    return autoscrollToFooter;
  }

  public boolean isAutoscrollAfterSwipe() {
    return autoscrollAfterSwipe;
  }

  public boolean isSupportSwipe() {
    return supportSwipe;
  }

  public boolean isEnableRefreshNoData() {
    return enableRefreshNoData;
  }

  public boolean isLoadOnCreate() {
    return loadOnCreate;
  }

  public boolean isHasOptionsMenu() {
    return hasOptionsMenu;
  }

  public int getPageSize() {
    return pageSize;
  }

  public final int[] getSwipeColorScheme() {
    return swipeColorScheme;
  }

  public final Integer getDividerId() {
    return dividerId;
  }

  public final DividerItemDecoration.DividerExclusions getDividerExclusions() {
    return dividerExclusions;
  }

  public int getMsgNoData() {
    return msgNoData;
  }

  public int getMsgRetryButton() {
    return msgRetryButton;
  }

  public int getMsgUnknownError() {
    return msgUnknownError;
  }

  public int getMsgRefreshButton() {
    return msgRefreshButton;
  }

  public boolean isSupportEndless() {
    return supportEndless;
  }

  public int getMsgNoConnection() {
    return msgNoConnection;
  }

  public boolean isCancelOnDestroy() {
    return cancelOnDestroy;
  }

  public static class Builder {

    private boolean autoscrollToFooter;
    private boolean autoscrollAfterSwipe;
    private boolean supportSwipe;
    private boolean supportEndless;
    private boolean enableRefreshNoData = true;
    private boolean loadOnCreate = true;
    private boolean hasOptionsMenu;
    private boolean cancelOnDestroy = true;
    private int pageSize = 20;
    private int[] swipeColorScheme;
    private Integer dividerId;
    private DividerItemDecoration.DividerExclusions dividerExclusions;

    private int msgNoConnection = R.string.noconnection;
    private int msgNoData = R.string.nodata;
    private int msgRetryButton = R.string.retry_button;
    private int msgUnknownError = R.string.unknown_error;
    private int msgRefreshButton = R.string.refresh_button;

    public Builder setAutoscrollToFooter(boolean autoscrollToFooter) {
      this.autoscrollToFooter = autoscrollToFooter;
      return this;
    }

    public Builder setAutoscrollAfterSwipe(boolean
                                               autoscrollAfterSwipe) {
      this.autoscrollAfterSwipe = autoscrollAfterSwipe;
      return this;
    }

    public Builder setSupportSwipe(boolean supportSwipe) {
      this.supportSwipe = supportSwipe;
      return this;
    }

    public Builder setSupportEndless(boolean supportEndless) {
      this.supportEndless = supportEndless;
      return this;
    }

    public Builder setEnableRefreshNoData(boolean
                                              enableRefreshNoData) {
      this.enableRefreshNoData = enableRefreshNoData;
      return this;
    }

    public Builder setLoadOnCreate(boolean loadOnCreate) {
      this.loadOnCreate = loadOnCreate;
      return this;
    }

    public Builder setHasOptionsMenu(boolean hasOptionsMenu) {
      this.hasOptionsMenu = hasOptionsMenu;
      return this;
    }

    public Builder setPageSize(int pageSize) {
      this.pageSize = pageSize;
      return this;
    }

    public Builder setSwipeColorScheme(int... swipeColorScheme) {
      this.swipeColorScheme = swipeColorScheme;
      return this;
    }

    public Builder setDividerId(Integer dividerId) {
      this.dividerId = dividerId;
      return this;
    }

    public Builder setDividerExclusions(DividerItemDecoration.DividerExclusions dividerExclusions) {
      this.dividerExclusions = dividerExclusions;
      return this;
    }

    public Builder setMsgNoConnection(int msgNoConnection) {
      this.msgNoConnection = msgNoConnection;
      return this;
    }

    public Builder setMsgNoData(int msgNoData) {
      this.msgNoData = msgNoData;
      return this;
    }

    public Builder setMsgRetryButton(int msgRetryButton) {
      this.msgRetryButton = msgRetryButton;
      return this;
    }

    public Builder setMsgUnknownError(int msgUnknownError) {
      this.msgUnknownError = msgUnknownError;
      return this;
    }

    public Builder setMsgRefreshButton(int msgRefreshButton) {
      this.msgRefreshButton = msgRefreshButton;
      return this;
    }

    public Builder setCancelOnDestroy(boolean cancelOnDestroy) {
      this.cancelOnDestroy = cancelOnDestroy;
      return this;
    }

    public GenericRecyclerViewConfiguration build() {
      return new GenericRecyclerViewConfiguration(autoscrollToFooter, autoscrollAfterSwipe,
          supportSwipe, enableRefreshNoData, loadOnCreate, hasOptionsMenu, supportEndless,
          cancelOnDestroy, pageSize, swipeColorScheme, dividerId, dividerExclusions,
          msgNoConnection, msgNoData, msgRetryButton, msgUnknownError, msgRefreshButton);
    }
  }

}