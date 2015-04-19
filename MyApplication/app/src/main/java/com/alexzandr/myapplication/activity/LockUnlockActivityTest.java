package com.alexzandr.myapplication.activity;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.alexzandr.myapplication.AdapterItemHandler;
import com.alexzandr.myapplication.DataBaseTask;
import com.alexzandr.myapplication.LockUnlockAdapter;
import com.alexzandr.myapplication.Singleton;
import com.alexzandr.myapplication.fragment.ErrorShowDialog;
import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.view.ZoneLevelButton;

import java.util.ArrayList;
import java.util.HashMap;


public class LockUnlockActivityTest extends ActionBarActivity implements OnClickListener, ErrorShowDialog.OnShowErrors {

    private int mLevelsCount;
    private int mZonesCount;
    private LinearLayout mFirstRow;
    private LockUnlockAdapter mAdapter;
    private ProgressDialog mProgressDialog;
    private ArrayList<AdapterItemHandler> mArrayForAdapter;
    private GridView mGridView;
    private Button mRefreshButton;
    private ErrorShowDialog mErrorDialog;

    private static int sRefreshCount;

    private final static String KEY_COUNT_OF_ZONES = "zoneCount";
    private final static String KEY_COUNT_OF_LEVELS = "levelCount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_unlock_test);

        mFirstRow = (LinearLayout) findViewById(R.id.lockUnlock_test_firstRow);
        mRefreshButton = (Button) findViewById(R.id.lockUnlock_test_buttonRefresh);
        mGridView = (GridView) findViewById(R.id.lockUnlock_test_list_of_button);

        mErrorDialog = new ErrorShowDialog();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(R.string.progressBar_title);
        mProgressDialog.setMessage(getText(R.string.progressBar_massage));
        mProgressDialog.setCancelable(false);

