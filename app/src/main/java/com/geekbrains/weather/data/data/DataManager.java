package com.geekbrains.weather.data.data;

import android.os.Handler;
import android.util.Log;

import com.geekbrains.weather.model.weather.WeatherRequest;
import com.geekbrains.weather.retrofit.OpenWeather;
import com.geekbrains.weather.ui.geoweb.OkHttpRequester;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class DataManager implements IDataManager {

    private static final String BASE_URL = "https://api.openweathermap.org/";
    private static final String TAG = "DataManager";
    private OpenWeather openWeather;

    // переменная интерфейса обратног вызова
    private OnResponseCompleted responseCompleted;

    public DataManager(OnResponseCompleted responseCompleted) {
        this.responseCompleted = responseCompleted;
    }

    @Override
    public void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        openWeather = retrofit.create(OpenWeather.class);
    }

    @Override
    public void requestRetrofit(String city, String keyApi) {
        openWeather.loadWeather(city, keyApi).enqueue(new Callback<WeatherRequest>() {
            final Handler handler = new Handler();
            @Override
            public void onResponse(Call<WeatherRequest> call, final Response<WeatherRequest> response) {
                if (response != null) {
                    Log.d(TAG, response.body().toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //вызов метода интерфейса обратного вызова
                            responseCompleted.onCompleted(response.body());
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<WeatherRequest> call, Throwable t) {
                Log.d(TAG, t.getMessage());

            }
        });
    }
//интерфейс обратного вызова
    public interface OnResponseCompleted {
        void onCompleted(WeatherRequest response);
    }
}
