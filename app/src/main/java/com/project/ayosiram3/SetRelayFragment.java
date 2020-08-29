package com.project.ayosiram3;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SetRelayFragment extends Fragment implements View.OnClickListener{

    public TextView tvChangeWater, tvChangeAuto, tvContent, tvNextFuzzy, tvNormalState, tvTime1, tvTime2;
    public TextView tvContentTime1, tvContentTime2, tvDisableTime1, tvDisableTime2;
    public DatabaseReference databaseReference, fbAutoContent, fbNextFuzzy, fbContent, fbNormalState,
            fbTime1, fbTime2;
    boolean auto;
    int nextFuzzy;

    public SetRelayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_relay, container, false);

        tvChangeWater = view.findViewById(R.id.tv_rl_click2);
        tvChangeAuto = view.findViewById(R.id.tv_rl_click1);
        tvContent = view.findViewById(R.id.tv_rl_content1);
        tvNextFuzzy = view.findViewById(R.id.tv_rl_value1);
        tvNormalState = view.findViewById(R.id.tv_rl_value2);


        tvTime1 = view.findViewById(R.id.tv_rl_click3);
        tvTime2 = view.findViewById(R.id.tv_rl_click4);
        tvContentTime1 = view.findViewById(R.id.tv_rl_content3);
        tvContentTime2 = view.findViewById(R.id.tv_rl_content4);
        tvDisableTime1 = view.findViewById(R.id.tv_rl_disable_time1);
        tvDisableTime2 = view.findViewById(R.id.tv_rl_disable_time2);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        fbAutoContent = databaseReference.child("Relay/auto");
        fbNextFuzzy = databaseReference.child("Relay/water/fuzzyAuto");
        fbContent = databaseReference.child("Relay/auto");
        fbNormalState = databaseReference.child("Fuzzy/fzSource/fzWater");
        fbTime1 = databaseReference.child("Relay/time/time1");
        fbTime2 = databaseReference.child("Relay/time/time2");


        fbNextFuzzy.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nextFuzzy = dataSnapshot.getValue(Integer.class);
                tvNextFuzzy.setText(nextFuzzy +" mL");

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        fbAutoContent.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                auto = dataSnapshot.getValue(Boolean.class);
                if(auto){
                    tvContent.setText(R.string.relay_auto);
                    tvNextFuzzy.setText(nextFuzzy +" mL");
                }else{
                    tvContent.setText(R.string.relay_auto2);
                    tvNextFuzzy.setText("-");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        fbNormalState.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int nextFuzzy = dataSnapshot.getValue(Integer.class);
                tvNormalState.setText(nextFuzzy +" mL");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        fbTime1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String text = snapshot.getValue(String.class);
                if(text==null){
                    tvContentTime1.setText("Not Set");
                }else tvContentTime1.setText(text);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        fbTime2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String text = snapshot.getValue(String.class);
                if(text==null){
                    tvContentTime2.setText("Not Set");
                }else tvContentTime2.setText(text);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });



        tvChangeAuto.setOnClickListener(SetRelayFragment.this);
        tvChangeWater.setOnClickListener(SetRelayFragment.this);
        tvTime1.setOnClickListener(SetRelayFragment.this);
        tvTime2.setOnClickListener(SetRelayFragment.this);
        tvDisableTime1.setOnClickListener(SetRelayFragment.this);
        tvDisableTime2.setOnClickListener(SetRelayFragment.this);
        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getChildFragmentManager();
        switch (v.getId()) {
            case R.id.tv_rl_click2:
                ModalAutoFragment modalAutoFragment = new ModalAutoFragment();


                modalAutoFragment.show(fragmentManager, ModalAutoFragment.class.getSimpleName());
                break;
            case R.id.tv_rl_click3:
                ModalWaktuFragment modalWaktuFragment = new ModalWaktuFragment();
                modalWaktuFragment.show(fragmentManager, ModalWaktuFragment.class.getSimpleName());
                break;
            case R.id.tv_rl_disable_time1:
                fbTime1.removeValue();
                break;
            case R.id.tv_rl_click4:
                ModalWaktu2Fragment modalWaktu2Fragment = new ModalWaktu2Fragment();
                modalWaktu2Fragment.show(fragmentManager, ModalWaktu2Fragment.class.getSimpleName());
                break;
            case R.id.tv_rl_disable_time2:
                fbTime2.removeValue();
                break;
            case R.id.tv_rl_click1:
                if(auto){
                    databaseReference.child("Relay/auto").setValue(false);
                }else{
                    databaseReference.child("Relay/auto").setValue(true);
                }
                break;

        }
    }

    ModalAutoFragment.ModalAutoListener modalAutoListener = new ModalAutoFragment.ModalAutoListener() {
        @Override
        public void onGetText(int value) {
            databaseReference.child("Fuzzy/fzSource/fzWater").setValue(value);
        }
    };

    ModalWaktuFragment.ModalWaktuListener modalWaktuListener = new ModalWaktuFragment.ModalWaktuListener() {
        @Override
        public void onGetText(String value) {
            fbTime1.setValue(value);
        }
    };

    ModalWaktu2Fragment.ModalWaktu2Listener modalWaktu2Listener = new ModalWaktu2Fragment.ModalWaktu2Listener() {
        @Override
        public void onGetText(String value) {
            fbTime2.setValue(value);
        }
    };
}