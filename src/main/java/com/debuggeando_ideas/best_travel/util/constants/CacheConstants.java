package com.debuggeando_ideas.best_travel.util.constants;

public class CacheConstants {
    public static final String FLY_CACHE_NAME = "flights";
    public static final String HOTEL_CACHE_NAME = "hotels";
    // Equivalente a cada medianoche para la "expresión crontab"
    public static final String SCHEDULED_RESET_CACHE_MIDNIGHT = "0 0 0 * * ?";
    // Equivalente a cada 1 minuto para la "expresión crontab"
    public static final String SCHEDULED_RESET_CACHE_MINUTE = "0 * * * * *"; //"0 0 0 * * ?";

}
