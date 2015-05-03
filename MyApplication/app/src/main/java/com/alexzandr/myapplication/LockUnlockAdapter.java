package com.alexzandr.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alexzandr.myapplication.handler.AdapterItemHandler;

import java.util.ArrayList;

/**
 * Created by AlexZandR on 19.04.2015.
 */
public class LockUnlockAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<AdapterItemHandler> mHandlers;
    private String mKeyHeadlineHeight;
    private String mKeySectionHeight;
    private SharedPreferences mSettings;
    private Context mContext;

    public LockUnlockAdapter(Context context, ArrayList<AdapterItemHandler> handlers) {
        mContext = context;
        mHandlers = handlers;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        String mPreferenceName = mContext.getResources().getString(R.string.preference_name);
        mKeyHeadlineHeight = mContext.getResources().getString(R.string.preference_headLine_height);
        mKeySectionHeight = mContext.getResources().getString(R.string.preference_section_height);
        mSettings = mContext.getSharedPreferences(mPreferenceName, Context.MODE_PRIVATE);

    }

    @Override
    public int getCount() {
        return mHandlers.size();
    }

    @Override
    public Object getItem(int position) {
        return mHandlers.get(position);
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

        AdapterItemHandler handler = mHandlers.get(position);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText(handler.getTextForView());
        textView.setBackgroundColor(handler.getBackgroundColor());
        textView.setTextColor(handler.getTextColor());
        int height;

        if (handler.getType() == AdapterItemHandler.LEVEL_BUTTON){
            height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    mSettings.getInt(mKeyHeadlineHeight, Singleton.getDefaultHeadlineHeightDp()),
                    mContext.getResources().getDisplayMetrics());
            textView.setHeight(height);
        } else {
            height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    mSettings.getInt(mKeySectionHeight, Singleton.getDefaultSectionHeightDp()),
                    mContext.getResources().getDisplayMetrics());
        }

        textView.setHeight(height);

        return view;
    }

    public AdapterItemHandler getHandler(int position) {
        return ((AdapterItemHandler) getItem(position));
    }

}
