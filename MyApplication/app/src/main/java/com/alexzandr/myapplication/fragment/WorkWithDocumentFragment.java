package com.alexzandr.myapplication.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alexzandr.myapplication.DataBaseTask;
import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.fragment.dialog.ErrorShowDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anekrasov on 02.06.15.
 */
public class WorkWithDocumentFragment extends WarehouseFragment {

    public static final int UPDATE_ACTIVITY = 1;
    public static final int DELETE_ACTIVITY = 2;
    public static final int DOC_NOT_EXISTS = 0;
    public static final int DOC_LENGTH = 10;
    public static final String ARG_TYPE_KEY = "type";

    private EditText mEditTextDocList;
    private CharSequence mEditTextValue;
    private TextView mTextViewLabel;
    private Button mButtonAction;
    private int mType;
    private ErrorShowDialog mErrorShowDialog;

    public WorkWithDocumentFragment(){
    }

    public static WorkWithDocumentFragment newInstance(int type){
        WorkWithDocumentFragment instance = new WorkWithDocumentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE_KEY, type);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (mEditTextDocList != null) {
            mEditTextValue = mEditTextDocList.getText();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt(ARG_TYPE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_work_with_document, container, false);

        mButtonAction = (Button) view.findViewById(R.id.doc_buttonAction);
        mButtonAction.setOnClickListener(this);

        if (isPortOrientation()) {
            view.findViewById(R.id.doc_buttonBack).setOnClickListener(this);
        }

        mEditTextDocList = (EditText) view.findViewById(R.id.doc_editText);
        mTextViewLabel = (TextView) view.findViewById(R.id.doc_textView);
        mErrorShowDialog = new ErrorShowDialog();

        if (isPortOrientation()) {
            if (mType == DELETE_ACTIVITY) {
                mActivity.setTitle(R.string.title_activity_delete_document);
            } else {
                mActivity.setTitle(R.string.title_activity_update_document);
            }
        }
        doAction();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mEditTextDocList != null) {
            if (TextUtils.isEmpty(mEditTextDocList.getText()) && !TextUtils.isEmpty(mEditTextValue)) {
                mEditTextDocList.setText(mEditTextValue);
            }
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.doc_buttonBack:
                mActivity.finish();
                break;
            case R.id.doc_buttonAction:
                if (mEditTextDocList.getText().length() >= DOC_LENGTH) {
                    String buttonText = ((Button) view).getText().toString();

                    if (buttonText.equals(getText(R.string.doc_buttonUpdate))) {
                        doAction(UPDATE_ACTIVITY);
                    }
                    else if (buttonText.equals(getText(R.string.doc_buttonDelete))) {
                        doAction(DELETE_ACTIVITY);
                    }
                    else {
                        doAction();
                    }
                } else if (TextUtils.isEmpty(mEditTextDocList.getText())){
                    mEditTextDocList.append(getText(R.string.doc_editText_no_docNumber));
                    mEditTextDocList.selectAll();
                } else {
                    mEditTextDocList.append(getText(R.string.doc_editText_wrong_docNumber));
                    mEditTextDocList.selectAll();
                }
        }
    }

    private void doAction() {
        if (mType == DELETE_ACTIVITY) {
            mButtonAction.setText(R.string.doc_buttonDelete);
        } else {
            mButtonAction.setText(R.string.doc_buttonUpdate);
        }

        mEditTextDocList.setVisibility(View.VISIBLE);
        mTextViewLabel.setText(R.string.doc_textView_text);
    }

    private void doAction(int actionType){
        switch (actionType){
            case UPDATE_ACTIVITY:
                workWithDoc(DataBaseTask.UPDATE_IN_DOC);
                break;
            case DELETE_ACTIVITY:
                workWithDoc(DataBaseTask.DELETE_IN_DOC);
                break;
            default:break;
        }
    }

    private void workWithDoc(int type){
        mButtonAction.setText(R.string.doc_buttonRepeat);
        mEditTextDocList.setVisibility(View.GONE);
        DataBaseTask task = new DataBaseTask();
        HashMap<String, Integer> resultMap;
        task.procedureParamDocList = stringInOneLine(mEditTextDocList.getText().toString());
        try {
            task.execute(type);
            resultMap = task.get();

            if (task.exception != null){
                throw task.exception;
            }

            if (resultMap != null) {
                StringBuilder stringBuilder = new StringBuilder("");

                for (Map.Entry<String, Integer> entry : resultMap.entrySet()) {
                    String docStatus = "";
                    switch (entry.getValue()){
                        case DOC_NOT_EXISTS:
                            docStatus = getString(R.string.doc_editText_wrong_docNumber);
                            break;
                        case UPDATE_ACTIVITY:
                            docStatus = getString(R.string.doc_editText_update_docNumber);
                            break;
                        case DELETE_ACTIVITY:
                            docStatus = getString(R.string.doc_editText_delete_docNumber);
                            break;
                        default:break;
                    }
                    stringBuilder.append(entry.getKey());
                    stringBuilder.append(docStatus);
                    stringBuilder.append("\n");
                }

                mTextViewLabel.setText(stringBuilder);

            } else {
                mTextViewLabel.setText(R.string.doc_textView_error_in_DB);
            }
        }catch (Exception e){
            showError(e.getMessage());
        }

    }

    private String stringInOneLine(String resultString){
        return resultString.replace("\n", ",").replace(" ", "");
    }

    public void showError(String errorText) {
        Bundle errorMassage = new Bundle();
        errorMassage.putString(ErrorShowDialog.KEY_FOR_ERROR, errorText);
        mErrorShowDialog.setArguments(errorMassage);
        mErrorShowDialog.show(getFragmentManager(), "ErrorDialog");
    }
}