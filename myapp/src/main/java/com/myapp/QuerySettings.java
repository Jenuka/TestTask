package com.myapp;

import java.time.LocalDateTime;
import java.util.logging.Logger;

/**
 * Класс, содеращий настройки фильтра при отображении хранимых записей о погоде
 */
public class QuerySettings {

    /**
     * Название города
     */
    private String cityName;
    /**
     * Максимальная температура
     */
    private Double maxTemperature;
    /**
     * Минимальная температура
     */
    private Double minTemperature;
    /**
     * Максимальное (самое позднее) время обновления данных
     */
    private LocalDateTime maxTime;
    /**
     * Минимальное (самое раннее) время обновления данных
     */
    private LocalDateTime minTime;
    /**
     * Тип погоды (солнечно. облачно и т.д.)
     */
    private String weather; //переименовать его и связанные методы
    /**
     * Столбец, по которому упорядочиваются данные при их выводе
     */
    private String sortedBy;
    /**
     * направление сортировки данных false - сортировка по возрастанию true -
     * сортировка по убыванию
     */
    private boolean sortDirection;

    /**
     * Конструктор, создаёт объект, отвечающий за фильтрацию данных при запросах с начальными настройками: <br>
     * Выводятся все имеющиеся данные о погоде, с сортировкой по названию города по алфавиту
     */
    public QuerySettings() {
        cityName = null;
        maxTemperature = null;
        minTemperature = null;
        maxTime = null;
        minTime = null;
        weather = null;
        sortedBy = "cityname";
        sortDirection = false;
    }

    /**
     * Метод, возвращающий название города, по которому выводятся данные
     *
     * @return Название города, по которому выводятся данные
     */
    public String getCityName() {
        return this.cityName;
    }

    /**
     * Метод, устанавливающий название города, по которому должны выводиться
     * данные
     *
     * @param newName Название города, по которому должны выводиться данные
     */
    public void setCityName(String newName) {
        this.cityName = newName;
    }

    /**
     * Метод, возвращающий максимальное ограничение по температуре для выводимых
     * данных
     *
     * @return Максимальная температура, по которой должны выводиться данные
     */
    public Double getMaxTemperature() {
        return this.maxTemperature;
    }

    /**
     * Метод, устанавливающий максимальное занчение температуры для выводимых
     * данных, осуществляет проверку, что максимальная температура не меньше
     * минимальной
     *
     * @param newMaxTemperature Новая максимальная температура для выводимых
     * данных
     */
    public void setMaxTemperature(Double newMaxTemperature) {
        if (newMaxTemperature == null || minTemperature == null || newMaxTemperature >= minTemperature) {
            this.maxTemperature = newMaxTemperature;
        } else {
            System.out.println("Максимальная температура должна быть больше или равна минимальной");
        }

    }

    /**
     * Метод. возращающий минимальное ограничение по температуре для выводимых
     * данных
     *
     * @return Минимальная температура, по которой должны выводиться данные
     */
    public Double getMinTemperature() {
        return this.minTemperature;
    }

    /**
     * Метод, устанавливающий минимальное значение температуры для выводимых
     * данных, осуществляет проверку, что минимальная температура не больше
     * максимальной
     *
     * @param newMinTemperature Новая минимальная температура для выводимых
     * данных
     */
    public void setMinTemperature(Double newMinTemperature) {
        if (newMinTemperature == null || maxTemperature == null || newMinTemperature <= maxTemperature) {
            this.minTemperature = newMinTemperature;
        } else {
            System.out.println("Минимальная температура должна быть меньше или равна минимальной");
        }
    }

    /**
     * Метод, возвращающий минимальное ограничение по времени для выводимых
     * данных
     *
     * @return Минимальное ограничение по времени для выводимых данных
     */
    public LocalDateTime getMinTime() {
        return this.minTime;
    }

    /**
     * Метод, устанавливающий минимальное ограничение по времени для выводимых
     * данных, осуществляет проверку, что минимальное время раньше максимального
     *
     * @param newMinTime Новое минимальное ограничение по времени для выводимых
     * данных
     */
    public void setMinTime(LocalDateTime newMinTime) {
        if (newMinTime == null || maxTime == null || newMinTime.isBefore(maxTime)) {
            this.minTime = newMinTime;
        } else {
            System.out.println("Минимальное время должно быть раньше максимального");
        }
    }

    /**
     * Метод, возвращающий максимальное ограничение по времени для выводимых
     * данных
     *
     * @return Максимальное ограничение по времени для выводимых данных
     */
    public LocalDateTime getMaxTime() {
        return this.maxTime;
    }

