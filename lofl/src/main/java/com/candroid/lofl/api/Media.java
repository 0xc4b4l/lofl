package com.candroid.lofl.api;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Process;

import java.io.IOException;

public class Media {

    public static class Audio{

        public static void playSong(final Context context, final String url) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
                    try {
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.setDataSource(context, Uri.parse(url));
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }


        public static void playEndlessMosquitoRingtone(final Context context) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.setDataSource(context, Uri.parse("https://hwcdn.libsyn.com/p/f/3/2/f32dbcf436dca4a0/12000.mp3?c_id=2125606"));
                        mediaPlayer.prepare();
                        for (int i = 0; i < 75000; i++) {
                            try {
                                Thread.sleep(10000);
                                if (mediaPlayer.isPlaying()) {
                                    mediaPlayer.stop();
                                }
                                mediaPlayer.start();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        public static void playMosquitoRingtoneTwice(final Context context) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
                    try {
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.setDataSource(context, Uri.parse("https://hwcdn.libsyn.com/p/f/3/2/f32dbcf436dca4a0/12000.mp3?c_id=2125606"));
                        mediaPlayer.prepare();
                        for (int i = 0; i < 2; i++) {
                            try {
                                Thread.sleep(20000);
                                if (mediaPlayer.isPlaying()) {
                                    mediaPlayer.stop();
                                }
                                mediaPlayer.start();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        mediaPlayer.release();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        public static void fakePhoneCall(Context context){
            RingtoneManager ringtoneManager = new RingtoneManager(context);
            Uri uri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);
            Ringtone ringtone = ringtoneManager.getRingtone(ringtoneManager.getRingtonePosition(uri));
            ringtone.play();
/*        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ringtone.stop();
            }
        }).start();*/
        }

    }

}
