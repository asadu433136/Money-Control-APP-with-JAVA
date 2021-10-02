package com.example.pluralcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class settings extends AppCompatActivity {

    /////////////////////////////////////
    FirebaseUser user;
    String userID;
    //////////////////////////////////////
    DatabaseReference tCurrencyRef;
    DatabaseReference gCurrencyRef;
    DatabaseReference currencyRate;

    private Spinner currency;
    private Button back;
    String curr;
    double conversionRate;
    String projectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide();


        Intent i = getIntent();
        projectName = i.getStringExtra("projectName");

        currency = (Spinner)findViewById(R.id.spinner_set_currency);
        back = (Button) findViewById(R.id.settings_back_button);

        ////////////////////// Auth
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        currencyRate = FirebaseDatabase.getInstance("https://money-control-c1462-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        tCurrencyRef= FirebaseDatabase.getInstance("https://money-control-c1462-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users").child(userID).child(projectName).child("transactions");
        gCurrencyRef = FirebaseDatabase.getInstance("https://money-control-c1462-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users").child(userID).child("user_info").child("currency");

        //Adapter for Currency
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<String>(
                settings.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.currency)
        );
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currency.setAdapter(currencyAdapter);

        // Preload currency spinner
        gCurrencyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    curr = snapshot.getValue(String.class) +"";
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        currency.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < currency.getCount(); i++) {
                    if (currency.getItemAtPosition(i).equals(curr)) {
                        currency.setSelection(i);
                        break;
                    }
                }
            }
        });
        /////////////////////////////////////////////// preloaded


        /// get currency rate
        currencyRate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String rate= snapshot.child("conv_rate").getValue(String.class);
                    conversionRate = Double.parseDouble(rate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //set listener for currency
        currency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextSize(24);

                String current = currency.getSelectedItem().toString();
                if(current.contains("Select") || current.equals(curr)){
                    return;
                }
                if(current.equals("Dollar")){

                }else if(current.equals("Euro")){

                }
                gCurrencyRef.setValue(current);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(settings.this, MainActivity.class);

                i.putExtra("projectName", projectName);
                startActivity(i);
            }
        });

        ///////////////////////////////////////////////
        // End of On create
    }
}