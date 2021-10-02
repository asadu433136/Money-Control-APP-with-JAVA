package com.example.pluralcode;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class deletePopUp extends FragmentActivity {

    DatabaseReference myRef;
    private RecyclerView mRecyclerView;
    Button yes;
    Button no;
    String key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .85), (int) (height * 0.25));

        yes = (Button) findViewById(R.id.delete_yes_button);
        no = (Button) findViewById(R.id.delete_no_button);

        Intent i = getIntent();
        key = i.getStringExtra("keyToDelete");

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef= FirebaseDatabase.getInstance("https://money-control-c1462-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("transactions");
                myRef.child(key).removeValue();

                Intent i = new Intent(deletePopUp.this,MainActivity.class);
                i.putExtra("keyToDelete",key);
                startActivity(i);

            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    public String getMyData(){
        return key;
    }
}
