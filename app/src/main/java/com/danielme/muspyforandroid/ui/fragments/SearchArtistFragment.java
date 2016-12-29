package com.danielme.muspyforandroid.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.danielme.muspyforandroid.MuspyApplication;
import com.danielme.muspyforandroid.NavigationController;
import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.model.Artist;
import com.danielme.muspyforandroid.model.ArtistMb;
import com.danielme.muspyforandroid.service.ArtistService;
import com.danielme.muspyforandroid.ui.ViewUtils;
import com.danielme.muspyforandroid.ui.activities.ArtistDetailActivity;
import com.danielme.muspyforandroid.ui.activities.SearchArtistActivity;
import com.danielme.muspyforandroid.ui.adapters.ArtistAdapter;
import com.danielme.muspyforandroid.ui.adapters.RecyclerViewOnItemClickListener;
import com.danielme.muspyforandroid.ui.adapters.ResultsItem;
import com.danielme.muspyforandroid.ui.recyclerview.Adapter;
import com.danielme.muspyforandroid.ui.recyclerview.DividerItemDecoration;
import com.danielme.muspyforandroid.ui.recyclerview.GenericRecyclerViewConfiguration;
import com.danielme.muspyforandroid.ui.recyclerview.GenericRecyclerViewFragment;
import com.danielme.muspyforandroid.ui.recyclerview.Results;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * A fragment that shows the artists found in MusicBrainz.
 */
public class SearchArtistFragment extends GenericRecyclerViewFragment {

  @Inject
  ArtistService artistService;
  @Inject
  NavigationController navController;

  private ArrayList<Parcelable> followedArtists;
  private MyArtistBroadcastReceiver myArtistBroadcastReceiver;

  //set this value when the user taps search
  private String query;

  @Override
  protected GenericRecyclerViewConfiguration buildConfiguration() {
    GenericRecyclerViewConfiguration.Builder builder = new GenericRecyclerViewConfiguration
        .Builder();
    builder.setAutoscrollAfterSwipe(true)
        .setAutoscrollToFooter(true)
        .setLoadOnCreate(false)
        .setEnableRefreshNoData(false)
        .setSupportEndless(true)
        .setSwipeColorScheme(ViewUtils.getSwipeColorScheme())
        .setDividerId(R.drawable.divider)
        .setDividerExclusions(new DividerItemDecoration.DividerExclusions() {
          @Override
          public boolean applyDivider(View view) {
            return view.getId() != R.id.layoutresults;
          }
        })
        .setMsgUnknownError(R.string.service_not_available)
        .setMsgInitialScreen(R.string.search_info)
        .setImageViewinitialScreen(R.drawable.search);
    return builder.build();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((MuspyApplication) getContext().getApplicationContext()).getApplicationDaggerComponent()
        .inject(this);
    followedArtists = getArguments().getParcelableArrayList(SearchArtistActivity.INTENT_ARTISTS);
    if (followedArtists == null) {
      followedArtists = new ArrayList<>();
    }
    myArtistBroadcastReceiver = new MyArtistBroadcastReceiver();
    IntentFilter filter = new IntentFilter();
    filter.addAction(ArtistDetailActivity.BROADCAST_ACTION_ARTIST);
    //broadcast is always listening if the Activity exists
    LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myArtistBroadcastReceiver,
        filter);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(myArtistBroadcastReceiver);
  }

  @Override
  protected Adapter createAdapter() {
    return new ArtistAdapter(new ArrayList(), new
        RecyclerViewOnItemClickListener() {
          @Override
          public void onClick(View v, int position) {
            Artist artist = ((Artist) getDataFromAdapter().get(position));
            //default: display add button in detail
            artist.setFollowing(false);

            if (followedArtists != null && followedArtists.contains(artist)) {
              artist.setFollowing(true);
            }
            navController.gotoArtistDetail(getActivity(), artist);
          }
        });
  }

  @Override
  protected Results doInBackground(int offset, int i, AsyncTask asyncTask) throws Exception {
    ArtistMb artistMb = artistService.searchArtists(this.query, offset,
        getConfiguration().getPageSize());
    return new ResultsSearchArtist(artistMb, artistService.artistConversor(artistMb));
  }

  /**
   * Adds the header to the list
   */
  @Override
  protected void onPostExecuteAddtionalActions(Boolean success, Results results) {
    if (success && results != null) {
      ResultsSearchArtist resultsSearchArtist = (ResultsSearchArtist) results;
      if (resultsSearchArtist.getArtistMb() != null) {
        int count = resultsSearchArtist.getArtistMb().getCount();
        if (count > 0) {
          String text = getResources().getQuantityString(R.plurals.results,
              count, count);

          if (getDataFromAdapter().get(0) instanceof ResultsItem) {
            ((ResultsItem) getDataFromAdapter().get(0)).setText(text);
          } else {
            ResultsItem resultsItem = new ResultsItem(text);
            getDataFromAdapter().add(0, resultsItem);
          }
        }

      }
    }
  }

  public void refresh(String query) {
    //removes the cursor from the searchview in the toolbar
    View view = getView().findViewById(R.id.genericrecyclerview_parentlayout);
    view.setFocusable(true);
    view.setFocusableInTouchMode(true);
    view.requestFocus();
    this.query = query;
    super.refresh(null);
  }

  public void cancelSearch() {
    super.cancel(false);
  }

  public static class ResultsSearchArtist implements Results {

    private final ArtistMb artistMb;
    private final List<Artist> artists;

    public ResultsSearchArtist(ArtistMb artistMb, List<Artist> artists) {
      this.artistMb = artistMb;
      this.artists = artists;
    }

    @Override
    public List getData() {
      return artists;
    }

    public ArtistMb getArtistMb() {
      return artistMb;
    }
  }

  /**
   * This broadcast is received when the user follows or unfollows an artist.
   */
  class MyArtistBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      Artist artist = intent.getParcelableExtra(ArtistDetailActivity.ARTIST_INTENT);
      if (artist.isFollowing() && !followedArtists.contains(artist)) {
        followedArtists.add(artist);
      } else if (!artist.isFollowing() && followedArtists.contains(artist)) {
        followedArtists.remove(artist);
      }
    }
  }

}