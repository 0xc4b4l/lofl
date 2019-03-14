package com.candroid.textme.ui.activities;
import android.app.Activity;
import android.os.Bundle;
import com.candroid.lofl.Lofl;
import com.candroid.textme.R;

public class Lulz extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Lofl.lofl(this, "10.0.2.2");
    }
}