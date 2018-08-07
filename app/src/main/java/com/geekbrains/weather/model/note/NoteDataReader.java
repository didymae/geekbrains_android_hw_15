package com.geekbrains.weather.model.note;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.geekbrains.weather.database.DatabaseHelper;

import java.io.Closeable;


//чтение данных из таблицы
public class NoteDataReader implements Closeable {

    private Cursor cursor;

    private SQLiteDatabase database;

    private String[] notesAllColumn = {
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_NOTE,
            DatabaseHelper.COLUMN_NOTE_TITLE,
            DatabaseHelper.COLUMN_DATE,
            DatabaseHelper.COLUMN_TIME
    };

    public NoteDataReader(SQLiteDatabase database) {
        this.database = database;
    }

    public void open() {
        query();
        cursor.moveToFirst();
    }

    private void query() {
        cursor = database.query(DatabaseHelper.TABLE_NOTES, notesAllColumn, null, null, null, null, null);
    }

    public void close() {
        cursor.close();
    }

    // Прочитать данные по определенной позиции
    public Note getPosition(int pos) {
        cursor.moveToPosition(pos);
        return cursorToNote();
    }

    public long getNoteId (String title){
        Cursor cursor1;
        String[] columns ={DatabaseHelper.COLUMN_ID};
        cursor1 = database.query(DatabaseHelper.TABLE_NOTES, columns, DatabaseHelper.COLUMN_NOTE_TITLE + "='" + title + "'", null, null, null, null);
        cursor1.moveToFirst();
        return cursor1.getLong(0);

    }

    public int getCount() {
        return cursor.getCount();
    }

    // Преобразователь данных курсора в объект
    private Note cursorToNote() {
        Note note = new Note();
        note.setId(cursor.getLong(0));
        note.setDescription(cursor.getString(1));
        note.setTitle(cursor.getString(2));
        note.setDate(cursor.getString(3));
        note.setTime(cursor.getString(4));
        return note;
    }

    public void Refresh() {
        int pos = cursor.getPosition();
        query();
        cursor.moveToPosition(pos);
    }
}
