package com.example.ultimatetictactoe20;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;
import java.util.function.Supplier;

public class LowBatteryReceiver extends BroadcastReceiver {
    private final static String BATTERY_LEVEL = "level";
    private final Runnable saveGame;

    private final boolean hasContact;
    private boolean displayed = false;

    private TextToSpeech tts;

    public LowBatteryReceiver(Runnable saveGame, boolean hasContact) {
        this.saveGame = saveGame;
        this.hasContact = hasContact;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int level = intent.getIntExtra(BATTERY_LEVEL, 0);
        Log.d("AutoSave", intent.getExtras().toString());

        if (level == 2 && hasContact && !displayed) { // save the game if battery hits 2% and playing against a contact
            saveGame.run();
            tts = new TextToSpeech(context, i -> {if (i == TextToSpeech.SUCCESS) tts.setLanguage(Locale.ENGLISH);});
            new Handler().postDelayed(()-> tts.speak("Phone about to shutoff, saving game.", TextToSpeech.QUEUE_ADD, Bundle.EMPTY, null), 50L);
            displayed = true;

            Log.d("AutoSave", "saved game");
            Toast.makeText(context, "Phone about to shutoff, saving game.", Toast.LENGTH_LONG).show();
        }
    }
}