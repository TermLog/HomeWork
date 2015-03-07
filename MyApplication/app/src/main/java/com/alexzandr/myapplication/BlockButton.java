package com.alexzandr.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.Button;

/**
 * Created by AlexZandR on 22.02.2015.
 */
public class BlockButton extends Button {
    private int mZone;
    private int mLevel;
    private int mBlocked;
    final static int UNBLOCKED = 1;
    final static int BLOCKED = 2;
    final static int BOTH = 3;

    public BlockButton(Context context){
        super(context);
    }
    public BlockButton(Context context, AttributeSet attSet){
        super(context, attSet);
    }
    public BlockButton(Context context, AttributeSet attSet, int defStyleAttr){
        super(context, attSet, defStyleAttr);
    }

    public BlockButton(Context context, int zone, int level, int blocked) {
        super(context);
        setZone(zone);
        setLevel(level);
        setBlocked(blocked);
        setOnClickListener((LockUnlockActivity)context);
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

        String resultNameN = "00" + zone;
        setText("P" + resultNameN.substring(resultNameN.length() - 2) + ", " + level);
    }

    public void setZone(int zone) {
        this.mZone = zone;
    }
    public void setLevel(int level) {
        this.mLevel = level;
    }
    public void setBlocked(int block) {
        this.mBlocked = block;
        setAndroidSettings();
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
    public int getBlocked() {
        return this.mBlocked;
    }

    private void setAndroidSettings(){
        switch (mBlocked){
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