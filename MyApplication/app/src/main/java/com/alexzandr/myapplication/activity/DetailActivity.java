package com.alexzandr.myapplication.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.Singleton;
import com.alexzandr.myapplication.fragment.dialog.SetHeightDialog;
import com.alexzandr.myapplication.fragment.dialog.SetHeightDialog.OnAdapterChangedListener;
import com.alexzandr.myapplication.fragment.tablet.BlankFragment;
import com.alexzandr.myapplication.fragment.tablet.LockUnlockFragment;
import com.alexzandr.myapplication.fragment.tablet.WarehouseFragment;
import com.alexzandr.myapplication.fragment.tablet.WorkWithDocumentFragment;

public class DetailActivity extends TabletActivity implements OnAdapterChangedListener {

    private SetHeightDialog mDialogSetHeight;
    private WarehouseFragment mFragment;
    private boolean mIsStarted;
    private boolean mIsFinished;
    public static final int DEFAULT_FRAGMENT_TYPE = -1;
    public static final int GET_FRAGMENT_FROM_SINGLETON = -2;
    public static final String KEY_FOR_STARTED = "start";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState != null){
            mIsStarted = savedInstanceState.getBoolean(KEY_FOR_STARTED, false);
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        if (!isPortOrientation()) {
            mIsFinished = true;
            finish();
            return;
        }

        if (!mIsStarted) {
            mDialogSetHeight = new SetHeightDialog();
            int fragType = getIntent().getIntExtra(getString(R.string.transfer_fragment_key), DEFAULT_FRAGMENT_TYPE);
            switch (fragType) {
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
                    mFragment = WorkWithDocumentFragment.newInstance(fragType);
                    break;
            }

            FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
            if (mFragment != null) {
                fragTransaction.add(R.id.detailFrame_fragmentPlace, mFragment);
            } else {
                fragTransaction.add(R.id.detailFrame_fragmentPlace, new BlankFragment());
            }
            fragTransaction.commit();
            mIsStarted = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_FOR_STARTED, mIsStarted);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mIsFinished) {
            mFragment = (WarehouseFragment) getFragmentManager().findFragmentById(R.id.detailFrame_fragmentPlace);
            Singleton.saveFragment(mFragment);
            FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
            fragTransaction.remove(mFragment);
            fragTransaction.commit();
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
                Intent intent = new Intent(DetailActivity.this, SettingsActivity.class);
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
    public void onAdapterChanged() {
        mFragment.onAdapterChanged();
    }
}
