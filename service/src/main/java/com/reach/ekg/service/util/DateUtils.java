package com.reach.ekg.service.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static DateTimeFormatter formatter
            = DateTimeFormatter.ofPattern("EEE, d MMM yyyy hh:mm:ss a");

    public static String now() {
        return LocalDateTime.now().format(formatter);
    }
}
