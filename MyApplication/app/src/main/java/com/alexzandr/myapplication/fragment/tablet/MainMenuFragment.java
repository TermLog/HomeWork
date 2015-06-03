package com.alexzandr.myapplication.fragment.tablet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.activity.LockUnlockActivityTest;
import com.alexzandr.myapplication.activity.LoginActivity;
import com.alexzandr.myapplication.activity.WorkWithDocumentActivity;

/**
 * Created by anekrasov on 02.06.15.
 */
public class MainMenuFragment extends WarehouseFragment implements View.OnClickListener{

    public MainMenuFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_menu, container, false);
        Button button = (Button) v.findViewById(R.id.mainMenu_buttonUpdate);
        button.setOnClickListener(this);
        button = (Button) v.findViewById(R.id.mainMenu_buttonDelete);
        button.setOnClickListener(this);
        button = (Button) v.findViewById(R.id.mainMenu_buttonDelete);
        button.setOnClickListener(this);
        button = (Button) v.findViewById(R.id.mainMenu_buttonLockUnlock);
        button.setOnClickListener(this);
        button = (Button) v.findViewById(R.id.mainMenu_buttonExit);
        button.setOnClickListener(this);

        return v;
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
        Intent intent = new Intent((Activity)mListener, WorkWithDocumentActivity.class);
        intent.putExtra(WorkWithDocumentActivity.MAP_KEY, WorkWithDocumentActivity.UPDATE_ACTIVITY);
        startActivity(intent);
    }

    public void showDelete() {
        Intent intent = new Intent((Activity)mListener, WorkWithDocumentActivity.class);
        intent.putExtra(WorkWithDocumentActivity.MAP_KEY, WorkWithDocumentActivity.DELETE_ACTIVITY);
        startActivity(intent);
    }

    public void showLockUnlock() {
//        Intent intent = new Intent(MainMenuActivity.this, LockUnlockActivity.class);
        Intent intent = new Intent((Activity)mListener, LockUnlockActivityTest.class);
        startActivity(intent);
    }

    public void exitClick() {
        mListener.logOut();
    }
}
