package com.alexzandr.myapplication.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;

import com.alexzandr.myapplication.R;

/**
 * Created by AlexZandR on 22.02.2015.
 */
public class SectionButton extends AnimatedButton {
    private int mZone;
    private int mLevel;
    private int mBlockedType;
    private final static int UNBLOCKED = 1;
    private final static int BLOCKED = 2;
    private final static int BOTH = 3;

    public SectionButton(Context context){
        super(context);
    }

    public SectionButton(Context context, AttributeSet attSet){
        super(context, attSet);
    }

    public SectionButton(Context context, AttributeSet attSet, int defStyleAttr){
        super(context, attSet, defStyleAttr);
    }

    public SectionButton(Context context, int zone, int level, int blocked) {
        super(context);
        setZone(zone);
        setLevel(level);
        setBlockedType(blocked);
        setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        setOnClickListener((OnClickListener)context);
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

        String resultNameN = "00" + zone;
        setText("P" + resultNameN.substring(resultNameN.length() - 2) + ", " + level);
    }

    void setZone(int zone) {
        this.mZone = zone;
    }

    void setLevel(int level) {
        this.mLevel = level;
    }

    public void setBlockedType(int block) {
        this.mBlockedType = block;
        setBlockedColor();
    }

    public int getZone() {
        return this.mZone;
    }

    public int getLevel() {
        return this.mLevel;
    }

    public String getStringForKey(){
        return "P" + getZone() + "_" + getLevel();
    }

//    public int getBlocked() {
//        return this.mBlockedType;
//    }

    private void setBlockedColor(){
        switch (mBlockedType){
            case UNBLOCKED:
                setBackgroundResource(R.color.lockUnlock_button_unblocked);
                break;
            case BLOCKED:
                setBackgroundResource(R.color.lockUnlock_button_blocked);
                break;
            case BOTH:
                setBackgroundResource(R.color.lockUnlock_button_both);
                break;
            default: break;
        }
    }
}