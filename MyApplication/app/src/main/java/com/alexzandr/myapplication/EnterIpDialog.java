package com.alexzandr.myapplication;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by anekrasov on 26.02.15.
 */
public class EnterIpDialog extends DialogFragment implements OnClickListener {

    private EditText mEditTextIP;
    private static final String IP_ADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_enter_ip, null);
        view.findViewById(R.id.dialog_buttonOk).setOnClickListener(this);
        view.findViewById(R.id.dialog_buttonCancel).setOnClickListener(this);

        mEditTextIP = (EditText) view.findViewById(R.id.dialog_editText);
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        getDialog().setTitle("Укажите IP-адрес сервера");
        if (!TextUtils.isEmpty(mEditTextIP.getText().toString())) {
            mEditTextIP.selectAll();
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

    public void buttonOkClick(){
        String valueIp = mEditTextIP.getText().toString();
        if (TextUtils.isEmpty(valueIp)) {
            mEditTextIP.setHint("Необходимо указать IP-адрес");
            mEditTextIP.requestFocus();
        }else {
            Pattern pattern = Pattern.compile(IP_ADDRESS_PATTERN);
            Matcher matcher = pattern.matcher(valueIp);
            if (matcher.matches()){
                ((LoginActivity)getActivity()).makeServerChoice(valueIp);
                dismiss();
            }else {
                getDialog().setTitle("Не верный формат IP-адреса");
                mEditTextIP.selectAll();
                mEditTextIP.requestFocus();
            }
        }
    }
}