package com.alexzandr.myapplication;

import android.os.AsyncTask;

import java.util.HashMap;

/**
 * Created by AlexZandR on 23.02.2015.
 */
public class DataBaseTask extends AsyncTask<Integer, Void, HashMap<String, Integer>> {
    final static int ALL_TABLE = 1;
    final static int ZONE_LEVEL = 2;
    final static int BLOCK_BUTTON = 3;
    public int procedureParamType;
    public int procedureParamValue;
    public int procedureParamZone;
    public int procedureParamLevel;
    @Override
    protected HashMap<String, Integer> doInBackground(Integer... params) {
        int intParams = params[0];
        QueryToServer serverData = new QueryToServer();
        HashMap<String, Integer> mapResult = null;
        switch (intParams){
            case ALL_TABLE:
                mapResult = serverData.getAllData();
                break;
            case ZONE_LEVEL:
                mapResult = serverData.changeZoneLevel(procedureParamType, procedureParamValue);
                break;
            case BLOCK_BUTTON:
                mapResult = serverData.changeSection(procedureParamZone, procedureParamLevel);
                break;
            default: break;
        }
        return mapResult;
    }
}
