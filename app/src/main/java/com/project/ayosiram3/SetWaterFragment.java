package com.project.ayosiram3;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.ayosiram3.ModalManualFragment;
import com.project.ayosiram3.R;

public class SetWaterFragment extends Fragment implements View.OnClickListener{

    ImageButton btOn, btOff, btInput;
    TextView totalMl, flowMl;
    DatabaseReference databaseReference, fbTotal, fbFlow;

    public SetWaterFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set_water, container, false);

        btOn = view.findViewById(R.id.bt_wt_on);
        btOff = view.findViewById(R.id.bt_wt_off);
        btInput = view.findViewById(R.id.bt_wt_input);

        btInput.setOnClickListener(SetWaterFragment.this);
        btOn.setOnClickListener(SetWaterFragment.this);
        btOff.setOnClickListener(SetWaterFragment.this);

        totalMl = view.findViewById(R.id.tv_wt_total);
        flowMl = view.findViewById(R.id.tv_wt_flow);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        fbTotal = databaseReference.child("Water/WaterSum");
        fbFlow = databaseReference.child("Water/WaterFlow");

        fbTotal.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int nextFuzzy = dataSnapshot.getValue(Integer.class);
                totalMl.setText(nextFuzzy+" mL");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        fbFlow.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int nextFuzzy = dataSnapshot.getValue(Integer.class);
                flowMl.setText(nextFuzzy + " mL/s");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_wt_on:
                databaseReference.child("Relay/control").setValue(1);
                databaseReference.child("Relay/water/onoff").setValue(5000);
                break;
            case R.id.bt_wt_off:
                databaseReference.child("Relay/control").setValue(1);
                databaseReference.child("Relay/water/onoff").setValue(0);
                break;
            case R.id.bt_wt_input:
                ModalManualFragment modalManualFragment = new ModalManualFragment();
                FragmentManager fragmentManager = getChildFragmentManager();
                modalManualFragment.show(fragmentManager, ModalManualFragment.class.getSimpleName());
                break;
        }
    }

    ModalManualFragment.ModalManualListener modalManualListener = new ModalManualFragment.ModalManualListener() {
        @Override
        public void onGetText(int value) {
            databaseReference.child("Relay/water/inputManual").setValue(value);
            databaseReference.child("Relay/control").setValue(2);
        }
    };
}