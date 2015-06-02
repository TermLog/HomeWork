package com.alexzandr.myapplication.fragment.dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.alexzandr.myapplication.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by anekrasov on 26.02.15.
 */
public class EnterIpDialog extends DialogFragment implements OnClickListener {

    private EditText mEditTextIp;
    private EnterIpDialogInteractionListener mActivity;
    private static final String IP_ADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        try {
            mActivity = (EnterIpDialogInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement " + EnterIpDialogInteractionListener.class.getName());
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_enter_ip, null);

        view.findViewById(R.id.dialog_buttonOk).setOnClickListener(this);
        view.findViewById(R.id.dialog_buttonCancel).setOnClickListener(this);

        mEditTextIp = (EditText) view.findViewById(R.id.dialog_editText);
        setCancelable(false);
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        getDialog().setTitle(R.string.dialog_title);
        if (!TextUtils.isEmpty(mEditTextIp.getText().toString())) {
            mEditTextIp.selectAll();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.dialog_buttonOk:
                buttonOkClick();
                break;
            case R.id.dialog_buttonCancel:
                dismiss();
                break;
            default:break;
        }
    }

    void buttonOkClick(){
        String valueIp = mEditTextIp.getText().toString();
        if (TextUtils.isEmpty(valueIp)) {
            mEditTextIp.setHint(R.string.dialog_editText_hint_noIp);
            mEditTextIp.requestFocus();
        }else {
            Pattern pattern = Pattern.compile(IP_ADDRESS_PATTERN);
            Matcher matcher = pattern.matcher(valueIp);
            if (matcher.matches()){
                mActivity.onServerChosen(valueIp);
                dismiss();
            }else {
                getDialog().setTitle(R.string.dialog_title_wrongIp);
                mEditTextIp.selectAll();
                mEditTextIp.requestFocus();
            }
        }
    }



    public interface EnterIpDialogInteractionListener {
        public void onServerChosen(String serverIp);
    }
}
