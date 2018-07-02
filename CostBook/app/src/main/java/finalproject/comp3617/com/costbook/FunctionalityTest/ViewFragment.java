package finalproject.comp3617.com.costbook.FunctionalityTest;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import finalproject.comp3617.com.costbook.R;

public class ViewFragment extends Fragment {

    private static final String TAG = "ViewAdsFragment";
    private DatabaseReference databaseReference;
    private RecyclerView adsRecyclerView;
    private EditText searchResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_view_fragment,
                container, false);

        searchResult = view.findViewById(R.id.category_v);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        Button button = (Button) view.findViewById(R.id.view_adds_b);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getClassifiedAds();
            }
        });

        adsRecyclerView = (RecyclerView) view.findViewById(R.id.ads_lst);

        LinearLayoutManager recyclerLayoutManager =
                new LinearLayoutManager(getActivity().getApplicationContext());
        adsRecyclerView.setLayoutManager(recyclerLayoutManager);

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(adsRecyclerView.getContext(),
                        recyclerLayoutManager.getOrientation());
        adsRecyclerView.addItemDecoration(dividerItemDecoration);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void getClassifiedAds() {
        String category = ((TextView) getActivity()
                .findViewById(R.id.category_v)).getText().toString();
        getClassifiedsFromDb(category);

        // Fixes issue of inputConnection failed error!!
        searchResult.clearFocus();
    }

    private void getClassifiedsFromDb(final String category) {
        // Realtime results through ValueEventListener
        databaseReference.child("classified").orderByChild("category")
                .equalTo(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ClassifiedAd> adsList = new ArrayList<ClassifiedAd>();
                for (DataSnapshot adSnapshot: dataSnapshot.getChildren()) {
                    adsList.add(adSnapshot.getValue(ClassifiedAd.class));
                }
                Log.d(TAG, "no of ads for search is "+adsList.size());
                Toast.makeText(getActivity(),
                        "Number of ads for this search is " + adsList.size(),
                        Toast.LENGTH_SHORT).show();
                RecyclerViewAdapter recyclerViewAdapter = new
                        RecyclerViewAdapter(adsList, getActivity());
                adsRecyclerView.setAdapter(recyclerViewAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Error trying to get classified ads for " + category+
                        " "+databaseError);
                Toast.makeText(getActivity(),
                        "Error trying to get classified ads for " + category,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

//    @Override
//    public void onPause() {
//        EditText.findViewById(R.id.category_v);
//        Log.e("DEBUG", "OnPause of ViewFragment");
//        super.onPause();
//    }
}
