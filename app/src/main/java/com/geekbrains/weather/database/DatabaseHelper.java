package com.geekbrains.weather.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notes.db";
    public static final int DATABASE_VERSION = 2;

    public static final String TABLE_NOTES = "notes";
    public static final String TABLE_WEATHER_NOTES = "weather_notes";


    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_NOTE_TITLE = "title";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_NOTE_ID = "note_id";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_TEMPERATURE = "temperature";
    public static final String COLUMN_WIND = "wind";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NOTES + " (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NOTE + " TEXT," +
                COLUMN_NOTE_TITLE + " TEXT UNIQUE,"+ COLUMN_DATE + " TEXT," + COLUMN_TIME + " TEXT);");
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_WEATHER_NOTES + " (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NOTE_ID + " INTEGER REFERENCES "
                + TABLE_NOTES + " (" + COLUMN_ID + " ) ON UPDATE CASCADE ON DELETE CASCADE, " +
                COLUMN_CITY + " TEXT,"+ COLUMN_TEMPERATURE + " INTEGER," + COLUMN_WIND + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if ((oldVersion == 1) && (newVersion == 2)) {
            String upgradeQuery1 = "ALTER TABLE " + TABLE_NOTES + " ADD COLUMN" +
                    COLUMN_DATE + " TEXT DEFAULT CURRENT_DATE";

            String upgradeQuery2 = "ALTER TABLE " + TABLE_NOTES + " ADD COLUMN" +
                    COLUMN_TIME + " TEXT DEFAULT CURRENT_TIME";
            sqLiteDatabase.execSQL(upgradeQuery1);
            sqLiteDatabase.execSQL(upgradeQuery2);

        }
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_WEATHER_NOTES + " (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NOTE_ID + " INTEGER REFERENCES "
                + TABLE_NOTES + " (" + COLUMN_ID + " ) ON UPDATE CASCADE ON DELETE CASCADE, " +
                COLUMN_CITY + " TEXT,"+ COLUMN_TEMPERATURE + " INTEGER," + COLUMN_WIND + " TEXT);");
    }
}
