package com.example.pluralcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    /////////////////////////////////////
    FirebaseUser user;
    String userID;
    //////////////////////////////////////
    EditText fullName, email, password, phone;
    Button register;
    TextView login;
    DatabaseReference ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullName=findViewById(R.id.full_name_editText);
        email = findViewById(R.id.email_editText);
        password= findViewById(R.id.password_editText);
        phone = findViewById(R.id.phone_editText);
        register = findViewById(R.id.register_button);
        login = findViewById(R.id.login_from_register);

        firebaseAuth = FirebaseAuth.getInstance();
        ref = ref = FirebaseDatabase.getInstance("https://money-control-c1462-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users");
        ref.keepSynced(false);

        if(firebaseAuth.getCurrentUser()!=null){
            Intent i = new Intent(register.this, projects.class);
            startActivity(i);
            finish();
        }


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mFullName = fullName.getText().toString().trim();
                String mEmail = email.getText().toString().trim();
                String mPassword = password.getText().toString().trim();
                String mPhone = phone.getText().toString().trim();

                if(TextUtils.isEmpty(mFullName)){
                    fullName.setError("Full name is required.");
                    return;
                }
                if(TextUtils.isEmpty(mEmail)){
                    email.setError("Email is required.");
                    return;
                }
                if(TextUtils.isEmpty(mPassword)){
                    password.setError("Password is required.");
                    return;
                }
                if(mPassword.length()<6){
                    password.setError("Password should be 6 characters or longer.");
                    return;
                }

                //register

                firebaseAuth.createUserWithEmailAndPassword(mEmail,mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(register.this,"Successfully Registered",Toast.LENGTH_SHORT).show();

                            /// set default stats for new user
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            userID = user.getUid();
                            ref = ref.child(userID).child("user_info");

                            ref.child("full_name").setValue(mFullName);
                            ref.child("phone").setValue(mPhone);
                            ref.child("currency").setValue("Euro");






                            //////
                            Intent i = new Intent(register.this, projects.class);
                            startActivity(i);
                            overridePendingTransition(0, 0);
                        }else{
                            Toast.makeText(register.this,"Not Registered : " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });


        //Go to Login

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(register.this, login.class);
                startActivity(i);
                overridePendingTransition(0, 0);
                finish();
            }
        });


        getSupportActionBar().hide();
    }
}