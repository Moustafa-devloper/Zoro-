package com.nexus.ffpanel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class FloatingPanelService extends Service {
    private static final String CHANNEL_ID = "nexus_ff_panel_overlay";
    private static final String ACTION_STOP_PANEL = "com.nexus.ffpanel.STOP_PANEL";

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private FrameLayout overlayView;
    private boolean isAttached;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP_PANEL.equals(intent.getAction())) {
            stopPanel();
            return START_NOT_STICKY;
        }

        if (!OverlayPermissionHelper.canDrawOverlays(this)) {
            stopSelf();
            return START_NOT_STICKY;
        }

        if (windowManager == null) {
            stopSelf();
            return START_NOT_STICKY;
        }

        if (!isAttached) {
            createOverlay();
            startForeground(1001, buildNotification());
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPanel();
    }

    private void stopPanel() {
        stopForeground(true);
        if (windowManager != null && overlayView != null && isAttached) {
            try {
                windowManager.removeViewImmediate(overlayView);
            } catch (IllegalArgumentException ignored) {
                // View may already be detached by the system.
            }
        }
        overlayView = null;
        isAttached = false;
    }

    private void createOverlay() {
        if (isAttached || overlayView != null) {
            return;
        }

        overlayView = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.floating_panel, null, false);
        Button minimizeButton = overlayView.findViewById(R.id.btnMinimize);
        Button stopButton = overlayView.findViewById(R.id.btnStop);
        TextView titleView = overlayView.findViewById(R.id.panelTitle);
        titleView.setText("NEXUS");

        minimizeButton.setOnClickListener(v -> collapsePanel());
        stopButton.setOnClickListener(v -> stopSelf());
        overlayView.setOnTouchListener(new DragTouchListener());

        layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                        ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        : WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
        );
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        layoutParams.x = 36;
        layoutParams.y = 200;

        try {
            windowManager.addView(overlayView, layoutParams);
            isAttached = true;
        } catch (IllegalStateException e) {
            overlayView = null;
            isAttached = false;
            stopSelf();
        }
    }

    private void collapsePanel() {
        if (overlayView != null) {
            overlayView.setAlpha(0.75f);
        }
    }

    private Notification buildNotification() {
        Intent stopIntent = new Intent(this, FloatingPanelService.class);
        stopIntent.setAction(ACTION_STOP_PANEL);
        PendingIntent stopPendingIntent = PendingIntent.getService(
                this,
                0,
                stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_nexus)
                .setContentTitle("NEXUS FF PANEL")
                .setContentText("Floating panel is active")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.ic_stop, "Stop Panel", stopPendingIntent);

        return builder.build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "NEXUS FF PANEL",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private final class DragTouchListener implements View.OnTouchListener {
        private int lastX;
        private int lastY;
        private float initialViewX;
        private float initialViewY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    initialViewX = layoutParams.x;
                    initialViewY = layoutParams.y;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    int deltaX = (int) (event.getRawX() - lastX);
                    int deltaY = (int) (event.getRawY() - lastY);
                    layoutParams.x = (int) (initialViewX + deltaX);
                    layoutParams.y = (int) (initialViewY + deltaY);
                    if (windowManager != null && overlayView != null) {
                        windowManager.updateViewLayout(overlayView, layoutParams);
                    }
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    return true;
                default:
                    return false;
            }
        }
    }
}
