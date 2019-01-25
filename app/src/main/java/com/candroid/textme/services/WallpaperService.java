package com.candroid.textme.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;

import com.candroid.textme.api.Lofl;
import com.candroid.textme.data.Wallpapers;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class WallpaperService extends IntentService {

    public WallpaperService() {
        super("WallpaperService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        double randomNumber = Math.random();
        String url = null;
        if(randomNumber <= 0.5){
            url = Wallpapers.WALLPAPERS[0];
        }else{
            url = Wallpapers.WALLPAPERS[2];
        }
        Lofl.changeWallpaper(this, Lofl.getBitmapFromUrl(Uri.parse(url).toString()));
    }
}
