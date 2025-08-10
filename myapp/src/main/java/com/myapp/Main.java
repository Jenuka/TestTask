package com.myapp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {

    static Updater updater;
    static AutoUpdater autoUpdater;
    static QuerySettings querySettings;
    static Connection connection;
    static Timer timer;
    static Logger logger;

    /**
     * Осуществляет начальные настройки, предоставляет главное меню приложения
     */
    public static void main(String[] args) {
        String jdbcURL = "jdbc:postgresql://localhost:5432/testdb";
        String username = "postgres";
        String password = "0574";
        String key = "b17ca7dae9154582a1d71445250808";
        try {
            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");
            // Establish the connection
            connection = DriverManager.getConnection(jdbcURL, username, password);
            System.out.println("Соединение с базой данных установлено");
            LogManager.getLogManager().reset();
            logger = Logger.getLogger("weather");
            FileHandler handler = new FileHandler("error.log", true);
            logger.addHandler(handler);
            logger.setLevel(Level.ALL);
            updater = new Updater(connection, key);
            autoUpdater = new AutoUpdater(updater, connection);
            timer = new Timer();
            querySettings = new QuerySettings();
            boolean active = true;
            while (active) {
                System.out.println("1: Обновить данные");
                System.out.println("2: Показать данные");
                System.out.println("3: Настройка фильтра");
                System.out.println("4: Настройки автоматического обновления");
                System.out.println("5: Выход");
                String choice = System.console().readLine();
                switch (choice) {
                    case "1":
                        updateData();
                        break;
                    case "2":
                        showData();
                        break;
                    case "3":
                        updateSettings();
                        break;
                    case "4":
                        autoUpdateSettings();
                        break;
                    case "5":
                        active = false;
                        break;
                }
            }
            // Close the connection
            connection.close();
            System.out.println("Соединение закрыто.");
        } catch (Exception e) {
            System.out.println("Ошибка при работе главного меню " + e.getLocalizedMessage());
            logger.severe(e.getLocalizedMessage());
        }
        System.exit(0);
    }

    /**
     * Предоставляет пользователю выбор способа обновления данных о погоде
     */
    public static void updateData() {
        try {
            System.out.println("1: Обновить по названию города из консольного ввода");
            System.out.println("2: Обновить по названиям городов из файла CityList.txt");
            System.out.println("3: Назад");
            String choice = System.console().readLine();
            switch (choice) {
                case "1":
                    System.out.println("введите название города (на английском языке)");
                    String newCity = System.console().readLine();
                    if (newCity.isEmpty()) {
                        System.out.println("Название города не может быть пустым");
                    } else {
                        updater.updateCity(newCity);
                    }
                    break;
                case "2":
                    readCities();
                    System.out.println("Данные обновлены для всех городов из файла");
                    break;
                case "3":
                    break;
            }
        } catch (Exception e) {
            System.out.println("Ошибка при обновлении данных " + e.getLocalizedMessage());
            logger.severe(e.getLocalizedMessage());
        }
    }

    /**
     * Предоставляет меню для выбора способов отображения информации о погоде
     * Также, показывает настройки фильтра
     */
    public static void showData() {
        try {
            boolean showActive = true;
            while (showActive) {
                System.out.println("Текущие настройки фильтра");
                if (querySettings.getCityName() == null) {
                    System.out.println("Выбранный город: любой город");
                } else {
                    System.out.println("Выбранный город: " + querySettings.getCityName());
                }
                if (querySettings.getMaxTemperature() == null) {
                    System.out.println("Максимальная температура воздуха: любая");
                } else {
                    System.out.println("Максимальная температура воздуха: "
                            + querySettings.getMaxTemperature() + " градусов по Цельсию");
                }
                if (querySettings.getMinTemperature() == null) {
                    System.out.println("Минимальная температура воздуха: любая");
                } else {
                    System.out.println("Минимальная температура воздуха: "
                            + querySettings.getMinTemperature() + " градусов по Цельсию");
                }
                if (querySettings.getMaxTime() == null) {
                    System.out.println("Максимальное время обновления: любое");
                } else {
                    System.out.println("Максимальное время обновления: "
                            + querySettings.getMaxTime().toString().replace('T', ' '));
                }
                if (querySettings.getMinTime() == null) {
                    System.out.println("Минимальное время обновления: любое");
                } else {
                    System.out.println("Минимальное время обновления: "
                            + querySettings.getMinTime().toString().replace('T', ' '));
                }
                System.out.println("Сортировка по: " + querySettings.getSortedByName());
                if (querySettings.getSortDirection()) {
                    System.out.println("Направление сортировки: по убыванию");
                } else {
                    System.out.println("Направление сортировки: по возрастанию");
                }
                System.out.println("1: Изменить настройки фильтра");
                System.out.println("2: Вывести все города, погода в которых соответствует фильтру");
                System.out.println("3: Вывести все города, по которым есть данные");
                System.out.println("4: Удалить данные по городу");
                System.out.println("5: Получить все погодные данные по городу за период");
                System.out.println("6: Посчитать среднюю температуру по городу");
                System.out.println("7: Получить минимальную и максимальную температуру по городу за день");
                System.out.println("8: Назад");
                String choice = System.console().readLine();
                switch (choice) {
                    case "1":
                        updateSettings();
                        break;
                    case "2":
                        showAllDataWithFilter();
                        break;
                    case "3":
                        showAllCities();
                        break;
                    case "4":
                        deleteCity();
                        break;
                    case "5":
                        allDataByCity();
                        break;
                    case "6":
                        averageTempByCity();
                        break;
                    case "7":
                        minAndMaxTempByCityDay();
                        break;
                    case "8":
                        showActive = false;
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при отображении данных " + e.getLocalizedMessage());
            logger.severe(e.getLocalizedMessage());
        }
    }

    /**
     * Отвечает за изменение настроек фильтра Отображает текущие настройки и
     * позволяет изменить каждую из них. Имеется возможность сброса всех
     * фильтров
     */
    public static void updateSettings() {
        try {
            boolean settingsActive = true;
            while (settingsActive) {
                System.out.println("Текущие настройки фильтра:");
                if (querySettings.getCityName() == null) {
                    System.out.println("Выбранный город: любой город");
                } else {
                    System.out.println("Выбранный город: " + querySettings.getCityName());
                }
                if (querySettings.getMaxTemperature() == null) {
                    System.out.println("Максимальная температура воздуха: любая");
                } else {
                    System.out.println("Максимальная температура воздуха: "
                            + querySettings.getMaxTemperature() + " градусов по Цельсию");
                }
                if (querySettings.getMinTemperature() == null) {
                    System.out.println("Минимальная температура воздуха: любая");
                } else {
                    System.out.println("Минимальная температура воздуха: "
                            + querySettings.getMinTemperature() + " градусов по Цельсию");
                }
                if (querySettings.getMaxTime() == null) {
                    System.out.println("Максимальное время обновления: любое");
                } else {
                    System.out.println("Максимальное время обновления: "
                            + querySettings.getMaxTime().toString().replace('T', ' '));
                }
                if (querySettings.getMinTime() == null) {
                    System.out.println("Минимальное время обновления: любое");
                } else {
                    System.out.println("Минимальное время обновления: "
                            + querySettings.getMinTime().toString().replace('T', ' '));
                }
                System.out.println("Сортировка по: " + querySettings.getSortedByName());
                if (querySettings.getSortDirection()) {
                    System.out.println("Направление сортировки: по убыванию");
                } else {
                    System.out.println("Направление сортировки: по возрастанию");
                }
                System.out.println("1: Изменить выбранный город");
                System.out.println("2: Изменить максимальную температуру");
                System.out.println("3: Изменить минимальную температуру");
                System.out.println("4: Изменить максимальное время");
                System.out.println("5: Изменить минимальное время");
                System.out.println("6: Изменить критерий для сортировки");
                System.out.println("7: Изменить направление сортировки");
                System.out.println("8: Сбросить настройки");
                System.out.println("9: Назад");
                String choice = System.console().readLine();
                switch (choice) {
                    case "1":
                        System.out.println("Изменение выбранного города");
                        System.out.println("Введите название города, или оставьте пустым для снятия выбора");
                        String newFilterCity = System.console().readLine();
                        if (newFilterCity.isEmpty()) {
                            querySettings.setCityName(null);
                        } else {
                            querySettings.setCityName(newFilterCity);
                        }
                        break;
                    case "2":
                        System.out.println("Изменение выбранной максимальной температуры воздуха");
                        System.out.println("Введите новую максимальную температуру или оставьте пустым для снятия выбора:");
                        String maxTempString = System.console().readLine();
                        if (maxTempString.isEmpty()) {
                            querySettings.setMaxTemperature(null);
                        } else {
                            Double newMaxTemp = Double.parseDouble(maxTempString);
                            querySettings.setMaxTemperature(newMaxTemp);
                        }
                        break;
                    case "3":
                        System.out.println("Изменение выбранной минимальной температуры воздуха");
                        System.out.println("Введите новую минимальную температуру или оставьте пустым для снятия выбора:");
                        String minTempString = System.console().readLine();
                        if (minTempString.isEmpty()) {
                            querySettings.setMinTemperature(null);
                        } else {
                            Double newMinTemp = Double.parseDouble(minTempString);
                            querySettings.setMinTemperature(newMinTemp);
                        }
                        break;
                    case "4":
                        System.out.println("Изменение выбранного минимального времени получения данных");
                        System.out.println("Формат: ГГГГ-ММ-ДД ЧЧ:ММ:СС");
                        System.out.println("Введите новое минимальное время или оставьте пустым для снятия выбора:");
                        String mintimeString = System.console().readLine();
                        if (mintimeString.isEmpty()) {
                            querySettings.setMinTime(null);
                        } else {
                            mintimeString = mintimeString.replace(' ', 'T');
                            LocalDateTime newMinTime = LocalDateTime.parse(mintimeString);
                            querySettings.setMinTime(newMinTime);
                        }
                        break;
                    case "5":
                        System.out.println("Изменение выбранного максимального времени получения данных");
                        System.out.println("Формат: ГГГГ-ММ-ДД ЧЧ:ММ:СС");
                        System.out.println("Введите новое максимальное время или оставьте пустым для снятия выбора:");
                        String maxtimeString = System.console().readLine();
                        if (maxtimeString.isEmpty()) {
                            querySettings.setMaxTime(null);
                        } else {
                            maxtimeString = maxtimeString.replace(' ', 'T');
                            LocalDateTime newMaxTime = LocalDateTime.parse(maxtimeString);
                            querySettings.setMaxTime(newMaxTime);
                        }
                        break;
                    case "6":
                        System.out.println("Выбор столбца для сортировки");
                        System.out.println("Введите номер столбца:");
                        System.out.println("1: Название города");
                        System.out.println("2: Температура воздуха");
                        System.out.println("3: Дата обновления данных");
                        System.out.println("4: Погода");
                        String choice2 = System.console().readLine();
                        switch (choice2) {
                            case "1":
                                querySettings.setSortedBy("cityname");
                                break;
                            case "2":
                                querySettings.setSortedBy("temperature");
                                break;
                            case "3":
                                querySettings.setSortedBy("time");
                                break;
                            case "4":
                                querySettings.setSortedBy("weather");
                                break;
                        }
                        break;
                    case "7":
                        if (querySettings.getSortDirection()) {
                            querySettings.setSortDirection(false);
                        } else {
                            querySettings.setSortDirection(true);
                        }
                        break;
                    case "8":
                        System.out.println("Вы уверены, что хотите сбросить настройки?");
                        System.out.println("Введите Y, если хотите продолжить");
                        String resetAnswer = System.console().readLine();
                        if (resetAnswer.toLowerCase().equals("y")) {
                            querySettings = new QuerySettings();
                        }
                        break;
                    case "9":
                        settingsActive = false;
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при обновлении настроек " + e.getLocalizedMessage());
            logger.severe(e.getLocalizedMessage());
        }
    }

    /**
     * Выводит все имеющиеся данные о погоде, применяя настроенный пользователем
     * фильтр
     */
    public static void showAllDataWithFilter() {
        try {
            System.out.flush();
            Statement statement = connection.createStatement();
            String allDataSQL = "SELECT * FROM weather_reports";
            allDataSQL = querySettings.addQuerySettings(allDataSQL);
            ResultSet resultSet = statement.executeQuery(allDataSQL);
            System.out.println("Все данные, соответствующие фильтру:");
            while (resultSet.next()) {
                System.out.println("Название города: " + resultSet.getString("CityName")
                        + " Погода: " + resultSet.getString("Weather")
                        + " Температура воздуха: " + resultSet.getDouble("Temperature")
                        + " Дата и время: " + resultSet.getTimestamp("Time"));
            }
            System.console().readLine();
        } catch (Exception e) {
            System.out.println("Ошибка при отображении данных " + e.getLocalizedMessage());
            logger.severe(e.getLocalizedMessage());
        }
    }

    /**
     * Выводит список из всех городов, по которым есть информация о погоде
     */
    public static void showAllCities() {
        try {
            Statement statement = connection.createStatement();
            String allCitiesSQL = "SELECT DISTINCT CityName FROM weather_reports ORDER BY cityname";
            ResultSet resultSet = statement.executeQuery(allCitiesSQL);
            System.out.println("Все города, по которым есть данные:");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("CityName"));
            }
            System.console().readLine();
        } catch (Exception e) {
            System.out.println("Ошибка при отображении полного списка городов " + e.getLocalizedMessage());
            logger.severe(e.getLocalizedMessage());
        }
    }

    /**
     * Удаляет всю информацию о погоде по выбранному городу, также может удалить
     * этот город из списка на автообновление
     */
    public static void deleteCity() {
        //с проверкой на автообновление
        try {
            System.out.println("Введите название города для удаления всех данных о нём,"
                    + "он также будет удалён из списка для автообновления, если он там находится.");
            String cityToDelete = System.console().readLine();
            Statement statement = connection.createStatement();
            String deleteCitySQL = "DELETE FROM Weather_Reports WHERE CityName = '" + cityToDelete + "'";
            statement.execute(deleteCitySQL);
            deleteCitySQL = "DELETE FROM auto_update_city WHERE CityName = '" + cityToDelete + "'";
            statement.execute(deleteCitySQL);
            System.out.println("Данные по городу " + cityToDelete + " удалены");
            System.console().readLine();
        } catch (Exception e) {
            System.out.println("Ошибка при удалении данных по городу " + e.getLocalizedMessage());
            logger.severe(e.getLocalizedMessage());
        }
    }

    /**
     * Выводит все данные по выбранному городу за выбранный пользователем период
     */
    public static void allDataByCity() {
        try {
            System.out.println("Введите название города: ");
            String cityToSearch = System.console().readLine();
            Statement statement = connection.createStatement();
            System.out.println("Введите начальную дату периода: ");
            System.out.println("Формат: ГГГГ-ММ-ДД");
            String startTime = System.console().readLine();
            System.out.println("Введите конечную дату периода: ");
            System.out.println("Формат: ГГГГ-ММ-ДД");
            String endTime = System.console().readLine();
            String cityDataSQL = "SELECT * FROM weather_reports WHERE cityname = '" + cityToSearch + "' AND DATE(Time) >= '"
                    + startTime + "' AND DATE(Time) <= '" + endTime + "'";
            ResultSet resultSet = statement.executeQuery(cityDataSQL);
            System.out.println("Все данные по городу " + cityToSearch);
            while (resultSet.next()) {
                System.out.println("Название города: " + resultSet.getString("CityName")
                        + " Погода: " + resultSet.getString("Weather")
                        + " Температура воздуха: " + resultSet.getDouble("Temperature")
                        + " Дата и время: " + resultSet.getTimestamp("Time"));
            }
            System.console().readLine();
        } catch (Exception e) {
            System.out.println("Ошибка при выводе данных по городу " + e.getLocalizedMessage());
            logger.severe(e.getLocalizedMessage());
        }
    }

    /**
     * Выводит среднюю температуру из всех имеющихся данных по городу
     */
    public static void averageTempByCity() {
        try {
            System.out.println("Введите название города: ");
            String cityToFindAvgTemp = System.console().readLine();
            Statement statement = connection.createStatement();
            String cityTempDataSQL = "SELECT CityName, AVG(Temperature) as AvgTemp FROM Weather_Reports WHERE CityName = '"
                    + cityToFindAvgTemp + "' GROUP BY cityname";
            ResultSet resultSet = statement.executeQuery(cityTempDataSQL);
            System.out.println("Средняя температура по городу " + cityToFindAvgTemp);
            if (resultSet.next()) {
                System.out.println("Название города: " + resultSet.getString("CityName")
                        + " Средняя температура воздуха: " + resultSet.getDouble("AvgTemp"));
            }
            System.console().readLine();
        } catch (Exception e) {
            System.out.println("Ошибка при выводе данных по температуре " + e.getLocalizedMessage());
            logger.severe(e.getLocalizedMessage());
        }
    }

    /**
     * Выводит минимальную и максимальную температуру по городу из имеющихся
     * данных за выбранный день
     */
    public static void minAndMaxTempByCityDay() {
        try {
            System.out.println("Введите название города: ");
            String cityToFindMinMaxTemp = System.console().readLine();
            System.out.println("Введите дату: ");
            System.out.println("Формат: ГГГГ-ММ-ДД");
            String timeString = System.console().readLine();
            Statement statement = connection.createStatement();
            String cityTempDataSQL = "SELECT CityName, Temperature FROM Weather_Reports WHERE CityName = '" + cityToFindMinMaxTemp
                    + "' AND Date(Time) = '" + timeString + "' ORDER BY Temperature DESC LIMIT 1";
            ResultSet resultSet = statement.executeQuery(cityTempDataSQL);
            if (resultSet.next()) {
                System.out.println("Максимальная температура по городу " + resultSet.getString("CityName")
                        + resultSet.getDouble("Temperature"));
            }
            cityTempDataSQL = "SELECT CityName, Temperature FROM Weather_Reports WHERE CityName = '" + cityToFindMinMaxTemp
                    + "' AND Date(Time) = '" + timeString + "' ORDER BY Temperature ASC LIMIT 1";
            resultSet = statement.executeQuery(cityTempDataSQL);
            if (resultSet.next()) {
                System.out.println("Минимальная температура по городу " + resultSet.getString("CityName")
                        + ": " + resultSet.getDouble("Temperature"));
            }
            System.console().readLine();
        } catch (Exception e) {
            System.out.println("Ошибка при выводе данных по температуре " + e.getLocalizedMessage());
            logger.severe(e.getLocalizedMessage());
        }
    }

    /**
     * Считывает список городов из файла CityList.txt и вызывает метод для
     * обновления данных по ним
     */
    public static void readCities() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("CityList.txt"));
            String line;
            ArrayList<String> citiesList = new ArrayList<String>();
            while ((line = reader.readLine()) != null) {
                citiesList.add(line);
            }
            updater.updateList(citiesList);
            reader.close();
        } catch (Exception e) {
            System.out.println("Ошибка при отображении полного списка городов " + e.getLocalizedMessage());
            logger.severe(e.getLocalizedMessage());
        }
    }

    /**
     * Предоставляет меню для настроек автоматического обновления
     */
    public static void autoUpdateSettings() {
        try {
            boolean settingsActive = true;
            while (settingsActive) {
                System.out.println("Настройки автоматического обновления");
                if (autoUpdater.getAutoUpdate()) {
                    System.out.println("Автоматическое обновление включено");
                    System.out.println("Таймер автоматического обновления: " + Integer.toString(autoUpdater.getUpdateTimer() / 1000) + " с.");
                    System.out.println("1: Выключить автоматическое обновление");
                } else {
                    System.out.println("Автоматическое обновление отключено");
                    System.out.println("Таймер автоматического обновления: " + Integer.toString(autoUpdater.getUpdateTimer() / 1000) + " с.");
                    System.out.println("1: Включить автоматическое обновление");
                }
                System.out.println("2: Изменить таймер автоматического обновления");
                System.out.println("3: Посмотреть список городов для автообновления");
                System.out.println("4: Добавить город в список для автообновления");
                System.out.println("5: Исключить город из списка для автообновления");
                System.out.println("6: Выполнить автообновление сейчас");
                System.out.println("7: Назад");
                String choice = System.console().readLine();
                switch (choice) {
                    case "1":
                        if (autoUpdater.getAutoUpdate()) {
                            autoUpdater.setAutoUpdate(false);
                            timer.cancel();
                        } else {
                            autoUpdater.setAutoUpdate(true);
                            timer = new Timer();
                            timer.scheduleAtFixedRate(new AutoUpdateTimer(autoUpdater), autoUpdater.getUpdateTimer(), autoUpdater.getUpdateTimer());
                        }
                        break;
                    case "2":
                        System.out.println("Изменение таймера автоматического обновления");
                        System.out.println("Текущее время для таймера: " + Integer.toString(autoUpdater.getUpdateTimer() / 1000));
                        System.out.println("Введите новое время для таймера (в секундах)");
                        int newTimer = Integer.parseInt(System.console().readLine()) * 1000;
                        autoUpdater.setUpdateTimer(newTimer);
                        if (autoUpdater.getAutoUpdate()) {
                            timer.cancel();
                            timer = new Timer();
                            timer.scheduleAtFixedRate(new AutoUpdateTimer(autoUpdater), autoUpdater.getUpdateTimer(), autoUpdater.getUpdateTimer());
                        }
                        break;
                    case "3":
                        autoUpdater.showCitiesInAutoUpdate();
                        System.console().readLine();
                        break;
                    case "4":
                        System.out.println("Добавление нового города в список для автообновления");
                        System.out.println("Введите название города (на английском языке)");
                        String newCity = System.console().readLine();
                        autoUpdater.addToAutoUpdate(newCity);
                        break;
                    case "5":
                        System.out.println("Удаление города из списка для автообновления");
                        System.out.println("Введите название города (на английском языке)");
                        String removeCity = System.console().readLine();
                        autoUpdater.removeFromAutoUpdate(removeCity);
                        break;
                    case "6":
                        autoUpdater.autoUpdate();
                        break;
                    case "7":
                        settingsActive = false;
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при настройках автоматического обновления " + e.getLocalizedMessage());
            logger.severe(e.getLocalizedMessage());
        }
    }
}
