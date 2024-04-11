package com.example.ultimatetictactoe20;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.function.Supplier;

public class LowBatteryReceiver extends BroadcastReceiver {
    private final static String BATTERY_LEVEL = "level";
    private final Runnable saveGame;

    private final boolean hasContact;

    public LowBatteryReceiver(Runnable saveGame, boolean hasContact) {
        this.saveGame = saveGame;
        this.hasContact = hasContact;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int level = intent.getIntExtra(BATTERY_LEVEL, 0);

        if (level == 2 && hasContact) { // save the game if battery hits 2% and playing against a contact
            saveGame.run();
            Log.d("AutoSave", "saved game");
            Toast.makeText(context, "Phone about to shutoff, saving game.", Toast.LENGTH_LONG).show();
        }
    }
}