package com.example.ultimatetictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button playButton = findViewById(R.id.MENUplayBtn);
        Button tutorialButton = findViewById(R.id.MENUtutorialBtn);
        Button exitButton = findViewById(R.id.MENUexitBtn);

        playButton.setOnClickListener(this);
        tutorialButton.setOnClickListener(this);
        exitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.MENUplayBtn) startActivity(new Intent(this, GameActivity.class));
        if (id == R.id.MENUtutorialBtn) return;
        if (id == R.id.MENUaboutBtn) return;
        if (id == R.id.MENUexitBtn) return;
    }
}