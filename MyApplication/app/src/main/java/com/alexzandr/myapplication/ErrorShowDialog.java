package com.alexzandr.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by anekrasov on 19.03.15.
 */
public class ErrorShowDialog extends DialogFragment  implements DialogInterface.OnClickListener {
    public static final String KEY_FOR_ERROR = "error";
    private OnShowErrors mActivity;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        try {
            mActivity = (OnShowErrors) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement " + OnShowErrors.class.getName());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder mDialog = new AlertDialog.Builder((Activity) mActivity)
                .setTitle(R.string.errorShow_title)
                .setNegativeButton(R.string.errorShow_negativeButton, this)
                .setMessage(getArguments().getString(KEY_FOR_ERROR));

        setCancelable(false);
        return mDialog.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        this.dismissAllowingStateLoss();
    }

    public interface OnShowErrors {
        void showError(String errorText);
    }
}
