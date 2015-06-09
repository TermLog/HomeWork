package com.alexzandr.myapplication.fragment.tablet;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;

import com.alexzandr.myapplication.fragment.dialog.SetHeightDialog;

public class WarehouseFragment extends Fragment implements OnClickListener,
        SetHeightDialog.OnAdapterChangedListener {

    protected OnFragmentInteractionListener mListener;
    protected Activity mActivity;

    public WarehouseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
            mActivity = activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        setRetainInstance(true);
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
        public void onFragmentInteraction(int buttonId);
        public void logOut();
    }

    public boolean isPortOrientation() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return displayMetrics.widthPixels < displayMetrics.heightPixels;
    }

    public String getTitle() {
        return "";
    }

}
