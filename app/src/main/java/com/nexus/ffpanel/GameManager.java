package com.nexus.ffpanel;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class GameManager {
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

    private GameManager() {
    }

    public static List<GameApp> getInstalledGames(Context context) {
        if (context == null) {
            return new ArrayList<>();
        }
        PackageManager packageManager = context.getPackageManager();
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = packageManager.queryIntentActivities(launcherIntent, 0);

        List<GameApp> gameApps = new ArrayList<>();
        for (ResolveInfo info : apps) {
            String packageName = info.activityInfo.packageName;
            String appName = info.loadLabel(packageManager).toString();
            Drawable icon = info.loadIcon(packageManager);
            gameApps.add(new GameApp(packageName, appName, icon));
        }

        gameApps.sort(Comparator.comparing(a -> a.appName.toLowerCase()));
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
