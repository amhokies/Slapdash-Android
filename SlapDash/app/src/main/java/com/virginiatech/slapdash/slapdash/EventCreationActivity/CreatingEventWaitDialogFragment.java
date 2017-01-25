package com.virginiatech.slapdash.slapdash.EventCreationActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.virginiatech.slapdash.slapdash.R;

import java.util.zip.Inflater;

/**
 * Created by nima on 10/17/16.
 */

public class CreatingEventWaitDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(getActivity()
                .getLayoutInflater()
                .inflate(R.layout.event_creation_wait_fragment, null));

        return builder.create();
    }
}
