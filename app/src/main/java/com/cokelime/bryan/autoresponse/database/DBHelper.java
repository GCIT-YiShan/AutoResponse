package com.cokelime.bryan.autoresponse.database;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Bryan on 7/9/2015.
 */

public class DBHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "autoResponse.db";

    public static final String TABLE_HISTORY = "tbl_history";
    public static final String TABLE_BLOCK_LIST = "tbl_block_list";

    //common column names
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PHONE = "phone";


    //tbl_history columns
    public static final String HISTORY_TIME = "time_called";

    //tbl_block_list columns
    public static final String BLOCK_NAME = "name";
    public static final String BLOCK_WEEKDAY = "weekdays";
    public static final String BLOCK_FROM = "from_time";
    public static final String BLOCK_TO = "to_time";
    public static final String BLOCK_REPLY_TEXT = "reply_text";


    //tbl_history create statement
    private static final String CREATE_TBL_HISTORY = "CREATE TABLE " + TABLE_HISTORY
            +"("
            +  COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PHONE +" TEXT,"
            + HISTORY_TIME +" TEXT NOT NULL"
            +")";

    private static final String CREATE_TBL_BLOCK_LIST = "CREATE TABLE " + TABLE_BLOCK_LIST
            +"("
            +  COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PHONE +" TEXT,"
            + BLOCK_NAME + " TEXT,"
            + BLOCK_WEEKDAY +" TEXT,"
            + BLOCK_FROM +" TEXT,"
            + BLOCK_TO +" TEXT,"
            + BLOCK_REPLY_TEXT + " TEXT"
            +")";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TBL_HISTORY);
        db.execSQL(CREATE_TBL_BLOCK_LIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLOCK_LIST);

        // create new tables
        onCreate(db);

    }

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
//            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

//            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }
}
