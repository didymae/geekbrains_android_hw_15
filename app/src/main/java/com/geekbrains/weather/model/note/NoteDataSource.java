package com.geekbrains.weather.model.note;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.geekbrains.weather.database.DatabaseHelper;

import java.io.Closeable;
import java.io.IOException;


//работает непосредственно с
//нашей БД (сохраняет и загружает данные в нее)
public class NoteDataSource implements Closeable {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    private NoteDataReader noteDataReader;

    public NoteDataSource(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = databaseHelper.getWritableDatabase();
        noteDataReader = new NoteDataReader(database);
        noteDataReader.open();
    }
    // Закрыть базу данных
    @Override
    public void close() throws IOException {
        noteDataReader.close();
        databaseHelper.close();
    }

    // Добавить новую запись
    public Note addNote(String title, String desc, String date, String time) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NOTE, desc);
        values.put(DatabaseHelper.COLUMN_NOTE_TITLE, title);
        values.put(DatabaseHelper.COLUMN_DATE, date);
        values.put(DatabaseHelper.COLUMN_TIME, time);



        long insertId = database.insert(DatabaseHelper.TABLE_NOTES, null, values);

        Note newNote = new Note();
        newNote.setDescription(desc);
        newNote.setTitle(title);
        newNote.setId(insertId);
        newNote.setDate(date);
        newNote.setTime(time);
        return newNote;
    }
    // Изменить запись
    public void editNote(Note note, String desc, String title) {
        ContentValues editValues = new ContentValues();
        editValues.put(databaseHelper.COLUMN_ID, note.getId());
        editValues.put(databaseHelper.COLUMN_NOTE, note.getDescription());
        editValues.put(databaseHelper.COLUMN_NOTE_TITLE, note.getTitle());
        editValues.put(databaseHelper.COLUMN_DATE,note.getDate());
        editValues.put(databaseHelper.COLUMN_TIME,note.getTime());


        database.update(databaseHelper.TABLE_NOTES,
                editValues,
                databaseHelper.COLUMN_ID + "=" + note.getId(),
                null);
    }
    // Удалить запись
    public void deleteNote(Note note) {
        long id = note.getId();
        database.delete(DatabaseHelper.TABLE_NOTES, DatabaseHelper.COLUMN_ID +
                " = " + id, null);
    }
    // Очистить таблицу
    public void deleteAll() {
        database.delete(DatabaseHelper.TABLE_NOTES, null, null);
    }

    // Вернуть читателя (он потребуется в других местах)
    public NoteDataReader getNoteDataReader() {
        return noteDataReader;
    }
}
