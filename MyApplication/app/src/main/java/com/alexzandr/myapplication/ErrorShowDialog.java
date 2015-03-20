package com.alexzandr.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

/**
 * Created by anekrasov on 19.03.15.
 */
public class ErrorShowDialog extends DialogFragment implements OnClickListener {
    public static final String KEY_FOR_ERROR = "error";
    OnShowMainMenu mActivity;
    AlertDialog.Builder dialog;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new AlertDialog.Builder((LoginActivity)mActivity)
                .setTitle(R.string.errorShow_title)
                .setNegativeButton(R.string.errorShow_negativeButton, this)
                .setMessage(getArguments().getString(KEY_FOR_ERROR));

        setCancelable(true);
        return dialog.create();
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        try {
            mActivity = (OnShowMainMenu) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMadeServerChoice");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        this.dismissAllowingStateLoss();
    }

    public interface OnShowMainMenu {
        public void showMainMenu();
    }
}
