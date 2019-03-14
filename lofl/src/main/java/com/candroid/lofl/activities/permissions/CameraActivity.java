package com.candroid.lofl.activities.permissions;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.candroid.lofl.activities.HeadlessActivity;

public class CameraActivity extends HeadlessActivity {
    public static final int CAMERA_REQUEST_CODE = 33;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            onBackPressed();
        }else{
            requestPermissions();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
    }

    public void requestPermissions(){
        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }else{
            onBackPressed();
        }
    }
}
