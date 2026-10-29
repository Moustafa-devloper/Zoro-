package com.nexus.ffpanel;

import android.content.Context;
import android.content.pm.PackageManager;

public final class ShizukuStatusHelper {
    private static final String SHIZUKU_PACKAGE = "moe.shizuku.privileged.api";

    private ShizukuStatusHelper() {
    }

    public static String getStatus(Context context) {
        if (context == null) {
            return "Unavailable";
        }
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(SHIZUKU_PACKAGE, 0);
            return "Installed";
        } catch (PackageManager.NameNotFoundException e) {
            return "Not installed";
        }
    }

    public static boolean isInstalled(Context context) {
        return "Installed".equals(getStatus(context));
    }
}
