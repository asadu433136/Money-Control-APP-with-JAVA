package com.example.pluralcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class addProject extends AppCompatActivity {
    /////////////////////////////////////
    FirebaseUser user;
    String userID;
    //////////////////////////////////////
    DatabaseReference myRef;
    EditText pName, pDescription;
    Button addProjectButton, cancelProject, editProject, deleteProject;

    /////////////////////////
    ArrayList<String>  projectnamelist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);
        getSupportActionBar().hide();

        pName=findViewById(R.id.project_title_EditText);
        pDescription= findViewById(R.id.project_description_EditText);
        addProjectButton = findViewById(R.id.commitProject);
        cancelProject = findViewById(R.id.cancelProject);
        editProject = findViewById(R.id.editProject);
        deleteProject = findViewById(R.id.deleteProject);


        editProject.setEnabled(false);
        deleteProject.setEnabled(false);

        projectnamelist = new ArrayList<>();

        /////////////////////////////////////////////////////////////////////////////////////

        Intent i = getIntent();
        String key = i.getStringExtra("key");
        String title = i.getStringExtra("title");
        String description = i.getStringExtra("description");

        ////////////////////////////////////////////////////////////////////////////////////

        if(key!=null){
            Drawable buttonDrawable = editProject.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            //the color is a direct color int and not a color resource
            DrawableCompat.setTint(buttonDrawable, Color.parseColor("#483D8B"));

            editProject.setBackground(buttonDrawable);
            deleteProject.setBackground(buttonDrawable);

            editProject.setEnabled(true);
            deleteProject.setEnabled(true);

            pName.setText(title);
            pDescription.setText(description);
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        ////////////////////////////////////////////////////////////////////////////
        ////////////////  FETCH DATA TO PREVENT PROJECT DUPLICATION //////////////

        myRef = FirebaseDatabase.getInstance("https://money-control-c1462-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users").child(userID);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot npsnapshot : snapshot.getChildren()){
                        String keys = npsnapshot.getKey()+"";
                        if(!keys.contains("user")){
                            String name = npsnapshot.child("name").getValue().toString()+"";
                            projectnamelist.add(name);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////// IMPLEMENT BUTTONS  /////////////////////////////////////////////////////////


        addProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = pName.getText().toString().trim();
                String description = pDescription.getText().toString().trim();
                if(title.equals(null) || title.length()>20 ){
                    pName.setError("Input correct details");
                    return;
                }
                if(projectnamelist.contains(title) ){
                    pName.setError("Project Already Exists");
                    return;
                }
                if(description.toString().equals(null) || description.length()>50 ){
                    pDescription.setError("Input correct details");
                    return;
                }
                Project p = new Project(title,description);
                myRef.push().setValue(p);
                Intent i = new Intent(addProject.this, projects.class);
                startActivity(i);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        editProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tit = pName.getText().toString().trim();
                String des = pDescription.getText().toString().trim();
                if(tit.equals(null) || tit.length()>20 ){
                    pName.setError("Input correct details");
                    return;
                }
                if(tit.equals(title) && des.equals(description)){
                    Toast.makeText(addProject.this,"No changes were made", Toast.LENGTH_LONG).show();
                    return;
                }
                if(projectnamelist.contains(tit) && !tit.equals(title) ){
                    pName.setError("Project Already Exists");
                    return;
                }
                if(des.toString().equals(null) || des.length()>50 ){
                    pDescription.setError("Input correct details");
                    return;
                }
                Project p = new Project(tit,des);
                myRef.child(key).child("name").setValue(tit);
                myRef.child(key).child("description").setValue(des);
                Intent i = new Intent(addProject.this, projects.class);
                startActivity(i);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        deleteProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.child(key).removeValue();
                Intent i = new Intent(addProject.this, projects.class);
                startActivity(i);
                overridePendingTransition(0, 0);
                finish();
            }
        });

    }

    public void cancel(View V){
        finish();
        overridePendingTransition(0, 0);
    }
}