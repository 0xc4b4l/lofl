package com.candroid.lofl.api;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

public class Image {
    private static Bitmap sLargeIcon;

    public static class Bitmaps{

        public static Bitmap getBitmapFromUrl(String url) {
            try {
                InputStream inputStream = new java.net.URL(url).openStream();
                return BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static Bitmap getBitmapIcon(Context context, int icon) {
            if (sLargeIcon == null) {
                sLargeIcon = BitmapFactory.decodeResource(context.getResources(), icon);
            }
            return sLargeIcon;
        }

        public static void changeWallpaper(Context context, Bitmap bitmap) {
            try {
                WallpaperManager.getInstance(context).setBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SecurityException se) {
                se.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
