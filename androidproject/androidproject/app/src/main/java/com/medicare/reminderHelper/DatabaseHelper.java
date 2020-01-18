package com.medicare.reminderHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pgupta on 9/6/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    Context context;
    public DatabaseHelper(Context context) {
        super(context, "mydb.db", null, 1);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Databases.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Databases.updateTable(db);
    }
}
