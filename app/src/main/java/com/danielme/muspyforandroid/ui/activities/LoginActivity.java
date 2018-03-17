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
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.danielme.muspyforandroid.MuspyApplication;
import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.exceptions.ForbiddenUnauthorizedException;
import com.danielme.muspyforandroid.model.Credential;
import com.danielme.muspyforandroid.model.User;
import com.danielme.muspyforandroid.service.UserService;
import com.danielme.muspyforandroid.ui.ViewUtils;
import com.danielme.muspyforandroid.ui.recyclerview.DialogFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends AbstractBaseActivity {

  //failed login attemps before displaying a help message
  private static final int MAX_ATTEMPS = 3;
  private int attemps;

  @Inject
  UserService userService;

  @BindView(R.id.editTextEmail)
  EditText editTextEmail;
  @BindView(R.id.editTextPassword)
  EditText editTextPassword;
  @BindView(R.id.text_input_layout_email)
  TextInputLayout textInputLayoutEmail;
  @BindView(R.id.text_input_layout_pass)
  TextInputLayout textInputLayoutPass;

  private ProgressDialog progressDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);
    ((MuspyApplication) getApplicationContext()).getApplicationDaggerComponent().inject(this);

    ViewUtils.initDefaultToolbarUpNavigationListener(this);

    editTextEmail.addTextChangedListener(new ViewUtils.ClearErrorInputLayout(textInputLayoutEmail));
    editTextPassword.addTextChangedListener(
        new ViewUtils.ClearErrorInputLayout(textInputLayoutPass));
    editTextPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        boolean action = false;
        if (actionId == EditorInfo.IME_ACTION_DONE) {
          ViewUtils.clearEditTextFocus(LoginActivity.this);
          login(null);
          action = true;
        }
        return action;
      }
    });
    editTextEmail.requestFocus();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (progressDialog != null && progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
  }

  /**
   * Validates user input and performs the login with {@link LoginAsyncTask}
   */
  @OnClick(R.id.buttonSignin)
  public void login(View view) {
    boolean haserror = false;

    if (TextUtils.isEmpty(editTextEmail.getText())) {
      ViewUtils.toggleTextInputLayoutError(textInputLayoutEmail, getString(R.string.noemail));
      haserror = true;
    } else {
      ViewUtils.toggleTextInputLayoutError(textInputLayoutEmail, null);
    }

    if (TextUtils.isEmpty(editTextPassword.getText())) {
      ViewUtils.toggleTextInputLayoutError(textInputLayoutPass, getString(R.string.nopass));
      haserror = true;
    } else {
      ViewUtils.toggleTextInputLayoutError(textInputLayoutPass, null);
    }

    if (!haserror) {
      if (ViewUtils.isNetworkConnected(this)) {
        new LoginAsyncTask(editTextEmail.getText().toString(), editTextPassword.getText()
            .toString())
            .execute();
      } else {
        ViewUtils.showYesDialogFragment(this, android.R.string.dialog_alert_title,
            R.string.noconnection, null);
      }
    }
  }

  @OnClick(R.id.TextViewReset)
  public void reset(@Nullable View view) {
    navController.gotoReset(this, editTextEmail.getText().toString());
  }

  private class LoginAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private final String email;
    private final String password;
    private User user;

    LoginAsyncTask(String email, String password) {
      this.email = email;
      this.password = password;
    }

    @Override
    protected void onPreExecute() {
      progressDialog = ViewUtils.buildProgressDialog(LoginActivity.this, R.string.loading);
      progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
      try {
        Credential credential = new Credential(null, email, password);
        user = userService.getUser(credential);
        return true;
      } catch (ForbiddenUnauthorizedException ex) {
          return false;
      } catch (Exception ex) {
          Log.e(LoginActivity.class.getCanonicalName(), "error while checking credentials", ex);
          return null;
      }
    }

    @Override
    protected void onPostExecute(Boolean result) {

      progressDialog.dismiss();
      if (result == null) {
        ViewUtils.showYesDialogFragment(LoginActivity.this, android.R.string.dialog_alert_title,
            R.string.unknown_error, null);
      } else {
        if (user != null) {
          userService.storeCredentials(user, password);
          navController.gotoHome(LoginActivity.this);
        } else {
          attemps++;
          if (attemps == MAX_ATTEMPS) {
            ViewUtils.showYesNoDialogFragment(LoginActivity.this,
                android.R.string.dialog_alert_title,
                R.string.invalid_credentials_reset, new DialogFragment.OkDialogFragmentListener() {
                  @Override
                  public void onOkDialogFragment() {
                    reset(null);
                  }

                  @Override
                  public int describeContents() {
                    return 0;
                  }

                  @Override
                  public void writeToParcel(Parcel dest, int flags) {
                    //nothing here
                  }
                });

          } else {
            ViewUtils.showYesDialogFragment(LoginActivity.this, android.R.string.dialog_alert_title,
                R.string.invalid_credentials, null);
          }

        }
      }
    }

  }

}