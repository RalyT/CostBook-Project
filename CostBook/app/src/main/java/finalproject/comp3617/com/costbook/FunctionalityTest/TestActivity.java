package finalproject.comp3617.com.costbook.FunctionalityTest;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import finalproject.comp3617.com.costbook.R;

import static finalproject.comp3617.com.costbook.R.*;

public class TestActivity extends AppCompatActivity {

        private FragmentManager fm;

//        Toolbar tb = findViewById(id.toolbar);
//        setSupportActionBar(tb);
//        tb.setSubtitle("Realtime Database");

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(layout.activity_test);

            fm = getSupportFragmentManager();
            addClassifiedAdFrgmt();
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.ads_menu, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case id.add_ad_m:
                    addClassifiedAdFrgmt();
                    return true;
                case id.view_ads_m:
                    viewlassifiedAdFrgmt();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        public void addClassifiedAdFrgmt(){
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(id.adds_frame, new AddFragment());
            ft.commit();
        }
        public void viewlassifiedAdFrgmt(){
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(id.adds_frame, new ViewFragment());
            ft.commit();
        }
}
