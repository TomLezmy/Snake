package com.tomlezmy.snake.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.tomlezmy.snake.R;

public class AddHighScoreDialogFragment extends DialogFragment{
    AlertDialog alertDialog;
    View dialogView;
    MyDialogListener callback;
    TextInputEditText name;
    Button cancel, save;

    public interface MyDialogListener {
        void onReturn(String name);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (MyDialogListener)context;
        } catch (ClassCastException ex) {
            throw new ClassCastException("The activity must implement MyDialogListener interface");
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.fragment_add_high_score, (ViewGroup) getActivity().findViewById(R.id.root_layout), false);
        builder.setView(dialogView);
        alertDialog = builder.create();

        cancel = dialogView.findViewById(R.id.cancel_button);
        save = dialogView.findViewById(R.id.save_button);
        name = dialogView.findViewById(R.id.name_text);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onReturn(null);
                alertDialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!name.getText().toString().isEmpty()) {
                    callback.onReturn(name.getText().toString());
                    alertDialog.dismiss();
                }
            }
        });

        setCancelable(false);
        return alertDialog;
    }
}
