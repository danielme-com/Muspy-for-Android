package com.danielme.muspyforandroid.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.danielme.muspyforandroid.MuspyApplication;
import com.danielme.muspyforandroid.NavigationController;
import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.model.Artist;
import com.danielme.muspyforandroid.model.Release;
import com.danielme.muspyforandroid.service.ReleaseService;
import com.danielme.muspyforandroid.service.UserService;
import com.danielme.muspyforandroid.ui.ViewUtils;
import com.danielme.muspyforandroid.ui.activities.ArtistDetailActivity;
import com.danielme.muspyforandroid.ui.adapters.RecyclerViewOnItemClickListener;
import com.danielme.muspyforandroid.ui.adapters.ReleasesAdapter;
import com.danielme.muspyforandroid.ui.recyclerview.Adapter;
import com.danielme.muspyforandroid.ui.recyclerview.GenericRecyclerViewConfiguration;
import com.danielme.muspyforandroid.ui.recyclerview.GenericRecyclerViewFragment;
import com.danielme.muspyforandroid.ui.recyclerview.Results;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * A fragment that shows the releases of an artist.
 */
public class ArtistReleasesFragment extends GenericRecyclerViewFragment {

  @Inject
  UserService userService;
  @Inject
  ReleaseService releaseService;
  @Inject
  NavigationController navController;

  private Artist artist;

  @Override
  protected GenericRecyclerViewConfiguration buildConfiguration() {
    GenericRecyclerViewConfiguration.Builder builder = new GenericRecyclerViewConfiguration
        .Builder();
    builder.setAutoscrollAfterSwipe(true)
        .setAutoscrollToFooter(true)
        .setEnableRefreshNoData(false)
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

    if (getArguments() != null) {
      artist = getArguments().getParcelable(ArtistDetailActivity.ARTIST_INTENT);
    }
    if (artist == null) {
      throw new IllegalArgumentException("artist cannot be null");
    }
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
        });
  }

  @Override
  protected Results doInBackground(int offset, int pageSize, AsyncTask asyncTask) throws Exception {
    String mbid = artist.getMbid();
    ArrayList<Release> releases = releaseService.getReleasesByArtist(offset, pageSize, mbid);
    return new ReleasesFragment.ResultsReleases(releases);
  }

}