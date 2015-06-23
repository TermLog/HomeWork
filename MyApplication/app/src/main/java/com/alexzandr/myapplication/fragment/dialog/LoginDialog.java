package com.alexzandr.myapplication.fragment.dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupMenu;

import com.alexzandr.myapplication.sql.DataBaseTask;
import com.alexzandr.myapplication.sql.QueryToServer;
import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.application.Singleton;

import java.util.HashMap;

/**
 * Created by anekrasov on 03.06.15.
 */
public class LoginDialog extends DialogFragment implements OnClickListener,
        EnterIpDialog.EnterIpDialogInteractionListener, ErrorShowDialog.OnShowErrors,
        Parcelable{

    private LoginDialogInteractionListener mListener;
    private Activity mActivity;
    private EditText mEditTextUser;
    private EditText mEditTextPassword;
    private Button mChoiceServerButton;
    private DialogFragment mDialogOtherIp;
    private ProgressDialog mProgressDialog;
    private ErrorShowDialog mErrorDialog;
    private SharedPreferences mPreferences;
    private int mServerId = SERVER_DEFAULT;
    private String mServerIp;

    public static final String HOME_IP = "192.168.1.105";
    public static final String WORK_IP = "10.100.6.15";
    public static final String KEY_FOR_SERVER_ID = "ServerId";
    public static final String KEY_FOR_SERVER_IP = "ServerIp";
    private static final int SERVER_DEFAULT = 0;
    private static final int SERVER_HOME = 1;
    private static final int SERVER_WORK = 2;
    private static final int SERVER_OTHER = 3;
    private static final boolean REMEMBER_NOT_ACTIVE = false;
    private static final boolean REMEMBER_IS_ACTIVE = true;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        mActivity = activity;
        try {
            mListener = (LoginDialogInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement " + LoginDialogInteractionListener.class.getName());
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
//        setRetainInstance(true);
        setCancelable(false);

        if (savedInstanceState != null) {
            mServerId = savedInstanceState.getInt(KEY_FOR_SERVER_ID);
            mServerIp = savedInstanceState.getString(KEY_FOR_SERVER_IP);
        }

        mDialogOtherIp = new EnterIpDialog();
        mErrorDialog = new ErrorShowDialog();
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setTitle(R.string.progressBar_title);
        mProgressDialog.setMessage(getText(R.string.progressBar_massage));
        mProgressDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_login, null);

        mEditTextUser = (EditText) view.findViewById(R.id.login_editUser);
        mEditTextPassword = (EditText) view.findViewById(R.id.login_editPassword);
        mChoiceServerButton = (Button) view.findViewById(R.id.login_buttonChoice);
        mChoiceServerButton.setOnClickListener(this);

        view.findViewById(R.id.login_buttonOk).setOnClickListener(this);
        view.findViewById(R.id.login_buttonCancel).setOnClickListener(this);
        view.findViewById(R.id.login_checkBox_remember).setOnClickListener(this);

        mPreferences = mActivity.getSharedPreferences(getString(R.string.remember_preference_name), Context.MODE_PRIVATE);
        if (mPreferences.getBoolean(getString(R.string.remember_preference_key_is_active), REMEMBER_NOT_ACTIVE)){
            mEditTextUser.setText(mPreferences.getString(getString(R.string.remember_preference_key_user), ""));
            mEditTextPassword.setText(mPreferences.getString(getString(R.string.remember_preference_key_password), ""));
            CheckBox cb = (CheckBox) view.findViewById(R.id.login_checkBox_remember);
            cb.setChecked(true);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_FOR_SERVER_ID, mServerId);
        outState.putString(KEY_FOR_SERVER_IP, mServerIp);
    }


    public void serverChoice(){
        PopupMenu popupMenu = new PopupMenu(mActivity, mChoiceServerButton);
        popupMenu.inflate(R.menu.popup_menu_login);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.login_popup_home:
                        onServerChosen(SERVER_HOME, HOME_IP);
                        return true;
                    case R.id.login_popup_work:
                        onServerChosen(SERVER_WORK, WORK_IP);
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
        onServerChosen(SERVER_OTHER, serverIp);
    }

    public void onServerChosen(int serverId, String serverIp){
        switch (serverId){
            case SERVER_HOME:
                mChoiceServerButton.setText(R.string.serverName_home);
                break;
            case SERVER_WORK:
                mChoiceServerButton.setText(R.string.serverName_work);
                break;
            case SERVER_OTHER:
                mChoiceServerButton.setText(serverIp);
                break;
            default: break;
        }
        mServerId = serverId;
        mServerIp = serverIp;
        mChoiceServerButton.setTextColor(getResources().getColor(R.color.text_blue));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_buttonOk:
                showMainMenu();
                break;
            case R.id.login_buttonCancel:
                this.dismiss();
                mActivity.finish();
                break;
            case R.id.login_checkBox_remember:
                onClickRemember(v);
                break;
            case R.id.login_buttonChoice:
                serverChoice();
                break;
            default: break;
        }
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
        if (!mErrorDialog.isAdded()) {
            Bundle errorMassage = new Bundle();
            errorMassage.putString(ErrorShowDialog.KEY_FOR_ERROR, errorText);
            mErrorDialog.setArguments(errorMassage);
            mErrorDialog.show(getFragmentManager(), "ErrorDialog");
        }
    }

    private void createConnection(){
        QueryToServer mQueryToServer = new QueryToServer(mServerIp, mEditTextUser.getText().toString(), mEditTextPassword.getText().toString());
        Singleton.setQuery(mQueryToServer);
        InnerTask task = new InnerTask();
        task.execute(DataBaseTask.CHECK_CONNECTION);
    }

    public void rememberMe() {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (mPreferences.getBoolean(getString(R.string.remember_preference_key_is_active), REMEMBER_NOT_ACTIVE)){
            editor.putString(getString(R.string.remember_preference_key_user), mEditTextUser.getText().toString());
            editor.putString(getString(R.string.remember_preference_key_password), mEditTextPassword.getText().toString());
            editor.apply();
        } else {
            editor.clear().apply();
        }
    }

    public void onClickRemember(View view) {
        CheckBox cb = (CheckBox) view;
        if (!cb.isChecked()){
            mPreferences.edit().clear().apply();
            mPreferences.edit().putBoolean(getString(R.string.remember_preference_key_is_active), REMEMBER_NOT_ACTIVE).apply();
        } else {
            mPreferences.edit().putBoolean(getString(R.string.remember_preference_key_is_active), REMEMBER_IS_ACTIVE).apply();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

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

                rememberMe();
                Singleton.setPreferencesName(mEditTextUser.getText().toString());
                mListener.logIn();
                LoginDialog.this.dismiss();

            } else {

                String errorMassage = exception.getMessage();

                switch (mServerId){
                    case SERVER_HOME:
                        errorMassage = errorMassage + getString(R.string.serverName_home);
                        break;
                    case SERVER_WORK:
                        errorMassage = errorMassage + getString(R.string.serverName_work);
                        break;
                    case SERVER_OTHER:
                        errorMassage = errorMassage + mServerIp;
                        break;
                    default: break;
                }

                showError(errorMassage);
            }
        }
    }

    public interface LoginDialogInteractionListener {
        void logIn();
    }
}
