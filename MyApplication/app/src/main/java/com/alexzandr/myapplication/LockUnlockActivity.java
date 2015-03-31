package com.alexzandr.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.HashMap;


public class LockUnlockActivity extends ActionBarActivity implements OnClickListener, ErrorShowDialog.OnShowErrors {

    private TableLayout mTable;
    private TableRow mFirstRow;
    private Button mRefreshButton;
    private ErrorShowDialog mErrorDialog;
    private DataBaseTask mTask;
    private HashMap<String, Integer> mResultMap;
    private Animation mButtonAnimation = null;

    private static int sRefreshCount;

    private final static String KEY_COUNT_OF_ZONES = "zoneCount";
    private final static String KEY_COUNT_OF_LEVELS = "levelCount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_unlock);

        mTable = (TableLayout) findViewById(R.id.lockUnlock_table);
        mFirstRow = (TableRow) findViewById(R.id.lockUnlock_firstRow);
        mRefreshButton = (Button) findViewById(R.id.lockUnlock_buttonRefresh);

        mErrorDialog = new ErrorShowDialog();
        mErrorDialog.setCancelable(false);

        mButtonAnimation = AnimationUtils.loadAnimation(this, R.anim.button_scale_animation);

    }

    @Override
    protected void onStart(){
        super.onStart();
        if (mTable.getChildCount() <= 1) {
            createTable();
        }
    }

    @Override
    public void onClick(View view) {
        if (view instanceof BlockButton){
            blockButtonClick((BlockButton) view);
        } else if (view instanceof ZoneLevelButton){
            zoneLevelClick((ZoneLevelButton) view);
        }
    }

    void createTable(){
        mTask = new DataBaseTask();
        mResultMap = null;
        try{
            mTask.execute(DataBaseTask.ALL_TABLE);
            mResultMap = mTask.get();

            if (mTask.exception != null){
                throw mTask.exception;
            }

            if (mResultMap != null) {
                int zone, level;
                int zoneCount = mResultMap.get(KEY_COUNT_OF_ZONES);
                int levelCount = mResultMap.get(KEY_COUNT_OF_LEVELS);

                for (level = 1; level <= levelCount; level++){
                    mFirstRow.addView(new ZoneLevelButton(this, ZoneLevelButton.TYPE_LEVEL, level));
                }

                for (zone = 1; zone <= zoneCount; zone++) {
                    TableRow row = new TableRow(this);
                    row.addView(new ZoneLevelButton(this, ZoneLevelButton.TYPE_ZONE, zone));

                    for (level = 1; level <= levelCount; level++) {
                        row.addView(new BlockButton(this, zone, level, mResultMap.get("P" + zone + "_" + level)));
                    }

                    mTable.addView(row);
                }
            }
        } catch (Exception e){
            showError(e.getMessage());
        }
    }

    public void onRefreshClick(View view){
        view.startAnimation(mButtonAnimation);
        mTask = new DataBaseTask();
        mResultMap = null;
        try{
            mTask.execute(DataBaseTask.ALL_TABLE);
            mResultMap = mTask.get();

            if (mTask.exception != null){
                throw mTask.exception;
            }

            if (mResultMap != null) {
                int rowCount = mTable.getChildCount();

                for (int rowNumber = 0; rowNumber < rowCount; rowNumber++) {

                    if (mTable.getChildAt(rowNumber) instanceof TableRow) {
                        TableRow row = (TableRow) mTable.getChildAt(rowNumber);
                        int buttonCount = row.getChildCount();

                        for (int buttonNumber = 0; buttonNumber < buttonCount; buttonNumber++) {

                            if (row.getChildAt(buttonNumber) instanceof BlockButton) {
                                BlockButton button = (BlockButton) row.getChildAt(buttonNumber);
                                button.setBlocked(mResultMap.get("P" + rowNumber + "_" + buttonNumber));
                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            showError(e.getMessage());
        }
        mRefreshButton.setText("Ref_" + (++sRefreshCount));
    }

    private void blockButtonClick(BlockButton button){
        button.startAnimation(mButtonAnimation);
        mTask = new DataBaseTask();
        mTask.procedureParamZone = button.getZone();
        mTask.procedureParamLevel = button.getLevel();
        try {
            mTask.execute(DataBaseTask.BLOCK_BUTTON);
            mResultMap = mTask.get();

            if (mTask.exception != null){
                throw mTask.exception;
            }

            button.setBlocked(mResultMap.get(button.getStringForKey()));
        }catch (Exception e){
            showError(e.getMessage());
        }
    }

    private void zoneLevelClick(ZoneLevelButton button){
        button.startAnimation(mButtonAnimation);
        mTask = new DataBaseTask();
        mResultMap = null;
        mTask.procedureParamType = button.getType();
        mTask.procedureParamValue = button.getValue();
        try {
            mTask.execute(DataBaseTask.ZONE_LEVEL);
            mResultMap = mTask.get();

            if (mTask.exception != null){
                throw mTask.exception;
            }

            if (button.getType() == ZoneLevelButton.TYPE_ZONE && mResultMap != null){

                changeButtonInRow((TableRow) button.getParent(), mResultMap, button);

            }else if (mResultMap != null) {
                int rowCount = mTable.getChildCount();
                for (int rowNumber = 0; rowNumber < rowCount; rowNumber++) {
                    if (mTable.getChildAt(rowNumber) instanceof TableRow) {
                        changeButtonInRow((TableRow) mTable.getChildAt(rowNumber), mResultMap, button);
                    }
                }
            }
        }catch (Exception e){
            showError(e.getMessage());
        }
    }

    private void changeButtonInRow(TableRow parentRow, HashMap<String, Integer> map, ZoneLevelButton zoneLevelButton){
        int buttonCount = parentRow.getChildCount();
        for (int i = 0; i < buttonCount; i++){
            if (parentRow.getChildAt(i) instanceof BlockButton){
                BlockButton button = (BlockButton)parentRow.getChildAt(i);
                if (button.getLevel() == zoneLevelButton.getValue() || zoneLevelButton.getType() == ZoneLevelButton.TYPE_ZONE) {
                    try {
                        button.setBlocked(map.get(button.getStringForKey()));
                    } catch (Exception e) {
                        showError(e.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public void showError(String errorText) {
        Bundle errorMassage = new Bundle();
        errorMassage.putString(ErrorShowDialog.KEY_FOR_ERROR, errorText);
        mErrorDialog.setArguments(errorMassage);
        mErrorDialog.show(getFragmentManager(), "ErrorDialog");
    }
}
