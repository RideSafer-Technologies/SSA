package com.ridesafertechnologies.ssa.util;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.ridesafertechnologies.ssa.R;

public class SSA_Dialog_Alert extends ActionBarActivity {

    private boolean close = false;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ssa__dialog__alert);
        mediaPlayer = MediaPlayer.create(this, R.raw.tornadosireniidelilah747233690);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_ssa__dialog__alert, menu);
        return false;
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
    //Close SSA Dialog Alert if with an intent, or resume it after
    //snooze
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
    public void dismiss(){
        mediaPlayer.release();
        mediaPlayer = null;
        this.moveTaskToBack(true);
    }
    public void snooze(){
        mediaPlayer.pause();
        this.moveTaskToBack(true);
    }
}
