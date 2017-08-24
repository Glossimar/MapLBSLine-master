package com.bignerdranch.android.maplbsline.Tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DataBaseHelper extends SQLiteOpenHelper{

    private Context context;
    private final String Day1 = "create table Day1 ("
            +"latitude double, "
            +"longitude double)";

    private final String Day2 = "create table Day2 ("
            +"latitude double, "
            +"longitude double)";

    private final String date = "create table date ("
            +"everyday long)";

    public DataBaseHelper (Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
         db.execSQL(Day1);
        db.execSQL(Day2);
        db.execSQL(date);
        Toast.makeText(context, "Database create successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Day1");
        db.execSQL("drop table if exists Day2");
        db.execSQL("drop table if exists date");
        onCreate(db);
    }
}