    /**
     * Метод, устанавливающий максимальное ограничение по времени для выводимых
     * данных, осуществляет проверку, что максимальное время позже минимального
     *
     * @param newMaxTime Новое максимальное ограничение по времени для выводимых
     * данных
     */
    public void setMaxTime(LocalDateTime newMaxTime) {
        if (newMaxTime == null || minTime == null || newMaxTime.isAfter(minTime)) {
            this.maxTime = newMaxTime;
        } else {
            System.out.println("Максимальное время должно быть позже минимального");
        }
    }

    /**
     * Метод, возвращающий тип погоды, по которому выводятся данные
     *
     * @return Тип погоды, по которому выводятся данные
     */
    public String getWeatherType() {
        return this.weather;
    }

    /**
     * Метод, устанавливающий ограничение по типу погоды для вывода данных
     *
     * @param newWeatherType Тип погоды, по которому должны выводиться данные
     */
    public void setWeatherType(String newWeatherType) {
        this.weather = newWeatherType;
    }

    /**
     * Метод, возвращающий столбец, по которому ведётся сортировка данных при
     * выводе
     *
     * @return столбец, по которому ведётся сортировка
     */
    public String getSortedBy() {
        return this.sortedBy;
    }

    /**
     * Метод, устанавливающий столбец, по которому ведётся сортировка
     *
     * @param newSortedBy Столбец, по которому должна вестись сортировка
     */
    public void setSortedBy(String newSortedBy) {
        this.sortedBy = newSortedBy;
    }

    /**
     * Метод, возвращающий направление сортировки
     *
     * @return Направление сортировки, true, если сортировка по убыванию, false,
     * если сортировка по возрастанию
     */
    public boolean getSortDirection() {
        return this.sortDirection;
    }

    /**
     * Метод, устанавливающий направление сортировки
     *
     * @param newSortDirection Новое направление сортировки
     */
    public void setSortDirection(boolean newSortDirection) {
        this.sortDirection = newSortDirection;
    }

    /**
     * Метод, который возвращает название столбца, по которому осуществляется
     * сортировка, на русском языке
     *
     * @return название столбца, по которому осуществляется сортировка, на
     * русском языке
     */
    public String getSortedByName() {
        switch (getSortedBy().toLowerCase()) {
            case "cityname":
                return "Название города";
            case "temperature":
                return "Температура воздуха";
            case "time":
                return "Время обновления";
            case "weather":
                return "Погода";
        }
        return getSortedBy();
    }

    /**
     * Метод, добавляющий к переданному запросу параметры фильтра (условия и
     * сортировка)
     *
     * @param query Запрос, к которому необходимо добавить параметры фильтра
     * @return Запрос с добавленными параметрами фильтра
     */
    public String addQuerySettings(String query) {
        try {
            StringBuilder sb = new StringBuilder(query);
            boolean added = false;
            if (cityName != null) {
                added = true;
                sb.append(" WHERE ");
                sb.append("cityname = '").append(cityName).append("'");
            }
            if (maxTemperature != null) {
                if (added) {
                    sb.append(" AND ");
                    sb.append("temperature <= '").append(maxTemperature).append("'");
                } else {
                    added = true;
                    sb.append(" WHERE ");
                    sb.append("temperature <= '").append(maxTemperature).append("'");
                }
            }
            if (minTemperature != null) {
                if (added) {
                    sb.append(" AND ");
                    sb.append("temperature >= '").append(minTemperature).append("'");
                } else {
                    added = true;
                    sb.append(" WHERE ");
                    sb.append("temperature >= '").append(minTemperature).append("'");
                }
            }
            if (maxTime != null) {
                if (added) {
                    sb.append(" AND ");
                    sb.append("time <= '").append(maxTime.toString()).append("'");
                } else {
                    added = true;
                    sb.append(" WHERE ");
                    sb.append("time <= '").append(maxTime.toString()).append("'");
                }
            }
            if (minTime != null) {
                if (added) {
                    sb.append(" AND ");
                    sb.append("time >= '").append(minTime.toString()).append("'");
                } else {
                    added = true;
                    sb.append(" WHERE ");
                    sb.append("time >= '").append(minTime.toString()).append("'");
                }
            }
            if (weather != null) {
                if (added) {
                    sb.append(" AND ");
                    sb.append("weather = '").append(weather).append("'");
                } else {
                    added = true;
                    sb.append(" WHERE ");
                    sb.append("weather = '").append(weather).append("'");
                }
            }
            sb.append(" ORDER BY ").append(sortedBy);
            if (sortDirection) {
                sb.append(" DESC");
            } else {
                sb.append(" ASC");
            }
            return sb.toString();
        } catch (Exception e) {
            System.out.println("Ошибка при добавлении параметров к запросу " + e.getLocalizedMessage());
            Logger logger = Logger.getLogger("weather");
            logger.severe(e.getLocalizedMessage());
        }
        return query;
    }
}
