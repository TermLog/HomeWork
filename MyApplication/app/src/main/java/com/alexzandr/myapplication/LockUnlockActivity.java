package com.alexzandr.myapplication;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class LockUnlockActivity extends ActionBarActivity {

    public TextView captionText;
    public TableLayout table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_unlock);
        captionText = (TextView)findViewById(R.id.lockUnlock_captionText);
        table = (TableLayout) findViewById(R.id.lockUnlock_table);
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onStart(){
        super.onStart();
        int zone, level;
        TableRow.LayoutParams rowLayoutParam = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        rowLayoutParam.setMargins(2, 2, 2, 2);
        for (zone = 1; zone <= ((int)(Math.random() * 6)) + 5; zone++){

            String buttonName = "00" + zone;
            buttonName = "P" + buttonName.substring(buttonName.length() - 2, buttonName.length());

            Button buttonZone = new Button(this);
            buttonZone.setLayoutParams(rowLayoutParam);
            buttonZone.setText("Zone " + buttonName);
            buttonZone.setBackgroundResource(R.color.lockUnlock_button_zoneAndLevel);

            TableRow row = new TableRow(this);
            row.setLayoutParams(rowLayoutParam);
            row.addView(buttonZone);

            for (level = 1; level <= ((int)(Math.random() * 3)) + 2; level++){
                BlockButton button = new BlockButton(this, zone, level, (int)(Math.random() * 3) + 1);
                button.setLayoutParams(rowLayoutParam);
                button.setText(buttonName + ", " + level);
                row.addView(button);
            }

            table.addView(row);
        }
    }

    private class BlockButton extends Button{
        public BlockButton(Context context, int zone, int level, int blocked) {
            super(context);
            setZone(zone);
            setLevel(level);
            setBlocked(blocked);

            setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        String block;
                        if (isBlocked()) {
                            block = "заблокированныу";
                        } else {
                            block = "разблокированные";
                        }
                        captionText.setText("Зона = " + getZone() + "; уровень = " + getLevel() + ". Ячейки " + block + "(" + mBlocked + ")");
                        captionText.setBackground(getBackground());
                    }
                    return false;
                }
            });

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

        private int mZone;
        private int mLevel;
        public int mBlocked;
        final static int UNBLOCKED = 1;
        final static int BLOCKED = 2;
        final static int BOTH = 3;

        public void setZone(int zone) {
            this.mZone = zone;
        }
        public void setLevel(int level) {
            this.mLevel = level;
        }
        public void setBlocked(int block) {
            this.mBlocked = block;
        }

        public int getZone() {
            return this.mZone;
        }
        public int getLevel() {
            return this.mLevel;
        }
        public boolean isBlocked() {
            return this.mBlocked != UNBLOCKED;
        }

    }

}
