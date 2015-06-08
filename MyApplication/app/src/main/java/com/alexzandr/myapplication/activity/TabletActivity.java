package com.alexzandr.myapplication.activity;

import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.alexzandr.myapplication.Singleton;
import com.alexzandr.myapplication.fragment.dialog.ErrorShowDialog.OnShowErrors;
import com.alexzandr.myapplication.fragment.dialog.LoginDialog.LoginDialogInteractionListener;
import com.alexzandr.myapplication.fragment.tablet.WarehouseFragment.OnFragmentInteractionListener;

abstract class TabletActivity extends ActionBarActivity implements LoginDialogInteractionListener,
        OnShowErrors, OnFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if  (!Singleton.isTablet()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void showError(String errorText) {}

    @Override
    public void onFragmentInteraction() {}

    @Override
    public void logOut(){}

    @Override
    public void logIn() {}

    public boolean isPortOrientation() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return displayMetrics.widthPixels < displayMetrics.heightPixels;
    }
}
