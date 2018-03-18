package com.danielme.muspyforandroid.ui.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.danielme.muspyforandroid.MuspyApplication;
import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.model.LabelWrapper;
import com.danielme.muspyforandroid.model.Media;
import com.danielme.muspyforandroid.model.Release;
import com.danielme.muspyforandroid.model.ReleaseMB;
import com.danielme.muspyforandroid.model.Track;
import com.danielme.muspyforandroid.service.ReleaseService;
import com.danielme.muspyforandroid.ui.ViewUtils;
import com.danielme.muspyforandroid.ui.activities.ReleaseActivity;
import com.danielme.muspyforandroid.ui.adapters.ReleaseAdapter;
import com.danielme.muspyforandroid.ui.recyclerview.Adapter;
import com.danielme.muspyforandroid.ui.recyclerview.GenericRecyclerViewConfiguration;
import com.danielme.muspyforandroid.ui.recyclerview.GenericRecyclerViewFragment;
import com.danielme.muspyforandroid.ui.recyclerview.Results;
import com.securepreferences.SecurePreferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

/**
 * A fragment that shows the details of a release including the tracklist.
 */
public class ReleaseFragment extends GenericRecyclerViewFragment {

  private static final String RELEASE_URL = "https://musicbrainz.org/release-group/";
  private static final String PREF_AGENDA = "PREF_AGENDA";

  @Inject
  ReleaseService releaseService;
  @Inject
  SecurePreferences securePreferences;

  private Release release;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    release = getArguments().getParcelable(ReleaseActivity.RELEASE_INTENT);

