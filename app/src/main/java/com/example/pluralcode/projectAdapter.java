package com.example.pluralcode;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class projectAdapter extends RecyclerView.Adapter<projectAdapter.ViewHolder> {

    private List<Project> projects;

    //For click
    projectAdapter.ClickListener mClickListener;



    public projectAdapter(List<Project> projects, projectAdapter.ClickListener clickListener) {

        this.projects = projects;
        this.mClickListener = clickListener;
    }



    @NonNull
    @Override
    public projectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.project_list_item,parent,false);


        return new projectAdapter.ViewHolder(view,mClickListener);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Project project= projects.get(position);

        String name = project.getName()+"";
        String description = project.getDescription()+"";


        holder.mName.setText(name);
        holder.mDescription.setText(description);

    }

    @Override
    public int getItemCount() {
        return projects.size();
    }



    ////////////////////////////////////////////////////////////////////////////////////////////

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mName,mDescription;
        private Button deleteProject, editProject;

        projectAdapter.ClickListener clickListener;

        public ViewHolder(@NonNull View itemView, projectAdapter.ClickListener clickListener) {
            super(itemView);
            mName =(TextView) itemView.findViewById(R.id.project_name);
            mDescription =(TextView) itemView.findViewById(R.id.project_description);

            deleteProject = (Button) itemView.findViewById(R.id.delete_project_button);
            editProject = (Button) itemView.findViewById(R.id.edit_project_button);



            this.clickListener=clickListener;

            itemView.setOnClickListener(this);
            deleteProject.setOnClickListener(this);
            editProject.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == deleteProject.getId()) {
                clickListener.onItemClick(getAdapterPosition(), "delete");
            } else if(v.getId() == editProject.getId()) {
                clickListener.onItemClick(getAdapterPosition(), "edit");
            }else{
                clickListener.onItemClick(getAdapterPosition(), "openTransaction");
            }

        }
    }

    public interface ClickListener {
        void onItemClick(int position, String s);
        void onItemLongClick(int position, View v);
    }
}
