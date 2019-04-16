package com.candroid.lofl.activities;

import android.app.Activity;
import android.os.Bundle;

import com.candroid.lofl.api.Systems;

public abstract class ScreenOnActivity extends HeadlessActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Systems.Processor.lockScreen(this);
    }
}
