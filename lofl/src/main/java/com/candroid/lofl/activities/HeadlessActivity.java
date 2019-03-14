package com.candroid.lofl.activities;

import android.app.Activity;

public abstract class HeadlessActivity extends Activity {

    @Override
    public void onBackPressed() {
        finishAndRemoveTask();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}