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

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.danielme.muspyforandroid.MuspyApplication;
import com.danielme.muspyforandroid.NavigationController;
import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.exceptions.ForbiddenUnauthorizedException;
import com.danielme.muspyforandroid.service.UserService;
import com.danielme.muspyforandroid.ui.ViewUtils;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.ListIterator;

import javax.inject.Inject;
import javax.net.ssl.SSLHandshakeException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Abstraction for all the fragments tha shows a recyclerwiew.
 */
public abstract class GenericRecyclerViewFragment extends Fragment {

  public enum LoadType {
    SWIPE,
    ENDLESS,
    REFRESH_NOSWIPE, //circular progress bar in the middle
    REFRESH_SWIPE //swipe but refresh the data
  }

  @Inject
  UserService userService;
  @Inject
  NavigationController navController;

  @BindView(R.id.recyclerView_generic)
  protected RecyclerView recyclerViewGeneric;
  @BindView(R.id.swipeLayout_generic)
  protected SwipeRefreshLayout swipeRefreshGeneric;
  @BindView(R.id.button_refresh)
  protected Button buttonRefresh;
  @BindView(R.id.textView_message)
  protected TextView textViewMessage;
  @BindView(R.id.progress_circular_center)
  protected ProgressBar progressBar;

  private boolean hasMore = true;
  private GenericRecyclerViewConfiguration configuration;
  private LoadDataAsyncTask loadDataAsyncTask;


  //METHODS TO IMPLEMENT

  protected abstract GenericRecyclerViewConfiguration buildConfiguration();

  protected abstract Adapter createAdapter();

  protected abstract Results doInBackground(int offset, int pageSize, AsyncTask asyncTask)
          throws Exception;


  protected void onPostExecuteAddtionalActions(Boolean success, Results results) {
    //default: nothing
  }

  /**
   * Cancels the asynctask.
   */
  protected void cancel(boolean mayInterruptRunning) {
    if (loadDataAsyncTask != null) {
      loadDataAsyncTask.cancel(mayInterruptRunning);
    }
  }

  /**
   * Adds all the results at the bottom of the recyclerView.
   */
  protected void addBotton(Results results) {
    getDataFromAdapter().addAll(results.getData());
    int start = getDataFromAdapter().size();
    recyclerViewGeneric.getAdapter().notifyItemRangeInserted(start,
            getDataFromAdapter().size() - 1);
  }

