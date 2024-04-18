package com.example.ultimatetictactoe20.Music;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ultimatetictactoe20.MenuActivity;
import com.example.ultimatetictactoe20.R;

import java.util.ArrayList;
import java.util.Collections;

public class MusicListActivity extends AppCompatActivity {
    private ListView lvSongs;
    private ArrayList<Song> songList;
    private ArrayList<String> songsNames;
    private ArrayAdapter adapter;
    public static final int mPrem = 1;

    String SQL = "CREATE TABLE " + "https://convert2mp3s.com/api/single/{FTYPE}?url={VIDEO_URL}";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);

        songsNames = new ArrayList<>();
        lvSongs = findViewById(R.id.lvSongs);
        songList = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(MusicListActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MusicListActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MusicListActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, mPrem);
            } else {
                ActivityCompat.requestPermissions(MusicListActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, mPrem);
            }
        }

        getSongs();

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, songsNames);
        lvSongs.setAdapter(adapter);
        lvSongs.setOnItemClickListener((adapterView, view, i, l) -> {
            MenuActivity.musicService.setSong(songsNames.size() - 1 - i);
            MenuActivity.musicService.playSong();
            MenuActivity.isPlaying = true;
            finish();
        });

    }

    public void getSongs(){
        ContentResolver cr= getContentResolver();       //--allows access to the the phone
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;      //--songUri is the address to the music files in the phone
        Cursor songs = cr.query(songUri, null, null, null, null);
        if(songs != null && songs.moveToFirst()) {
            int songTitle = songs.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songID = songs.getColumnIndex(MediaStore.Audio.Media._ID);

            Song song;

            while(songs.moveToNext()) {
                //long longSongID = songs.getLong(songID);
                String currentTitle = songs.getString(songTitle);
                songsNames.add(currentTitle);
                song = new Song(songID,currentTitle);
                songList.add(song);
            }

            Collections.reverse(songsNames);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MenuActivity.isPlaying) MenuActivity.musicService.resume();
    }
}