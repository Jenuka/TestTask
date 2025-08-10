package com.myapp;

import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * Класс, отвечающий за автообновление данных о погоде по таймеру
 */
public class AutoUpdateTimer extends TimerTask {

    /**
     * Объект, осуществляющий обновление данных
     */
    private AutoUpdater autoUpdater;

    /**
     * Конструктор,  создаёт объект, который добавляется в таймер для последующего выполнения автообновлений по расписанию
     * @param autoUpdater Объект, осуществляющий обновление данных
     */
    public AutoUpdateTimer(AutoUpdater autoUpdater) {
        this.autoUpdater = autoUpdater;
    }

    /**
     * Метод, который вызывает автообновление, если оно включено
     */
    @Override
    public void run() {
        try {
            if (autoUpdater.getAutoUpdate()) {
                autoUpdater.autoUpdate();
            }
        } catch (Exception e) {
            System.out.println("Ошибка при запуске автоматического обновления " + e.getLocalizedMessage());
            Logger logger = Logger.getLogger("weather");
            logger.severe(e.getLocalizedMessage());
        }
    }
}
