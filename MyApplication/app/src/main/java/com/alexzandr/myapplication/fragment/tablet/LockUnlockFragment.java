package com.alexzandr.myapplication.fragment.tablet;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.alexzandr.myapplication.DataBaseTask;
import com.alexzandr.myapplication.LockUnlockAdapter;
import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.Singleton;
import com.alexzandr.myapplication.fragment.dialog.ErrorShowDialog;
import com.alexzandr.myapplication.fragment.dialog.SetHeightDialog;
import com.alexzandr.myapplication.handler.AdapterItemHandler;
import com.alexzandr.myapplication.handler.LevelHandler;
import com.alexzandr.myapplication.handler.SectionHandler;
import com.alexzandr.myapplication.handler.ZoneHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class LockUnlockFragment extends WarehouseFragment implements SetHeightDialog.OnAdapterChangedListener {

    private int mLevelsCount;
    private int mZonesCount;
    private LockUnlockAdapter mAdapterSection;
    private LockUnlockAdapter mAdapterLevel;
    private ProgressDialog mProgressDialog;
    private ArrayList<AdapterItemHandler> mSections;
    private ArrayList<AdapterItemHandler> mLevels;
    private GridView mGridViewLevel;
    private GridView mGridViewSection;
    private ErrorShowDialog mErrorDialog;

    private final static String KEY_COUNT_OF_ZONES = "zoneCount";
    private final static String KEY_COUNT_OF_LEVELS = "levelCount";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mErrorDialog = new ErrorShowDialog();
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setTitle(R.string.progressBar_title);
        mProgressDialog.setMessage(getText(R.string.progressBar_massage));
        mProgressDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_lock_unlock_test, container, false);
        mGridViewLevel = (GridView) view.findViewById(R.id.lockUnlock_test_firstRow);
        mGridViewSection = (GridView) view.findViewById(R.id.lockUnlock_test_sections);

        LockTask task = new LockTask();
        task.execute(DataBaseTask.GET_ALL_DATA);

        if (isPortOrientation()) {
            mActivity.setTitle(R.string.title_activity_lock_unlock);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        onAdapterChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    void createTable(HashMap<String, Integer> map){

        mZonesCount = map.get(KEY_COUNT_OF_ZONES);
        mLevelsCount = map.get(KEY_COUNT_OF_LEVELS);
        mSections = createSections(map, mSections);

        fillFirstRow(mLevelsCount);

        if (mAdapterSection == null) {
            mAdapterSection = new LockUnlockAdapter(mActivity, mSections);
            mGridViewSection.setAdapter(mAdapterSection);
            mGridViewSection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AdapterItemHandler handler = mAdapterSection.getHandler(position);
                    if (handler.isAvailability()) {
                        view.startAnimation(Singleton.getAnimation());
                        switch (handler.getType()) {
                            case AdapterItemHandler.ZONE_BUTTON:
                                zoneClick((ZoneHandler)handler, position);
                                break;
                            case AdapterItemHandler.SECTION_BUTTON:
                                sectionClick((SectionHandler)handler);
                                break;
                        }
                    }
                }
            });
        }
        mGridViewSection.setNumColumns(mLevelsCount + 1);
        mAdapterSection.notifyDataSetChanged();
    }

    private ArrayList<AdapterItemHandler> createSections(HashMap<String, Integer> map, ArrayList<AdapterItemHandler> handlers) {
        if (handlers == null) {
            handlers = new ArrayList<>();
        } else {
            handlers.clear();
        }
        for (int zone = 1; zone <= mZonesCount; zone++) {
            handlers.add(new ZoneHandler(zone));
            for (int level = 1; level <= mLevelsCount; level++) {
                SectionHandler handler = new SectionHandler(zone, level);
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
        if (mLevels == null){
            mLevels = new ArrayList<>();
        }

        mLevels.clear();
        mLevels.add(new LevelHandler(LevelHandler.REFRESH_BUTTON));
        for (int level = 1; level <= levelsCount; level++){
            mLevels.add(new LevelHandler(level));
        }

        if (mAdapterLevel == null){
            mAdapterLevel = new LockUnlockAdapter(mActivity, mLevels);
            mGridViewLevel.setAdapter(mAdapterLevel);
            mGridViewLevel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    LevelHandler handler = (LevelHandler) mAdapterLevel.getHandler(position);
                    if (handler.isAvailability()) {
                        view.startAnimation(Singleton.getAnimation());
                        if (handler.getLevel() == LevelHandler.REFRESH_BUTTON) {
                            refresh();
                        } else {
                            levelClick(handler.getLevel());
                        }
                    }
                }
            });
        }

        mAdapterLevel.notifyDataSetChanged();
        mGridViewLevel.setNumColumns(mLevelsCount + 1);
    }

    public void refresh(){
        LockTask task = new LockTask();
        task.execute(DataBaseTask.GET_ALL_DATA);
//        mRefreshButton.setText("Ref_" + (++sRefreshCount));
    }

    private void sectionClick(SectionHandler handler){
        LockTask task = new LockTask(handler);
        task.procedureParamZone = handler.getZone();
        task.procedureParamLevel = handler.getLevel();
        task.execute(DataBaseTask.SECTION_CHANGE);
    }

    private void sectionChange(SectionHandler handler, HashMap<String, Integer> map){
        String key = handler.getStringForKey();
        handler.setBlockedType(map.get(key));
        mAdapterSection.notifyDataSetChanged();
    }

    private void zoneClick(ZoneHandler handler, int position){
        LockTask task = new LockTask(handler, position);
        task.procedureParamType = handler.getType();
        task.procedureParamValue = handler.getZone();
        task.execute(DataBaseTask.ZONE_LEVEL_CHANGE);
    }

    private void zoneChange(int itemIndex, HashMap<String, Integer> map){
        for (int level = 1; level <= mLevelsCount; level++) {
            SectionHandler handler = (SectionHandler) mSections.get(itemIndex + level);
            String key = handler.getStringForKey();
            try {
                handler.setBlockedType(map.get(key));
                handler.setAvailability(true);
            } catch (NullPointerException e){
                handler.setAvailability(false);
            }
        }
        mAdapterSection.notifyDataSetChanged();
    }

    private void levelClick(int level){
        LockTask task = new LockTask(level);
        task.procedureParamType = AdapterItemHandler.LEVEL_BUTTON;
        task.procedureParamValue = level;
        task.execute(DataBaseTask.ZONE_LEVEL_CHANGE);
    }

    private void levelChange(int level, HashMap<String, Integer> map){
        for (int zone = 0; zone < mZonesCount; zone++) {
            SectionHandler handler = (SectionHandler) mSections.get(level + zone * (mLevelsCount + 1));
            String key = handler.getStringForKey();
            try {
                handler.setBlockedType(map.get(key));
                handler.setAvailability(true);
            } catch (NullPointerException e){
                handler.setAvailability(false);
            }
        }
        mAdapterSection.notifyDataSetChanged();
    }

