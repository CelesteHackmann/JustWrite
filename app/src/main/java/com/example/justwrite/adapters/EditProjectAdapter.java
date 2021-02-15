package com.example.justwrite.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.justwrite.R;
import com.example.justwrite.classes.DatabaseHelper;
import com.example.justwrite.classes.Project;

import java.util.ArrayList;

public class EditProjectAdapter extends RecyclerView.Adapter<EditProjectAdapter.EditProjectViewHolder>{
    private final ArrayList<Project> mProjectList;
    private final LayoutInflater mInflater;
    private final Context mContext;
    public boolean changesMade;

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
        DatabaseHelper db = DatabaseHelper.getInstance(mContext);
        if (db.projectIsArchived(current.getId())) {
            holder.editProjectVisibilityButton.setText(R.string.unarchive_button_text);
        }
        else {
            holder.editProjectVisibilityButton.setText(R.string.archive_button_text);
        }
        holder.currentProjectId = current.getId();
    }

    @Override
    public int getItemCount() {
        return mProjectList.size();
    }

    public boolean projectsUpdated() {
        return changesMade;
    }

    class EditProjectViewHolder extends RecyclerView.ViewHolder{
        public final EditText editProjectTitle;
        public final EditText editProjectGenre;
        public final Button updateProjectButton;
        public final Button editProjectVisibilityButton;
        public String currentProjectId;

        public EditProjectViewHolder(final View itemView) {
            super(itemView);
            editProjectTitle = itemView.findViewById(R.id.editTextProjectTitle);
            editProjectGenre = itemView.findViewById(R.id.editTextProjectGenre);
            updateProjectButton = itemView.findViewById(R.id.updateProjectButton);
            editProjectVisibilityButton = itemView.findViewById(R.id.changeArchiveButton);
            final DatabaseHelper db = DatabaseHelper.getInstance(mContext);
            updateProjectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newTitle = String.valueOf(editProjectTitle.getText());
                    String newGenre = String.valueOf(editProjectGenre.getText());
                    db.updateProjectTitleAndGenre(currentProjectId, newTitle, newGenre);
                    changesMade = true;
                    Toast.makeText(mContext, "Project Updated!", Toast.LENGTH_SHORT).show();
                }
            });
            editProjectVisibilityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.toggleProjectVisibility(currentProjectId);
                    changesMade = true;
                    toggleProjectVisibility(db);
                }
            });
        }

        private void toggleProjectVisibility(DatabaseHelper db) {
            if (db.projectIsArchived(currentProjectId)) {
                editProjectVisibilityButton.setText(R.string.unarchive_button_text);
            }
            else {
                editProjectVisibilityButton.setText(R.string.archive_button_text);
            }
        }
    }
}
