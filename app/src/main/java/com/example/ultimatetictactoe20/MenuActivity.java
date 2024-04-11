package com.example.ultimatetictactoe20;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ultimatetictactoe20.Music.MusicListActivity;
import com.example.ultimatetictactoe20.Music.MusicService;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    public static MusicService musicService;
    private Intent playIntent;
    public static boolean isPlaying;

    private String contactName = "";

    private ActivityResultLauncher<Intent> contentLauncher;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button playContactButton = findViewById(R.id.MENUplayContactBtn);
        Button playGuestButton = findViewById(R.id.MENUplayGuestBtn);
        Button exitButton = findViewById(R.id.MENUexitBtn);

        playContactButton.setOnClickListener(this);
        playGuestButton.setOnClickListener(this);

        exitButton.setOnClickListener(this);

        // music
        musicService = new MusicService();
        isPlaying = false;

        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }

        // contacts
        initContact();
    }

    private void initContact() {
        contentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Cursor cursor;
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        try {
                            cursor = getContentResolver().query(intent.getData(), null, null, null, null);
                            cursor.moveToFirst();

                            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            isPlaying = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isPlaying = false;
        }
    };

    private static final int CONTACT_PICK_REQUEST_CODE = 1001; // You can use any unique integer value

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.MENUplayGuestBtn) startActivity(new Intent(this, GameActivity.class));
        if (id == R.id.MENUplayContactBtn) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, CONTACT_PICK_REQUEST_CODE);
        }

        if (view.getId() == R.id.MENUexitBtn) {
            stopService(playIntent);
            MusicService.stopPlayMusic();
            isPlaying = false;
            finishAffinity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CONTACT_PICK_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri contactUri = data.getData();
                Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);

                if (cursor.moveToFirst()) {

                    int nameColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    String contactName = cursor.getString(nameColumnIndex);

                    // Pass the contact name to the next activity
                    Intent intent = new Intent(this, GameActivity.class);
                    intent.putExtra("CONTACT_NAME", contactName);
                    startActivity(intent);
                }
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.music_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (isPlaying) menu.getItem(0).setTitle("MUTE");
        else menu.getItem(0).setTitle("UNMUTE");

        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.toggleMusicBttn) {
            if (isPlaying) musicService.pause();
            else musicService.resume();
            isPlaying = !isPlaying;
        }
        if (item.getItemId() == R.id.changeMusicBttn) startActivity(new Intent(this, MusicListActivity.class));
        if (item.getItemId() == R.id.eraseMemoryBttn) this.deleteDatabase("memory_db.db");

        return super.onOptionsItemSelected(item);
    }
}