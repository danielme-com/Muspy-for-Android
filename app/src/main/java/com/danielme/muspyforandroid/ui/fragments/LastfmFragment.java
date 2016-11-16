package com.danielme.muspyforandroid.ui.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.danielme.muspyforandroid.MuspyApplication;
import com.danielme.muspyforandroid.NavigationController;
import com.danielme.muspyforandroid.R;
import com.danielme.muspyforandroid.exceptions.ForbiddenUnauthorizedException;
import com.danielme.muspyforandroid.service.ArtistService;
import com.danielme.muspyforandroid.service.UserService;
import com.danielme.muspyforandroid.ui.ViewUtils;
import com.danielme.muspyforandroid.ui.recyclerview.DialogFragment;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This fragment handles the form for importing the artists of a lastfm user.
 */
public class LastfmFragment extends Fragment {

  private static final int DEFAULT_SEEKBAR = 25;
  private static final int USERNAME_MIN = 3;
  private static final int USERNAME_MAX = 15;
  //perido label myspy api
  private static final String[] PERIOD = {"overall", "12month", "6month", "3month", "7day"};

  @Inject
  ArtistService artistService;
  @Inject
  UserService userService;
  @Inject
  NavigationController navController;

  @Bind(R.id.seekBar)
  SeekBar seekBar;
  //Seekbar label
  @Bind(R.id.textViewNumber)
  TextView textViewNumber;
  @Bind(R.id.spinner)
  Spinner spinnerPeriod;
  @Bind(R.id.editTextLastfm)
  EditText editTextLastfm;
  @Bind(R.id.text_input_layout_lastfm)
  TextInputLayout textInputLayout;

  private ProgressDialog progressDialog;
  private ImportAsyncTask importAsyncTask;

  private final View.OnTouchListener touchListener = new View.OnTouchListener() {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
      ViewUtils.hideKeyboard(getActivity(), editTextLastfm);
      return false;
    }
  };

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    setHasOptionsMenu(true);

    View view = inflater.inflate(R.layout.fragment_lastfm, container, false);
    ButterKnife.bind(this, view);
    ((MuspyApplication) getContext().getApplicationContext()).getApplicationDaggerComponent()
        .inject(this);

    initForm();

    return view;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.fragment_lastfm_menu, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_import:
        importArtists();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    if (importAsyncTask != null) {
      importAsyncTask.cancel(false);
    }
    if (progressDialog != null) {
      progressDialog.dismiss();
    }
  }

  private void importArtists() {
    if (editTextLastfm.getText().length() < USERNAME_MIN
        || editTextLastfm.getText().length() > USERNAME_MAX) {
      ViewUtils.toggleTextInputLayoutError(textInputLayout,
          getString(R.string.invalid_user_lastfm));
    } else {
      ViewUtils.clearEditTextFocus(getActivity());
      ViewUtils.toggleTextInputLayoutError(textInputLayout, null);
      importAsyncTask = new ImportAsyncTask(editTextLastfm.getText().toString(), textViewNumber
          .getText().toString(), spinnerPeriod.getSelectedItemPosition());
      importAsyncTask.execute();
    }
  }

  private void initForm() {

    textViewNumber.setText(String.valueOf(DEFAULT_SEEKBAR));
    editTextLastfm.requestFocus();

    //the same order than period attribute
    String[] periodNames = {getString(R.string.periodOverall), getString(R.string.period12month),
        getString(R.string.period6month), getString(R.string.period3month), getString(R.string
        .period7day)};
    spinnerPeriod.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_item, periodNames));

    spinnerPeriod.setOnTouchListener(touchListener);
    seekBar.setOnTouchListener(touchListener);
    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        textViewNumber.setText(String.valueOf(progress + DEFAULT_SEEKBAR));
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
        //not implemented
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        //not implemented
      }
    });
  }

  class ImportAsyncTask extends AsyncTask<Void, Void, Integer> {

    private static final int OK = 0;
    private static final int NO_USER = 1;
    private static final int EXC = 2;

    private final String user;
    private final String top;
    private final int periodPostion;

    ImportAsyncTask(String user, String top, int periodPostion) {
      this.user = user;
      this.top = top;
      this.periodPostion = periodPostion;
    }

    @Override
    protected void onPreExecute() {
      if (!ViewUtils.isNetworkConnected(getActivity())) {
        cancel(true);
        if (!ViewUtils.isAnyDialoFragmentVisible(LastfmFragment.this)) {
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
        if (userService.checkLastfmUser(user)) {
          if (!isCancelled() && artistService.importLastfm(user, PERIOD[periodPostion], top)) {
            return OK;
          } else {
            return EXC;
          }
        } else {
          return NO_USER;
        }
      } catch (ForbiddenUnauthorizedException ex) {
          userService.deleteCredentials();
          cancel(false);
          navController.gotoLogin(getActivity());
          return EXC;
      } catch (IOException ex) {
          Log.e(this.getClass().toString(), "exception importing form lastfm", ex);
          return EXC;
      }
    }

    @Override
    protected void onPostExecute(Integer result) {
      progressDialog.dismiss();
      switch (result) {
        case OK:
          ViewUtils.showYesDialogFragment(LastfmFragment.this, R.string.dialog_info_title, R.string
              .info_import_success, new DialogFragment.OkDialogFragmentListener() {
            @Override
            public void onOkDialogFragment() {
              if (getActivity() != null) {
                getActivity().finish();
              }
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
          break;
        case NO_USER:
          ViewUtils.showYesDialogFragment(LastfmFragment.this, android.R.string
              .dialog_alert_title, R.string.lastfm_user_unknown, null);
          break;
        default:
          ViewUtils.showYesDialogFragment(LastfmFragment.this, android.R.string
              .dialog_alert_title, R.string.unknown_error, null);
          break;
      }
    }

  }

}
