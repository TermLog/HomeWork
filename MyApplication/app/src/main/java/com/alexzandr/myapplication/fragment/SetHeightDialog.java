package com.alexzandr.myapplication.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.Singleton;

/**
 * Created by anekrasov on 29.04.15.
 */
public class SetHeightDialog extends DialogFragment implements
        SeekBar.OnSeekBarChangeListener,
        TextView.OnEditorActionListener,
        View.OnFocusChangeListener,
        View.OnClickListener {

    private int mType;
    private int mMinValue;
    private SeekBar mSeekBar;
    private EditText mEditText;
    private Button mSaveButton;
    private Button mCancelButton;
    private String mPreferenceKey;
    private SharedPreferences mSettings;
    private static final double PERCENT_OF_WIDTH = 0.8;

    public static final String KEY_FOR_TYPE = "type";
    public static final int DIALOG_TYPE_HEADLINE_HEIGHT = 1;
    public static final int DIALOG_TYPE_SECTION_HEIGHT = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int dpWidth = (int) (PERCENT_OF_WIDTH * displayMetrics.widthPixels);
        LayoutParams params = new LayoutParams(dpWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

        View view = inflater.inflate(R.layout.dialog_set_height, null);

        mSaveButton = (Button) view.findViewById(R.id.dialog_set_height_saveButton);
        mSaveButton.setOnClickListener(this);

        mCancelButton = (Button) view.findViewById(R.id.dialog_set_height_cancelButton);
        mCancelButton.setOnClickListener(this);

        mSeekBar = (SeekBar) view.findViewById(R.id.dialog_set_height_seekBar);
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setLayoutParams(params);
        mSeekBar.setMax(Singleton.getSeekBarMax());

        mEditText = (EditText) view.findViewById(R.id.dialog_set_height_editText);
        mEditText.setOnEditorActionListener(this);
        mEditText.setOnFocusChangeListener(this);
        mEditText.setText(Integer.toString(mSeekBar.getProgress()));

        mSettings = Singleton.getContext().getSharedPreferences(Singleton.getPreferencesName(), Context.MODE_PRIVATE);

        return view;
    }

    @Override
    public void onStart(){
        super.onStart();

        mType = getArguments().getInt(KEY_FOR_TYPE);
        mMinValue = Singleton.getSeekBarMin();

        if (mType == DIALOG_TYPE_HEADLINE_HEIGHT){
            getDialog().setTitle(R.string.dialog_set_height_headLine_title);
            mPreferenceKey = getString(R.string.preference_key_height_headLine);
        }
        if (mType == DIALOG_TYPE_SECTION_HEIGHT){
            getDialog().setTitle(R.string.dialog_set_height_section_title);
            mPreferenceKey = getString(R.string.preference_key_height_section);
        }

        setSeekBarProgress(mSettings.getInt(mPreferenceKey,
                mType == DIALOG_TYPE_HEADLINE_HEIGHT ? Singleton.getDefaultHeadlineHeightDp() : Singleton.getDefaultSectionHeightDp()));
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        setSeekBarProgress(v.getText());
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            setSeekBarProgress(((EditText) v).getText());
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            setSeekBarProgress(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    private void setSeekBarProgress(CharSequence progress){
        if (TextUtils.isEmpty(progress)) {
            setSeekBarProgress(mMinValue);
        } else {
            setSeekBarProgress(Integer.parseInt(progress.toString()));
        }
    }
    private void setSeekBarProgress(int progress){
        if (progress < mMinValue){
            progress = mMinValue;
        }
        if (progress > mSeekBar.getMax()){
            progress = mSeekBar.getMax();
        }

        mSeekBar.setProgress(progress);
        mEditText.setText(Integer.toString(progress));
        mEditText.setSelection(mEditText.length());
    }

    @Override
    public void onClick (View v){
        if (v.getId() == R.id.dialog_set_height_saveButton){
            setSeekBarProgress(mEditText.getText());
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putInt(mPreferenceKey, mSeekBar.getProgress());
            editor.apply();
        }
        dismiss();
    }
}
