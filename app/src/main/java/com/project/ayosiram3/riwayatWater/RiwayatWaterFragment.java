package com.project.ayosiram3.riwayatWater;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.ayosiram3.R;
import com.project.ayosiram3.RiwayatData;

import java.util.ArrayList;

public class RiwayatWaterFragment extends Fragment implements View.OnClickListener {

    DatabaseReference dbRef;
    ArrayList<RiwayatData> riwayatData = new ArrayList<>();
    RiwayatWaterAdapter riwayatWaterAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_riwayat_water, container, false);

        Button btHour   = view.findViewById(R.id.bt_rwWater_hour);
        Button btDay    = view.findViewById(R.id.bt_rwWater_day);

        btHour.setOnClickListener(RiwayatWaterFragment.this);
        btDay.setOnClickListener(RiwayatWaterFragment.this);

        dbRef = FirebaseDatabase.getInstance().getReference();
        getFirebaseData("Riwayat/rwWater/rwHour");

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView recyclerView = view.findViewById(R.id.rv_rv_water);

        riwayatWaterAdapter = new RiwayatWaterAdapter(riwayatData);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(riwayatWaterAdapter);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_rwWater_hour:
                riwayatData.clear();
                getFirebaseData("Riwayat/rwWater/rwHour");
                break;
            case R.id.bt_rwWater_day:
                riwayatData.clear();
                getFirebaseData("Riwayat/rwWater/rwDay");
                break;
        }
    }

    public void getFirebaseData(String address){
        DatabaseReference getData = dbRef.child(address);
        getData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                int value = dataSnapshot.getValue(int.class);
                String key = dataSnapshot.getKey();
                String child = Integer.toString(value);

                riwayatData.add(new RiwayatData(key,child));
                riwayatWaterAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}