package com.geekbrains.weather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.session.MediaSessionManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class WeatherFragment extends BaseFragment implements /*Observer,*/ CreateActionFragment.OnHeadlineSelectedListener {


    private static final String ARG_COUNTRY = "ARG_COUNTRY";
    private String country;
    private TextView textCity;
    private TextView textTemp;
    private TextView textHumidity;
    private boolean isCheckedNotification = false;
    private boolean isCheckedUpdates = false;
    private boolean isCheckedUseGPS = false;
    private SensorManager sensorManager;
    private Sensor sensorTemp;
    private Sensor sensorHumidity;


    public WeatherFragment() {
    }

    public static WeatherFragment newInstance(String country) {
        Bundle args = new Bundle();
        args.putString(ARG_COUNTRY, country);


        WeatherFragment fragment = new WeatherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            country = getArguments().getString(ARG_COUNTRY);
        }
    }

    private void initSensors() {
        sensorManager = (SensorManager)getBaseActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorTemp = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        sensorHumidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Toast.makeText(getContext(), "onAttachWeather", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Toast.makeText(getContext(), "onDetachWeather", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.weather_layout, container, false);
    }

    @Override
    protected void initLayout(View view, Bundle savedInstanceState) {
        //включаем меню
        setHasOptionsMenu(true);
        //инициализируем сенсоры и location
        initSensors();

        //инициализируем различные textview
        textTemp = view.findViewById(R.id.tv_temperature);
        textHumidity = view.findViewById(R.id.tv_humidity);
       // textTemp.setText(sensorTemp.getPower();
        textCity = view.findViewById(R.id.tv_country);

        String sensor_error = getResources().getString(R.string.error_no_sensor);

        if (sensorTemp == null) {
            textTemp.setText(sensor_error);
        }
        if (sensorHumidity == null) {
            textHumidity.setText(sensor_error);
        }

        if (textCity != null) {
            Log.d(ARG_COUNTRY, "NOTnull!!");
        }
        if (country != null && country.length() > 0) {
            Log.d(ARG_COUNTRY, "IN");
            textCity.setVisibility(View.VISIBLE);
            textCity.setText(country);
        } else {
            Log.d(ARG_COUNTRY, "ELSE");
            textCity.setVisibility(View.GONE);
        }
    }

    @Override
    public BaseActivity getBaseActivity() {
        return super.getBaseActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        sensorManager.registerListener(listenerTemp,sensorTemp,sensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(listenerHumidity,sensorHumidity,sensorManager.SENSOR_DELAY_FASTEST);
        Toast.makeText(getContext(), "onStartWeather", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(getContext(), "onResumeWeather", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        Toast.makeText(getContext(), "onPauseWeather", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(listenerTemp, sensorTemp);
        sensorManager.unregisterListener(listenerHumidity, sensorHumidity);
        Toast.makeText(getContext(), "onStopWeather", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Toast.makeText(getContext(), "onDestroyWeather", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onArticleSelected(ArrayList<String> citiesList) {
        textCity.setVisibility(View.VISIBLE);
        String cities = citiesList.toString();
        textCity.setText(cities.substring(cities.indexOf("[") + 1, cities.indexOf("]")));
    }
    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.base, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.use_gps:
                if (isCheckedUseGPS) {
                    isCheckedUseGPS = false;
                    item.setChecked(isCheckedUseGPS);
                } else {
                    isCheckedUseGPS = true;
                    getBaseActivity().requestLocationPermission();
                    item.setChecked(isCheckedUseGPS);
                    Log.d(ARG_COUNTRY, "setschecked");
                }
                return true;
            case R.id.auto_updates:
                if (isCheckedUpdates) {
                    isCheckedUpdates = false;
                    item.setChecked(isCheckedUpdates);
                } else {
                    isCheckedUpdates = true;
                    item.setChecked(isCheckedUpdates);
                }
                return true;

            case R.id.action_notifications:
                if (isCheckedNotification) {
                    isCheckedNotification = false;
                    item.setChecked(isCheckedNotification);
                } else {
                    isCheckedNotification = true;
                    item.setChecked(isCheckedNotification);
                }
                return true;
            case R.id.simple_view:
                item.setChecked(true);
                return true;
            case R.id.popular_view:
                item.setChecked(true);
                return true;
            case R.id.custom_view:
                item.setChecked(true);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }



    SensorEventListener listenerHumidity = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            showHumiditySensor(sensorEvent);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };


    SensorEventListener listenerTemp = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            showTempSensor(sensorEvent);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private void showTempSensor(SensorEvent event){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(event.values[0]);
        textTemp.setText(stringBuilder);
    }
    private void showHumiditySensor(SensorEvent event) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(event.values[0]);
        textHumidity.setText(stringBuilder);
    }



}
