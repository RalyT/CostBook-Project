package finalproject.comp3617.com.costbook.FunctionalityTest;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import finalproject.comp3617.com.costbook.R;

public class AddFragment extends Fragment {

    private static final String TAG = "AddAdFragment";

    private DatabaseReference dbRef;
    private int nextClassifiedID;
    private boolean isEdit;
    private String adId;
    private Button button;
    private TextView headTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_add_fragment,
                container, false);

        button = (Button) view.findViewById(R.id.post_add);
        headTxt = view.findViewById(R.id.add_head_tv);

        dbRef = FirebaseDatabase.getInstance().getReference();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEdit) {
                    addEvent();
                } else {
                    updateEvent();
                }

            }
        });

        //add or update depending on existence of adId in arguments
        if (getArguments() != null) {
            adId = getArguments().getString("adId");
        }
        if (adId != null) {
            populateUpdateAd();
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    // Adds the event of creating a ClassifiedAd object and adding it.
    public void addEvent() {
        ClassifiedAd classifiedAd = createClassifiedAdObj();
        addClassifiedToDB(classifiedAd);
    }

    public void updateEvent() {
        ClassifiedAd classifiedAd = createClassifiedAdObj();
        updateClassifiedToDB(classifiedAd);
    }

    // Adds ClassifiedObj to Database
    private void addClassifiedToDB(final ClassifiedAd classifiedAd) {
        final DatabaseReference idDatabaseRef = FirebaseDatabase.getInstance()
                .getReference("ClassifiedIDs").child("id");
        Log.d(TAG, "addClassifiedToDB run. ClassifiedIDs:" + idDatabaseRef.getDatabase());

        idDatabaseRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                //create id node if it doesn't exist
                //this code runs only once
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

                        }
                    });

                    Log.d(TAG, "Classified id null so " +
                            " transaction aborted, " );

                    return Transaction.abort();
                }

                nextClassifiedID = mutableData.getValue(int.class);
                mutableData.setValue(nextClassifiedID + 1);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean state,
                                   DataSnapshot dataSnapshot) {
                if (state) {
                    Log.d(TAG, "Classified id retrieved ");
                    addClassified(classifiedAd, ""+nextClassifiedID);
                } else {
                    Log.d(TAG, "Classified id retrieval unsuccessful " + databaseError);
                    Toast.makeText(getActivity(),
                            "There is a problem, please submit ad post again",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    // Add transaction to Database
    private void addClassified(ClassifiedAd classifiedAd, String cAdId) {
        classifiedAd.setAdId(cAdId);
        dbRef.child("classified").child(cAdId)
                .setValue(classifiedAd)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if(isEdit){
                                addClassifieds();
                            }else{
                                restUi();
                            }
                            Log.d(TAG, "Classified has been added to db");
                            Toast.makeText(getActivity(),
                                    "Classified has been posted",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "Classified couldn't be added to db");
                            Toast.makeText(getActivity(),
                                    "Classified could not be added",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void populateUpdateAd() {
        headTxt.setText("Edit Ad");
        button.setText("Edit Ad");
        isEdit = true;

        dbRef.child("classified").child(adId).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ClassifiedAd cAd = dataSnapshot.getValue(ClassifiedAd.class);
                        displayAdForUpdate(cAd);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "Error trying to get classified ad for update " +
                                ""+databaseError);
                        Toast.makeText(getActivity(),
                                "Please try classified edit action again",
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private void displayAdForUpdate(ClassifiedAd cAd){
        ((EditText) getActivity()
                .findViewById(R.id.title_a)).setText(cAd.getTitle());
        ((EditText) getActivity()
                .findViewById(R.id.category_a)).setText(cAd.getCategory());
        ((EditText) getActivity()
                .findViewById(R.id.desc_a)).setText(cAd.getDescription());
        ((EditText) getActivity()
                .findViewById(R.id.name_a)).setText(cAd.getName());
        ((EditText) getActivity()
                .findViewById(R.id.phone_a)).setText(cAd.getPhone());
        ((EditText) getActivity()
                .findViewById(R.id.city_a)).setText(cAd.getCity());
    }

    // Updates a new object
    private void updateClassifiedToDB(ClassifiedAd classifiedAd) {
        // Toast for testing
        Toast.makeText(getActivity(),
                "updateClassifiedToDB method invoked",
                Toast.LENGTH_LONG).show();
        addClassified(classifiedAd, adId);
    }

    // Creates a new object
    private ClassifiedAd createClassifiedAdObj() {
        final ClassifiedAd ad = new ClassifiedAd();
        ad.setTitle(((EditText) getActivity()
                .findViewById(R.id.title_a)).getText().toString());
        ad.setCategory(((EditText) getActivity()
                .findViewById(R.id.category_a)).getText().toString());
        ad.setDescription(((EditText) getActivity()
                .findViewById(R.id.desc_a)).getText().toString());
        ad.setName(((EditText) getActivity()
                .findViewById(R.id.name_a)).getText().toString());
        ad.setPhone(((EditText) getActivity()
                .findViewById(R.id.phone_a)).getText().toString());
        ad.setCity(((EditText) getActivity()
                .findViewById(R.id.city_a)).getText().toString());
        return ad;
    }

    // Clears the input fields
    private void restUi() {
        ((EditText) getActivity()
                .findViewById(R.id.title_a)).setText("");
        ((EditText) getActivity()
                .findViewById(R.id.category_a)).setText("");
        ((EditText) getActivity()
                .findViewById(R.id.desc_a)).setText("");
        ((EditText) getActivity()
                .findViewById(R.id.name_a)).setText("");
        ((EditText) getActivity()
                .findViewById(R.id.phone_a)).setText("");
        ((EditText) getActivity()
                .findViewById(R.id.city_a)).setText("");
    }

    //
    private void addClassifieds() {
        Intent i = new Intent();
        i.setClass(getActivity(), TestActivity.class);
        startActivity(i);
    }
}
