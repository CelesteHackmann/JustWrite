package com.example.justwrite.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.justwrite.R;
import com.example.justwrite.classes.Analytic;

import java.util.LinkedList;

public class AnalyticListAdapter extends RecyclerView.Adapter<AnalyticListAdapter.AnalyticViewHolder> {
    private final LinkedList<Analytic> mAnalyticList;
    private final LayoutInflater mInflater;

    public AnalyticListAdapter(Context context, LinkedList<Analytic> analyticsList) {
        mInflater = LayoutInflater.from(context);
        this.mAnalyticList = analyticsList;
    }

    @NonNull
    @Override
    public AnalyticViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.analytic_list_item, parent, false);
        return new AnalyticViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AnalyticViewHolder holder, int position) {
        Analytic mCurrent = mAnalyticList.get(position);
         holder.analyticName.setText(mCurrent.getName());
         holder.analyticData.setText(mCurrent.getData());
    }

    @Override
    public int getItemCount() {
        return mAnalyticList.size();
    }

    static class AnalyticViewHolder extends RecyclerView.ViewHolder {
        public final TextView analyticName;
        public final TextView analyticData;

        public AnalyticViewHolder(View itemView) {
            super(itemView);
            analyticName = itemView.findViewById(R.id.analyticName);
            analyticData = itemView.findViewById(R.id.analyticData);
        }
    }
}
