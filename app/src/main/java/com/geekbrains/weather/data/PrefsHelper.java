package com.geekbrains.weather.data;


public interface PrefsHelper {

    String getSharedPreferences(String keyPref);

    void saveSharedPreferences(String keyPref, String value);

    void deleteSharedPreferences(String keyPref);
}
