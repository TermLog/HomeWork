package com.alexzandr.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class MainMenuActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void exitClick(View view) {
        this.finish();
    }

    public void updateClick(View view) {
        showUpdate();
    }

    public void deleteClick(View view) {
        showDelete();
    }

    public void lockUnlockClick(View view) {
        showLockUnlock();
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
        Intent intent = new Intent(MainMenuActivity.this, LockUnlockActivity.class);
        startActivity(intent);
    }
}
