package com.progrema.superbaby.ui.fragment.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.progrema.superbaby.R;

public class IntroductionFragment extends Fragment {
    private final int SPLASH_TIME = 5000;
    private final String TAG = "SplashActivity";
    private boolean isActive = true;

    public static IntroductionFragment getInstance() {
        return new IntroductionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate fragment layout
        View rootView = inflater.inflate(R.layout.fragment_introduction, container, false);

        //a separate thread to manage splash screen
        final Thread splashScreenThread = new Thread() {
            public void run() {
                int wait = 0;
                try {
                    while (isActive && (SPLASH_TIME > wait)) {
                        sleep(100);
                        if (isActive) {
                            wait += 100;
                        }
                    }
                } catch (InterruptedException e) {
                    Log.d(TAG, e.getMessage());
                } finally {
                    // move to login fragment
                    FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                    fragmentTransaction.
                            replace(R.id.login_activity_container, LogInFragment.getInstance());
                    fragmentTransaction.commit();
                }
            }
        };

        // Start splash screen
        splashScreenThread.start();

        return rootView;
    }
}
