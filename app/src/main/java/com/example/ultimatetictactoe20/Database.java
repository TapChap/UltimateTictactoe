package com.example.ultimatetictactoe20;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tictactoe.db";
    private static final int DATABASE_VERSION = 2; // Increment version to update schema

    public static final String TABLE_NAME = "savetable";
    public static final String UID = "_id"; // primary Key, automatic ID
    public static final String CONTACT_NAME_COL = "contact_name"; // New column for opponent name
    public static final String DATA_COL = "gamestate";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CONTACT_NAME_COL + " TEXT, " + DATA_COL + " TEXT );";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public Database(Context context) {
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

    public void saveState(String data, String contactName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CONTACT_NAME_COL, contactName);
        cv.put(DATA_COL, data);

        // Update the existing row if a game state for the given opponent already exists
        if (db.update(TABLE_NAME, cv, CONTACT_NAME_COL + "=?", new String[]{contactName}) == 0)
            // Insert a new row if a game state doesn't exist for the given opponent
            db.insert(TABLE_NAME, null, cv);

        db.close();
    }


    public String getState(String contactName) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, new String[]{DATA_COL}, CONTACT_NAME_COL + "=?", new String[]{contactName}, null, null, null);
        String savedString = null;
        if (cursor.moveToFirst()) {
            savedString = cursor.getString(cursor.getColumnIndex(DATA_COL));
        }
        cursor.close();
        database.close();
        return savedString;
    }

    public boolean hasSavedGame(String contactName) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query the database to check if a game state exists for the given opponent name
        Cursor cursor = db.query(TABLE_NAME, new String[]{UID}, CONTACT_NAME_COL + "=?", new String[]{contactName}, null, null, null);

        // Check if the cursor has any rows, indicating that a game state exists for the opponent
        boolean hasSavedState = cursor.getCount() > 0;

        // Close cursor and database connection
        cursor.close();
        db.close();

        return hasSavedState;
    }


    public void remove(String contactName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, CONTACT_NAME_COL + "=?", new String[]{contactName});
        db.close();
    }
}

