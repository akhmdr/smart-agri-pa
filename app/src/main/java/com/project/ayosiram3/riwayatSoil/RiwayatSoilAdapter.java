package com.project.ayosiram3.riwayatSoil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.ayosiram3.R;
import com.project.ayosiram3.RiwayatData;

import java.util.ArrayList;

public class RiwayatSoilAdapter extends RecyclerView.Adapter<RiwayatSoilAdapter.ViewHolder> {

    private ArrayList<RiwayatData> riwayatData;

    public RiwayatSoilAdapter(ArrayList<RiwayatData> riwayatData){
        this.riwayatData = riwayatData;
    }

    @NonNull
    @Override
    public RiwayatSoilAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_riwayat,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RiwayatSoilAdapter.ViewHolder holder, int position) {
        float value = Float.parseFloat(riwayatData.get(position).getChild());

        holder.tvTime.setText(riwayatData.get(position).getKey());
        holder.tvValue.setText(riwayatData.get(position).getChild() + " %");

        if(value<40){
            holder.tvFuzzy.setText("Dry");
        }else if(value<67.5){
            holder.tvFuzzy.setText("Moist");
        }else{
            holder.tvFuzzy.setText("Wet");
        }

    }

    @Override
    public int getItemCount() {
        return riwayatData != null ? riwayatData.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTime, tvValue, tvFuzzy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_rw_time);
            tvValue = itemView.findViewById(R.id.tv_rw_value);
            tvFuzzy = itemView.findViewById(R.id.tv_rw_fuzzy);
        }
    }
}