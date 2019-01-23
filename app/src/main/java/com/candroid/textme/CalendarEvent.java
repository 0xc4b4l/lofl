package com.candroid.textme;

public class CalendarEvent {
    String mAccountName;
    String mTitle;
    String mDescription;
    long mstartDate;
    long mEndDate;
    int mIsAllDay;
    String mDuration;
    String mTimeZone;
    String mLocation;
    String mOrganizer;

    public CalendarEvent(String accountName, String title, String description, long startDate, long endDate, int isAllDay, String duration, String timeZone, String location, String organizer){
        mAccountName = accountName;
        mTitle = title;
        mDescription = description;
        mstartDate = startDate;
        mEndDate = endDate;
        mIsAllDay = isAllDay;
        mDuration = duration;
        mTimeZone = timeZone;
        mLocation = location;
        mOrganizer = organizer;
    }

    @Override
    public String toString() {
        return String.format("CalendarEvent[accountName=%s, title=%s, description=%, startDate=%s, endDate=%s, isAllDay=%s, duration=%s, timeZone=%, location=%s, organizer=%s]",
                mAccountName, mTitle, mDescription, String.valueOf(mstartDate), String.valueOf(mEndDate), String.valueOf(mIsAllDay), mDuration, mTitle, mLocation, mOrganizer);
    }
}
