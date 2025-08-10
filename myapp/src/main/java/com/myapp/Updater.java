package com.myapp;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Класс, отвечающий за обновление данных о погоде
 */
public class Updater {

    /**
     * api-ключ, необходимый для обращения к внешнему сайту
     */
    private String key;
    /**
     * Подключение к базе данных, необходимо для сохранения полученных данных
     */
    private Connection connection;
    /**
     * Конструктор, создаёт объект, выполняющий получение данных о погоде
     * @param connection Подключение к базе данных
     * @param key API-ключ для получения данных о погоде
     */
    public Updater(Connection connection, String key) {
        this.connection = connection;
        this.key = key;
    }

    /**
     * Обновляет данные о погоде для одного города, создавая отдельный поток для
     * обновления
     *
     * @param CityName Название города, для которого необходимо обновить данные
     * о погоде
     */
    public void updateCity(String CityName) {
        try {
            UpdateThread updateThread = new UpdateThread(key, CityName, connection);
            updateThread.start();
        } catch (Exception e) {
            System.out.println("Ошибка при запуске обновления " + e.getLocalizedMessage());
            Logger logger = Logger.getLogger("weather");
            logger.severe(e.getLocalizedMessage());
        }

    }

    /**
     * Обновляет данные о погоде для списка городов, для каждого города из
     * списка создаётся отдельный поток для обновления
     *
     * @param CityNames Список, содержащий названия городов, для которых
     * необходимо обновить данные о погоде
     */
    public void updateList(ArrayList<String> CityNames) {
        try {
            for (String city : CityNames) {
                UpdateThread updateThread = new UpdateThread(key, city, connection);
                updateThread.start();
            }
        } catch (Exception e) {
            System.out.println("Ошибка при запуске обновления " + e.getLocalizedMessage());
            Logger logger = Logger.getLogger("weather");
            logger.severe(e.getLocalizedMessage());
        }
    }
}
