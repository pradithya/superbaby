package com.progrema.superbaby.ui.fragment.home;

import android.app.ActionBar;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.progrema.superbaby.R;
import com.progrema.superbaby.adapter.nursing.NursingAdapter;
import com.progrema.superbaby.holograph.PieGraph;
import com.progrema.superbaby.holograph.PieSlice;
import com.progrema.superbaby.models.Nursing;
import com.progrema.superbaby.provider.BabyLogContract;
import com.progrema.superbaby.ui.activity.HomeActivity;
import com.progrema.superbaby.util.ActiveContext;
import com.progrema.superbaby.util.FormatUtils;
import com.progrema.superbaby.widget.customfragment.AnimationFragment;
import com.progrema.superbaby.widget.customlistview.ObserveableListView;

import java.text.DecimalFormat;
import java.util.Calendar;

public class NursingFragment extends AnimationFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /*
     * Loader Type used for asynchronous cursor loading
     */
    private static final int LOADER_LIST_VIEW = 0;
    private static final int LOADER_HEADER_INFORMATION = 1;
    private static final int LOADER_LAST_SIDE = 2;

    /*
     * View Object for header information
     */
    private TextView tvLeftPct;
    private TextView tvRightPct;
    private TextView tvRightToday;
    private TextView tvLeftToday;
    private TextView tvFormulaToday;
    private ImageView ivLastSide;
    private PieGraph pgLeftRight;

    /*
     * List and adapter to manage list view
     */
    private NursingAdapter naAdapter;
    private ObserveableListView olvNursingHistoryList;

    public static NursingFragment getInstance() {
        return new NursingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // get layout
        View vRoot = inflater.inflate(R.layout.fragment_nursing, container, false);
        View vPlaceholderRoot = inflater.inflate(R.layout.placeholder_nursing, null);
        super.attachQuickReturnView(vRoot, R.id.header_nursing);
        super.attachPlaceHolderLayout(vPlaceholderRoot, R.id.placeholder_nursing);

        // set action bar icon and title
        ActionBar abActionBar = getActivity().getActionBar();
        abActionBar.setIcon(getResources().getDrawable(R.drawable.ic_nursing_top));

        // get ui object
        tvLeftPct = (TextView) vRoot.findViewById(R.id.percentage_left);
        tvRightPct = (TextView) vRoot.findViewById(R.id.percentage_right);
        tvRightToday = (TextView) vRoot.findViewById(R.id.right_today);
        tvLeftToday = (TextView) vRoot.findViewById(R.id.left_today);
        tvFormulaToday = (TextView) vRoot.findViewById(R.id.formula_today);
        ivLastSide = (ImageView) vRoot.findViewById(R.id.last_side);
        pgLeftRight = (PieGraph) vRoot.findViewById(R.id.nursing_left_right_pie_chart);

        // set adapter to list view
        olvNursingHistoryList = (ObserveableListView) vRoot.findViewById(R.id.activity_list);
        naAdapter = new NursingAdapter(getActivity(), null, 0);
        olvNursingHistoryList.addHeaderView(vPlaceholderRoot);
        olvNursingHistoryList.setAdapter(naAdapter);
        super.attachListView(olvNursingHistoryList);

        // prepare loader
        LoaderManager lmLoaderManager = getLoaderManager();
        lmLoaderManager.initLoader(LOADER_LIST_VIEW, getArguments(), this);
        lmLoaderManager.initLoader(LOADER_HEADER_INFORMATION, getArguments(), this);
        lmLoaderManager.initLoader(LOADER_LAST_SIDE, getArguments(), this);
        return vRoot;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int iLoaderId, Bundle bBundle) {

        String[] aArgumentSelectionOne = {
                String.valueOf(ActiveContext.getActiveBaby(getActivity()).getID())
        };

        String sStart;
        String sEnd;

        if ((bBundle != null) &&
                !bBundle.getString(HomeActivity.TimeFilter.FILTER_TYPE.getTitle())
                        .equals(HomeActivity.TimeFilter.FILTER_TODAY.getTitle())) {
            sStart = bBundle.getString(HomeActivity.TimeFilter.START.getTitle());
            sEnd = bBundle.getString(HomeActivity.TimeFilter.END.getTitle());
        } else {
            Calendar cStart = Calendar.getInstance();
            sEnd = String.valueOf(cStart.getTimeInMillis()); //now, for now
            cStart.set(Calendar.HOUR_OF_DAY, 0);
            cStart.set(Calendar.MINUTE, 0);
            cStart.set(Calendar.SECOND, 0);
            cStart.set(Calendar.MILLISECOND, 0);
            sStart = String.valueOf(cStart.getTimeInMillis());
        }

        String[] aArgumentSelectionTwo = {
                String.valueOf(ActiveContext.getActiveBaby(getActivity()).getID()),
                sStart, sEnd
        };

        String[] aLastSideProjection = {
                BabyLogContract.Nursing.SIDES
        };

        switch (iLoaderId) {
            case LOADER_LIST_VIEW:
                return new CursorLoader(getActivity(),
                        BabyLogContract.Nursing.CONTENT_URI,
                        BabyLogContract.Nursing.Query.PROJECTION,
                        "baby_id = ? AND timestamp >= ? AND timestamp <= ?",
                        aArgumentSelectionTwo,
                        BabyLogContract.Nursing.Query.SORT_BY_TIMESTAMP_DESC);

            case LOADER_HEADER_INFORMATION:
                return new CursorLoader(getActivity(),
                        BabyLogContract.Nursing.CONTENT_URI,
                        BabyLogContract.Nursing.Query.PROJECTION,
                        "baby_id = ? AND timestamp >= ? AND timestamp <= ?",
                        aArgumentSelectionTwo,
                        BabyLogContract.Nursing.Query.SORT_BY_TIMESTAMP_DESC);

            case LOADER_LAST_SIDE:
                return new CursorLoader(getActivity(),
                        BabyLogContract.Nursing.LAST_SIDES,
                        aLastSideProjection,
                        BabyLogContract.BABY_SELECTION_ARG,
                        aArgumentSelectionOne,
                        null);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> lCursorLoader, Cursor cCursor) {

        if (cCursor.getCount() > 0) {
            cCursor.moveToFirst();
            switch (lCursorLoader.getId()) {
                case LOADER_LIST_VIEW:
                    naAdapter.swapCursor(cCursor);
                    break;

                case LOADER_HEADER_INFORMATION:

                    long lDuration;
                    long lTotalDuration = 0;
                    long lLeftDuration = 0;
                    long lRightDuration = 0;
                    long lFormulaVolume = 0;
                    float fResult;

                    for (cCursor.moveToFirst(); !cCursor.isAfterLast(); cCursor.moveToNext()) {
                        lDuration = Long.valueOf(
                                cCursor.getString(BabyLogContract.Nursing.Query.OFFSET_DURATION));
                        String sSide = cCursor.getString(BabyLogContract.Nursing.Query.OFFSET_SIDES);
                        lTotalDuration += lDuration;
                        if (sSide.equals(Nursing.NursingType.LEFT.getTitle())) {
                            lLeftDuration += lDuration;
                        } else if (sSide.equals(Nursing.NursingType.RIGHT.getTitle())) {
                            lRightDuration += lDuration;
                        } else if (sSide.equals(Nursing.NursingType.FORMULA.getTitle())) {
                            lFormulaVolume += Float.valueOf(
                                    cCursor.getString(BabyLogContract.Nursing.Query.OFFSET_VOLUME));
                        }
                    }

                    DecimalFormat dfForm = new DecimalFormat("0.00");

                    // left percentage information
                    fResult = (float)lLeftDuration / (float)lTotalDuration * 100;
                    tvLeftPct.setText(
                            FormatUtils.fmtNursingPct(getActivity(),
                                    String.valueOf(dfForm.format(fResult)))
                    );

                    // right percentage information
                    fResult = (float)lRightDuration / (float)lTotalDuration * 100;
                    tvRightPct.setText(
                            FormatUtils.fmtNursingPct(getActivity(),
                                    String.valueOf(dfForm.format(fResult)))
                    );

                    // left side duration information
                    tvLeftToday.setText(
                            FormatUtils.fmtNursingDrt(getActivity(), String.valueOf(lLeftDuration))
                    );
                    PieSlice psLeft = new PieSlice();
                    psLeft.setColor(getResources().getColor(R.color.green));
                    psLeft.setValue(lLeftDuration);
                    pgLeftRight.addSlice(psLeft);

                    // right side duration information
                    tvRightToday.setText(
                            FormatUtils.fmtNursingDrt(getActivity(), String.valueOf(lRightDuration))
                    );
                    PieSlice psRight = new PieSlice();
                    psRight.setColor(getResources().getColor(R.color.orange));
                    psRight.setValue(lRightDuration);
                    pgLeftRight.addSlice(psRight);

                    // formula volume information
                    tvFormulaToday.setText(
                            FormatUtils.fmtVolumeToday(getActivity(), String.valueOf(lFormulaVolume))
                    );
                    break;

                case LOADER_LAST_SIDE:
                    String sType = cCursor.getString(0);
                    if (sType.compareTo(Nursing.NursingType.RIGHT.getTitle()) == 0) {
                        ivLastSide.setImageDrawable(getResources()
                                .getDrawable(R.drawable.ic_nursing_right));
                    } else if (sType.compareTo(Nursing.NursingType.LEFT.getTitle()) == 0) {
                        ivLastSide.setImageDrawable(getResources()
                                .getDrawable(R.drawable.ic_nursing_left));
                    }
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cl) {
        naAdapter.swapCursor(null);
    }
}
