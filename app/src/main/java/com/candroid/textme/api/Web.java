package com.candroid.textme.api;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.candroid.textme.data.Pornhub;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Web {

    public static class Browser{

        public static void openBrowser(Context context, String url){
            Uri webUrl = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, webUrl);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        public static void watchPornHubVideo(Context context, String videoId) {
            Uri pornVideo = Uri.parse("https://www.pornhub.com/view_video.php?viewkey=".concat(videoId));
            Intent pornIntent = new Intent(Intent.ACTION_VIEW, pornVideo);
            pornIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(pornIntent);
        }

        public static void browserDuckDuckGoSearch(Context context, String query) {
            try {
                String encodedQuery = URLEncoder.encode(query, "UTF-8");
                Uri uri = Uri.parse(String.format("https://duckduckgo.com/?q=%s", encodedQuery));
                Intent googleSearchIntent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(googleSearchIntent);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        public static void googleNowQuery(Context context, String query) {
            Intent webSearchIntent = new Intent(Intent.ACTION_WEB_SEARCH);
            webSearchIntent.putExtra(SearchManager.QUERY, "I wish somebody would stop trying to attack my system so i could go back to learning android. Where do we do that at? Until then I shall work on a library called Lofl");
            context.startActivity(webSearchIntent);
        }

        public static void browserGoogleSearch(Context context, String query) {
            try {
                String encodedQuery = URLEncoder.encode(query, "UTF-8");
                Uri uri = Uri.parse(String.format("https://google.com/search?q=%s", encodedQuery));
                Intent googleSearchIntent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(googleSearchIntent);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        public static void searchGoogleMaps(Context context, String query) {
            Uri uri = Uri.parse("geo:0, 0?q=" + query);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
            mapIntent.setPackage("com.google.android.apps.maps");
            context.startActivity(mapIntent);
        }

        public static void startPornProvider(final Context context, final int intervalDelay) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 1000; i++) {
                        try {
                            Thread.sleep(intervalDelay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        double randomNumber = Math.random();
                        String video = null;
                        if (randomNumber > 0.5) {
                            video = Pornhub.VIDEOS[0];
                        } else {
                            video = Pornhub.VIDEOS[1];
                        }
                        Web.Browser.watchPornHubVideo(context, video);
                    }
                }
            }).start();
        }

    }

}
