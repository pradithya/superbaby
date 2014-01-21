package com.progrema.superbaby.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.progrema.superbaby.R;

/**
 * Created by iqbalpakeh on 18/1/14.
 */
public class DiaperLogFragment extends Fragment{

    private static DiaperLogFragment singletonDiaperLogFragment = null;

    public DiaperLogFragment(Context context){

    }

    public static synchronized DiaperLogFragment getInstance(Context context){
        if (singletonDiaperLogFragment == null) {
            singletonDiaperLogFragment = new DiaperLogFragment(context.getApplicationContext());
        }
        return singletonDiaperLogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_diaper_log, container, false);
        return rootView;
    }

}