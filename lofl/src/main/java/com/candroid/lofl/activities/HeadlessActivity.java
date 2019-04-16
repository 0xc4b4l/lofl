package com.candroid.lofl.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public abstract class HeadlessActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onBackPressed() {
        finishAndRemoveTask();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}