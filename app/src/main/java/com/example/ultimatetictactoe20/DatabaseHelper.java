package com.example.ultimatetictactoe20;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "memory_db.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "savetable";
    public static final String UID = "_id"; // primary Key, automatic ID
    public static final String DATA_COL = "gamestate";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DATA_COL + " TEXT );";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void addData(String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DATA_COL, data);
        db.insert(TABLE_NAME, null, cv);
        db.close();
    }

    public String readData() {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, new String[]{DATA_COL}, null, null, null, null, null);
        String savedString = null;
        if (cursor.moveToLast()) {
            savedString = cursor.getString(0); // Retrieves data from the first column (index 0) of the last row
        }
        cursor.close();
        database.close();
        return savedString;
    }


    public void remove(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, DATA_COL + "= ? ", new String[]{name});
        db.close();
    }
}
