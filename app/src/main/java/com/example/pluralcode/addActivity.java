package com.example.pluralcode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

public class addActivity extends AppCompatActivity {

    /////////////////////////////////////
    FirebaseUser user;
    String userID;
    //////////////////////////////////////
    String projectName;

    private Boolean newEntry;


    private Spinner spinnerTypeOfTr;
    private Spinner spinnerExpensesCat;
    private Spinner spinnerIncomeCat;
    private Spinner currency;
    private Spinner repeat;

    //date related variables
    private TextView displayDate;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    Button button,editButton,cancelButton,delete;

    String key;

    EditText amount;
    EditText description;
    EditText payment;
    EditText comment;

    FrameLayout contactFrame;
    TextView contactTxt;


    //time related variables
    private TextView timeDisplay;
    int hour;
    int minute;

    Transaction trans;

    private static final int CONTACT_PERMISSION_CODE = 1;
    private static final int CONTACT_PICK_CODE = 2;


    //firebase
    DatabaseReference ref;



    //OnCreate Method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);


        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();



        spinnerTypeOfTr = (Spinner)findViewById(R.id.spinnerType);
        spinnerExpensesCat = (Spinner)findViewById(R.id.spinnerExpensesCategories);
        spinnerIncomeCat = (Spinner)findViewById(R.id.spinnerIncomeCategories);
        currency = (Spinner)findViewById(R.id.spinner_choose_currency);
        repeat = (Spinner)findViewById(R.id.spinner_repeat);

        contactFrame =findViewById(R.id.contact_frame);
        contactTxt = findViewById(R.id.contact_edit);

