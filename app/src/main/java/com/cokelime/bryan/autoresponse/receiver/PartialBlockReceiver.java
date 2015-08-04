package com.cokelime.bryan.autoresponse.receiver;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.telephony.TelephonyManager;

import com.cokelime.bryan.autoresponse.database.AutoResponseDataSource;
import com.cokelime.bryan.autoresponse.database.DBHelper;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Bryan on 7/13/2015.
 */
public class PartialBlockReceiver extends PhoneStateReceiver {
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
//                Log.d(TAG, "PhoneStateReceiver**Incoming call " + incomingNumber);

                // read data from db that matches the incoming number
                AutoResponseDataSource dataSource = new AutoResponseDataSource(context);

                dataSource.open();
                Cursor cursor = dataSource.getBlockRule(incomingNumber);


                if(cursor != null && cursor.getCount() > 0){

                    Calendar calendar = Calendar.getInstance();
                    dataSource.insertHistory(incomingNumber);

                    while (cursor.moveToNext()) {


                        int weekdayIdx = cursor.getColumnIndex(DBHelper.BLOCK_WEEKDAY);

                        int fromIdx = cursor.getColumnIndex(DBHelper.BLOCK_FROM);
                        int toIdx = cursor.getColumnIndex(DBHelper.BLOCK_TO);

                        int replyTxtIdx = cursor.getColumnIndex(DBHelper.BLOCK_REPLY_TEXT);

                        boolean isWeekdayNull = cursor.isNull(weekdayIdx);
                        boolean isFromTimeNull = cursor.isNull(fromIdx);
                        boolean isToTimeNull = cursor.isNull(toIdx);
                        boolean isReplyTxtNull = cursor.isNull(replyTxtIdx);

                        // empty condtions then block phone because it is there
                        if(isWeekdayNull && isFromTimeNull && isToTimeNull){
                            killCall(context);

                            if(!isReplyTxtNull){
                                sendTxt(incomingNumber, cursor.getString(replyTxtIdx));
                            }

                            dataSource.close();
                            return;
                        }


                        if(!isWeekdayNull){
                            // check which day to block

                            String weekdayStr = cursor.getString(weekdayIdx);

//                            Log.d(TAG,"PhoneStateReceiver **"+ weekdayStr);

                            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                            if(weekdayStr.contains(String.valueOf(dayOfWeek))){

                                // check time before kill call if there is time

                                // if there is time interval
                                if(!isFromTimeNull && !isToTimeNull){

                                    if(checkTimeWithinInterval(cursor.getString(fromIdx),cursor.getString(toIdx))){
                                        killCall(context);

                                        if(!isReplyTxtNull){
                                            sendTxt(incomingNumber, cursor.getString(replyTxtIdx));
                                        }

                                        dataSource.close();
                                        return;

                                    } else {
                                        continue;
                                    }

                                }

                                killCall(context);

                                if(!isReplyTxtNull){
                                    sendTxt(incomingNumber, cursor.getString(replyTxtIdx));
                                }

                                dataSource.close();
                                return;
                            }

                        }

                        if(isWeekdayNull && !isToTimeNull && !isToTimeNull){

                            if(checkTimeWithinInterval(cursor.getString(fromIdx),cursor.getString(toIdx))){
                                killCall(context);

                                if(!isReplyTxtNull){
                                    sendTxt(incomingNumber, cursor.getString(replyTxtIdx));
                                }

                                dataSource.close();
                                return;

                            } else {
                                break;
                            }

                        }



                    }

                }

                dataSource.close();

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


    private boolean checkTimeWithinInterval(String fromTime, String toTime){

        String[] fromTimeArr = fromTime.split(":");

        int fromHr = Integer.parseInt(fromTimeArr[0]);
        int fromMin = Integer.parseInt(fromTimeArr[1]);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, fromHr);
        calendar1.set(Calendar.MINUTE, fromMin);

        String[] toTimeArr = toTime.split(":");

        int toHr = Integer.parseInt(toTimeArr[0]);
        int toMin = Integer.parseInt(toTimeArr[1]);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY, toHr);
        calendar2.set(Calendar.MINUTE, toMin);

        Calendar calendar3 = Calendar.getInstance();


        if(calendar1.get(Calendar.DAY_OF_WEEK) == calendar3.get(Calendar.DAY_OF_WEEK)
                && fromHr > calendar3.get(Calendar.HOUR_OF_DAY)){
            calendar1.add(Calendar.DATE, -1);
            calendar2.add(Calendar.DATE, -1);

        }

        if(fromHr > toHr){
            calendar2.add(Calendar.DATE, 1);
        }

        Date current = calendar3.getTime();
        if (current.after(calendar1.getTime()) && current.before(calendar2.getTime())) {
            return true;
        }


        return false;
    }
}
