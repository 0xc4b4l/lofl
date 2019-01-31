package com.candroid.textme.data.pojos;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

public class Recorder {

    private static final int SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int FORMAT = MediaRecorder.OutputFormat.THREE_GPP;
    private static final int ENCODER = MediaRecorder.OutputFormat.AMR_NB;
    private static int sId = 0;
    private File mOutputFile;
    private MediaRecorder mRecorder;

    public Recorder(Context context){
        mRecorder = new MediaRecorder();
        sId++;
        // TODO: 1/30/19 possible issue with file null on first sleep after install
        mOutputFile = new File(context.getCacheDir() + File.separator + String.format("soundfile%s.3gpp", String.valueOf(sId)));
        mRecorder.setAudioSource(SOURCE);
        mRecorder.setOutputFormat(FORMAT);
        mRecorder.setAudioEncoder(ENCODER);
        mRecorder.setOutputFile(mOutputFile);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pause(){
        mRecorder.pause();
    }

    public void resume(){
        mRecorder.resume();
    }

    public File getFile(){
        return mOutputFile;
    }

    public void stop(){
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    public void start(){
        mRecorder.start();
    }
}