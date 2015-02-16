package com.ridesafertechnologies.ssa;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextSwitcher;


public class About_Main_Activity extends ActionBarActivity {

    private TextSwitcher ts_child_in_seat;
    private TextSwitcher ts_bluetooth_connection;
    private TextSwitcher ts_temperature_threshold;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();

            // Get Reference
//            ts_child_in_seat = (TextSwitcher) findViewById(R.id.is_child_in_seat);
//            ts_bluetooth_connection = (TextSwitcher) findViewById(R.id.is_bluetooth_connectivity);
//            ts_temperature_threshold = (TextSwitcher) findViewById(R.id.is_temp_threshold);
//
//            ts_child_in_seat.setFactory(child_Factory);

            /*
            * Set the in and out animations. Using the fade_in/out animations
            * provided by the framework.
            */
//            Animation in = AnimationUtils.loadAnimation(this,
//                    android.R.anim.fade_in);
//            Animation out = AnimationUtils.loadAnimation(this,
//                    android.R.anim.fade_out);
//
//
//            ts_child_in_seat.setInAnimation(in);
//            ts_child_in_seat.setOutAnimation(out);
        }
    }

//    private ViewSwitcher.ViewFactory child_Factory = new ViewSwitcher.ViewFactory() {
//        @Override
//        public View makeView() {
//            // Create a new TextView
//            TextView tv_child_in_seat = new TextView(About_Main_Activity.this);
//            return null;
//        }
//    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_about_main, container, false);



            return rootView;
        }


    }
}
