package com.project.ayosiram3;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.ayosiram3.riwayatSoil.RiwayatSoilFragment;
import com.project.ayosiram3.riwayatTemp.RiwayatTempFragment;
import com.project.ayosiram3.riwayatWater.RiwayatWaterFragment;

public class HomeFragment extends Fragment implements View.OnClickListener{
    private TextView tvHomeSoil;
    private TextView tvHomeSoilfz;
    private TextView tvHomeTemp;
    private TextView tvHomeTempfz;
    private TextView tvHomeTempdt;
    private TextView tvHomeSoildt;
    private TextView tvHomeAuto;
    private TextView tvHomeLast;
    private TextView tvAutoContent;
    private TextView tvLastContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvHomeSoil      = view.findViewById(R.id.tv_home_soil);
        tvHomeSoilfz    = view.findViewById(R.id.tv_home_soilfz);
        tvHomeSoildt    = view.findViewById(R.id.tv_home_soildt);

        tvHomeTemp      = view.findViewById(R.id.tv_home_temp);
        tvHomeTempfz    = view.findViewById(R.id.tv_home_tempfz);
        tvHomeTempdt    = view.findViewById(R.id.tv_home_tempdt);

        tvHomeAuto      = view.findViewById(R.id.tv_home_auto);
        tvHomeLast      = view.findViewById(R.id.tv_home_last);
        tvAutoContent   = view.findViewById(R.id.tv_home_auto_content);
        tvLastContent   = view.findViewById(R.id.tv_home_last_content);

        tvHomeTempdt.setOnClickListener(HomeFragment.this);
        tvHomeSoildt.setOnClickListener(HomeFragment.this);
        tvHomeAuto.setOnClickListener(HomeFragment.this);
        tvHomeLast.setOnClickListener(HomeFragment.this);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference fbRtSoil = databaseReference.child("Realtime/rtSoil");
        DatabaseReference fbRtTemp = databaseReference.child("Realtime/rtTemp");
        DatabaseReference fbAuto    = databaseReference.child("Relay/auto");
        DatabaseReference fbLast    = databaseReference.child("Riwayat/rwWater/rTentatif");

        fbRtSoil.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float value = dataSnapshot.getValue(Float.class);
                if(value<40){
                    tvHomeSoilfz.setText(view.getContext().getString(R.string.dry));
                }else if(value<67.5){
                    tvHomeSoilfz.setText(view.getContext().getString(R.string.humid2));
                }else{
                    tvHomeSoilfz.setText(view.getContext().getString(R.string.wet));
                }
                String myText = value + " %";
                tvHomeSoil.setText(myText);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}

        });
        fbRtTemp.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float value = dataSnapshot.getValue(Float.class);
                if(value<22.5){
                    tvHomeTempfz.setText(view.getContext().getString(R.string.cold));
                }else if(value<27.5){
                    tvHomeTempfz.setText(view.getContext().getString(R.string.cool));
                }else if(value<32.5){
                    tvHomeTempfz.setText(view.getContext().getString(R.string.normal));
                }else if(value<37.5){
                    tvHomeTempfz.setText(view.getContext().getString(R.string.warm));
                }else {
                    tvHomeTempfz.setText(view.getContext().getString(R.string.hot));
                }
                String myText = value + " °C";
                tvHomeTemp.setText(myText);
            }
            public void onCancelled(@NonNull DatabaseError databaseError) {}

        });
        fbAuto.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean value = dataSnapshot.getValue(Boolean.class);
                if(value) tvAutoContent.setText("ON");
                else  tvAutoContent.setText("OFF");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        fbLast.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float value = dataSnapshot.getValue(Float.class);
                String myText = value + " mL";
                tvLastContent.setText(myText);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_home_soildt:
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RiwayatSoilFragment()).addToBackStack(null).commit();
                break;
            case R.id.tv_home_tempdt:
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RiwayatTempFragment()).addToBackStack(null).commit();
                break;
            case R.id.tv_home_auto:
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SetRelayFragment()).addToBackStack(null).commit();
                break;
            case R.id.tv_home_last:
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RiwayatWaterFragment()).addToBackStack(null).commit();
                break;
        }
    }
}