        LockTask task = new LockTask();
        task.execute(DataBaseTask.GET_ALL_DATA);
    }

    @Override
    public void onClick(View view) {
        levelClick((ZoneLevelButton) view);
    }

    void createTable(HashMap<String, Integer> map){

        mZonesCount = map.get(KEY_COUNT_OF_ZONES);
        mLevelsCount = map.get(KEY_COUNT_OF_LEVELS);
        mArrayForAdapter = createArray(map, mArrayForAdapter);

        fillFirstRow(mLevelsCount);

        if (mAdapter == null) {
            mAdapter = new LockUnlockAdapter(this, mArrayForAdapter);
            mGridView.setAdapter(mAdapter);
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AdapterItemHandler handler = mAdapter.getHandler(position);
                    if (handler.getAvailability()) {
                        view.startAnimation(Singleton.getAnimation());
                        switch (handler.getType()) {
                            case AdapterItemHandler.ZONE_BUTTON:
                                zoneClick(handler, position);
                                break;
                            case AdapterItemHandler.SECTION_BUTTON:
                                sectionClick(handler);
                                break;
                        }
                    }
                }
            });
        }
        mGridView.setNumColumns(mLevelsCount + 1);
        mAdapter.notifyDataSetChanged();
    }

    private ArrayList<AdapterItemHandler> createArray(HashMap<String, Integer> map, ArrayList<AdapterItemHandler> handlers) {
        if (handlers == null) {
            handlers = new ArrayList<>();
        } else {
            handlers.clear();
        }
        for (int zone = 1; zone <= mZonesCount; zone++) {
            handlers.add(new AdapterItemHandler(zone));
            for (int level = 1; level <= mLevelsCount; level++) {
                AdapterItemHandler handler = new AdapterItemHandler(zone, level);
                try {
                    handler.setBlockedType(map.get(handler.getStringForKey()));
                } catch (NullPointerException nullException){
                    handler.setAvailability(false);
                }
                handlers.add(handler);
            }
        }
        return handlers;
    }

    private void fillFirstRow(int levelsCount) {
        if (mFirstRow.getChildCount() > 1) {
            for (int i =  mFirstRow.getChildCount(); i > 0; i--) {
                if (mFirstRow.getChildAt(i - 1) instanceof ZoneLevelButton){
                    mFirstRow.removeView(mFirstRow.getChildAt(i - 1));
                }
            }
        }
        for (int level = 1; level <= levelsCount; level++){
            mFirstRow.addView(new ZoneLevelButton(this, ZoneLevelButton.TYPE_LEVEL, level));
        }
    }

    public void onRefreshClick(View view){
        LockTask task = new LockTask();
        task.execute(DataBaseTask.GET_ALL_DATA);
        mRefreshButton.setText("Ref_" + (++sRefreshCount));
    }

    private void sectionClick(AdapterItemHandler handler){
        LockTask task = new LockTask(handler);
        task.procedureParamZone = handler.getZone();
        task.procedureParamLevel = handler.getLevel();
        task.execute(DataBaseTask.SECTION_CHANGE);
    }

    private void sectionChange(AdapterItemHandler handler, HashMap<String, Integer> map){
        String key = handler.getStringForKey();
        handler.setBlockedType(map.get(key));
        mAdapter.notifyDataSetChanged();
    }

    private void zoneClick(AdapterItemHandler handler, int position){
        LockTask task = new LockTask(handler, position);
        task.procedureParamType = handler.getType();
        task.procedureParamValue = handler.getZone();
        task.execute(DataBaseTask.ZONE_LEVEL_CHANGE);
    }

    private void zoneChange(int itemIndex, HashMap<String, Integer> map){
        for (int level = 1; level <= mLevelsCount; level++) {
            AdapterItemHandler handler = mArrayForAdapter.get(itemIndex + level);
            String key = handler.getStringForKey();
            try {
                handler.setBlockedType(map.get(key));
            } catch (NullPointerException e){
                handler.setBlockedType(handler.getBlockedType());
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void levelClick(ZoneLevelButton levelButton){
        LockTask task = new LockTask(levelButton.getValue());
        task.procedureParamType = levelButton.getType();
        task.procedureParamValue = levelButton.getValue();
        task.execute(DataBaseTask.ZONE_LEVEL_CHANGE);
    }

    private void levelChange(int level, HashMap<String, Integer> map){
        for (int zone = 0; zone < mZonesCount; zone++) {
            AdapterItemHandler button = mArrayForAdapter.get(level + zone * (mLevelsCount + 1));
            String key = button.getStringForKey();
            try {
                button.setBlockedType(map.get(key));
            } catch (NullPointerException e){
                button.setBlockedType(button.getBlockedType());
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError(String errorText) {
        Bundle errorMassage = new Bundle();
        errorMassage.putString(ErrorShowDialog.KEY_FOR_ERROR, errorText);
        mErrorDialog.setArguments(errorMassage);
        mErrorDialog.show(getFragmentManager(), "ErrorDialog");
    }

    private class LockTask extends DataBaseTask{

        private final static int NO_INDEX = 0;
        AdapterItemHandler itemHandler;
        int itemIndex;

        LockTask(){
            this(null, NO_INDEX);
        }

        LockTask(int levelNumber) {
            this(null, levelNumber);
        }

        LockTask(AdapterItemHandler itemHandler){
            this(itemHandler, NO_INDEX);
        }

        LockTask(AdapterItemHandler itemHandler, int index){
            this.itemHandler = itemHandler;
            itemIndex = index;
        }

        @Override
        protected void onPreExecute(){
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(HashMap<String, Integer> result) {

            mProgressDialog.dismiss();

            if (exception != null){
                showError(exception.getMessage());
            } else if (result != null) {
                if (itemHandler == null) {
                    if (itemIndex == NO_INDEX) {
                        createTable(result);
                    } else {
                        levelChange(itemIndex, result);
                    }
                } else if (itemHandler.getType() == AdapterItemHandler.SECTION_BUTTON) {
                    sectionChange(itemHandler, result);
                } else {
                    zoneChange(itemIndex, result);
                }
            }
        }
    }
}
