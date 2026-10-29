package com.nexus.ffpanel;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private CheckBox cbDarkTheme;
    private CheckBox cbAmoled;
    private CheckBox cbHaptics;
    private SeekBar sbPanelSize;
    private SeekBar sbOpacity;
    private TextView tvSizeValue;
    private TextView tvOpacityValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        cbDarkTheme = findViewById(R.id.cbDarkTheme);
        cbAmoled = findViewById(R.id.cbAmoled);
        cbHaptics = findViewById(R.id.cbHaptics);
        sbPanelSize = findViewById(R.id.sbPanelSize);
        sbOpacity = findViewById(R.id.sbOpacity);
        tvSizeValue = findViewById(R.id.tvSizeValue);
        tvOpacityValue = findViewById(R.id.tvOpacityValue);

        Button btnReset = findViewById(R.id.btnResetSettings);
        Button btnBack = findViewById(R.id.btnBackSettings);

        cbDarkTheme.setChecked(SettingsRepository.getBoolean(this, SettingsRepository.KEY_THEME_DARK, true));
        cbAmoled.setChecked(SettingsRepository.getBoolean(this, SettingsRepository.KEY_AMOLED, true));
        cbHaptics.setChecked(SettingsRepository.getBoolean(this, SettingsRepository.KEY_HAPTIC_FEEDBACK, true));

        sbPanelSize.setMax(120);
        sbPanelSize.setProgress(SettingsRepository.getInt(this, SettingsRepository.KEY_FLOATING_PANEL_SIZE, 80));
        sbOpacity.setMax(100);
        sbOpacity.setProgress(SettingsRepository.getInt(this, SettingsRepository.KEY_PANEL_OPACITY, 80));

        updateLabels();

        sbPanelSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SettingsRepository.putInt(SettingsActivity.this, SettingsRepository.KEY_FLOATING_PANEL_SIZE, progress);
                updateLabels();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        sbOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SettingsRepository.putInt(SettingsActivity.this, SettingsRepository.KEY_PANEL_OPACITY, progress);
                updateLabels();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        cbDarkTheme.setOnCheckedChangeListener((buttonView, isChecked) ->
                SettingsRepository.putBoolean(this, SettingsRepository.KEY_THEME_DARK, isChecked));
        cbAmoled.setOnCheckedChangeListener((buttonView, isChecked) ->
                SettingsRepository.putBoolean(this, SettingsRepository.KEY_AMOLED, isChecked));
        cbHaptics.setOnCheckedChangeListener((buttonView, isChecked) ->
                SettingsRepository.putBoolean(this, SettingsRepository.KEY_HAPTIC_FEEDBACK, isChecked));

        btnReset.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Reset settings")
                .setMessage("Reset all saved NEXUS settings to default values?")
                .setPositiveButton("Reset", (dialog, which) -> {
                    SettingsRepository.resetDefaults(this);
                    cbDarkTheme.setChecked(true);
                    cbAmoled.setChecked(true);
                    cbHaptics.setChecked(true);
                    sbPanelSize.setProgress(80);
                    sbOpacity.setProgress(80);
                    updateLabels();
                })
                .setNegativeButton("Cancel", null)
                .show());

        btnBack.setOnClickListener(v -> finish());
    }

    private void updateLabels() {
        tvSizeValue.setText("Panel size: " + sbPanelSize.getProgress() + "%");
        tvOpacityValue.setText("Opacity: " + sbOpacity.getProgress() + "%");
    }
}
