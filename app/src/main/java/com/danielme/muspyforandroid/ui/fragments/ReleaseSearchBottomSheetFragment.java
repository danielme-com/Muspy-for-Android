package com.danielme.muspyforandroid.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.ui.ExternalSearch;

import java.io.UnsupportedEncodingException;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This fragment handles the bottom sheet for picking a search engine in the release UI.
 */
public class ReleaseSearchBottomSheetFragment extends BottomSheetDialogFragment {

  private static final String ARG_MBID ="mbid";
  private static final String ARG_QUERY ="query";

  private final BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new
          BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
              //setStateText(newState);
              if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
              }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
              //nothing here
            }
          };

  public static ReleaseSearchBottomSheetFragment newInstance(String query, String mbid) {
    ReleaseSearchBottomSheetFragment fragment = new ReleaseSearchBottomSheetFragment();

    Bundle args = new Bundle();
    args.putString(ARG_QUERY, query);
    args.putString(ARG_MBID, mbid);
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void setupDialog(Dialog dialog, int style) {
    super.setupDialog(dialog, style);
    View contentView = View.inflate(getContext(), R.layout.fragment_search_bottomsheet, null);
    ButterKnife.bind(this, contentView);
    dialog.setContentView(contentView);
    BottomSheetBehavior<View> mBottomSheetBehavior = BottomSheetBehavior.from(((View) contentView
            .getParent()));
    if (mBottomSheetBehavior != null) {
      mBottomSheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
      mBottomSheetBehavior.setPeekHeight(1200); //displays all the elements in the screen
      contentView.requestLayout();
    }
  }


  @OnClick({R.id.youtube, R.id.spotify, R.id.lastfm, R.id.google, R.id.musicbrainz, R.id.amazon,
          R.id.play, R.id.deezer})
  public void onClick(View view) {
    String query = getArguments().getString(ARG_QUERY);
    ExternalSearch externalSearch = new ExternalSearch(query);
    try {
      switch (view.getId()) {
        case R.id.youtube:
          externalSearch.openYoutube(getActivity());
          break;
        case R.id.spotify:
          externalSearch.openSpotify((AppCompatActivity) getActivity());
          break;
        case R.id.lastfm:
          externalSearch.openLastFmRelease(getActivity());
          break;
        case R.id.google:
          externalSearch.openGoogle(getActivity());
          break;
        case R.id.musicbrainz:
          externalSearch.openMusicBrainzRelease(getActivity(), getArguments().getString(ARG_MBID));
          break;
        case R.id.amazon:
          externalSearch.openAmazon(getActivity());
          break;
        case R.id.play:
          externalSearch.openGooglePlay(getActivity());
          break;
        case R.id.deezer:
          externalSearch.openDeezer((AppCompatActivity) getActivity());
          break;
        default:
          throw new IllegalArgumentException(view.getId() + "not implemented");
      }
    } catch (UnsupportedEncodingException ex) {
      Log.e(this.getTag(), ex.getMessage(), ex);
    }
    dismiss();
  }


}