package com.alexzandr.myapplication.fragment.tablet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexzandr.myapplication.R;

/**
 * Created by anekrasov on 02.06.15.
 */
public class WorkWithDocumentFragment extends WarehouseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_work_with_document, container, false);
    }
}