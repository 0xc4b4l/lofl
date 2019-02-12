package com.candroid.lofl.activities;

import android.app.Activity;

public abstract class BackPressedActivity extends Activity {

    @Override
    public void onBackPressed() {
        finishAndRemoveTask();
    }
}
