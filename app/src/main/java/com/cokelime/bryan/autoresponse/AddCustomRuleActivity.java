package com.cokelime.bryan.autoresponse;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.cokelime.bryan.autoresponse.database.AutoResponseDataSource;
import com.cokelime.bryan.autoresponse.database.DBHelper;

import java.util.Calendar;

/**
 * Created by Bryan on 7/9/2015.
 */
public class AddCustomRuleActivity extends Activity{


    static final int PICK_CONTACT_REQUEST = 55;

    EditText mPhoneNumber;
    EditText mName;
    EditText mTextReply;

    Switch weekdaySwitch;
    Switch timeSwitch;
    Switch textSwitch;

    AutoResponseDataSource mDataSoruce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_custom_layout);

        mPhoneNumber = (EditText) findViewById(R.id.phoneNumber);
        mName = (EditText) findViewById(R.id.name);

        final LinearLayout weekdays = (LinearLayout) findViewById(R.id.weekdays);

        //disable weekdays
        layoutSwitch(weekdays, false);

        final LinearLayout timeInterval = (LinearLayout) findViewById(R.id.timeInterval);

        layoutSwitch(timeInterval, false);


        weekdaySwitch = (Switch) findViewById(R.id.weekdaySwitch);

        weekdaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    layoutSwitch(weekdays, true);
                }else{
                    layoutSwitch(weekdays, false);
                }


            }
        });


        timeSwitch = (Switch) findViewById(R.id.timeSwitch);

        timeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    layoutSwitch(timeInterval, true);
                } else {
                    layoutSwitch(timeInterval, false);
                }

            }
        });


        textSwitch = (Switch) findViewById(R.id.textSwitch);

        mTextReply = (EditText) findViewById(R.id.replyText);
        mTextReply.setEnabled(false);

        textSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTextReply.setEnabled(true);
                } else {
                    mTextReply.setEnabled(false);
                }

            }
        });

        final EditText fromTime = (EditText) findViewById(R.id.fromTime);
        final EditText toTime = (EditText) findViewById(R.id.toTime);


        fromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTimePickerDialog(fromTime, "Select Start Time");
            }
        });

        toTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTimePickerDialog(toTime, "Select End Time");
            }
        });




        Button chooseBtn = (Button) findViewById(R.id.chooseFromContactsBtn);


        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
                startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);

            }
        });


        mDataSoruce = new AutoResponseDataSource(this);

        Button addBtn = (Button) findViewById(R.id.addBtn);


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDataSoruce.open();

                ContentValues values = new ContentValues();

                String name = mName.getText().toString();
                String phone = mPhoneNumber.getText().toString();

                //make phone number only number. remove any - +

                phone = phone.replace("-","");
                phone = phone.replace("+","");
                phone = phone.replace(" ","");
                phone = phone.replace(")","");
                phone = phone.replace("(","");

                values.put(DBHelper.COLUMN_PHONE, phone);
                values.put(DBHelper.BLOCK_NAME, name);

                if(weekdaySwitch.isChecked()){

                    ToggleButton[] toggleButtons = new ToggleButton[7];

                    toggleButtons[0] = (ToggleButton) findViewById(R.id.toggleSunday);
                    toggleButtons[1] = (ToggleButton) findViewById(R.id.toggleMonday);
                    toggleButtons[2] = (ToggleButton) findViewById(R.id.toggleTuesday);
                    toggleButtons[3] = (ToggleButton) findViewById(R.id.toggleWednesday);
                    toggleButtons[4] = (ToggleButton) findViewById(R.id.toggleThursday);
                    toggleButtons[5] = (ToggleButton) findViewById(R.id.toggleFriday);
                    toggleButtons[6] = (ToggleButton) findViewById(R.id.toggleSaturday);

                    String results = new String();

                    for(int i = 0; i < toggleButtons.length; i++){

                        if(toggleButtons[i].isChecked()){
                            results += (i+1);
                        }

                    }

                    values.put(DBHelper.BLOCK_WEEKDAY,results);


                } else {
                    values.put(DBHelper.BLOCK_WEEKDAY,(String)null);
                }


                if(timeSwitch.isChecked()){

                    if(fromTime.getText().length() != 0 && toTime.getText().length() != 0){

                        values.put(DBHelper.BLOCK_FROM, fromTime.getText().toString());
                        values.put(DBHelper.BLOCK_TO, toTime.getText().toString());

                    }

                } else {
                    values.put(DBHelper.BLOCK_FROM,(String)null);
                    values.put(DBHelper.BLOCK_TO, (String)null);
                }


                if(textSwitch.isChecked()){

                    values.put(DBHelper.BLOCK_REPLY_TEXT, mTextReply.getText().toString());

                } else {
                    values.put(DBHelper.BLOCK_REPLY_TEXT, (String) null);
                }


                mDataSoruce.insertBlockRule(values);

                mDataSoruce.close();

                setResult(Activity.RESULT_OK);

                finish();
            }
        });




    }

    private void setTimePickerDialog(final EditText timeEditText, String title) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddCustomRuleActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                timeEditText.setText( selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle(title);
        mTimePicker.show();
    }


    @Override
    protected void onResume() {
        mDataSoruce.open();
        super.onResume();
    }


    @Override
    protected void onPause() {
        mDataSoruce.close();
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request it is that we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();
                // We only need the NUMBER column, because there will be only one row in the result
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER , ContactsContract.Contacts.DISPLAY_NAME};

                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.
                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);

                // Retrieve the Display Name
                cursor.moveToFirst();
                int column = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                String name = cursor.getString(column);

                mName.setText(name);

                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(column);

                // Do something with the phone number...


                mPhoneNumber.setText(number);

            }
        }
    }

    private void layoutSwitch(LinearLayout linearLayout, boolean enable){


        for ( int i = 0; i < linearLayout.getChildCount();  i++ ){
            View view = ((LinearLayout) linearLayout.getChildAt(i)).getChildAt(0);
            view.setEnabled(enable);
        }

    }


}