//    @Override
    public void showError(String errorText) {
        Bundle errorMassage = new Bundle();
        errorMassage.putString(ErrorShowDialog.KEY_FOR_ERROR, errorText);
        mErrorDialog.setArguments(errorMassage);
        mErrorDialog.show(getFragmentManager(), "ErrorDialog");
    }

    @Override
    public void onAdapterChanged() {
        if (mAdapterLevel != null) {
            mAdapterLevel.notifyDataSetChanged();
        }
        if (mAdapterSection != null) {
            mAdapterSection.notifyDataSetChanged();
        }
    }

    @Override
    public String getTitle() {
        return getString(R.string.title_activity_lock_unlock);
    }


    private class LockTask extends DataBaseTask{

        private final static int NO_INDEX = 0;
        final static int CREATE_TABLE = 0;
        final static int SECTION_CHANGE = 1;
        final static int LEVEL_CHANGE = 2;
        final static int ZONE_CHANGE = 3;
        AdapterItemHandler itemHandler;
        int itemIndex;
        int queryType;

        LockTask(){
            this(null, NO_INDEX, CREATE_TABLE);
        }

        LockTask(int levelNumber) {
            this(null, levelNumber, LEVEL_CHANGE);
        }

        LockTask(AdapterItemHandler itemHandler){
            this(itemHandler, NO_INDEX, SECTION_CHANGE);
        }

        LockTask(AdapterItemHandler itemHandler, int index){
            this(itemHandler, index, ZONE_CHANGE);
        }

        LockTask(AdapterItemHandler itemHandler, int index, int type){
            this.itemHandler = itemHandler;
            this.itemIndex = index;
            this.queryType = type;
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
            } else if (result == null) {
                showError(getString(R.string.no_data_in_query));
            } else {
                switch (queryType){
                    case CREATE_TABLE:
                        createTable(result);
                        break;
                    case SECTION_CHANGE:
                        sectionChange((SectionHandler)itemHandler, result);
                        break;
                    case LEVEL_CHANGE:
                        levelChange(itemIndex, result);
                        break;
                    case ZONE_CHANGE:
                        zoneChange(itemIndex, result);
                        break;
                    default: break;
                }
            }
        }
    }
}
