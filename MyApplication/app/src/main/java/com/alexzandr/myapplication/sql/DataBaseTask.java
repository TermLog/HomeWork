package com.alexzandr.myapplication.sql;

import android.os.AsyncTask;

import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.application.Singleton;
import com.alexzandr.myapplication.exception.CheckConnectionException;
import com.alexzandr.myapplication.exception.NullQueryException;

import java.util.HashMap;

/**
 * Created by AlexZandR on 23.02.2015.
 */
public class DataBaseTask extends AsyncTask<Integer, Void, HashMap<String, Integer>> {
    public final static int GET_ALL_DATA = 1;
    public final static int ZONE_LEVEL_CHANGE = 2;
    public final static int SECTION_CHANGE = 3;
    public final static int CHECK_CONNECTION = 4;
    public final static int UPDATE_IN_DOC = 5;
    public final static int DELETE_IN_DOC = 6;
    public int procedureParamType;
    public int procedureParamValue;
    public int procedureParamZone;
    public int procedureParamLevel;
    public String procedureParamDocList;
    public HashMap<String, Integer> mapResult;
    public CheckConnectionException exception = null;

    @Override
    protected HashMap<String, Integer> doInBackground(Integer... params) {
        int intParams = params[0];
        mapResult = null;
        QueryToServer serverData = Singleton.getQueryToServer();
        if (serverData == null) {
            String msg = Singleton.getContext().getResources().getString(R.string.error_msg_null_query);
            exception = new NullQueryException(msg);
            return mapResult;
        }
        try {
            switch (intParams){
                case GET_ALL_DATA:
                    mapResult = serverData.getAllData();
                    break;
                case ZONE_LEVEL_CHANGE:
                    mapResult = serverData.changeZoneLevel(procedureParamType, procedureParamValue);
                    break;
                case SECTION_CHANGE:
                    mapResult = serverData.changeSection(procedureParamZone, procedureParamLevel);
                    break;
                case UPDATE_IN_DOC:
                    mapResult = serverData.updateDoc(procedureParamDocList);
                    break;
                case DELETE_IN_DOC:
                    mapResult = serverData.deleteDoc(procedureParamDocList);
                    break;
                case CHECK_CONNECTION:
                    mapResult = serverData.checkConnection();
                    break;
                default: break;
            }
        } catch (CheckConnectionException e) {
            exception = e;
        }
        return mapResult;
    }
}
