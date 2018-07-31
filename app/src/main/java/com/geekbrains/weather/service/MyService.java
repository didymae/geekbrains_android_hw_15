package com.geekbrains.weather.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.HandlerThread;
import android.os.IBinder;
import android.widget.Toast;

import com.geekbrains.weather.ui.base.BaseActivity;

import static android.app.Service.START_NOT_STICKY;


public class MyService extends Service implements SensorEventListener  {
    private SensorManager sensorManager;
    private Sensor sensorTemp;
    private Sensor sensorHumidity;
    IBinder mBinder; // Интерфейс связи с клиентом
    @Override
    public void onCreate() {
        // Служба создается

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Служба стартовала
        initSensors();
        sensorManager.registerListener(this,sensorTemp,sensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this,sensorHumidity,sensorManager.SENSOR_DELAY_FASTEST);
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public IBinder onBind(Intent intent) {
        // Привязка клиента
        return mBinder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // Удаление привязки
        return true;
    }
    @Override
    public void onRebind(Intent intent) {
        // Перепривязка клиента
    }
    @Override
    public void onDestroy() {
        // Уничтожение службы
    }



    private void initSensors() {
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensorTemp = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        sensorHumidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
    }



        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE |sensorEvent.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY  )
                writeData(sensorEvent.values, sensorEvent.sensor.getType());
        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }


    private void writeData(float[] values, int type) {

        try {
            Intent intent = new Intent(BaseActivity.BROADCAST_ACTION);
            intent.putExtra(BaseActivity.SENSOR_VAL, values[0]);
            intent.putExtra(BaseActivity.SENSOR_TYPE, type);

            sendBroadcast(intent);
        } catch (Throwable t1) {
            Toast.makeText(getApplicationContext(),
                    "Exception: " + t1.toString(), Toast.LENGTH_SHORT)
                    .show();
        }
    }

}