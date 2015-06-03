package com.alexzandr.myapplication.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.fragment.dialog.EnterIpDialog.EnterIpDialogInteractionListener;
import com.alexzandr.myapplication.fragment.dialog.LoginDialog;
import com.alexzandr.myapplication.fragment.dialog.SetHeightDialog;
import com.alexzandr.myapplication.fragment.tablet.BlankFragment;
import com.alexzandr.myapplication.fragment.tablet.MainMenuFragment;
import com.alexzandr.myapplication.fragment.tablet.WorkWithDocumentFragment;

public class WarehouseActivity extends TabletActivity implements EnterIpDialogInteractionListener {

    private LoginDialog mLoginDialog;
    private SetHeightDialog mDialogSetHeight;
    private Fragment mMenuFragment;
    private Fragment mDetailFragment;
    private Fragment mBlankFragment;
    private boolean mIsLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse);

        mDialogSetHeight = new SetHeightDialog();

    }

    @Override
    public void onResume(){
        super.onResume();
        showLoginForm();
    }

    @Override
    public void onStop(){
        super.onStop();
        System.out.println("ON STOP_STOP_STOP_STOP. IS LOGGED " + mIsLogged);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        try {
            mLoginDialog.dismiss();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        super.onSaveInstanceState(outState);

        if (mLoginDialog != null)
            outState.putParcelable("loginDialog", mLoginDialog);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLoginDialog = savedInstanceState.getParcelable("loginDialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        Bundle dialogType = new Bundle();

        switch (itemId){
            case R.id.login_menu_forget_me:
                SharedPreferences preferences = getSharedPreferences(getString(R.string.remember_preference_name), Context.MODE_PRIVATE);
                preferences.edit().clear().apply();
                break;
            case R.id.login_menu_settings:
                Intent intent = new Intent(WarehouseActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.login_menu_headLine_height:
                dialogType.putInt(SetHeightDialog.KEY_FOR_TYPE, SetHeightDialog.DIALOG_TYPE_HEADLINE_HEIGHT);
                mDialogSetHeight.setArguments(dialogType);
                mDialogSetHeight.show(getFragmentManager(), "SetHeadLineHeightDialog");
                break;
            case R.id.login_menu_section_height:
                dialogType.putInt(SetHeightDialog.KEY_FOR_TYPE, SetHeightDialog.DIALOG_TYPE_SECTION_HEIGHT);
                mDialogSetHeight.setArguments(dialogType);
                mDialogSetHeight.show(getFragmentManager(), "SetHeadLineHeightDialog");
                break;
            default: break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void logIn() {
        FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();

        if (mMenuFragment == null) {
            mMenuFragment = new MainMenuFragment();
        }
        fragTransaction.replace(R.id.warehouse_menuFrame, mMenuFragment);

        if (!isPortOrientation()) {
            if (mDetailFragment == null) {
                mDetailFragment = new BlankFragment();
            }
            fragTransaction.replace(R.id.warehouse_detailFrame, mDetailFragment);
        }

        fragTransaction.commit();

        mIsLogged = true;
        mLoginDialog = null;
    }


    public void showLoginForm() {
        FragmentTransaction fragTransaction;

        if (!mIsLogged) {

            if (mLoginDialog == null) {
                mLoginDialog = new LoginDialog();
            } else {
                fragTransaction = getFragmentManager().beginTransaction();
                fragTransaction.remove(mLoginDialog);
                fragTransaction.commit();
            }

            if (isPortOrientation()) {
                fragTransaction = getFragmentManager().beginTransaction();
                fragTransaction.add(R.id.warehouse_menuFrame, mLoginDialog);
                fragTransaction.commit();
            } else {
                mLoginDialog.show(getFragmentManager(), "LoginDialog");
            }
        }
    }

    @Override
    public void onServerChosen(String serverIp) {
        mLoginDialog.onServerChosen(serverIp);
    }


    @Override
    public void logOut(){
        mIsLogged = false;

        FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
        if (!isPortOrientation()) {
            fragTransaction.remove(mDetailFragment);
        }
        fragTransaction.remove(mMenuFragment);
        fragTransaction.commit();

        showLoginForm();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        mIsLogged = false;
    }

}