  /**
   * Adds the new items at the top of the RecyclerView. If there are missing items between the
   * current items in the recyclerView and the new items, the recyclerView will only display the new
   * items.
   */
  protected void addTop(Results results) {
    if (getDataFromAdapter().isEmpty()) {
      getDataFromAdapter().addAll(results.getData());
      getAdapter().notifyItemRangeInserted(0, results.getData().size());
    } else {
      Object firstOldItem = getDataFromAdapter().get(getDataFromAdapter().size() - 1);
      int index = results.getData().indexOf(firstOldItem);
      if (index == -1) {
        //there are missing items in the update, resets the list
        getDataFromAdapter().clear();
        getDataFromAdapter().addAll(results.getData());
        getAdapter().notifyDataSetChanged();
      } else if (index > 0) {
        ListIterator listIterator = results.getData().listIterator();
        int i = 0;
        //adds the new items at the begginig of the list
        while (listIterator.hasNext() && i < index) {
          getDataFromAdapter().add(0, listIterator.next());
          i++;
        }
        getAdapter().notifyItemRangeInserted(0, index - 1);
      }
      //index == 0 no new data, do nothing
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((MuspyApplication) getContext().getApplicationContext()).getApplicationDaggerComponent()
            .inject(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    configuration = buildConfiguration();
    View rootView = inflater.inflate(R.layout.fragment_generic_recyclerview, container, false);
    ButterKnife.bind(this, rootView);

    initSwipeLayout();
    initRecyclerView();

    if (configuration.isLoadOnCreate()) {
      refresh(null);
    }
    setHasOptionsMenu(configuration.isHasOptionsMenu());
    return rootView;
  }


  @Override
  public void onDestroyView() {
    super.onDestroyView();
    //avoids memory leaks when the activity is destroyed
    if (loadDataAsyncTask != null) {
      loadDataAsyncTask.cancel(false);
    }
    progressBar = null;
    loadDataAsyncTask = null;
    recyclerViewGeneric = null;
    swipeRefreshGeneric = null;
    buttonRefresh = null;
    textViewMessage = null;
  }

  protected Adapter getAdapter() {
    return (Adapter) recyclerViewGeneric.getAdapter();
  }

  protected List getDataFromAdapter() {
    return getAdapter().getData();
  }


  /**
   * Refresh the recyclerView. Displays the loading indicator (swipe indicator or progress circular
   * bar in the middle of the screen).
   */
  @OnClick(R.id.button_refresh)
  public void refresh(View view) {
    hideMessage();
    if (!getDataFromAdapter().isEmpty()) { //clears the prevoius data
      getDataFromAdapter().clear();
      getAdapter().notifyDataSetChanged();
    }
    if (configuration.isSupportSwipe()) {
      //triggers the swipe progranmatically the first time the page/frament is created
      swipeRefreshGeneric.post(new Runnable() {
        @Override
        public void run() {
          if (swipeRefreshGeneric != null) {
            swipeRefreshGeneric.setRefreshing(true);
            //setRefreshing doesn't call OnRefreshListener
            loadDataAsyncTask = new LoadDataAsyncTask(LoadType.REFRESH_SWIPE);
            loadDataAsyncTask.execute();
          }
        }
      });
    } else {
      //progress indicator in the middle of the screen
      progressBar.setVisibility(View.VISIBLE);
      swipeRefreshGeneric.setVisibility(View.GONE);

      loadDataAsyncTask = new LoadDataAsyncTask(LoadType.REFRESH_NOSWIPE);
      loadDataAsyncTask.execute();
    }
  }

  public UserService getUserService() {
    return userService;
  }

  protected void load(LoadType loadType) {
    loadDataAsyncTask = new LoadDataAsyncTask(loadType);
    loadDataAsyncTask.execute();
  }

  private void initSwipeLayout() {
    if (configuration.isSupportSwipe()) {
      if (configuration.getSwipeColorScheme() != null) {
        swipeRefreshGeneric.setColorSchemeResources(configuration.getSwipeColorScheme());
      }
      swipeRefreshGeneric.setOnRefreshListener(
              new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                  loadDataAsyncTask = new LoadDataAsyncTask(LoadType.REFRESH_SWIPE);
                  loadDataAsyncTask.execute();
                }
              });
    } else {
      swipeRefreshGeneric.setEnabled(false);
    }
  }

  /**
   * Initializes the RecyclerView. If endless scrolling is enabled creates an OnScrollListener.
   */
  private void initRecyclerView() {
    final Adapter adapter = createAdapter();
    if (adapter == null) {
      throw new IllegalArgumentException("createAdapter returns null!!!");
    }

    if (configuration.getDividerId() != null) {
      recyclerViewGeneric.addItemDecoration(new DividerItemDecoration(getContext(),
              configuration.getDividerId(), configuration.getDividerExclusions()));
    }

    recyclerViewGeneric.setAdapter(adapter);
    recyclerViewGeneric.setLayoutManager(new LinearLayoutManager(getActivity()));

    if (configuration.isSupportEndless()) {
      addOnscrollerListener();
    }

    if (configuration.getMsgInitialScreen() != -1) {
      textViewMessage.setText(getString(configuration.getMsgInitialScreen()));
      textViewMessage.setVisibility(View.VISIBLE);
    }
    if (configuration.getImageViewinitialScreen() != -1) {
      //add drawable on top of the text
      textViewMessage.setCompoundDrawablesWithIntrinsicBounds(null,
              ContextCompat.getDrawable(getContext(), configuration.getImageViewinitialScreen()),
              null, null);
    }
  }

