package com.alexzandr.myapplication;

import android.content.Context;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.sql.*;
import java.util.HashMap;

/**
 * Created by AlexZandR on 21.02.2015.
 */
public class QueryToServer {
    private String mParam1;
    private String mParam2;
    private String mConnectionUrl;
    private String mServerIp;
    private Connection mConnection = null;
    private Statement mStatement = null;
    private ResultSet mResultSet = null;

    private final static String JTDS_CLASS_NAME = "net.sourceforge.jtds.jdbc.Driver";
    private final static String MAP_KEY_COLUMN = "mapKey";
    private final static String MAP_VALUE_COLUMN = "mapValue";
    private final static int PORT = 1433;
    private final static int CONNECTION_TIMEOUT_MS = 5 * 1000;

    public QueryToServer(String serverIP, String param1, String param2){
        mServerIp = serverIP;
        mConnectionUrl = "jdbc:jtds:sqlserver://" + mServerIp + ":1433/PRD1";
        mParam1 = param1;
        mParam2 = param2;
    }

    public HashMap<String, Integer> getAllData(){
        String queryText = "exec dbo.proc_Alex_GetAllData";
        return getResult(queryText);
    }

    public HashMap<String, Integer> changeSection(int zone, int level){
        String queryText = "exec dbo.proc_Alex_ChangeSection @zone = '" + zone + "', @level = '" + level + "'";
        return getResult(queryText);
    }

    public HashMap<String, Integer> changeZoneLevel(int type, int value){
        String queryText = "exec dbo.proc_Alex_ChangeZoneLevel @type = '" + type + "', @value = '" + value + "'";
        return getResult(queryText);
    }

    public HashMap<String, Integer> updateDoc(String docList){
        String queryText = "exec dbo.proc_Alex_UpdateDocument @rec = '" + docList + "'";
        return getResult(queryText);
    }

    public HashMap<String, Integer> deleteDoc(String docList){
        String queryText = "exec dbo.proc_Alex_DeleteQuote @rec = '" + docList + "'";
        return getResult(queryText);
    }

    public HashMap<String, Integer> checkConnection() {
        CheckConnectionException exception;
        Context context = GlobalAccess.getApp();

        try {
            isReachableServer();
            Class.forName(JTDS_CLASS_NAME);
            mConnection = DriverManager.getConnection(mConnectionUrl, mParam1, mParam2);
            return new HashMap<>();
        } catch (CheckConnectionException connectException){

            exception =  new CheckConnectionException(context.getResources().getString(R.string.login_error_msg_disconnect_from_server));

        } catch (SocketTimeoutException timeoutException){

            exception = new CheckConnectionException(context.getResources().getString(R.string.login_error_msg_wrong_server));

        } catch (SQLException sqlException){

            exception =  new CheckConnectionException(context.getResources().getString(R.string.login_error_msg_wrong_user_password));

        } catch (Exception e){

            exception =  new CheckConnectionException(e.getMessage());

        } finally {
            if (mConnection != null) try { mConnection.close(); } catch(Exception e) {e.printStackTrace(); }
        }

        throw exception;
    }

    private void isReachableServer() throws IOException {
        Socket socket = new Socket();
        SocketAddress address = new InetSocketAddress(mServerIp, PORT);
        socket.connect(address, CONNECTION_TIMEOUT_MS);

        if (!(socket.isConnected())) {
            throw new CheckConnectionException("Socket not connected");
        }

        try{ socket.close(); }catch (Exception e){ e.printStackTrace(); }
    }

    private  HashMap<String, Integer> getResult(String text){

        HashMap<String, Integer> resultMap = checkConnection();

        try{
            Class.forName(JTDS_CLASS_NAME);
            mConnection = DriverManager.getConnection(mConnectionUrl, mParam1, mParam2);
            mStatement = mConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            mResultSet = mStatement.executeQuery(text);
            while(mResultSet.next()){
                resultMap.put(mResultSet.getString(MAP_KEY_COLUMN), mResultSet.getInt(MAP_VALUE_COLUMN));
            }
            return resultMap;
        }catch (Exception e){
            e.printStackTrace();
            throw new CheckConnectionException(e.getMessage());
        } finally {
            if (mResultSet != null) try { mResultSet.close(); } catch(Exception e) {e.printStackTrace(); }
            if (mStatement != null) try { mStatement.close(); } catch(Exception e) {e.printStackTrace(); }
            if (mConnection != null) try { mConnection.close(); } catch(Exception e) {e.printStackTrace(); }
        }
    }
}
