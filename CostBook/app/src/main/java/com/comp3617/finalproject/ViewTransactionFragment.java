package com.comp3617.finalproject;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.comp3617.finalproject.HelperClasses.CostTransaction;
import com.comp3617.finalproject.HelperClasses.RecyclerViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewTransactionFragment extends Fragment {

    private static final String TAG = "ViewTransactionFragment";
    private DatabaseReference databaseReference;
    private RecyclerView transRecyclerView;
    private Spinner categorySpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_view_transaction_fragment,
                container, false);

        // Retrieve Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Category Spinner - Default template
        categorySpinner = (Spinner) view.findViewById(R.id.category_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.view_category_array, R.layout.spinner_layout);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        // Apply the adapter to the spinner
        categorySpinner.setAdapter(adapter);

        // Search Button
        Button btnSearch = (Button) view.findViewById(R.id.btn_search_trans);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCostTransaction();
            }
        });

        // Transaction Recycler View
        transRecyclerView = (RecyclerView) view.findViewById(R.id.trans_list);

        LinearLayoutManager recyclerLayoutManager =
                new LinearLayoutManager(getActivity().getApplicationContext());
        transRecyclerView.setLayoutManager(recyclerLayoutManager);

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(transRecyclerView.getContext(),
                        recyclerLayoutManager.getOrientation());
        transRecyclerView.addItemDecoration(dividerItemDecoration);

        // Initial Query Realtime results through ValueEventListener
        returnAllTransactions();

        return view;
    }

    /**
     *  Makes a retrieve from database of transactions depending on category.
     */
    public void getCostTransaction() {
        // Read from Category textView
        String category = categorySpinner.getSelectedItem().toString();
        getCostTransactionFromDb(category);

        // NOTE* Fixes issue of "inputConnection failed" error!!?
//        searchResult.clearFocus();
    }

    /**
     * Queries database for all transactions of a category.
     * @param category Category of the transactions being retrieved
     */
    private void getCostTransactionFromDb(final String category) {

        if(category.equals("All")) {
            returnAllTransactions();
        } else {
            // Realtime results through ValueEventListener
            databaseReference.child("transactions").orderByChild("category")
                    .equalTo(category).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<CostTransaction> transList = new ArrayList<CostTransaction>();
                    for (DataSnapshot transSnapshot : dataSnapshot.getChildren()) {
                        // Limits the query results to 20
                        if (transList.size() != 20) {
                            transList.add(transSnapshot.getValue(CostTransaction.class));
                        }
                    }
                    // Debugging
                    Log.d(TAG, "Number of results for this search is " + transList.size());
                    Toast.makeText(getActivity(),
                            "Number of results for this search is " + transList.size(),
                            Toast.LENGTH_SHORT).show();

                    RecyclerViewAdapter recyclerViewAdapter = new
                            RecyclerViewAdapter(transList, getActivity());
                    transRecyclerView.setAdapter(recyclerViewAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "Error trying to get transactions for category: " + category
                            + " " + databaseError);
                    Toast.makeText(getActivity(),
                            "Error trying to get transactions for category: " + category,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     *  Returns all transactions irregardless of category
     */
    public void returnAllTransactions() {
        databaseReference.child("transactions").orderByChild("day").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<CostTransaction> transList = new ArrayList<CostTransaction>();
                for (DataSnapshot transSnapshot: dataSnapshot.getChildren()) {
                    // Limits the query results to 20
                    if (transList.size() != 20) {
                        transList.add(transSnapshot.getValue(CostTransaction.class));
                    }
                }
                // Debugging
                Log.d(TAG, "Number of results for this search is " + transList.size());
                Toast.makeText(getActivity(),
                        "Number of results for this search is " + transList.size(),
                        Toast.LENGTH_SHORT).show();

                RecyclerViewAdapter recyclerViewAdapter = new
                        RecyclerViewAdapter(transList, getActivity());
                transRecyclerView.setAdapter(recyclerViewAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Error trying to get initial transactions for category: "
                        + databaseError);
                Toast.makeText(getActivity(),
                        "Error trying to get initial transactions",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onPause() {
        Log.e("DEBUG", "OnPause of ViewTransactionFragment");
        super.onPause();
    }
}