  private void addOnscrollerListener() {
    recyclerViewGeneric.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (hasMore && !hasFooter() && !isAnyDialoFragmentAdded()) {
          LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView
                  .getLayoutManager();
          if (layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager
                  .getItemCount() - 1) {
            //displays the footer
            getDataFromAdapter().add(new Footer());
            Handler handler = new Handler();

            final Runnable r = new Runnable() {
              public void run() {
                recyclerViewGeneric.getAdapter().notifyDataSetChanged();
              }
            };
            handler.post(r);

            if (configuration.isAutoscrollToFooter()) {
              recyclerView.scrollToPosition(getDataFromAdapter().size() - 1);
            }
            loadDataAsyncTask = new LoadDataAsyncTask(LoadType.ENDLESS);
            loadDataAsyncTask.execute();
          }
        }
      }

    });
  }


  private boolean hasFooter() {
    List list = getDataFromAdapter();
    return !list.isEmpty() && list.get(list.size() - 1) instanceof Footer;
  }


  /**
   * Displays a message in the middle of the screen and hides  the RecyclerView.
   */
  protected void displayMsg(int id) {
    textViewMessage.setText(getString(id));
    textViewMessage.setVisibility(View.VISIBLE);
    if (configuration.isEnableRefreshNoData() || configuration.getMsgNoData() != id) {
      //retry button is always enabled when no data
      buttonRefresh.setVisibility(View.VISIBLE);
    } else {
      buttonRefresh.setVisibility(View.GONE);
    }
    swipeRefreshGeneric.setVisibility(View.GONE);
  }

  /**
   * Hides the message in the middle of the screen and displays the RecyclerView
   */
  protected void hideMessage() {
    textViewMessage.setVisibility(View.GONE);
    buttonRefresh.setVisibility(View.GONE);
    textViewMessage.setCompoundDrawables(null, null, null, null);
    swipeRefreshGeneric.setVisibility(View.VISIBLE);
  }

  /**
   * Removes the footer and notifies the adapter. Hides the loading indicator (swipe or
   * progressbar).
   */
  private void removeLoadingIndicators(LoadType loadType) {
    if (LoadType.REFRESH_NOSWIPE == loadType) {
      progressBar.setVisibility(View.GONE);
    } else if (LoadType.ENDLESS == loadType && hasFooter()) {
      getDataFromAdapter().remove(getDataFromAdapter().size() - 1);
      recyclerViewGeneric.getAdapter().notifyItemRemoved(getDataFromAdapter().size());
    } else {
      swipeRefreshGeneric.setRefreshing(false);
    }
  }

  protected void showAlert(Fragment fragment, String
          title, String msg) {
    FragmentManager fm = fragment.getFragmentManager();
    DialogFragment errorDialogFragment = DialogFragment.newInstance(title, msg, getString(android
            .R.string.ok), null, null);

    //ensures that isAnyDialoFragmentAdded returns true after this method
    //avoids state loss
    fm.beginTransaction().add(errorDialogFragment, DialogFragment.TAG).commitNowAllowingStateLoss();
  }

  protected boolean isAnyDialoFragmentAdded() {
    return isDialogFragmentAdded(DialogFragment.TAG);
  }

  private boolean isDialogFragmentAdded(String tag) {
    Fragment dialog = getActivity().getSupportFragmentManager().findFragmentByTag(tag);
    return dialog != null && dialog.isAdded();
  }

  public boolean isHasMore() {
    return hasMore;
  }

  protected void setHasMore(boolean hasMore) {
    this.hasMore = hasMore;
  }

  protected GenericRecyclerViewConfiguration getConfiguration() {
    return configuration;
  }

  /**
   * remember:the tasks for the same AsyncTask are executed one by one when using the default thread
   * pool executor (serial executor).
   */
  class LoadDataAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private final LoadType loadType;
    private Results results;

    LoadDataAsyncTask(LoadType loadType) {
      this.loadType = loadType;
    }

    /**
     * Checks if network connectivity is available, If it isn't  displays an alert message. Current
     * data is preserved.
     */
    @Override
    protected void onPreExecute() {
      if (!ViewUtils.isNetworkConnected(getContext())) {
        if (getAdapter().countRealData() == 0) {
          buttonRefresh.setText(getString(configuration.getMsgRetryButton()));
          displayMsg(configuration.getMsgNoConnection());
        } else if (!isAnyDialoFragmentAdded()) {
          //no connection but the fragment is already displaying some data, we keep these data on
          // screen
          showAlert(GenericRecyclerViewFragment.this,
                  getString(android.R.string.dialog_alert_title),
                  getString(configuration.getMsgNoConnection()));
        }
        cancel(true);
      }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
      if (!isCancelled()) {
        try {
          int offset = 0;
          if (loadType == LoadType.ENDLESS) {
            offset = getAdapter().countRealData();
          }
          results = GenericRecyclerViewFragment.this.doInBackground(offset, configuration
                  .getPageSize(), this);
          return Boolean.TRUE;
        } catch (ForbiddenUnauthorizedException ex) {
          Log.e(this.getClass().getCanonicalName(), ex.getMessage(), ex);
          userService.deleteCredentials();
          navController.gotoWelcome(getActivity());
          //dont cancel here!! cancel is performed in onDestroy
        } catch (UnknownHostException | SSLHandshakeException | SocketTimeoutException ex) {
          //this is not really an error, just a networking\server issue
          return Boolean.FALSE;
        } catch (Exception ex) {
          Log.e(this.getClass().getCanonicalName(), ex.getMessage(), ex);
          Crashlytics.logException(ex);
          return Boolean.FALSE;
        }
      }
      return Boolean.FALSE;
    }

    @Override
    protected void onPostExecute(Boolean success) {
      if (isAdded()) {
        if (success) {
          onPostExecuteSuccess();
        } else {
          onPostExecuteError();
        }
        onPostExecuteAddtionalActions(success, results);
      }
    }

    private void onPostExecuteError() {
      if (getDataFromAdapter().isEmpty()) { //NO DATA
        displayMsg(configuration.getMsgUnknownError());
        buttonRefresh.setText(getString(configuration.getMsgRetryButton()));
      } else {
        if (!isAnyDialoFragmentAdded()) {
          showAlert(GenericRecyclerViewFragment.this,
                  getString(android.R.string.dialog_alert_title),
                  getString(configuration.getMsgUnknownError()));
        }
      }
      removeLoadingIndicators(loadType);
    }

    private void onPostExecuteSuccess() {

      removeLoadingIndicators(loadType);

      if (results == null) {
        throw new IllegalArgumentException("results cannot be null!! Please check doInBackground");
      }
      if (results.getData().isEmpty()) {
        if (loadType != LoadType.ENDLESS) {
          getDataFromAdapter().clear();
          getAdapter().notifyDataSetChanged();
        }
        if (getDataFromAdapter() == null || getDataFromAdapter().isEmpty()) {
          displayMsg(configuration.getMsgNoData());
          buttonRefresh.setText(getString(configuration.getMsgRefreshButton()));
        }
        hasMore = false;
      } else {
        hasMore = results.getData().size() == configuration.getPageSize();
        hideMessage();
        if (loadType == LoadType.SWIPE) {
          addTop(results);
          if (configuration.isAutoscrollAfterSwipe()) {
            recyclerViewGeneric.scrollToPosition(0);
          }
        } else if (loadType == LoadType.ENDLESS) {
          addBotton(results);
        } else { //refresh
          getDataFromAdapter().clear();
          getDataFromAdapter().addAll(results.getData());
          getAdapter().notifyDataSetChanged();
          //resets previuos scrolled postion
          recyclerViewGeneric.scrollToPosition(0);
        }
        setupOverscrollMode();
      }
    }

    /**
     * Ensures the ovescroll effect is only applied when all the recyclerview data don't fit in the
     * screen.
     */
    private void setupOverscrollMode() {
      recyclerViewGeneric.post(new Runnable() {
        public void run() {
          LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerViewGeneric
                  .getLayoutManager();
          //the first and last row are displayed at the same time
          if (!getDataFromAdapter().isEmpty()
                  && layoutManager.findFirstCompletelyVisibleItemPosition() == 0
                  && layoutManager.findLastCompletelyVisibleItemPosition()
                  == getDataFromAdapter().size() - 1) {
            recyclerViewGeneric.setOverScrollMode(View.OVER_SCROLL_NEVER);
          } else {
            recyclerViewGeneric.setOverScrollMode(View.OVER_SCROLL_ALWAYS);

          }
        }
      });
    }

    @Override
    protected void onCancelled() {
      super.onCancelled();
      if (isAdded()) {
        removeLoadingIndicators(loadType);
      }
    }

  }

}