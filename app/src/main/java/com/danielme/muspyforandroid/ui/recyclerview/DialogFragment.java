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
package com.danielme.muspyforandroid.ui.recyclerview;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

public class DialogFragment extends AppCompatDialogFragment {

  public static final String TAG = "OkDialogFragment";

  private static final String MSG = "msg";
  private static final String TITLE = "title";
  private static final String LISTENER = "listener";
  private static final String YES = "yes";
  private static final String NO = "no";

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle(getArguments().getString(TITLE));
    builder.setMessage(getArguments().getString(MSG));
    setCancelable(false);

    DialogInterface.OnClickListener onClickListener = null;
    final OkDialogFragmentListener listener = getArguments().getParcelable(LISTENER);
    if (listener != null) {
      onClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          listener.onOkDialogFragment();
        }
      };
    }

    builder.setPositiveButton(getArguments().getString(YES), onClickListener);
    String no = getArguments().getString(NO);
    if (no != null) {
      builder.setNegativeButton(no, null);
    }

    return builder.create();
  }

  public static DialogFragment newInstance(String title, String msg, String yes, String no,
                                           OkDialogFragmentListener listener) {
    Bundle args = new Bundle();
    args.putString(MSG, msg);
    args.putString(TITLE, title);
    args.putString(YES, yes);
    args.putString(NO, no);
    args.putParcelable(LISTENER, listener);
    DialogFragment frag = new DialogFragment();
    frag.setArguments(args);

    return frag;
  }

  /**
   * Listener for the  ok button  of the dialog fragment.
   */
  public interface OkDialogFragmentListener extends Parcelable {
    void onOkDialogFragment();
  }

}