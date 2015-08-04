package com.cokelime.bryan.autoresponse.receiver;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.cokelime.bryan.autoresponse.database.AutoResponseDataSource;

/**
 * Created by Bryan on 7/13/2015.
 */
public class AllCallBlockReceiver extends PhoneStateReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
//            Log.d(TAG, "PhoneStateReceiver**Call State=" + state);

            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
//                Log.d(TAG,"PhoneStateReceiver**Idle");
            } else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                // Incoming call
                String incomingNumber =
                        intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
//                Log.d(TAG,"PhoneStateReceiver**Incoming call " + incomingNumber);

                // add data into DB
                AutoResponseDataSource dataSource = new AutoResponseDataSource(context);

                dataSource.open();
                dataSource.insertHistory(incomingNumber);
                dataSource.close();


                if (!killCall(context)) { // Using the method defined earlier
//                    Log.d(TAG,"PhoneStateReceiver **Unable to kill incoming call");
                }

            } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
//                Log.d(TAG,"PhoneStateReceiver **Offhook");
            }
        } else if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            // Outgoing call
            String outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
//            Log.d(TAG,"PhoneStateReceiver **Outgoing call " + outgoingNumber);

//            setResultData(null); // Kills the outgoing call

        } else {
//            Log.d(TAG,"PhoneStateReceiver **unexpected intent.action=" + intent.getAction());
        }
    }
}
