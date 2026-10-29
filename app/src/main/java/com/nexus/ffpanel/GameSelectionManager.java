package com.nexus.ffpanel;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class GameSelectionManager {
    public static final class GameApp {
        public final String packageName;
        public final String appName;
        public final Drawable icon;

        public GameApp(String packageName, String appName, Drawable icon) {
            this.packageName = packageName;
            this.appName = appName;
            this.icon = icon;
        }
    }

    private GameSelectionManager() {
    }

    public static List<GameApp> getInstalledGames(Context context) {
        if (context == null) {
            return Collections.emptyList();
        }
        PackageManager pm = context.getPackageManager();
        Intent launcher = new Intent(Intent.ACTION_MAIN);
        launcher.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = pm.queryIntentActivities(launcher, 0);

        List<GameApp> gameApps = new ArrayList<>();
        for (ResolveInfo resolveInfo : apps) {
            String packageName = resolveInfo.activityInfo.packageName;
            String label = resolveInfo.loadLabel(pm).toString();
            Drawable icon = resolveInfo.loadIcon(pm);
            gameApps.add(new GameApp(packageName, label, icon));
        }

        gameApps.sort(Comparator.comparing(item -> item.appName.toLowerCase()));
        return gameApps;
    }

    public static boolean launchGame(Context context, String packageName) {
        if (context == null || packageName == null || packageName.trim().isEmpty()) {
            return false;
        }
        PackageManager pm = context.getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage(packageName);
        if (launchIntent == null) {
            return false;
        }
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launchIntent);
        return true;
    }
}
