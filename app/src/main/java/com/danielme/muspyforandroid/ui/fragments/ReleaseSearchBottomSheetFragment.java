package com.danielme.muspyforandroid.ui.fragments;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.View;

import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.ui.ViewUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This fragment handles the bottom sheet for picking a search engine in the release UI.
 */
public class ReleaseSearchBottomSheetFragment extends BottomSheetDialogFragment {

  private static final Map<String, String> AMAZON_DOMAINS;
  static {
    AMAZON_DOMAINS = new HashMap<>(14);
    AMAZON_DOMAINS.put("US", "com");
    AMAZON_DOMAINS.put("ES", "es");
    AMAZON_DOMAINS.put("RC", "cn");
    AMAZON_DOMAINS.put("IN", "in");
    AMAZON_DOMAINS.put("JP", "co.jp");
    AMAZON_DOMAINS.put("FR", "fr");
    AMAZON_DOMAINS.put("DE", "de");
    AMAZON_DOMAINS.put("IT", "it");
    AMAZON_DOMAINS.put("NL", "nl");
    AMAZON_DOMAINS.put("GB", "co.uk");
    AMAZON_DOMAINS.put("CA", "ca");
    AMAZON_DOMAINS.put("MX", "com.mx");
    AMAZON_DOMAINS.put("AU", "com.au");
    AMAZON_DOMAINS.put("BR", "com.br");
  }

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

  @OnClick({R.id.youtube, R.id.lastfm, R.id.google, R.id.musicbrainz, R.id.amazon, R.id.play})
  public void onClick(View view) {
    String query = getArguments().getString("query");

    try {
      switch (view.getId()) {
        case R.id.youtube:
          openYoutube(query);
          break;
        case R.id.lastfm:
          ViewUtils.launchUrlWithCustomTab("http://www.lastfm.com/search?q="
              + URLEncoder.encode(query, "UTF-8"), getActivity());
          break;
        case R.id.google:
          ViewUtils.launchUrlWithCustomTab("https://www.google.com/search?q="
              + URLEncoder.encode(query, "UTF-8"), getActivity());
          break;
        case R.id.musicbrainz:
          ViewUtils.launchUrlWithCustomTab("https://musicbrainz.org/release/"
              + getArguments().getString("mbid"), getActivity());
          break;
        case R.id.amazon:
          ViewUtils.launchUrlWithCustomTab("https://www.amazon." + getAmazonDomain()
              + "/gp/aw/s/?k=" + URLEncoder.encode(query, "UTF-8"), getActivity());
          break;
        case R.id.play:
          openGooglePlay(query);
          break;
        default:
          throw new IllegalArgumentException(view.getId() + "not implemented");
      }
    } catch (UnsupportedEncodingException ex) {
      Log.e(this.getTag(), ex.getMessage(), ex);
    }
    dismiss();
  }

  private void openGooglePlay(String query) throws UnsupportedEncodingException {
    Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
    intent.setData(Uri.parse("market://search?c=music&q=" + query));
    try {
      startActivity(intent);
    } catch (ActivityNotFoundException ex) {
      ViewUtils.launchUrlWithCustomTab("https://play.google.com/store/search?c=music&q="
          + URLEncoder.encode(query, "UTF-8"), getActivity());
    }
  }

  private void openYoutube(String query) throws UnsupportedEncodingException {
    Intent intent = new Intent(Intent.ACTION_SEARCH);
    intent.setPackage("com.google.android.youtube");
    intent.putExtra("query", query);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    try {
      startActivity(intent);
    } catch (ActivityNotFoundException ex) {
      //youtube app? no problem
      ViewUtils.launchUrlWithCustomTab("https://www.youtube.com/results?search_query="
          + URLEncoder.encode(query, "UTF-8"), getActivity());
    }
  }

  public static ReleaseSearchBottomSheetFragment newInstance(String query, String mbid) {
    ReleaseSearchBottomSheetFragment fragment = new ReleaseSearchBottomSheetFragment();

    Bundle args = new Bundle();
    args.putString("query", query);
    args.putString("mbid", mbid);
    fragment.setArguments(args);

    return fragment;
  }

  private String getAmazonDomain() {
    String locale = getContext().getResources().getConfiguration().locale.getCountry();
    String domain = AMAZON_DOMAINS.get(locale);
    if (domain == null) {
      domain = AMAZON_DOMAINS.get("US");
    }
    return domain;
  }
}