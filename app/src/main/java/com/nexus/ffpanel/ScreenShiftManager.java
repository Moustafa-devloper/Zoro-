package com.nexus.ffpanel;

import java.util.Arrays;
import java.util.List;

public final class ScreenShiftManager {
    public static final class DisplayProfile {
        public final String id;
        public final String label;
        public final int width;
        public final int height;
        public final float density;

        public DisplayProfile(String id, String label, int width, int height, float density) {
            this.id = id;
            this.label = label;
            this.width = width;
            this.height = height;
            this.density = density;
        }
    }

    public static List<DisplayProfile> getProfiles() {
        return Arrays.asList(
                new DisplayProfile("default", "Default", 1080, 2400, 3.0f),
                new DisplayProfile("balanced", "Balanced", 1080, 2340, 2.75f),
                new DisplayProfile("compact", "Compact", 960, 2160, 2.5f),
                new DisplayProfile("custom", "Custom", 1080, 2340, 2.75f)
        );
    }

    public static String getCapabilityStatus() {
        return "Display resizing is not supported for this app without a privileged system route or Shizuku authorization.";
    }

    public static boolean canApplyProfiles() {
        return false;
    }
}
