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

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.danielme.muspyforandroid.MuspyApplication;
import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.service.UserService;
import com.danielme.muspyforandroid.ui.ViewUtils;
import com.danielme.muspyforandroid.ui.recyclerview.DialogFragment;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * This activity handles the UI for reset the password).
 */
public class ResetActivity extends AbstractBaseActivity {

  public static final String EXTRA_EMAIL = "email";

  @Inject
  UserService userService;

  @BindView(R.id.editTextEmail)
  EditText editTextEmail;
  @BindView(R.id.text_input_layout_email)
  TextInputLayout textInputLayoutEmail;

  private ProgressDialog progressDialog;
  private ResetAsyncTask resetAsyncTask;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_reset);
    ButterKnife.bind(this);
    ((MuspyApplication) getApplicationContext()).getApplicationDaggerComponent().inject(this);
    ViewUtils.initDefaultToolbarUpNavigationListener(this);

    editTextEmail.setText(getIntent().getStringExtra(EXTRA_EMAIL));
    editTextEmail.addTextChangedListener(new ViewUtils.ClearErrorInputLayout(textInputLayoutEmail));
    editTextEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        boolean action = false;
        if (actionId == EditorInfo.IME_ACTION_DONE) {
          ViewUtils.clearEditTextFocus(ResetActivity.this);
          reset(null);
          action = true;
        }
        return action;
      }
    });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (progressDialog != null && progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
    if (resetAsyncTask != null) {
      resetAsyncTask.cancel(false);
    }
  }

  @OnClick(R.id.buttonReset)
  public void reset(View view) {
    if (TextUtils.isEmpty(editTextEmail.getText())) {
      ViewUtils.toggleTextInputLayoutError(textInputLayoutEmail, super.getString(R.string.noemail));
    } else if (!Patterns.EMAIL_ADDRESS.matcher(editTextEmail.getText()).matches()) {
      ViewUtils.toggleTextInputLayoutError(textInputLayoutEmail, super.getString(R.string
          .invalid_email));
    } else {
      resetAsyncTask = new ResetAsyncTask(editTextEmail.getText().toString());
      resetAsyncTask.execute();
    }
  }

  private class ResetAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private final String email;

    ResetAsyncTask(String email) {
      this.email = email;
    }

    @Override
    protected void onPreExecute() {
      if (!ViewUtils.isNetworkConnected(ResetActivity.this)) {
        cancel(true);
        ViewUtils.showYesDialogFragment(ResetActivity.this, android.R.string.dialog_alert_title,
            R.string.noconnection, null);
      } else {
        progressDialog = ViewUtils.buildProgressDialog(ResetActivity.this, R.string.loading);
        progressDialog.show();
      }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
      try {
        return userService.reset(email, ResetActivity.this);
      } catch (IOException ex) {
        Log.e(ResetActivity.class.getCanonicalName(), "error reset password: " + email, ex);
        return false;
      }
    }

    @Override
    protected void onPostExecute(Boolean result) {

      progressDialog.dismiss();
      if (result) {
        String msg = String.format(getString(R.string.emailsentreset), email);
        ViewUtils.showYesDialogFragment(ResetActivity.this, R.string.dialog_info_title,
            msg, new DialogFragment.OkDialogFragmentListener() {
              @Override
              public void onOkDialogFragment() {
                ResetActivity.this.onBackPressed();
              }

              @Override
              public int describeContents() {
                return 0;
              }

              @Override
              public void writeToParcel(Parcel parcel, int i) {
                //nothing here
              }
            });
      } else {
        ViewUtils.showYesDialogFragment(ResetActivity.this, android.R.string.dialog_alert_title,
            R.string.unknown_error_reset, null);

      }
    }

  }

}