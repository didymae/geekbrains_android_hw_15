package com.geekbrains.weather.model.note;

public class WeatherNote {
    private long id;
    private long note_id;

    private String city;
    private long temperature;
    private long wind;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNote_id() {
        return note_id;
    }

    public void setNote_id(long note_id) {
        this.note_id = note_id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public long getTemperature() {
        return temperature;
    }

    public void setTemperature(long temperature) {
        this.temperature = temperature;
    }

    public long getWind() {
        return wind;
    }

    public void setWind(long wind) {
        this.wind = wind;
    }
}
