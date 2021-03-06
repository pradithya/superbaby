package com.progrema.superbaby.ui.fragment.history;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.progrema.superbaby.R;
import com.progrema.superbaby.adapter.measurement.MeasurementAdapter;
import com.progrema.superbaby.models.ActivityMeasurement;
import com.progrema.superbaby.provider.BabyLogContract;
import com.progrema.superbaby.ui.activity.HomeActivity;
import com.progrema.superbaby.ui.fragment.dialog.MeasurementDialog;
import com.progrema.superbaby.util.ActiveContext;
import com.progrema.superbaby.widget.customfragment.HistoryFragment;
import com.progrema.superbaby.widget.customlistview.ObserveableListView;

import java.util.Calendar;

public class MeasurementFragment extends HistoryFragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        MeasurementAdapter.Callback, MeasurementDialog.Callback, HistoryFragmentServices {

    private static final int LOADER_LIST_VIEW = 0;
    private static final int LOADER_HEADER = 1;
    private ObserveableListView measurementHistoryList;
    private MeasurementAdapter adapter;
    private String timeFilterStart;
    private String timeFilterEnd;
    private View root;
    private String entryTag;
    private View placeholder;

    public static MeasurementFragment getInstance() {
        return new MeasurementFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prepareFragment(inflater, container);
        prepareListView();
        prepareLoaderManager();
        return root;
    }

    @Override
    public void prepareFragment(LayoutInflater inflater, ViewGroup container) {
        root = inflater.inflate(R.layout.fragment_measurement, container, false);
        placeholder = inflater.inflate(R.layout.placeholder_header, null);
        super.attachQuickReturnView(root, R.id.header_container);
        super.attachPlaceHolderLayout(placeholder, R.id.placeholder_header);
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setIcon(getResources().getDrawable(R.drawable.ic_measurement_top));
    }

    @Override
    public void prepareHandler() {

    }

    @Override
    public void prepareListView() {
        measurementHistoryList = (ObserveableListView) root.findViewById(R.id.activity_list);
        adapter = new MeasurementAdapter(getActivity(), null, 0);
        adapter.setCallback(this);
        measurementHistoryList.addHeaderView(placeholder);
        measurementHistoryList.setAdapter(adapter);
        super.attachListView(measurementHistoryList);
    }


    @Override
    public void onMeasurementEntryEditSelected(View entry) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        entryTag = entry.getTag().toString();
        MeasurementDialog measurementChoiceBox = MeasurementDialog.getInstance();
        measurementChoiceBox.setCallback(this);
        measurementChoiceBox.show(fragmentTransaction, "measurement_dialog");
    }

    @Override
    public void onMeasurementDialogSelected(Intent data) {
        Bundle bundle = data.getExtras();
        ActivityMeasurement activityMeasurement = new ActivityMeasurement();
        activityMeasurement.setActivityId(Long.valueOf(entryTag));
        activityMeasurement.setHeight(Float.valueOf(bundle.getString(ActivityMeasurement.HEIGHT_KEY)));
        activityMeasurement.setWeight(Float.valueOf(bundle.getString(ActivityMeasurement.WEIGHT_KEY)));
        activityMeasurement.edit(getActivity());
    }

    @Override
    public void prepareLoaderManager() {
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(LOADER_LIST_VIEW, getArguments(), this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        String[] timeFilter;
        calculateTimeFilter(bundle);
        timeFilter = buildTimeFilter();
        return new CursorLoader(getActivity(), BabyLogContract.Measurement.CONTENT_URI,
                BabyLogContract.Measurement.Query.PROJECTION,
                "baby_id = ? AND timestamp >= ? AND timestamp <= ?",
                timeFilter,
                BabyLogContract.Measurement.Query.SORT_BY_TIMESTAMP_DESC);
    }

    private void calculateTimeFilter(Bundle bundle) {
        if (isNotNull(bundle)) {
            timeFilterStart = bundle.getString(HomeActivity.TimeFilter.START.getTitle());
            timeFilterEnd = bundle.getString(HomeActivity.TimeFilter.END.getTitle());
        } else {
            Calendar calendarStart = Calendar.getInstance();
            timeFilterEnd = String.valueOf(calendarStart.getTimeInMillis());
            calendarStart.set(Calendar.HOUR_OF_DAY, 0);
            calendarStart.set(Calendar.MINUTE, 0);
            calendarStart.set(Calendar.SECOND, 0);
            calendarStart.set(Calendar.MILLISECOND, 0);
            timeFilterStart = String.valueOf(calendarStart.getTimeInMillis());
        }
    }

    private boolean isNotNull(Object object) {
        return object != null;
    }

    private String[] buildTimeFilter() {
        String[] timeFilter = {
                String.valueOf(ActiveContext.getActiveBaby(getActivity()).getActivityId()),
                timeFilterStart, timeFilterEnd
        };
        return timeFilter;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.getCount() >= 0) {
            cursor.moveToFirst();
            adapter.swapCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
