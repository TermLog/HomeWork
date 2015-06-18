package com.alexzandr.myapplication.adapter_new;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.alexzandr.myapplication.R;

/**
 * Created by AlexZandR on 04.05.2015.
 */
public class ColorListAdapter extends BaseAdapter {
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private LayoutInflater mInflater;

    public ColorListAdapter(Context context, CharSequence[] entries, CharSequence[] values){
        mEntries = entries;
        mEntryValues = values;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mEntries.length;
    }

    @Override
    public Object getItem(int position) {
        return mEntries[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CheckedTextView view = (CheckedTextView) convertView;
        if (view == null) {
            view = (CheckedTextView) mInflater.inflate(R.layout.color_list_item_single_choice, parent, false);
        }
        view.setText(mEntries[position]);
        view.setBackgroundColor(Color.parseColor(mEntryValues[position].toString()));

        return view;
    }
}
