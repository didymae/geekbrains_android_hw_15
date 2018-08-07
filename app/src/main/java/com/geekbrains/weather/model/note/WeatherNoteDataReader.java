package com.geekbrains.weather.model.note;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.geekbrains.weather.database.DatabaseHelper;

import java.io.Closeable;
import java.io.IOException;

public class WeatherNoteDataReader implements Closeable {

        private Cursor cursor;

        private SQLiteDatabase database;

        private String[] notesAllColumn = {
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_NOTE_ID,
                DatabaseHelper.COLUMN_CITY,
                DatabaseHelper.COLUMN_TEMPERATURE,
                DatabaseHelper.COLUMN_WIND
        };

        public WeatherNoteDataReader (SQLiteDatabase database) {
            this.database = database;
        }

        public void open() {
            query();
            cursor.moveToFirst();
        }

        private void query() {
            cursor = database.query(DatabaseHelper.TABLE_WEATHER_NOTES, notesAllColumn, null, null, null, null, null);
        }

        public  String getWeatherNoteByNoteId(long note_id){
            cursor = database.query(DatabaseHelper.TABLE_WEATHER_NOTES, notesAllColumn, DatabaseHelper.COLUMN_NOTE_ID + "='" + note_id + "'", null, null, null, null);
            if (cursor.getCount() == 1){
                cursor.moveToFirst();
                return cursor.getString(2)+ " : temp" + cursor.getInt(3) + ", wind " + cursor.getInt(4) + "m/c";

            }
            return "No data available";
        }

        public void close() {
            cursor.close();
        }

        // Прочитать данные по определенной позиции
        public WeatherNote getPosition(int pos) {
            cursor.moveToPosition(pos);
            return cursorToNote();
        }

        public int getCount() {
            return cursor.getCount();
        }

        // Преобразователь данных курсора в объект
        private WeatherNote cursorToNote() {
            WeatherNote note = new WeatherNote();
            note.setId(cursor.getLong(0));
            note.setNote_id(cursor.getLong(1));
            note.setCity(cursor.getString(2));
            note.setTemperature(cursor.getLong(3));
            note.setWind(cursor.getLong(4));
            return note;
        }

        public void Refresh() {
            int pos = cursor.getPosition();
            query();
            cursor.moveToPosition(pos);
        }
    }
