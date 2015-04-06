package com.alexzandr.myapplication;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;

import java.util.HashMap;

public class LoginActivity extends ActionBarActivity implements EnterIpDialog.OnMadeServerChoice, ErrorShowDialog.OnShowErrors {
	
    private EditText mUser;
    private EditText mPassword;
    private Button mChoiceServerButton;
    private DialogFragment mDialogOtherIP;
    private ProgressDialog mProgressDialog;
    private ErrorShowDialog mErrorDialog;
    private int mServerId = SERVER_DEFAULT;
    private String mServerIp;

    public static final String HOME_IP = "192.168.1.104";
    public static final String WORK_IP = "10.100.6.15";
    private static final int SERVER_DEFAULT = 0;

    public static QueryToServer sQueryToServer;
    private static Context sContext;
    private Animation mButtonAnimation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sContext = getApplicationContext();
        mUser = (EditText) findViewById(R.id.login_editUser);
        mPassword = (EditText) findViewById(R.id.login_editPassword);
        mChoiceServerButton = (Button) findViewById(R.id.login_buttonChoice);
        mDialogOtherIP = new EnterIpDialog();
        mErrorDialog = new ErrorShowDialog();
        mErrorDialog.setCancelable(false);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(R.string.progressBar_title);
        mProgressDialog.setMessage(getText(R.string.progressBar_massage));
        mProgressDialog.setCancelable(false);
        mButtonAnimation = AnimationUtils.loadAnimation(this, R.anim.button_scale_animation);
    }

    private void isEmptyLoginForms(){

        String userName = mUser.getText().toString();
        String passwordValue = mPassword.getText().toString();
        String mErrorText;

        if (TextUtils.isEmpty(userName)){
            mErrorText = getString(R.string.login_error_msg_no_user);
            mUser.setError(Html.fromHtml("<h2>Ошибка:</h2>" + mErrorText));
            throw new LogonException(mErrorText);
        }

        if (TextUtils.isEmpty(passwordValue)){
            mErrorText = getString(R.string.login_error_msg_no_password);
            mPassword.setError(Html.fromHtml("<h2>Ошибка:</h2>" + mErrorText));
            throw new LogonException(mErrorText);
        }
    }

    private void isEmptyServer(){
        if (mServerId == SERVER_DEFAULT){
            throw new LogonException(getString(R.string.login_error_msg_no_server));
        }
    }

    @Override
    public void showError(String errorText) {
        Bundle errorMassage = new Bundle();
        errorMassage.putString(ErrorShowDialog.KEY_FOR_ERROR, errorText);
        mErrorDialog.setArguments(errorMassage);
        mErrorDialog.show(getFragmentManager(), "ErrorDialog");
    }

    void showMainMenu(){
        try {
            isEmptyLoginForms();
            isEmptyServer();
            createConnection();
        } catch (LogonException e){
            showError(e.getMessage());
        }
    }

    public void enterClick(View view) {
        view.startAnimation(mButtonAnimation);
        showMainMenu();

    }
    public void cancelClick(View view){
        view.startAnimation(mButtonAnimation);
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
                        makeServerChoice(R.string.serverName_home, HOME_IP);
                        return true;
                    case R.id.login_popup_work:
                        makeServerChoice(R.string.serverName_work, WORK_IP);
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

    @Override
    public void makeServerChoice(String serverIp){
        mServerId = R.string.serverName_other;
        mServerIp = serverIp;
        mChoiceServerButton.setText(serverIp);
        mChoiceServerButton.setTextColor(getResources().getColor(R.color.text_blue));
    }

    void makeServerChoice(int serverId, String serverIp){
        mServerId = serverId;
        mServerIp = serverIp;
        mChoiceServerButton.setText(serverId);
        mChoiceServerButton.setTextColor(getResources().getColor(R.color.text_blue));
    }

    private void createConnection(){
        sQueryToServer = new QueryToServer(mServerIp, mUser.getText().toString(), mPassword.getText().toString());
        InnerTask task = new InnerTask();
        task.execute(DataBaseTask.CHECK_CONNECTION);
    }

    public static Context getApp(){
        return sContext;
    }

    private class InnerTask extends DataBaseTask{

        @Override
        protected void onPreExecute(){
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(HashMap<String, Integer> result) {

            mProgressDialog.dismiss();

            if (exception == null){

                Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();

            } else {

                String errorMassage = exception.getMessage();

                if (mServerId != R.string.serverName_other) {
                    errorMassage = errorMassage + getString(mServerId);
                } else {
                    errorMassage = errorMassage + mServerIp;
                }

                showError(errorMassage);
            }
        }
    }
}
