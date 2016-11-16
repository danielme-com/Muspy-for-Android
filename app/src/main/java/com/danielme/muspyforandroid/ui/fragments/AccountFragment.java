package com.danielme.muspyforandroid.ui.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.danielme.muspyforandroid.MuspyApplication;
import com.danielme.muspyforandroid.NavigationController;
import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.exceptions.ForbiddenUnauthorizedException;
import com.danielme.muspyforandroid.model.User;
import com.danielme.muspyforandroid.service.UserService;
import com.danielme.muspyforandroid.ui.ViewUtils;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This fragment handles the user account form. The data displayed by this fragment is loaded
 * the first time this fragment is visible to the user ({@link AccountFragment#setUserVisibleHint}).
 */
public class AccountFragment extends Fragment {

  private static final String SETTINGS_URL = "https://muspy.com/settings";

  @Inject
  UserService userService;
  @Inject
  NavigationController navController;

  @Bind(R.id.editTextEmail)
  EditText editTextEmail;
  @Bind(R.id.text_input_layout_email)
  TextInputLayout textInputLayoutEmail;
  @Bind(R.id.checkBoxNotifications)
  CheckBox checkBoxNotifications;
  @Bind(R.id.checkBoxAlbum)
  CheckBox checkBoxAlbum;
  @Bind(R.id.checkBoxSingle)
  CheckBox checkBoxSingle;
  @Bind(R.id.checkBoxEP)
  CheckBox checkBoxEp;
  @Bind(R.id.checkBoxCompilation)
  CheckBox checkBoxCompilation;
  @Bind(R.id.checkBoxLive)
  CheckBox checkBoxLive;
  @Bind(R.id.checkBoxOther)
  CheckBox checkBoxOther;
  @Bind(R.id.checkBoxRemix)
  CheckBox checkBoxRemix;
  @Bind(R.id.button_refresh)
  Button buttonRefresh;
  @Bind(R.id.textView_message)
  TextView textViewMessage;
  @Bind(R.id.progress_circular_center)
  ProgressBar progressBar;
  @Bind(R.id.form)
  ViewGroup viewForm;

  private MenuItem menuSave;
  private ProgressDialog progressDialog;
  private SaveAsyncTask saveAsyncTask;
  private GetUserAsyncTask getUserAsyncTask;
  //if there was an error saving the form
  private boolean reloadData = true;

  /**
   * This listener hides the keyboard when the user taps a form input other than an edittext..
   */
  private final View.OnTouchListener touchListener = new View.OnTouchListener() {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
      ViewUtils.hideKeyboard(getActivity(), editTextEmail);
      return false;
    }
  };

  /**
   * Refresh the user datat every time the user views the fragment.
   */
  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (isVisibleToUser && reloadData) {
      refresh(null);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    setHasOptionsMenu(true);
    View rootView = inflater.inflate(R.layout.fragment_account, container, false);
    ButterKnife.bind(this, rootView);
    ((MuspyApplication) getContext().getApplicationContext()).getApplicationDaggerComponent()
        .inject(this);

    return rootView;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.fragment_account_menu, menu);
    menuSave = menu.findItem(R.id.action_save);
    //save action is only visible if the user data have been loaded into the form
    if (viewForm.getVisibility() != View.VISIBLE) {
      menuSave.setVisible(false);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_save:
        save();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onStop() {
    if (saveAsyncTask != null) {
      saveAsyncTask.cancel(true);
    }
    if (getUserAsyncTask != null) {
      getUserAsyncTask.cancel(false);
    }
    if (progressDialog != null) {
      progressDialog.dismiss();
    }
    super.onStop();
  }

  @OnClick(R.id.buttonOptions)
  public void moreOptions(View view) {
    ViewUtils.launchUrlWithCustomTab(SETTINGS_URL, getActivity());
  }


  @OnClick(R.id.button_refresh)
  public void refresh(View view) {
    //resets the form
    checkBoxCompilation.setChecked(false);
    checkBoxRemix.setChecked(false);
    checkBoxOther.setChecked(false);
    checkBoxLive.setChecked(false);
    checkBoxEp.setChecked(false);
    checkBoxAlbum.setChecked(false);
    checkBoxSingle.setChecked(false);
    checkBoxNotifications.setChecked(false);

    getUserAsyncTask = new GetUserAsyncTask();
    getUserAsyncTask.execute();
  }

  private void save() {
    if (!Patterns.EMAIL_ADDRESS.matcher(editTextEmail.getText().toString()).matches()) {
      ViewUtils.toggleTextInputLayoutError(textInputLayoutEmail, getString(R.string.noemail));
    } else {
      ViewUtils.toggleTextInputLayoutError(textInputLayoutEmail, null);
      User user = new User();

      user.setEmail(editTextEmail.getText().toString());
      user.setNotifications(checkBoxNotifications.isChecked());
      user.setFilterAlbum(checkBoxAlbum.isChecked());
      user.setFilterCompilation(checkBoxCompilation.isChecked());
      user.setFilterEP(checkBoxEp.isChecked());
      user.setFilterLive(checkBoxLive.isChecked());
      user.setFilterOther(checkBoxOther.isChecked());
      user.setFilterRemix(checkBoxRemix.isChecked());
      user.setFilterSingle(checkBoxSingle.isChecked());

      ViewUtils.clearEditTextFocus(getActivity());
      saveAsyncTask = new SaveAsyncTask(user);
      saveAsyncTask.execute();
    }
  }

  class SaveAsyncTask extends AsyncTask<Void, Void, Integer> {

    private final User user;

    SaveAsyncTask(User user) {
      this.user = user;
    }

    @Override
    protected void onPreExecute() {
      if (!ViewUtils.isNetworkConnected(getActivity())) {
        cancel(true);
        if (!ViewUtils.isAnyDialoFragmentVisible(AccountFragment.this)) {
          ViewUtils.showYesDialogFragment((AppCompatActivity) getActivity(), android.R.string
              .dialog_alert_title, R.string.noconnection, null);
        }
      } else {
        progressDialog = ViewUtils.buildProgressDialog(getActivity(), R.string.sending);
        progressDialog.show();
      }
    }

    @Override
    protected Integer doInBackground(Void... params) {
      try {
        return userService.updateUser(user);
      } catch (ForbiddenUnauthorizedException ex) {
        userService.deleteCredentials();
        cancel(false);
        navController.gotoLogin(getActivity());
        return UserService.CODE_ERROR;
      } catch (IOException e) {
        return UserService.CODE_ERROR;
      }
    }

    @Override
    protected void onPostExecute(Integer result) {
      progressDialog.dismiss();
      switch (result) {
        case UserService.CODE_EMAIL_DUPLICATE:
          ViewUtils.toggleTextInputLayoutError(textInputLayoutEmail, getString(R.string
              .email_duplicate));
          reloadData = false;
          break;
        case UserService.CODE_ERROR:
          ViewUtils.showYesDialogFragment(AccountFragment.this, android.R.string
              .dialog_alert_title, R.string.unknown_error, null);
          reloadData = true;
          break;
        default:
          //OK
          ViewUtils.showYesDialogFragment(AccountFragment.this, R.string.dialog_info_title, R.string
              .info_save_success, null);
          reloadData = false;
          break;
      }

    }
  }

  class GetUserAsyncTask extends AsyncTask<Void, Void, User> {

    @Override
    protected void onPreExecute() {
      viewForm.setVisibility(View.GONE);
      if (!ViewUtils.isNetworkConnected(getActivity())) {
        cancel(true);
        buttonRefresh.setText(getString(R.string.retry_button));
        displayMsg(R.string.noconnection);
      } else {
        hideMessage();
        progressBar.setVisibility(View.VISIBLE);
      }
    }

    @Override
    protected User doInBackground(Void... params) {
      if (!isCancelled()) {
        try {
          return userService.getUser();
        } catch (ForbiddenUnauthorizedException ex) {
          userService.deleteCredentials();
          cancel(false);
          navController.gotoLogin(getActivity());
        } catch (IOException ex) {
          Log.e(getActivity().getClass().getCanonicalName(), "error getting user data", ex);
        }
      }
      return null;
    }

    @Override
    protected void onPostExecute(User user) {
      progressBar.setVisibility(View.GONE);
      if (user != null) {
        menuSave.setVisible(true);
        reloadData = false;
        editTextEmail.setText(user.getEmail());
        ViewUtils.toggleTextInputLayoutError(textInputLayoutEmail, null);

        checkBoxNotifications.setChecked(user.isNotifications());
        checkBoxAlbum.setChecked(user.isFilterAlbum());
        checkBoxSingle.setChecked(user.isFilterSingle());
        checkBoxEp.setChecked(user.isFilterEP());
        checkBoxCompilation.setChecked(user.isFilterCompilation());
        checkBoxLive.setChecked(user.isFilterLive());
        checkBoxOther.setChecked(user.isFilterOther());
        checkBoxRemix.setChecked(user.isFilterRemix());

        checkBoxNotifications.setOnTouchListener(touchListener);
        checkBoxAlbum.setOnTouchListener(touchListener);
        checkBoxSingle.setOnTouchListener(touchListener);
        checkBoxEp.setOnTouchListener(touchListener);
        checkBoxCompilation.setOnTouchListener(touchListener);
        checkBoxLive.setOnTouchListener(touchListener);
        checkBoxOther.setOnTouchListener(touchListener);
        checkBoxRemix.setOnTouchListener(touchListener);

        hideMessage();

        viewForm.setVisibility(View.VISIBLE);
      } else {
        if (!ViewUtils.isAnyDialoFragmentVisible(AccountFragment.this)) {

          ViewUtils.showYesDialogFragment(AccountFragment.this, android.R.string.dialog_alert_title,
              R.string.unknown_error, null);
        }
        displayMsg(R.string.unknown_error);
      }
    }

    /**
     * Displays a message in the middle of the screen.
     */
    private void displayMsg(int id) {
      textViewMessage.setText(getString(id));
      textViewMessage.setVisibility(View.VISIBLE);
      if (R.string.nodata != id) {
        //retry button is always enabled when no data
        buttonRefresh.setVisibility(View.VISIBLE);
      } else {
        buttonRefresh.setVisibility(View.GONE);
      }
    }

    /**
     * Hides the message in the middle of the screen and displays teh RecyclerView
     */
    private void hideMessage() {
      textViewMessage.setVisibility(View.GONE);
      buttonRefresh.setVisibility(View.GONE);
    }
  }

}