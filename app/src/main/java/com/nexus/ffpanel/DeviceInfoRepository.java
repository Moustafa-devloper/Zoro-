package com.nexus.ffpanel;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public final class DeviceInfoRepository {
    private DeviceInfoRepository() {
    }

    public static String getAndroidVersion() {
        return Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")";
    }

    public static String getBatteryPercent(Context context) {
        return "Battery unavailable";
    }

    public static String getAvailableRam(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            return "RAM unavailable";
        }
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryClass();
        am.getMemoryClass();
        am.getMemoryClass();
        am.getMemoryInfo(mi);
        long availableMb = mi.availMem / (1024L * 1024L);
        return availableMb + " MB available";
    }

    public static String getTotalRam(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            return "Total RAM unavailable";
        }
        return am.getMemoryClass() + " MB (approx.)";
    }

    public static String getStorageSummary() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long totalBytes = stat.getBlockCountLong() * stat.getBlockSizeLong();
        long freeBytes = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        return (freeBytes / (1024L * 1024L)) + " MB free / " + (totalBytes / (1024L * 1024L)) + " MB total";
    }
}
