package com.ridesafertechnologies.ssa;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;


public class About_Main_Activity extends ActionBarActivity {

    // Text Switchers to be dynamically switched
    private TextSwitcher mBluetoothSwitcher;
    private TextSwitcher mChildSwitcher;
    private TextSwitcher mTemperatureSwitcher;
    private TextSwitcher mBatterySwitcher;

    // Arrays of strings for the text switchers
    String textBluetoothSwitcher[] = {"Connected", "Disconnected"};
    String textChildSwitcher[] = {"In Seat", "Not In Seat"};
    String textTemperatureSwitcher[] = {"Within Threshold", "Threshold Surpassed"};
    String textBatterySwitcher[] = {"Charging", "Discharging"};

    // Lengths of the switchers
    int countBluetoothSwithcer = textBluetoothSwitcher.length;
    int countChildSwitcher = textChildSwitcher.length;
    int countTemperatureSwitcher = textTemperatureSwitcher.length;
    int countBatterySwitcher = textBatterySwitcher.length;

    // Indexes for the switchers
    int cIndexBluetoothSwitcher = -1;
    int cIndexChildSwitcher = -1;
    int cIndexTemperatureSwitcher = -1;
    int cIndexBatterySwithcer = -1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_main);

        // create references
        mBluetoothSwitcher = (TextSwitcher) findViewById(R.id.is_bluetooth_connectivity);
        mChildSwitcher = (TextSwitcher) findViewById(R.id.is_child_in_seat);
        mTemperatureSwitcher = (TextSwitcher) findViewById(R.id.is_temp_threshold);
        mBatterySwitcher = (TextSwitcher) findViewById(R.id.is_battery_status);

        // Set View Factories
        mBluetoothSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView bluetoothText = new TextView(About_Main_Activity.this);
                bluetoothText.setGravity(Gravity.TOP | Gravity.RIGHT);
                bluetoothText.setTextSize(15);
                bluetoothText.setTextColor(Color.BLACK);
                return bluetoothText;
            }
        });
//
//        mChildSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
//            @Override
//            public View makeView() {
//                TextView childText = new TextView(About_Main_Activity.this);
//                childText.setGravity(Gravity.TOP | Gravity.RIGHT);
//                childText.setTextSize(15);
//                if(Data_Parser.getIsChildInSeat())
//                    childText.setTextColor(Color.GREEN);
//                else
//                    childText.setTextColor(Color.RED);
//                return childText;
//            }
//        });
//
//        mTemperatureSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
//            @Override
//            public View makeView() {
//                TextView temperatureText = new TextView(About_Main_Activity.this);
//                temperatureText.setGravity(Gravity.TOP | Gravity.RIGHT);
//                temperatureText.setTextSize(15);
//                if(Data_Parser.getIsTempThresholdReached())
//                    temperatureText.setTextColor(Color.RED);
//                else
//                    temperatureText.setTextColor(Color.GREEN);
//                return temperatureText;
//            }
//        });
//
//        mBatterySwitcher.setFactory(new ViewSwitcher.ViewFactory() {
//            @Override
//            public View makeView() {
//                TextView batteryText = new TextView(About_Main_Activity.this);
//                batteryText.setGravity(Gravity.TOP | Gravity.RIGHT);
//                batteryText.setTextSize(15);
//                if(Data_Parser.getIsChildInSeat())
//                    batteryText.setTextColor(Color.GREEN);
//                else
//                    batteryText.setTextColor(Color.RED);
//                return batteryText;
//            }
//        });

        // Animation Declarations
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        // set the animation type of the TextSwitchers
//        mBluetoothSwitcher.setInAnimation(in);
//        mBluetoothSwitcher.setOutAnimation(out);
//        mChildSwitcher.setInAnimation(in);
//        mChildSwitcher.setOutAnimation(out);
//        mTemperatureSwitcher.setInAnimation(in);
//        mTemperatureSwitcher.setOutAnimation(out);
//        mBatterySwitcher.setInAnimation(in);
//        mBatterySwitcher.setOutAnimation(out);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }


    }

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
            Intent settingsActivity = new Intent(getApplicationContext(), Settings_Activity.class);
            startActivity(settingsActivity);
            return true;
        } else if (id == R.id.action_user_manual) {
            Intent userManualActivity = new Intent(getApplicationContext(), User_Manual_Activity.class);
            startActivity(userManualActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.activity_about_main);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new PlaceholderFragment())
                .commit();
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
