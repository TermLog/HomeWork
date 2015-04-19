package com.alexzandr.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by AlexZandR on 19.04.2015.
 */
public class LockUnlockAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<AdapterItemHandler> mButtons;

    public LockUnlockAdapter(Context context, ArrayList<AdapterItemHandler> buttons) {
        mButtons = buttons;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mButtons.size();
    }

    @Override
    public Object getItem(int position) {
        return mButtons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(R.layout.view_for_gridview, parent, false);
        }

        AdapterItemHandler handler = mButtons.get(position);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText(handler.getTextForButton());
        textView.setBackgroundColor(handler.getBackgroundColor());
        textView.setTextColor(handler.getTextColor());

        return view;
    }

    public AdapterItemHandler getHandler(int position) {
        return ((AdapterItemHandler) getItem(position));
    }

}
