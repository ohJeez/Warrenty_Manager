package com.example.warrantymanager;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class Dates {
    private Dates() {
    }

    public static final String FORMAT = "yyyy-MM-dd";

    public static Date parse(String s) {
        try {
            SimpleDateFormat f = new SimpleDateFormat(FORMAT, Locale.US);
            f.setLenient(false);
            return f.parse(s);
        } catch (Exception e) {
            return null;
        }
    }

    public static String format(Date d) {
        return new SimpleDateFormat(FORMAT, Locale.US).format(d);
    }

    public static String expiry(String purchase, int months) {
        Date d = parse(purchase);
        if (d == null) throw new IllegalArgumentException("Invalid purchase date");
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        int day = c.get(Calendar.DAY_OF_MONTH);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.MONTH, months);
        int last = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        c.set(Calendar.DAY_OF_MONTH, Math.min(day, last));
        return format(c.getTime());
    }

    public static long daysLeft(String date) {
        Date d = parse(date);
        if (d == null) return Long.MIN_VALUE;
        Calendar now = Calendar.getInstance();
        zero(now);
        Calendar target = Calendar.getInstance();
        target.setTime(d);
        zero(target);
        return TimeUnit.MILLISECONDS.toDays(target.getTimeInMillis() - now.getTimeInMillis());
    }

    private static void zero(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    public static String status(String expiry) {
        long days = daysLeft(expiry);
        if (days < 0) return "Expired";
        if (days <= 30) return "Expiring soon (" + days + " days)";
        return "Active (" + days + " days left)";
    }
}
