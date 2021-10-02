package com.example.pluralcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {

    TextView display;
    Button button;
    String projectName;


    RecyclerView mRecyclerView;
    DatabaseReference myRef;

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ////// GET PROJECT  ///////////////////////////////////
        Intent i = getIntent();
        projectName = i.getStringExtra("projectName");
        String key = i.getStringExtra("keyToDelete");



        if(key!=null){
            Fragment fragment;
            if(key.equals("budget")){
                fragment = budgetFragment.newInstance(projectName,"");
            }else{
                fragment = transactionsFragment.newInstance(projectName,"");
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.bigContainer, fragment).commit();
            key=null;
        }
        else{
            getSupportFragmentManager().beginTransaction().replace(R.id.bigContainer,dashboardFragment.newInstance(projectName,"")).commit();
        }




        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        bottomNavigationView= findViewById(R.id.bottomNav);

        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);


    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment=null;
                    Intent i= new Intent(MainActivity.this,addActivity.class);
                    i.putExtra("projectName",projectName);


                    switch (item.getItemId()){
                        case R.id.dashboard:
                            fragment= dashboardFragment.newInstance(projectName,"");
                            break;
                        case R.id.transactions:
                            fragment= transactionsFragment.newInstance(projectName,"");
                            //startActivity(j);
                            break;
                        case R.id.add:
                            startActivity(i);
                            overridePendingTransition(0, 0);
                            break;
                        case R.id.budget:
                            fragment= budgetFragment.newInstance(projectName,"");
                            break;
                    }
                    if (fragment!=null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.bigContainer, fragment).commit();
                        overridePendingTransition(0, 0);
                    }
                    return true;
                }
            };

    /*
    public void openTransaction(View v){
        Intent i = new Intent(this,Transactions.class);
        startActivity(i);
    }
     */
}