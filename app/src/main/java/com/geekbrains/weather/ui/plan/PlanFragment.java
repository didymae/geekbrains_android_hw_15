package com.geekbrains.weather.ui.plan;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.geekbrains.weather.Constants;
import com.geekbrains.weather.R;
import com.geekbrains.weather.data.PrefsData;
import com.geekbrains.weather.data.PrefsHelper;
import com.geekbrains.weather.data.data.DataManager;
import com.geekbrains.weather.data.data.IDataManager;
import com.geekbrains.weather.model.note.Note;
import com.geekbrains.weather.model.note.NoteDataReader;
import com.geekbrains.weather.model.note.NoteDataSource;
import com.geekbrains.weather.model.note.WeatherNote;
import com.geekbrains.weather.model.note.WeatherNoteDataReader;
import com.geekbrains.weather.model.note.WeatherNoteDataSource;
import com.geekbrains.weather.model.weather.Weather;
import com.geekbrains.weather.model.weather.WeatherRequest;
import com.geekbrains.weather.ui.base.BaseFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.geekbrains.weather.Constants.KEYAPI;
import static com.geekbrains.weather.Constants.TEMP_K;


public class PlanFragment extends BaseFragment {

    private NoteDataSource noteDataSource;
    private NoteDataReader noteDataReader;
    private WeatherNoteDataSource weatherNoteDataSource;
    private WeatherNoteDataReader weatherNoteDataReader;
    private NoteAdapter adapter;
    private EditText editTextNoteDate;
    private EditText editTextNote;
    private EditText editTextNoteTitle;
    private EditText editTextNoteTime;
    private TextView tv_weather;
    private PrefsHelper prefsHelper;
    private long temp;
    private long wind;
    private long note_id;
    private String city;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.plan_layout, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.plan_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.menu_add:
                addElement();
                return true;
            case R.id.menu_clear:
                clearList();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearList() {
        noteDataSource.deleteAll();
        dataUpdate();
    }

    @Override
    protected void initLayout(View view, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        initDataSource();
        prefsHelper = new PrefsData(getBaseActivity());

        RecyclerView recyclerView = view.findViewById(R.id.rv_plan);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NoteAdapter(noteDataReader);
        adapter.setOnMenuItemClickListener(new NoteAdapter.OnMenuItemClickListener() {
            @Override
            public void onItemEditClick(Note note) {
                editElement(note);
            }

            @Override
            public void onItemDeleteClick(Note note) {
                deleteElement(note);
            }

            @Override
            public void onItemWeatherClick(Note note) {
                showWeather(note);
            }
        });


        recyclerView.setAdapter(adapter);
    }

    private void showWeather(Note note) {
        String weatherString = weatherNoteDataReader.getWeatherNoteByNoteId(note.getId());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View av_weather = inflater.inflate(R.layout.show_weather, null);
        tv_weather = av_weather.findViewById(R.id.tv_weather_note);
        tv_weather.setText(weatherString);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(av_weather);
        builder.setTitle(R.string.weather_forecast);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }


    private void addElement() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View alertView = inflater.inflate(R.layout.add_recycler, null);
        editTextNote = alertView.findViewById(R.id.et_note_text);
        editTextNoteTitle = alertView.findViewById(R.id.et_note_title);
        editTextNoteDate = alertView.findViewById(R.id.et_note_date);
        editTextNoteTime = alertView.findViewById(R.id.et_note_time);

        editTextNoteTime.setInputType(InputType.TYPE_NULL);
        editTextNoteTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime(editTextNoteTime);
            }
        });

        editTextNoteTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setTime(editTextNoteTime);
                }
            }
        });

        editTextNoteDate.setInputType(InputType.TYPE_NULL);
        editTextNoteDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(editTextNoteDate);
            }

        });

        editTextNoteDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setDate(editTextNoteDate);
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(alertView);
        builder.setTitle(R.string.title_add);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.menu_add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    noteDataSource.addNote(
                            editTextNoteTitle.getText().toString(),
                            editTextNote.getText().toString(),
                            editTextNoteDate.getText().toString(),
                            editTextNoteTime.getText().toString());
                    dataUpdate();

            }
        });
        builder.show();
    }

    private void deleteElement(Note note) {
        noteDataSource.deleteNote(note);
        dataUpdate();
    }

    private void editElement(Note note) {
        noteDataSource.editNote(note, "Edited", "Edited title");
        dataUpdate();
    }

    private void dataUpdate() {
        noteDataReader.Refresh();
        adapter.notifyDataSetChanged();
        weatherDataUpdate(editTextNoteTitle.getText().toString());

    }

    private void weatherDataUpdate(String title) {
         note_id = noteDataReader.getNoteId(title);

        IDataManager dataManager = new DataManager(new DataManager.OnResponseCompleted() {
            @Override
            public void onCompleted(WeatherRequest response) {
                {
                   temp = Math.round(response.getMain().getTemp()-TEMP_K);
                   wind = Math.round(response.getWind().getSpeed());
                    weatherNoteDataSource.addNote(
                            note_id,
                            city,
                            temp,
                            wind);
                    weatherNoteDataReader.Refresh();
                }
            }
        });
        city = prefsHelper.getSharedPreferences(Constants.CITY);
        dataManager.initRetrofit();
        dataManager.requestRetrofit(city+",ru", KEYAPI);
    }

    private void initDataSource() {
        noteDataSource = new NoteDataSource(getActivity().getApplicationContext());
        noteDataSource.open();
        noteDataReader = noteDataSource.getNoteDataReader();
        weatherNoteDataSource =  new WeatherNoteDataSource(getActivity().getApplicationContext());
        weatherNoteDataSource.open();
        weatherNoteDataReader = weatherNoteDataSource.getWeatherNoteDataReader();
    }


    void setDate(final EditText ed) {
        Calendar mcurrentTime = Calendar.getInstance();
        int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);
        int month = mcurrentTime.get(Calendar.MONTH);
        int year = mcurrentTime.get(Calendar.YEAR);
        DatePickerDialog mTimePicker;
        mTimePicker = new DatePickerDialog(getContext(), R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int monthOfYear, int dayOfMonth) {
                monthOfYear += 1;
                ed.setText(dayOfMonth + "." + (monthOfYear<10?("0"+monthOfYear):(monthOfYear)) + "." + selectedYear);
            }
        }, year, month, day);
        mTimePicker.show();
    }

    void setTime (final EditText ed) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getContext(),R.style.DatePickerDialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                ed.setText( selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.show();

    }

}
