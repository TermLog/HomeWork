package com.alexzandr.myapplication.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.application.Singleton;
import com.alexzandr.myapplication.fragment.dialog.EnterIpDialog.EnterIpDialogInteractionListener;
import com.alexzandr.myapplication.fragment.dialog.LoginDialog;
import com.alexzandr.myapplication.fragment.dialog.SetHeightDialog;
import com.alexzandr.myapplication.fragment.dialog.SetHeightDialog.OnAdapterChangedListener;
import com.alexzandr.myapplication.fragment.BlankFragment;
import com.alexzandr.myapplication.fragment.LockUnlockFragment;
import com.alexzandr.myapplication.fragment.MainMenuFragment;
import com.alexzandr.myapplication.fragment.WarehouseFragment;

public class WarehouseActivity extends TabletActivity implements
        EnterIpDialogInteractionListener, OnAdapterChangedListener {

    private LoginDialog mLoginDialog;
    private boolean mIsLogged;
    private int mSelectedMainMenuButtonId;
    private Menu mSettingMenu;

    private static final String KEY_LOGIN_DIALOG = "LoginDialog";
    private static final String KEY_IS_LOGGED = "isLogged";
    private static final String KEY_SELECTED_MAIN_MENU_BUTTON_ID = "buttonId";
    private static final int DESELECT_ALL_MAIN_MENU_BUTTON = -1;
    private static final boolean HIDE_SETTING_MENU = false;
    private static final boolean SHOW_SETTING_MENU = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse);

        mDialogSetHeight = new SetHeightDialog();

        if (savedInstanceState == null) {
            mLoginDialog = new LoginDialog();
            showLoginForm(mLoginDialog);
        } else {
            mIsLogged = savedInstanceState.getBoolean(KEY_IS_LOGGED, false);
            mSelectedMainMenuButtonId = savedInstanceState.getInt(KEY_SELECTED_MAIN_MENU_BUTTON_ID);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        mSettingMenu = menu;

        if (!mIsLogged){
            mSettingMenu.setGroupVisible(R.id.menu_setting_group, HIDE_SETTING_MENU);
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mIsLogged) {
            if (!isPortOrientation()) {
                WarehouseFragment fragment = Singleton.getSavedFragment();
                if (fragment != null) {
                    if (fragment instanceof LockUnlockFragment) {
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.warehouse_detailFrame, new LockUnlockFragment())
                                .commit();
                    } else {
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.warehouse_detailFrame, fragment)
                                .commit();
                    }
                    onFragmentInteraction(mSelectedMainMenuButtonId);
                    Singleton.clearSavedFragment();
                } else if (isBlankDetailFrame()) {
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.warehouse_detailFrame, new BlankFragment())
                            .commit();

                    onFragmentInteraction(DESELECT_ALL_MAIN_MENU_BUTTON);
                }
            }
        } else if (mLoginDialog == null) {
            showLoginForm();
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
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_LOGGED, mIsLogged);
        outState.putInt(KEY_SELECTED_MAIN_MENU_BUTTON_ID, mSelectedMainMenuButtonId);
    }

    @Override
    public void onLogIn() {

        getFragmentManager().beginTransaction()
                .replace(R.id.warehouse_menuFrame, new MainMenuFragment())
                .commit();

        getFragmentManager().beginTransaction()
                .replace(R.id.warehouse_detailFrame, new BlankFragment())
                .commit();

        mSettingMenu.setGroupVisible(R.id.menu_setting_group, SHOW_SETTING_MENU);
        mIsLogged = true;
        mLoginDialog = null;
    }


    public void showLoginForm() {
        if (isPortOrientation()){
            mLoginDialog = (LoginDialog) getFragmentManager().findFragmentByTag(KEY_LOGIN_DIALOG);
        } else {
            mLoginDialog = (LoginDialog) getFragmentManager().findFragmentById(R.id.warehouse_menuFrame);
        }
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().remove(mLoginDialog).commit();
        manager.executePendingTransactions();
        showLoginForm(mLoginDialog);
    }

    public void showLoginForm(LoginDialog dialog) {

        FragmentTransaction fragTransaction;

        if (!mIsLogged) {
            if (isPortOrientation()) {
                fragTransaction = getFragmentManager().beginTransaction();
                fragTransaction.add(R.id.warehouse_menuFrame, dialog, KEY_LOGIN_DIALOG);
                fragTransaction.commit();
            } else {
                dialog.show(getFragmentManager(), KEY_LOGIN_DIALOG);
                if (mIsErrorDialogShow){
                    mErrorShowDialog.show(getFragmentManager(), TAG_FOR_ERROR_DIALOG);
                }
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
        mSettingMenu.setGroupVisible(R.id.menu_setting_group, HIDE_SETTING_MENU);
        mLoginDialog = new LoginDialog();
        showLoginForm(mLoginDialog);
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
        MainMenuFragment fragment = (MainMenuFragment)
                getFragmentManager().findFragmentById(R.id.warehouse_menuFrame);

        if (buttonId == DESELECT_ALL_MAIN_MENU_BUTTON) {

            fragment.deselectAllButton(mSelectedMainMenuButtonId, true);

        } else if (!isPortOrientation()) {

            fragment.selectButton(mSelectedMainMenuButtonId, buttonId);
        }

        mSelectedMainMenuButtonId = buttonId;
    }
}
