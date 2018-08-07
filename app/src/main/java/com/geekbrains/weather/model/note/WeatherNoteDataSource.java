package com.geekbrains.weather.model.note;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.geekbrains.weather.database.DatabaseHelper;

import java.io.Closeable;
import java.io.IOException;

public class WeatherNoteDataSource implements Closeable {

        private DatabaseHelper databaseHelper;
        private SQLiteDatabase database;
        private WeatherNoteDataReader weatherNoteDataReader;

        public WeatherNoteDataSource(Context context) {
            databaseHelper = new DatabaseHelper(context);
        }

        public void open() throws SQLException {
            database = databaseHelper.getWritableDatabase();
            weatherNoteDataReader = new WeatherNoteDataReader(database);
            weatherNoteDataReader.open();
        }
        // Закрыть базу данных
        @Override
        public void close() throws IOException {
            weatherNoteDataReader.close();
            databaseHelper.close();
        }

        // Добавить новую запись
        public WeatherNote addNote(long note_id, String city,long temperature, long wind ) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_NOTE_ID, note_id);
            values.put(DatabaseHelper.COLUMN_CITY, city);
            values.put(DatabaseHelper.COLUMN_TEMPERATURE, temperature);
            values.put(DatabaseHelper.COLUMN_WIND, wind);



            long insertId = database.insert(DatabaseHelper.TABLE_WEATHER_NOTES, null, values);

            WeatherNote newNote = new WeatherNote();
            newNote.setNote_id(note_id);
            newNote.setCity(city);
            newNote.setTemperature(temperature);
            newNote.setWind(wind);
            return newNote;
        }
        // Изменить запись
        public void editNote(WeatherNote note, long note_id, String city, long temperature, long wind) {
            ContentValues editValues = new ContentValues();
            editValues.put(databaseHelper.COLUMN_ID, note.getId());
            editValues.put(databaseHelper.COLUMN_NOTE_ID, note.getNote_id());
            editValues.put(databaseHelper.COLUMN_CITY, note.getCity());
            editValues.put(databaseHelper.COLUMN_TEMPERATURE,note.getTemperature());
            editValues.put(databaseHelper.COLUMN_WIND,note.getWind());


            database.update(databaseHelper.TABLE_WEATHER_NOTES,
                    editValues,
                    databaseHelper.COLUMN_ID + "=" + note.getId(),
                    null);
        }
        // Удалить запись
        public void deleteNote(WeatherNote note) {
            long id = note.getId();
            database.delete(DatabaseHelper.TABLE_WEATHER_NOTES, DatabaseHelper.COLUMN_NOTE_ID +
                    " = " + id, null);
        }
        // Очистить таблицу
        public void deleteAll() {
            database.delete(DatabaseHelper.TABLE_WEATHER_NOTES, null, null);
        }

        // Вернуть читателя (он потребуется в других местах)
        public WeatherNoteDataReader getWeatherNoteDataReader() {
            return weatherNoteDataReader;
        }
    }


