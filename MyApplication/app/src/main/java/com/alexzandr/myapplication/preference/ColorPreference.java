package com.alexzandr.myapplication.preference;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.preference.ListPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alexzandr.myapplication.adapter.ColorListAdapter;
import com.alexzandr.myapplication.R;

/**
 * Created by AlexZandR on 03.05.2015.
 */
public class ColorPreference extends ListPreference {
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private int mClickedDialogEntryIndex;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ColorPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ColorPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorPreference(Context context) {
        super(context);
        init();
    }

    public void init() {
        mEntries = getEntries();
        mEntryValues = getEntryValues();
        setLayoutResource(R.layout.preference_color);
    }


    @Override
    protected void onBindView(View view) {
        final TextView titleView = (TextView) view.findViewById(R.id.preference_color_title);
        if (titleView != null) {
            final CharSequence title = getTitle();
            if (!TextUtils.isEmpty(title)) {
                titleView.setText(title);
                titleView.setVisibility(View.VISIBLE);
            } else {
                titleView.setVisibility(View.GONE);
            }
        }

        final TextView summaryView = (TextView) view.findViewById(R.id.summary);
        if (summaryView != null) {
            setSummary(getEntry() + getContext().getResources().getString(R.string.color_preference_summary_text));
            final CharSequence summary = getSummary();
            if (!TextUtils.isEmpty(summary)) {
                summaryView.setText(summary);
                summaryView.setVisibility(View.VISIBLE);
            } else {
                summaryView.setVisibility(View.GONE);
            }
        }

        FrameLayout fragment = (FrameLayout) view.findViewById(R.id.color_example);
        if (fragment != null) {
            fragment.setVisibility(View.VISIBLE);
            if (getValue() == null){
                fragment.setBackgroundColor(Color.RED);
            } else {
                fragment.setBackgroundColor(Color.parseColor(getValue()));
            }
        }
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);

        if (mEntries == null || mEntryValues == null) {
            throw new IllegalStateException(
                    "ListPreference requires an entries array and an entryValues array.");
        }

        ColorListAdapter adapter = new ColorListAdapter(getContext(), mEntries, mEntryValues);

        mClickedDialogEntryIndex = findIndexOfValue(getValue());
        builder.setSingleChoiceItems(adapter, mClickedDialogEntryIndex,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mClickedDialogEntryIndex = which;

                        ViewGroup vg = (ViewGroup) ((Dialog) dialog).getWindow().getDecorView();
                        View v = vg.getChildAt(0);
                        int color = Color.parseColor(getEntryValues()[which].toString());
                        ValueAnimator colorAnim = ObjectAnimator.ofInt(v, "backgroundColor", Color.WHITE, color);
                        colorAnim.setDuration(500);
                        colorAnim.setEvaluator(new ArgbEvaluator());
                        colorAnim.setRepeatCount(1);
                        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
                        colorAnim.start();
                    }
                });
        builder.setPositiveButton(getContext().getResources().getString(R.string.color_preference_positiveButton_text), this);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult && mClickedDialogEntryIndex >= 0 && mEntryValues != null) {
            String value = mEntryValues[mClickedDialogEntryIndex].toString();
            if (callChangeListener(value)) {
                setValue(value);
            }
        }
    }
}
