package com.alexzandr.myapplication.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.application.Singleton;
import com.alexzandr.myapplication.fragment.dialog.SetHeightDialog;
import com.alexzandr.myapplication.fragment.dialog.SetHeightDialog.OnAdapterChangedListener;
import com.alexzandr.myapplication.fragment.BlankFragment;
import com.alexzandr.myapplication.fragment.LockUnlockFragment;
import com.alexzandr.myapplication.fragment.WarehouseFragment;
import com.alexzandr.myapplication.fragment.WorkWithDocumentFragment;

public class DetailActivity extends TabletActivity implements OnAdapterChangedListener {

//    private SetHeightDialog mDialogSetHeight;
    private WarehouseFragment mFragment;
    private boolean mIsStarted;
    private boolean mIsTablet;

    public static final int DEFAULT_FRAGMENT_TYPE = -1;
    public static final int GET_FRAGMENT_FROM_SINGLETON = -2;
    public static final String KEY_FOR_STARTED = "start";

    private static final boolean DELETE_FRAGMENT_FROM_SINGLETON = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        mIsTablet = Singleton.isTablet();
        if (savedInstanceState != null){
            mIsStarted = savedInstanceState.getBoolean(KEY_FOR_STARTED, false);
        }

        if (!isPortOrientation() && mIsTablet) {
            if (mIsStarted) {
                mFragment = (WarehouseFragment) getFragmentManager().findFragmentById(R.id.detailFrame_fragmentPlace);
                Singleton.saveFragment(mFragment);
                getFragmentManager()
                        .beginTransaction()
                        .remove(mFragment)
                        .commit();
            }
            finish();
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        mDialogSetHeight = new SetHeightDialog();

        if (mIsTablet) {
            if (!mIsStarted) {
                addFragment(DELETE_FRAGMENT_FROM_SINGLETON);
            }
        } else if (isPortOrientation()) {
            addFragment();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_FOR_STARTED, mIsStarted);
    }

    @Override
    public void onAdapterChanged() {
        mFragment.onAdapterChanged();
    }

    private void addFragment() {
        addFragment(false);
    }

    private void addFragment(boolean deleteFromSingleton) {
        int type = getIntent().getIntExtra(getString(R.string.transfer_fragment_key), DEFAULT_FRAGMENT_TYPE);
        switch (type) {
            case DEFAULT_FRAGMENT_TYPE:
                mFragment = new LockUnlockFragment();
                break;
            case GET_FRAGMENT_FROM_SINGLETON:
                mFragment = Singleton.getSavedFragment();

                if (mFragment == null) {
                    mFragment = new LockUnlockFragment();
                }
                break;
            default:
                mFragment = WorkWithDocumentFragment.newInstance(type);
                break;
        }

        FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
        if (mFragment != null) {
            fragTransaction.add(R.id.detailFrame_fragmentPlace, mFragment);
        } else {
            fragTransaction.add(R.id.detailFrame_fragmentPlace, new BlankFragment());
        }
        fragTransaction.commit();

        if (deleteFromSingleton) {
            Singleton.clearSavedFragment();
        }

        mIsStarted = true;
    }


}
