package com.alexzandr.myapplication.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.activity.DetailActivity;

/**
 * Created by anekrasov on 02.06.15.
 */
public class MainMenuFragment extends WarehouseFragment {

    private View mView;
    private int mSelectFragmentId;

    private static final int UPDATE_FRAGMENT_ID = 1;
    private static final int DELETE_FRAGMENT_ID = 2;
    private static final int LOCK_UNLOCK_FRAGMENT_ID = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main_menu, container, false);
        mView.findViewById(R.id.mainMenu_buttonUpdate).setOnClickListener(this);
        mView.findViewById(R.id.mainMenu_buttonDelete).setOnClickListener(this);
        mView.findViewById(R.id.mainMenu_buttonLockUnlock).setOnClickListener(this);
        mView.findViewById(R.id.mainMenu_buttonExit).setOnClickListener(this);

        return mView;
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

        mListener.onFragmentInteraction(view.getId());
    }

    public void showUpdate() {
        if (isPortOrientation()) {

            Intent intent = new Intent((Activity) mListener, DetailActivity.class);
            intent.putExtra(getString(R.string.transfer_fragment_key), WorkWithDocumentFragment.UPDATE_ACTIVITY);
            startActivity(intent);

        } else if (mSelectFragmentId != UPDATE_FRAGMENT_ID){

            WorkWithDocumentFragment fragment = WorkWithDocumentFragment.newInstance(WorkWithDocumentFragment.UPDATE_ACTIVITY);
            mActivity.getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.warehouse_detailFrame, fragment)
                    .commit();

            mSelectFragmentId = UPDATE_FRAGMENT_ID;
        }
    }

    public void showDelete() {
        if (isPortOrientation()) {

            Intent intent = new Intent((Activity) mListener, DetailActivity.class);
            intent.putExtra(getString(R.string.transfer_fragment_key), WorkWithDocumentFragment.DELETE_ACTIVITY);
            startActivity(intent);

        } else if (mSelectFragmentId != DELETE_FRAGMENT_ID){

            WorkWithDocumentFragment fragment =  WorkWithDocumentFragment.newInstance(WorkWithDocumentFragment.DELETE_ACTIVITY);
            mActivity.getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.warehouse_detailFrame, fragment)
                    .commit();

            mSelectFragmentId = DELETE_FRAGMENT_ID;
        }
    }

    public void showLockUnlock() {
        if (isPortOrientation()) {

            Intent intent = new Intent((Activity) mListener, DetailActivity.class);
            startActivity(intent);

        } else if (mSelectFragmentId != LOCK_UNLOCK_FRAGMENT_ID){

            mActivity.getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.warehouse_detailFrame, new LockUnlockFragment())
                    .commit();

            mSelectFragmentId = LOCK_UNLOCK_FRAGMENT_ID;
        }
    }

    public void selectButton(int oldId, int newId){
        int selectedColor = getResources().getColor(R.color.background_blue);
        int unSelectedColor = getResources().getColor(R.color.background_main_menu_landscape_deepBlue);

        try {
            if (mView.findViewById(oldId) != null) {
                mView.findViewById(oldId).setBackgroundColor(unSelectedColor);
            }
            mView.findViewById(newId).setBackgroundColor(selectedColor);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void exitClick() {
        mListener.logOut();
    }
}
