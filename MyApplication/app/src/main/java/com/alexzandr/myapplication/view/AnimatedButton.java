package com.alexzandr.myapplication.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.alexzandr.myapplication.Singleton;

/**
 * Created by serg on 4/15/15.
 */
public class AnimatedButton extends Button implements View.OnClickListener {

    private OnClickListener mOnClickListener;

    public AnimatedButton(Context context) {
        super(context);
        init();
    }

    public AnimatedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimatedButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        super.setOnClickListener(this);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mOnClickListener = l;
    }

    @Override
    public void onClick(View v) {
        startAnimation(Singleton.getAnimation());
        if (mOnClickListener != null) {
            mOnClickListener.onClick(v);
        }
    }
}
