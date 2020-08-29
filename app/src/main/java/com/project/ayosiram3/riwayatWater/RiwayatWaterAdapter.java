package com.project.ayosiram3.riwayatWater;

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

public class RiwayatWaterAdapter extends RecyclerView.Adapter<RiwayatWaterAdapter.ViewHolder> {
    private ArrayList<RiwayatData> riwayatData;
    public RiwayatWaterAdapter(ArrayList<RiwayatData> riwayatData){
        this.riwayatData = riwayatData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_riwayat_water,parent,false);
        return new RiwayatWaterAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        float value = Float.parseFloat(riwayatData.get(position).getChild());

        holder.tvTime.setText(riwayatData.get(position).getKey());
        holder.tvValue.setText(riwayatData.get(position).getChild() + " mL");

    }

    @Override
    public int getItemCount() {
        return riwayatData != null ? riwayatData.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTime, tvValue;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_wr_time);
            tvValue = itemView.findViewById(R.id.tv_wr_value);
        }
    }
}
