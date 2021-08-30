package com.example.bookClub.browsebooks;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.bookClub.R;

public class SubmitIntensityDialogFragment extends DialogFragment {

    public interface SubmitIntensityDialogListener {
        public void onDialogIntensityClick (DialogFragment dialog);
    }

    SubmitIntensityDialogFragment.SubmitIntensityDialogListener listener;
    String intensity = "";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (SubmitIntensityDialogFragment.SubmitIntensityDialogListener) context;
        } catch (ClassCastException e) {}
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Submit Intensity Level");

        builder.setItems(R.array.intensity_array, (dialog, which) -> {
            intensity = getResources().getStringArray(R.array.intensity_array)[which];
            listener.onDialogIntensityClick(SubmitIntensityDialogFragment.this);
        });
        return builder.create();
    }
}