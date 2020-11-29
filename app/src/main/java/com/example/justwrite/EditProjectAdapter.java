package com.example.justwrite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EditProjectAdapter extends RecyclerView.Adapter<EditProjectAdapter.EditProjectViewHolder>{
    private ArrayList<Project> mProjectList;
    private final LayoutInflater mInflater;
    static Context mContext;

    public EditProjectAdapter(Context context, ArrayList<Project> projectList) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mProjectList = projectList;
    }

    @NonNull
    @Override
    public EditProjectAdapter.EditProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.edit_project_item, parent, false);
        return new EditProjectViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EditProjectAdapter.EditProjectViewHolder holder, int position) {
        Project current = mProjectList.get(position);
        holder.editProjectTitle.setText(current.getTitle());
        holder.editProjectGenre.setText(current.getGenre());
        holder.currentProjectId = current.getId();
    }

    @Override
    public int getItemCount() {
        return mProjectList.size();
    }

    static class EditProjectViewHolder extends RecyclerView.ViewHolder{
        public final EditText editProjectTitle;
        public final EditText editProjectGenre;
        public final Button updateProjectButton;
        public String currentProjectId;

        public EditProjectViewHolder(final View itemView) {
            super(itemView);
            editProjectTitle = itemView.findViewById(R.id.editTextProjectTitle);
            editProjectGenre = itemView.findViewById(R.id.editTextProjectGenre);
            updateProjectButton = itemView.findViewById(R.id.updateProjectButton);
            updateProjectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newTitle = String.valueOf(editProjectTitle.getText());
                    String newGenre = String.valueOf(editProjectGenre.getText());
                    DatabaseHelper db = DatabaseHelper.getInstance(mContext);
                    db.updateProjectTitleAndGenre(currentProjectId, newTitle, newGenre);
                    Toast.makeText(mContext, "Project Updated!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
