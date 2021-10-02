package com.example.pluralcode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
 * Use the {@link transactionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class transactionsFragment extends Fragment implements MyAdapter.ClickListener {

    /////////////////////////////////////
    FirebaseUser user;
    String userID;
    //////////////////////////////////////
    String projectName;

    private RecyclerView mRecyclerView;
    private ArrayList<Transaction>transactions;
    private ArrayList<String> keysList;
    private MyAdapter adapter;
    private SearchView searchView;
    DatabaseReference myRef;
    String key;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public transactionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment transactionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static transactionsFragment newInstance(String param1, String param2) {
        transactionsFragment fragment = new transactionsFragment();
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
       //
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragmen


        View rootView = inflater.inflate(R.layout.fragment_transactions, container, false);

        final Context context = getActivity().getApplicationContext();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_transactions);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        keysList=new ArrayList<String>();
        transactions=new ArrayList<Transaction>();
        searchView = (SearchView) rootView.findViewById(R.id.search_view);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();


        myRef= FirebaseDatabase.getInstance("https://money-control-c1462-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users").child(userID).child(projectName).child("transactions");
        myRef.keepSynced(true);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                for (DataSnapshot npsnapshot : dataSnapshot.getChildren()){
                    Transaction t=npsnapshot.getValue(Transaction.class);
                    String key = npsnapshot.getKey()+"";
                    Log.d("keys", key);

                    transactions.add(t);
                    keysList.add(key);
                }
                /// error
                adapter=new MyAdapter(transactions,transactionsFragment.this);
                mRecyclerView.setAdapter(adapter);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(searchView!=null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if(newText.equals("")){
                        MyAdapter filteredAdapter=new MyAdapter(transactions,transactionsFragment.this);
                        mRecyclerView.setAdapter(filteredAdapter);

                    }else{
                        search(newText);
                    }

                    return true;
                }
            });

        }
    }

    private void search(String str){
        ArrayList<Transaction> filteredTrans = new ArrayList<Transaction>();
        for(Transaction transaction: transactions){
            if(transaction.getCategory().toLowerCase().contains(str.toLowerCase())  ||
                    transaction.getPayment_type().toLowerCase().contains(str.toLowerCase())){
                filteredTrans.add(transaction);
            }


        }
        MyAdapter filteredAdapter=new MyAdapter(filteredTrans,this);
        mRecyclerView.setAdapter(filteredAdapter);
    }


    /// what happens when you click a transaction  //////
    ////////////////////////////////////////////////////
    @Override
    public void onItemClick(int position) {
        key = keysList.get(position);

        //Intent i = new Intent(getActivity(),deletePopUp.class);
        //i.putExtra("keyToDelete",key);
        Intent i = new Intent(getActivity(),addActivity.class);
        i.putExtra("keyToDelete",key);
        i.putExtra("projectName",projectName);
        startActivity(i);

    }

    public void delete(String key){
        myRef.child(key).removeValue();

        keysList.clear();
        transactions.clear();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot npsnapshot : snapshot.getChildren()){
                    Transaction t=npsnapshot.getValue(Transaction.class);
                    String key = npsnapshot.getKey()+"";
                    keysList.add(key);
                    transactions.add(t);
                }
                adapter=new MyAdapter(transactions,transactionsFragment.this);
                mRecyclerView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onItemLongClick(int position, View v) {

    }
}