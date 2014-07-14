package com.suitscreen.app.mobiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "SocketService";
    private static String START_ACTION = "NotifyServiceStart";
    private static String STOP_ACTION  = "NotifyServiceStop";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "MyReceiver onReceive action = " + action);
        
        if ( START_ACTION.equalsIgnoreCase(action) ) {

            context.startService(new Intent(context, SocketService.class));

        } else if ( STOP_ACTION.equalsIgnoreCase(action) ) {

            context.stopService(new Intent(context, SocketService.class));
        }
    }
}
