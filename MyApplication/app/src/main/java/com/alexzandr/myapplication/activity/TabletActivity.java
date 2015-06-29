package com.alexzandr.myapplication.activity;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.application.Singleton;
import com.alexzandr.myapplication.fragment.dialog.ErrorShowDialog;
import com.alexzandr.myapplication.fragment.dialog.LoginDialog;
import com.alexzandr.myapplication.fragment.WarehouseFragment;
import com.alexzandr.myapplication.fragment.dialog.SetHeightDialog;

public abstract class TabletActivity extends AppCompatActivity implements
        ErrorShowDialog.OnShowErrors,
        LoginDialog.LoginDialogInteractionListener,
        WarehouseFragment.OnFragmentInteractionListener {

    protected SetHeightDialog mDialogSetHeight;
    protected Menu mSettingMenu;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
                Intent intent = new Intent(this, SettingsActivity.class);
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
    public void onLogIn() {}

    public boolean isPortOrientation() {
        return Singleton.isPortOrientation();
    }
}
