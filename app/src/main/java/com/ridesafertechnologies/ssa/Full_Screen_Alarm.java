package com.ridesafertechnologies.ssa;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class Full_Screen_Alarm extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full__screen__alarm);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_full__screen__alarm, menu);
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
    public void dismiss(View view){
        this.finish();
    }
    public void snooze(View view) {
        this.moveTaskToBack(true);
        //not sure how to bring it back
    }
    //Below is the intent that worked for the demo commented out.
    /*
    Intent fullAlarm;
    fullAlarm = new Intent(getApplicationContext(), Full_Screen_Alarm.class);
    startActivity(fullAlarm);
    */

}
