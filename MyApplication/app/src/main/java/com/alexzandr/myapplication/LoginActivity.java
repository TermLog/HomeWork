package com.alexzandr.myapplication;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LoginActivity extends ActionBarActivity implements EnterIpDialog.OnMadeServerChoice {
    private EditText mUser;
    private EditText mPassword;
    private TextView mDescr;
    private Button mChoiceServerButton;
    private DialogFragment mDialogOtherIP;
    private int mServerId = R.string.serverName_default;
    private String mServerIp;

    public static final int NO_ERROR = 0;
    public static final int ERROR_EMPTY_USER = 1;
    public static final int ERROR_EMPTY_PASSWORD = 2;
    public static final int ERROR_EMPTY_BOTH = 3;
    public static final int ERROR_EMPTY_SERVER = 4;
    public static final int ERROR_WRONG_SERVER = 5;
    public static final int ERROR_WRONG_LOGIN_PASSWORD = 6;
    public static final int ERROR_UNAVAILABLE_MSSQL = 7;

    public static QueryToServer db;
    public static int errorType = NO_ERROR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUser = (EditText) findViewById(R.id.login_editUser);
        mPassword = (EditText) findViewById(R.id.login_editPassword);
        mDescr = (TextView) findViewById(R.id.login_textDescr);
        mChoiceServerButton = (Button) findViewById(R.id.login_buttonChoice);
        mDialogOtherIP = new EnterIpDialog();

        // interesting decision :) I can't say this is wrong
        // but I'd implement this another way
        mUser.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setDefaultEditText(mUser, R.string.login_textDescr_user);
                }
                return false;
            }
        });
        mUser.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setDefaultEditText(mUser, R.string.login_textDescr_user);
                }
            }
        });
        mPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setDefaultEditText(mPassword, R.string.login_textDescr_password);
                }
                return false;
            }
        });
        mPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setDefaultEditText(mPassword, R.string.login_textDescr_password);
                }
            }
        });

    }

    private boolean isEmptyLoginForms(){
        errorType = NO_ERROR;
        String userName = mUser.getText().toString();
        String passwordValue = mPassword.getText().toString();

        if(TextUtils.isEmpty(userName)){
            setErrorStyleEditText(mUser);
            errorType = ERROR_EMPTY_USER;
        }
        if(TextUtils.isEmpty(passwordValue)){
            setErrorStyleEditText(mPassword);
            errorType += ERROR_EMPTY_PASSWORD;
        }
        return errorType != NO_ERROR;
    }

    private boolean isEmptyServer(){
        if (mServerId == R.string.serverName_default) {
            errorType = ERROR_EMPTY_SERVER;
        }
        return errorType != NO_ERROR;
    }

    private void showMainMenu(){
        if (isEmptyLoginForms() || isEmptyServer() || !createConnection()){
            mDescr.setTextColor(Color.RED);
            switch (errorType) {
                case ERROR_EMPTY_USER:
                    mDescr.setText(R.string.login_textDescr_no_user);
                    break;
                case ERROR_EMPTY_PASSWORD:
                    mDescr.setText(R.string.login_textDescr_no_password);
                    break;
                case ERROR_EMPTY_BOTH:
                    mDescr.setText(R.string.login_textDescr_no_user_password);
                    break;
                case ERROR_EMPTY_SERVER:
                    mDescr.setText(R.string.login_textDescr_no_server);
                    mChoiceServerButton.callOnClick();
                    break;
                case ERROR_WRONG_SERVER:
                    if (mServerId != R.string.serverName_other) {
                        mDescr.setText(getString(R.string.login_textDescr_wrong_server) + getString(mServerId) + "!");
                    } else {
                        mDescr.setText(getString(R.string.login_textDescr_wrong_server) + mServerIp + "!");
                    }
                    break;
                case ERROR_WRONG_LOGIN_PASSWORD:
                    setErrorStyleEditText(mUser);
                    setErrorStyleEditText(mPassword);
                    if (mServerId != R.string.serverName_other) {
                        mDescr.setText(getString(R.string.login_textDescr_wrong_user_password) + getString(mServerId));
                    } else {
                        mDescr.setText(getString(R.string.login_textDescr_wrong_user_password) + mServerIp);
                    }
                    break;
                case ERROR_UNAVAILABLE_MSSQL:
                    if (mServerId != R.string.serverName_other) {
                        mDescr.setText(getString(R.string.login_textDescr_unavailable_server) + getString(mServerId));
                    } else {
                        mDescr.setText(getString(R.string.login_textDescr_unavailable_server) + mServerIp);
                    }
                    break;
                default: break;
            }
            errorType = NO_ERROR;
        } else {
            Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
            startActivity(intent);
            mDescr.setText(R.string.login_textDescr);
            mUser.setText(null);
            mPassword.setText(null);
        }
    }

    public void enterClick(View view) {
        showMainMenu();
    }
    public void cancelClick(View view){
        this.finish();
    }

    public void serverChoice(View view){
        PopupMenu popupMenu = new PopupMenu(this, mChoiceServerButton);
        popupMenu.inflate(R.menu.popup_menu_login);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.login_popup_home:
                        makeServerChoice(R.string.serverName_home, getString(R.string.serverIp_home));
                        return true;
                    case R.id.login_popup_work:
                        makeServerChoice(R.string.serverName_work, getString(R.string.serverIp_work));
                        return true;
                    case R.id.login_popup_other:
                        mDialogOtherIP.show(getFragmentManager(), "myDialog");
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    public void setDefaultEditText(EditText view, int stringResId) {
        mDescr.setText(stringResId);
        mDescr.setTextColor(getResources().getColor(R.color.text_blue));
        view.setBackgroundResource(R.color.main_EditBackground_Default);
        view.setHintTextColor(getResources().getColor(R.color.main_EditHint_Default));
    }

    // rename method
    public void setErrorStyleEditText(EditText view) {
        view.setBackgroundResource(R.color.main_EditBackground_Error);
        view.setHintTextColor(getResources().getColor(R.color.main_EditHint_Error));
    }

    @Override
    public void makeServerChoice(String serverIp){
        mServerId = R.string.serverName_other;
        mServerIp = serverIp;
        mChoiceServerButton.setText(serverIp);
        mChoiceServerButton.setTextColor(getResources().getColor(R.color.text_blue));
    }

    public void makeServerChoice(int serverId, String serverIp){
        mServerId = serverId;
        mServerIp = serverIp;
        mChoiceServerButton.setText(serverId);
        mChoiceServerButton.setTextColor(getResources().getColor(R.color.text_blue));
    }

    private boolean createConnection(){
        db = new QueryToServer(mServerIp, mUser.getText().toString(), mPassword.getText().toString());
        DataBaseTask dbt = new DataBaseTask();
        try{
            dbt.execute(DataBaseTask.CHECK);
            return (dbt.get(30, TimeUnit.SECONDS) != null);
        } catch(TimeoutException timeoutException){
            timeoutException.printStackTrace();
            dbt.cancel(true);
            errorType = ERROR_UNAVAILABLE_MSSQL;
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
