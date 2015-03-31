package com.alexzandr.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class MainMenuActivity extends ActionBarActivity {

    private Animation mButtonAnimation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        mButtonAnimation = AnimationUtils.loadAnimation(this, R.anim.button_scale_animation);
    }

    public void exitClick(View view) {
        Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.finish();
    }

    public void updateClick(View view) {

        view.startAnimation(mButtonAnimation);
        showUpdate();
    }

    public void deleteClick(View view) {

        view.startAnimation(mButtonAnimation);
        showDelete();
    }

    public void lockUnlockClick(View view) {

        view.startAnimation(mButtonAnimation);
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
