package com.nexus.ffpanel;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;
import android.os.PowerManager;

public final class PerformanceMonitor {
    private PerformanceMonitor() {
    }

    public static String getBatteryPercent(Context context) {
        if (context == null) {
            return "Battery unavailable";
        }
        Intent batteryIntent = context.registerReceiver(null, new Intent().setAction(Intent.ACTION_BATTERY_CHANGED));
        if (batteryIntent == null) {
            return "Battery unavailable";
        }
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        if (level < 0 || scale <= 0) {
            return "Battery unavailable";
        }
        return level + "%";
    }

    public static String getBatteryStatus(Context context) {
        if (context == null) {
            return "Battery unavailable";
        }
        Intent batteryIntent = context.registerReceiver(null, new Intent().setAction(Intent.ACTION_BATTERY_CHANGED));
        if (batteryIntent == null) {
            return "Battery unavailable";
        }
        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
            return "Charging";
        }
        if (status == BatteryManager.BATTERY_STATUS_DISCHARGING || status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
            return "Discharging";
        }
        return "Battery unavailable";
    }

    public static boolean isPowerSaveMode(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (powerManager == null) {
            return false;
        }
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && powerManager.isPowerSaveMode();
    }

    public static String getAvailableMemory(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) {
            return "Memory unavailable";
        }
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        manager.getMemoryClass();
        manager.getMemoryInfo(info);
        long availableMb = info.availMem / (1024L * 1024L);
        return availableMb + " MB available";
    }
}
