package com.example.warrantymanager;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ReminderWorker extends Worker {
    public static final String CHANNEL = "warranty_reminders";

    public ReminderWorker(@NonNull Context c, @NonNull WorkerParameters p) {
        super(c, p);
    }

    public static void schedule(Context c) {
        PeriodicWorkRequest work = new PeriodicWorkRequest.Builder(ReminderWorker.class, 1, TimeUnit.DAYS).build();
        WorkManager.getInstance(c).enqueueUniquePeriodicWork("daily_warranty_check", ExistingPeriodicWorkPolicy.KEEP, work);
    }

    public static void channel(Context c) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel(CHANNEL, "Warranty expiry reminders", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(ch);
        }
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        if (Build.VERSION.SDK_INT >= 33 && context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
            return Result.success();
        channel(context);
        DatabaseHelper db = new DatabaseHelper(context);
        for (Product p : db.all("", false)) {
            long days = Dates.daysLeft(p.expiryDate);
            if (days != 30 && days != 7 && days != 0) continue;
            int threshold = (int) days;
            if (db.alreadyNotified(p.id, threshold, p.expiryDate)) continue;
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("id", p.id);
            PendingIntent pending = PendingIntent.getActivity(context, (int) (p.id % 100000) + threshold * 100000, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            String message = days == 0 ? "Warranty expires today" : "Warranty expires in " + days + " days";
            Notification n = new NotificationCompat.Builder(context, CHANNEL).setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle(p.name).setContentText(message).setContentIntent(pending).setAutoCancel(true).build();
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify((int) ((p.id * 37 + threshold) % Integer.MAX_VALUE), n);
            db.markNotified(p.id, threshold, p.expiryDate);
        }
        return Result.success();
    }
}
