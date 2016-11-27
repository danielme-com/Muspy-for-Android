package com.danielme.muspyforandroid.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.danielme.muspyforandroid.MuspyApplication;
import com.danielme.muspyforandroid.NavigationController;
import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.model.Artist;
import com.danielme.muspyforandroid.service.ArtistService;
import com.danielme.muspyforandroid.service.UserService;
import com.danielme.muspyforandroid.ui.adapters.ResultsItem;
import com.danielme.muspyforandroid.ui.ViewUtils;
import com.danielme.muspyforandroid.ui.activities.ArtistDetailActivity;
import com.danielme.muspyforandroid.ui.adapters.ArtistAdapter;
import com.danielme.muspyforandroid.ui.adapters.RecyclerViewOnItemClickListener;
import com.danielme.muspyforandroid.ui.recyclerview.Adapter;
import com.danielme.muspyforandroid.ui.recyclerview.DividerItemDecoration;
import com.danielme.muspyforandroid.ui.recyclerview.GenericRecyclerViewConfiguration;
import com.danielme.muspyforandroid.ui.recyclerview.GenericRecyclerViewFragment;
import com.danielme.muspyforandroid.ui.recyclerview.Results;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * The data displayed by this fragment is loaded the first time this fragment is visible to
 * the user ({@link MyArtistsFragment#setUserVisibleHint}).
 */
public class MyArtistsFragment extends GenericRecyclerViewFragment {

  @Inject
  ArtistService artistService;
  @Inject
  UserService userService;
  @Inject
  NavigationController navController;

  private boolean firstload = true;
  private MyArtistBroadcastReceiver artistBroadcastReceiver;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((MuspyApplication) getContext().getApplicationContext()).getApplicationDaggerComponent()
        .inject(this);

    artistBroadcastReceiver = new MyArtistBroadcastReceiver();
    IntentFilter filter = new IntentFilter();
    filter.addAction(ArtistDetailActivity.BROADCAST_ACTION_ARTIST);
    //broadcast is always listening if the Activity exists
    LocalBroadcastManager.getInstance(getActivity()).registerReceiver(artistBroadcastReceiver,
        filter);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(artistBroadcastReceiver);
  }

  @Override
  protected GenericRecyclerViewConfiguration buildConfiguration() {
    GenericRecyclerViewConfiguration.Builder builder = new GenericRecyclerViewConfiguration
        .Builder();
    builder.setAutoscrollAfterSwipe(true)
        .setAutoscrollToFooter(true)
        .setSupportSwipe(true)
        .setLoadOnCreate(false)
        .setHasOptionsMenu(true)
        .setSwipeColorScheme(ViewUtils.getSwipeColorScheme())
        .setDividerId(R.drawable.divider)
        .setDividerExclusions(new DividerItemDecoration.DividerExclusions() {
          @Override
          public boolean applyDivider(View view) {
            return view.getId() != R.id.layoutresults;
          }
        })
    .setMsgNoData(R.string.noartists);
    return builder.build();
  }

  /**
   * Gets the artists the first time the user views the fragment.
   */
  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);

    if (isVisibleToUser && firstload) {
      firstload = false;
      refresh(null);
    }
  }

  @Override
  public void addTop(Results results) {
    getDataFromAdapter().clear();
    getDataFromAdapter().addAll(results.getData());
    getAdapter().notifyDataSetChanged();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.fragment_myartists_menu, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_search:
        ArrayList<Parcelable> artistsFromAdapter = (ArrayList<Parcelable>) getDataFromAdapter();
        navController.gotoSearchArtist(getActivity(), artistsFromAdapter);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  protected Adapter createAdapter() {
    return new ArtistAdapter(new ArrayList(), new
        RecyclerViewOnItemClickListener() {

          @Override
          public void onClick(View v, int position) {
            Artist artist = ((Artist) getDataFromAdapter().get(position));
            navController.gotoArtistDetail(getActivity(), artist);
          }
        });
  }

  @Override
  protected Results doInBackground(int offset, int pageSize, AsyncTask asyncTask) throws Exception {
    List artists = artistService.getUserArtist();
    return new ResultsMyArtists(artists);
  }

  @Override
  protected void onPostExecuteAddtionalActions(Boolean success, Results results) {
    if (success && !results.getData().isEmpty()) {
      String text = getResources().getQuantityString(R.plurals.following,
          getDataFromAdapter().size(), getDataFromAdapter().size());
      ResultsItem resultsItem = new ResultsItem(text);
      getDataFromAdapter().add(0, resultsItem);
    }
  }

  public static class ResultsMyArtists implements Results {

    private final List<Artist> artists;

    public ResultsMyArtists(List<Artist> artists) {
      this.artists = artists;
    }

    @Override
    public List getData() {
      return artists;
    }
  }

  /**
   * This broadcast is received when the user follows or unfollows an artist.
   */
  class MyArtistBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

      Artist artist = intent.getParcelableExtra(ArtistDetailActivity.ARTIST_INTENT);
      //This list contains a header with the results if size > 1
      List<Object> artists = getDataFromAdapter();
      if (artists.contains(artist)) {
        //delete
        if (artists.remove(artist)) {
          if (artists.size() < 2) {
            //no data, only the header
            artists.clear();
            displayMsg(getConfiguration().getMsgNoData());
          } else {
            //just updates the header
            updateHeader(artists);
          }
          getAdapter().notifyDataSetChanged();
        }
      } else {
        addArtist(artists, artist);
      }
    }

    private void updateHeader(List artists) {
      String text = getResources().getQuantityString(R.plurals.following, artists.size() - 1,
          artists.size() - 1);
      ((ResultsItem) artists.get(0)).setText(text);
    }

    private void addArtist(List artists, Artist artist) {
      //new artist, searching for the insert position
      int newPos = 0;
      //remember: the list is already sorted.
      for (Object currentArtist : artists) {
        if (currentArtist instanceof Artist && ((Artist) currentArtist).getFullName()
            .compareTo(artist.getFullName()) > 0) {
          break;
        }
        newPos++;
      }

      if (newPos == 0) {
        //adds the header & the artist
        String text = getResources().getQuantityString(R.plurals.following, 1, 1);
        ResultsItem resultsItem = new ResultsItem(text);
        artists.add(0, resultsItem);
        artists.add(1, artist);
        //displays the recyclerview
        hideMessage();
      } else {
        //adds the header
        artists.add(newPos, artist);
        //and updates the header
       updateHeader(artists);
      }
      getAdapter().notifyDataSetChanged();
    }

  }

}
