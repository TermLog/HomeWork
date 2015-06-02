package com.alexzandr.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.fragment.dialog.SetHeightDialog;
import com.alexzandr.myapplication.fragment.tablet.WarehouseFragment;

public class TabletActivity extends ActionBarActivity implements WarehouseFragment.OnFragmentInteractionListener {

    private SetHeightDialog mDialogSetHeight;
    private boolean mIsBegin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet);
        mDialogSetHeight = new SetHeightDialog();
    }

    @Override
    public void onResume() {
        super.onResume();
        setVisibleByOrientation();
        mIsBegin = false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setVisibleByOrientation(newConfig.orientation);
    }

    public void setVisibleByOrientation(){
        setVisibleByOrientation(getResources().getConfiguration().orientation);
    }

    public void setVisibleByOrientation(int orientation){
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

            findViewById(R.id.tablet_details).setVisibility(View.VISIBLE);
            findViewById(R.id.tablet_main_menu).setVisibility(View.VISIBLE);

        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {

            if (mIsBegin) {
                findViewById(R.id.tablet_details).setVisibility(View.GONE);
            } else {
                findViewById(R.id.tablet_main_menu).setVisibility(View.GONE);
            }

        }
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
                Intent intent = new Intent(TabletActivity.this, SettingsActivity.class);
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
    public void onFragmentInteraction() {

    }
}
