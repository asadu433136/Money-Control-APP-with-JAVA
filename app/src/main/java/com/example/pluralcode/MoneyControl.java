package com.example.pluralcode;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class MoneyControl extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance("https://money-control-c1462-default-rtdb.europe-west1.firebasedatabase.app/").setPersistenceEnabled(true);
    }
}
