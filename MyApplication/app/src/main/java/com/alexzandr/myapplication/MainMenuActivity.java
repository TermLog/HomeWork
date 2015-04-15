package com.alexzandr.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class MainMenuActivity extends ActionBarActivity {

    private final Animation mScaleAnimationForButton = Singleton.getAnimation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void onClick (View view) {
        view.startAnimation(mScaleAnimationForButton);
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
        Intent intent = new Intent(MainMenuActivity.this, LockUnlockActivity.class);
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
