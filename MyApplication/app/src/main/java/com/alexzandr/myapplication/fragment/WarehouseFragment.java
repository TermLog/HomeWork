package com.alexzandr.myapplication.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;

import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.activity.TabletActivity;
import com.alexzandr.myapplication.application.Singleton;
import com.alexzandr.myapplication.fragment.dialog.SetHeightDialog;
import com.alexzandr.myapplication.handler.AdapterItemHandler;
import com.alexzandr.myapplication.service.SqlQueryIntentService;
import com.alexzandr.myapplication.sql.DataBaseTask;

import java.util.HashMap;

public class WarehouseFragment extends Fragment implements OnClickListener,
        SetHeightDialog.OnAdapterChangedListener {

    protected OnFragmentInteractionListener mListener;
    protected TabletActivity mActivity;
    protected ProgressDialog mProgressDialog;
    protected BroadcastReceiver mReceiverError;
    protected BroadcastReceiver mReceiverSuccess;

    public WarehouseFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        try {
            mActivity = (TabletActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must extends TabletActivity");
        }

        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setTitle(R.string.progressBar_title);
        mProgressDialog.setMessage(getText(R.string.progressBar_massage));
        mProgressDialog.setCancelable(false);
        setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReceiverError = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mProgressDialog.dismiss();
                showError(intent.getStringExtra(SqlQueryIntentService.EXTRA_ERROR_MESSAGE));
            }
        };
    }

    @Override
    public void onStart() {
        IntentFilter filterSuccess = new IntentFilter(SqlQueryIntentService.QUERY_SUCCESSFUL);
        IntentFilter filterError = new IntentFilter(SqlQueryIntentService.QUERY_ERROR);
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mReceiverSuccess, filterSuccess);
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mReceiverError, filterError);
        super.onStart();
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mReceiverSuccess);
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mReceiverError);
        mProgressDialog.dismiss();
        super.onStop();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mActivity = null;
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onAdapterChanged() {
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(int buttonId);
        void logOut();
    }

    public boolean isPortOrientation() {
        return Singleton.isPortOrientation();
    }

    public void showError(String errorText) {
        mActivity.showError(errorText);
    }

    protected class WarehouseTask extends DataBaseTask {

        @Override
        protected void onPreExecute(){
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(HashMap<String, Integer> result) {

            mProgressDialog.dismiss();

            if (exception != null){
                showError(exception.getMessage());
            } else if (result == null) {
                showError(getString(R.string.no_data_in_query));
            } else  {
                processResult(result);
            }
        }

        void processResult(HashMap<String, Integer> result) {
        }
    }
}
