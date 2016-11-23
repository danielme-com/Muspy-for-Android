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
package com.danielme.muspyforandroid.ui.activities;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.ui.ViewUtils;
import com.danielme.muspyforandroid.ui.fragments.SearchArtistFragment;

import butterknife.ButterKnife;

/**
 * Searches an artists in MusicBrainz.
 */
public class SearchArtistActivity extends AbstractBaseActivity {

  public static final String INTENT_ARTISTS = "artists";
  private static final String FRG_TAG = "searchArtistsFrg";

  private SearchView searchView;
  private View closeIcon;

  Toolbar toolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_search_artist);
    ButterKnife.bind(this);
    toolbar = ViewUtils.initDefaultToolbarUpNavigationListener(this, null);

    //when rotating fragment is reused
    if (savedInstanceState == null) {
      SearchArtistFragment searchArtistFragment = new SearchArtistFragment();
      searchArtistFragment.setArguments(getIntent().getExtras());
      getSupportFragmentManager().beginTransaction()
          .add(R.id.layout, searchArtistFragment, FRG_TAG)
          .commit();
    }

  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.activity_search_artist_menu, menu);

    final MenuItem searchItem = menu.findItem(R.id.action_search);

    searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
    //displays always the searchview textfield
    searchView.setIconified(false);
    closeIcon = searchView.findViewById(R.id.search_close_btn);
    if (closeIcon != null) {
      //close icon is only enabled if the text field is not empty
      closeIcon.setEnabled(false);
      closeIcon.setOnClickListener(new View.OnClickListener() {
        //cancels the current search
        @Override
        public void onClick(View v) {
          SearchArtistFragment fragment =
              (SearchArtistFragment) getSupportFragmentManager().findFragmentByTag(FRG_TAG);
          if (fragment != null) {
            fragment.cancelSearch();
          }
          //unfortunally I have to implement the default behaviour
          searchView.setQuery(null, false);
          searchView.requestFocus();
          ViewUtils.showKeyboard(searchView.findViewById(R.id.search_src_text),
              SearchArtistActivity.this);
        }
      });
    }

    searchView.setQueryHint(getString(R.string.search_artist));
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        if (query.trim().length() > 1) {
          ViewUtils.hideKeyboard(SearchArtistActivity.this, searchView);
          SearchArtistFragment fragment = (SearchArtistFragment) getSupportFragmentManager()
              .findFragmentByTag(FRG_TAG);
          fragment.refresh();
        }
        return true;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        if (closeIcon != null) {
          //close icon is only enabled if the text field is not empty
          closeIcon.setEnabled(newText.isEmpty() ? false : true);
        }

        return true;
      }
    });
    //the searchview will never be collapsed
    searchView.setOnCloseListener(new SearchView.OnCloseListener() {
      @Override
      public boolean onClose() {
        return true;
      }
    });

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  protected void onResume() {
    super.onResume();
    //avoids display the keyboard
    if (searchView != null) {
      searchView.clearFocus();
    }
  }

  public SearchView getSearchView() {
    return searchView;
  }

}