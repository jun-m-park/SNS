package com.example.junmp.teamproject;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class FeedActivity extends AppCompatActivity /*implements PostAdapter.onMusicListener*/{
    String userId;
    String userName;
    public static TextView nameMusic;
    public static boolean isMusic = false;
    boolean isPause = false;
    /*MusicService ms = new MusicService();
    public static boolean isMusic = false;*/
    public static MusicService ms = new MusicService();

    boolean isService = false;

    public static SeekBar seekBar;
    TextView minTime;
    public static TextView maxTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = new Intent(FeedActivity.this, MusicService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        seekBar = findViewById(R.id.seekbar);
        minTime = findViewById(R.id.mintime);
        maxTime = findViewById(R.id.maxtime);
        nameMusic = findViewById(R.id.musicname);

        userId = getIntent().getStringExtra("userId");
        userName = getIntent().getStringExtra("userName");

        ViewFragment viewFragment = ViewFragment.newInstance(userId);
        getSupportFragmentManager().beginTransaction().add(R.id.post_frame, viewFragment).commit();

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewFragment viewFragment = ViewFragment.newInstance(userId);
                getSupportFragmentManager().beginTransaction().replace(R.id.post_frame, viewFragment).commit();
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetupFragment setupFragment = SetupFragment.newInstance(userId);
                getSupportFragmentManager().beginTransaction().replace(R.id.post_frame, setupFragment).commit();
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchFragment searchFragment = SearchFragment.newInstance(userId);
                getSupportFragmentManager().beginTransaction().replace(R.id.post_frame, searchFragment).commit();
            }
        });

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileFragment profileFragment = ProfileFragment.newInstance(userId, userName);
                getSupportFragmentManager().beginTransaction().replace(R.id.post_frame, profileFragment).commit();
            }
        });

        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FeedActivity.this, WritingPostActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                if(fromUser) {
                    ms.player.seekTo(progress);
                }
                int m = progress / 60000;
                int s = (progress % 60000) / 1000;
                String strTime = String.format("%02d:%02d", m, s);
                minTime.setText(strTime);

            }
        });

        findViewById(R.id.pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPause){
                    ms.player.pause();
                    isPause = true;
                }
                else if(isPause){
                    ms.player.seekTo(ms.player.getCurrentPosition());
                    ms.player.start();
                    isPause = false;
                }
            }
        });
    }

    ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            MusicService.MyBinder mb = (MusicService.MyBinder) service;
            ms = mb.getService();
            isService = true;
        }
        public void onServiceDisconnected(ComponentName name) {
            isService = false;
        }
    };
}
