package com.progrema.superbaby.adapter.measurementhistory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.progrema.superbaby.R;
import com.progrema.superbaby.provider.BabyLogContract;
import com.progrema.superbaby.util.FormatUtils;

/**
 * Created by iqbalpakeh on 27/2/14.
 */
public class MeasurementHistoryAdapter extends CursorAdapter
{
    private LayoutInflater inflater;
    private int layout;

    public MeasurementHistoryAdapter(Context context, Cursor c, int flags)
    {
        super(context, c, flags);
        inflater = LayoutInflater.from(context);
        layout = R.layout.history_item_measurement;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        return inflater.inflate(layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        String timeStamp = cursor.getString(BabyLogContract.Measurement.Query.OFFSET_TIMESTAMP);
        String height = cursor.getString(BabyLogContract.Measurement.Query.OFFSET_HEIGHT);
        String weight = cursor.getString(BabyLogContract.Measurement.Query.OFFSET_WEIGHT);

        TextView textViewDate = (TextView) view.findViewById(R.id.history_item_date);
        TextView textViewTime = (TextView) view.findViewById(R.id.history_item_time);
        TextView textViewHeight = (TextView) view.findViewById(R.id.history_item_height);
        TextView textViewWeight = (TextView) view.findViewById(R.id.history_item_weight);

        textViewDate.setText(FormatUtils.formatDate(context, timeStamp));
        textViewTime.setText(FormatUtils.formatTime(context, timeStamp));
        textViewHeight.setText(height + " cm");
        textViewWeight.setText(weight + " kg");
    }
}