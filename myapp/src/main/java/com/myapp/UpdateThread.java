package com.myapp;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Класс, отвечающий за обновление данных о погоде. Один объект класса
 * предаставляет один поток для обновления данных по одному городу.
 */
public class UpdateThread extends Thread {

    /**
     * Содержит api-ключ для получения данных с внешнего сайта
     */
    private String key;
    /**
     * Содержит название города, для которого нужно обновить данные
     */
    private String city;
    /**
     * Соединение с базой данных, куда должны быть добавлены полученные данные
     */
    private Connection connection;

    /**
     * Конструктор, создаёт поток для получения данных о погоде
     *
     * @param key API-ключ для получения данных о погоде
     * @param city Название города, для которого необходимо получить данные о
     * погоде
     * @param connection Соединение с базой данных для сохранения полученной
     * информации
     */
    public UpdateThread(String key, String city, Connection connection) {
        this.key = key;
        this.city = city;
        this.connection = connection;
    }

    /**
     * Метод, осуществляющий получение информации о погоде с помощью
     * последовательного вызова методов для получения, обработки и добавления
     * информации в базу данных
     */
    @Override
    public void run() {
        JSONObject object = getData();
        if (object != null) {
            WeatherReport report = prepareData(object);
            insertData(report);
        }
    }

    /**
     * Метод, получающий данные о погоде с внешнего сайта
     *
     * @return Полученные данные о погоде в формате JSON
     */
    private JSONObject getData() {
        JSONObject obj = null;
        try {
            city = city.replace(' ', '_');
            String connString = "http://api.weatherapi.com/v1/current.json?key=" + key + "&q=" + city + "&aqi=no";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(URI.create(connString)).build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JSONParser parser = new JSONParser();
                obj = (JSONObject) parser.parse(response.body());
            } else {
                if (response.statusCode() == 400) {
                    JSONParser parser = new JSONParser();
                    obj = (JSONObject) parser.parse(response.body());
                    JSONObject errorObject = (JSONObject) obj.get("error");
                    int errorCode = (int) errorObject.get("code");
                    String errorMessage = (String) errorObject.get("message");
                    System.out.println("Ошибка при получении данных: " + errorCode + " " + errorMessage);
                    return null;
                } else {
                    System.out.println("Ошибка при получении данных: " + response.statusCode());
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при получении данных " + e.getLocalizedMessage());
            Logger logger = Logger.getLogger("weather");
            logger.severe(e.getLocalizedMessage());
        }
        return obj;
    }

    /**
     * Метод, преобразующий полученные данные о погоде из формата JSON в объект
     * для последующего добавления в БД
     *
     * @param obj Данные в формате JSON, полученные с внешенего сайта
     * @return Объект класса WeatherReport, содержащий нужные данные для БД
     */
    private WeatherReport prepareData(JSONObject obj) {
        WeatherReport report = null;
        try {
            JSONObject locationObject = (JSONObject) obj.get("location");
            JSONObject currentObject = (JSONObject) obj.get("current");
            String cityName = (String) locationObject.get("name");
            Double temperature = (Double) currentObject.get("temp_c");
            String timeString = (String) currentObject.get("last_updated");
            timeString = timeString.replace(" ", "T");
            LocalDateTime time = LocalDateTime.parse(timeString);
            JSONObject conditionObject = (JSONObject) currentObject.get("condition");
            String weather = (String) conditionObject.get("text");
            report = new WeatherReport(cityName, temperature, time, weather);
        } catch (Exception e) {
            System.out.println("Ошибка при обработке полученных данных " + e.getLocalizedMessage());
            Logger logger = Logger.getLogger("weather");
            logger.severe(e.getLocalizedMessage());
        }
        return report;
    }

    /**
     * Метод, добавляющий полученные данные о погоде в БД
     *
     * @param report Объект класса WeatherReport, содержит данные о погоде,
     * которые должны быть добавлены в БД
     */
    private void insertData(WeatherReport report) {
        try {
            Statement statement = connection.createStatement();
            String InsertSQL = "INSERT INTO weather_reports (CityName, Temperature, Time, Weather) values ('" + report.cityName + "', "
                    + report.temperature.toString() + ", '" + report.time + "', '" + report.weather + "')";
            statement.execute(InsertSQL);
            System.out.println("Данные обновлены для города " + report.cityName);
        } catch (Exception e) {
            System.out.println("Ошибка при добавлении в базу данных " + e.getLocalizedMessage());
            Logger logger = Logger.getLogger("weather");
            logger.severe(e.getLocalizedMessage());
        }
    }
}
