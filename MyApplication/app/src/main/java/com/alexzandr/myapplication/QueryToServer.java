package com.alexzandr.myapplication;

import android.os.AsyncTask;

import java.sql.*;
//import com.microsoft.sqlserver.jdbc.SQLServerResultSet;

/**
 * Created by AlexZandR on 21.02.2015.
 */
public class QueryToServer {
    private final static String USER_NAME = "sa";
    private final static String USER_PASS = "sql";
    String connectionUrl = "jdbc:jtds:sqlserver://192.168.1.104:1433/PRD1";
    Connection cn = null;
    Statement st = null;
    ResultSet rs = null;

    public int getLevelCount(){
        StringBuilder queryText = new StringBuilder("select count(distinct(substring(l.loc, 2, 2))) 'levelCount'\n");
        queryText.append("from prd1.wh1.loc l (nolock)\n");
        queryText.append("where l.loc like 'P[0,1][0-9]%'\n");
        queryText.append("and l.locationtype = 'SPEED-PICK'\n");
        queryText.append("and l.locationflag = 'NONE'\n");


        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        cn = DriverManager.getConnection(connectionUrl, USER_NAME, USER_PASS);
                        System.out.println("cn on");
                    } catch (Exception e) {
                        System.out.println("cn off");
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            if(cn == null) System.out.println("cn is null");

            st = cn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            rs = st.executeQuery(queryText.toString());
            rs.next();
            return rs.getInt("levelCount");
        }catch (Exception e){
            e.printStackTrace();
            return -10;
        } finally {
            if (rs != null) try { rs.close(); } catch(Exception e) {e.printStackTrace(); }
            if (st != null) try { st.close(); } catch(Exception e) {e.printStackTrace(); }
            if (cn != null) try { cn.close(); } catch(Exception e) {e.printStackTrace(); }
        }

    }

    /*Thread thread = new Thread(new Runnable(){
        @Override
        public void run() {
            try {
                //Your code goes here
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });*/
}
