package com.alexzandr.myapplication.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.Singleton;
import com.alexzandr.myapplication.fragment.dialog.EnterIpDialog.EnterIpDialogInteractionListener;
import com.alexzandr.myapplication.fragment.dialog.LoginDialog;
import com.alexzandr.myapplication.fragment.dialog.SetHeightDialog;
import com.alexzandr.myapplication.fragment.dialog.SetHeightDialog.OnAdapterChangedListener;
import com.alexzandr.myapplication.fragment.tablet.BlankFragment;
import com.alexzandr.myapplication.fragment.tablet.LockUnlockFragment;
import com.alexzandr.myapplication.fragment.tablet.MainMenuFragment;
import com.alexzandr.myapplication.fragment.tablet.WarehouseFragment;

public class WarehouseActivity extends TabletActivity implements
        EnterIpDialogInteractionListener, OnAdapterChangedListener {

    private LoginDialog mLoginDialog;
    private SetHeightDialog mDialogSetHeight;
    private boolean mIsLogged;
    private int mSelectedMainMenuButtonId;

    private static final String KEY_LOGIN_DIALOG = "loginDialog";
    private static final String KEY_IS_LOGGED = "isLogged";
    private static final String KEY_SELECTED_MAIN_MENU_BUTTON_ID = "buttonId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse);

        mDialogSetHeight = new SetHeightDialog();
    }

    @Override
    public void onResume() {
        super.onResume();
        showLoginForm();

        if (Singleton.getSavedFragment() != null && !isPortOrientation()) {
            if (Singleton.getSavedFragment() instanceof LockUnlockFragment) {
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.warehouse_detailFrame, new LockUnlockFragment())
                        .commit();
            } else {
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.warehouse_detailFrame, Singleton.getSavedFragment())
                        .commit();
            }
            onFragmentInteraction(mSelectedMainMenuButtonId);
            Singleton.clearSavedFragment();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (isPortOrientation() && !isBlankDetailFrame()){
            WarehouseFragment fragment = (WarehouseFragment) getFragmentManager().findFragmentById(R.id.warehouse_detailFrame);
            getFragmentManager().beginTransaction().remove(fragment).commit();

            Intent intent = new Intent(WarehouseActivity.this, DetailActivity.class);
            if (!(fragment instanceof LockUnlockFragment)) {
                Singleton.saveFragment(fragment);
                intent.putExtra(getString(R.string.transfer_fragment_key), DetailActivity.GET_FRAGMENT_FROM_SINGLETON);
            }
            startActivity(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        try {
            if (mLoginDialog != null) {
                mLoginDialog.dismiss();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        super.onSaveInstanceState(outState);

        if (mLoginDialog != null) {
            outState.putParcelable(KEY_LOGIN_DIALOG, mLoginDialog);
        }

        outState.putBoolean(KEY_IS_LOGGED, mIsLogged);
        outState.putInt(KEY_SELECTED_MAIN_MENU_BUTTON_ID, mSelectedMainMenuButtonId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLoginDialog = savedInstanceState.getParcelable(KEY_LOGIN_DIALOG);
        mIsLogged = savedInstanceState.getBoolean(KEY_IS_LOGGED, false);
        mSelectedMainMenuButtonId = savedInstanceState.getInt(KEY_SELECTED_MAIN_MENU_BUTTON_ID);
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

        getFragmentManager().beginTransaction()
                .replace(R.id.warehouse_menuFrame, new MainMenuFragment())
                .addToBackStack(null)
                .commit();

        getFragmentManager().beginTransaction()
                .replace(R.id.warehouse_detailFrame, new BlankFragment())
                .commit();

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
            fragTransaction.remove(getFragmentManager().findFragmentById(R.id.warehouse_detailFrame));
        }
        fragTransaction.remove(getFragmentManager().findFragmentById(R.id.warehouse_menuFrame));
        fragTransaction.commit();

        showLoginForm();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        mIsLogged = false;
    }

    @Override
    public void onAdapterChanged() {
        OnAdapterChangedListener listener = (OnAdapterChangedListener) getFragmentManager().findFragmentById(R.id.warehouse_detailFrame);
        listener.onAdapterChanged();
    }

    public boolean isBlankDetailFrame() {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.warehouse_detailFrame);
        return fragment instanceof BlankFragment || fragment == null;
    }

    @Override
    public void onFragmentInteraction(int buttonId) {

        if (!isPortOrientation()) {
            ((MainMenuFragment) getFragmentManager().findFragmentById(R.id.warehouse_menuFrame))
                    .selectButton(mSelectedMainMenuButtonId, buttonId);
        }

        mSelectedMainMenuButtonId = buttonId;

    }
}
