package com.alexzandr.myapplication.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.fragment.dialog.EnterIpDialog;
import com.alexzandr.myapplication.fragment.dialog.ErrorShowDialog;
import com.alexzandr.myapplication.fragment.dialog.LoginDialog;
import com.alexzandr.myapplication.fragment.dialog.LoginDialog.LoginDialogInteractionListener;
import com.alexzandr.myapplication.fragment.dialog.SetHeightDialog;
import com.alexzandr.myapplication.fragment.tablet.MainMenuFragment;
import com.alexzandr.myapplication.fragment.tablet.WarehouseFragment;
import com.alexzandr.myapplication.fragment.tablet.WorkWithDocumentFragment;

public class WarehouseActivity extends ActionBarActivity implements LoginDialogInteractionListener,
        ErrorShowDialog.OnShowErrors,
        EnterIpDialog.EnterIpDialogInteractionListener,
        WarehouseFragment.OnFragmentInteractionListener {

    private LoginDialog mLoginDialog;
    private SetHeightDialog mDialogSetHeight;
    private Fragment mMenuFragment;
    private Fragment mDetailFragment;
    private boolean isLaunched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse);
        System.out.println("CREATE A NEW WarehouseActivity");

        if (!isLaunched) {
            if (mLoginDialog == null) {
                System.out.println("SET mLoginDialog");
                mLoginDialog = new LoginDialog();
            }
            showLoginForm("FROM onCreate");
        }
        mDialogSetHeight = new SetHeightDialog();

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
    public void onLogIn() {
        FragmentManager fragManager = getFragmentManager();
        FragmentTransaction fragTransaction = fragManager.beginTransaction();

        if (mMenuFragment == null) {
            mMenuFragment = new MainMenuFragment();
            fragTransaction.add(R.id.warehouse_menuFrame, mMenuFragment);
        } else {
            mMenuFragment = new MainMenuFragment();
            fragTransaction.replace(R.id.warehouse_menuFrame, mMenuFragment);
        }

        if (!isPortOrientation()) {
            fragTransaction.add(R.id.warehouse_detailFrame, new LoginDialog());
        }

        fragTransaction.commit();

        isLaunched = true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        showLoginForm("FROM onConfigurationChanged");
    }

    public void showLoginForm(String fromWhat) {
        System.out.println(fromWhat);
        if (!isLaunched) {
            if (isPortOrientation()) {
                FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
                System.out.println("PORT REMOVE mLoginDialog");
                fragTransaction.remove(mLoginDialog);
                fragTransaction.commit();

                fragTransaction = getFragmentManager().beginTransaction();
                fragTransaction.add(R.id.warehouse_menuFrame, mLoginDialog);
                fragTransaction.commit();
                System.out.println("PORT END COMMIT ADD mLoginDialog");
            } else {
                FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
                System.out.println("LANDSCAPE REMOVE mLoginDialog");
                fragTransaction.remove(mLoginDialog);
                System.out.println("COMMIT REMOVE mLoginDialog");
                fragTransaction.commit();
                System.out.println("SHOW mLoginDialog");
                mLoginDialog.show(getFragmentManager(), "LoginDialog");
            }
        }
    }

    @Override
    public void showError(String errorText) {

    }

    @Override
    public void onServerChosen(String serverIp) {
        mLoginDialog.onServerChosen(serverIp);
    }

    @Override
    public void onFragmentInteraction() {

    }

    public boolean isPortOrientation() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return displayMetrics.widthPixels < displayMetrics.heightPixels;
    }
}
