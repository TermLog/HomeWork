package com.alexzandr.myapplication;

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
    private static int serverId = 0;
    private static String serverIP;
    public static QueryToServer db;
    static final int EMPTY_USER = 1;
    static final int EMPTY_PASSWORD = 2;
    static final int EMPTY_BOTH = 3;
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

    private void showMainMenu(){
        int errors = 0;
        String userName = user.getText().toString();
        String passwordValue = password.getText().toString();

        if(TextUtils.isEmpty(userName)){
            setErrorStyleEditText(user);
            errors = EMPTY_USER;
        }
        if(TextUtils.isEmpty(passwordValue)){
            setErrorStyleEditText(password);
            if(errors == EMPTY_USER)
                errors = EMPTY_BOTH;
            else
                errors = EMPTY_PASSWORD;
        }

        if(errors != 0){
            descr.setTextColor(Color.RED);
            switch (errors) {
                case EMPTY_USER:
                    descr.setText("Не указано имя пользователя!");
                    break;
                case EMPTY_PASSWORD:
                    descr.setText("Не указан пароль!");
                    break;
                case EMPTY_BOTH:
                    descr.setText("Не указано имя пользователя и пароль!");
                    break;
                default: break;
            }
        }else if (serverId == 0){
            descr.setText("Не выбран сервер!");
            choiceServerButton.callOnClick();
        }else if (createConnection()){
            Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
            startActivity(intent);
            user.setText(null);
            password.setText(null);
        }else{
            descr.setText("Не верно указаны данные для соединения с сервером " + getResources().getString(serverId) + "!\n" + "Введите данные заново.");
            setErrorStyleEditText(user);
            setErrorStyleEditText(password);
        }
    }

    public void enterClick(View view) {
        showMainMenu();
    }
    public void cancelClick(View view){
        this.finish();
    }
    public void serverChoice(View view){
        showPopupMenu(view);
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

    private void showPopupMenu(final View view){
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.popup_menu_login);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.login_popup_home:
                        serverId = R.string.serverName_home;
                        serverIP = "192.168.1.104";
                        ((Button) view).setText(getResources().getText(R.string.serverName_home));
                        ((Button) view).setTextColor(getResources().getColor(R.color.text_blue));
                        return true;
                    case R.id.login_popup_work:
                        serverId = R.string.serverName_work;
                        serverIP = "10.100.6.15";
                        ((Button) view).setText(getResources().getText(R.string.serverName_work));
                        ((Button) view).setTextColor(getResources().getColor(R.color.text_blue));
                        return true;
//                    case R.id.login_popup_other:
//                        serverId = R.string.serverName_other;
//                        ((Button) view).setText(getResources().getText(R.string.serverName_other));
//                        ((Button) view).setTextColor(getResources().getColor(R.color.text_blue));
//                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }
    private boolean createConnection(){
        db = new QueryToServer(serverId, user.getText().toString(), password.getText().toString());
        DataBaseTask dbt = new DataBaseTask();
        try{
            dbt.execute(DataBaseTask.CHECK);
            return (dbt.get(30, TimeUnit.SECONDS) != null);
        } catch(TimeoutException timeoutException){
            timeoutException.printStackTrace();
            dbt.cancel(true);
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
