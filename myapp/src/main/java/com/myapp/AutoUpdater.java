package com.myapp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Класс, отвечающий за автообновление и его настройки
 */
public class AutoUpdater {

    /**
     * Показывает, включено ли автообновление (true - включено, false -
     * выключено)
     */
    private boolean autoUpdate;
    /**
     * Задержка времени между автообновлениями (в милисекундах)
     */
    private int updateTimer;
    /**
     * Объект, отвечающий за непосредственное обновление данных
     */
    private Updater updater;
    /**
     * Соединение с базой данных
     */
    private Connection connection;

    /**
     * Конструктор, создаёт объект для управления автообновлением с начальными
     * настройками: <br> Автообновление раз в 60 секунд, <br> Автообновление отключено
     *
     * @param updater Объект, отвечающий за получение данных о погоде
     * @param connection Соединение с базой данных
     */
    public AutoUpdater(Updater updater, Connection connection) {
        this.updater = updater;
        this.connection = connection;
        this.updateTimer = 60 * 1000;
        this.autoUpdate = false;
    }

    /**
     * Метод, возвращающий значение поля autoUpdate
     *
     * @return Значение поля autoUpdate
     */
    public boolean getAutoUpdate() {
        return this.autoUpdate;
    }

    /**
     * Метод, устанавливающий значение поля autoUpdate
     *
     * @param newautoUpdate Новое значение поля autoUpdate
     */
    public void setAutoUpdate(boolean newautoUpdate) {
        this.autoUpdate = newautoUpdate;
    }

    /**
     * Метод, возвращающий длительность таймера автообновления
     *
     * @return Длительность таймера автообновления в милисекундах
     */
    public int getUpdateTimer() {
        return this.updateTimer;
    }

    /**
     * Метод, устанавливающий новое значение длительности таймера автообновления
     * с проверкой, что значение больше 0
     *
     * @param newUpdatetimer Новое значение таймера автообновления
     */
    public void setUpdateTimer(int newUpdatetimer) {
        if (newUpdatetimer > 0) {
            this.updateTimer = newUpdatetimer;
        } else {
            System.out.println("Время таймера должно быть больше 0 секунд");
        }
    }

    /**
     * Метод, отвечающий за добавление города в список городов для
     * автоматического обновления
     *
     * @param CityName Название города, который будет добавлен в список
     * автообновления
     */
    public void addToAutoUpdate(String CityName) {
        try {
            String checksql = "SELECT * FROM auto_update_city where cityname = '" + CityName + "'";
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(checksql);
            if (resultSet.next()) {
                System.out.println("Город " + CityName + " уже был добавлен в список для автообновления");
                return;
            }
            String insertsql = "INSERT INTO auto_update_city (cityname) values ('" + CityName + "')";
            statement.execute(insertsql);
            System.out.println("Город " + CityName + " добавлен в список для автообновления");
        } catch (Exception e) {
            System.out.println("Ошибка при добавлении города в список для автообновления " + e.getLocalizedMessage());
            Logger logger = Logger.getLogger("weather");
            logger.severe(e.getLocalizedMessage());
        }
    }

    /**
     * Метод, отвечающий за удаления города из списка на автообновление
     *
     * @param CityName Название города, который должен быть удалён из списка для
     * автообновления
     */
    public void removeFromAutoUpdate(String CityName) {
        try {
            String deletesql = "DELETE FROM auto_update_city where cityname = '" + CityName + "'";
            Statement statement = this.connection.createStatement();
            statement.execute(deletesql);
            System.out.println("Город " + CityName + " удалён из списка для автообновления");
        } catch (Exception e) {
            System.out.println("Ошибка при удалении города из списка для автообновления " + e.getLocalizedMessage());
            Logger logger = Logger.getLogger("weather");
            logger.severe(e.getLocalizedMessage());
        }
    }

    /**
     * Метод для вывода в консоль всех городов в списке для автообновления
     */
    public void showCitiesInAutoUpdate() {
        try {
            String selectsql = "select * FROM auto_update_city";
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(selectsql);
            System.out.println("Города в списке для автообновления");
            int i = 0;
            while (resultSet.next()) {
                i++;
                System.out.println(Integer.toString(i) + ": " + resultSet.getString("CityName"));
            }
        } catch (Exception e) {
            System.out.println("Ошибка при отображении полного списка городов " + e.getLocalizedMessage());
            Logger logger = Logger.getLogger("weather");
            logger.severe(e.getLocalizedMessage());
        }
    }

    /**
     * Метод, выполняющий обновление для всех городов в списке для
     * автообновления
     */
    public void autoUpdate() {
        try {
            ArrayList<String> CitiesList = new ArrayList<String>();
            String selectsql = "select * FROM auto_update_city";
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(selectsql);
            while (resultSet.next()) {
                CitiesList.add(resultSet.getString("CityName"));
            }
            updater.updateList(CitiesList);
        } catch (Exception e) {
            System.out.println("Ошибка при автоматическом обновлении " + e.getLocalizedMessage());
            Logger logger = Logger.getLogger("weather");
            logger.severe(e.getLocalizedMessage());
        }
    }
}
