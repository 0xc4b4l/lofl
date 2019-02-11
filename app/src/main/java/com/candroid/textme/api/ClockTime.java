package com.candroid.textme.api;

import android.util.Log;

import java.util.Date;

public class ClockTime {

    public static boolean isBetweenMidnightAndFive() {
        Date date = new Date(System.currentTimeMillis());
        return date.getHours() < 5 && date.getHours() >= 0;
    }

    public static long millisTillMidnight() {
        Date currentDate = new Date(System.currentTimeMillis());
        Date midnight = new Date(currentDate.getYear(), currentDate.getMonth(), currentDate.getDay(), 0, 0, 10);
        midnight.setHours(0);
        midnight.setDate(currentDate.getDate() + 1);
        Log.d("DATE", String.format("Current date = %s", currentDate.toString()));
        Log.d("DATE", String.format("midnight date = %s", midnight.toString()));
        return midnight.getTime() - currentDate.getTime();
    }

}
