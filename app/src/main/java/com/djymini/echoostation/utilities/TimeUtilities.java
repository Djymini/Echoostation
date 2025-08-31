package com.djymini.echoostation.utilities;

import java.util.Locale;

public class TimeUtilities {
    public static long currentTimeMillis(){
        return System.currentTimeMillis();
    }

    public static String formatDuration(long durationMs) {
        long minutes = (durationMs / 1000) / 60;
        long seconds = (durationMs / 1000) % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }
}
