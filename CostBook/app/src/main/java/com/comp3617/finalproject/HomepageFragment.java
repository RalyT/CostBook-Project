package com.comp3617.finalproject;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.comp3617.finalproject.HelperClasses.CostTransaction;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomepageFragment extends Fragment {

    private static final String TAG = "HomePageFragment";
    private DatabaseReference databaseReference;
    private TextView tvDailySpending;
    private TextView tvMonthlySpending;
    private TextView tvPreviousDailySpending;
    private TextView tvPreviousMonthlySpending;
    private Date todayDate;
    private int currentDay;
    private int currentMonth;
    private int currentYear;
    private double totalDailySpending;
    private double percentDailyDifference;
    private double totalMonthlySpending;
    private double percentMonthlyDifference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_homepage_fragment,
                container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        tvDailySpending = view.findViewById(R.id.tv_spent_today);
        tvMonthlySpending = view.findViewById(R.id.tv_spent_month);
        tvPreviousDailySpending = view.findViewById(R.id.tv_daily_percent_diff);
        tvPreviousMonthlySpending = view.findViewById(R.id.tv_monthly_percent_diff);

        todayDate = new Date();
        parseDate();
        getDailySpending();
        return view;
    }

    /**
     * Parses date object to return values.
     */
    private void parseDate() {
        String unparsedDay = (String) DateFormat.format("dd", todayDate); // 20
        String unparsedMonth = (String) DateFormat.format("MM",   todayDate); // 06
        String unparsedYear  = (String) DateFormat.format("yyyy", todayDate); // 2013

        // Parse back into Ints for comparison
        try {
            currentDay = Integer.parseInt(unparsedDay);
            currentMonth = Integer.parseInt(unparsedMonth);
            currentYear = Integer.parseInt(unparsedYear);
        } catch(NumberFormatException nfe) { }
    }

    private void getDailySpending() {

        // Query the datebase for transactions made today
        databaseReference.child("transactions").orderByChild("day")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        double previousDaySpending = 0;
                        double previousMonthSpending = 0;

                        List<CostTransaction> transList = new ArrayList<CostTransaction>();
                        for (DataSnapshot transSnapshot: dataSnapshot.getChildren()) {
                            transList.add(transSnapshot.getValue(CostTransaction.class));
                        }

                        // Sorting the relevant transactions by day / month
                        for(int i = 0; i < transList.size(); i++) {
                            // Daily Spending
                            if (transList.get(i).getMonth() == (currentMonth - 1)
                                    && transList.get(i).getDay() == currentDay) {
                                totalDailySpending += Double.parseDouble(transList.get(i).getCost());
                            }

                            // Monthly Spending
                            if (transList.get(i).getMonth() == (currentMonth - 1)) {
                                totalMonthlySpending += Double.parseDouble(transList.get(i).getCost());
                            }

                            // Previous daily spending
                            if (transList.get(i).getMonth() == (currentMonth - 1)
                                    && transList.get(i).getDay() == currentDay - 1) {
                                previousDaySpending += Double.parseDouble(transList.get(i).getCost());
                            }

                            // Monthly daily spending
                            if (transList.get(i).getMonth() == (currentMonth - 2)) {
                                previousMonthSpending += Double.parseDouble(transList.get(i).getCost());
                            }
                        }
                        // Calculate in case there were no transactions from the day prior
                        if (previousDaySpending == 0) {
                            percentDailyDifference = (totalDailySpending - 0);
                        }

                        // Calculate in case there were no transactions from the month prior
                        if (previousMonthSpending == 0) {
                            percentMonthlyDifference = (totalMonthlySpending - 0);
                        }

                        // Calculate in case there were no transactions from today
                        if (totalDailySpending == 0) {
                            percentDailyDifference = (-1) * previousDaySpending;
                        } else {
                            percentDailyDifference = (totalDailySpending - previousDaySpending) / previousDaySpending * 100;
                        }

                        // Calculate in case there were no transactions from today
                        if (totalMonthlySpending == 0) {
                            percentMonthlyDifference = (-1) * previousMonthSpending;
                        } else {
                            percentMonthlyDifference = (totalMonthlySpending - previousMonthSpending) / previousMonthSpending * 100;
                        }

                        // Cutoff Double to Hundredths
                        DecimalFormat df = new DecimalFormat("#.##");
                        tvDailySpending.setText("Today you have spent: $" + df.format(totalDailySpending));
                        tvMonthlySpending.setText("This month, you have spent: $" + df.format(totalMonthlySpending));
//                Log.d("TAG", "percentDailyDifference value before printing: " + percentDailyDifference );
//                Log.d("TAG", "previousDaySpending value before printing: " + previousDaySpending );
                        if (percentDailyDifference < 0) {
                            percentDailyDifference += -2 * percentDailyDifference;
                            tvPreviousDailySpending.setText("You have spent  -%" + df.format(percentDailyDifference)
                                    + "  less than yesterday: ($" + previousDaySpending + ")");
                            tvPreviousDailySpending.setBackgroundResource(R.color.light_teal);
                        } else {
                            tvPreviousDailySpending.setText("You have spent  %" + df.format(percentDailyDifference)
                                    + "  more than yesterday: ($" + previousDaySpending + ")");
                            tvPreviousDailySpending.setBackgroundResource(R.color.light_red);
                        }
                        if (percentMonthlyDifference < 0) {
                            percentMonthlyDifference += -2 * percentMonthlyDifference;
                            tvPreviousMonthlySpending.setText("You have spent  -%" + df.format(percentMonthlyDifference)
                                    + "  less than last Month: ($" + df.format(previousMonthSpending) + ")");
                            tvPreviousMonthlySpending.setBackgroundResource(R.color.light_teal);
                        } else {
                            tvPreviousMonthlySpending.setText("You have spent %" + df.format(percentMonthlyDifference)
                                    + " more than last month: ($" + df.format(previousMonthSpending) + ")");
                            tvPreviousMonthlySpending.setBackgroundResource(R.color.light_red);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "Error trying to get initial transactions for Date: "
                                + databaseError);
                        Toast.makeText(getActivity(),
                                "Error trying to get transactions",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
