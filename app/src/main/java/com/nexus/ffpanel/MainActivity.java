package com.nexus.ffpanel;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS = "nexus_prefs";
    private static final String KEY_GAMING_MODE = "gaming_mode";
    private static final String KEY_SELECTED_GAME = "selected_game";
    private static final String KEY_PANEL_ACTIVE = "panel_active";

    private SharedPreferences prefs;
    private TextView tvDeviceSummary;
    private TextView tvGamingState;
    private TextView tvFloatingState;
    private TextView tvShizukuState;
    private TextView tvFpsState;
    private TextView tvCapabilityState;
    private TextView tvSelectedGame;
    private Button btnToggleGamingMode;
    private Button btnFloatingPanel;
    private Button btnOpenSettings;
    private Button btnSettings;
    private Button btnSelectGame;
    private Button btnScreenShift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        tvDeviceSummary = findViewById(R.id.tvDeviceSummary);
        tvGamingState = findViewById(R.id.tvGamingState);
        tvFloatingState = findViewById(R.id.tvFloatingState);
        tvShizukuState = findViewById(R.id.tvShizukuState);
        tvFpsState = findViewById(R.id.tvFpsState);
        tvCapabilityState = findViewById(R.id.tvCapabilityState);
        tvSelectedGame = findViewById(R.id.tvSelectedGame);
        btnToggleGamingMode = findViewById(R.id.btnToggleGamingMode);
        btnFloatingPanel = findViewById(R.id.btnFloatingPanel);
        btnOpenSettings = findViewById(R.id.btnOpenSettings);
        btnSettings = findViewById(R.id.btnSettings);
        btnSelectGame = findViewById(R.id.btnSelectGame);
        btnScreenShift = findViewById(R.id.btnScreenShift);

        btnToggleGamingMode.setOnClickListener(v -> toggleGamingMode());
        btnFloatingPanel.setOnClickListener(v -> toggleFloatingPanel());
        btnOpenSettings.setOnClickListener(v -> openSettings());
        btnSettings.setOnClickListener(v -> openSettings());
        btnSelectGame.setOnClickListener(v -> openGameSelection());
        btnScreenShift.setOnClickListener(v -> showScreenShiftInfo());

        updateDashboard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDashboard();
    }

    private void toggleGamingMode() {
        boolean enabled = prefs.getBoolean(KEY_GAMING_MODE, false);
        prefs.edit().putBoolean(KEY_GAMING_MODE, !enabled).apply();
        updateDashboard();
    }

    private void toggleFloatingPanel() {
        if (!OverlayPermissionHelper.canDrawOverlays(this)) {
            OverlayPermissionHelper.openOverlaySettings(this);
            return;
        }

        boolean active = prefs.getBoolean(KEY_PANEL_ACTIVE, false);
        prefs.edit().putBoolean(KEY_PANEL_ACTIVE, !active).apply();

        if (!active) {
            Intent service = new Intent(this, FloatingPanelService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(service);
            } else {
                startService(service);
            }
        } else {
            stopService(new Intent(this, FloatingPanelService.class));
        }

        updateDashboard();
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void openGameSelection() {
        java.util.List<GameSelectionManager.GameApp> games = GameSelectionManager.getInstalledGames(this);
        if (games.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Game library")
                    .setMessage("No launchable games were detected on this device.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        String[] labels = new String[games.size()];
        for (int i = 0; i < games.size(); i++) {
            labels[i] = games.get(i).appName;
        }

        new AlertDialog.Builder(this)
                .setTitle("Select a game")
                .setItems(labels, (dialog, which) -> {
                    GameSelectionManager.GameApp selected = games.get(which);
                    prefs.edit().putString(KEY_SELECTED_GAME, selected.packageName).apply();
                    tvSelectedGame.setText("Selected game: " + selected.appName);
                    GameSelectionManager.launchGame(this, selected.packageName);
                })
                .show();
    }

    private void showScreenShiftInfo() {
        new AlertDialog.Builder(this)
                .setTitle("Screen Shift")
                .setMessage(ScreenShiftManager.getCapabilityStatus())
                .setPositiveButton("OK", null)
                .show();
    }

    private void updateDashboard() {
        boolean gamingMode = prefs.getBoolean(KEY_GAMING_MODE, false);
        boolean panelActive = prefs.getBoolean(KEY_PANEL_ACTIVE, false);
        String selectedGame = prefs.getString(KEY_SELECTED_GAME, null);

        String deviceSummary = "Android " + DeviceInfoRepository.getAndroidVersion() + "\n"
                + DeviceInfoRepository.getAvailableRam(this) + "\n"
                + DeviceInfoRepository.getTotalRam(this);
        tvDeviceSummary.setText(deviceSummary);

        tvGamingState.setText(gamingMode ? "Gaming mode active" : "Gaming mode idle");
        tvFloatingState.setText(panelActive ? "Floating panel active" : "Floating panel stopped");
        tvShizukuState.setText(ShizukuStatusHelper.getStatus(this));
        tvFpsState.setText("FPS measurement unavailable on this device.");
        tvCapabilityState.setText(ScreenShiftManager.getCapabilityStatus());

        if (selectedGame != null && !selectedGame.trim().isEmpty()) {
            tvSelectedGame.setText("Selected game: " + selectedGame);
        } else {
            tvSelectedGame.setText("Selected game: none");
        }

        btnToggleGamingMode.setText(gamingMode ? "Disable Gaming Mode" : "Enable Gaming Mode");
        btnFloatingPanel.setText(panelActive ? "Stop Floating Panel" : "Start Floating Panel");
    }
}
