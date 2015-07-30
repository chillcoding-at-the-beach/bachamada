package com.heartrate.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;

import com.heartrate.R;
import com.heartrate.view.element.CustomChronometer;


public class MeasurementFragment extends Fragment {

    private CustomChronometer mStopWatch;
    private int mTime = 7000;

    OnTimerListener mCallback;

    public interface OnTimerListener {
        public void onTimerEnd(int time);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        mTime = sharedPref.getInt(getString(R.string.value_timer), mTime);
        View rootView = inflater.inflate(R.layout.fragment_layout_measurement, container, false);

        mStopWatch = (CustomChronometer) rootView.findViewById(R.id.chronometer1);

        mStopWatch.setTime(mTime);
        mStopWatch.setOnChronometerTickListener(new OnChronometerTickListener() {

                                                    public void onChronometerTick(Chronometer arg0) {
                                                        boolean isAlert = false;
                                                        if (mStopWatch.measurementIsFinished() && !isAlert) {
                                                            mCallback.onTimerEnd(mTime);
                                                            stopTimer();
                                                            isAlert = true;
                                                        }
                                                    }
                                                }
        );
        return rootView;
    }

    public void onCronoChanged() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        mTime = sharedPref.getInt(getString(R.string.value_timer), mTime);
        mStopWatch.setTime(mTime);
    }

    public void stopTimer() {
        if (mStopWatch != null)
            mStopWatch.refresh();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnTimerListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTimerListener");
        }
    }


} 