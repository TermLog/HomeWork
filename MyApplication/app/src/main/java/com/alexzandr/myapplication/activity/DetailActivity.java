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
    private boolean isStarted;
    public static final int DEFAULT_FRAGMENT_TYPE = -1;
    public static final int GET_FRAGMENT_FROM_SINGLETON = -2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }

    @Override
    public void onResume(){
        super.onResume();
        System.out.println("IS_STARTED (onResume) = " + isStarted);
        if (!isPortOrientation()) {
            WarehouseFragment fragment = (WarehouseFragment) getFragmentManager().findFragmentById(R.id.detailFrame_fragmentPlace);
            FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
            fragTransaction.remove(fragment);
            fragTransaction.commit();

            if (fragment == null)
                System.out.println("FRAGMENT IS NULL");
//            Singleton.saveFragment(mFragment);
//            getFragmentManager().beginTransaction().remove(mFragment).commit();
            fragTransaction = getFragmentManager().beginTransaction();
            fragTransaction.add(R.id.detailFrame_fragmentPlace, fragment);
            fragTransaction.commit();
//            getFragmentManager().beginTransaction().add(R.id.detailFrame_fragmentPlace, fragment).commit();
//            finish();
//            return;
        }

        if (!isStarted) {
            mDialogSetHeight = new SetHeightDialog();
            int fragType = getIntent().getIntExtra(getString(R.string.transfer_fragment_key), DEFAULT_FRAGMENT_TYPE);
            switch (fragType) {
                case DEFAULT_FRAGMENT_TYPE:
                    mFragment = new LockUnlockFragment();
                    System.out.println("DETAIL ACTIVITY DEFAULT_FRAGMENT_TYPE");
                    break;
                case GET_FRAGMENT_FROM_SINGLETON:
                    mFragment = Singleton.getSavedFragment();
                    if (mFragment == null) {
                        mFragment = new LockUnlockFragment();
                        System.out.println("DETAIL ACTIVITY SINGLETON SAVED FRAGMENT IS NULL");
                    }
                    System.out.println("DETAIL ACTIVITY GET_FRAGMENT_FROM_SINGLETON");
                    break;
                default:
                    mFragment = WorkWithDocumentFragment.newInstance(fragType);
                    System.out.println("DETAIL ACTIVITY DEFAULT");
                    break;
            }

            FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
            if (mFragment != null) {
                fragTransaction.add(R.id.detailFrame_fragmentPlace, mFragment);
            } else {
                fragTransaction.add(R.id.detailFrame_fragmentPlace, new BlankFragment());
            }
            fragTransaction.commit();
            isStarted = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("start", isStarted);
        System.out.println("IS_STARTED (onSaveInstanceState) = " + isStarted);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isStarted = savedInstanceState.getBoolean("start", false);
        System.out.println("IS_STARTED (onRestoreInstanceState) = " + isStarted);
    }

    @Override
    public void onStop() {
        super.onStop();

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
