package com.example.justwrite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class SprintListAdapter extends RecyclerView.Adapter<SprintListAdapter.SprintViewHolder> {
    private final LinkedList<Sprint> mSprintList;
    private final LayoutInflater mInflater;

    public SprintListAdapter(Context context, LinkedList<Sprint> sprintList) {
        mInflater = LayoutInflater.from(context);
        this.mSprintList = sprintList;
    }

    @NonNull
    @Override
    public SprintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.sprint_list_item, parent, false);
        return new SprintViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SprintListAdapter.SprintViewHolder holder, int position) {
        Sprint mCurrent = mSprintList.get(position);
        holder.sprintWordCount.setText(mCurrent.getWordCount() + " words");
        holder.sprintItemView.setText(mCurrent.toString());
    }

    @Override
    public int getItemCount() {
        return mSprintList.size();
    }

    static class SprintViewHolder extends RecyclerView.ViewHolder {
        public final TextView sprintItemView;
        public final TextView sprintWordCount;

        public SprintViewHolder(View itemView) {
            super(itemView);
            sprintItemView = itemView.findViewById(R.id.sprint);
            sprintWordCount = itemView.findViewById(R.id.sprintWordCount);
        }
    }
}
