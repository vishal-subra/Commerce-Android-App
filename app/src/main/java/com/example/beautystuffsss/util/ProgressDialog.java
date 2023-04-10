package com.example.beautystuffsss.util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.beautystuffsss.R;

public class ProgressDialog {
    Dialog dialog;
    Context context;

    public ProgressDialog(Context context) {
        this.context = context;
    }

    public void show(String message) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.layout_progress_dialog, null, false);
        dialog.setContentView(view);
        TextView dialogText = view.findViewById(R.id.progress_dialog_text);
        dialogText.setText(message);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
