package com.alexzandr.myapplication.activity;

import android.app.FragmentManager;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.alexzandr.myapplication.application.Singleton;
import com.alexzandr.myapplication.fragment.dialog.ErrorShowDialog;
import com.alexzandr.myapplication.fragment.dialog.LoginDialog;
import com.alexzandr.myapplication.fragment.WarehouseFragment;

public abstract class TabletActivity extends ActionBarActivity implements
        ErrorShowDialog.OnShowErrors,
        LoginDialog.LoginDialogInteractionListener,
        WarehouseFragment.OnFragmentInteractionListener {

    protected ErrorShowDialog mErrorShowDialog;
    protected boolean mIsErrorDialogShow = false;
    public static final String TAG_FOR_ERROR_DIALOG = "ErrorDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if  (!Singleton.isTablet()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mErrorShowDialog = (ErrorShowDialog) getFragmentManager().findFragmentByTag(TAG_FOR_ERROR_DIALOG);
        if (mErrorShowDialog == null) {
            mErrorShowDialog = new ErrorShowDialog();
        } else {
            mIsErrorDialogShow = true;
            if (!isPortOrientation()) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().remove(mErrorShowDialog).commit();
                fragmentManager.executePendingTransactions();
            }
        }
    }

    @Override
    public void showError(String errorText) {
        Bundle errorMassage = new Bundle();
        errorMassage.putString(ErrorShowDialog.KEY_FOR_ERROR, errorText);
        mErrorShowDialog.setArguments(errorMassage);
        mErrorShowDialog.show(getFragmentManager(), TAG_FOR_ERROR_DIALOG);
        mIsErrorDialogShow = true;
    }

    @Override
    public void onFragmentInteraction(int buttonId) {}

    @Override
    public void logOut(){}

    @Override
    public void logIn() {}

    public boolean isPortOrientation() {
        return Singleton.isPortOrientation();
    }
}
