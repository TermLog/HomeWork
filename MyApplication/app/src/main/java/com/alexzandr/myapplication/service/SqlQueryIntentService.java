package com.alexzandr.myapplication.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.alexzandr.myapplication.application.Singleton;
import com.alexzandr.myapplication.exception.CheckConnectionException;
import com.alexzandr.myapplication.sql.QueryToServer;

import java.util.HashMap;

public class SqlQueryIntentService extends IntentService {

    public final static int GET_ALL_DATA = 1;
    public final static int ZONE_LEVEL_CHANGE = 2;
    public final static int SECTION_CHANGE = 3;
    public final static int CHECK_CONNECTION = 4;
    public final static int UPDATE_IN_DOC = 5;
    public final static int DELETE_IN_DOC = 6;

    public static final String EXTRA_QUERY_TYPE = "com.alexzandr.myapplication.SqlQueryIntentService.extra.QUERY_TYPE";
    public static final String EXTRA_TYPE_OR_ZONE = "com.alexzandr.myapplication.SqlQueryIntentService.extra.TYPE_OR_ZONE";
    public static final String EXTRA_VALUE_OR_LEVEL = "com.alexzandr.myapplication.SqlQueryIntentService.extra.VALUE_OR_LEVEL";
    public static final String EXTRA_DOC_LIST = "com.alexzandr.myapplication.SqlQueryIntentService.extra.DOC_LIST";
    public static final String EXTRA_RESULT_MAP = "com.alexzandr.myapplication.SqlQueryIntentService.extra.RESULT_MAP";
    public static final String EXTRA_ERROR_MESSAGE = "com.alexzandr.myapplication.SqlQueryIntentService.extra.ERROR_MESSAGE";

    public static final String QUERY_SUCCESSFUL = "com.alexzandr.myapplication.SqlQueryIntentService.QUERY_SUCCESSFUL";
    public static final String QUERY_ERROR = "com.alexzandr.myapplication.SqlQueryIntentService.QUERY_ERROR";

    public CheckConnectionException exception = null;

    public static void executeQuery(Context context, int queryType, int typeOrZone, int valueOrLevel, String docList) {
        Intent intent = new Intent(context, SqlQueryIntentService.class);
        intent.putExtra(EXTRA_QUERY_TYPE, queryType);
        intent.putExtra(EXTRA_TYPE_OR_ZONE, typeOrZone);
        intent.putExtra(EXTRA_VALUE_OR_LEVEL, valueOrLevel);
        intent.putExtra(EXTRA_DOC_LIST, docList);
        context.startService(intent);
    }

    public static void executeQuery(Context context, int queryType, int typeOrZone, int valueOrLevel) {
        executeQuery(context, queryType, typeOrZone, valueOrLevel, "");
    }

    public static void executeQuery(Context context, int queryType, String docList) {
        executeQuery(context, queryType, 0, 0, docList);
    }

    public static void executeQuery(Context context, int queryType) {
        executeQuery(context, queryType, 0, 0);
    }


    public SqlQueryIntentService() {
        super("SqlQueryIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            HashMap<String, Integer> result = null;
            QueryToServer serverData = Singleton.getQueryToServer();
            int queryType = intent.getIntExtra(EXTRA_QUERY_TYPE, CHECK_CONNECTION);
            try {
                System.out.println("BEGIN QUERY");
                switch (queryType) {
                    case GET_ALL_DATA:
                        result = serverData.getAllData();
                        break;
                    case ZONE_LEVEL_CHANGE:
                        int type = intent.getIntExtra(EXTRA_TYPE_OR_ZONE, 0);
                        int value = intent.getIntExtra(EXTRA_VALUE_OR_LEVEL, 0);
                        result = serverData.changeZoneLevel(type, value);
                        break;
                    case SECTION_CHANGE:
                        int zone = intent.getIntExtra(EXTRA_TYPE_OR_ZONE, 0);
                        int level = intent.getIntExtra(EXTRA_VALUE_OR_LEVEL, 0);
                        result = serverData.changeSection(zone, level);
                        break;
                    case CHECK_CONNECTION:
                        result = serverData.checkConnection();
                        break;
                    case UPDATE_IN_DOC:
                        String updateList = intent.getStringExtra(EXTRA_DOC_LIST);
                        result = serverData.updateDoc(updateList);
                        break;
                    case DELETE_IN_DOC:
                        String deleteList = intent.getStringExtra(EXTRA_DOC_LIST);
                        result = serverData.deleteDoc(deleteList);
                        break;
                    default:
                        break;
                }
                sendResultSuccess(result, queryType);
            } catch (CheckConnectionException e) {
                sendResultError(e.getMessage());
            }
        }
    }

    private void sendResultSuccess(HashMap<String, Integer> result, int queryType) {
        Intent resultIntent = new Intent(QUERY_SUCCESSFUL)
                .putExtra(EXTRA_RESULT_MAP, result)
                .putExtra(EXTRA_QUERY_TYPE, queryType);
        System.out.println("SEND RESULT MAP");

        if (result == null)
            System.out.println("RESULT MAP IS NULL");

        LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
    }

    private void sendResultError(String errorMessage) {
        Intent resultIntent = new Intent(QUERY_ERROR)
                .putExtra(EXTRA_ERROR_MESSAGE, errorMessage);

        System.out.println("SEND ERROR");

        LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
    }
}
