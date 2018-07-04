package finalproject.comp3617.com.costbook;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import finalproject.comp3617.com.costbook.HelperClasses.CostTransaction;

public class RecyclerViewAdapter extends
        RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<CostTransaction> transList;
    private Context context;

    // RecyclerViewAdaptor Constructor
    public RecyclerViewAdapter(List<CostTransaction> list, Context ctx) {
        transList = list;
        context = ctx;
    }

    @Override
    public int getItemCount() {
        return transList.size();
    }

    @Override
    public RecyclerViewAdapter.ViewHolder
    onCreateViewHolder(ViewGroup parent, int viewType) {

        // Creates a new transaction Item to display
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_recycler_view_adapter, parent, false);

        RecyclerViewAdapter.ViewHolder viewHolder =
                new RecyclerViewAdapter.ViewHolder(view);
        return viewHolder;
    }

    /**
     * Viewholder for costTransaction item details
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView category;
        public TextView cost;
        public TextView date;
        public Button edit;
        public Button delete;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.item_title);
            category = (TextView) view.findViewById(R.id.item_category);
            cost = (TextView) view.findViewById(R.id.item_cost);
            date = (TextView) view.findViewById(R.id.item_date);
            edit = view.findViewById(R.id.btn_edit_item);
            delete = view.findViewById(R.id.btn_delete_item);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {

        // Transaction Item's position
        final int transPos = position;
        final CostTransaction costTransaction = transList.get(position);

        Log.d("Transaction Title: ", costTransaction.getTitle());

        holder.title.setText(costTransaction.getTitle());
        holder.category.setText(costTransaction.getCategory());
        holder.cost.setText("$" + costTransaction.getCost());
        holder.date.setText(retrieveFormattedDate(costTransaction));

        // Not sure why this isn't working, perhaps the background is underneath something?
        switch (costTransaction.getCategory()) {
            case "Food":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.category_food));
            case "Clothes":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.category_clothes));
            case "Bills":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.category_bills));
            case "Gas":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.category_gas));
            case "Education":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.category_education));
            case "Entertainment":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.category_entertainment));
            case "Other":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.category_other));
        }

        // Edit Transaction instance in recyclerView
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"holder.edit.setOnClickListener() is run",Toast.LENGTH_LONG).show();
                editCostTransaction(costTransaction.getTransactionId());
            }
        });

        // Delete Transaction instance in recyclerView
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCostTransaction(costTransaction.getTransactionId(), transPos);
            }
        });
    }

    /**
     * Edits the details for a Cost transaction. ###### NEEDS TO BE COMPLETED AFTER AddTransactions fragment
     * @param TransactionId Transaction Id
     */
    private void editCostTransaction(String TransactionId){
        FragmentManager fm = ((MainActivity)context).getSupportFragmentManager();

        Bundle bundle = new Bundle();
        bundle.putString("TransactionId", TransactionId);

//        Toast.makeText(context,"TransactionId: " + TransactionId,Toast.LENGTH_LONG).show();

        AddTransactionFragment addFragment = new AddTransactionFragment();
        addFragment.setArguments(bundle);

        fm.beginTransaction().replace(R.id.adds_frame, addFragment).commit();
        }

    /**
     * Deletes CostTransaction Item from RecyclerView.
     * @param TransactionId Id of Transaction to be deleted
     * @param position Position of the item
     */
    private void deleteCostTransaction(String TransactionId, final int position){
        FirebaseDatabase.getInstance().getReference()
            .child("transactions").child(TransactionId).removeValue()
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Removes the transaction item from list and updates
                    transList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, transList.size());

                    Log.d("Delete Transaction", "Transaction has been deleted");
                    Toast.makeText(context,
                            "Transaction has been deleted",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("Delete Transaction", "Transaction could not be deleted");
                    Toast.makeText(context,
                            "Transaction could not be deleted",
                            Toast.LENGTH_SHORT).show();
                }
                }
            });
    }

    /**
     * Accepts a transaction, returns the date formatted as a string.
     * @param transaction CostTransaction obj
     * @return
     */
    private String retrieveFormattedDate(CostTransaction transaction) {
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(Calendar.YEAR, transaction.getYear());
        selectedDate.set(Calendar.MONTH, transaction.getMonth());
        selectedDate.set(Calendar.DAY_OF_MONTH, transaction.getDay());

        String currentDate = DateFormat.getDateInstance(DateFormat.FULL)
                .format(selectedDate.getTime());
        return currentDate;
    }
}
