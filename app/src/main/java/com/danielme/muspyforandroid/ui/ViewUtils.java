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
package com.danielme.muspyforandroid.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.ui.recyclerview.DialogFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Utility method used by activitis and fragments.
 */
public final class ViewUtils {

  private static final String LENGTH_FORMAT = "%02d:%02d:%02d";

  private ViewUtils() {
    //utility class
  }

  /**
   * Display/hides TextInputLayout error.
   *
   * @param msg the message, or null to hide
   */
  public static void toggleTextInputLayoutError(@NonNull TextInputLayout textInputLayout,
                                                String msg) {
    textInputLayout.setError(msg);
    if (msg == null) {
      textInputLayout.setErrorEnabled(false);
    } else {
      textInputLayout.setErrorEnabled(true);
    }
  }

  /**
   * Clears the focus and hides the soft keyboard if the currect focused view is an EditText.
   */
  public static void clearEditTextFocus(@NonNull Activity activity) {
    View view = activity.getCurrentFocus();
    if (view instanceof EditText) {
      InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(
          Context.INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
      view.clearFocus();
    }
  }

  /**
   * Hides the soft keyboard.
   */
  public static void hideKeyboard(@NonNull Activity activity, @NonNull View view) {
    InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(
        Context.INPUT_METHOD_SERVICE);
    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }

  public static void showKeyboard(@NonNull View view, @NonNull Context context) {
    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context
        .INPUT_METHOD_SERVICE);
    if (context.getResources().getConfiguration().orientation
        == Configuration.ORIENTATION_LANDSCAPE){
      imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
      imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    } else {
      imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }
  }

