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

public class ModalWaktu2Fragment extends DialogFragment implements View.OnClickListener {

    TextView tvSave, tvCancel;
    EditText etHour, etMinute;
    ModalWaktu2Listener modalWaktu2Listener;
    String text, strJam, strMinute;


    public ModalWaktu2Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_modal_waktu2, container, false);
        tvSave = view.findViewById(R.id.tv_save_time2);
        tvCancel = view.findViewById(R.id.tv_cancel_time2);
        etHour = view.findViewById(R.id.tv_hour_time2);
        etMinute = view.findViewById(R.id.tv_minute_time2);

        tvSave.setOnClickListener(ModalWaktu2Fragment.this);
        tvCancel.setOnClickListener(ModalWaktu2Fragment.this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel_time2:
                getDialog().cancel();
                break;
            case R.id.tv_save_time2:
                strJam = etHour.getText().toString().trim();
                if (strJam.matches("")) {
                    getDialog().dismiss();
                    break;
                }
                strMinute = etMinute.getText().toString().trim();
                if (strMinute.matches("")) {
                    getDialog().dismiss();
                    break;
                }
                int h = Integer.parseInt(strJam);
                int m = Integer.parseInt(strMinute);
                text = tambahNol(h, m);
                if (modalWaktu2Listener != null) {
                    modalWaktu2Listener.onGetText(text);
                }
                getDialog().dismiss();
                break;
        }

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Fragment fragment = getParentFragment();
        if (fragment instanceof SetRelayFragment) {
            SetRelayFragment setRelayFragment = (SetRelayFragment) fragment;
            this.modalWaktu2Listener = setRelayFragment.modalWaktu2Listener;
        }
    }

    public String tambahNol(int h, int m) {
        String teks, hour, minute;

        h = h % 24;
        if (h < 10) {
            hour = "0" + h;
        } else hour = Integer.toString(h);

        m = m % 60;
        if (m < 10) {
            minute = "0" + m ;
        } else minute = Integer.toString(m);

        teks = hour + ":" + minute;
        return teks;
    }

    public interface ModalWaktu2Listener {
        void onGetText(String value);
    }
}