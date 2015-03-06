package com.ridesafertechnologies.ssa;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class Full_Screen_Alarm extends ActionBarActivity {
    private boolean close = false;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full__screen__alarm);
        mediaPlayer = MediaPlayer.create(this,R.raw.tornadosireniidelilah747233690 );
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

    }
    //use another intent to close the full screen alarm activity or resume it
    //from snooze.
    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        close = intent.getExtras().getBoolean("close");

        if(close == true){
            mediaPlayer.release();
            mediaPlayer = null;
            this.finish();
        }
        else if(close == false){
            mediaPlayer.start();
            this.onResume();
        }
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
        //it only hides the activity and waits to be killed by the
        //notification trigger.
        mediaPlayer.release();
        mediaPlayer = null;
        this.moveTaskToBack(true);

        //this.finish();
    }
    public void snooze(View view) {
        //send intent to snooze service
        mediaPlayer.stop();
        this.moveTaskToBack(true);
        //need the snooze service to bring it back
    }
    //Below is the intent that worked for the demo commented out.
    /*
    Intent fullAlarm;
    fullAlarm = new Intent(getApplicationContext(), Full_Screen_Alarm.class);
    startActivity(fullAlarm);
    */

}
