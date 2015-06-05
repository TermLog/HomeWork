package com.alexzandr.myapplication.fragment.tablet;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.activity.DetailActivity;
import com.alexzandr.myapplication.activity.LockUnlockActivityTest;
import com.alexzandr.myapplication.activity.WorkWithDocumentActivity;

/**
 * Created by anekrasov on 02.06.15.
 */
public class MainMenuFragment extends WarehouseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);
        view.findViewById(R.id.mainMenu_buttonUpdate).setOnClickListener(this);
        view.findViewById(R.id.mainMenu_buttonDelete).setOnClickListener(this);
        view.findViewById(R.id.mainMenu_buttonLockUnlock).setOnClickListener(this);
        view.findViewById(R.id.mainMenu_buttonExit).setOnClickListener(this);

        return view;
    }

    @Override
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
        if (isPortOrientation()) {
            Intent intent = new Intent((Activity) mListener, DetailActivity.class);
            intent.putExtra(getString(R.string.transfer_fragment_key), WorkWithDocumentFragment.UPDATE_ACTIVITY);
            startActivity(intent);
        } else {
            WorkWithDocumentFragment fragment =  WorkWithDocumentFragment.newInstance(WorkWithDocumentFragment.UPDATE_ACTIVITY);
            FragmentTransaction fragTransaction = mActivity.getFragmentManager().beginTransaction();
            fragTransaction.replace(R.id.warehouse_detailFrame, fragment);
            fragTransaction.commit();
        }
    }

    public void showDelete() {
        if (isPortOrientation()) {
            Intent intent = new Intent((Activity) mListener, DetailActivity.class);
            intent.putExtra(getString(R.string.transfer_fragment_key), WorkWithDocumentFragment.DELETE_ACTIVITY);
            startActivity(intent);
        } else {
            WorkWithDocumentFragment fragment =  WorkWithDocumentFragment.newInstance(WorkWithDocumentFragment.DELETE_ACTIVITY);
            FragmentTransaction fragTransaction = mActivity.getFragmentManager().beginTransaction();
            fragTransaction.replace(R.id.warehouse_detailFrame, fragment);
            fragTransaction.commit();
        }
    }

    public void showLockUnlock() {
        if (isPortOrientation()) {
            Intent intent = new Intent((Activity) mListener, DetailActivity.class);
            startActivity(intent);
        } else {
            LockUnlockFragment fragment =  new LockUnlockFragment();
            FragmentTransaction fragTransaction = mActivity.getFragmentManager().beginTransaction();
            fragTransaction.replace(R.id.warehouse_detailFrame, fragment);
            fragTransaction.commit();
        }
    }

    public void exitClick() {
        mListener.logOut();
    }
}
