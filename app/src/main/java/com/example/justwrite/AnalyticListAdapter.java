package com.example.justwrite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class AnalyticListAdapter extends RecyclerView.Adapter<AnalyticListAdapter.AnalyticViewHolder> {
    private LinkedList<Analytic> mAnalyticList;
    private LayoutInflater mInflater;

    public AnalyticListAdapter(Context context, LinkedList<Analytic> analyticsList) {
        mInflater = LayoutInflater.from(context);
        this.mAnalyticList = analyticsList;
    }

    @NonNull
    @Override
    public AnalyticViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.analytic_list_item, parent, false);
        return new AnalyticViewHolder(mItemView, this);
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
        final AnalyticListAdapter mAdapter;

        public AnalyticViewHolder(View itemView, AnalyticListAdapter adapter) {
            super(itemView);
            analyticName = itemView.findViewById(R.id.analyticName);
            this.mAdapter = adapter;
            analyticData = itemView.findViewById(R.id.analyticData);
        }
    }
}
