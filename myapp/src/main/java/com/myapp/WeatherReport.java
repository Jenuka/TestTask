package com.myapp;

import java.time.LocalDateTime;

/**
 * Класс, объекты которого служат для хранения информации о погоде
 */
public class WeatherReport {

    /**
     * Название города
     */
    public String cityName;
    /**
     * Температура воздуха
     */
    public Double temperature;
    /**
     * Время обновления данных о погоде
     */
    public LocalDateTime time;
    /**
     * Тип погоды
     */
    public String weather;
    /**
     * Конструктор, создаёт объект для хранения данных о погоде
     * @param cityName Название города
     * @param temperature Температура воздуха
     * @param time Время получения данных о погоде
     * @param weather Тип погоды
     */
    public WeatherReport(String cityName, Double temperature, LocalDateTime time, String weather) {
        this.cityName = cityName;
        this.temperature = temperature;
        this.time = time;
        this.weather = weather;
    }
}
