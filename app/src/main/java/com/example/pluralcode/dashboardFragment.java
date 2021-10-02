package com.example.pluralcode;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
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
 * Use the {@link dashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class dashboardFragment extends Fragment {

    /////////////////////////////////////
    FirebaseUser user;
    String userID;
    //////////////////////////////////////
    private String projectName;

    TextView totBalance;
    TextView totExpenses;
    TextView totIncome;
    String currency;
    String globalCurrency;
    double conversionRate;
    Button settingsButton, gotoProject;

    Double expenses;

    private ArrayList<String> categoryList;

    private ArrayList<Integer> amountList;

    private PieChart pieChart;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public dashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment dashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static dashboardFragment newInstance(String param1, String param2) {
        dashboardFragment fragment = new dashboardFragment();
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
        //Bundle bundle= this.getArguments();
        //String projectName = bundle.getString("projectName");

        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_dashboard, container, false);

        final Context context = getActivity().getApplicationContext();

        totBalance = (TextView)rootView.findViewById(R.id.totalBalanceValue) ;
        totExpenses = (TextView)rootView.findViewById(R.id.totalExpensesValue) ;
        totIncome = (TextView)rootView.findViewById(R.id.totalIncomeValue) ;
        settingsButton = (Button) rootView.findViewById(R.id.settings_button);
        gotoProject = (Button) rootView.findViewById(R.id.gotoprojects_button);

        pieChart = (PieChart) rootView.findViewById(R.id.pieChart);
        categoryList= new ArrayList<String>();
        amountList = new ArrayList<Integer>();

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        //Default Currency

        conversionRate =0.82;
        DatabaseReference currencyRef = FirebaseDatabase.getInstance("https://money-control-c1462-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users").child(userID);
        //currencyRef.keepSynced(true);
        currencyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //dynamic rate
                //conversionRate = Double.parseDouble(snapshot.child("conv_rate").getValue(String.class)+"");
                globalCurrency = snapshot.child("user_info").child("currency").getValue(String.class)+"";

                // work on numbers (combing all listeners)

                /////////////////////////////////////////////////
                // Load balance and Expenses   ///////////

                snapshot = snapshot.child(projectName).child("transactions");
                if (snapshot.exists()){
                    Double income=0.00;
                    expenses =0.00;
                    String category;

                    for (DataSnapshot npsnapshot : snapshot.getChildren()){
                        Transaction t=npsnapshot.getValue(Transaction.class);
                        String type = t.getType();


                        //get currency
                        currency = t.getCurrency()+"";

                        // get category
                        category = t.getCategory()+"";

                        Double amt= Double.parseDouble(t.getAmount()+"");
                        if(!currency.equals(globalCurrency)){
                            if(currency.equals("Dollar")){
                                amt = amt * conversionRate;
                            }else{
                                amt = amt / conversionRate;
                            }
                        }

                        if(type.equals("Income")){
                            income+=amt;

                            // get expenses , and add to list for pie chart
                        }else{
                            expenses+=amt;

                            if(categoryList.contains(category)){
                                int i = categoryList.indexOf(category);
                                amountList.set(i, amountList.get(i) + Integer.parseInt(t.getAmount()+""));
                            }else{
                                categoryList.add(category);
                                amountList.add(Integer.parseInt(t.getAmount()+""));
                            }

                        }
                    }


                    Double balance =  income-expenses;
                    if(balance>0){
                        totBalance.setTextColor(Color.parseColor("#008000"));
                    }else{
                        totBalance.setTextColor(Color.parseColor("#F04B28"));
                    }
                    balance=Math.abs(balance);


                    if(globalCurrency.equals("Dollar")){
                        globalCurrency="$";
                    }else{
                        globalCurrency="€";
                    }



                    totBalance.setText(globalCurrency + String.format("%.2f", balance));
                    totIncome.setText(globalCurrency + String.format("%.2f", income));
                    totExpenses.setText(globalCurrency + String.format("%.2f", expenses));

                    setupPieChart();
                    loadPieChartData();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        // settings
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, settings.class);
                i.putExtra("projectName", projectName);
                startActivity(i);
            }
        });


        gotoProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, projects.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        return rootView;
    }
    ////////////////////////////////////////////////////////////////////////////////
    ///////////// END OF ON CREATE

    private void setupPieChart() {
        //pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        //pieChart.setCenterText("Spending by Category");
        //pieChart.setCenterTextSize(24);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private void loadPieChartData(){

        ArrayList<PieEntry> entries = new ArrayList<>();

        for(int i=0; i<categoryList.size();i++){
            float a = amountList.get(i);
            float b = expenses.floatValue();
            entries.add(new PieEntry((a/b),categoryList.get(i)));
        }


        ArrayList<Integer> colors = new ArrayList<>();
        for (int color: ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color: ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expense Category");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);


        dataSet.notifyDataSetChanged();
        pieChart.notifyDataSetChanged();
        pieChart.setData(data);
        pieChart.invalidate();

        //pieChart.animateY(1400, Easing.EaseInOutQuad);
    }

}