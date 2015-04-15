package com.alexzandr.myapplication;

import android.app.DialogFragment;
import android.app.ProgressDialog;
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

public class LoginActivity extends ActionBarActivity implements EnterIpDialog.EnterIpDialogInteractionListener, ErrorShowDialog.OnShowErrors {
	
    private EditText mEditTextUser;
    private EditText mEditTextPassword;
    private Button mChoiceServerButton;
    private DialogFragment mDialogOtherIp;
    private ProgressDialog mProgressDialog;
    private ErrorShowDialog mErrorDialog;
    private int mServerId = SERVER_DEFAULT;
    private String mServerIp;

    public static final String HOME_IP = "192.168.1.104";
    public static final String WORK_IP = "10.100.6.15";
    private static final int SERVER_DEFAULT = 0;

    private Animation mScaleAnimationForButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEditTextUser = (EditText) findViewById(R.id.login_editUser);
        mEditTextPassword = (EditText) findViewById(R.id.login_editPassword);
        mChoiceServerButton = (Button) findViewById(R.id.login_buttonChoice);
        mDialogOtherIp = new EnterIpDialog();
        mErrorDialog = new ErrorShowDialog();
//        mErrorDialog.setCancelable(false);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(R.string.progressBar_title);
        mProgressDialog.setMessage(getText(R.string.progressBar_massage));
        mProgressDialog.setCancelable(false);
        mScaleAnimationForButton = AnimationUtils.loadAnimation(this, R.anim.button_scale_animation);
    }

    public void serverChoice(View view){
        PopupMenu popupMenu = new PopupMenu(this, mChoiceServerButton);
        popupMenu.inflate(R.menu.popup_menu_login);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.login_popup_home:
                        onServerChosen(R.string.serverName_home, HOME_IP);
                        return true;
                    case R.id.login_popup_work:
                        onServerChosen(R.string.serverName_work, WORK_IP);
                        return true;
                    case R.id.login_popup_other:
                        mDialogOtherIp.show(getFragmentManager(), "otherIpDialog");
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    @Override
    public void onServerChosen(String serverIp){
        onServerChosen(R.string.serverName_other, serverIp);
    }

    void onServerChosen(int serverId, String serverIp){
        if (serverId == R.string.serverName_other){
            mChoiceServerButton.setText(serverIp);
        } else {
            mChoiceServerButton.setText(serverId);
        }
        mServerId = serverId;
        mServerIp = serverIp;
        mChoiceServerButton.setTextColor(getResources().getColor(R.color.text_blue));
    }

    public void enterClick(View view) {
        view.startAnimation(mScaleAnimationForButton);
        showMainMenu();
    }

    public void cancelClick(View view){
        view.startAnimation(mScaleAnimationForButton);
        this.finish();
    }

    void showMainMenu(){
        if (!isEmptyLoginForms()) {
            createConnection();
        }
    }

    private boolean isEmptyLoginForms(){
        String userName = mEditTextUser.getText().toString();
        String passwordValue = mEditTextPassword.getText().toString();
        String ErrorTitle = getString(R.string.login_error_msg_title);
        String ErrorText;

        if (TextUtils.isEmpty(userName)){
            ErrorText = getString(R.string.login_error_msg_no_user);
            mEditTextUser.setError(Html.fromHtml("<h2>" + ErrorTitle + "</h2>" + ErrorText));
            showError(ErrorText);
            return true;
        }

        if (TextUtils.isEmpty(passwordValue)){
            ErrorText = getString(R.string.login_error_msg_no_password);
            mEditTextPassword.setError(Html.fromHtml("<h2>" + ErrorTitle + "</h2>" + ErrorText));
            showError(ErrorText);
            return true;
        }

        if (mServerId == SERVER_DEFAULT){
            showError(getString(R.string.login_error_msg_no_server));
            return true;
        }

        return false;
    }

    @Override
    public void showError(String errorText) {
        Bundle errorMassage = new Bundle();
        errorMassage.putString(ErrorShowDialog.KEY_FOR_ERROR, errorText);
        mErrorDialog.setArguments(errorMassage);
        mErrorDialog.show(getFragmentManager(), "ErrorDialog");
    }

    private void createConnection(){
        QueryToServer mQueryToServer = new QueryToServer(mServerIp, mEditTextUser.getText().toString(), mEditTextPassword.getText().toString());
        Singleton.setQuery(mQueryToServer);
        InnerTask task = new InnerTask();
        task.execute(DataBaseTask.CHECK_CONNECTION);
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
