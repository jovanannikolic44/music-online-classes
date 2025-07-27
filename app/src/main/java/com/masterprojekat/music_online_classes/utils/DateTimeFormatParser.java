package com.masterprojekat.music_online_classes.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeFormatParser {
    public static String changeDateFormatTo(String date, String oldFormat, String newFormat) {
        SimpleDateFormat oldSimpleDAte = new SimpleDateFormat(oldFormat, Locale.US);
        try {
            Date oldDate = oldSimpleDAte.parse(date);
            if (oldDate == null) {
                return null;
            }
            SimpleDateFormat outputFormat = new SimpleDateFormat(newFormat, Locale.US);
            return outputFormat.format(oldDate);
        } catch (ParseException e) {
            Log.e("DateFormatError", "Greska pri parsiranju datuma: " + e.getMessage(), e);
            return null;
        }
    }

    public static String changeTimeFormatTo(String time, String oldFormat, String newFormat) {
        SimpleDateFormat oldSimpleTime = new SimpleDateFormat(oldFormat, Locale.US);
        try {
            Date oldTime = oldSimpleTime.parse(time);
            if (oldTime == null) {
                return null;
            }
            SimpleDateFormat outputFormat = new SimpleDateFormat(newFormat, Locale.US);
            return outputFormat.format(oldTime);
        } catch (ParseException e) {
            Log.e("TimeFormatError", "Greska pri parsiranju vremena: " + e.getMessage(), e);
            return null;
        }
    }
}