  public static Toolbar initDefaultToolbarUpNavigationListener(
      @NonNull final AppCompatActivity activity) {
    Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
    activity.setSupportActionBar(toolbar);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        activity.onBackPressed();
      }
    });
    return toolbar;
  }

  public static Toolbar initDefaultToolbarUpNavigationListener(
      @NonNull final AppCompatActivity activity, String title) {
    Toolbar toolbar = initDefaultToolbarUpNavigationListener(activity);
    activity.getSupportActionBar().setTitle(title);
    return toolbar;
  }

  public static ProgressDialog buildProgressDialog(Context context, int msg) {
    ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Progress);
    progressDialog.setMessage(context.getResources().getString(msg));
    progressDialog.setCancelable(false);
    return progressDialog;
  }

  /**
   * Opens an url using a customized Chrome Custom Tab.
   */
  public static void launchUrlWithCustomTab(String url, Activity activity) {
    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
    builder.setToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimary));
    builder.setCloseButtonIcon(BitmapFactory.decodeResource(activity.getResources(), R.drawable
        .ic_arrow_back));
    builder.setShowTitle(true);
    builder.build().launchUrl(activity, Uri.parse(url));
  }

  public static boolean isNetworkConnected(@NonNull Context context) {
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
        Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = connectivityManager.getActiveNetworkInfo();
    return info != null && info.isConnected() && info.isAvailable();
  }

  public static void showYesDialogFragment(@NonNull AppCompatActivity activity, int
      title, int msg, DialogFragment.OkDialogFragmentListener listener) {
    showYesDialogFragment(activity, title, activity.getString(msg), listener);
  }

  public static void showYesDialogFragment(@NonNull AppCompatActivity activity, int
      title, String msg, DialogFragment.OkDialogFragmentListener listener) {

    FragmentManager fm = activity.getSupportFragmentManager();
    DialogFragment errorDialogFragment = DialogFragment.newInstance(activity.getString(title),
        msg, activity.getString(android.R.string.yes), null, listener);
    errorDialogFragment.show(fm, DialogFragment.TAG);
  }

  public static void showYesDialogFragment(@NonNull Fragment fragment, int
      title, int msg, DialogFragment.OkDialogFragmentListener listener) {
    FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
    Fragment prev = fragment.getFragmentManager().findFragmentByTag(DialogFragment.TAG);
    if (prev != null) {
      ft.remove(prev);
    }
    ft.addToBackStack(null);

    DialogFragment errorDialogFragment = DialogFragment.newInstance(fragment.getString(title),
        fragment.getString(msg), fragment.getString(android.R.string.yes), null, listener);
    errorDialogFragment.show(ft, DialogFragment.TAG);
  }

  public static void showYesNoDialogFragment(@NonNull AppCompatActivity activity, int title,
                                             int msg,
                                             DialogFragment.OkDialogFragmentListener listener) {
    FragmentManager fm = activity.getSupportFragmentManager();
    DialogFragment errorDialogFragment = DialogFragment.newInstance(activity.getString(title),
        activity.getString(msg), activity.getString(android.R.string.yes),
        activity.getString(android.R.string.no), listener);
    errorDialogFragment.show(fm, DialogFragment.TAG);
  }

  public static boolean isAnyDialoFragmentVisible(Fragment fragment) {
    return isDialogFragmentShowing(fragment, DialogFragment.TAG) || isDialogFragmentShowing(
        fragment, DialogFragment.TAG);
  }

  public static boolean isDialogFragmentShowing(Fragment fragment, String tag) {
    DialogFragment dialogFragment = (DialogFragment) fragment.getFragmentManager()
        .findFragmentByTag(tag);
    return dialogFragment != null && dialogFragment.getDialog() != null
        && dialogFragment.getDialog().isShowing();
  }

  /**
   * Parses the dates provided by Muspy: <ul><li>yyyy <li>yyyy-MM <li>yyyy-MM-dd</ul>
   */
  public static ReleaseDate localizedDate(String dateString) {
    ReleaseDate releaseDate = new ReleaseDate();
    if (dateString != null) {
      String patternOriginal = null;
      String newPattern = null;
      if (dateString.matches("^\\d{4}$")) {
        patternOriginal = "yyyy";
        newPattern = "yyyy";
      } else if (dateString.matches("^\\d{4}-\\d{2}$")) {
        patternOriginal = "yyyy-MM";
        newPattern = "MM/yyyy";
      } else if (dateString.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
        patternOriginal = "yyyy-MM-dd";
        if (Locale.getDefault().getLanguage().equalsIgnoreCase("ES")) {
          newPattern = "dd/MM/yyyy";
        } else {
          newPattern = "MM/dd/yyyy";
        }
      }
      if (patternOriginal != null) {
        try {
          SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patternOriginal);
          Date date = simpleDateFormat.parse(dateString);
          simpleDateFormat = new SimpleDateFormat(newPattern);
          releaseDate.setDate(date);
          releaseDate.setString(simpleDateFormat.format(date));
          return releaseDate;
        } catch (Exception ex) {
            Log.e(ViewUtils.class.getCanonicalName(), ex.getMessage(), ex);
        }
      } else {
          releaseDate.setString(dateString);
          return releaseDate;
      }
    }
    releaseDate.setString("");
    return releaseDate;
  }

  /**
   * Converts the milliseconds into a human readeable string (03:27)
   */
  public static String formatMilliseconds(int millis) {
    String time = null;
    if (millis > 0) {
      time = String.format(LENGTH_FORMAT, TimeUnit.MILLISECONDS.toHours(millis),
          TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
          TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
      if (time.startsWith("00:")) {
        time = time.substring("00:".length());
      }
    }
    return time;
  }

  /**
   * Reads a text file from the assets.
   */
  public static String loadFromAssets(Context context, String assetPath) throws IOException {
    StringBuilder sb = new StringBuilder();
    BufferedReader in = null;
    try {
      InputStream is = context.getAssets().open(assetPath);
      in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
      String str;
      while ((str = in.readLine()) != null) {
        sb.append(str);
      }
    } finally {
      if (in != null) {
        in.close();
      }
    }
    return sb.toString();
  }

  /**
   * Default color scheme for the swipe layout indicator.
   */
  public static int[] getSwipeColorScheme() {
    return new int[]{R.color.indigo, R.color.green, R.color.orange};
  }


  public static class ReleaseDate {

    private Date date;
    private String string;

    public Date getDate() {
      return date;
    }

    public void setDate(Date date) {
      this.date = date;
    }

    public String getString() {
      return string;
    }

    public void setString(String string) {
      this.string = string;
    }
  }

  /**
   * Hides the error message after text changes.
   */
  public static class ClearErrorInputLayout implements TextWatcher {

    private final TextInputLayout textInputLayout;

    public ClearErrorInputLayout(TextInputLayout textInputLayou) {
      this.textInputLayout = textInputLayou;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      //nothing here
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      //nothing here
    }

    @Override
    public void afterTextChanged(Editable s) {
      ViewUtils.toggleTextInputLayoutError(textInputLayout, null);
    }

  }

}