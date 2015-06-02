package com.alexzandr.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.fragment.dialog.SetHeightDialog;

public class MainMenuActivity extends ActionBarActivity {

    private SetHeightDialog mDialogSetHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
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
                Intent intent = new Intent(MainMenuActivity.this, SettingsActivity.class);
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

    public void onClick (View view) {
        switch (view.getId()) {
            case R.id.mainMenu_buttonUpdate:
                showUpdate();
                break;
            case R.id.mainMenu_buttonDelete:
                showDelete();
                break;
            case R.id.mainMenu_buttonLockUnlock:
                showLockUnlock();
                break;
            case R.id.mainMenu_buttonExit:
                exitClick();
                break;
            default:
                break;
        }
    }

    public void showUpdate() {
        Intent intent = new Intent(MainMenuActivity.this, WorkWithDocumentActivity.class);
        intent.putExtra(WorkWithDocumentActivity.MAP_KEY, WorkWithDocumentActivity.UPDATE_ACTIVITY);
        startActivity(intent);
    }

    public void showDelete() {
        Intent intent = new Intent(MainMenuActivity.this, WorkWithDocumentActivity.class);
        intent.putExtra(WorkWithDocumentActivity.MAP_KEY, WorkWithDocumentActivity.DELETE_ACTIVITY);
        startActivity(intent);
    }

    public void showLockUnlock() {
//        Intent intent = new Intent(MainMenuActivity.this, LockUnlockActivity.class);
        Intent intent = new Intent(MainMenuActivity.this, LockUnlockActivityTest.class);
        startActivity(intent);
    }

    public void exitClick() {
        Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.finish();
    }
}
