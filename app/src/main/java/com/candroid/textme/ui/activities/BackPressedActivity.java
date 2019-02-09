package com.candroid.textme.ui.activities;

import android.app.Activity;

public abstract class BackPressedActivity extends Activity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAndRemoveTask();
    }
}
