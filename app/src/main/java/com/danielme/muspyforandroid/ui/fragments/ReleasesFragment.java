package com.danielme.muspyforandroid.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.danielme.muspyforandroid.MuspyApplication;
import com.danielme.muspyforandroid.NavigationController;
import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.model.Artist;
import com.danielme.muspyforandroid.model.Release;
import com.danielme.muspyforandroid.service.ReleaseService;
import com.danielme.muspyforandroid.ui.ViewUtils;
import com.danielme.muspyforandroid.ui.activities.ArtistDetailActivity;
import com.danielme.muspyforandroid.ui.adapters.RecyclerViewOnItemClickListener;
import com.danielme.muspyforandroid.ui.adapters.ReleasesAdapter;
import com.danielme.muspyforandroid.ui.recyclerview.Adapter;
import com.danielme.muspyforandroid.ui.recyclerview.GenericRecyclerViewConfiguration;
import com.danielme.muspyforandroid.ui.recyclerview.GenericRecyclerViewFragment;
import com.danielme.muspyforandroid.ui.recyclerview.Results;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.inject.Inject;

public class ReleasesFragment extends GenericRecyclerViewFragment {

  private ArtistBroadcastReceiver artistBroadcastReceiver;

  @Inject
  ReleaseService releaseService;
  @Inject
  NavigationController navController;

  @Override
  protected GenericRecyclerViewConfiguration buildConfiguration() {
    GenericRecyclerViewConfiguration.Builder builder = new GenericRecyclerViewConfiguration
        .Builder();
    builder.setAutoscrollAfterSwipe(true)
        .setAutoscrollToFooter(true)
        .setSupportSwipe(true)
        .setSupportEndless(true)
        .setSwipeColorScheme(ViewUtils.getSwipeColorScheme())
        .setDividerId(R.drawable.divider);
    return builder.build();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ((MuspyApplication) getContext().getApplicationContext()).getApplicationDaggerComponent()
        .inject(this);

    artistBroadcastReceiver = new ArtistBroadcastReceiver();
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
  protected Adapter createAdapter() {
    return new ReleasesAdapter(new ArrayList(), new
        RecyclerViewOnItemClickListener() {

          @Override
          public void onClick(View v, int position) {
            navController.gotoReleaseDetail(getActivity(),
                (Release) getDataFromAdapter().get(position));
          }
        }, releaseService, getActivity());
  }

  @Override
  protected Results doInBackground(int offset, int pageSize, AsyncTask asyncTask) throws Exception {
    ArrayList<Release> releases = releaseService.getReleasesByUser(offset, pageSize);
    return new ResultsReleases(releases);
  }

  /**
   * This broadcast is received when the user follows or unfollows an artist.
   */
  class ArtistBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      Artist artist = intent.getParcelableExtra(ArtistDetailActivity.ARTIST_INTENT);
      //removes all the releases of the unfollowed artist
      if (!artist.isFollowing()) {
        ListIterator iterator = getDataFromAdapter().listIterator();
        int oldSize = getDataFromAdapter().size();
        while (iterator.hasNext()) {
          Object obj = iterator.next();
          if (obj instanceof Release && ((Release) obj).getArtist().getMbid().equals(artist
              .getMbid())) {
            iterator.remove();
          }
        }
        //loads additional data to "fill" the items deleted
        if (oldSize > getDataFromAdapter().size()) {
          getAdapter().notifyDataSetChanged();
          load(LoadType.ENDLESS);
        }
      } else { //resets the list, we dont know the releases of the artists added
        getDataFromAdapter().clear();
        setHasMore(true);
        getAdapter().notifyDataSetChanged();
        load(LoadType.ENDLESS);
      }
    }

  }

  public static class ResultsReleases implements Results {

    private final List<Release> releases;

    public ResultsReleases(List<Release> releases) {
      this.releases = releases;
    }

    @Override
    public List getData() {
      return releases;
    }
  }

}
