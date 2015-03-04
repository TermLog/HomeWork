package com.alexzandr.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;

public class WorkWithDocumentActivity extends ActionBarActivity {

    public static final int UPDATE_ACTIVITY = 1;
    public static final int DELETE_ACTIVITY = 2;
    public static final int DOC_NOT_EXISTS = 0;
    public static final int DOC_UPDATED = 1;
    public static final int DOC_DELETED = 2;
    public static final String MAP_KEY = "type";
//    private static final int FOR_UPDATE = R.string.doc_buttonUpdate;
//    private static final int FOR_DELETE = R.string.doc_buttonDelete;
//    private static final int FOR_REPEAT = R.string.doc_buttonRepeat;
    private EditText mDocList;
    private TextView mLabel;
    private Button mButtonAction;
    private int mActivityType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_with_document);

        mButtonAction = (Button) findViewById(R.id.doc_buttonAction);
        mDocList = (EditText) findViewById(R.id.doc_editText);
        mLabel = (TextView) findViewById(R.id.doc_textView);
        mActivityType = getIntent().getIntExtra(MAP_KEY, UPDATE_ACTIVITY);

        if (mActivityType == DELETE_ACTIVITY) {
            setTitle(R.string.title_activity_delete_document);
        }
        doAction(R.string.doc_buttonRepeat);
    }

    public void onClick(View view){

            switch (view.getId()) {
                case R.id.doc_buttonBack:
                    finish();
                    break;
                case R.id.doc_buttonAction:
                    if (mDocList.getText().length() >= 10) {
                        String buttonText = ((Button) view).getText().toString();

                        if (buttonText.equals(getText(R.string.doc_buttonUpdate))) {
                            doAction(R.string.doc_buttonUpdate);
                        }
                        else if (buttonText.equals(getText(R.string.doc_buttonDelete))) {
                            doAction(R.string.doc_buttonDelete);
                        }
                        else {
                            doAction(R.string.doc_buttonRepeat);
                        }
                    } else if (TextUtils.isEmpty(mDocList.getText())){
                        mDocList.append("Не указан номер документа");
                        mDocList.selectAll();
                    } else {
                        mDocList.append(" - неверный номер документа");
                        mDocList.selectAll();
                    }
            }

    }

    private void workWithDoc(int type){
        mButtonAction.setText(getText(R.string.doc_buttonRepeat));
        mDocList.setVisibility(View.GONE);
        DataBaseTask dbt = new DataBaseTask();
        HashMap<String, Integer> map = null;
        dbt.procedureParamDocList = stringInOneLine(mDocList.getText().toString());
        try {
            dbt.execute(type);
            map = dbt.get();
        }catch (Exception e){
            e.printStackTrace();
        }
        if (map != null) {
            String resultText = "";
            StringBuilder stringBuilder = new StringBuilder("");
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String docStatus = "";
                switch (entry.getValue()){
                    case DOC_NOT_EXISTS:
                        docStatus = " - не существует в БД";
                        break;
                    case DOC_UPDATED:
                        docStatus = " - обновлен";
                        break;
                    case DOC_DELETED:
                        docStatus = " - апостроф удален";
                        break;
                    default:break;
                }
                resultText = resultText + entry.getKey() + docStatus + "\n";
                stringBuilder.append(entry.getKey());
                stringBuilder.append(docStatus);
                stringBuilder.append("\n");
            }
            mLabel.setText(stringBuilder);
        } else {
            mLabel.setText("Произошла ошибка при работе с базой данных");
        }
    }

    private void doAction(int actionType){
        switch (actionType){
            case R.string.doc_buttonRepeat:

                if (mActivityType == DELETE_ACTIVITY) {
                    mButtonAction.setText(getText(R.string.doc_buttonDelete));
                }
                else {
                    mButtonAction.setText(getText(R.string.doc_buttonUpdate));
                }

                mDocList.setVisibility(View.VISIBLE);
                mLabel.setText(getText(R.string.doc_textView_text));
                break;
            case R.string.doc_buttonUpdate:
                workWithDoc(DataBaseTask.UPDATE_DOC);
                break;
            case R.string.doc_buttonDelete:
                workWithDoc(DataBaseTask.DELETE_DOC);
                break;
            default:break;
        }
    }

    private String stringInOneLine(String resultString){
        return resultString.replace("\n", ",").replace(" ", "");
    }
}