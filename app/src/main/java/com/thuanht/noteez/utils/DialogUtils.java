package com.thuanht.noteez.utils;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.thuanht.noteez.R;

public class DialogUtils {
    private static DialogUtils instance;
    private onClickButtonDialog onClickButtonDialog;

    private DialogUtils() {
    }

    public static DialogUtils getInstance() {
        if (instance == null) {
            instance = new DialogUtils();
        }
        return instance;
    }

    public void showDialog(Context context, String message, onClickButtonDialog listener) {
        this.onClickButtonDialog = listener;
        // Use the Builder class for convenient dialog construction.
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
        builder.setMessage(message)
                .setPositiveButton("Thoát", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        onClickButtonDialog.onClick();
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
    public interface onClickButtonDialog{
        void onClick();
    }
}
