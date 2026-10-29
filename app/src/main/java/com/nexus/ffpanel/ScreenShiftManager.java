package com.nexus.ffpanel;

import android.os.Build;

public final class ScreenShiftManager {
    private ScreenShiftManager() {
    }

    public static String getCapabilityStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return "Screen Shift capability is device- and permission-dependent. Supported system-level display changes may require Shizuku or a privileged route.";
        }
        return "Screen Shift is not supported on this Android version without a privileged system route.";
    }

    public static boolean canApplyProfiles() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }
}
