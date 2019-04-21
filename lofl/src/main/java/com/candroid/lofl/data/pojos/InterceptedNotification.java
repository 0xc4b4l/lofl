package com.candroid.lofl.data.pojos;

public class InterceptedNotification {
    public String title, body, footer, app, time;

    public InterceptedNotification(String title, String body, String footer, String app, String time){
        this.title = title;
        this.body = body;
        this.footer = footer;
        this.app = app;
        this.time = time;

    }

    @Override
    public String toString() {
        return String.format("InterceptedNotification[title=%s, body=%s, footer=%s, app=%s, time=%s]", title, body, footer, app, time);
    }
}
