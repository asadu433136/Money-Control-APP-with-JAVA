package com.example.pluralcode;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link budgetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class budgetFragment extends Fragment implements budgetAdapter.ClickListener{

    ////////////////////////////////////////////////
    FirebaseUser user;
    String userID;
    //////////////////////////////////////////////
    String projectName;

    private RecyclerView mRecyclerView;
    private ArrayList<Budget>budgets;
    private ArrayList<String> keysList;
    private budgetAdapter adapter;
    DatabaseReference myRef;
    DatabaseReference transRef;
    String key;
    String initialCategory;
    int itemPosition;
    //add new budget

    private Spinner spinnerBudgetCat;
    private EditText inputBudget;
    private Button addBudget, cancelBudget, showBudget, editBudget,deleteBudget;
    private ConstraintLayout budgetContainer;

    //for capturing clicked budget
    private ArrayList<String> categoriesList;
    private ArrayList<String> amountList;



    //progress for different categories
    private int food,groceries,entertainment,household,education,health,gift,other;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public budgetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment budgetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static budgetFragment newInstance(String param1, String param2) {
        budgetFragment fragment = new budgetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            projectName=mParam1;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_budget, container, false);

        final Context context = getActivity().getApplicationContext();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_budget);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        keysList=new ArrayList<String>();
        budgets=new ArrayList<Budget>();
        categoriesList= new ArrayList<>();
        amountList=new ArrayList<>();

        //addition
        spinnerBudgetCat = (Spinner)rootView.findViewById(R.id.spinner_budget_category);
        inputBudget = (EditText) rootView.findViewById(R.id.input_budget_amount);

        editBudget = (Button)rootView.findViewById(R.id.edit_budget);
        deleteBudget = (Button)rootView.findViewById(R.id.delete_budget);
        addBudget=(Button)rootView.findViewById(R.id.submit_budget);
        cancelBudget=(Button)rootView.findViewById(R.id.cancel_budget);
        showBudget=(Button)rootView.findViewById(R.id.show_budget_button);
        budgetContainer=(ConstraintLayout)rootView.findViewById(R.id.budget_add_container);

        ////initially disable buttons
        editBudget.setEnabled(false);
        deleteBudget.setEnabled(false);

        key=null;



        //adding process
        ////////////////////////////////////////////////////////////

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        Budget budget = new Budget();

        ///////////////////////////////////////////////
        //// my database ref load ////////////
        myRef= FirebaseDatabase.getInstance("https://money-control-c1462-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users").child(userID).child(projectName);
        myRef.keepSynced(true);
        transRef= FirebaseDatabase.getInstance("https://money-control-c1462-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users").child(userID).child(projectName).child("transactions");
        transRef.keepSynced(true);
        //get the progress amounts
        transRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    for (DataSnapshot npsnapshot : snapshot.getChildren()){
                        Transaction t=npsnapshot.getValue(Transaction.class);
                        //String key = npsnapshot.getKey()+"";

                        String sAmount =t.getAmount()+"";
                        int amount = Integer.parseInt(sAmount);
                        String category = t.getCategory()+"";

                        //food,groceries,entertainment,household,education,health,gift,other

                        if(category.equals("Food")){food+=amount;}
                        else if(category.equals("Groceries")){groceries+=amount;}
                        else if(category.equals("Entertainment")){entertainment+=amount;}
                        else if(category.equals("Household")){household+=amount;}
                        else if(category.equals("Education")){education+=amount;}
                        else if(category.equals("Health")){health+=amount;}
                        else if(category.equals("Gift")){gift+=amount;}
                        else{other+=amount;}

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                budgets.clear();
                if(snapshot.exists()){

                    snapshot= snapshot.child("budgets");
                    for (DataSnapshot npsnapshot : snapshot.getChildren()){
                        Budget b=npsnapshot.getValue(Budget.class);
                        String key = npsnapshot.getKey()+"";

                        // format data
                        String category = b.getCategory()+"";


                        if(category.equals("Food")){b.setProgress(String.valueOf(food));}
                        else if(category.equals("Groceries")){b.setProgress(String.valueOf(groceries));}
                        else if(category.equals("Entertainment")){b.setProgress(String.valueOf(entertainment));}
                        else if(category.equals("Household")){b.setProgress(String.valueOf(household));}
                        else if(category.equals("Education")){b.setProgress(String.valueOf(education));}
                        else if(category.equals("Health")){b.setProgress(String.valueOf(health));}
                        else if(category.equals("Gift")){b.setProgress(String.valueOf(gift));}
                        else if(category.equals("Other")){b.setProgress(String.valueOf(other));}
                        else{b.setProgress(String.valueOf(0));}


                        budgets.add(b);
                        keysList.add(key);
                        categoriesList.add(b.getCategory()+"");
                        amountList.add(b.getAmount());
                    }

                    adapter=new budgetAdapter(budgets,budgetFragment.this);
                    mRecyclerView.setAdapter(adapter);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        budgetContainer.setVisibility(View.GONE);

        //Adapter for Expenses Category
        ArrayAdapter<String> budgetAdapter = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.categoriesExpenses)
        );
        budgetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBudgetCat.setAdapter(budgetAdapter);

        /////////////////////////////////////////////////////////////////////////
        //////////////// Button Functions  ///////////////////////////////////////

        showBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key=null;
                show();
            }
        });

        addBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> categoriesExist= new ArrayList<>();
                String selectedCategory = spinnerBudgetCat.getSelectedItem().toString();
                String progress="";
                if(!budgets.isEmpty()){

                    for(Budget b: budgets){
                        categoriesExist.add(b.getCategory()+"");
                    }

                }

                if(inputBudget.getText().toString().equals("") || inputBudget.getText().toString().equals(null) || inputBudget.getText().toString().length()>7 || !isNumeric(inputBudget.getText().toString()) ){
                    inputBudget.setError("Input correct details");
                    return;

                }

                else if(categoriesExist.contains(selectedCategory)){
                    Toast.makeText(context,"Budget already set, choose another category",Toast.LENGTH_LONG);
                    return;
                }

                budget.setCategory(selectedCategory);
                budget.setAmount(inputBudget.getText().toString());


                //////////////////////////////////////////////////
                ///////// set progress  /////////////////////////

                if(selectedCategory.equals("Food")){progress=String.valueOf(food);}
                else if(selectedCategory.equals("Groceries")){progress=String.valueOf(groceries);}
                else if(selectedCategory.equals("Entertainment")){progress=String.valueOf(entertainment);}
                else if(selectedCategory.equals("Household")){progress=String.valueOf(household);}
                else if(selectedCategory.equals("Education")){progress=String.valueOf(education);}
                else if(selectedCategory.equals("Health")){progress=String.valueOf(health);}
                else if(selectedCategory.equals("Gift")){progress=String.valueOf(gift);}
                else{progress=String.valueOf(other);}


                budget.setProgress(progress);


                String genKey= selectedCategory.substring(0,4) + String.valueOf(System.currentTimeMillis());
                myRef.child("budgets").child(genKey).setValue(budget);
                budgets.add(budget);
                keysList.add(genKey);
                adapter.notifyDataSetChanged();


                budgetContainer.setVisibility(View.GONE);

                Toast.makeText(context,"Budget added successfully",Toast.LENGTH_LONG).show();

            }
        });

        editBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedCategory = spinnerBudgetCat.getSelectedItem().toString();
                String amount = inputBudget.getText().toString();
                String progress;
                Budget b = new Budget();

                if( (selectedCategory.contains("Select")) || amount=="" || amount.length()>7 ){
                    Toast.makeText(context,"Enter correct details",Toast.LENGTH_LONG).show();
                }
                else{
                    //////////////////////////////////////////////////
                    ///////// set progress  /////////////////////////

                    if(selectedCategory.equals("Food")){progress=String.valueOf(food);}
                    else if(selectedCategory.equals("Groceries")){progress=String.valueOf(groceries);}
                    else if(selectedCategory.equals("Entertainment")){progress=String.valueOf(entertainment);}
                    else if(selectedCategory.equals("Household")){progress=String.valueOf(household);}
                    else if(selectedCategory.equals("Education")){progress=String.valueOf(education);}
                    else if(selectedCategory.equals("Health")){progress=String.valueOf(health);}
                    else if(selectedCategory.equals("Gift")){progress=String.valueOf(gift);}
                    else{progress=String.valueOf(other);}

                    b.setProgress(progress);
                    b.setAmount(amount);
                    b.setCategory(selectedCategory);

                    if((!initialCategory.equals(selectedCategory)) && categoriesList.contains(selectedCategory)){
                        Toast.makeText(context,"Budget Already exists",Toast.LENGTH_LONG).show();
                    }
                    else{
                        myRef.child("budgets").child(key).setValue(b);

                        Toast.makeText(context,"successful",Toast.LENGTH_LONG).show();

                        budgetContainer.setVisibility(View.GONE);

                        //key=null;
                    }


                }

            }
        });

        deleteBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myRef.child("budgets").child(key).removeValue();

                String genKey = keysList.get(itemPosition);
                keysList.remove(itemPosition);
                budgets.remove(itemPosition);


                adapter.notifyDataSetChanged();
                budgetContainer.setVisibility(View.GONE);
                key=null;
                Toast.makeText(context,"budget deleted",Toast.LENGTH_LONG).show();
            }
        });

        cancelBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                budgetContainer.setVisibility(View.GONE);
                
                // Disable buttons
                Drawable buttonDrawable = editBudget.getBackground();
                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                //the color is a direct color int and not a color resource
                DrawableCompat.setTint(buttonDrawable, Color.parseColor("#d3d3d3"));

                editBudget.setBackground(buttonDrawable);
                deleteBudget.setBackground(buttonDrawable);

                editBudget.setEnabled(false);
                deleteBudget.setEnabled(false);

                key=null;
            }
        });






        return rootView;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////// END OF ONCREATE ///////////////////////////////////////////////////////////

    @Override
    public void onItemClick(int position) {
        key = keysList.get(position);
        itemPosition=position;
        show();

        /*
        Intent i = new Intent(getActivity(),deleteBudget.class);
        i.putExtra("keyToDelete",key);
        startActivity(i);

         */
    }

    @Override
    public void onItemLongClick(int position, View v) {

    }

    public void show() {
        if(budgetContainer.getVisibility()== View.VISIBLE){
            budgetContainer.setVisibility(View.GONE);
        }else{
            if(key==null){
                Drawable buttonDrawable = editBudget.getBackground();
                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                //the color is a direct color int and not a color resource
                DrawableCompat.setTint(buttonDrawable, Color.parseColor("#d3d3d3"));

                editBudget.setBackground(buttonDrawable);
                deleteBudget.setBackground(buttonDrawable);

                editBudget.setEnabled(false);
                deleteBudget.setEnabled(false);

                //initialise text fields
                inputBudget.setText("");
                spinnerBudgetCat.setSelection(0);

                budgetContainer.setVisibility(View.VISIBLE);
            }
            else{
                Drawable buttonDrawable = editBudget.getBackground();
                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                //the color is a direct color int and not a color resource
                DrawableCompat.setTint(buttonDrawable, Color.parseColor("#FFFFFF"));

                editBudget.setBackground(buttonDrawable);
                deleteBudget.setBackground(buttonDrawable);

                editBudget.setEnabled(true);
                deleteBudget.setEnabled(true);

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Budget b = new Budget();
                        snapshot=snapshot.child("budgets");
                            for (DataSnapshot npsnapshot : snapshot.getChildren()){
                                if(key!=null && key.equals(npsnapshot.getKey()+"")){
                                    b=npsnapshot.getValue(Budget.class);
                                }
                            }

                            //////////////////////////////////////////////////////////
                            Budget finalB = b;
                            spinnerBudgetCat.post(new Runnable() {
                                @Override
                                public void run() {
                                    String cat = finalB.getCategory()+"";
                                    for (int i = 0; i < spinnerBudgetCat.getCount(); i++) {
                                        if (spinnerBudgetCat.getItemAtPosition(i).equals(cat)) {
                                            spinnerBudgetCat.setSelection(i);
                                            break;
                                        }
                                    }
                                }
                            });
                            //////////////////////////////////////////////////////////////////////
                            initialCategory= b.getCategory()+"";

                            // make sure budget amount is no null

                                inputBudget.setText(b.getAmount() + "");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            budgetContainer.setVisibility(View.VISIBLE);
        }
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }



}
