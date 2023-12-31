package com.example.ultimatetictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private SwitchMaterial flipForOSwitch;
    public static boolean flipFor0 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        flipForOSwitch = findViewById(R.id.switch0);
        flipForOSwitch.setOnClickListener(view -> flipFor0 = !flipFor0);
    }
}