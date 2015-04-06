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

        mTask = new DataBaseTask();

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
        if (view instanceof SectionButton){
            sectionButtonClick((SectionButton) view);
        } else if (view instanceof ZoneLevelButton){
            zoneLevelClick((ZoneLevelButton) view);
        }
    }

    void createTable(){
        try{
            mTask.execute(DataBaseTask.GET_ALL_DATA);
            mResultMap = mTask.get();

            if (mTask.exception != null){
                throw mTask.exception;
            }

            if (mResultMap != null) {
                int zonesCount = mResultMap.get(KEY_COUNT_OF_ZONES);
                int levelsCount = mResultMap.get(KEY_COUNT_OF_LEVELS);

                fillFirstRow(levelsCount);

                addNewRow(zonesCount, levelsCount);
            }
        } catch (Exception e){
            showError(e.getMessage());
        } finally {
            mResultMap = null;
            mTask.exception = null;
        }

    }

    private void fillFirstRow(int levelsCount) {
        for (int level = 1; level <= levelsCount; level++){
            mFirstRow.addView(new ZoneLevelButton(this, ZoneLevelButton.TYPE_LEVEL, level));
        }
    }

    private void addNewRow(int zonesCount, int levelsCount) {
        for (int zone = 1; zone <= zonesCount; zone++) {
            TableRow newRow = new TableRow(this);
            fillRow(zone, levelsCount, newRow);
            mTable.addView(newRow);
        }
    }

    private void fillRow(int zoneNumber, int levelsCount, TableRow row){
        row.addView(new ZoneLevelButton(this, ZoneLevelButton.TYPE_ZONE, zoneNumber));

        for (int level = 1; level <= levelsCount; level++){
            int buttonBlockedType = mResultMap.get("P" + zoneNumber + "_" + level);
            row.addView(new SectionButton(this, zoneNumber, level, buttonBlockedType));
        }
    }

    public void onRefreshClick(View view){
        view.startAnimation(mButtonAnimation);
        try{
            mTask.execute(DataBaseTask.GET_ALL_DATA);
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
                            if (row.getChildAt(buttonNumber) instanceof SectionButton) {
                                SectionButton button = (SectionButton) row.getChildAt(buttonNumber);
                                String key = button.getStringForKey();

                                button.setBlockedType(mResultMap.get(key));
                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            showError(e.getMessage());
        } finally {
            mResultMap = null;
            mTask.exception = null;
        }
        mRefreshButton.setText("Ref_" + (++sRefreshCount));
    }

    private void sectionButtonClick(SectionButton button){
        button.startAnimation(mButtonAnimation);
        mTask.procedureParamZone = button.getZone();
        mTask.procedureParamLevel = button.getLevel();
        try {
            mTask.execute(DataBaseTask.SECTION_CHANGE);
            mResultMap = mTask.get();

            if (mTask.exception != null){
                throw mTask.exception;
            }

            String key = button.getStringForKey();
            button.setBlockedType(mResultMap.get(key));
        } catch (Exception e){
            showError(e.getMessage());
        } finally {
            mResultMap = null;
            mTask.exception = null;
        }
    }

    private void zoneLevelClick(ZoneLevelButton button){
        button.startAnimation(mButtonAnimation);
        mTask.procedureParamType = button.getType();
        mTask.procedureParamValue = button.getValue();
        try {
            mTask.execute(DataBaseTask.ZONE_LEVEL_CHANGE);
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
        } finally {
            mResultMap = null;
            mTask.exception = null;
        }
    }

    private void changeButtonInRow(TableRow parentRow, HashMap<String, Integer> map, ZoneLevelButton zoneLevelButton){
        int buttonCount = parentRow.getChildCount();
        for (int i = 0; i < buttonCount; i++){
            if (parentRow.getChildAt(i) instanceof SectionButton){
                SectionButton button = (SectionButton)parentRow.getChildAt(i);
                if (button.getLevel() == zoneLevelButton.getValue() || zoneLevelButton.getType() == ZoneLevelButton.TYPE_ZONE) {
                    try {
                        button.setBlockedType(map.get(button.getStringForKey()));
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
