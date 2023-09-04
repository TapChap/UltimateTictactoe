package com.example.ultimatetictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.stream.IntStream;

public class SplachActivity extends AppCompatActivity {
    private ImageView[] splaches = new ImageView[4];
    private Handler h = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splach);

        // TODO: replace with automatic process (for loop)
        splaches[0] = findViewById(R.id.splach1);
        splaches[1] = findViewById(R.id.splach2);
        splaches[2] = findViewById(R.id.splach3);
        splaches[3] = findViewById(R.id.splach4);

        IntStream.range(0, 4).forEach(i -> setSplachVisible(false, i));


        // can change the amount of cycles by changing this variable alone!
        // all the rest of the code is build on it
        int cycles = 3;

        splachSequance(cycles, 0);
        h.postDelayed(()-> startActivity(new Intent(this, MenuActivity.class)), cycles * 600);
    }

    private void setSplachVisible(boolean isVisible, int index) {
        splaches[index].setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    private void splachOnce(){
        for (int i = 0; i < splaches.length; i++){
            h.postDelayed(
            () -> setSplachVisible(true, i), 100 * i + 100);
        }

        h.postDelayed(() -> IntStream.range(0, 4).forEach(i -> setSplachVisible(false, i)), 500);
    }

    private void splachSequance(int times, int ran){
        Log.d("SPLACH", "splashed once");
        if (ran < times) h.postDelayed(this::splachOnce, ran * 600L);
        else return;

        splachSequance(times, ran + 1);
    }
}