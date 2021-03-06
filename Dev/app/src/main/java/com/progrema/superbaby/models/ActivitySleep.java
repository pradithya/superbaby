package com.progrema.superbaby.models;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;

import com.progrema.superbaby.provider.BabyLogContract;
import com.progrema.superbaby.util.ActiveContext;

public class ActivitySleep extends BaseActivity {

    private long duration;

    public ActivitySleep() {
    }

    public ActivitySleep(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeLong(duration);
    }

    public void readFromParcel(Parcel parcel) {
        super.readFromParcel(parcel);
        duration = parcel.readLong();
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public void insert(Context context) {
        ContentValues values = new ContentValues();
        values.put(BabyLogContract.Sleep.BABY_ID, getBabyID());
        values.put(BabyLogContract.Sleep.TIMESTAMP, getTimeStampInString());
        values.put(BabyLogContract.Sleep.DURATION, getDuration());
        context.getContentResolver().insert(BabyLogContract.Sleep.CONTENT_URI, values);
    }

    @Override
    public void delete(Context context) {
        String[] selectionArgs = {
                String.valueOf(ActiveContext.getActiveBaby(context).getActivityId()),
                String.valueOf(getActivityId())};
        context.getContentResolver().delete(
                BabyLogContract.Sleep.CONTENT_URI,
                "baby_id = ? AND activity_id = ?",
                selectionArgs);
    }

    @Override
    public void edit(Context context) {
        String[] selectionArgs = {
                String.valueOf(ActiveContext.getActiveBaby(context).getActivityId()),
                String.valueOf(getActivityId())};
        ContentValues values = new ContentValues();
        values.put(BabyLogContract.Sleep.DURATION, getDuration());
        context.getContentResolver().update(BabyLogContract.Sleep.CONTENT_URI, values,
                "baby_id = ? AND activity_id = ?", selectionArgs);
    }
}
