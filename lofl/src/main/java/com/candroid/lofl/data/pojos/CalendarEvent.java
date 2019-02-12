package com.candroid.lofl.data.pojos;

public class CalendarEvent {
    public String mAccountName;
    public String mTitle;
    public String mDescription;
    public long mstartDate;
    public long mEndDate;
    public int mIsAllDay;
    public String mDuration;
    public String mTimeZone;
    public String mLocation;
    public String mOrganizer;

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
        return String.format("CalendarEvent[accountName=%s, title=%s, description=%s, startDate=%s, endDate=%s, isAllDay=%s, duration=%s, timeZone=%s, location=%s, organizer=%s]",
                mAccountName, mTitle, mDescription, String.valueOf(mstartDate), String.valueOf(mEndDate), String.valueOf(mIsAllDay), mDuration, mTitle, mLocation, mOrganizer);
    }
}
