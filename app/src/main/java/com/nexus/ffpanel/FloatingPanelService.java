package com.nexus.ffpanel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
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
    private static final String TAG = "FloatingPanelService";
    private static final String CHANNEL_ID = "nexus_ff_panel_overlay";

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
        stopForeground(true);
        if (windowManager != null && overlayView != null && isAttached) {
            try {
                windowManager.removeView(overlayView);
            } catch (IllegalArgumentException ignored) {
                // Could already be detached.
            }
        }
        overlayView = null;
        isAttached = false;
    }

    private void createOverlay() {
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

        windowManager.addView(overlayView, layoutParams);
        isAttached = true;
    }

    private void collapsePanel() {
        if (overlayView != null) {
            overlayView.setAlpha(0.75f);
        }
    }

    private Notification buildNotification() {
        Intent stopIntent = new Intent(this, FloatingPanelService.class);
        stopIntent.setAction("STOP_PANEL");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_nexus)
                .setContentTitle("NEXUS FF PANEL ULTRA")
                .setContentText("Floating panel active")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setOnlyAlertOnce(true);

        builder.addAction(R.drawable.ic_stop, "Stop", null);
        return builder.build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "NEXUS Floating Panel",
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
        private float initialTouchX;
        private float initialTouchY;
        private float initialViewX;
        private float initialViewY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    initialViewX = layoutParams.x;
                    initialViewY = layoutParams.y;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    int deltaX = (int) (event.getRawX() - lastX);
                    int deltaY = (int) (event.getRawY() - lastY);
                    layoutParams.x = initialViewX + deltaX;
                    layoutParams.y = initialViewY + deltaY;
                    if (windowManager != null) {
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