    ((MuspyApplication) getContext().getApplicationContext()).getApplicationDaggerComponent()
        .inject(this);
  }

  @Override
  protected GenericRecyclerViewConfiguration buildConfiguration() {
    GenericRecyclerViewConfiguration.Builder builder = new GenericRecyclerViewConfiguration
        .Builder();

    return builder.setEnableRefreshNoData(false)
        .setHasOptionsMenu(true)
        .setMsgNoData(R.string
            .data_not_available)
        .setDividerId(R.drawable.divider)
        .build();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.fragment_release_menu, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_share:
        openShare();
        return true;
      case R.id.action_agenda:
        openAgenda();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onPostExecuteAddtionalActions(Boolean success, Results results) {
    if (!securePreferences.getBoolean(PREF_AGENDA, false)) {
      securePreferences.edit().putBoolean(PREF_AGENDA, true).apply();
      new MaterialTapTargetPrompt.Builder(getActivity())
              .setTarget(R.id.action_agenda)
              .setIcon(R.drawable.ic_view_agenda)
              .setPrimaryText(getString(R.string.info_agenda))
              .show();
    }
  }

  @Override
  protected Adapter createAdapter() {
    return new ReleaseAdapter(new ArrayList(), releaseService, getActivity());
  }

  @Override
  protected Results doInBackground(int offset, int pageSize, AsyncTask asynctask) throws Exception {
    final List data = new ArrayList();
    ReleaseMB releaseMB = releaseService.getReleasesAndTracklist(release);
    if (releaseMB != null && !asynctask.isCancelled()) {
      data.add(releaseMB);
      createPlainList(data);
    }

    return new Results() {

      final List releases = data;

      @Override
      public List getData() {
        return releases;
      }
    };
  }

  /**
   * Builds the list that will be used in the recyclerview.
   */
  private void createPlainList(List data) {
    ReleaseMB selected = (ReleaseMB) data.get(0);

    Set<String> formats = mapMedia(selected, data);
    mapFormat(selected, formats);

    selected.setArtist(release.getArtist().getName());
    if (release.getType() != null) {
      selected.setType(release.getType().toString());
    }

    selected.setCover(release.getCoverUrl());

    selected.setGroupMbid(release.getMbid());

    mapType(selected);
    mapCountry(selected);
    mapLabel(selected);
  }

  /**
   * A release can have multiple media and formats (2 cd, cd + dvd, etc).
   */
  private Set<String> mapMedia(ReleaseMB selected, List data) {

    int totalLengthRelease = 0;

    Map<String, Integer> formatCounter = new LinkedHashMap<String, Integer>();

    for (Media media : selected.getMedia()) {
      if (selected.getMedia().size() > 1) {
        data.add(media);
        //CD 1
        //CD 2
        //DVD 1
        //CD 3
        if (media.getFormat() == null) {
          media.setFormat("");
        } else {
          Integer count = formatCounter.get(media.getFormat());
          if (count == null) {
            count = 1;
          } else {
            count = count + 1;
          }
          formatCounter.put(media.getFormat(), count);
          media.setNumber(count);
        }
      }

      //tracks for the media
      int totalLengthMedia = 0;
      for (Track track : media.getTracks()) {
        data.add(track);
        totalLengthMedia += track.getLength();
      }
      media.setTotalLength(totalLengthMedia);
      totalLengthRelease += totalLengthMedia;
    }
    selected.setTotalLength(totalLengthRelease);

    //we dont want to display the media number if there is only one media of a format
    for (Object item : data) {
      if (item instanceof Media) {
        Media media = (Media) item;
        Integer count = formatCounter.get(media.getFormat());
        if (count == null || count == 1) {
          media.setNumber(0);
        }
      }
    }

    return formatCounter.keySet();
  }

  private void mapCountry(ReleaseMB selected) {
    if (selected.getCountry() != null) {
      //i18n
      int countryId = getResources().getIdentifier(selected.getCountry(), "string",
          getActivity().getPackageName());
      try {
        selected.setCountryName(getString(countryId));
      } catch (Resources.NotFoundException ex) {
        Log.w(ReleaseFragment.class.getSimpleName(), "country code not found: " + selected
            .getCountry());
      }
    }
  }

  private void mapType(ReleaseMB selected) {
    if (selected.getType() != null) {
      //i18n
      int typeId = getResources().getIdentifier(selected.getType(), "string",
          getActivity().getPackageName());
      selected.setType(getString(typeId));
    }
  }

  private void mapFormat(ReleaseMB selected, Set<String> formats) {
    StringBuilder formatSb = new StringBuilder();
    for (String format : formats) {
      if (formatSb.length() > 0) {
        formatSb.append(" \u2022 ");
      }
      formatSb.append(format);
    }
    selected.setFormat(formatSb.toString());
  }

  private void mapLabel(ReleaseMB release) {
    StringBuilder labels = new StringBuilder();
    for (LabelWrapper label : release.getLabels()) {
      if (label.getLabel() != null) {
        if (labels.length() > 0) {
          labels.append(" \u2022 ");
        }
        labels.append(label.getLabel().getName());
      }
    }
    release.setLabel(labels.toString());
  }

  private void openShare() {
    String message = release.getArtist().getName() + " ( "
        + ViewUtils.localizedDate(release.getDate()).getString() + ") :  " + release.getName()
        + "   -   " + RELEASE_URL + release.getMbid();
    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
    intent.setType("text/plain");
    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.share_title);
    intent.putExtra(Intent.EXTRA_TEXT, message);
    startActivityForResult(Intent.createChooser(intent, getString(R.string.share_title)), 0);
  }


  private void openAgenda() {
    Intent intent = new Intent(Intent.ACTION_EDIT);
    Date date = ViewUtils.localizedDate(release.getDate()).getDate();
    if (date == null) {
      date = Calendar.getInstance().getTime();
    }
    intent.setType("vnd.android.cursor.item/event");
    intent.putExtra("beginTime", date.getTime());
    intent.putExtra("allDay", true);
    intent.putExtra("endTime", date.getTime());
    intent.putExtra("title", release.getArtist().getName() + " : " + release.getName());
    intent.putExtra("description", RELEASE_URL + release.getMbid());

    startActivity(Intent.createChooser(intent, getString(R.string.pick_agenda)));
  }

}