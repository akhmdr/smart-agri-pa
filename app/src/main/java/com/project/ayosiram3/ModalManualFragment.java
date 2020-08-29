package com.project.ayosiram3;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class ModalManualFragment extends DialogFragment implements View.OnClickListener{

    EditText etDialog;
    TextView tvSave, tvCancel;
    ModalManualListener modalManualListener;
    String text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_modal_manual, container, false);
        etDialog    = view.findViewById(R.id.et_dialog_man);
        tvSave      = view.findViewById(R.id.tv_save_man);
        tvCancel    = view.findViewById(R.id.tv_cancel_man);

        tvSave.setOnClickListener(ModalManualFragment.this);
        tvCancel.setOnClickListener(ModalManualFragment.this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel_man:
                getDialog().cancel();
                break;
            case R.id.tv_save_man:
                text = etDialog.getText().toString().trim();
                if(text.matches("")){
                    getDialog().dismiss();
                    break;
                }
                int value = Integer.parseInt(text);
                if (modalManualListener != null) {
                    modalManualListener.onGetText(value);
                }
                getDialog().dismiss();
                break;
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Fragment fragment = getParentFragment();
        if(fragment instanceof SetWaterFragment){
            SetWaterFragment setWaterFragment = (SetWaterFragment) fragment;
            this.modalManualListener = setWaterFragment.modalManualListener;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.modalManualListener = null;
    }

    public interface ModalManualListener {
        void onGetText(int value);
    }
}