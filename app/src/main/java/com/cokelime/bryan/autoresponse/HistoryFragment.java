package com.cokelime.bryan.autoresponse;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
public class HistoryFragment extends Fragment {


    private AutoResponseDataSource dataSource;

    public HistoryFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_history, container, false);

        ListView historyList = (ListView) v.findViewById(R.id.historyList);

        dataSource = ((MainActivity) getActivity()).getmDataSoruce();

        Cursor cursor = dataSource.getHistory();

        final MyCursorAdapter adapter = new MyCursorAdapter(
                getActivity(), R.layout.history_row_layout, cursor, 0 );

        historyList.setAdapter(adapter);


        Button clear = (Button) v.findViewById(R.id.historyClearBtn);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("Clear ALL?")
                        .setMessage("Are you sure you want to clear all the hisotry?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dataSource.clearTable(DBHelper.TABLE_HISTORY);
                                adapter.swapCursor(dataSource.getHistory());

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


    private class MyCursorAdapter extends ResourceCursorAdapter{

        public MyCursorAdapter(Context context, int layout, Cursor c, int flags) {
            super(context, layout, c, flags);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            TextView phoneNumber = (TextView) view.findViewById(R.id.historyPhoneNumber);
            phoneNumber.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PHONE)));

            TextView timeCalled = (TextView) view.findViewById(R.id.hisotryTimeCalled);
            timeCalled.setText(cursor.getString(cursor.getColumnIndex(DBHelper.HISTORY_TIME)));

        }
    }


}


