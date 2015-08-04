package com.cokelime.bryan.autoresponse;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.cokelime.bryan.autoresponse.receiver.AllCallBlockReceiver;
import com.cokelime.bryan.autoresponse.receiver.PartialBlockReceiver;

public class PhoneCallBlockService extends Service implements MyIntentActions{

    BroadcastReceiver mALLBlockReceiver;

    BroadcastReceiver mPartialBlockReceiver;

    int mNotificationId = 333;

    PendingIntent resultPendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();

        Intent resultIntent = new Intent(this, MainActivity.class);

        resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );



        mALLBlockReceiver = new AllCallBlockReceiver();

        mPartialBlockReceiver = new PartialBlockReceiver();



    }

    @Override
    public void onDestroy() {

        try {

            unregisterReceiver(mALLBlockReceiver);

        } catch(IllegalArgumentException e){
//            Log.d("PHONE_SERVICE", "all block receiver already unregisterd before onDestory");
        }

        try {

            unregisterReceiver(mPartialBlockReceiver);

        } catch(IllegalArgumentException e){
//            Log.d("PHONE_SERVICE", "partial block receiver already unregisterd before onDestory");
        }

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(mNotificationId);

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");

        if(action.equals(MyIntentActions.ACTION_BLOCK_ALL_CALL)){


            try {

                unregisterReceiver(mPartialBlockReceiver);

            } catch(IllegalArgumentException e){
//                Log.d("PHONE_SERVICE","partial block receiver already unregisterd");
            }


            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setOngoing(true)
                            .setSmallIcon(R.drawable.ic_stat_action_announcement)
                            .setContentTitle("Auto Response")
                            .setContentText("All Call Blocking in Progress!");

            builder.setContentIntent(resultPendingIntent);

            // Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, builder.build());

            registerReceiver(mALLBlockReceiver,intentFilter);

        } else if (action.equals(MyIntentActions.ACTION_BLOCK_PARTIAL)){

            try {

                unregisterReceiver(mALLBlockReceiver);

            } catch(IllegalArgumentException e){
//                Log.d("PHONE_SERVICE","all block receiver already unregisterd");
            }

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setOngoing(true)
                            .setSmallIcon(R.drawable.ic_stat_action_announcement)
                            .setContentTitle("Auto Response")
                            .setContentText("Partial Call Blocking in Progress!");

            builder.setContentIntent(resultPendingIntent);

            // Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, builder.build());

            registerReceiver(mPartialBlockReceiver,intentFilter);

        }



        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
