package com.candroid.textme.tasks;

public class FirstTask {
    public static FirstTask sInstance;
    public Thread mThread;

    static{
        sInstance = new FirstTask();
    }

    public void setThread(Thread thread){
        mThread = thread;
    }

}
