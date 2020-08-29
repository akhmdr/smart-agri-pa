package com.project.ayosiram3.riwayatTemp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.ayosiram3.R;
import com.project.ayosiram3.RiwayatData;

import java.util.ArrayList;

public class RiwayatTempAdapter extends RecyclerView.Adapter<RiwayatTempAdapter.ViewHolder> {
    private ArrayList<RiwayatData> riwayatData;

    public RiwayatTempAdapter(ArrayList<RiwayatData> riwayatData){
        this.riwayatData = riwayatData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_riwayat,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        float value = Float.parseFloat(riwayatData.get(position).getChild());

        holder.tvTime.setText(riwayatData.get(position).getKey());
        holder.tvValue.setText(riwayatData.get(position).getChild() + " °C");

        if(value<22.5){
            holder.tvFuzzy.setText("Cold");
        }else if(value<27.5){
            holder.tvFuzzy.setText("Cool");
        }else if(value<32.5){
            holder.tvFuzzy.setText("Normal");
        }else if(value<37.5){
            holder.tvFuzzy.setText("Warm");
        }else {
            holder.tvFuzzy.setText("Hot");
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
