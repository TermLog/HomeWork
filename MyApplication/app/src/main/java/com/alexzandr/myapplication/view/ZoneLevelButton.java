package com.alexzandr.myapplication.view;

import android.app.ActionBar;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;

import com.alexzandr.myapplication.R;

/**
 * Created by AlexZandR on 22.02.2015.
 */
public class ZoneLevelButton extends AnimatedButton {
    public final static int TYPE_ZONE = 1;
    public final static int TYPE_LEVEL = 2;
    private int mType;
    private int mValue;

    public ZoneLevelButton(Context context){
        super(context);
    }
    public ZoneLevelButton(Context context, AttributeSet attSet){
        super(context, attSet);
    }
    public ZoneLevelButton(Context context, AttributeSet attSet, int defStyleAttr){
        super(context, attSet, defStyleAttr);
    }

    public ZoneLevelButton(Context context, int type, int value){
        super(context);
        setType(type);
        setValue(value);
        setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        setOnClickListener((OnClickListener)context);
        setBackgroundResource(R.color.lockUnlock_button_zoneAndLevel);
        setTextColor(getResources().getColor(R.color.text_white));
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

        if (type == TYPE_LEVEL){
            setText("LvL " + value);
        }else{
            String resultNameN = "00" + value;
            setText("Zone P" + resultNameN.substring(resultNameN.length() - 2));
        }
    }

    public void setType(int type){
        mType = type;
    }

    public void setValue(int value){
        mValue = value;
    }

    public int getType(){
        return mType;
    }

    public int getValue(){
        return mValue;
    }
}
