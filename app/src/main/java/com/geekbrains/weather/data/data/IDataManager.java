package com.geekbrains.weather.data.data;



public interface IDataManager {
    void initRetrofit();

    void requestRetrofit(String city, String keyApi);

}
