package com.alexzandr.myapplication.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alexzandr.myapplication.DataBaseTask;
import com.alexzandr.myapplication.fragment.ErrorShowDialog;
import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.Singleton;

import java.util.HashMap;
import java.util.Map;

public class WorkWithDocumentActivity extends ActionBarActivity implements ErrorShowDialog.OnShowErrors {

    public static final int UPDATE_ACTIVITY = 1;
    public static final int DELETE_ACTIVITY = 2;
    public static final int DOC_NOT_EXISTS = 0;
    public static final int DOC_LENGTH = 10;
    public static final String MAP_KEY = "type";

    private EditText mEditTextDocList;
    private TextView mTextViewLabel;
    private Button mButtonAction;
    private int mActivityType;
    private ErrorShowDialog mErrorShowDialog;
//    private final Animation mScaleAnimationForButton = Singleton.getAnimation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_with_document);

        mButtonAction = (Button) findViewById(R.id.doc_buttonAction);
        mEditTextDocList = (EditText) findViewById(R.id.doc_editText);
        mTextViewLabel = (TextView) findViewById(R.id.doc_textView);
        mActivityType = getIntent().getIntExtra(MAP_KEY, UPDATE_ACTIVITY);
        mErrorShowDialog = new ErrorShowDialog();

        if (mActivityType == DELETE_ACTIVITY) {
            setTitle(R.string.title_activity_delete_document);
        }
        doAction();
    }

    public void onClick(View view){
//        view.startAnimation(mScaleAnimationForButton);
        switch (view.getId()) {
            case R.id.doc_buttonBack:
                finish();
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
        if (mActivityType == DELETE_ACTIVITY) {
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
        HashMap<String, Integer> resultMap = null;
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

    @Override
    public void showError(String errorText) {
        Bundle errorMassage = new Bundle();
        errorMassage.putString(ErrorShowDialog.KEY_FOR_ERROR, errorText);
        mErrorShowDialog.setArguments(errorMassage);
        mErrorShowDialog.show(getFragmentManager(), "ErrorDialog");
    }
}