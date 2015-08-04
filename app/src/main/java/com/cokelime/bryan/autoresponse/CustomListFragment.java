package com.cokelime.bryan.autoresponse;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.cokelime.bryan.autoresponse.database.AutoResponseDataSource;
import com.cokelime.bryan.autoresponse.database.DBHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class CustomListFragment extends Fragment {

    private ListView mRecyclerView;
    private ResourceCursorAdapter mAdapter;
    private AutoResponseDataSource dataSource;

    final public static int ADD_CUSTOM_REQUEST_CODE = 83;

    public CustomListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_custom_list, container, false);


        ListView list = (ListView) v.findViewById(R.id.custom_list_view);

        dataSource = ((MainActivity) getActivity()).getmDataSoruce();

        Cursor cursor = (dataSource.getBlockList());

        mAdapter = new MyAdapter(getActivity(), R.layout.row_layout, cursor, 0 );

        list.setAdapter(mAdapter);

        //TODO on item click edit rule

        TextView header = (TextView) v.findViewById(R.id.list_view_header);


        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(),AddCustomRuleActivity.class);


                startActivityForResult(i, ADD_CUSTOM_REQUEST_CODE);


            }
        });

        Button clear = (Button) v.findViewById(R.id.blockListClear);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("Clear ALL?")
                        .setMessage("Are you sure you want to clear all the rules?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dataSource.clearTable(DBHelper.TABLE_BLOCK_LIST);
                                mAdapter.swapCursor(dataSource.getBlockList());

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();



            }
        });


        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ADD_CUSTOM_REQUEST_CODE){

            if(resultCode == Activity.RESULT_OK){

                dataSource.open();

                mAdapter.swapCursor(dataSource.getBlockList());


            }

        }
    }


    private class MyAdapter extends ResourceCursorAdapter{

        public MyAdapter(Context context, int layout, Cursor c, int flags) {
            super(context, layout, c, flags);
        }


        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            TextView phoneNumber = (TextView) view.findViewById(R.id.blockNumber);
            phoneNumber.setText(getString(cursor, DBHelper.COLUMN_PHONE));

            TextView name = (TextView) view.findViewById(R.id.blockName);
            name.setText(getString(cursor, DBHelper.BLOCK_NAME));

            String weekdayStr = getString(cursor, DBHelper.BLOCK_WEEKDAY);

            TextView weekdays = (TextView) view.findViewById(R.id.blockWeekday);

            if(weekdayStr !=null && !weekdayStr.isEmpty()){
                //convert to actual text

                String displayStr = "";

                if(weekdayStr.contains("1")){
                    displayStr += "Sun ";
                }

                if(weekdayStr.contains("2")){
                    displayStr += "Mon ";
                }

                if(weekdayStr.contains("3")){
                    displayStr += "Tue ";
                }

                if(weekdayStr.contains("4")){
                    displayStr += "Wed ";
                }

                if(weekdayStr.contains("5")){
                    displayStr += "Thur ";
                }

                if(weekdayStr.contains("6")){
                    displayStr += "Fri ";
                }

                if(weekdayStr.contains("7")){
                    displayStr += "Sat ";
                }



                weekdays.setText(displayStr);
            } else {
                weekdays.setText("ALL WEEK");
            }

            String fromTimeStr = getString(cursor, DBHelper.BLOCK_FROM);
            TextView fromTime = (TextView) view.findViewById(R.id.blockFromTime);
            if(fromTimeStr != null && !fromTimeStr.isEmpty()){

                fromTime.setText(fromTimeStr);
            } else {
                fromTime.setText("None");
            }

            String toTimeStr = getString(cursor, DBHelper.BLOCK_TO);
            TextView toTime = (TextView) view.findViewById(R.id.blockToTime);

            if(toTimeStr !=null && !toTimeStr.isEmpty()){

                toTime.setText(toTimeStr);
            } else {
                toTime.setText("None");
            }

            String replyStr = getString(cursor, DBHelper.BLOCK_REPLY_TEXT);
            TextView replyMsg = (TextView) view.findViewById(R.id.replyMsg);

            if(replyStr !=null && !replyStr.isEmpty()){
                replyMsg.setText(replyStr);
            } else {
                replyMsg.setText("None");
            }

            final int id = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ID));


            Button deleteBtn = (Button) view.findViewById(R.id.rowDeleteBtn);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    dataSource.deleteRow(DBHelper.TABLE_BLOCK_LIST, id);

                    mAdapter.swapCursor(dataSource.getBlockList());

                }
            });



        }

        private String getString(Cursor cursor, String column) {
            return cursor.getString(cursor.getColumnIndex(column));
        }
    }

}


