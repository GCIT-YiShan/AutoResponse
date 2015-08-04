package com.cokelime.bryan.autoresponse.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Bryan on 7/9/2015.
 */
public class AutoResponseDataSource {



    private SQLiteDatabase database;
    private DBHelper dbHelper;


    public AutoResponseDataSource(Context context) {

        dbHelper = new DBHelper(context);

    }

    public void open() throws SQLiteException{
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }


    public long insertHistory(String phoneNumber){

        ContentValues values = new ContentValues();

        values.put(DBHelper.COLUMN_PHONE,phoneNumber);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        values.put(DBHelper.HISTORY_TIME, dateFormat.format(cal.getTime()));


        long insertId = database.insert(DBHelper.TABLE_HISTORY, null, values);

        return insertId;

    }


    public Cursor getHistory(){

        Cursor cursor = database.query(DBHelper.TABLE_HISTORY, null, null, null, null, null, DBHelper.COLUMN_ID + " DESC", "25");

        return cursor;
    }


    public long insertBlockRule(ContentValues input){


        return database.insert(DBHelper.TABLE_BLOCK_LIST, null, input);
    }


    public Cursor getBlockList(){

        Cursor cursor = database.query(DBHelper.TABLE_BLOCK_LIST, null, null, null, null, null, null, null);

        return cursor;
    }


    public Cursor getBlockRule(String phoneNumber){


        return database.rawQuery("SELECT * FROM "+DBHelper.TABLE_BLOCK_LIST
                + " WHERE "+DBHelper.COLUMN_PHONE
                +" LIKE '%"+phoneNumber.substring(1,phoneNumber.length()) +"'" ,null);
    }

    public void clearTable(String tableName){

        database.delete(tableName, null, null);
        database.execSQL("VACUUM");

    }

    public void deleteRow(String tableName, int id) {

        database.delete(tableName, DBHelper.COLUMN_ID+"=?",new String[]{Integer.toString(id)});

    }
}