        //Adapter for type of transaction
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(
                addActivity.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.typeOfTransaction)
        );
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeOfTr.setAdapter(typeAdapter);


        ///////////////////////////////////////////////////////////
        ////////////////   set visibility of income and expenses   //////////////

        spinnerTypeOfTr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = spinnerTypeOfTr.getSelectedItem().toString();
                if(type.equals("Income")){
                    spinnerIncomeCat.setVisibility(View.VISIBLE);
                    spinnerExpensesCat.setVisibility(View.GONE);
                }else if(type.equals("Expenses")){
                    spinnerExpensesCat.setVisibility(View.VISIBLE);
                    spinnerIncomeCat.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //Adapter for Currency
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<String>(
                addActivity.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.currency)
        );
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currency.setAdapter(currencyAdapter);

        //Adapter for Expenses Category
        ArrayAdapter<String> expensesCategoriesAdapter = new ArrayAdapter<String>(
                addActivity.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.categoriesExpenses)
        );
        expensesCategoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExpensesCat.setAdapter(expensesCategoriesAdapter);

        //Adapter for Income Category
        ArrayAdapter<String> incomeCategoriesAdapter = new ArrayAdapter<String>(
                addActivity.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.categoriesIncome)
        );
        incomeCategoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIncomeCat.setAdapter(incomeCategoriesAdapter);


        spinnerIncomeCat.setVisibility(View.GONE);


        //Adapter for repeat Category
        ArrayAdapter<String> repeatAdapter = new ArrayAdapter<String>(
                addActivity.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.repeat)
        );
        repeatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeat.setAdapter(repeatAdapter);
        // End of spinners


        //Beginning of date and time creation
        timeDisplay = (TextView)findViewById(R.id.time);
        displayDate = (TextView)findViewById(R.id.date);

        //initialise date and time

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        month=month+1;

        int hr = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);


        String dateString = day + "/" + month + "/" + year;

        displayDate.setText(dateString);
        timeDisplay.setText(String.format(Locale.getDefault(),"%02d:%02d",hr,min));


        //set onclick listener
        displayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog= new DatePickerDialog(
                    addActivity.this,
                    android.R.style.Theme_DeviceDefault_Dialog_Alert,
                        dateSetListener,
                        year,month,day
                );
                //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();
            }
        });

        dateSetListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;

                String dateString = dayOfMonth + "/" + month + "/" + year;
                displayDate.setText(dateString);
            }
        };
        // End of date creation




        //INSERT TO FIREBASE
        //////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////
        Intent i = getIntent();
        key = i.getStringExtra("keyToDelete");
        projectName= i.getStringExtra("projectName");



        ref = FirebaseDatabase.getInstance("https://money-control-c1462-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users").child(userID).child(projectName);
        ref.keepSynced(true);
        amount=(EditText)findViewById(R.id.amount);
        description=(EditText)findViewById(R.id.description);
        comment=(EditText)findViewById(R.id.comment);
        payment=(EditText)findViewById(R.id.payment);
        button = (Button) findViewById(R.id.commitAdd);
        editButton = (Button) findViewById(R.id.editTransaction);
        cancelButton = (Button) findViewById(R.id.cancelAdd);
        delete=(Button) findViewById(R.id.deleteTransaction);


        //////////////////////////////////////////////////////////

        editButton.setEnabled(false);
        delete.setEnabled(false);

        if(key!=null){
            Drawable buttonDrawable = editButton.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            //the color is a direct color int and not a color resource
            DrawableCompat.setTint(buttonDrawable, Color.parseColor("#483D8B"));

            editButton.setBackground(buttonDrawable);
            delete.setBackground(buttonDrawable);

            editButton.setEnabled(true);
            delete.setEnabled(true);
        }


        ///////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////  transaction to send  /////////////////////////////////////////////////////
        trans = new Transaction();

        ////////////////////////////////////////////////////////////////
        // //////////loadup add activity for edit


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    dataSnapshot = dataSnapshot.child("transactions");
                    newEntry=false;
                    /// if edit ///////////////////////////////////
                    if(key!=null){
                        Transaction t = new Transaction();
                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()){
                            if(key.equals(npsnapshot.getKey()+"")){
                                t=npsnapshot.getValue(Transaction.class);
                            }
                        }

                        amount.setText(t.getAmount()+"");

                        /////////////////////////////////////////////
                        Transaction finalT = t;
                        spinnerTypeOfTr.post(new Runnable() {
                            @Override
                            public void run() {
                                String type = finalT.getType()+"";
                                for (int i = 0; i < spinnerTypeOfTr.getCount(); i++) {
                                    if (spinnerTypeOfTr.getItemAtPosition(i).equals(type)) {
                                        spinnerTypeOfTr.setSelection(i);
                                        break;
                                    }
                                }
                            }
                        });

                        ////////////////////////////////////////////////////////
                        currency.post(new Runnable() {
                            @Override
                            public void run() {
                                String curr = finalT.getCurrency()+"";
                                for (int i = 0; i < currency.getCount(); i++) {
                                    if (currency.getItemAtPosition(i).equals(curr)) {
                                        currency.setSelection(i);
                                        break;
                                    }
                                }
                            }
                        });
                        ////////////////////////////////////////////////////////
                        spinnerExpensesCat.post(new Runnable() {
                            @Override
                            public void run() {
                                String cat = finalT.getCategory()+"";
                                for (int i = 0; i < spinnerExpensesCat.getCount(); i++) {
                                    if (spinnerExpensesCat.getItemAtPosition(i).equals(cat)) {
                                        spinnerExpensesCat.setSelection(i);
                                        break;
                                    }
                                }
                            }
                        });
                        ///////////////////////////////////////////////////////////
                        spinnerIncomeCat.post(new Runnable() {
                            @Override
                            public void run() {
                                String cat = finalT.getCategory()+"";
                                for (int i = 0; i < spinnerIncomeCat.getCount(); i++) {
                                    if (spinnerIncomeCat.getItemAtPosition(i).equals(cat)) {
                                        spinnerIncomeCat.setSelection(i);
                                        break;
                                    }
                                }
                            }
                        });
                        ///////////////////////////////////////////////////////////////////
                        repeat.post(new Runnable() {
                            @Override
                            public void run() {
                                String rep = finalT.getRepeat()+"";
                                for (int i = 0; i < repeat.getCount(); i++) {
                                    if (repeat.getItemAtPosition(i).equals(rep)) {
                                        repeat.setSelection(i);
                                        break;
                                    }
                                }
                            }
                        });
                        ///////////////////////////////////////////////////////////////////

                        description.setText(t.getDescription()+"");
                        comment.setText(t.getComment()+"");
                        payment.setText(t.getPayment_type()+"");
                        displayDate.setText(t.getDate()+"");
                        timeDisplay.setText(t.getTime()+"");
                        contactTxt.setText(t.getContact()+"");

                    }
                }
                ///////////////////////////////////
                // else datasnaphot not exist ////
                else{
                    newEntry = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category="";
                String curr = currency.getSelectedItem().toString();
                if(spinnerExpensesCat.getVisibility()== View.VISIBLE){
                    category=spinnerExpensesCat.getSelectedItem().toString();
                }else if(spinnerIncomeCat.getVisibility()== View.VISIBLE){
                    category=spinnerIncomeCat.getSelectedItem().toString();
                }

                if(amount.getText().toString().equals(null) || amount.getText().toString().length()>7 || category.contains("Select")){
                    Toast.makeText(addActivity.this,"Enter correct details",Toast.LENGTH_LONG).show();
                    return;
                }
                if(description.getText().toString().equals(null) || description.getText().toString().equals("") ){
                    description.setError("Input Name");
                    return;
                }

                if(comment.getText().toString().equals(null) || comment.getText().toString().equals("") ){
                    comment.setError("Input Name");
                    return;
                }

                if(curr.contains("Select")){
                    Toast.makeText(addActivity.this,"Choose currency",Toast.LENGTH_LONG).show();
                    return;
                }
                if(curr.contains("Select")){
                    Toast.makeText(addActivity.this,"Choose currency",Toast.LENGTH_LONG).show();
                    return;
                }
                if(displayDate.getText().toString().contains("Select")){
                    Toast.makeText(addActivity.this,"Choose date",Toast.LENGTH_LONG).show();
                    return;
                }
                if(timeDisplay.getText().toString().contains("Select")){
                    Toast.makeText(addActivity.this,"Choose time",Toast.LENGTH_LONG).show();
                    return;
                }
                int mAmount= Integer.parseInt(amount.getText().toString().trim());

                trans.setCurrency(currency.getSelectedItem().toString().trim());
                trans.setAmount(mAmount);
                trans.setDescription(description.getText().toString().trim());
                trans.setPayment_type(payment.getText().toString().trim());
                trans.setCategory(category.trim());
                trans.setType(spinnerTypeOfTr.getSelectedItem().toString().trim());
                trans.setDate(displayDate.getText().toString().trim());
                trans.setTime(timeDisplay.getText().toString().trim());
                trans.setComment(comment.getText().toString().trim());
                trans.setContact(contactTxt.getText().toString().trim());
                trans.setRepeat(repeat.getSelectedItem().toString().trim());


                ref.child("transactions").push().setValue(trans);
                Toast.makeText(addActivity.this,"successful",Toast.LENGTH_LONG).show();

                Intent i = new Intent(addActivity.this, MainActivity.class);
                i.putExtra("projectName", projectName);
                startActivity(i);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String category="";
                String curr = currency.getSelectedItem().toString();

                if(spinnerExpensesCat.getVisibility()== View.VISIBLE){
                    category=spinnerExpensesCat.getSelectedItem().toString();
                }else if(spinnerIncomeCat.getVisibility()== View.VISIBLE){
                    category=spinnerIncomeCat.getSelectedItem().toString();
                }

                if(amount.getText().toString().equals(null) || amount.getText().toString().length()>7 || category.contains("Select")){
                    Toast.makeText(addActivity.this,"Enter correct details",Toast.LENGTH_LONG).show();
                    return;
                }
                if(description.getText().toString().equals(null) || description.getText().toString().equals("") ){
                    description.setError("Input Name");
                    return;
                }

                if(comment.getText().toString().equals(null) || comment.getText().toString().equals("") ){
                    comment.setError("Input Name");
                    return;
                }
                if(curr.contains("Select")){
                    Toast.makeText(addActivity.this,"Choose currency",Toast.LENGTH_LONG).show();
                    return;
                }
                if(curr.contains("Select")){
                    Toast.makeText(addActivity.this,"Choose currency",Toast.LENGTH_LONG).show();
                    return;
                }
                if(displayDate.getText().toString().contains("Select")){
                    Toast.makeText(addActivity.this,"Choose date",Toast.LENGTH_LONG).show();
                    return;
                }
                if(timeDisplay.getText().toString().contains("Select")){
                    Toast.makeText(addActivity.this,"Choose time",Toast.LENGTH_LONG).show();
                    return;
                }
                int mAmount= Integer.parseInt(amount.getText().toString().trim());
                trans.setAmount(mAmount);
                trans.setDescription(description.getText().toString().trim());
                trans.setPayment_type(payment.getText().toString().trim());
                trans.setCategory(category.trim());
                trans.setType(spinnerTypeOfTr.getSelectedItem().toString().trim());
                trans.setDate(displayDate.getText().toString().trim());
                trans.setTime(timeDisplay.getText().toString().trim());
                trans.setComment(comment.getText().toString().trim());
                trans.setCurrency(currency.getSelectedItem().toString().trim());
                trans.setContact(contactTxt.getText().toString().trim());
                trans.setRepeat(repeat.getSelectedItem().toString().trim());


                ref.child("transactions").child(key).setValue(trans);
                Toast.makeText(addActivity.this,"successful",Toast.LENGTH_LONG).show();

                Intent i = new Intent(addActivity.this, MainActivity.class);
                i.putExtra("keyToDelete",key);
                i.putExtra("projectName", projectName);
                startActivity(i);
                overridePendingTransition(0, 0);
                finish();


            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.child("transactions").child(key).removeValue();

                Intent i = new Intent(addActivity.this,MainActivity.class);
                i.putExtra("keyToDelete",key);
                i.putExtra("projectName", projectName);
                startActivity(i);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        contactFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //first we need to check read contact permission
                if (checkContactPermission()){
                    //permission granted, pick contact
                    pickContactIntent();
                }
                else {
                    //permission not granted, request
                    requestContactPermission();
                }
            }
        });

        getSupportActionBar().hide();
    }
    // end of onCreate method

    // time picker/display method -- done outside of the onCreate method
    public void displayTime(View v){

        TimePickerDialog.OnTimeSetListener timeSetListener= new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hour = hourOfDay;
                minute = minute;
                String timeString = hour + ":" + minute;

                timeDisplay.setText(String.format(Locale.getDefault(),"%02d:%02d",hour,minute));
            }
        };

        TimePickerDialog timePickerDialog= new TimePickerDialog(
                addActivity.this,
                AlertDialog.THEME_HOLO_DARK,
                timeSetListener,
                hour,minute,true
                );
        timePickerDialog.show();


    }


    //Get Contact Codes
    public boolean checkContactPermission(){
        boolean result = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS) == (PackageManager.PERMISSION_GRANTED
        );

        return result;  //true if permission granted, false if not
    }

    private void pickContactIntent(){
        //intent to pick contact
        //Intent intent = new Intent (Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, CONTACT_PICK_CODE);
    }

    private void requestContactPermission(){
        //permissions to request
        String[] permission = {Manifest.permission.READ_CONTACTS};

        ActivityCompat.requestPermissions(this, permission, CONTACT_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //handle permission request result
        if (requestCode == CONTACT_PERMISSION_CODE){
            if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //permission granted, can pick contact now
                pickContactIntent();
            }
            else {
                //permission denied
                Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //handle intent results
        if (resultCode == RESULT_OK){
            //calls when user click a contact from list

            if (requestCode == CONTACT_PICK_CODE){
                contactTxt.setText("");

                Cursor cursor1, cursor2;

                //get data from intent
                Uri uri = data.getData();

                cursor1 = getContentResolver().query(uri, null, null, null, null);

                if (cursor1.moveToFirst()){
                    //get contact details
                    //String contactId = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts._ID));
                    String contactName = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    //String contactThumnail = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                    //String idResults = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    //int idResultHold = Integer.parseInt(idResults);

                    contactTxt.setText(contactName);

                    /*
                    if (idResultHold == 1){
                        cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+contactId,
                                null,
                                null
                        );
                        //a contact may have multiple phone numbers
                        while (cursor2.moveToNext()){
                            //get phone number
                            String contactNumber = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            //set details

                            contactTxt.append("\nPhone: "+contactNumber);
                            //before setting image, check if have or not
                            if (contactThumnail != null){
                                thumbnailIv.setImageURI(Uri.parse(contactThumnail));
                            }
                            else {
                                thumbnailIv.setImageResource(R.drawable.ic_person);
                            }
                        }
                        cursor2.close();
                    }

                     */
                    cursor1.close();
                }
            }

        }
        else {
            //calls when user click back button | don't pick contact
        }
    }

    //End of contact

    public void cancel(View V){
        finish();
        overridePendingTransition(0, 0);
    }



}