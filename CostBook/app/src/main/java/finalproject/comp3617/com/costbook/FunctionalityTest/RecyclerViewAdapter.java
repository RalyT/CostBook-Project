package finalproject.comp3617.com.costbook.FunctionalityTest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.List;

import finalproject.comp3617.com.costbook.R;

public class RecyclerViewAdapter extends
        RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private List<ClassifiedAd> adsList;
        private Context context;

        public RecyclerViewAdapter(List<ClassifiedAd> list, Context ctx) {
            adsList = list;
            context = ctx;
        }
        @Override
        public int getItemCount() {
            return adsList.size();
        }

        @Override
        public RecyclerViewAdapter.ViewHolder
        onCreateViewHolder(ViewGroup parent, int viewType) {

            // Creates a new Ad Item
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_recycler_view_adapter, parent, false);

            RecyclerViewAdapter.ViewHolder viewHolder =
                    new RecyclerViewAdapter.ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
            final int itemPos = position;
            final ClassifiedAd classifiedAd = adsList.get(position);

            Log.d("CLASSIFIEDAD: ", classifiedAd.getTitle());

            holder.title.setText(classifiedAd.getTitle());
            holder.name.setText(classifiedAd.getName());
            holder.phone.setText(classifiedAd.getPhone());

            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editClassifiedAd(classifiedAd.getAdId());
                }
            });

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteClassifiedAd(classifiedAd.getAdId(), itemPos);
                }
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView title;
            public TextView name;
            public TextView phone;
            public Button edit;
            public Button delete;

            public ViewHolder(View view) {
                super(view);
                name = (TextView) view.findViewById(R.id.title_i);
                title = (TextView) view.findViewById(R.id.name_i);
                phone = (TextView) view.findViewById(R.id.phone_i);
                edit = view.findViewById(R.id.edit_ad_b);
                delete = view.findViewById(R.id.delete_ad_b);
            }
        }

        private void editClassifiedAd(String adId){
            FragmentManager fm = ((TestActivity)context).getSupportFragmentManager();

            Bundle bundle = new Bundle();
            bundle.putString("adId", adId);

            AddFragment addFragment = new AddFragment();
            addFragment.setArguments(bundle);

            fm.beginTransaction().replace(R.id.adds_frame, addFragment).commit();
        }

        // Delete item from RecyclerView
        private void deleteClassifiedAd(String adId, final int position){
            FirebaseDatabase.getInstance().getReference()
                    .child("classified").child(adId).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //remove item from list alos and refresh recyclerview
                                adsList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, adsList.size());

                                Log.d("Delete Ad", "Classified has been deleted");
                                Toast.makeText(context,
                                        "Classified has been deleted",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("Delete Ad", "Classified couldn't be deleted");
                                Toast.makeText(context,
                                        "Classified could not be deleted",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
}
