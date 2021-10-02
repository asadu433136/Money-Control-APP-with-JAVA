package com.example.pluralcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class projects extends AppCompatActivity implements projectAdapter.ClickListener{

    DatabaseReference myRef;
    FirebaseUser user;


    RecyclerView projectsRecyclerView;
    TextView welcome;
    ArrayList<Project> projectList;
    String userID;
    ArrayList<String> projectnamelist, keyList;
    ArrayList<String> projectdescriptionlist;
    String projectname;
    projectAdapter adapter;

    Button addProjectButton, editProjectButton , deleteProjectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);
        getSupportActionBar().hide();

        /////////////////////////////////////////////////////////////////////////////
        ////  INITIALIZE VARIABLES  /////////////////////////////////////////////////

        addProjectButton = findViewById(R.id.add_projects_button2);
        editProjectButton = findViewById(R.id.edit_project_button);
        deleteProjectButton = findViewById(R.id.delete_project_button);


        projectsRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_projects);
        projectsRecyclerView.setHasFixedSize(true);
        projectsRecyclerView.setLayoutManager(new LinearLayoutManager(projects.this));
        welcome = findViewById(R.id.projects_welcome_textview);
        projectList = new ArrayList<>();
        projectnamelist= new ArrayList<>();
        projectdescriptionlist= new ArrayList<>();
        keyList =  new ArrayList<>();

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        myRef=FirebaseDatabase.getInstance("https://money-control-c1462-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users").child(userID);
        myRef.keepSynced(true);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    String fullName = snapshot.child("user_info").child("full_name").getValue().toString()+"";

                    for (DataSnapshot npsnapshot : snapshot.getChildren()){
                        //Project p=npsnapshot.getValue(Project.class);
                        String key = npsnapshot.getKey()+"";
                        if(!key.contains("user")){
                            String name = npsnapshot.child("name").getValue().toString()+"";
                            String description = npsnapshot.child("description").getValue().toString()+"";

                            Project p= new Project();
                            p.setName(name);
                            p.setDescription(description);

                            projectList.add(p);
                            keyList.add(key);
                            projectnamelist.add(name);
                            projectdescriptionlist.add(description);
                        }
                    }


                    //String model="Hi " + fullName + "\n, View and add projects here.";
                    //String text= model.replace("\n", "&lt;br&gt;");
                    //welcome.setText(Html.fromHtml(Html.fromHtml(text).toString()));
                    welcome.setText("Hi " + fullName + ", View and add projects here.");
                    adapter=new projectAdapter(projectList, projects.this);
                    projectsRecyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        addProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(projects.this,addProject.class));
            }
        });
    }

    /////////////////////////////////////////////////////////////////////
    ///////  END OF ONCREATE  /////////////////////////////////////////

    @Override
    public void onItemClick(int position, String s) {
        if(s == "edit"){
            Intent i = new Intent(projects.this, addProject.class);
            i.putExtra("key", keyList.get(position));
            i.putExtra("title",projectnamelist.get(position));
            i.putExtra("description",projectdescriptionlist.get(position));
            startActivity(i);
        }else if(s=="delete"){

            ///  Create dialog box
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Delete Project");
            alert.setMessage("Are you sure you want to delete?");
            alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // continue with delete
                    myRef.child(keyList.get(position)).removeValue();
                    keyList.clear();
                    projectList.clear();
                    projectnamelist.clear();
                    projectdescriptionlist.clear();

                    Toast.makeText(projects.this,"Project deleted",Toast.LENGTH_LONG).show();
                }
            });
            alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // close dialog
                    dialog.cancel();
                }
            });
            alert.show();
        }else{
            projectname =keyList.get(position);

            Intent i = new Intent(projects.this,MainActivity.class);
            i.putExtra("projectName",projectname);
            startActivity(i);
            finish();
        }

    }

    @Override
    public void onItemLongClick(int position, View v) {

    }

    public void signOut(View v){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(projects.this,login.class));
        overridePendingTransition(0, 0);
        finish();

    }
}