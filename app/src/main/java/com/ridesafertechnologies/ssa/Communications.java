package com.ridesafertechnologies.ssa;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class Communications extends IntentService {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO: Do something Useful
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: For Communication return IBinder implementation
        return null;
    }

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_FOO = "com.ridesafertechnologies.ssa.action.FOO";
    public static final String ACTION_BAZ = "com.ridesafertechnologies.ssa.action.BAZ";

    // TODO: Rename parameters
    public static final String EXTRA_PARAM1 = "com.ridesafertechnologies.ssa.extra.PARAM1";
    public static final String EXTRA_PARAM2 = "com.ridesafertechnologies.ssa.extra.PARAM2";

    public Communications() {
        super("Communications");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Toast.makeText(getApplicationContext(), "Comms Service STARTED", Toast.LENGTH_LONG).show();
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
