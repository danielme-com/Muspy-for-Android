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
import com.danielme.muspyforandroid.model.Credential;
import com.danielme.muspyforandroid.model.User;
import com.danielme.muspyforandroid.service.UserService;
import com.danielme.muspyforandroid.ui.ViewUtils;
import com.danielme.muspyforandroid.ui.recyclerview.DialogFragment;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Signs up a new user. If the process is succesful user is logged.
 */
public class RegisterActivity extends AbstractBaseActivity {

  private static final int PASSWORD_LENGTH = 4;
  private static final String POLICY_URL = "https://muspy.com/about";

  @Inject
  UserService userService;

  @BindView(R.id.editTextEmail)
  EditText editTextEmail;
  @BindView(R.id.editTextPassword)
  EditText editTextPassword;
  @BindView(R.id.editTextRepeatPassword)
  EditText editTextRepeatPassword;
  @BindView(R.id.text_input_layout_email)
  TextInputLayout textInputLayoutEmail;
  @BindView(R.id.text_input_layout_pass)
  TextInputLayout textInputLayoutPass;
  @BindView(R.id.text_input_layout_repeat_pass)
  TextInputLayout textInputLayoutRepeatPass;

  private ProgressDialog progressDialog;
  private RegisterAsyncTask registerAsyncTask;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
    ButterKnife.bind(this);
    ((MuspyApplication) getApplicationContext()).getApplicationDaggerComponent().inject(this);
    ViewUtils.initDefaultToolbarUpNavigationListener(this);

    editTextEmail.addTextChangedListener(new ViewUtils.ClearErrorInputLayout(textInputLayoutEmail));
    editTextPassword.addTextChangedListener(
        new ViewUtils.ClearErrorInputLayout(textInputLayoutPass));
    editTextRepeatPassword.addTextChangedListener(
        new ViewUtils.ClearErrorInputLayout(textInputLayoutRepeatPass));
    editTextRepeatPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        boolean action = false;
        if (actionId == EditorInfo.IME_ACTION_DONE) {
          ViewUtils.clearEditTextFocus(RegisterActivity.this);
          signup(null);
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
    if (registerAsyncTask != null) {
      registerAsyncTask.cancel(false);
    }
  }

  /**
   * Validates user input.
   */
  @OnClick(R.id.buttonSignup)
  public void signup(View view) {
    boolean haserror = false;

    if (TextUtils.isEmpty(editTextEmail.getText())) {
      ViewUtils.toggleTextInputLayoutError(textInputLayoutEmail, super.getString(R.string.noemail));
      haserror = true;
    } else if (!Patterns.EMAIL_ADDRESS.matcher(editTextEmail.getText()).matches()) {
      ViewUtils.toggleTextInputLayoutError(textInputLayoutEmail, super.getString(R.string
          .invalid_email));
      haserror = true;
    } else {
      ViewUtils.toggleTextInputLayoutError(textInputLayoutEmail, null);
    }

    if (TextUtils.isEmpty(editTextPassword.getText())) {
      ViewUtils.toggleTextInputLayoutError(textInputLayoutPass, super.getString(R.string.nopass));
      haserror = true;
    } else if (editTextPassword.getText().length() < PASSWORD_LENGTH) {
      ViewUtils.toggleTextInputLayoutError(textInputLayoutPass, super.getString(R.string
          .pass_length));
      haserror = true;
    } else {
      ViewUtils.toggleTextInputLayoutError(textInputLayoutPass, null);
    }

    if (!editTextPassword.getText().toString().equals(editTextRepeatPassword.getText().toString())
        ) {
      ViewUtils.toggleTextInputLayoutError(textInputLayoutRepeatPass, super.getString(R.string
          .pass_nomatch));
      haserror = true;
    } else {
      ViewUtils.toggleTextInputLayoutError(textInputLayoutRepeatPass, null);
    }

    if (!haserror) {
      registerAsyncTask = new RegisterAsyncTask(editTextEmail.getText()
          .toString(), editTextPassword.getText().toString());
      registerAsyncTask.execute();
    }

  }

  @OnClick(R.id.TextViewPolicy)
  public void policy(View view) {
    ViewUtils.launchUrlWithCustomTab(POLICY_URL, this);
  }


  /**
   * onPreExecute implementation for all tasks.
   * @param task
   */
  private void commonOnPreExecute(AsyncTask task) {
    if (!ViewUtils.isNetworkConnected(this)) {
      task.cancel(true);
      ViewUtils.showYesDialogFragment(this, android.R.string.dialog_alert_title,
          R.string.noconnection, null);
    } else {
      progressDialog = ViewUtils.buildProgressDialog(this, R.string.loading);
      progressDialog.show();
    }
  }

  /**
   * Signs up the user.
   */
  private class RegisterAsyncTask extends AsyncTask<Void, Void, Integer> {

    private final String email;
    private final String password;

    RegisterAsyncTask(String email, String password) {
      this.email = email;
      this.password = password;
    }

    @Override
    protected void onPreExecute() {
      commonOnPreExecute(this);
    }

    @Override
    protected Integer doInBackground(Void... params) {
      try {
        return userService.createUser(email, password);
      } catch (IOException ex) {
        Log.e(RegisterActivity.class.getCanonicalName(), "error creating a new user: " + email, ex);
        return null;
      }
    }

    @Override
    protected void onPostExecute(Integer result) {
      progressDialog.dismiss();
      if (result == null) {
        ViewUtils.showYesDialogFragment(RegisterActivity.this, android.R.string
            .dialog_alert_title, R.string.unknown_error, null);
      } else {
        switch (result) {
          case UserService.CODE_EMAIL_DUPLICATE:
            ViewUtils.showYesDialogFragment(RegisterActivity.this,
                android.R.string.dialog_alert_title, R.string.email_duplicate, null);
            break;
          case UserService.CODE_ERROR:
            ViewUtils.showYesDialogFragment(RegisterActivity.this, android.R.string
                .dialog_alert_title, R.string.unknown_error, null);
            break;
          default:
            ViewUtils.showYesDialogFragment(RegisterActivity.this, R.string.dialog_info_title,
                R.string.emailsentsignup, new DialogFragment.OkDialogFragmentListener() {
                  @Override
                  public void onOkDialogFragment() {
                    (new AutoLoginAsyncTask(email, password)).execute();
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
            break;
        }
      }

    }

  }

  /**
   * Logs the new user in Muspy for Android.
   */
  private class AutoLoginAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private final String email;
    private final String password;
    User user;

    AutoLoginAsyncTask(String email, String password) {
      this.email = email;
      this.password = password;
    }

    @Override
    protected void onPreExecute() {
      commonOnPreExecute(this);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
      try {
        Credential credential = new Credential(null, email, password);
        user = userService.getUser(credential);
        return Boolean.TRUE;
      } catch (Exception ex) {
        Log.e(RegisterActivity.class.getCanonicalName(), "error while checking credentials", ex);
        return Boolean.FALSE;
      }
    }

    @Override
    protected void onPostExecute(Boolean result) {
      progressDialog.dismiss();
      if (!result) {
        ViewUtils.showYesDialogFragment(RegisterActivity.this, android.R.string.dialog_alert_title,
            R.string.unknown_error, null);
      } else {
        if (user != null) {
          userService.storeCredentials(user, password);
          navController.gotoHome(RegisterActivity.this);
        } else {
          ViewUtils.showYesDialogFragment(RegisterActivity.this,
              android.R.string.dialog_alert_title, R.string.invalid_autologin, null);
        }
      }
    }

  }

}