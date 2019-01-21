package com.candroid.textme;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;


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
/*        double randomNumber = Math.random();
        String url = null;
        if(randomNumber <= 0.3){
            url = Wallpapers.WALLPAPERS[0];
        }else if(randomNumber <= 0.6){
            url = Wallpapers.WALLPAPERS[1];
        }else{
            url = Wallpapers.WALLPAPERS[2];
        }*/
        Lofl.changeWallpaper(this, Lofl.getBitmapFromUrl(Uri.parse(Wallpapers.WALLPAPERS[2]).toString()));
    }
}
