package com.comp3617.finalproject;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.comp3617.finalproject.HelperClasses.CostTransaction;
import com.comp3617.finalproject.HelperClasses.DatePickerFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;

public class AddTransactionFragment extends Fragment
        implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "AddTransactionFragment";

    private DatabaseReference dbRef;
    private int nextTransactionID;
    private boolean isEdit;
    private String transactionId;
    private Button addButton;
    private ImageButton dateButton;
    private int dateYear;
    private int dateMonth;
    private int dateDay;
    private TextView headTxt;
    private ImageView categoryImage;
    private TextView displayDate;
    private Spinner categorySpinner;
    private FragmentManager fm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_add_transaction_fragment,
                container, false);

        // Retrieve firebase reference
        dbRef = FirebaseDatabase.getInstance().getReference();

        // UI Elements
        headTxt = view.findViewById(R.id.add_head_tv);
        categoryImage = view.findViewById(R.id.categoryImage);
        displayDate = view.findViewById(R.id.date_display_tv);
        addButton = (Button) view.findViewById(R.id.btn_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEdit) {
                    addEvent();
                } else {
                    updateEvent();
                }
            }
        });

        // DatePicker button
        dateButton = (ImageButton) view.findViewById(R.id.btn_date_dialog);
        dateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                // Needs to pass in Fragment reference
                datePicker.setTargetFragment(AddTransactionFragment.this,0);
                datePicker.show(getFragmentManager(), "date Picker");
            }
        });

        // Category Spinner - Default template
        categorySpinner = (Spinner) view.findViewById(R.id.category_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.add_category_array, R.layout.spinner_layout);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        // Apply the adapter to the spinner
        categorySpinner.setAdapter(adapter);

        // OnItemSelectedListener for Spinner to change category image
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Debugging
//                userSelectedIndex = position;
//                Toast.makeText(getActivity(), "Position: " + position, Toast.LENGTH_LONG).show();
                switch(position) {
                    case 0:
                        categoryImage.setImageResource(R.drawable.category_food);
                        break;
                    case 1:
                        categoryImage.setImageResource(R.drawable.category_clothing);
                        break;
                    case 2:
                        categoryImage.setImageResource(R.drawable.category_bills);
                        break;
                    case 3:
                        categoryImage.setImageResource(R.drawable.category_gas);
                        break;
                    case 4:
                        categoryImage.setImageResource(R.drawable.category_education);
                        break;
                    case 5:
                        categoryImage.setImageResource(R.drawable.category_entertainment);
                        break;
                    case 6:
                        categoryImage.setImageResource(R.drawable.category_other);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //add or update depending on existence of transactionId in arguments
        if (getArguments() != null) {
            transactionId = getArguments().getString("TransactionId");
        }
        if (transactionId != null) {
            populateUpdateTransaction();
        }

        fm = getFragmentManager();

        return view;
    }

    /**
     * Retrieves the user selected date from the date dialog.
     * @param view
     * @param year
     * @param month
     * @param dayOfMonth
     */
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar selectedDate = Calendar.getInstance();
        // Storing date information
        selectedDate.set(Calendar.YEAR, year);
        dateYear = year;
        selectedDate.set(Calendar.MONTH, month);
        dateMonth = month;
        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        dateDay = dayOfMonth;
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL)
                .format(selectedDate.getTime());
        displayDate.setText(currentDate);
    }

    /**
     *  Invokes methods to create a new CostTransaction and add it to DB.
     */
    public void addEvent() {
        CostTransaction transaction = createCostTransactionObj();
        addTransactionToDB(transaction);
    }

    /**
     *  Invokes methods to create a new CostTransaction to replace transaction in DB.
     */
    public void updateEvent() {
        // HERE
        CostTransaction costTransaction = createCostTransactionObj();
        updateTransactionToDB(costTransaction);
    }

    /**
     *  Searches for transaction Id, and updates Transaction.
     * @param cTransaction new CostTransaction to update with
     */
    private void updateTransactionToDB(CostTransaction cTransaction) {
        // Toast for testing
        Toast.makeText(getActivity(),
                "updateTransactionToDB method invoked",
                Toast.LENGTH_LONG).show();
        addCostTransaction(cTransaction, transactionId);
    }

    /**
     *  Checks for number of transaction Ids and Adds CostTransaction object to database.
     * @param costTransaction CostTransaction object to be added to DB
     */
    private void addTransactionToDB(final CostTransaction costTransaction) {
        final DatabaseReference idDatabaseRef = FirebaseDatabase.getInstance()
                .getReference("TransactionIDs").child("id");
        // Keep count of the number of IDs
        Log.d(TAG, "addTransactionToDB() run. TransactionIDs:" + idDatabaseRef.getDatabase());

        idDatabaseRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                // Create initial Id node if it doesn't exist, only runs once
                if (mutableData.getValue(int.class) == null) {
                    idDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //set initial value
                            if(dataSnapshot != null && dataSnapshot.getValue() == null){
                                idDatabaseRef.setValue(1);
                                Log.d(TAG, "Initial id is set");
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, "onCanceclled: " + databaseError);
                        }
                    });

                    Log.d(TAG, "Transaction id null so transaction aborted,");
                    return Transaction.abort();
                }

                nextTransactionID = mutableData.getValue(int.class);
                // Increment the number of Ids
                mutableData.setValue(nextTransactionID + 1);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean state,
                                   DataSnapshot dataSnapshot) {
                if (state) {
                    Log.d(TAG, "CostTransaction Id retrieved ");
                    addCostTransaction(costTransaction, "" + nextTransactionID);
                } else {
                    Log.d(TAG, "CostTransaction Id retrieval unsuccessful: " + databaseError);
                    Toast.makeText(getActivity(),
                            "There was a problem, please re-submit transaction again!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Adds CostTransaction Object to be added to Database.
     * @param costTransaction
     * @param transId
     */
    private void addCostTransaction(CostTransaction costTransaction, String transId) {
        costTransaction.setTransactionId(transId);
        dbRef.child("transactions").child(transId)
                .setValue(costTransaction)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if(isEdit){
                                // Sends Intent
                                addTransactions();
                            }else{
                                // Clears editText fields
                                restUi();
                            }
                            Log.d(TAG, "Transaction has been added to database.");
                            Toast.makeText(getActivity(),
                                    "Transaction has been successfully added",
                                    Toast.LENGTH_SHORT).show();
                            // Change view to History page
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.replace(R.id.adds_frame, new ViewTransactionFragment());
                            ft.commit();
                        } else {
                            Log.d(TAG, "Transaction couldn't be added to database.");
                            Toast.makeText(getActivity(),
                                    "Transaction could not be added",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Populate Transaction information for updates.
     */
    private void populateUpdateTransaction() {
        headTxt.setText("Edit Transaction");
        addButton.setText("Edit");
        isEdit = true;

        dbRef.child("transactions").child(transactionId).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        CostTransaction cTransaction = dataSnapshot.getValue(CostTransaction.class);
                        displayTransactionForUpdate(cTransaction);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "Error trying to get CostTransaction for update " +
                                "" + databaseError);
                        Toast.makeText(getActivity(),
                                "Please try to edit the transaction again",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     *  Displays information from a Transaction to be edited.
     *  Helper method for populateUpdateTransaction()
     * @param costTransaction Transaction to be displayed
     */
    private void displayTransactionForUpdate(CostTransaction costTransaction){

        ((EditText) getActivity()
                .findViewById(R.id.title_editText)).setText(costTransaction.getTitle());
        categorySpinner.setSelection(getIndex(categorySpinner, costTransaction.getCategory()));
        ((EditText) getActivity()
                .findViewById(R.id.desc_editText)).setText(costTransaction.getDescription());
        ((EditText) getActivity()
                .findViewById(R.id.cost_editText)).setText(costTransaction.getCost());

        // Need to consider how to update DatepickeerDialog
    }

    /**
     *  Creates a new CostTransaction object.
     * @return CostTransaction Object
     */
    private CostTransaction createCostTransactionObj() {
        final CostTransaction costTransaction = new CostTransaction();
        // Title
        costTransaction.setTitle(((EditText) getActivity()
                .findViewById(R.id.title_editText)).getText().toString());
        // Category - Pulls data from Category spinner
        costTransaction.setCategory(categorySpinner.getSelectedItem().toString());

        // Description
        costTransaction.setDescription(((EditText) getActivity()
                .findViewById(R.id.desc_editText)).getText().toString());
        // Cost
        costTransaction.setCost(((EditText) getActivity()
                .findViewById(R.id.cost_editText)).getText().toString());
        // Date
        costTransaction.setYear(dateYear);
        costTransaction.setMonth(dateMonth);
        costTransaction.setDay(dateDay);
        return costTransaction;
    }

    /**
     *  Clear the input fields for adding a new CostTranasction.
     */
    private void restUi() {
        ((EditText) getActivity()
                .findViewById(R.id.title_editText)).setText("");
        ((EditText) getActivity()
                .findViewById(R.id.desc_editText)).setText("");
        ((EditText) getActivity()
                .findViewById(R.id.cost_editText)).setText("");
    }

    /**
     * Finalizes adding transactions and sends Intent.
     * Helper method for addCostTransaction
     */
    private void addTransactions() {
        Intent i = new Intent();
        i.setClass(getActivity(), MainActivity.class);
        startActivity(i);
    }

    /**
     * Helper method for populating category spinner during transaction update.
     * @param spinner
     * @param myString
     * @return
     */
    private int getIndex(Spinner spinner, String myString){
        for (int i = 0; i < spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}