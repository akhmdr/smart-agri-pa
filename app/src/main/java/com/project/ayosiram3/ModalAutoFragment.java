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

public class ModalAutoFragment extends DialogFragment implements View.OnClickListener {

    EditText etDialog;
    TextView tvSave, tvCancel;
    ModalAutoListener modalAutoListener;
    String text;

    public ModalAutoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_modal_auto, container, false);

        etDialog    = view.findViewById(R.id.et_dialog_auto);
        tvSave      = view.findViewById(R.id.tv_save_auto);
        tvCancel    = view.findViewById(R.id.tv_cancel_auto);

        tvSave.setOnClickListener(ModalAutoFragment.this);
        tvCancel.setOnClickListener(ModalAutoFragment.this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel_auto:
                getDialog().cancel();
                break;
            case R.id.tv_save_auto:
                text = etDialog.getText().toString().trim();
                if(text.matches("")){
                    getDialog().dismiss();
                    break;
                }
                int value = Integer.parseInt(text);
                if(modalAutoListener != null){
                    modalAutoListener.onGetText(value);
                }
                getDialog().dismiss();
                break;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Fragment fragment = getParentFragment();
        if(fragment instanceof SetRelayFragment){
            SetRelayFragment relayFragment = (SetRelayFragment) fragment;
            this.modalAutoListener = relayFragment.modalAutoListener;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.modalAutoListener = null;
    }

    public interface ModalAutoListener {
        void onGetText(int value);
    }
}