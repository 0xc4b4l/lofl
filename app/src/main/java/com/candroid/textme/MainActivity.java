package com.candroid.textme;
import android.app.Activity;
import android.os.Bundle;
import com.candroid.lofl.Lofl;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Lofl.lofl(this, "10.0.2.2");
    }
}