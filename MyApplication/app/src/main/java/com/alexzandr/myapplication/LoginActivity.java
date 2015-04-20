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

public class LoginActivity extends ActionBarActivity {
    private EditText user;
    private EditText password;
    private TextView descr;
    private Button choiceServerButton;
    private DialogFragment dialogOtherIP;
    // R.string.serverName_default is already a public static int field, do not duplicate it
    public static int mServerId = R.string.serverName_default;
    public static String mServerIp;
    public static QueryToServer db;
    public static final int NO_ERROR = 0;
    public static final int ERROR_EMPTY_USER = 1;
    public static final int ERROR_EMPTY_PASSWORD = 2;
    public static final int ERROR_EMPTY_BOTH = 3;
    public static final int ERROR_EMPTY_SERVER = 4;
    public static final int ERROR_WRONG_SERVER = 5;
    public static final int ERROR_WRONG_LOGIN_PASSWORD = 6;
    public static final int ERROR_UNAVAILABLE_MSSQL = 7;
    public static int errorType = NO_ERROR;
    // the same as mServerId
    static final int DEFAULT_USER = R.string.login_textDescr_user;
    static final int DEFAULT_PASSWORD = R.string.login_textDescr_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user = (EditText) findViewById(R.id.login_editUser);
        password = (EditText) findViewById(R.id.login_editPassword);
        descr = (TextView) findViewById(R.id.login_textDescr);
        choiceServerButton = (Button) findViewById(R.id.login_buttonChoice);
        dialogOtherIP = new EnterIpDialog();

        // interesting decision :) I can't say this is wrong
        // but I'd implement this another way
        user.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setDefaultEditText(user, DEFAULT_USER);
                }
                return false;
            }
        });
        user.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setDefaultEditText(user, DEFAULT_USER);
                }
            }
        });
        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setDefaultEditText(password, DEFAULT_PASSWORD);
                }
                return false;
            }
        });
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setDefaultEditText(password, DEFAULT_PASSWORD);
                }
            }
        });

    }

    private boolean isEmptyLoginForms(){
        errorType = NO_ERROR;
        String userName = user.getText().toString();
        String passwordValue = password.getText().toString();

        if(TextUtils.isEmpty(userName)){
            setErrorStyleEditText(user);
            errorType = ERROR_EMPTY_USER;
        }
        if(TextUtils.isEmpty(passwordValue)){
            setErrorStyleEditText(password);
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
            descr.setTextColor(Color.RED);
            switch (errorType) {
                case ERROR_EMPTY_USER:
                    descr.setText("Не указано имя пользователя!");
                    break;
                case ERROR_EMPTY_PASSWORD:
                    descr.setText("Не указан пароль!");
                    break;
                case ERROR_EMPTY_BOTH:
                    descr.setText("Не указано имя пользователя и пароль!");
                    break;
                case ERROR_EMPTY_SERVER:
                    descr.setText("Не выбран сервер!");
                    choiceServerButton.callOnClick();
                    break;
                case ERROR_WRONG_SERVER:
                    if (mServerId != R.string.serverName_other) {
                        descr.setText("Не доступен сервер " + getText(mServerId) + "!");
                    } else {
                        descr.setText("Не доступен сервер " + mServerIp + "!");
                    }
                    break;
                case ERROR_WRONG_LOGIN_PASSWORD:
                    setErrorStyleEditText(user);
                    setErrorStyleEditText(password);
                    if (mServerId != R.string.serverName_other) {
                        descr.setText("Не верно указан логин или пароль \n для соединения с MSSQL сервером " + getString(mServerId));
                    } else {
                        descr.setText("Не верно указан логин или пароль \n для соединения с MSSQL сервером " + mServerIp);
                    }
                    break;
                case ERROR_UNAVAILABLE_MSSQL:
                    if (mServerId != R.string.serverName_other) {
                        descr.setText("Не доступен MSSQLSERVER на сервере " + getString(mServerId));
                    } else {
                        descr.setText("Не доступен MSSQLSERVER на сервере " + mServerIp);
                    }
                    break;
                default: break;
            }
            errorType = NO_ERROR;
        } else {
            Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
            startActivity(intent);
            descr.setText(getText(R.string.login_textDescr));
            user.setText(null);
            password.setText(null);
        }
    }

    public void enterClick(View view) {
        showMainMenu();
    }
    public void cancelClick(View view){
        this.finish();
    }

    public void serverChoice(View view){
        PopupMenu popupMenu = new PopupMenu(this, choiceServerButton);
        popupMenu.inflate(R.menu.popup_menu_login);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.login_popup_home:
                        makeServerChoice(R.string.serverName_home, "192.168.1.104");
                        return true;
                    case R.id.login_popup_work:
                        makeServerChoice(R.string.serverName_work, "10.100.6.15");
                        return true;
                    case R.id.login_popup_other:
                        dialogOtherIP.show(getFragmentManager(), "myDialog");
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    public void setDefaultEditText(EditText view, int stringResId) {
        descr.setText(stringResId);
        descr.setTextColor(getResources().getColor(R.color.text_blue));
        view.setBackgroundResource(R.color.main_EditBackground_Default);
        view.setHintTextColor(getResources().getColor(R.color.main_EditHint_Default));
    }

    // rename method
    public void setErrorStyleEditText(EditText view) {
        view.setBackgroundResource(R.color.main_EditBackground_Error);
        view.setHintTextColor(getResources().getColor(R.color.main_EditHint_Error));
    }

    public void makeServerChoice(String serverIp){
        mServerId = R.string.serverName_other;
        mServerIp = serverIp;
        choiceServerButton.setText(serverIp);
        choiceServerButton.setTextColor(getResources().getColor(R.color.text_blue));
    }

    public void makeServerChoice(int serverId, String serverIp){
        mServerId = serverId;
        mServerIp = serverIp;
        choiceServerButton.setText(getText(serverId));
        choiceServerButton.setTextColor(getResources().getColor(R.color.text_blue));
    }

    private boolean createConnection(){
        db = new QueryToServer(mServerIp, user.getText().toString(), password.getText().toString());
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
