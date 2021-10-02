package com.example.pluralcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText email, password;
    Button login;
    TextView register, forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email_editText);
        password= findViewById(R.id.password_editText);
        register = findViewById(R.id.register_from_login);
        forgotPassword = findViewById(R.id.forgot_password_txt);
        login = findViewById(R.id.login_button);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null){
            Intent i = new Intent(login.this, projects.class);
            startActivity(i);
            finish();
        }


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = email.getText().toString().trim();
                String mPassword = password.getText().toString().trim();
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

                //log in

                firebaseAuth.signInWithEmailAndPassword(mEmail,mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(login.this,"Successfully logged in",Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(login.this, projects.class);
                            startActivity(i);
                            overridePendingTransition(0, 0);

                        }else{
                            Toast.makeText(login.this,"Error : " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        //go to register

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), register.class);
                startActivity(i);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        //forgot password

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText email = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetA = new AlertDialog.Builder(v.getContext());
                passwordResetA.setTitle("Reset Password");
                passwordResetA.setMessage("Enter your Email to receive Reset Link");
                passwordResetA.setView(email);

                passwordResetA.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // send reset link to email
                        String mail = email.getText().toString().trim();
                        firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(login.this,"Password Reset Email successfully sent",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(login.this,"Error! Reset link Not sent " + e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordResetA.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                        dialog.cancel();
                    }
                });

                //show alert
                passwordResetA.create().show();
            }
        });


        getSupportActionBar().hide();
    }
}