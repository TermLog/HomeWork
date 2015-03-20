package com.alexzandr.myapplication;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;

import java.util.HashMap;

public class LoginActivity extends ActionBarActivity implements EnterIpDialog.OnMadeServerChoice, ErrorShowDialog.OnShowMainMenu {
	
    private EditText mUser;
    private EditText mPassword;
    private Button mChoiceServerButton;
    private DialogFragment mDialogOtherIP;
    private ProgressDialog mProgressDialog;
    private ErrorShowDialog mErrorDialog;
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
        mChoiceServerButton = (Button) findViewById(R.id.login_buttonChoice);
        mDialogOtherIP = new EnterIpDialog();
        mErrorDialog = new ErrorShowDialog();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(R.string.progressBar_title);
        mProgressDialog.setMessage(getText(R.string.progressBar_massage));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (errorType != NO_ERROR){
                    showError();
                } else {
                    Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                }
            }
        });

        // interesting decision :) I can't say this is wrong
        // but I'd implement this another way
        mUser.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setDefaultEditText(mUser);
                }
                return false;
            }
        });
        mUser.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setDefaultEditText(mUser);
                }
            }
        });
        mPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setDefaultEditText(mPassword);
                }
                return false;
            }
        });
        mPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setDefaultEditText(mPassword);
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
        if (mServerId == R.string.serverName_default && errorType == NO_ERROR) {
            errorType = ERROR_EMPTY_SERVER;
        }
        return errorType != NO_ERROR;
    }

    public void showError(){
        String result ="";
        switch (errorType) {
            case ERROR_EMPTY_USER:
                result = getString(R.string.login_textDescr_no_user);
                break;
            case ERROR_EMPTY_PASSWORD:
                result = getString(R.string.login_textDescr_no_password);
                break;
            case ERROR_EMPTY_BOTH:
                result = getString(R.string.login_textDescr_no_user_password);
                break;
            case ERROR_EMPTY_SERVER:
                result = getString(R.string.login_textDescr_no_server);
                break;
            case ERROR_WRONG_SERVER:
                if (mServerId != R.string.serverName_other) {
                    result = getString(R.string.login_textDescr_wrong_server) + getString(mServerId) + "!";
                } else {
                    result = getString(R.string.login_textDescr_wrong_server) + mServerIp + "!";
                }
                break;
            case ERROR_WRONG_LOGIN_PASSWORD:
                setErrorStyleEditText(mUser);
                setErrorStyleEditText(mPassword);
                if (mServerId != R.string.serverName_other) {
                    result = getString(R.string.login_textDescr_wrong_user_password) + getString(mServerId);
                } else {
                    result = getString(R.string.login_textDescr_wrong_user_password) + mServerIp;
                }
                break;
            case ERROR_UNAVAILABLE_MSSQL:
                if (mServerId != R.string.serverName_other) {
                    result = getString(R.string.login_textDescr_unavailable_server) + getString(mServerId);
                } else {
                    result = getString(R.string.login_textDescr_unavailable_server) + mServerIp;
                }
                break;
            default: break;
        }
        errorType = NO_ERROR;
        Bundle errorMassage = new Bundle();
        errorMassage.putString(ErrorShowDialog.KEY_FOR_ERROR, result);
        mErrorDialog.setArguments(errorMassage);
        mErrorDialog.show(getFragmentManager(), "ErrorDialog");
    }

    @Override
    public void showMainMenu(){
        isEmptyLoginForms();
        isEmptyServer();
        if (errorType == NO_ERROR){
            createConnection();
        } else {
            showError();
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
                        mDialogOtherIP.show(getFragmentManager(), "otherIpDialog");
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    public void setDefaultEditText(EditText view) {
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

    private void createConnection(){
        mProgressDialog.show();
        db = new QueryToServer(mServerIp, mUser.getText().toString(), mPassword.getText().toString());
        InnerTask dbt = new InnerTask();
            dbt.execute(DataBaseTask.CHECK);
    }

    class InnerTask extends DataBaseTask{
        @Override
        protected void onPostExecute(HashMap<String, Integer> result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
        }
    }
}
