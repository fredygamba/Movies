package com.example.movies.utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.movies.R;
import com.google.android.material.snackbar.Snackbar;

public class DialogsManager extends DialogFragment {

    private String message;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message).setPositiveButton(getResources().getString(R.string.close), null);
        return builder.create();
    }

    public void showDialog(FragmentManager fragmentManager, String message) {
        this.message = message;
        show(fragmentManager, "");
    }

    public static void showSnackBar(View view, String message) {
        showSnackBar(view, message, null, null);
    }

    public static void showSnackBar(View view, String message, View.OnClickListener action, String actionLabel) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        snackbar.setAction(actionLabel, action).show();
        snackbar.show();
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
